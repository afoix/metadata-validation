package com.afoix.faang.validation;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.ontologies.OLSClient;
import com.afoix.metadatavalidator.problems.BaseAttributeValueProblem;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.services.BioSamplesService;
import com.afoix.metadatavalidator.services.ZoomaService;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import com.afoix.metadatavalidator.validators.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RulesFileLoader {

    private final OLSClient olsClient;
    private final BioSamplesService bioSamples;
    private final ZoomaService zooma;

    public RulesFileLoader(OLSClient olsClient, BioSamplesService bioSamples, ZoomaService zooma) {
        this.olsClient = olsClient;
        this.bioSamples = bioSamples;
        this.zooma = zooma;
    }

    @NotNull
    public Validator createValidatorsFromJsonFile(Path checklistPath) throws IOException, MisconfiguredValidatorException {
        JSONObject json;
        try (InputStream stream = new FileInputStream(checklistPath.toFile())) {
            json = new JSONObject(new JSONTokener(stream));
        }

        CompositeValidator ruleset = new CompositeValidator();
        for (Object ruleGroupObj : json.getJSONArray("rule_groups")) {
            ruleset.add(makeValidatorForRuleGroup((JSONObject) ruleGroupObj));
        }

        return ruleset;
    }

    Validator makeValidatorForRuleGroup(JSONObject ruleGroup) throws MisconfiguredValidatorException {
        CompositeValidator validators = new CompositeValidator();

        JSONArray rules = ruleGroup.getJSONArray("rules");
        for (int ruleIndex = 0; ruleIndex < rules.length(); ruleIndex++) {
            validators.add(makeValidatorsForRule(rules.getJSONObject(ruleIndex)));
        }

        // TODO: Consistency checks (notes in the schema that map to hardcoded functions in validator)

        // TODO: Imports (???)

        Validator result = validators;

        if (ruleGroup.has("condition")) {
            result = applyRulegroupCondition(result, ruleGroup.getJSONObject("condition"));
        }

        return result;
    }

    Validator makeValidatorsForRule(JSONObject rule) throws MisconfiguredValidatorException {
        final String name = rule.getString("name");
        final String mandatory = rule.getString("mandatory");

        CompositeValidator validatorsForThisRule = new CompositeValidator();
        Validator rootValidator = validatorsForThisRule;
        validatorsForThisRule.add(new AttributeIsPresentValidator(name));

        if (mandatory.equals("recommended")) {
            rootValidator = new DemoteErrorsToWarningsValidator(rootValidator);
        } else if (mandatory.equals("optional")) {
            rootValidator = new ConditionalValidator(rootValidator) {
                @Override
                protected boolean shouldValidate(Entity entity) {
                    try {
                        return entity.hasAttribute(name);
                    } catch (InvalidAttributeNameOrPathException e) {
                        return false;
                    }
                }
            };
        }

        if (rule.has("description")) {
            rootValidator.setDescription(rule.getString("description"));
        }

        final boolean allowMultiple = rule.has("allow_multiple")
                && ((rule.get("allow_multiple") instanceof Boolean && rule.getBoolean("allow_multiple"))
                || (rule.get("allow_multiple") instanceof Integer && rule.getInt("allow_multiple") == 1));
        if (!allowMultiple) {
            if (mandatory.equals("recommended")) {
                // For recommended rules, problems are only warnings, except this one.
                // That means that it will not work to add the validator to the same
                // composite validator as all the rest. Instead the root needs to become
                // a new composite validator which checks this before doing the other ones.
                rootValidator = new CompositeValidator(
                        new AttributeIsSingleValuedValidator(name),
                        rootValidator
                );
            } else {
                validatorsForThisRule.add(new AttributeIsSingleValuedValidator(name));
            }
        }

        Validator typeSpecificValidator = getTypeSpecificValidatorForRule(rule, name);
        if (typeSpecificValidator != null) {
            if (typeSpecificValidator.getClass() == CompositeValidator.class) {
                for (Validator nestedValidator :
                        (CompositeValidator)typeSpecificValidator) {
                    validatorsForThisRule.add(nestedValidator);
                }
            } else {
                validatorsForThisRule.add(typeSpecificValidator);
            }
        }

        return rootValidator;
    }

    Validator applyRulegroupCondition(Validator rulegroupValidator, JSONObject condition) {
        if (condition.has("attribute_value_match")) {
            JSONObject attributeValueMatch = condition.getJSONObject("attribute_value_match");

            Map<String, List<String>> attributeValuesToMatch = new HashMap<>();
            for (String attributeName : attributeValueMatch.keySet()) {
                attributeValuesToMatch.put(attributeName, attributeValueMatch.getJSONArray(attributeName).toList().stream().map(obj -> (String) obj).toList());
            }

            rulegroupValidator = new ConditionalValidator(rulegroupValidator) {
                @Override
                protected boolean shouldValidate(Entity entity) {
                    for (Map.Entry<String, List<String>> requiredAttribute :
                            attributeValuesToMatch.entrySet()) {
                        try {
                            if (!entity.hasAttribute(requiredAttribute.getKey()))
                                continue;
                            if (entity.getAttributeValues(requiredAttribute.getKey())
                                    .map(value -> {
                                        if (value instanceof OntologyTermRef ontologyTermRef)
                                            return ontologyTermRef.getLabel();
                                        else
                                            return value;
                                    }).noneMatch(value -> requiredAttribute.getValue().contains(value)))
                                return false;
                        } catch (InvalidAttributeNameOrPathException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    return true;
                }
            };
        }
        return rulegroupValidator;
    }

    Validator getTypeSpecificValidatorForRule(JSONObject rule, String name) throws MisconfiguredValidatorException {

        final String type = rule.getString("type");

        switch (type) {
            case "text":   //=> Bio::Metadata::Validate::TextAttributeValidator
                if (rule.has("valid_values")) {
                    return new AttributeValuesAreInSetValidator<>(name,
                            rule.getJSONArray("valid_values")
                                    .toList().stream()
                                    .map(obj -> (String) obj)
                                    .collect(Collectors.toSet()));
                }
                break;

            case "number": //=> Bio::Metadata::Validate::NumberAttributeValidator
                // TODO: Support for valid_units
                return new AttributeValuesAreNumericValidator(name);

            case "enum":   //=> Bio::Metadata::Validate::EnumAttributeValidator
                return new AttributeValuesAreInSetValidator<>(name,
                        rule.getJSONArray("valid_values")
                                .toList().stream()
                                .map(obj -> (String) obj)
                                .collect(Collectors.toSet()));

            case "uri_value": //=> Bio::Metadata::Validate::UriValueAttributeValidator
                return new AttributeValuesAreUrisValidator(name,
                        AttributeValuesAreUrisValidator.DEFAULT_SCHEMES,
                        "http");

            case "ontology_id": { //=> Bio::Metadata::Validate::OntologyIdAttributeValidator
                List<OntologyConstraint> validTerms = getOntologyConstraints(rule);
                return new CompositeValidator(
                        new AttributeValuesAreOntologyTermsValidator(name, this.olsClient, true, zooma),
                        new AttributeValuesMeetOntologyTermConstraintsValidator(name, validTerms, this.olsClient)
                );
            }

            case "ontology_uri": { //=> Bio::Metadata::Validate::OntologyUriAttributeValidator
                List<OntologyConstraint> validTerms = getOntologyConstraints(rule);
                return new CompositeValidator(
                        new AttributeValuesAreOntologyIRIsValidator(name),
                        new AttributeValuesMeetOntologyTermConstraintsValidator(name, validTerms, this.olsClient));
            }

            case "ontology_text": { //=> Bio::Metadata::Validate::OntologyTextAttributeValidator
                List<OntologyTermRef> roots = getOntologyConstraints(rule).stream()
                        .map(OntologyConstraint::getTerm)
                        .toList();
                return new AttributeValuesAreOntologyTermsValidator(name, this.olsClient, true, roots, zooma);
            }

            case "ontology_attr_name": //=> Bio::Metadata::Validate::OntologyAttrNameValidator
                break;

            case "date": {     //=> Bio::Metadata::Validate::DateAttributeValidator
                Iterable<String> formats;
                if (rule.has("valid_units")) {
                    formats = rule.getJSONArray("valid_units").toList().stream().map(obj -> (String) obj).toList();
                } else {
                    formats = AttributeValuesAreDatesValidator.DEFAULT_PERMITTED_FORMATS;
                }
                return new AttributeValuesAreDatesValidator(name, formats);
            }

            case "relationship": { //=> Bio::Metadata::Validate::RelationshipValidator
                return new AttributeValuesAreRelatedEntitiesValidator(name, sampleId -> {
                    try {
                        return this.bioSamples.fetchSample(sampleId);
                    } catch (FileNotFoundException e) {
                        return null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // TODO: Implement the 'condition' part of these rules
                }, null);
            }

            case "ncbi_taxon":   //=> Bio::Metadata::Validate::NcbiTaxonomyValidator
                return new AttributeValuesAreNCBITaxonIDsValidator(name, olsClient);

            case "subid": //=> Bio::Metadata::Validate::SubmissionsIdentifierValidator
                return new AttributeValuesValidator(name) {
                    @Override
                    protected void validateValue(Object value,
                                                 ValidationContext context,
                                                 AttributeValueProblemBuilder problemBuilder) {
                        if (!value.toString().startsWith("GSB-")) {
                            context.reportProblem(
                                    new BaseAttributeValueProblem(
                                        problemBuilder,
                                        "Value is not a valid submission ID"
                                )
                            );
                        }
                    }
                };

            case "faang_breed":  //=> Bio::Metadata::Faang::FaangBreedValidator

                //default:
                //    throw new Exception("Type " + type + " not implemented yet");
        }

        return null;
    }

    @NotNull
    List<OntologyConstraint> getOntologyConstraints(JSONObject rule) {
        List<OntologyConstraint> validTerms = new ArrayList<>();
        JSONArray array = rule.getJSONArray("valid_terms");
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            OntologyTermRef term = OntologyTermRef.ofIri(URI.create(jsonObject.getString("term_iri")));
            term.setOntologyName(jsonObject.getString("ontology_name"));
            validTerms.add(new OntologyConstraint(term,
                    !jsonObject.has("allow_descendants") || tolerantBoolean(jsonObject.get("allow_descendants")),
                    !jsonObject.has("include_root") || tolerantBoolean(jsonObject.get("include_root"))
            ));
        }
        return validTerms;
    }

    boolean tolerantBoolean(Object obj) {
        if (obj instanceof Boolean b) {
            return b;
        }

        if (obj instanceof Integer i) {
            return i == 1;
        }

        if (obj instanceof String s) {
            return Boolean.parseBoolean(s);
        }

        throw new ClassCastException("Cannot coerce " + obj.getClass().getName() + " to boolean.");
    }
}
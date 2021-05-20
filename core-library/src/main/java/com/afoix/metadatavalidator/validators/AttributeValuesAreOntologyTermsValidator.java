package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.ontologies.OLSClient;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.services.ZoomaService;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ebi.pride.utilities.ols.web.service.model.ITerm;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Identifier;
import uk.ac.ebi.pride.utilities.ols.web.service.model.QueryFields;
import uk.ac.ebi.pride.utilities.ols.web.service.model.SearchQuery;

import java.io.IOException;
import java.net.URI;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

public class AttributeValuesAreOntologyTermsValidator extends AttributeValuesValidator {

    private final OLSClient olsClient;
    private final boolean lenientLabelMatching;
    private final Set<OntologyTermRef> suggestedRoots;
    private final ZoomaService zooma;

    public AttributeValuesAreOntologyTermsValidator(String attributeNameOrPath, OLSClient olsClient, boolean lenientLabelMatching, ZoomaService zooma) throws MisconfiguredValidatorException {
        super(attributeNameOrPath);

        this.olsClient = olsClient;
        this.lenientLabelMatching = lenientLabelMatching;
        this.suggestedRoots = new HashSet<>();
        this.zooma = zooma;
    }

    public AttributeValuesAreOntologyTermsValidator(String attributeNameOrPath,
                                                    OLSClient olsClient,
                                                    boolean lenientLabelMatching,
                                                    Iterable<OntologyTermRef> suggestedRoots,
                                                    ZoomaService zooma) throws MisconfiguredValidatorException {
        this(attributeNameOrPath, olsClient, lenientLabelMatching, zooma);
        suggestedRoots.forEach(this.suggestedRoots::add);

        for (OntologyTermRef root : suggestedRoots) {
            if (root.getOntologyName() == null) {
                root.setOntologyName(root.guessOntologyName());
                if (root.getOntologyName() == null) {
                    throw new MisconfiguredValidatorException(new Exception("Ontology name must be specified or derivable from the term ID"));
                }
            }
            if (root.toOLSIdentifier() == null) {
                throw new MisconfiguredValidatorException(new Exception("Don't know how to convert this OntologyTermRef to an OLS identifier object"));
            }
        }
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) throws MisconfiguredValidatorException {

        if (value == null) {
            context.reportProblem(problemBuilder.isNull());
            return;
        }

        OntologyTermRef termRef;
        if (value instanceof OntologyTermRef)
            termRef = (OntologyTermRef) value;
        else
            termRef = OntologyTermRef.ofLabel(value.toString());

        ITerm term = null;

        if (termRef.getLabel() != null && !termRef.getLabel().isEmpty() && termRef.toOLSIdentifier() == null) {
            // This term is only a label, so we need to search for it instead of just looking it up
            // in an ontology

            // Try Zooma first because it has curated mappings that can be higher quality
            term = getZoomaSuggestedTerm(termRef);

            if (term == null) {
                // Zooma should have already searched OLS for us but try it in case Zooma was not available

                olsClient.setQueryField(new QueryFields.QueryFieldBuilder().setLabel().setOboId().setShortForm().setIri().build().toString());
                for (OntologyTermRef root :
                        suggestedRoots) {
                    SearchQuery query = olsClient.getSearchQuery(0,
                            termRef.getLabel(),
                            root.getOntologyName().toLowerCase(Locale.ROOT),
                            true,
                            root.getIri().toString(),
                            false,
                            1);
                    if (query.getResponse().getNumFound() > 0) {
                        term = query.getResponse().getSearchResults()[0];
                        break;
                    }
                }
            }

            if (term != null) {
                context.reportSuggestedOntologyMapping(problemBuilder.getAttributeProblemBuilder().getEntity(),
                        this,
                        getAttributeNameOrPath(),
                        new OntologySuggestion(termRef.getLabel(),
                                OntologyTermRef.ofIri(URI.create(term.getIri().getIdentifier()), term.getName())));
            }

        } else {
            // It has an identifier so we will look it up by ID

            if (termRef.getOntologyName() == null) {
                termRef.setOntologyName(termRef.guessOntologyName());
                if (termRef.getOntologyName() == null) {
                    context.reportProblem(problemBuilder.unknownOntology());
                    return;
                }
            }

            Identifier termIdentifier = termRef.toOLSIdentifier();
            if (termIdentifier != null) {
                try {
                    term = olsClient.getTermById(termIdentifier, termRef.getOntologyName());
                } catch (HttpClientErrorException exception) {
                    // If it was HTTP 404 then the term does not exist in the ontology
                    if (exception.getStatusCode() != HttpStatus.NOT_FOUND)
                        throw exception;
                }
            }
        }

        if (term == null) {
            context.reportProblem(problemBuilder.ontologyTermDoesNotExist());
            return;
        }

        if (termRef.getLabel() != null) {
            String expectedLabel = term.getName();
            if (lenientLabelMatching) {
                expectedLabel = Normalizer.normalize(expectedLabel, Normalizer.Form.NFD);
                expectedLabel = expectedLabel.replaceAll("[^\\p{ASCII}]", "");
            }

            if (!termRef.getLabel().equals(expectedLabel)) {
                context.reportProblem(problemBuilder.ontologyTermLabelDoesNotMatch(expectedLabel));
            }
        }
    }

    private ITerm getZoomaSuggestedTerm(OntologyTermRef termRef) {
        try {
            Stream<OntologyTermRef> zoomaResults = zooma.query(termRef.getLabel());
            for (OntologyTermRef zoomaSuggestion : zoomaResults.toList()) {

                ITerm term = olsClient.getTermById(zoomaSuggestion.toOLSIdentifier(), zoomaSuggestion.guessOntologyName());
                if (suggestedRoots == null || suggestedRoots.isEmpty()) {
                    return term;
                }

                for (OntologyTermRef root : suggestedRoots) {
                    ITerm rootTerm = olsClient.getTermById(root.toOLSIdentifier(), root.guessOntologyName());

                    if (olsClient.isTermChildOf(term, rootTerm)) {
                        // This term suggested by Zooma is under one of the suggested roots
                        return term;
                    }
                }
            }
        } catch (IOException e) {
            // Couldn't connect to Zooma
        }
        return null;
    }

}

package com.afoix.faang.validation;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;
import com.afoix.metadatavalidator.problems.OntologyTermLabelDoesNotMatchDefinitionProblem;
import com.afoix.metadatavalidator.problems.Problem;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import com.afoix.metadatavalidator.validators.ValidationContext;
import com.afoix.metadatavalidator.validators.Validator;

import java.util.Arrays;

public class WarnOnOntologyLabelMismatchesContext implements ValidationContext {
    private final ValidationContext innerContext;

    final static String[] materialShortNameIds = new String[]{
            "OBI_0100026", "OBI_0001479", "OBI_0001468", "OBI_0302716", "OBI_0001876", "CLO_0000031"
    };

    public WarnOnOntologyLabelMismatchesContext(ValidationContext innerContext) {
        this.innerContext = innerContext;
    }

    @Override
    public void reportProblem(Problem problem) {

        // Treat ontology label mismatches as warnings instead of errors,
        if (problem instanceof OntologyTermLabelDoesNotMatchDefinitionProblem labelMismatch) {
            // except for Material terms which must match exactly (OntologyIdAttributeValidator.pm)
            if (Arrays.stream(materialShortNameIds)
                    .noneMatch(m -> ((OntologyTermRef)labelMismatch.getAttributeValue()).getShortFormId().equals(m))) {
                labelMismatch.setIsError(false);
            }
        }

        innerContext.reportProblem(problem);
    }

    @Override
    public void reportSuggestedOntologyMapping(Entity entity, Validator reporter, String attributeNameOrPath, OntologySuggestion suggestion) {
        innerContext.reportSuggestedOntologyMapping(entity, reporter, attributeNameOrPath, suggestion);
    }
}

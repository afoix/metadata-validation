package com.afoix.faang.validation;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;
import com.afoix.metadatavalidator.problems.OntologyTermLabelDoesNotMatchDefinitionProblem;
import com.afoix.metadatavalidator.problems.Problem;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import com.afoix.metadatavalidator.validators.ValidationContext;
import com.afoix.metadatavalidator.validators.Validator;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTests {

    @Test
    public void macleodHorses() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Path xslxPath = Path.of(Objects.requireNonNull(loader.getResource("faang_metadata_examples_macleod_horses_20160205.xlsx")).toURI());
        Path checklistPath = Path.of(Objects.requireNonNull(loader.getResource("faang_samples.metadata_rules.json")).toURI());

        TestValidationContext context = new TestValidationContext();
        FaangValidationProgram.doValidate(xslxPath, checklistPath, context);

        // Some of the entities have no problems, but for some of them, the sample XLSX file
        // supplied by FAANG uses 'tissue specimen' as the Material value but their standard
        // does not actually permit that (https://data.faang.org/ruleset/samples#standard).
        assertThat(context.getProblems().stream().filter(problem -> {
            if (problem instanceof OntologyTermLabelDoesNotMatchDefinitionProblem labelMismatch) {
                if (((OntologyTermRef)labelMismatch.getAttributeValue()).getLabel().equals("tissue specimen")) {
                    return false;
                }
            }
            return true;
        })).isEmpty();
    }

    public static class TestValidationContext implements ValidationContext {

        private final List<Problem> problems = new ArrayList<>();
        private final List<OntologySuggestion> suggestions = new ArrayList<>();

        public List<Problem> getProblems() {
            return problems;
        }

        @Override
        public void reportProblem(Problem problem) {
            problems.add(problem);
        }

        @Override
        public void reportSuggestedOntologyMapping(Entity entity, Validator reporter, String attributeNameOrPath, OntologySuggestion suggestion) {
            suggestions.add(suggestion);
        }

        public List<OntologySuggestion> getSuggestions() {
            return suggestions;
        }
    }
}

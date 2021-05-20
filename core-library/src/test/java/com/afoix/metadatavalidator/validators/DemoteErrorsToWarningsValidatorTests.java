package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;
import com.afoix.metadatavalidator.problems.BaseProblem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Execution(ExecutionMode.CONCURRENT)
public class DemoteErrorsToWarningsValidatorTests {

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @Test
    public void errorsFromInnerValidator_areConvertedToWarnings() throws MisconfiguredValidatorException {
        Validator innerValidator = mock(Validator.class);
        doAnswer(invocationOnMock -> {
            Entity entity = invocationOnMock.getArgument(0);
            ValidationContext context = invocationOnMock.getArgument(1);
            BaseProblem testProblem = new BaseProblem(entity, innerValidator, "test problem");
            testProblem.setIsError(true);
            context.reportProblem(testProblem);
            return null;
        }).when(innerValidator).validate(any(), any());

        new DemoteErrorsToWarningsValidator(innerValidator)
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().satisfies(
                problem -> assertThat(problem.isError()).isFalse()
        );
    }

    @Test
    public void suggestionsFromInnerValidator_arePassedToOuterContext() throws MisconfiguredValidatorException {
        Validator innerValidator = mock(Validator.class);
        OntologySuggestion suggestion = mock(OntologySuggestion.class);
        doAnswer(invocationOnMock -> {
            Entity entity = invocationOnMock.getArgument(0);
            ValidationContext context = invocationOnMock.getArgument(1);
            context.reportSuggestedOntologyMapping(entity, innerValidator, "test attribute", suggestion);
            return null;
        }).when(innerValidator).validate(any(), any());

        new DemoteErrorsToWarningsValidator(innerValidator)
                .validate(entity, context);

        assertThat(context.getSuggestions()).singleElement().isEqualTo(suggestion);
    }
}

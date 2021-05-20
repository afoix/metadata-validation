package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.problems.AttributeIsMissingProblem;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class AttributeIsPresentValidatorTests {

    public static final String ATTRIBUTE_NAME = "attr";

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @Test
    public void whenAttributeIsPresent_noProblemsProduced() throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, "value");

        new AttributeIsPresentValidator(ATTRIBUTE_NAME).validate(entity, context);

        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    public void whenAttributeIsNotPresent_problemIsProduced() throws MisconfiguredValidatorException {

        new AttributeIsPresentValidator(ATTRIBUTE_NAME).validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeIsMissingProblem.class);
    }

}

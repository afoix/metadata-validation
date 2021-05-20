package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.problems.AttributeHasMultipleValuesProblem;
import com.afoix.metadatavalidator.problems.AttributeIsMissingProblem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class AttributeIsSingleValuedValidatorTests {
    public static final String ATTRIBUTE_NAME = "attr";

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @Test
    public void whenAttributeIsSingleValued_noProblemsProduced() throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, "value");

        new AttributeIsSingleValuedValidator(ATTRIBUTE_NAME).validate(entity, context);

        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    public void whenAttributeIsNotSingleValued_problemIsProduced() throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, "value1");
        entity.putAdditionally(ATTRIBUTE_NAME, "value2");

        new AttributeIsSingleValuedValidator(ATTRIBUTE_NAME).validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeHasMultipleValuesProblem.class);
    }
}

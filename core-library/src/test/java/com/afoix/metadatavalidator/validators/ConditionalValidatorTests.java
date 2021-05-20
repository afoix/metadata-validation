package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Execution(ExecutionMode.CONCURRENT)
public class ConditionalValidatorTests {

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @Test
    public void validate_callsInnerValidator_ifConditionIsTrue() throws MisconfiguredValidatorException {
        Validator innerValidator = mock(Validator.class);

        new ConditionalValidator(innerValidator) {
            @Override
            protected boolean shouldValidate(Entity entity) {
                return true;
            }
        }.validate(entity, context);

        verify(innerValidator).validate(entity, context);
    }

    @Test
    public void validate_doesNotCallsInnerValidator_ifConditionIsFalse() throws MisconfiguredValidatorException {
        Validator innerValidator = mock(Validator.class);

        new ConditionalValidator(innerValidator) {
            @Override
            protected boolean shouldValidate(Entity entity) {
                return false;
            }
        }.validate(entity, context);

        verify(innerValidator, never()).validate(entity, context);
    }

    @Test
    public void defaultDescription_includesDescriptionOfInnerValidator() {
        Validator innerValidator = mock(Validator.class);
        when(innerValidator.getDescription()).thenReturn("Test description");

        ConditionalValidator validator = new ConditionalValidator(innerValidator) {
            @Override
            protected boolean shouldValidate(Entity entity) {
                return false;
            }
        };

        assertThat(validator.getDescription()).contains("Test description");
    }
}

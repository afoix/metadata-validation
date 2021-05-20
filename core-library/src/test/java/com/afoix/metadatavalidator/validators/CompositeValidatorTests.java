package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.awt.image.MemoryImageSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Execution(ExecutionMode.CONCURRENT)
public class CompositeValidatorTests {

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @Test
    public void validate_callsAllValidators() throws MisconfiguredValidatorException {
        Validator validator1 = mock(Validator.class);
        Validator validator2 = mock(Validator.class);
        Validator validator3 = mock(Validator.class);

        new CompositeValidator(validator1, validator2, validator3)
                .validate(entity, context);

        InOrder inOrder = Mockito.inOrder(validator1, validator2, validator3);
        inOrder.verify(validator1).validate(entity, context);
        inOrder.verify(validator2).validate(entity, context);
        inOrder.verify(validator3).validate(entity, context);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void defaultDescription_isDescriptionsOfInnerValidators() {
        Validator validator1 = mock(Validator.class);
        when(validator1.getDescription()).thenReturn("test1");
        Validator validator2 = mock(Validator.class);
        when(validator2.getDescription()).thenReturn("test2");
        Validator validator3 = mock(Validator.class);
        when(validator3.getDescription()).thenReturn("test3");

        String description = new CompositeValidator(validator1, validator2, validator3).getDescription();

        assertThat(description).contains("test1", "test2", "test3");
    }
}

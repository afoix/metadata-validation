package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class AbstractValidatorTests {

    @Test
    public void description_canBeSetAndGet() {
        Validator validator = new AbstractValidator() {
            @Override
            public void validate(Entity entity, ValidationContext context) { }
        };

        validator.setDescription("Test description");

        assertThat(validator.getDescription()).isEqualTo("Test description");
    }

    @Test
    public void description_isDefaultToStringValue() {
        Validator validator = new AbstractValidator() {
            @Override
            public void validate(Entity entity, ValidationContext context) { }
        };

        validator.setDescription("Test description");

        assertThat(validator.toString()).isEqualTo("Test description");
    }

    @Test
    public void getDescriptionOrDefault_returnsDefault_ifNoDescriptionSet() {
        Validator validator = new AbstractValidator() {
            @Override
            public void validate(Entity entity, ValidationContext context) { }

            @Override
            public String getDescription() {
                return getDescriptionOrDefault(() -> "Default description");
            }
        };

        assertThat(validator.getDescription()).isEqualTo("Default description");
    }

    @Test
    public void getDescriptionOrDefault_returnsCustomDescription_ifItIsSet() {
        Validator validator = new AbstractValidator() {
            @Override
            public void validate(Entity entity, ValidationContext context) { }

            @Override
            public String getDescription() {
                return getDescriptionOrDefault(() -> "Default description");
            }
        };

        validator.setDescription("Custom description");

        assertThat(validator.getDescription()).isEqualTo("Custom description");
    }
}

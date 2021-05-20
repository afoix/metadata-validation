package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.problems.ValidatorCannotProcessEntityTypeProblem;
import org.junit.jupiter.api.Test;

import javax.xml.validation.Schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ConformsToXSDValidatorTests {

    @Test
    public void nonXMLEntity_throwsException() throws MisconfiguredValidatorException {
        Entity entity = new KeyValueEntity();
        Schema schema = mock(Schema.class);
        TestValidationContext context = new TestValidationContext();

        new ConformsToXSDValidator(schema, "Test schema")
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(ValidatorCannotProcessEntityTypeProblem.class);
    }
}

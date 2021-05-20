package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.problems.RelatedEntityCouldNotBeFoundProblem;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.services.BioSamplesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for AttributeValuesAreRelatedEntitiesValidator.
 *
 * Incorporates test cases from 'relationship_rules()' tests in FAANG codebase:
 * https://github.com/FAANG/dcc-validate-metadata/blob/master/t/validate_attributes.t
 */
@Execution(ExecutionMode.CONCURRENT)
public class AttributeValuesAreRelatedEntitiesValidatorTests {

    public static final String ATTRIBUTE_NAME = "attr";

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @Test
    public void entityMappingFunction_cannotMapId_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "bob");
        new AttributeValuesAreRelatedEntitiesValidator(ATTRIBUTE_NAME, id -> null, null)
                .validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(RelatedEntityCouldNotBeFoundProblem.class);
    }

    @Test
    public void entityMappingFunction_mapsId_passesValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "bob");
        new AttributeValuesAreRelatedEntitiesValidator(ATTRIBUTE_NAME,
                id -> Map.of(
                        "bob", new KeyValueEntity()
                ).get(id), null)
                .validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    public void entityMappingFunction_canMapBiosamplesId() throws MisconfiguredValidatorException, IOException {
        final String biosamplesAccessionId = "SAMEA676028";

        entity.putReplacing(ATTRIBUTE_NAME, biosamplesAccessionId);

        BioSamplesService bioSamplesService = Mockito.mock(BioSamplesService.class);
        when(bioSamplesService.fetchSample(biosamplesAccessionId)).thenReturn(new KeyValueEntity());

        new AttributeValuesAreRelatedEntitiesValidator(ATTRIBUTE_NAME, id ->
        {
            try {
                return bioSamplesService.fetchSample(id);
            } catch (IOException e) {
                return null;
            }
        }, null)
                .validate(entity, context);

        assertThat(context.getProblems()).isEmpty();
        verify(bioSamplesService).fetchSample(biosamplesAccessionId);
    }

}

package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.ontologies.OLSClient;
import com.afoix.metadatavalidator.problems.OntologyTermDoesNotExistProblem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import uk.ac.ebi.pride.utilities.ols.web.service.model.ITerm;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Execution(ExecutionMode.CONCURRENT)
public class AttributeValuesAreNCBITaxonIDsValidatorTests {
    public static final String ATTRIBUTE_NAME = "attr";

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @Test
    public void whenAttributeIsValidNCBITaxonID_noProblemsProduced() throws MisconfiguredValidatorException {
        OLSClient client = mock(OLSClient.class);
        ITerm term = mock(ITerm.class);
        when(client.getTermById(new Identifier("NCBITaxon_12345", Identifier.IdentifierType.OWL), "NCBITaxon"))
                .thenReturn(term);

        entity.putAdditionally(ATTRIBUTE_NAME, 12345);

        new AttributeValuesAreNCBITaxonIDsValidator(ATTRIBUTE_NAME, client)
                .validate(entity, context);

        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    public void whenAttributeIsNotValidNCBITaxonID_producesProblem() throws MisconfiguredValidatorException {
        OLSClient client = mock(OLSClient.class);
        ITerm term = mock(ITerm.class);
        when(client.getTermById(any(), any())).thenReturn(null);

        entity.putAdditionally(ATTRIBUTE_NAME, 12345);

        new AttributeValuesAreNCBITaxonIDsValidator(ATTRIBUTE_NAME, client)
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermDoesNotExistProblem.class);
    }
}

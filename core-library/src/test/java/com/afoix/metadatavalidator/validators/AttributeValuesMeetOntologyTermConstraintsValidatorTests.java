package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.ontologies.OLSCachingClient;
import com.afoix.metadatavalidator.problems.OntologyTermDoesNotSatisfyAnyConstraintProblem;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.ontologies.OLSPRIDEClientAdapter;
import com.afoix.metadatavalidator.problems.UseOfRootOntologyTermIsNotPermittedProblem;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.pride.utilities.ols.web.service.client.OLSClient;
import uk.ac.ebi.pride.utilities.ols.web.service.config.OLSWsConfigProd;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Tests for AttributeValuesAreOntologyIDsValidator.
 * <p>
 * Incorporates test cases from 'ontology_id_rule()' and 'ontology_uri_rule()' tests in FAANG codebase:
 * https://github.com/FAANG/dcc-validate-metadata/blob/master/t/validate_attributes.t
 */
public class AttributeValuesMeetOntologyTermConstraintsValidatorTests {
    public static final String ATTRIBUTE_NAME = "attr";

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    private final com.afoix.metadatavalidator.ontologies.OLSClient olsClient = new OLSCachingClient(new OLSPRIDEClientAdapter(new OLSClient(new OLSWsConfigProd())));

    @Test
    public void termWithCorrectAncestor_producesNoProblems() throws MisconfiguredValidatorException {
        OntologyTermRef parentTerm = OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/UBERON_0010721"));
        parentTerm.setOntologyName("UBERON");

        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofShortFormId("UBERON_0010737", "distal tarsal bone 4"));
        new AttributeValuesMeetOntologyTermConstraintsValidator(ATTRIBUTE_NAME,
                List.of(new OntologyConstraint(parentTerm, true, true)),
                olsClient)
                .validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    public void termWithWrongAncestor_isError_shortFormId() throws MisconfiguredValidatorException {
        OntologyTermRef parentTerm = OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/UBERON_0002530"));
        parentTerm.setOntologyName("UBERON");

        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofShortFormId("UBERON_0010737", "distal tarsal bone 4"));
        new AttributeValuesMeetOntologyTermConstraintsValidator(ATTRIBUTE_NAME,
                List.of(new OntologyConstraint(parentTerm, true, true)),
                olsClient)
                .validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermDoesNotSatisfyAnyConstraintProblem.class);
    }

    @Test
    public void termWithWrongAncestor_isError_iri() throws MisconfiguredValidatorException {
        OntologyTermRef parentTerm = OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/PATO_0000047"));
        parentTerm.setOntologyName("PATO");

        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/PATO_0001994"), "unicellular"));
        new AttributeValuesMeetOntologyTermConstraintsValidator(ATTRIBUTE_NAME,
                List.of(new OntologyConstraint(parentTerm, true, true)),
                olsClient)
                .validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermDoesNotSatisfyAnyConstraintProblem.class);
    }

    @Test
    public void termEqualsReferenceTerm_withRootNotPermitted_isError() throws MisconfiguredValidatorException {
        OntologyTermRef parentTerm = OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/PATO_0000047"));
        parentTerm.setOntologyName("PATO");

        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/PATO_0000047")));
        new AttributeValuesMeetOntologyTermConstraintsValidator(ATTRIBUTE_NAME,
                List.of(new OntologyConstraint(parentTerm, true, false)),
                olsClient)
                .validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(UseOfRootOntologyTermIsNotPermittedProblem.class);
    }

    @Test
    public void usingReferenceTermWithNoIdentifier_throwsException() {
        Throwable result = catchThrowable(() -> new AttributeValuesMeetOntologyTermConstraintsValidator(ATTRIBUTE_NAME,
                List.of(new OntologyConstraint(OntologyTermRef.ofLabel("vague term"), true, true)),
                        olsClient));

        assertThat(result).isInstanceOf(MisconfiguredValidatorException.class);
    }
}

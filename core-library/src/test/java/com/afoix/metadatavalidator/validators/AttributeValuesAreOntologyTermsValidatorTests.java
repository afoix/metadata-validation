package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.ontologies.OLSCachingClient;
import com.afoix.metadatavalidator.problems.OntologyIsUnknownProblem;
import com.afoix.metadatavalidator.problems.OntologyTermDoesNotExistProblem;
import com.afoix.metadatavalidator.problems.OntologyTermLabelDoesNotMatchDefinitionProblem;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.ontologies.OLSPRIDEClientAdapter;
import com.afoix.metadatavalidator.services.ZoomaService;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.ac.ebi.pride.utilities.ols.web.service.client.OLSClient;
import uk.ac.ebi.pride.utilities.ols.web.service.config.OLSWsConfigProd;

import java.net.URI;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AttributeValuesAreOntologyIDsValidator.
 * <p>
 * Incorporates test cases from 'ontology_id_rule()' tests in FAANG codebase:
 * https://github.com/FAANG/dcc-validate-metadata/blob/master/t/validate_attributes.t
 */
public class AttributeValuesAreOntologyTermsValidatorTests {

    public static final String ATTRIBUTE_NAME = "attr";

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    private final com.afoix.metadatavalidator.ontologies.OLSClient olsClient = new OLSCachingClient(new OLSPRIDEClientAdapter(new OLSClient(new OLSWsConfigProd())));
    private final ZoomaService zooma = new ZoomaService();

    public static Stream<OntologyTermRef> validTerms() {
        return Stream.of(
                OntologyTermRef.ofShortFormId("UBERON_0002107", "liver"),
                OntologyTermRef.ofOboId("UBERON:0002107", "liver"),
                OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/PATO_0020001"), "male genotypic sex")
        );
    }

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @ParameterizedTest
    @MethodSource("validTerms")
    public void validTerm_isValid(OntologyTermRef term) throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, term);
        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME,
                olsClient,
                true,
                zooma)
                .validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    public static Stream<OntologyTermRef> invalidTerms() {
        return Stream.of(
                OntologyTermRef.ofShortFormId("cbeebies", "not a term"),
                OntologyTermRef.ofIri(URI.create("http://www.bbc.co.uk/cbeebies"), "not a term")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTerms")
    public void invalidTerm_isUnknown(OntologyTermRef term) throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, term);
        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME,
                olsClient,
                true,
                zooma)
                .validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyIsUnknownProblem.class);
    }

    public static Stream<OntologyTermRef> labelMismatchTerms() {
        return Stream.of(
                OntologyTermRef.ofOboId("UBERON:0002107", "not a liver"),
                OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/PATO_0020001"), "male")
        );
    }

    @ParameterizedTest
    @MethodSource("labelMismatchTerms")
    public void termAndLabelMismatch_isError(OntologyTermRef term) throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, term);
        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME, olsClient, true, zooma)
                .validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermLabelDoesNotMatchDefinitionProblem.class);
    }

    @Test
    public void termAndLabelMismatchOnAccentedCharacter_whenMatchingIsLenient_isValid() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofShortFormId("LBO_0001002", "Moxoto"));
        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME, olsClient, true, zooma)
                .validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    public void termAndLabelMismatchOnAccentedCharacter_whenMatchingIsNotLenient_isError() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofShortFormId("LBO_0001002", "Moxoto"));
        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME, olsClient, false, zooma)
                .validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermLabelDoesNotMatchDefinitionProblem.class);
    }

    @Test
    public void labelOnlyTerm_thatExistsUnderAValidRoot_generatesSuggestion() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofLabel("liver"));

        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME,
                olsClient,
                false,
                Set.of(OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/UBERON_0002530"))),
                zooma)
                .validate(entity, context);

        assertThat(context.getProblems()).isEmpty();
        assertThat(context.getSuggestions()).singleElement().satisfies(suggestion -> {
            assertThat(suggestion.getSourceText()).isEqualTo("liver");
            assertThat(suggestion.getSuggestedTerm().getIri()).isEqualTo(URI.create("http://purl.obolibrary.org/obo/UBERON_0002107"));
        });
    }

    @Test
    public void labelOnlyTerm_thatExistsUnderTheWrongRoot_generatesProblem() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofLabel("distal tarsal bone 4"));

        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME,
                olsClient,
                false,
                Set.of(OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/UBERON_0002530"))),
                zooma)
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermDoesNotExistProblem.class);
        assertThat(context.getSuggestions()).isEmpty();
    }

    @Test
    public void labelOnlyTerm_thatDoesNotExist_generatesProblem() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofLabel("not a term"));

        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME,
                olsClient,
                false,
                Set.of(OntologyTermRef.ofIri(URI.create("http://purl.obolibrary.org/obo/UBERON_0002530"))),
                zooma)
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermDoesNotExistProblem.class);
        assertThat(context.getSuggestions()).isEmpty();
    }

    @Test
    public void labelOnlyTerm_withNoSuggestedRoots_acceptsFirstZoomaSuggestion() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, OntologyTermRef.ofLabel("horse"));

        new AttributeValuesAreOntologyTermsValidator(ATTRIBUTE_NAME,
                olsClient,
                false,
                Set.of(),
                zooma)
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermLabelDoesNotMatchDefinitionProblem.class);
        assertThat(context.getSuggestions()).singleElement().satisfies(suggestion -> {
            assertThat(suggestion.getSourceText()).isEqualTo("horse");
            assertThat(suggestion.getSuggestedTerm().getIri()).isEqualTo(URI.create("http://purl.obolibrary.org/obo/NCIT_C14222"));
        });
    }
}

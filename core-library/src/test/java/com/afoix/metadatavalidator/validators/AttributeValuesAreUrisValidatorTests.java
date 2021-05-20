package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.problems.AttributeHasMultipleValuesProblem;
import com.afoix.metadatavalidator.problems.AttributeValueIsNotValidUriProblem;
import com.afoix.metadatavalidator.problems.UriHasNoSchemeProblem;
import com.afoix.metadatavalidator.problems.UriSchemeIsNotValidProblem;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AttributeValuesAreUrisValidator.
 *
 * Incorporates test cases from 'uri_rules()' tests in FAANG codebase:
 * https://github.com/FAANG/dcc-validate-metadata/blob/master/t/validate_attributes.t
 */
@Execution(ExecutionMode.CONCURRENT)
public class AttributeValuesAreUrisValidatorTests {

    public static final String ATTRIBUTE_NAME = "attr";

    private final Validator validator = new AttributeValuesAreUrisValidator(ATTRIBUTE_NAME, AttributeValuesAreUrisValidator.DEFAULT_SCHEMES, "http");
    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @ParameterizedTest
    @ValueSource(strings={
            "https://www.ebi.ac.uk",
            "www.ebi.ac.uk",
            "mailto:bob@example.org",
            "ftp://ftp.ebi.ac.uk",
    })
    public void validURIs_passValidation(String value) throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, value);
        validator.validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    // This one is broken because 'user:' gets interpreted as the scheme, instead of applying the default scheme.
    // If the username were 'mailto' this would be indistinguishable from a valid mailto URI, so it is impossible to
    // have a completely unambiguous rule for whether we should prepend the default scheme or not.
    @Test
    @Disabled
    public void uriWithUsernameAndPasswordButNoScheme_passesValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "user:pass@ftp.ebi.ac.uk");
        validator.validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    public void invalidURI_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "not actually a URI in a way");
        validator.validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeValueIsNotValidUriProblem.class);
    }

    @Test
    public void multiUris_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "https://www.ebi.ac.uk;ftp://ftp.ebi.ac.uk");
        validator.validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeHasMultipleValuesProblem.class);
    }

    @Test
    public void uriUsingUnsupportedScheme_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "telnet://bob:password@example.org:9000");
        validator.validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(UriSchemeIsNotValidProblem.class);
    }

    @Test
    public void uriWithNoScheme_andNoDefaultSchemeIsSet_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "www.ebi.ac.uk");

        new AttributeValuesAreUrisValidator(ATTRIBUTE_NAME, AttributeValuesAreUrisValidator.DEFAULT_SCHEMES, null)
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(UriHasNoSchemeProblem.class);
    }
}

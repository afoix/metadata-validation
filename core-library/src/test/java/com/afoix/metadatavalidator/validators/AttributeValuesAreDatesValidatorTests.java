package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.problems.AttributeValueIsNotADateProblem;
import com.afoix.metadatavalidator.problems.DateIsNotInAcceptableFormatProblem;
import com.afoix.metadatavalidator.problems.DateValueIsNotValidProblem;
import com.afoix.metadatavalidator.utils.DateWithFormat;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AttributeValuesAreDatesValidator.
 *
 * Incorporates test cases from 'date_rules()' tests in FAANG codebase:
 * https://github.com/FAANG/dcc-validate-metadata/blob/master/t/validate_attributes.t
 */
@Execution(ExecutionMode.CONCURRENT)
public class AttributeValuesAreDatesValidatorTests {

    public static final String ATTRIBUTE_NAME = "attr";

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    public static Stream<Object> validDateObjects() {
        return Stream.of(
                Named.of("Regular date", new DateWithFormat("2016-02-01", "YYYY-MM-DD")),
                Named.of("Leap day in leap year", new DateWithFormat("2016-02-29", "YYYY-MM-DD")),
                Named.of("Year and month only", new DateWithFormat("2016-02", "YYYY-MM")),
                Named.of("LocalDateTime object", LocalDateTime.now())
                );
    }

    @ParameterizedTest
    @MethodSource("validDateObjects")
    public void validDate_causesNoProblems(Object value) throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, value);
        new AttributeValuesAreDatesValidator(ATTRIBUTE_NAME).validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    public static Stream<Object> invalidDateObjects() {
        return Stream.of(
                Named.of("Impossible day", new DateWithFormat("2016-02-32", "YYYY-MM-DD")),
                Named.of("Leap day in non-leap year", new DateWithFormat("2015-02-29", "YYYY-MM-DD")),
                Named.of("Unparseable date", new DateWithFormat("this is not a date", "YYYY-MM-DD")),
                Named.of("Invalid month", new DateWithFormat("2016-31", "YYYY-MM"))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDateObjects")
    public void invalidDate_isNotValid(Object value) throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, value);
        new AttributeValuesAreDatesValidator(ATTRIBUTE_NAME).validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(DateValueIsNotValidProblem.class);
    }

    @Test
    public void nonDateObject_isNotValid() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "this is not a date");
        new AttributeValuesAreDatesValidator(ATTRIBUTE_NAME).validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeValueIsNotADateProblem.class);
    }

    @Test
    public void unacceptableFormat_isNotValid() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, new DateWithFormat("02-03-2015", "MM-DD-YYYY"));
        new AttributeValuesAreDatesValidator(ATTRIBUTE_NAME).validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(DateIsNotInAcceptableFormatProblem.class);
    }

    @Test
    public void unacceptableFormat_withCustomFormat_isNotValid() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, new DateWithFormat("02-03-2015", "DD-MM-YYYY"));
        new AttributeValuesAreDatesValidator(ATTRIBUTE_NAME, Set.of("MM-DD-YYYY")).validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(DateIsNotInAcceptableFormatProblem.class);
    }
}

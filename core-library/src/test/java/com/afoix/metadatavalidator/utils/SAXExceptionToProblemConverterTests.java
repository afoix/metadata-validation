package com.afoix.metadatavalidator.utils;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.XSDValidationProblem;
import com.afoix.metadatavalidator.validators.TestValidationContext;
import com.afoix.metadatavalidator.validators.Validator;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SAXExceptionToProblemConverterTests {

    private final TestValidationContext context = new TestValidationContext();
    private final Entity entity = mock(Entity.class);
    private final Validator validator = mock(Validator.class);

    @Test
    public void fatalError_isConverted_toError() {
        SAXExceptionToProblemConverter converter = new SAXExceptionToProblemConverter(context, entity, validator);
        SAXParseException exception = mock(SAXParseException.class);

        converter.fatalError(exception);

        assertThat(context.getProblems()).singleElement().satisfies(
                problem -> {
                    assertThat(problem).isInstanceOf(XSDValidationProblem.class);
                    assertThat(problem.isError()).isTrue();
                }
        );
    }

    @Test
    public void nonFatalError_isConverted_toError() {
        SAXExceptionToProblemConverter converter = new SAXExceptionToProblemConverter(context, entity, validator);
        SAXParseException exception = mock(SAXParseException.class);

        converter.error(exception);

        assertThat(context.getProblems()).singleElement().satisfies(
                problem -> {
                    assertThat(problem).isInstanceOf(XSDValidationProblem.class);
                    assertThat(problem.isError()).isTrue();
                }
        );
    }

    @Test
    public void warning_isConverted_toWarning() {
        SAXExceptionToProblemConverter converter = new SAXExceptionToProblemConverter(context, entity, validator);
        SAXParseException exception = mock(SAXParseException.class);

        converter.warning(exception);

        assertThat(context.getProblems()).singleElement().satisfies(
                problem -> {
                    assertThat(problem).isInstanceOf(XSDValidationProblem.class);
                    assertThat(problem.isError()).isFalse();
                }
        );
    }
}

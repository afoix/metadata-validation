package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class AttributeValuesAreUrisValidator extends AttributeValuesValidator {

    private final Set<String> supportedSchemes;
    private final String defaultSchemeIfNotSpecified;

    public static final Set<String> DEFAULT_SCHEMES = Set.of("http", "https", "mailto", "ftp");

    public AttributeValuesAreUrisValidator(String attributeNameOrPath, Set<String> supportedSchemes, String defaultSchemeIfNotSpecified) {
        super(attributeNameOrPath);
        this.supportedSchemes = supportedSchemes;
        this.defaultSchemeIfNotSpecified = defaultSchemeIfNotSpecified;
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) {

        if (value == null) {
            context.reportProblem(problemBuilder.isNull());
            return;
        }

        String valueString = value.toString();
        if (valueString.contains(";")) {
            context.reportProblem(problemBuilder.getAttributeProblemBuilder().isMultiValued());
            return;
        }

        URI uri;
        try {
            uri = new URI(valueString);
        } catch (URISyntaxException e) {
            context.reportProblem(problemBuilder.isNotValidUri());
            return;
        }

        String scheme = uri.getScheme();

        if ((scheme == null || scheme.isEmpty())) {
            scheme = defaultSchemeIfNotSpecified;
        }

        if (scheme == null) {
            context.reportProblem(problemBuilder.uriHasNoScheme());
        } else if (!supportedSchemes.contains(scheme)) {
            context.reportProblem(problemBuilder.uriSchemeIsNotValid());
        }
    }
}

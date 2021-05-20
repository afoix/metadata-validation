package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.entities.JSONEntity;
import com.afoix.metadatavalidator.problems.AttributeIsMissingProblem;
import com.afoix.metadatavalidator.problems.JSONSchemaValidationProblem;
import com.afoix.metadatavalidator.problems.Problem;
import com.afoix.metadatavalidator.problems.ValidatorCannotProcessEntityTypeProblem;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConformsToJSONSchemaValidator extends AbstractValidator {

    Schema schema;

    public ConformsToJSONSchemaValidator(URL schemaURL) throws IOException {
        setDescription("Validates that the entity conforms to the JSON Schema described at URL " + schemaURL);

        JSONObject schemaJson;
        try (InputStream stream = schemaURL.openStream()) {
            schemaJson = new JSONObject(new JSONTokener(stream));
        }
        schema = SchemaLoader.load(schemaJson);
    }

    @Override
    public void validate(Entity entity, ValidationContext context) {
        if (!(entity instanceof JSONEntity jsonEntity)) {
            context.reportProblem(new ValidatorCannotProcessEntityTypeProblem(entity, this, "Cannot validate non-JSON entities with ConformsToJSONSchemaValidator"));
            return;
        }

        try {
            schema.validate(jsonEntity.getRoot());
        } catch (ValidationException validationException) {
            if (validationException.getCausingExceptions().isEmpty()) {
                context.reportProblem(translateExceptionToProblem(entity, validationException));
            } else {
                for (ValidationException innerException :
                        validationException.getCausingExceptions()) {
                    context.reportProblem(translateExceptionToProblem(entity, innerException));
                }
            }
        }
    }

    private final Pattern requiredKeyMissingRegex = Pattern.compile("^required key \\[(.*)\\] not found$");

    @NotNull
    private Problem translateExceptionToProblem(Entity entity, ValidationException validationException) {
        Matcher matcher = requiredKeyMissingRegex.matcher(validationException.getErrorMessage());
        if (matcher.matches()) {
            return new AttributeIsMissingProblem(entity, this, matcher.group(1));
        }

        return new JSONSchemaValidationProblem(entity, this, validationException);
    }
}

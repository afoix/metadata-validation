package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.entities.XMLEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.problems.ValidatorCannotProcessEntityTypeProblem;
import com.afoix.metadatavalidator.utils.SAXExceptionToProblemConverter;
import org.xml.sax.SAXException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class ConformsToXSDValidator extends AbstractValidator {

    private final javax.xml.validation.Schema schema;

    public ConformsToXSDValidator(File schemaFile) throws SAXException {
        setDescription("Validates that the entity conforms to the XSD schema described in the file at path " + schemaFile.getPath());

        SchemaFactory factory = SchemaFactory.newDefaultInstance();
        schema = factory.newSchema(schemaFile);
    }

    public ConformsToXSDValidator(javax.xml.validation.Schema schema, String sourceName) {
        setDescription("Validates that the entity conforms to the XSD schema named \"" + sourceName + "\"");
        this.schema = schema;
    }

    @Override
    public void validate(Entity entity, ValidationContext context) throws MisconfiguredValidatorException {
        if (!(entity instanceof XMLEntity xmlEntity)) {
            context.reportProblem(new ValidatorCannotProcessEntityTypeProblem(entity, this, "Cannot validate non-XML entities with ConformsToXSDValidator"));
            return;
        }

        try {
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new SAXExceptionToProblemConverter(context, entity, this));
            validator.validate(new DOMSource(xmlEntity.getRoot()));
        } catch (SAXException e) {
            throw new MisconfiguredValidatorException(e);
        } catch (IOException e) {
            // Can not happen because we are not using a SAXSource
        }
    }

}

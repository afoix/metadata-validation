package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.utils.DateWithFormat;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttributeValuesAreDatesValidator extends AttributeValuesValidator {


    public static final List<String> DEFAULT_PERMITTED_FORMATS = List.of("YYYY-MM-DD", "YYYY-MM", "YYYY");
    private final Set<String> permittedFormats;

    public AttributeValuesAreDatesValidator(String attributeNameOrPath) {
        this(attributeNameOrPath, DEFAULT_PERMITTED_FORMATS);
    }

    public AttributeValuesAreDatesValidator(String attributeNameOrPath, Iterable<String> permittedFormats) {
        super(attributeNameOrPath);
        this.permittedFormats = new HashSet<>();
        permittedFormats.forEach(this.permittedFormats::add);
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) throws MisconfiguredValidatorException {
        if (value == null) {
            context.reportProblem(problemBuilder.isNull());
            return;
        }

        // If the value is already a Temporal object like a LocalDateTime then assume it is OK
        if (value instanceof Temporal) {
            return;
        }

        if (!(value instanceof DateWithFormat dateWithFormat)) {
            context.reportProblem(problemBuilder.isNotDate());
            return;
        }

        if (!permittedFormats.contains(dateWithFormat.getFormat())) {
            context.reportProblem(problemBuilder.dateIsNotInAcceptableFormat());
            return;
        }

        // convert the components of the date to what SimpleDateFormat expects
        String convertedFormat = dateWithFormat.getFormat()
                .replace("Y", "y")
                .replace("D", "d");

        SimpleDateFormat parser = new SimpleDateFormat(convertedFormat);
        Date date;
        try {
            date = parser.parse(dateWithFormat.getDateExpression());
        } catch (ParseException e) {
            context.reportProblem(problemBuilder.dateIsNotValid());
            return;
        }

        // SimpleDateFormat automatically adjusts invalid dates, which we do not want
        // To check if the date was valid, convert it back to a string and see if the
        // result is the same
        if (!parser.format(date).equals(dateWithFormat.getDateExpression())) {
            context.reportProblem(problemBuilder.dateIsNotValid());
            return;
        }

    }
}

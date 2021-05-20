package com.afoix.metadatavalidator.problems.builders;

import com.afoix.metadatavalidator.problems.*;
import com.afoix.metadatavalidator.utils.OntologyTermRef;

public class AttributeValueProblemBuilder {
    private final Object attributeValue;
    private final AttributeProblemBuilder attributeProblemBuilder;

    public AttributeValueProblemBuilder(AttributeProblemBuilder attributeProblemBuilder, Object attributeValue) {
        this.attributeProblemBuilder = attributeProblemBuilder;
        this.attributeValue = attributeValue;
    }

    public AttributeProblemBuilder getAttributeProblemBuilder() {
        return attributeProblemBuilder;
    }

    public AttributeValueProblem isNull() {
        return new AttributeValueIsNullProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath
        );
    }

    public AttributeValueProblem isNotNumeric() {
        return new AttributeValueIsNotNumericProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem isNotInPrescribedSet() {
        return new AttributeValueIsNotInSetProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem isNotValidUri() {
        return new AttributeValueIsNotValidUriProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem uriHasNoScheme() {
        return new UriHasNoSchemeProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem uriSchemeIsNotValid() {
        return new UriSchemeIsNotValidProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem unknownOntology() {
        return new OntologyIsUnknownProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }


    public AttributeValueProblem isNotOntologyTermRef() {
        return new AttributeValueIsNotOntologyTermRefProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem ontologyTermDoesNotExist() {
        return new OntologyTermDoesNotExistProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem isNotValidOntologyIri() {
        return new OntologyTermHasNoValidIriProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem ontologyTermLabelDoesNotMatch(String expectedLabel) {
        return new OntologyTermLabelDoesNotMatchDefinitionProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue,
                expectedLabel
        );
    }

    public AttributeValueProblem useOfRootTermNotPermitted(OntologyTermRef rootTerm) {
        return new UseOfRootOntologyTermIsNotPermittedProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue,
                rootTerm
        );
    }

    public AttributeValueProblem ontologyTermDoesNotSatisfyAnyConstraint() {
        return new OntologyTermDoesNotSatisfyAnyConstraintProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem relatedEntityNotFound() {
        return new RelatedEntityCouldNotBeFoundProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem measurementUnitsNotAcceptable() {
        return new MeasurementUnitsNotAcceptableProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem measurementMissingUnits() {
        return new MeasurementHasNoUnitsProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem valueIsNotMeasurement() {
        return new AttributeValueIsNotAMeasurementProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem isNotDate() {
        return new AttributeValueIsNotADateProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem dateIsNotInAcceptableFormat() {
        return new DateIsNotInAcceptableFormatProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public AttributeValueProblem dateIsNotValid() {
        return new DateValueIsNotValidProblem(
                attributeProblemBuilder.entity,
                attributeProblemBuilder.reporter,
                attributeProblemBuilder.attributeNameOrPath,
                attributeValue
        );
    }

    public Object getAttributeValue() {
        return attributeValue;
    }
}

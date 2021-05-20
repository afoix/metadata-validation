package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.ontologies.OLSClient;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ebi.pride.utilities.ols.web.service.model.ITerm;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Identifier;

import java.net.URI;
import java.util.List;

public class AttributeValuesMeetOntologyTermConstraintsValidator extends AttributeValuesValidator {

    private final List<OntologyConstraint> validTerms;
    private final OLSClient olsClient;

    public AttributeValuesMeetOntologyTermConstraintsValidator(String attributeNameOrPath, List<OntologyConstraint> validTerms, OLSClient olsClient) throws MisconfiguredValidatorException {
        super(attributeNameOrPath);
        this.validTerms = validTerms;
        this.olsClient = olsClient;

        for (OntologyConstraint constraint : validTerms) {
            if (constraint.getTerm().getOntologyName() == null) {
                constraint.getTerm().setOntologyName(constraint.getTerm().guessOntologyName());
                if (constraint.getTerm().getOntologyName() == null) {
                    throw new MisconfiguredValidatorException(new Exception("Ontology name must be specified or derivable from the term ID"));
                }
            }
            if (constraint.getTerm().toOLSIdentifier() == null) {
                throw new MisconfiguredValidatorException(new Exception("Don't know how to convert this OntologyTermRef to an OLS identifier object"));
            }
        }
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) throws MisconfiguredValidatorException {
        if (!(value instanceof OntologyTermRef termRef)) {
            context.reportProblem(problemBuilder.isNotOntologyTermRef());
            return;
        }

        ITerm term = null;
        Identifier identifier = termRef.toOLSIdentifier();
        if (identifier != null) {
            try {
                term = olsClient.getTermById(identifier, termRef.guessOntologyName());
            } catch(HttpClientErrorException exception) {
                if (exception.getStatusCode() != HttpStatus.NOT_FOUND) {
                    throw exception;
                }
            }
        }

        if (term == null) {
            context.reportProblem(problemBuilder.ontologyTermDoesNotExist());
            return;
        }

        for (OntologyConstraint validTerm : validTerms) {
            ITerm root = olsClient.getTermById(validTerm.getTerm().toOLSIdentifier(), validTerm.getTerm().getOntologyName());

            if (term.getIri().equals(root.getIri())) {
                if (!validTerm.isIncludeRoot()) {
                    context.reportProblem(problemBuilder.useOfRootTermNotPermitted(OntologyTermRef.ofIri(URI.create(root.getIri().getIdentifier()), root.getName())));
                }
                return;
            }

            if (!validTerm.isAllowDescendents()) {
                // No need to check children if they are not allowed
                continue;
            }

            if (olsClient.isTermChildOf(term, root)) {
                return;
            }
        }
        context.reportProblem(problemBuilder.ontologyTermDoesNotSatisfyAnyConstraint());
    }

}

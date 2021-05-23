package com.afoix.controllers;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.entities.XMLEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.loaders.XMLEntityLoader;
import com.afoix.metadatavalidator.ontologies.OLSCachingClient;
import com.afoix.metadatavalidator.ontologies.OLSPRIDEClientAdapter;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;
import com.afoix.metadatavalidator.problems.Problem;
import com.afoix.metadatavalidator.validators.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
import uk.ac.ebi.pride.utilities.ols.web.service.client.OLSClient;
import uk.ac.ebi.pride.utilities.ols.web.service.config.OLSWsConfigProd;

import javax.servlet.http.HttpServletRequest;
import javax.xml.validation.SchemaFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ValidationController {

    final Map<String, Validator> validators;

    public ValidationController() throws URISyntaxException, SAXException, FileNotFoundException, MisconfiguredValidatorException {

        com.afoix.metadatavalidator.ontologies.OLSClient olsClient = new OLSCachingClient(new OLSPRIDEClientAdapter(new OLSClient(new OLSWsConfigProd())));

        validators = Map.of(
                "sample", new CompositeValidator(
                    getXSDValidator("SRA.sample.xsd"),
                    new AttributeValuesAreNCBITaxonIDsValidator("/SAMPLE_SET/SAMPLE/SAMPLE_NAME/TAXON_ID", olsClient)
                )
        );
    }

    private ConformsToXSDValidator getXSDValidator(String resourceName) throws URISyntaxException, SAXException, FileNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL schemaURL = loader.getResource("schemas/" + resourceName);
        if (schemaURL == null) {
            throw new FileNotFoundException("Could not find resource file schemas/" + resourceName);
        }

        SchemaFactory factory = SchemaFactory.newDefaultInstance();
        return new ConformsToXSDValidator(factory.newSchema(schemaURL), resourceName);
    }

    static class ValidationResult {
        @Schema(description="The category of the message", allowableValues = {"error", "warning", "suggestion"})
        final String type;

        @Schema(description="A message about something that was discovered during validation.")
        final String message;

        ValidationResult(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }
    }

    @PostMapping(value="/{schemaType}/validate",
            consumes=MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Validate an XML metadata document.",
            requestBody = @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_XML_VALUE)),
            responses = {
                    @ApiResponse(responseCode = "200", description="The provided XML metadata is valid."),
                    @ApiResponse(responseCode = "400", description="The provided XML metadata did not pass validation.")
            }
    )
    public ResponseEntity<List<ValidationResult>> validate(@PathVariable String schemaType,
                                                           HttpServletRequest request) throws IOException, MisconfiguredValidatorException {

        Validator validator = validators.get(schemaType);
        if (validator == null) {
            return ResponseEntity.notFound().build();
        }

        List<XMLEntity> entities;
        XMLEntityLoader loader = new XMLEntityLoader(request.getInputStream()).withSourceName("Submitted entity");
        try {
            entities = loader.load().toList();
        } catch (InvalidFormatException e) {
            return ResponseEntity.badRequest().body(List.of(new ValidationResult("fatal", e.toString())));
        }

        List<ValidationResult> result = new ArrayList<>();
        final Boolean[] anyErrors = {false};
        ValidationContext context = new ValidationContext() {
            @Override
            public void reportProblem(Problem problem) {
                result.add(new ValidationResult(problem.isError() ? "error" : "warning", problem.getEntity().getIdentifier() + ": " + problem.getMessage()));
                anyErrors[0] |= problem.isError();
            }

            @Override
            public void reportSuggestedOntologyMapping(Entity entity, Validator reporter, String attributeNameOrPath, OntologySuggestion suggestion) {
                result.add(new ValidationResult("suggestion",
                        "Value \"" + suggestion.getSourceText() +
                                "\" for attribute " + attributeNameOrPath +
                                " could be mapped to ontology term " + suggestion.getSuggestedTerm()));
            }
        };

        for (XMLEntity entity :
                entities) {
            validator.validate(entity, context);
        }

        if (anyErrors[0]) {
            return ResponseEntity.badRequest().body(result);
        } else {
            return ResponseEntity.ok(result);
        }
    }
}

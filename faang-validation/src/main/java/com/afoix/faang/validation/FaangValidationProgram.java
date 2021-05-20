package com.afoix.faang.validation;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.ontologies.OLSCachingClient;
import com.afoix.metadatavalidator.ontologies.OLSPRIDEClientAdapter;
import com.afoix.metadatavalidator.services.BioSamplesService;
import com.afoix.metadatavalidator.services.ZoomaService;
import com.afoix.metadatavalidator.validators.ValidationContext;
import com.afoix.metadatavalidator.validators.Validator;
import org.apache.commons.cli.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import uk.ac.ebi.pride.utilities.ols.web.service.client.OLSClient;
import uk.ac.ebi.pride.utilities.ols.web.service.config.OLSWsConfigProd;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FaangValidationProgram {
    public static void main(String[] args) throws IOException, InvalidFormatException, MisconfiguredValidatorException {

        Options options = new Options();

        Option rulesFileOption = new Option("r", "rules", true, "path to the JSON rules file to use");
        rulesFileOption.setRequired(true);
        options.addOption(rulesFileOption);

        Option metadataFileOption = new Option("m", "metadata", true, "path to the metadata file to validate");
        metadataFileOption.setRequired(true);
        options.addOption(metadataFileOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("faang-validation", options);

            System.exit(1);
            return;
        }

        String rulesFile = cmd.getOptionValue(rulesFileOption.getOpt());
        String metadataFile = cmd.getOptionValue(metadataFileOption.getOpt());

        ConsolePrintingContext context = new ConsolePrintingContext();
        doValidate(Path.of(metadataFile), Path.of(rulesFile), context);

        System.exit(context.anyErrors() ? 1 : 0);
    }

    static void doValidate(Path metadataFilePath, Path rulesFilePath, ValidationContext context) throws IOException, InvalidFormatException, MisconfiguredValidatorException {
        List<KeyValueEntity> data = new FaangXLSXLoader(metadataFilePath.toFile()).load().toList();

        RulesFileLoader rulesFileLoader = new RulesFileLoader(
                new OLSCachingClient(new OLSPRIDEClientAdapter(new OLSClient(new OLSWsConfigProd()))),
                new BioSamplesService(),
                new ZoomaService()
        );

        Validator ruleset = rulesFileLoader.createValidatorsFromJsonFile(rulesFilePath);

        for (KeyValueEntity entity : data) {
            ruleset.validate(entity, new WarnOnOntologyLabelMismatchesContext(context));
        }
    }
}

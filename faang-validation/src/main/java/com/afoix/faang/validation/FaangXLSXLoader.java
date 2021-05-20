package com.afoix.faang.validation;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.loaders.EntityLoader;
import com.afoix.metadatavalidator.loaders.XLSXEntityLoader;
import com.afoix.metadatavalidator.utils.Measurement;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FaangXLSXLoader implements EntityLoader<KeyValueEntity> {

    // Only particular sheet names are relevant for the faang_samples checklist
    public static final List<String> PERMITTED_SHEET_NAMES = List.of(
            "animal",
            "specimen",
            "pool of specimens",
            "purified cells",
            //"cell culture",
            "cell line"
    );

    private final File sourceFile;

    public FaangXLSXLoader(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public Stream<KeyValueEntity> load() throws IOException, InvalidFormatException {
        return new XLSXEntityLoader(sourceFile)
                .withWorksheetFilter(PERMITTED_SHEET_NAMES::contains)
                .withRowPostprocessor(row -> {

                    mergeColumnsWithUnits(row);
                    mergeOntologyRefColumns(row);

                    // Do not keep attributes with null values
                    row.removeIf(e -> e.getValue() == null);
                })
                .load()
                .map(entity -> {
                    entity.setIdentifier(entity.getAttributeValues("Sample Name")
                            .findAny().orElseThrow().toString());
                    return entity;
                });
    }

    private static void mergeColumnsWithUnits(List<Map.Entry<String, Object>> row) {
        // Any column named 'unit' or 'units' gets merged into the previous one
        for (int index = 1; index < row.size(); index++) {
            if (row.get(index).getKey().equalsIgnoreCase("unit") || row.get(index).getKey().equalsIgnoreCase("units")) {
                Object value = row.get(index - 1).getValue();
                if (value instanceof Double) {
                    Measurement measurement = new Measurement((Double) value, (String) row.get(index).getValue());
                    row.get(index - 1).setValue(measurement);
                }
                row.remove(index);
                index--;
            }
        }
    }

    private static void mergeOntologyRefColumns(List<Map.Entry<String, Object>> row) {
        // 'Term Source REF' and 'Term Source ID' get merged into the preceding column as an ontology term
        for (int index = 0; index < row.size() - 2; index++) {
            if (row.get(index + 1).getKey().equalsIgnoreCase("Term Source REF")
                    && row.get(index + 2).getKey().equalsIgnoreCase("Term Source ID")) {
                Object label = row.get(index).getValue();
                Object termSourceRef = row.get(index + 1).getValue();
                Object termSourceId = row.get(index + 2).getValue();

                OntologyTermRef term = null;
                if (termSourceId != null) {
                    term = OntologyTermRef.ofUnknownId((String) termSourceId);
                }

                if (label != null) {
                    if (term == null) {
                        term = new OntologyTermRef();
                    }
                    term.setLabel((String) label);
                }

                if (termSourceRef != null && term != null) {
                    term.setOntologyName((String) termSourceRef);
                }

                row.get(index).setValue(term);
                row.remove(index + 2);
                row.remove(index + 1);
            }
        }
    }
}

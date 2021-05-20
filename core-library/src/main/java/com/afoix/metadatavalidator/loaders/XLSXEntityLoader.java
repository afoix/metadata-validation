package com.afoix.metadatavalidator.loaders;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class XLSXEntityLoader implements EntityLoader<KeyValueEntity> {
    private final File sourceFile;
    private String sourceName;
    private Predicate<String> worksheetFilter;
    private Consumer<List<Map.Entry<String, Object>>> rowPostProcessor;

    public XLSXEntityLoader(File sourceFile) {
        this.sourceFile = sourceFile;
        this.sourceName = sourceFile.getName();
    }

    public XLSXEntityLoader withSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    public XLSXEntityLoader withWorksheetFilter(Predicate<String> worksheetFilter) {
        this.worksheetFilter = worksheetFilter;
        return this;
    }

    public XLSXEntityLoader withRowPostprocessor(Consumer<List<Map.Entry<String, Object>>> postprocessor) {
        this.rowPostProcessor = postprocessor;
        return this;
    }

    @Override
    public Stream<KeyValueEntity> load() throws IOException, InvalidFormatException {
        Workbook xlsx = WorkbookFactory.create(sourceFile);
        // Load each sheet of the workbook, which contains a list of key-value pairs
        List<KeyValueEntity> data = new ArrayList<>();
        for (int sheetIndex = 0; sheetIndex < xlsx.getNumberOfSheets(); sheetIndex++) {
            String sheetName = xlsx.getSheetName(sheetIndex);
            if (worksheetFilter != null && !worksheetFilter.test(sheetName))
                continue;

            Sheet sheet = xlsx.getSheetAt(sheetIndex);
            loadSheet(data, sheet);
        }
        return data.stream();
    }

    private void loadSheet(List<KeyValueEntity> data, Sheet sheet) {
        Row headerRow = sheet.getRow(sheet.getTopRow());
        for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row dataRow = sheet.getRow(rowIndex);
            if (dataRow == null)
                continue;

            loadRow(data, headerRow, dataRow, sheet.getSheetName());
        }
    }

    private void loadRow(List<KeyValueEntity> data, Row headerRow, Row dataRow, String sheetName) {
        KeyValueEntity entityData = new KeyValueEntity();
        List<Map.Entry<String, Object>> extractedValues = new ArrayList<>();
        for (int cellIndex = headerRow.getFirstCellNum(); cellIndex < headerRow.getLastCellNum(); ++cellIndex) {
            String key = headerRow.getCell(cellIndex).getStringCellValue();
            Object value;
            Cell valueCell = dataRow.getCell(cellIndex);
            if (valueCell != null) {
                switch (valueCell.getCellTypeEnum()) {
                    case STRING -> value = valueCell.getStringCellValue();
                    case NUMERIC -> value = valueCell.getNumericCellValue();
                    case BLANK -> value = null;
                    default -> throw new UnsupportedOperationException("Unsupported cell type " + valueCell.getCellTypeEnum());
                }
            } else
                value = null;
            extractedValues.add(new AbstractMap.SimpleEntry<>(key, value));
        }

        if (rowPostProcessor != null) {
            rowPostProcessor.accept(extractedValues);
        }

        if (extractedValues.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry :
                extractedValues) {
            entityData.putAdditionally(entry.getKey(), entry.getValue());
        }

        entityData.setIdentifier(String.format("%s (%s, row %d)", sourceName, sheetName, dataRow.getRowNum()));
        data.add(entityData);
    }
}

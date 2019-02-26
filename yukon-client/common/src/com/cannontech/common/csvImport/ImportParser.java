package com.cannontech.common.csvImport;

import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;

public class ImportParser {
    private final ImportFileFormat format;
    
    /**
     * Creates a new parser that will parse import files based on the specified format.
     */
    public ImportParser(ImportFileFormat format) {
        this.format = format;
    }
    
    /**
     * Creates a new ImportData object from the CSVReader's file. 
     * 
     * @return The ImportData object.
     * @throws IOException If an error occurred reading the .csv file.
     */
    public ImportData parseFromCsvReader(CSVReader csvReader) throws IOException {
        List<String[]> stringData = csvReader.readAll();
        ImportData importData = new ImportData(stringData, format);
        return importData;
    }
}

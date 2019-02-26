package com.cannontech.common.bulk.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.point.PointCalculation;
import com.opencsv.CSVReader;

public interface CalculationImportService {

    /**
     * Returns the calculation file format.
     */
    public ImportFileFormat getFormat();
    
    /**
     * Attempts to parse the .csv calculation file provided in the form of a CSVReader.
     * @return a map of calculation names to PointCalculations.
     */
    public Map<String, PointCalculation> parseCalcFile(CSVReader csvReader, List<String> results, boolean ignoreInvalidColumns) throws IOException;
    
}

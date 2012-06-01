package com.cannontech.capcontrol.creation.service;

import java.util.List;

import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportResult;

public interface RegulatorImportService {
    
    /**
     * @return The import file format for a regulator import.
     */
    public ImportFileFormat getFormat();
    
    /**
     * Attempts a regulator import for the data specified in the data.
     */
    public List<ImportResult> startImport(ImportData importData);
}

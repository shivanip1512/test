package com.cannontech.capcontrol.creation.service;

import java.util.List;

import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportResult;

public interface RegulatorPointMappingImportService {
    
    /**
     * @return The import file format for a regulator point mapping import.
     */
    public ImportFileFormat getFormat();
    
    /**
     * Attempts a regulator point mapping import for the specified import data.
     */
    public List<ImportResult> startImport(ImportData importData);
}

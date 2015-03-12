package com.cannontech.capcontrol.creation.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.csvImport.CsvImportResult;
import com.cannontech.common.csvImport.CsvImportResultType;
import com.cannontech.common.csvImport.ImportValidationResult;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.PaoDao;

public class RegulatorImportHelper {
    @Autowired PaoDao paoDao;
    
    /**
     * Returns the pao for the regulator with this name, or null if no such regulator exists
     */
    public YukonPao findRegulatorPao(String name) {
        return paoDao.findYukonPao(name, PaoCategory.DEVICE, PaoClass.CAPCONTROL);
    }
    
    /**
     * Takes a validation result and returns a representative import result
     */
    public CsvImportResult processValidationResult(ImportValidationResult validationResult) {
        String[] invalidColumns = validationResult.getInvalidColumns().toArray(new String[1]);
        if(validationResult.isBadValue()) {
            return new CsvImportResult(CsvImportResultType.BAD_DATA, invalidColumns);
        } else if(validationResult.isMissingValue()) {
            return new CsvImportResult(CsvImportResultType.MISSING_DATA, invalidColumns);
        } else {
            throw new IllegalArgumentException("Unable to process validation result with type " + validationResult.getType());
        }
    }
}

package com.cannontech.capcontrol.creation.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.creation.service.RegulatorImportService;
import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportFileValidator;
import com.cannontech.common.csvImport.CsvImportResult;
import com.cannontech.common.csvImport.CsvImportResultType;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.ImportValidationResult;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.ImportResult;
import com.cannontech.common.csvImport.types.RegulatorType;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteRegulator;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.PaoDao;
import com.google.common.collect.Lists;

public class RegulatorImportServiceImpl implements RegulatorImportService {
    private static ImportFileFormat importFormat;
    
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private PaoDao paoDao;
    @Autowired private VoltageRegulatorDao regulatorDao;
    
    //This block specifies the format for voltage regulator import files
    static {
        importFormat = new ImportFileFormat();
        //REQUIRED
        importFormat.addRequiredColumn("ACTION", ImportAction.class, false);
        importFormat.addRequiredColumn("NAME", String.class, false);
        //VALUE DEPENDENT
        importFormat.addValueDependentColumn("TYPE", RegulatorType.class, false, "ACTION", ImportAction.ADD);
        //OPTIONAL
        importFormat.addOptionalColumn("DESCRIPTION", String.class, false);
        importFormat.addOptionalColumn("DISABLED", Boolean.class, false);
        importFormat.addOptionalColumn("KEEP ALIVE TIMER", Integer.class, false);
        importFormat.addOptionalColumn("KEEP ALIVE CONFIG", Integer.class, false);
    }
    
    /**
     * Retrieves the format for regulator import files.
     */
    public ImportFileFormat getFormat() {
        return importFormat;
    }
    
    /**
     * Runs an import of the supplied import data. Each row is validated, then imported if there are
     * no validation errors.
     */
    public List<ImportResult> startImport(ImportData importData) {
        List<ImportResult> results = Lists.newArrayList();
        
        List<ImportRow> rows = importData.getRows();
        for(ImportRow row : rows) {
            CsvImportResult result = null;
            
            ImportValidationResult validationResult = ImportFileValidator.validateRow(row, importData.getFormat());
            if(validationResult.isFailed()) {
                result = processValidationResult(validationResult);
            } else {
                ImportAction action = ImportAction.valueOf(row.getValue("ACTION"));
                switch(action) {
                    case ADD:
                        result = createRegulator(row);
                        break;
                    case UPDATE:
                        result = updateRegulator(row);
                        break;
                    case REMOVE:
                        result = removeRegulator(row);
                        break;
                    default:
                        result = new CsvImportResult(action, CsvImportResultType.INVALID_IMPORT_ACTION, row.getValue("NAME"));
                        break;
                }
            }
            results.add(result);
        }
        return results;
    }
    
    private CsvImportResult createRegulator(ImportRow row) {
        String name = row.getValue("NAME");
        YukonPao pao = retrieveRegulatorPao(name);
        if(pao != null) {
            //Error - device already exists with this name
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.OBJECT_EXISTS, name);
        }
        
        CompleteRegulator regulator = new CompleteRegulator();
        regulator.setPaoName(name);
        if(row.hasValue("DESCRIPTION")) {
            regulator.setDescription(row.getValue("DESCRIPTION"));
        }
        if(row.hasValue("DISABLED")) {
            regulator.setDisabled(Boolean.valueOf(row.getValue("DISABLED")));
        }
        if(row.hasValue("KEEP ALIVE TIMER")) {
            regulator.setKeepAliveTimer(Integer.valueOf(row.getValue("KEEP ALIVE TIMER")));
        }
        if(row.hasValue("KEEP ALIVE CONFIG")) {
            regulator.setKeepAliveConfig(Integer.valueOf(row.getValue("KEEP ALIVE CONFIG")));
        }
        
        RegulatorType type = RegulatorType.valueOf(row.getValue("TYPE"));
        paoPersistenceService.createPao(regulator, type.getPaoType());
        return new CsvImportResult(ImportAction.ADD, CsvImportResultType.SUCCESS, name);
    }
    
    private CsvImportResult updateRegulator(ImportRow row) {
        String name = row.getValue("NAME");
        YukonPao pao = retrieveRegulatorPao(name);
        if(pao == null) {
            //Error - no existing device with this name
            return new CsvImportResult(ImportAction.UPDATE, CsvImportResultType.NO_SUCH_OBJECT, name);
        }
        
        CompleteRegulator regulator = paoPersistenceService.retreivePao(pao.getPaoIdentifier(), CompleteRegulator.class);
        if(row.hasValue("DESCRIPTION")) {
            regulator.setDescription(row.getValue("DESCRIPTION"));
        }
        if(row.hasValue("DISABLED")) {
            regulator.setDisabled(Boolean.valueOf(row.getValue("DISABLED")));
        }
        if(row.hasValue("KEEP ALIVE TIMER")) {
            regulator.setKeepAliveTimer(Integer.valueOf(row.getValue("KEEP ALIVE TIMER")));
        }
        if(row.hasValue("KEEP ALIVE CONFIG")) {
            regulator.setKeepAliveConfig(Integer.valueOf(row.getValue("KEEP ALIVE CONFIG")));
        }
        
        paoPersistenceService.updatePao(regulator);
        return new CsvImportResult(ImportAction.UPDATE, CsvImportResultType.SUCCESS, name);
    }
    
    private CsvImportResult removeRegulator(ImportRow row) {
        String name = row.getValue("NAME");
        YukonPao pao = retrieveRegulatorPao(name);
        if(pao == null) {
            //Error - no existing device with this name
            return new CsvImportResult(ImportAction.REMOVE, CsvImportResultType.NO_SUCH_OBJECT, name);
        }
        if(!regulatorDao.isOrphan(pao.getPaoIdentifier().getPaoId())) {
            //Error - only orphan regulator can be deleted
            return new CsvImportResult(ImportAction.REMOVE, CsvImportResultType.HAS_DEPENDENCIES, name);
        }
        
        paoPersistenceService.deletePao(pao.getPaoIdentifier());
        return new CsvImportResult(ImportAction.REMOVE, CsvImportResultType.SUCCESS, name);
    }
    
    //Takes a validation result and returns a representative import result
    private CsvImportResult processValidationResult(ImportValidationResult validationResult) {
        String[] invalidColumns = validationResult.getInvalidColumns().toArray(new String[1]);
        if(validationResult.isBadValue()) {
            return new CsvImportResult(CsvImportResultType.BAD_DATA, invalidColumns);
        } else if(validationResult.isMissingValue()) {
            return new CsvImportResult(CsvImportResultType.MISSING_DATA, invalidColumns);
        } else {
            throw new IllegalArgumentException("Unable to process validation result with type " + validationResult.getType());
        }
    }
    
    //Returns the pao for the regulator with this name, or null if no such regulator exists
    private YukonPao retrieveRegulatorPao(String name) {
        return paoDao.findYukonPao(name, PaoCategory.CAPCONTROL, PaoClass.CAPCONTROL);
    }
}

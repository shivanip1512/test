package com.cannontech.capcontrol.creation.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.creation.service.RegulatorPointMappingImportService;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.common.csvImport.CsvImportResult;
import com.cannontech.common.csvImport.CsvImportResultType;
import com.cannontech.common.csvImport.FailedImportResult;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportFileValidator;
import com.cannontech.common.csvImport.ImportResult;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.ImportValidationResult;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.enums.RegulatorPointMapping;
import com.google.common.collect.Lists;

public class RegulatorPointMappingImportServiceImpl implements RegulatorPointMappingImportService {
    private static ImportFileFormat importFormat;
    
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private RegulatorImportHelper regulatorImportHelper;
    
    //This block specifies the format for regulator point mapping import files
    static {
        importFormat = new ImportFileFormat();
        //REQUIRED
        importFormat.addRequiredColumn("ACTION", ImportAction.class, false);
        importFormat.addRequiredColumn("REGULATOR NAME", String.class, false);
        importFormat.addRequiredColumn("MAPPING", RegulatorPointMapping.class, false);
        //VALUE-DEPENDENT
        importFormat.addValueDependentColumn("DEVICE TYPE", PaoType.class, false, "ACTION", ImportAction.ADD, ImportAction.UPDATE);
        importFormat.addValueDependentColumn("DEVICE NAME", String.class, false, "ACTION", ImportAction.ADD, ImportAction.UPDATE);
        importFormat.addValueDependentColumn("POINT NAME", String.class, false, "ACTION", ImportAction.ADD, ImportAction.UPDATE);
    }
    
    /**
     * Retrieves the format for regulator import files.
     */
    public ImportFileFormat getFormat() {
        return importFormat;
    }
    
    /**
     * Runs an import of the supplied data. Each row is validated, then imported if there are
     * no validation errors.
     */
    public List<ImportResult> startImport(ImportData importData) {
        List<ImportResult> results = Lists.newArrayList();
        
        List<ImportRow> rows = importData.getRows();
        for(ImportRow row : rows) {
            ImportResult result = null;
            
            ImportValidationResult validationResult = ImportFileValidator.validateRow(row, importFormat);
            if(validationResult.isFailed()) {
                result = regulatorImportHelper.processValidationResult(validationResult);
            } else {
                ImportAction action = ImportAction.valueOf(row.getValue("ACTION"));
                String regulatorName = row.getValue("REGULATOR NAME");
                YukonPao regulatorPao = regulatorImportHelper.retrieveRegulatorPao(regulatorName);
                if(regulatorPao == null) {
                    //Error - no existing device with this name
                    result = new CsvImportResult(action, CsvImportResultType.NO_SUCH_OBJECT, regulatorName);
                } else {
                    switch(action) {
                        case ADD:
                            result = createPointMapping(row, regulatorPao);
                            break;
                        case UPDATE:
                            result = updatePointMapping(row, regulatorPao);
                            break;
                        case REMOVE:
                            result = removePointMapping(row, regulatorPao, regulatorName);
                            break;
                        default:
                            result = new CsvImportResult(action, CsvImportResultType.INVALID_IMPORT_ACTION, row.getValue("NAME"));
                            break;
                    }
                }
            }
            results.add(result);
        }
        return results;
    }
    
    private ImportResult createPointMapping(ImportRow row, YukonPao regulatorPao) {
        //check that the mapping is allowed for regulator type
        RegulatorPointMapping mapping = RegulatorPointMapping.valueOf(row.getValue("MAPPING"));
        PaoType regulatorType = regulatorPao.getPaoIdentifier().getPaoType();
        Set<RegulatorPointMapping> validMappings = RegulatorPointMapping.getPointMappingsForPaoType(regulatorType);
        if(!validMappings.contains(mapping)) {
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.CONDITIONAL_BAD_DATA, mapping.toString(), regulatorType.getDbString());
        }
        
        //Get the point to map
        PaoType paoType = PaoType.valueOf(row.getValue("DEVICE TYPE"));
        String deviceName = row.getValue("DEVICE NAME");
        YukonPao pointPao = paoDao.findYukonPao(deviceName, paoType);
        if(pointPao == null) {
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.NO_SUCH_OBJECT, deviceName);
        }
        String pointName = row.getValue("POINT NAME");
        LitePoint point = pointDao.findPointByName(pointPao, pointName);
        if(point == null) {
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.NO_SUCH_OBJECT, row.getValue("POINT NAME"));
        }
        
        //check that the point is an appropriate type
        PointType pointType = point.getPointTypeEnum();
        PointType mappingPointType = mapping.getPointType();
        if(pointType != mappingPointType) {
            return new FailedImportResult("yukon.web.modules.capcontrol.import.regulatorMappingBadPointType", ImportAction.ADD.toString(), pointType.toString(), mapping.toString());
        }
        
        //Add the point mapping
        try {
            insertPointMapping(mapping, regulatorPao, point.getPointID(), false);
        } catch(IllegalStateException e) {
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.OBJECT_EXISTS, mapping.toString());
        }
        return new CsvImportResult(ImportAction.ADD, CsvImportResultType.SUCCESS, mapping.toString());
    }
    
    private ImportResult updatePointMapping(ImportRow row, YukonPao regulatorPao) {
        //check that the mapping is allowed for regulator type
        RegulatorPointMapping mapping = RegulatorPointMapping.valueOf(row.getValue("MAPPING"));
        PaoType regulatorType = regulatorPao.getPaoIdentifier().getPaoType();
        Set<RegulatorPointMapping> validMappings = RegulatorPointMapping.getPointMappingsForPaoType(regulatorType);
        if(!validMappings.contains(mapping)) {
            return new CsvImportResult(ImportAction.UPDATE, CsvImportResultType.CONDITIONAL_BAD_DATA, mapping.toString(), regulatorType.getDbString());
        }
        
        //Get the point to map
        PaoType paoType = PaoType.valueOf(row.getValue("DEVICE TYPE"));
        String deviceName = row.getValue("DEVICE NAME");
        YukonPao pointPao = paoDao.findYukonPao(deviceName, paoType);
        if(pointPao == null) {
            return new CsvImportResult(ImportAction.UPDATE, CsvImportResultType.NO_SUCH_OBJECT, deviceName);
        }
        String pointName = row.getValue("POINT NAME");
        LitePoint point = pointDao.findPointByName(pointPao, pointName);
        if(point == null) {
            return new CsvImportResult(ImportAction.UPDATE, CsvImportResultType.NO_SUCH_OBJECT, row.getValue("POINT NAME"));
        }
        
        //check that the point is an appropriate type
        PointType pointType = point.getPointTypeEnum();
        PointType mappingPointType = mapping.getPointType();
        if(pointType != mappingPointType) {
            //A failed - Point type X is not valid for Mapping Y
            return new FailedImportResult("yukon.web.modules.capcontrol.import.regulatorMappingBadPointType", ImportAction.UPDATE.toString(), pointType.toString(), mapping.toString());
        }
        
        //Add the point mapping
        insertPointMapping(mapping, regulatorPao, point.getPointID(), true);
        return new CsvImportResult(ImportAction.UPDATE, CsvImportResultType.SUCCESS, mapping.toString());
    }
    
    private ImportResult removePointMapping(ImportRow row, YukonPao regulatorPao, String regulatorName) {
        RegulatorPointMapping mapping = RegulatorPointMapping.valueOf(row.getValue("MAPPING"));
        boolean removedSuccessfully = extraPaoPointAssignmentDao.removeAssignment(regulatorPao.getPaoIdentifier(), mapping);
        if(removedSuccessfully) {
            return new CsvImportResult(ImportAction.REMOVE, CsvImportResultType.SUCCESS, mapping.toString());
        } else {
            return new CsvImportResult(ImportAction.REMOVE, CsvImportResultType.NO_SUCH_OBJECT, mapping.toString());
        }
    }
    
    //does the actual insert, and also updates ccMonitorBankList, if appropriate
    private void insertPointMapping(RegulatorPointMapping mapping, YukonPao regulatorPao, int pointId, boolean overwriteExistingPoint) {
        extraPaoPointAssignmentDao.addAssignment(regulatorPao, pointId, mapping, overwriteExistingPoint);
        //If mapping is voltage y, update ccmonitorbanklist
        if(mapping == RegulatorPointMapping.VOLTAGE_Y) {
            int regulatorId = regulatorPao.getPaoIdentifier().getPaoId();
            ccMonitorBankListDao.deleteNonMatchingRegulatorPoint(regulatorId, pointId);
            ccMonitorBankListDao.addRegulatorPoint(regulatorId);
        }
    }
}

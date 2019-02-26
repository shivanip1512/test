package com.cannontech.common.bulk.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.model.ImportCalculationFunction;
import com.cannontech.common.bulk.model.ImportCalculationOperation;
import com.cannontech.common.bulk.model.ImportCalculationType;
import com.cannontech.common.bulk.service.CalculationImportService;
import com.cannontech.common.csvImport.CsvImportResultType;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportFileValidator;
import com.cannontech.common.csvImport.ImportParser;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.ImportValidationResult;
import com.cannontech.common.exception.DuplicateColumnNameException;
import com.cannontech.common.exception.InvalidColumnNameException;
import com.cannontech.common.exception.RequiredColumnMissingException;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.point.PointCalculation;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.Maps;
import com.opencsv.CSVReader;

public class CalculationImportServiceImpl implements CalculationImportService {
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    
    private static ImportFileFormat CALCULATION_FORMAT;
    
    static {
        //Required columns
        CALCULATION_FORMAT = new ImportFileFormat();
        CALCULATION_FORMAT.addRequiredColumn("CALCULATION NAME", String.class);
        CALCULATION_FORMAT.addRequiredColumn("CALCULATION TYPE", ImportCalculationType.class);
        //Value-dependent columns
        CALCULATION_FORMAT.addValueDependentColumn("DEVICE NAME", String.class, "CALCULATION TYPE", ImportCalculationType.FUNCTION, ImportCalculationType.OPERATION);
        CALCULATION_FORMAT.addValueDependentColumn("DEVICE TYPE", ImportPaoType.class, "CALCULATION TYPE", ImportCalculationType.FUNCTION, ImportCalculationType.OPERATION);
        CALCULATION_FORMAT.addValueDependentColumn("POINT NAME", String.class, "CALCULATION TYPE", ImportCalculationType.FUNCTION, ImportCalculationType.OPERATION);
        CALCULATION_FORMAT.addValueDependentColumn("OPERATION", ImportCalculationOperation.class, "CALCULATION TYPE", ImportCalculationType.CONSTANT, ImportCalculationType.OPERATION);
        CALCULATION_FORMAT.addValueDependentColumn("CONSTANT", Double.class, "CALCULATION TYPE", ImportCalculationType.CONSTANT);
        CALCULATION_FORMAT.addValueDependentColumn("FUNCTION", ImportCalculationFunction.class, "CALCULATION TYPE", ImportCalculationType.FUNCTION);
        //descriptions
        CALCULATION_FORMAT.setDescriptionKey("CALCULATION NAME", "yukon.web.modules.tools.bulk.pointImport.column.calculationName");
        CALCULATION_FORMAT.setDescriptionKey("CALCULATION TYPE", "yukon.web.modules.tools.bulk.pointImport.column.calculationType");
        CALCULATION_FORMAT.setDescriptionKey("DEVICE NAME", "yukon.web.modules.tools.bulk.pointImport.column.calculationDeviceName");
        CALCULATION_FORMAT.setDescriptionKey("DEVICE TYPE", "yukon.web.modules.tools.bulk.pointImport.column.calculationDeviceType");
        CALCULATION_FORMAT.setValidValuesKey("DEVICE TYPE", "yukon.web.modules.tools.bulk.pointImport.validValues.deviceType");
        CALCULATION_FORMAT.setDescriptionKey("POINT NAME", "yukon.web.modules.tools.bulk.pointImport.column.calculationPointName");
        CALCULATION_FORMAT.setDescriptionKey("OPERATION", "yukon.web.modules.tools.bulk.pointImport.column.operation");
        CALCULATION_FORMAT.setDescriptionKey("CONSTANT", "yukon.web.modules.tools.bulk.pointImport.column.constant");
        CALCULATION_FORMAT.setDescriptionKey("FUNCTION", "yukon.web.modules.tools.bulk.pointImport.column.function");
    }
    
    @Override
    public ImportFileFormat getFormat() {
        return CALCULATION_FORMAT;
    }
    
    @Override
    public Map<String, PointCalculation> parseCalcFile(CSVReader csvReader, List<String> results, boolean ignoreInvalidColumns) throws IOException {
        CALCULATION_FORMAT.setIgnoreInvalidHeaders(ignoreInvalidColumns);
        ImportParser calculationParser = new ImportParser(CALCULATION_FORMAT);
        
        ImportData data = calculationParser.parseFromCsvReader(csvReader);
        csvReader.close();
        
        //first validate file structure and deal with errors
        try {
            ImportFileValidator.validateFileStructure(data);
        } catch(DuplicateColumnNameException e) {
            results.add("yukon.web.modules.tools.bulk.pointImport.error.duplicateColumns");
            results.add(e.getJoinedDuplicateColumnNames());
            return null;
        } catch(InvalidColumnNameException e) {
            results.add("yukon.web.modules.tools.bulk.pointImport.error.invalidColumns");
            results.add(e.getJoinedInvalidColumnNames());
            return null;
        } catch(RequiredColumnMissingException e) {
            results.add("yukon.web.modules.tools.bulk.pointImport.error.missingRequiredColumn");
            results.add(e.getJoinedMissingColumnNames());
            return null;
        }
        
        //next validate individual rows and deal with errors
        boolean rowValidationFailed = false;
        List<ImportValidationResult> rowValidationResults = ImportFileValidator.validateRows(data);
        for(ImportValidationResult validationResult : rowValidationResults) {
            if(validationResult.isFailed()) {
                rowValidationFailed = true;
                Collection<String> invalidColumns = validationResult.getInvalidColumns();
                if(validationResult.isBadValue()) {
                    results.add(CsvImportResultType.BAD_DATA.getFormatKey());
                    results.addAll(invalidColumns);
                } else if(validationResult.isMissingValue()) {
                    results.add(CsvImportResultType.MISSING_DATA.getFormatKey());
                    results.addAll(invalidColumns);
                } else {
                    throw new IllegalArgumentException("Unable to process validation result with type " + validationResult.getType());
                }
                
            }
        }
        if(rowValidationFailed) {
            return null;
        }
        
        Map<String, PointCalculation> calculationMap = Maps.newHashMap();
        
        Integer lineNum = 1;
        for(ImportRow row : data.getRows()) {
            PointCalculation calculation;
            
            String name = row.getValue("CALCULATION NAME");
            if(calculationMap.containsKey(name)) {
                calculation = calculationMap.get(name);
            } else {
                calculation = new PointCalculation();
            }
            
            ImportCalculationType type = ImportCalculationType.valueOf(row.getValue("CALCULATION TYPE"));
            switch(type) {
                case OPERATION:
                    LitePoint point = findPointFromRow(row, results, lineNum);
                    if(point == null) return null;
                    ImportCalculationOperation operation = ImportCalculationOperation.valueOf(row.getValue("OPERATION"));
                    calculation.addOperation(point.getPointID(), operation.getDatabaseRepresentation());
                    break;
                case CONSTANT:
                    ImportCalculationOperation constantOperation = ImportCalculationOperation.valueOf(row.getValue("OPERATION"));
                    Double constant = Double.valueOf(row.getValue("CONSTANT"));
                    calculation.addConstant(constantOperation.getDatabaseRepresentation(), constant);
                    break;
                case FUNCTION:
                    LitePoint functionPoint = findPointFromRow(row, results, lineNum);
                    if(functionPoint == null) return null;
                    ImportCalculationFunction function = ImportCalculationFunction.valueOf(row.getValue("FUNCTION"));
                    calculation.addFunction(functionPoint.getPointID(), function.getDatabaseRepresentation());
                    break;
                default:
                    results.add("yukon.web.modules.tools.bulk.pointImport.error.invalidCalculationType");
                    results.add(type.toString());
                    return null;
            }
            calculationMap.put(name, calculation);
            lineNum++;
        }
        
        return calculationMap;
    }
    
    //gets the device from the device name and type in the row, then gets the point with the name
    //specified in the row, on the retrieved device. If there's a failure along the way, the i18n
    //key and parameters are added to the results object.
    private LitePoint findPointFromRow(ImportRow row, List<String> results, Integer lineNum) {
        String deviceName = row.getValue("DEVICE NAME");
        PaoType paoType = ImportPaoType.valueOf(row.getValue("DEVICE TYPE"));
        YukonPao pao = paoDao.findYukonPao(deviceName, paoType);
        if(pao == null) {
            results.add("yukon.web.modules.tools.bulk.pointImport.error.invalidDeviceName");
            results.add(deviceName);
            results.add(lineNum.toString());
            return null;
        }
        
        String pointName = row.getValue("POINT NAME");
        LitePoint point = pointDao.findPointByName(pao, pointName);
        if(point == null) {
            results.add("yukon.web.modules.tools.bulk.pointImport.error.invalidPointName");
            results.add(pointName);
            results.add(deviceName);
            results.add(lineNum.toString());
            return null;
        }
        return point;
    }
}

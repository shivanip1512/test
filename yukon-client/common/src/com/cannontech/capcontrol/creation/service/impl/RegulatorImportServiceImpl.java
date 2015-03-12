package com.cannontech.capcontrol.creation.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.creation.RegulatorImportField;
import com.cannontech.capcontrol.creation.service.RegulatorImportService;
import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.common.csvImport.CsvImportResult;
import com.cannontech.common.csvImport.CsvImportResultType;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportFileValidator;
import com.cannontech.common.csvImport.ImportResult;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.ImportValidationResult;
import com.cannontech.common.csvImport.types.RegulatorType;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteRegulator;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.Lists;

public class RegulatorImportServiceImpl implements RegulatorImportService {
    private static ImportFileFormat importFormat;
    
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private VoltageRegulatorDao regulatorDao;
    @Autowired private RegulatorImportHelper regulatorImportHelper;
    
    //This block specifies the format for voltage regulator import files
    static {
        importFormat = new ImportFileFormat();
        for(RegulatorImportField format : RegulatorImportField.values()){
            switch(format.getInputType()){
            case REQUIRED:
                importFormat.addRequiredColumn(format.getName(),
                                               format.getTypeClass(),
                                               format.isNullable(),
                                               format.isUppercaseValue());
                break;
            case VALUE_DEPENDENT:
                importFormat.addValueDependentColumn(format.getName(),
                                                     format.getTypeClass(),
                                                     format.isNullable(),
                                                     format.isUppercaseValue(),
                                                     format.getDependedColumnName(),
                                                     format.getDependedColumnValues());
                break;
            case OPTIONAL:
                importFormat.addOptionalColumn(format.getName(),
                                               format.getTypeClass(),
                                               format.isNullable(),
                                               format.isUppercaseValue());
                break;
            }
        }
    }
    
    /**
     * Retrieves the format for regulator import files.
     */
    @Override
    public ImportFileFormat getFormat() {
        return importFormat;
    }
    
    /**
     * Runs an import of the supplied import data. Each row is validated, then imported if there are
     * no validation errors.
     */
    @Override
    public List<ImportResult> startImport(ImportData importData) {
        List<ImportResult> results = Lists.newArrayList();
        
        List<ImportRow> rows = importData.getRows();
        for(ImportRow row : rows) {
            CsvImportResult result = null;
            
            ImportValidationResult validationResult = ImportFileValidator.validateRow(row, importData.getFormat());
            if(validationResult.isFailed()) {
                result = regulatorImportHelper.processValidationResult(validationResult);
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
        YukonPao pao = regulatorImportHelper.findRegulatorPao(name);
        if(pao != null) {
            //Error - device already exists with this name
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.OBJECT_EXISTS, name);
        }
        
        CompleteRegulator regulator = new CompleteRegulator();
        if (!(PaoUtils.isValidPaoName(name))) {
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.INVALID_CHARS, name);
            
        }
        regulator.setPaoName(name);
        setOptionalColumns(regulator, row);

        RegulatorType type = RegulatorType.valueOf(row.getValue("TYPE"));
        paoPersistenceService.createPaoWithDefaultPoints(regulator, type.getPaoType());

        String configName = row.getValueMap().get(RegulatorImportField.CONFIGURATION.name());

        ConfigStatus attchmentStatus = attachConfig(configName, regulator, ImportAction.ADD);

        switch (attchmentStatus) {
        case OK:
        case BLANK:
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.SUCCESS, name);
        case NO_SUCH_CONFIG:
        case UNSUPPORTED_CONFIG:
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.INVALID_CONFIG_ADD, name, configName);

        }
        return new CsvImportResult(ImportAction.ADD, CsvImportResultType.SUCCESS, name);

    }

    private enum ConfigStatus {OK, BLANK, NO_SUCH_CONFIG, UNSUPPORTED_CONFIG}

    private ConfigStatus attachConfig(String configName, CompleteRegulator regulator, ImportAction action) {

        SimpleDevice device = SimpleDevice.of(regulator.getPaoIdentifier());

        ConfigStatus result = ConfigStatus.OK;
        LightDeviceConfiguration config;
        DeviceConfiguration defaultConfig = deviceConfigurationDao.getDefaultRegulatorConfiguration();

        if (StringUtils.isBlank(configName)) {
            //For a blank row in an update, do no action.
            //For a blank row in an add, use the default config.
            result = ConfigStatus.BLANK;
            if (action == ImportAction.UPDATE) return result;

            config = defaultConfig;

        } else {

            try {
                config = deviceConfigurationDao.getLightDeviceConfigurationByName(configName);
            } catch (NotFoundException e) {
                //No conifg with that name found
                result = ConfigStatus.NO_SUCH_CONFIG;
                if (action == ImportAction.UPDATE) return result;

                config = defaultConfig;
            }

            if (!deviceConfigurationDao.isTypeSupportedByConfiguration(config, device.getDeviceType())) {

                result = ConfigStatus.UNSUPPORTED_CONFIG;
                if (action == ImportAction.UPDATE) return result;

                config = defaultConfig;
            }
        }


        try {
            deviceConfigurationDao.assignConfigToDevice(config, device);
            return result;
        } catch (InvalidDeviceTypeException e) {
            if (action == ImportAction.UPDATE) {
                return ConfigStatus.UNSUPPORTED_CONFIG;
            }

            try {
                deviceConfigurationDao.assignConfigToDevice(defaultConfig, device);
            } catch (InvalidDeviceTypeException e1) {
                //The default config must be supported. Something is very wrong.
                throw new RuntimeException(e1);
            }

            return ConfigStatus.UNSUPPORTED_CONFIG;
        }
    }
    
    private CsvImportResult updateRegulator(ImportRow row) {
        String name = row.getValue("NAME");
        YukonPao pao = regulatorImportHelper.findRegulatorPao(name);
        if(pao == null) {
            //Error - no existing device with this name
            return new CsvImportResult(ImportAction.UPDATE, CsvImportResultType.NO_SUCH_OBJECT, name);
        }
        
        CompleteRegulator regulator = paoPersistenceService.retreivePao(pao.getPaoIdentifier(), CompleteRegulator.class);
        setOptionalColumns(regulator, row);

        paoPersistenceService.updatePao(regulator);
        
        String configName = row.getValueMap().get(RegulatorImportField.CONFIGURATION.name());
        
        ConfigStatus attchmentStatus = attachConfig(configName, regulator, ImportAction.UPDATE);

        switch (attchmentStatus) {
        case OK:
        case BLANK:
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.SUCCESS, name);
        case NO_SUCH_CONFIG:
        case UNSUPPORTED_CONFIG:
            return new CsvImportResult(ImportAction.ADD, CsvImportResultType.INVALID_CONFIG_UPDATE, name, configName);

        }
        return new CsvImportResult(ImportAction.ADD, CsvImportResultType.SUCCESS, name);
    }
    
    private CsvImportResult removeRegulator(ImportRow row) {
        String name = row.getValue("NAME");
        YukonPao pao = regulatorImportHelper.findRegulatorPao(name);
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

    private void setOptionalColumns(CompleteRegulator regulator, ImportRow row){
        for(String field : RegulatorImportField.getOptionalFieldNames()){
            if(row.hasValue(field)){
                String value = row.getValue(field);
                switch(RegulatorImportField.getByFieldName(field)){
                case DESCRIPTION:
                    regulator.setDescription(value);
                    break;
                case DISABLED:
                    regulator.setDisabled(Boolean.valueOf(value));
                    break;
                case KEEP_ALIVE_CONFIG:
                    regulator.setKeepAliveConfig(Integer.valueOf(value));
                    break;
                case KEEP_ALIVE_TIMER:
                    regulator.setKeepAliveTimer(Integer.valueOf(value));
                    break;
                case VOLT_CHANGE_PER_TAP:
                    regulator.setVoltChangePerTap(Double.valueOf(value));
                    break;
                default:
                    break;
                }
            }
        }
    }
}

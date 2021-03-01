package com.cannontech.common.bulk.processor;

import java.util.Map;

import com.cannontech.common.bulk.model.AnalogPointUpdateType;
import com.cannontech.common.bulk.model.PointPeriodicRate;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.types.StrictBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.common.point.CalcAnalogPointBuilder;
import com.cannontech.common.point.PointCalculation;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointType;

import static com.cannontech.common.bulk.model.PointImportParameters.*;

public class CalcAnalogPointImportProcessor extends ScalarPointImportProcessor {
private Map<String, PointCalculation> calcMap;
    
    public CalcAnalogPointImportProcessor(ImportFileFormat format, Map<String, PointCalculation> calcMap, 
                                          MessageSourceAccessor messageSourceAccessor, PaoDao paoDao, 
                                          PointDao pointDao, DBPersistentDao dbPersistentDao,
                                          PointBuilderFactory pointBuilderFactory) {
        
        super(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
        this.calcMap = calcMap;
    }
    
    @Override
    protected void createPoint(ImportRow row) {
        String deviceName = row.getValue(DEVICE_NAME.NAME);
        PaoType paoType = ImportPaoType.valueOf(row.getValue(DEVICE_TYPE.NAME));
        String pointName = row.getValue(POINT_NAME.NAME);
        int paoId = validatePaoAndPoint(deviceName, paoType, pointName);
        Boolean isDisabled = StrictBoolean.valueOf(row.getValue(DISABLED.NAME));
        
        CalcAnalogPointBuilder builder = pointBuilderFactory.getCalcAnalogPointBuilder(paoId, pointDao.getNextPointId(), pointName, isDisabled);
        
        setPointOffset(builder, row, paoId, deviceName);
        
        String calculationId = row.getValue(CALCULATION.NAME);
        PointCalculation calculation = calcMap.get(calculationId);
        if(calculation == null) {
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.invalidCalculation");
            throw new ProcessingException(error, "invalidCalculation");
        }
        builder.setCalculation(calculation);
        
        AnalogPointUpdateType updateType = AnalogPointUpdateType.valueOf(row.getValue(ANALOG_UPDATE_TYPE.NAME));
        builder.setUpdateType(updateType);
        if(updateType.hasRate()) {
            builder.setUpdateRate(PointPeriodicRate.valueOf(row.getValue(UPDATE_RATE.NAME)));
        }
        
        doSharedProcessing(builder, row);
        
        boolean forceQualityNormal = StrictBoolean.valueOf(row.getValue(FORCE_QUALITY_NORMAL.NAME));
        builder.setForceQualityNormal(forceQualityNormal);
        
        builder.insert();
    }
    
    @Override
    protected PointType getPointType(ImportRow row) {
        return PointType.CalcAnalog;
    }
}

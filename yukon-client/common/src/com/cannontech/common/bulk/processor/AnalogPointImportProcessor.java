package com.cannontech.common.bulk.processor;

import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.types.StrictBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.common.point.AnalogPointBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointTypes;

import static com.cannontech.common.bulk.model.PointImportParameters.*;

public class AnalogPointImportProcessor extends ScalarPointImportProcessor {
    
    public AnalogPointImportProcessor(ImportFileFormat format, MessageSourceAccessor messageSourceAccessor, 
                                      PaoDao paoDao, PointDao pointDao, DBPersistentDao dbPersistentDao,
                                      PointBuilderFactory pointBuilderFactory) {
        
        super(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    protected void createPoint(ImportRow row) {
        String deviceName = row.getValue(DEVICE_NAME.NAME);
        PaoType paoType = ImportPaoType.valueOf(row.getValue(DEVICE_TYPE.NAME));
        String pointName = row.getValue(POINT_NAME.NAME);
        int paoId = validatePaoAndPoint(deviceName, paoType, pointName);
        Boolean isDisabled = StrictBoolean.valueOf(row.getValue(DISABLED.NAME));
        
        AnalogPointBuilder builder = pointBuilderFactory.getAnalogPointBuilder(paoId, pointDao.getNextPointId(), pointName, isDisabled);
        
        if(row.hasValue(POINT_OFFSET.NAME)) {
            int pointOffset = Integer.valueOf(row.getValue(POINT_OFFSET.NAME));
            
            if(pointDao.deviceHasPoint(paoId, pointOffset, PointTypes.ANALOG_POINT)) {
                String error = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.pointImport.error.pointOffsetInUse", pointOffset, deviceName);
                throw new ProcessingException(error);
            }
            
            builder.setPointOffset(pointOffset);
        }
        
        if(row.hasValue(DEADBAND.NAME)) {
            double deadband = Double.valueOf(row.getValue(DEADBAND.NAME));
            builder.setDeadband(deadband);
        }
        
        doSharedProcessing(builder, row);
    
        double multiplier = Double.valueOf(row.getValue(MULTIPLIER.NAME));
        builder.setMultiplier(multiplier);
    
        double dataOffset = Double.valueOf(row.getValue(DATA_OFFSET.NAME));
        builder.setDataOffset(dataOffset);
    
        int meterDials = Integer.valueOf(row.getValue(METER_DIALS.NAME));
        builder.setMeterDials(meterDials);
        
        builder.insert();
    }
}

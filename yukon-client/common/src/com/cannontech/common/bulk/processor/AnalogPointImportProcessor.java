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
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointTypes;

public class AnalogPointImportProcessor extends ScalarPointImportProcessor {
    
    public AnalogPointImportProcessor(ImportFileFormat format, MessageSourceAccessor messageSourceAccessor, 
                                      PaoDao paoDao, PointDao pointDao, DBPersistentDao dbPersistentDao,
                                      PointBuilderFactory pointBuilderFactory) {
        
        super(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    protected void createPoint(ImportRow row) {
        String deviceName = row.getValue("DEVICE NAME");
        PaoType paoType = ImportPaoType.valueOf(row.getValue("DEVICE TYPE"));
        String pointName = row.getValue("POINT NAME");
        int paoId = validatePaoAndPoint(deviceName, paoType, pointName);
        Boolean isDisabled = StrictBoolean.valueOf(row.getValue("DISABLED"));
        
        AnalogPointBuilder builder = pointBuilderFactory.getAnalogPointBuilder(paoId, pointDao.getNextPointId(), pointName, isDisabled);
        
        if(row.hasValue("POINT OFFSET")) {
            int pointOffset = Integer.valueOf(row.getValue("POINT OFFSET"));
            
            if(pointDao.deviceHasPoint(paoId, pointOffset, PointTypes.ANALOG_POINT)) {
                String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.pointImport.error.pointOffsetInUse", pointOffset, deviceName);
                throw new ProcessingException(error);
            }
            
            builder.setPointOffset(pointOffset);
        }
        
        if(row.hasValue("DEADBAND")) {
            double deadband = Double.valueOf(row.getValue("DEADBAND"));
            builder.setDeadband(deadband);
        }
        
        doSharedProcessing(builder, row);
    
        double multiplier = Double.valueOf(row.getValue("MULTIPLIER"));
        builder.setMultiplier(multiplier);
    
        double dataOffset = Double.valueOf(row.getValue("DATA OFFSET"));
        builder.setDataOffset(dataOffset);
    
        int meterDials = Integer.valueOf(row.getValue("METER DIALS"));
        builder.setMeterDials(meterDials);
        
        builder.insert();
    }
}

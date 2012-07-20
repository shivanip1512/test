package com.cannontech.common.bulk.processor;

import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.types.StrictBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.AccumulatorType;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.common.point.AccumulatorPointBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointTypes;

public class AccumulatorPointImportProcessor extends AnalogAccumulatorSharedProcessing {
    
    public AccumulatorPointImportProcessor(ImportFileFormat format, MessageSourceAccessor messageSourceAccessor,
                                           PaoDao paoDao, PointDao pointDao, DBPersistentDao dbPersistentDao,
                                           PointBuilderFactory pointBuilderFactory) {
        
        super(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    public void createPoint(ImportRow row) {
        String deviceName = row.getValue("DEVICE NAME");
        PaoType paoType = ImportPaoType.valueOf(row.getValue("DEVICE TYPE"));
        String pointName = row.getValue("POINT NAME");
        int paoId = validatePaoAndPoint(deviceName, paoType, pointName);
        Boolean isDisabled = StrictBoolean.valueOf(row.getValue("DISABLED"));
        AccumulatorType accumulatorType = AccumulatorType.valueOf(row.getValue("ACCUMULATOR TYPE"));
        
        AccumulatorPointBuilder builder = pointBuilderFactory.getAccumulatorPointBuilder(paoId, pointDao.getNextPointId(), pointName, isDisabled, accumulatorType);
        
        if(row.hasValue("POINT OFFSET")) {
            int pointOffset = Integer.valueOf(row.getValue("POINT OFFSET"));
            
            try {
                pointDao.getLitePointIdByDeviceId_Offset_PointType(paoId, pointOffset, PointTypes.ANALOG_POINT);
                String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.pointImport.error.pointOffsetInUse", pointOffset, deviceName);
                throw new ProcessingException(error);
            } catch(NotFoundException e) {
                //No point on device with this type and offset - import can continue
            }
            
            builder.pointOffset(pointOffset);
        }
        
        doSharedProcessing(builder, row);
    
        double multiplier = Double.valueOf(row.getValue("MULTIPLIER"));
        builder.multiplier(multiplier);
    
        double dataOffset = Double.valueOf(row.getValue("DATA OFFSET"));
        builder.dataOffset(dataOffset);
    
        int meterDials = Integer.valueOf(row.getValue("METER DIALS"));
        builder.meterDials(meterDials);
        
        builder.insert();
    }
}

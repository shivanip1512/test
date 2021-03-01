package com.cannontech.common.bulk.processor;

import com.cannontech.common.bulk.model.StaleDataUpdateStyle;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.ScalarPointBuilder;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.UnitOfMeasure;

import static com.cannontech.common.bulk.model.PointImportParameters.*;

public abstract class ScalarPointImportProcessor extends PointImportProcessor {
    
    public ScalarPointImportProcessor(ImportFileFormat format, 
                                             MessageSourceAccessor messageSourceAccessor, 
                                             PaoDao paoDao,
                                             PointDao pointDao,
                                             DBPersistentDao dbPersistentDao,
                                             PointBuilderFactory pointBuilderFactory) {
        
        super(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    public void doSharedProcessing(ScalarPointBuilder builder, ImportRow row) {
        
        if(row.hasValue(ARCHIVE_TYPE.NAME)) {
            PointArchiveType archiveType = PointArchiveType.valueOf(row.getValue(ARCHIVE_TYPE.NAME));
            builder.setArchiveType(archiveType);
            if(archiveType.isIntervalRequired()) {
                PointArchiveInterval archiveInterval = PointArchiveInterval.valueOf(row.getValue(ARCHIVE_INTERVAL.NAME));
                builder.setArchiveInterval(archiveInterval);
            }
        }
        
        if(row.hasValue(HIGH_REASONABILITY.NAME)) {
            double highReasonability = Double.valueOf(row.getValue(HIGH_REASONABILITY.NAME));
            builder.setHighReasonability(highReasonability);
        }
        
        if(row.hasValue(LOW_REASONABILITY.NAME)) {
            double lowReasonability = Double.valueOf(row.getValue(LOW_REASONABILITY.NAME));
            builder.setLowReasonability(lowReasonability);
        }
        
        if(row.hasValue(HIGH_LIMIT_1.NAME)) {
            double highLimit1 = Double.valueOf(row.getValue(HIGH_LIMIT_1.NAME));
            double lowLimit1 = Double.valueOf(row.getValue(LOW_LIMIT_1.NAME));
            int limitDuration1 = Integer.valueOf(row.getValue(LIMIT_DURATION_1.NAME));
            builder.setHighLimit1(highLimit1);
            builder.setLowLimit1(lowLimit1);
            builder.setLimitDuration1(limitDuration1);
        }
        
        if(row.hasValue(HIGH_LIMIT_2.NAME)) {
            double highLimit2 = Double.valueOf(row.getValue(HIGH_LIMIT_2.NAME));
            double lowLimit2 = Double.valueOf(row.getValue(LOW_LIMIT_2.NAME));
            int limitDuration2 = Integer.valueOf(row.getValue(LIMIT_DURATION_2.NAME));
            builder.setHighLimit2(highLimit2);
            builder.setLowLimit2(lowLimit2);
            builder.setLimitDuration2(limitDuration2);
        }
        
        if(row.hasValue(STALE_DATA_TIME.NAME)) {
            int staleDataTime = Integer.valueOf(row.getValue(STALE_DATA_TIME.NAME));
            int staleDataUpdate = StaleDataUpdateStyle.valueOf(row.getValue(STALE_DATA_UPDATE.NAME)).getIndex();
            builder.setStaleDataTime(staleDataTime);
            builder.setStaleDataUpdate(staleDataUpdate);
        }
        
        String unitOfMeasureString = row.getValue(UNIT_OF_MEASURE.NAME);
        int unitOfMeasure = UnitOfMeasure.valueOf(unitOfMeasureString).getId();
        builder.setUnitOfMeasure(unitOfMeasure);
    
        int decimalPlaces = Integer.valueOf(row.getValue(DECIMAL_PLACES.NAME));
        builder.setDecimalPlaces(decimalPlaces);
    }
    
    /**
     * Helper method to set pointOffset
     */
    protected void setPointOffset(ScalarPointBuilder builder, ImportRow row, int paoId, String deviceName) {
        if (row.hasValue(POINT_OFFSET.NAME)) {
            int pointOffset = Integer.valueOf(row.getValue(POINT_OFFSET.NAME));
            if (pointOffset > 0) {
                if(pointDao.deviceHasPoint(paoId, pointOffset, getPointType(row))) {
                    String error = messageSourceAccessor.getMessage("yukon.exception.processingException.pointOffsetInUse", pointOffset, deviceName);
                    throw new ProcessingException(error, "pointOffsetInUse");
                }
            }
            builder.setPointOffset(pointOffset);
        }
    }
}

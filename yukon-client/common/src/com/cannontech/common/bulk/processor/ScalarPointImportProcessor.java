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
import com.cannontech.database.data.point.UnitOfMeasure;;

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
        
        if(row.hasValue("ARCHIVE TYPE")) {
            PointArchiveType archiveType = PointArchiveType.valueOf(row.getValue("ARCHIVE TYPE"));
            builder.setArchiveType(archiveType);
            if(archiveType.isIntervalRequired()) {
                PointArchiveInterval archiveInterval = PointArchiveInterval.valueOf(row.getValue("ARCHIVE INTERVAL"));
                builder.setArchiveInterval(archiveInterval);
            }
        }
        
        if(row.hasValue("HIGH REASONABILITY")) {
            double highReasonability = Double.valueOf(row.getValue("HIGH REASONABILITY"));
            builder.setHighReasonability(highReasonability);
        }
        
        if(row.hasValue("LOW REASONABILITY")) {
            double lowReasonability = Double.valueOf(row.getValue("LOW REASONABILITY"));
            builder.setLowReasonability(lowReasonability);
        }
        
        if(row.hasValue("HIGH LIMIT 1")) {
            double highLimit1 = Double.valueOf(row.getValue("HIGH LIMIT 1"));
            double lowLimit1 = Double.valueOf(row.getValue("LOW LIMIT 1"));
            int limitDuration1 = Integer.valueOf(row.getValue("LIMIT DURATION 1"));
            builder.setHighLimit1(highLimit1);
            builder.setLowLimit1(lowLimit1);
            builder.setLimitDuration1(limitDuration1);
        }
        
        if(row.hasValue("HIGH LIMIT 2")) {
            double highLimit2 = Double.valueOf(row.getValue("HIGH LIMIT 2"));
            double lowLimit2 = Double.valueOf(row.getValue("LOW LIMIT 2"));
            int limitDuration2 = Integer.valueOf(row.getValue("LIMIT DURATION 2"));
            builder.setHighLimit2(highLimit2);
            builder.setLowLimit2(lowLimit2);
            builder.setLimitDuration2(limitDuration2);
        }
        
        if(row.hasValue("STALE DATA TIME")) {
            int staleDataTime = Integer.valueOf(row.getValue("STALE DATA TIME"));
            int staleDataUpdate = StaleDataUpdateStyle.valueOf(row.getValue("STALE DATA UPDATE")).getIndex();
            builder.setStaleDataTime(staleDataTime);
            builder.setStaleDataUpdate(staleDataUpdate);
        }
        
        int unitOfMeasure = UnitOfMeasure.valueOf(row.getValue("UNIT OF MEASURE")).getId();
        builder.setUnitOfMeasure(unitOfMeasure);
    
        int decimalPlaces = Integer.valueOf(row.getValue("DECIMAL PLACES"));
        builder.setDecimalPlaces(decimalPlaces);
    }
}

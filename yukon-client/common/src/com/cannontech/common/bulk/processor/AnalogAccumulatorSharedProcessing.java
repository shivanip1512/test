package com.cannontech.common.bulk.processor;

import com.cannontech.common.bulk.model.StaleDataUpdateStyle;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.AnalogAccumulatorPointBuilderBase;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.common.point.UnitOfMeasure;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;

public abstract class AnalogAccumulatorSharedProcessing extends PointImportProcessor {
    
    public AnalogAccumulatorSharedProcessing(ImportFileFormat format, 
                                             MessageSourceAccessor messageSourceAccessor, 
                                             PaoDao paoDao,
                                             PointDao pointDao,
                                             DBPersistentDao dbPersistentDao,
                                             PointBuilderFactory pointBuilderFactory) {
        
        super(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    public void doSharedProcessing(AnalogAccumulatorPointBuilderBase builder, ImportRow row) {
        
        if(row.hasValue("ARCHIVE TYPE")) {
            PointArchiveType archiveType = PointArchiveType.valueOf(row.getValue("ARCHIVE TYPE"));
            PointArchiveInterval archiveInterval = PointArchiveInterval.valueOf(row.getValue("ARCHIVE INTERVAL"));
            builder.archiveType(archiveType);
            builder.archiveInterval(archiveInterval);
        }
        
        if(row.hasValue("HIGH REASONABILITY")) {
            double highReasonability = Double.valueOf(row.getValue("HIGH REASONABILITY"));
            builder.highReasonability(highReasonability);
        }
        
        if(row.hasValue("LOW REASONABILITY")) {
            double lowReasonability = Double.valueOf(row.getValue("LOW REASONABILITY"));
            builder.lowReasonability(lowReasonability);
        }
        
        if(row.hasValue("HIGH LIMIT 1")) {
            double highLimit1 = Double.valueOf(row.getValue("HIGH LIMIT 1"));
            double lowLimit1 = Double.valueOf(row.getValue("LOW LIMIT 1"));
            int limitDuration1 = Integer.valueOf(row.getValue("LIMIT DURATION 1"));
            builder.highLimit1(highLimit1);
            builder.lowLimit1(lowLimit1);
            builder.limitDuration1(limitDuration1);
        }
        
        if(row.hasValue("HIGH LIMIT 2")) {
            double highLimit2 = Double.valueOf(row.getValue("HIGH LIMIT 2"));
            double lowLimit2 = Double.valueOf(row.getValue("LOW LIMIT 2"));
            int limitDuration2 = Integer.valueOf(row.getValue("LIMIT DURATION 2"));
            builder.highLimit2(highLimit2);
            builder.lowLimit2(lowLimit2);
            builder.limitDuration2(limitDuration2);
        }
        
        if(row.hasValue("STALE DATA TIME")) {
            int staleDataTime = Integer.valueOf(row.getValue("STALE DATA TIME"));
            int staleDataUpdate = StaleDataUpdateStyle.valueOf(row.getValue("STALE DATA UPDATE")).getIndex();
            builder.staleDataTime(staleDataTime);
            builder.staleDataUpdate(staleDataUpdate);
        }
        
        int unitOfMeasure = UnitOfMeasure.valueOf(row.getValue("UNIT OF MEASURE")).getId();
        builder.unitOfMeasure(unitOfMeasure);
    
        int decimalPlaces = Integer.valueOf(row.getValue("DECIMAL PLACES"));
        builder.decimalPlaces(decimalPlaces);
    }
}

package com.cannontech.common.bulk.processor;

import java.util.Map;

import com.cannontech.common.bulk.model.PointPeriodicRate;
import com.cannontech.common.bulk.model.StaleDataUpdateStyle;
import com.cannontech.common.bulk.model.StatusPointUpdateType;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.types.StrictBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.common.point.CalcStatusPointBuilder;
import com.cannontech.common.point.PointCalculation;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointType;

import static com.cannontech.common.bulk.model.PointImportParameters.*;

public class CalcStatusPointImportProcessor extends PointImportProcessor {
    private Map<String, PointCalculation> calcMap;
    
    public CalcStatusPointImportProcessor(ImportFileFormat format, Map<String, PointCalculation> calcMap, 
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
        
        CalcStatusPointBuilder builder = pointBuilderFactory.getCalcStatusPointBuilder(paoId, pointDao.getNextPointId(), pointName, isDisabled);
        
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
        
        String calculationId = row.getValue(CALCULATION.NAME);
        PointCalculation calculation = calcMap.get(calculationId);
        if(calculation == null) {
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.invalidCalculation");
            throw new ProcessingException(error, "invalidCalculation");
        }
        builder.setCalculation(calculation);
        
        StatusPointUpdateType updateType = StatusPointUpdateType.valueOf(row.getValue(STATUS_UPDATE_TYPE.NAME));
        builder.setUpdateType(updateType);
        if(updateType.hasRate()) {
            builder.setUpdateRate(PointPeriodicRate.valueOf(row.getValue(UPDATE_RATE.NAME)));
        }

        builder.setStateGroup(row.getValue(STATE_GROUP.NAME));
        builder.setInitialState(row.getValue(INITIAL_STATE.NAME));
        
        if(row.hasValue(STALE_DATA_TIME.NAME)) {
            int staleDataTime = Integer.valueOf(row.getValue(STALE_DATA_TIME.NAME));
            int staleDataUpdate = StaleDataUpdateStyle.valueOf(row.getValue(STALE_DATA_UPDATE.NAME)).getIndex();
            builder.setStaleDataTime(staleDataTime);
            builder.setStaleDataUpdate(staleDataUpdate);
        }
        
        if(row.hasValue(ARCHIVE_DATA.NAME)) {
            builder.setArchiveData(StrictBoolean.valueOf(row.getValue(ARCHIVE_DATA.NAME)));
        }
        
        try {
            builder.insert();
        } catch(IllegalStateException e) {
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.invalidStateOrGroup");
            throw new ProcessingException(error, "invalidStateOrGroup", e);
        }
    }
    
    @Override
    protected PointType getPointType(ImportRow row) {
        return PointType.CalcStatus;
    }
}

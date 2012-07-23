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
        String deviceName = row.getValue("DEVICE NAME");
        PaoType paoType = ImportPaoType.valueOf(row.getValue("DEVICE TYPE"));
        String pointName = row.getValue("POINT NAME");
        int paoId = validatePaoAndPoint(deviceName, paoType, pointName);
        Boolean isDisabled = StrictBoolean.valueOf(row.getValue("DISABLED"));
        
        CalcStatusPointBuilder builder = pointBuilderFactory.getCalcStatusPointBuilder(paoId, pointDao.getNextPointId(), pointName, isDisabled);
        
        String calculationId = row.getValue("CALCULATION");
        PointCalculation calculation = calcMap.get(calculationId);
        if(calculation == null) {
            String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.pointImport.error.invalidCalculation");
            throw new ProcessingException(error);
        }
        builder.setCalculation(calculation);
        
        StatusPointUpdateType updateType = StatusPointUpdateType.valueOf(row.getValue("UPDATE TYPE"));
        builder.setUpdateType(updateType);
        if(updateType.hasRate()) {
            builder.setUpdateRate(PointPeriodicRate.valueOf(row.getValue("UPDATE RATE")));
        }

        builder.setStateGroup(row.getValue("STATE GROUP"));
        builder.setInitialState(row.getValue("INITIAL STATE"));
        
        if(row.hasValue("STALE DATA TIME")) {
            int staleDataTime = Integer.valueOf(row.getValue("STALE DATA TIME"));
            int staleDataUpdate = StaleDataUpdateStyle.valueOf(row.getValue("STALE DATA UPDATE")).getIndex();
            builder.setStaleDataTime(staleDataTime);
            builder.setStaleDataUpdate(staleDataUpdate);
        }
        
        if(row.hasValue("ARCHIVE DATA")) {
            builder.setArchiveData(StrictBoolean.valueOf(row.getValue("ARCHIVE DATA")));
        }
        
        try {
            builder.insert();
        } catch(IllegalStateException e) {
            String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.pointImport.error.invalidStateOrGroup");
            throw new ProcessingException(error, e);
        }
    }

}

package com.cannontech.common.bulk.processor;

import com.cannontech.common.bulk.model.StaleDataUpdateStyle;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.types.StrictBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.common.point.StatusPointBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.StateControlType;

public class StatusPointImportProcessor extends PointImportProcessor {
    
    public StatusPointImportProcessor(ImportFileFormat format, MessageSourceAccessor messageSourceAccessor,
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
        
        StatusPointBuilder builder = pointBuilderFactory.getStatusPointBuilder(paoId, pointDao.getNextPointId(), pointName, isDisabled);
        
        builder.stateGroup(row.getValue("STATE GROUP"));
        builder.initialState(row.getValue("INITIAL STATE"));
        
        if(row.hasValue("POINT OFFSET")) {
            builder.pointOffset(Integer.valueOf(row.getValue("POINT OFFSET")));
        }
        
        if(row.hasValue("ARCHIVE DATA")) {
            builder.archive(StrictBoolean.valueOf(row.getValue("ARCHIVE DATA")));
        }
        
        if(row.hasValue("CONTROL INHIBIT")) {
            builder.controlInhibit(StrictBoolean.valueOf(row.getValue("CONTROL INHIBIT")));
        }
        
        if(row.hasValue("STALE DATA TIME")) {
            builder.staleDataTime(Integer.valueOf(row.getValue("STALE DATA TIME")));
            StaleDataUpdateStyle dataUpdateStyle = StaleDataUpdateStyle.valueOf(row.getValue("STALE DATA UPDATE"));
            builder.staleDataUpdate(dataUpdateStyle.getIndex());
        }
        
        ControlType controlType = null;
        if(row.hasValue("CONTROL TYPE")) {
            controlType = ControlType.valueOf(row.getValue("CONTROL TYPE"));
            builder.controlType(controlType);
        }
        
        if(controlType != null) {
            if(row.hasValue("CONTROL POINT OFFSET")) {
                builder.controlOffset(Integer.valueOf(row.getValue("CONTROL POINT OFFSET")));
            }
            if(row.hasValue("CLOSE TIME 1")) {
                builder.closeTime1(Integer.valueOf(row.getValue("CLOSE TIME 1")));
            }
            if(row.hasValue("CLOSE TIME 2")) {
                builder.closeTime2(Integer.valueOf(row.getValue("CLOSE TIME 2")));
            }
            if(row.hasValue("STATE 1 COMMAND")) {
                builder.state1Command(StateControlType.valueOf(row.getValue("STATE 1 COMMAND")));
            }
            if(row.hasValue("STATE 2 COMMAND")) {
                builder.state2Command(StateControlType.valueOf(row.getValue("STATE 2 COMMAND")));
            }
            if(row.hasValue("COMMAND TIMEOUT")) {
                builder.commandTimeout(Integer.valueOf(row.getValue("COMMAND TIMEOUT")));
            }
        }
        
        try {
            builder.insert();
        } catch(IllegalStateException e) {
            String error = messageSourceAccessor.getMessage("yukon.web.modules.amr.pointImport.error.invalidStateOrGroup");
            throw new ProcessingException(error, e);
        }
    }
}

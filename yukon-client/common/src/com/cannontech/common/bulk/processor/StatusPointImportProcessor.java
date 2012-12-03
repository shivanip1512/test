package com.cannontech.common.bulk.processor;

import static com.cannontech.common.bulk.model.PointImportParameters.*;

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
import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.StatusControlType;

public class StatusPointImportProcessor extends PointImportProcessor {
    
    public StatusPointImportProcessor(ImportFileFormat format, MessageSourceAccessor messageSourceAccessor,
                                      PaoDao paoDao, PointDao pointDao, DBPersistentDao dbPersistentDao,
                                      PointBuilderFactory pointBuilderFactory) {
        
        super(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    @Override
    protected void createPoint(ImportRow row) {
        String deviceName = row.getValue(DEVICE_NAME.NAME);
        PaoType paoType = ImportPaoType.valueOf(row.getValue(DEVICE_TYPE.NAME));
        String pointName = row.getValue(POINT_NAME.NAME);
        int paoId = validatePaoAndPoint(deviceName, paoType, pointName);
        Boolean isDisabled = StrictBoolean.valueOf(row.getValue(DISABLED.NAME));
        
        StatusPointBuilder builder = pointBuilderFactory.getStatusPointBuilder(paoId, pointDao.getNextPointId(), pointName, isDisabled);
        
        builder.setStateGroup(row.getValue(STATE_GROUP.NAME));
        builder.setInitialState(row.getValue(INITIAL_STATE.NAME));
        
        if(row.hasValue(POINT_OFFSET.NAME)) {
            builder.setPointOffset(Integer.valueOf(row.getValue(POINT_OFFSET.NAME)));
        }
        
        if(row.hasValue(ARCHIVE_DATA.NAME)) {
            builder.setArchive(StrictBoolean.valueOf(row.getValue(ARCHIVE_DATA.NAME)));
        }
        
        if(row.hasValue(CONTROL_INHIBIT.NAME)) {
            builder.setControlInhibit(StrictBoolean.valueOf(row.getValue(CONTROL_INHIBIT.NAME)));
        }
        
        if(row.hasValue(STALE_DATA_TIME.NAME)) {
            builder.setStaleDataTime(Integer.valueOf(row.getValue(STALE_DATA_TIME.NAME)));
            StaleDataUpdateStyle dataUpdateStyle = StaleDataUpdateStyle.valueOf(row.getValue(STALE_DATA_UPDATE.NAME));
            builder.setStaleDataUpdate(dataUpdateStyle.getIndex());
        }
        
        StatusControlType controlType = null;
        if(row.hasValue(CONTROL_TYPE.NAME)) {
            controlType = StatusControlType.valueOf(row.getValue(CONTROL_TYPE.NAME));
            builder.setControlType(controlType);
        }
        
        if(controlType != null) {
            if(row.hasValue(CONTROL_POINT_OFFSET.NAME)) {
                builder.setControlOffset(Integer.valueOf(row.getValue(CONTROL_POINT_OFFSET.NAME)));
            }
            if(row.hasValue(CLOSE_TIME_1.NAME)) {
                builder.setCloseTime1(Integer.valueOf(row.getValue(CLOSE_TIME_1.NAME)));
            }
            if(row.hasValue(CLOSE_TIME_2.NAME)) {
                builder.setCloseTime2(Integer.valueOf(row.getValue(CLOSE_TIME_2.NAME)));
            }
            if(row.hasValue(STATE_1_COMMAND.NAME)) {
                builder.setState1Command(ControlStateType.valueOf(row.getValue(STATE_1_COMMAND.NAME)));
            }
            if(row.hasValue(STATE_2_COMMAND.NAME)) {
                builder.setState2Command(ControlStateType.valueOf(row.getValue(STATE_2_COMMAND.NAME)));
            }
            if(row.hasValue(COMMAND_TIMEOUT.NAME)) {
                builder.setCommandTimeout(Integer.valueOf(row.getValue(COMMAND_TIMEOUT.NAME)));
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

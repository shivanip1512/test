package com.cannontech.web.updater.phaseDetect.handler;


import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.phaseDetect.PhaseDetectUpdaterTypeEnum;

public class IsCompletePhaseDetectUpdaterHandler implements PhaseDetectUpdaterHandler {

    private CommandRequestExecutionDao commandRequestExecutionDao;
    
    @Override
    public String handle(int id, YukonUserContext userContext) {

        return Boolean.valueOf(commandRequestExecutionDao.isComplete(id)).toString();
    }
    
    @Override
    public PhaseDetectUpdaterTypeEnum getUpdaterType() {
        return PhaseDetectUpdaterTypeEnum.IS_COMPLETE;
    }

    @Autowired
    public void setCommandRequestExecutionDao(CommandRequestExecutionDao commandRequestExecutionDao) {
        this.commandRequestExecutionDao = commandRequestExecutionDao;
    }
}

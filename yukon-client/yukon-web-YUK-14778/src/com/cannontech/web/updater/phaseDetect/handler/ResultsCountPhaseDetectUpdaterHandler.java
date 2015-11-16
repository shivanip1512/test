package com.cannontech.web.updater.phaseDetect.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.phaseDetect.PhaseDetectUpdaterTypeEnum;

public class ResultsCountPhaseDetectUpdaterHandler implements PhaseDetectUpdaterHandler {

    private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    
    @Override
    public String handle(int id, YukonUserContext userContext) {

        int count = commandRequestExecutionResultDao.getCountByExecutionId(id);
        return String.valueOf(count);
    }
    
    @Override
    public PhaseDetectUpdaterTypeEnum getUpdaterType() {
        return PhaseDetectUpdaterTypeEnum.RESULTS_COUNT;
    }

    @Autowired
    public void setCommandRequestExecutionResultDao(
            CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
        this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
    }
}

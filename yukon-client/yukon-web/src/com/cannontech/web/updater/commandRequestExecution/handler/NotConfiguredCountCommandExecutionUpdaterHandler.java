package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class NotConfiguredCountCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionDao;

    @Override
    public String handle(int id, YukonUserContext userContext) {

        int count = commandRequestExecutionDao.getUnsupportedCountByExecutionId(id, CommandRequestUnsupportedType.NOT_CONFIGURED);
        return String.valueOf(count);
    }

    @Override
    public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
        return CommandRequestExecutionUpdaterTypeEnum.NOT_CONFIGURED_COUNT;
    }
}

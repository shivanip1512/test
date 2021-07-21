package com.cannontech.clientutils.logger.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.Direction;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class YukonLoggerServiceImpl implements YukonLoggerService {
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private YukonLoggerDao yukonLoggerDao;

    @Override
    public YukonLogger getLogger(int loggerId) {
        return yukonLoggerDao.getLogger(loggerId);
    }

    @Override
    public YukonLogger addLogger(YukonLogger logger) {
        int loggerId = yukonLoggerDao.addLogger(logger);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.LOGGER, loggerId);
        return logger;
    }

    @Override
    public YukonLogger updateLogger(int loggerId, YukonLogger logger) {
        yukonLoggerDao.updateLogger(loggerId, logger);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.LOGGER, loggerId);
        return logger;
    }

    @Override
    public int deleteLogger(int loggerId) {
        yukonLoggerDao.deleteLogger(loggerId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.LOGGER, loggerId);
        return loggerId;
    }

    @Override
    public List<YukonLogger> getLoggers(String loggerName, SortBy sortBy, Direction direction, List<LoggerLevel> loggerLevels) {
        return yukonLoggerDao.getLoggers(loggerName, sortBy, direction, loggerLevels);
    }
}
package com.cannontech.clientutils.logger.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class YukonLoggerDaoImpl implements YukonLoggerDao {

    @Autowired private DbChangeManager dbChangeManager;

    @Override
    public YukonLogger getLogger(int loggerId) {
        // TODO Will be implemented in YUK-24463
        return null;
    }

    @Override
    public void addLogger(YukonLogger logger) {
        // TODO Will be implemented in YUK-24463
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.LOGGER, logger.getLoggerId());
    }

    @Override
    public void updateLogger(YukonLogger logger) {
        // TODO Will be implemented in YUK-24463
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.LOGGER, logger.getLoggerId());
    }

    @Override
    public void deleteLogger(int loggerId) {
        // TODO Will be implemented in YUK-24463
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.LOGGER, loggerId);
    }

    @Override
    public List<YukonLogger> getLoggers() {
        // TODO Will be implemented in YUK-24463
        YukonLogger logger = new YukonLogger(1, LoggerLevel.INFO, "com.cannontech", null, null);
        List<YukonLogger> loggers = new ArrayList<YukonLogger>();
        loggers.add(logger);
        return loggers;
    }

}
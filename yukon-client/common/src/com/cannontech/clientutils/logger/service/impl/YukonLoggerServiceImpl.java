package com.cannontech.clientutils.logger.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.DeletionFailureException;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.LoggerType;
import com.cannontech.common.log.model.SystemLogger;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.Direction;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class YukonLoggerServiceImpl implements YukonLoggerService {
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private YukonLoggerDao yukonLoggerDao;
    @Autowired private SystemEventLogService systemEventLogService;

    @Override
    public YukonLogger getLogger(int loggerId) {
        try {
            YukonLogger logger = yukonLoggerDao.getLogger(loggerId);
            logger.setLoggerType(SystemLogger.isSystemLogger(logger.getLoggerName()) ? LoggerType.SYSTEM_LOGGER : LoggerType.USER_LOGGER );
            return logger;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Logger Id not found");
        }
    }

    @Override
    public YukonLogger addLogger(YukonLogger logger) {
        int loggerId = yukonLoggerDao.addLogger(logger);
        logger.setLoggerId(loggerId);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.LOGGER, loggerId);
        systemEventLogService.loggerAdded(logger.getLoggerName(), logger.getLevel().toString(),
                getExpirationDate(logger.getExpirationDate()), ApiRequestContext.getContext().getLiteYukonUser());
        return logger;
    }

    @Override
    public YukonLogger updateLogger(int loggerId, YukonLogger logger) {
        yukonLoggerDao.updateLogger(loggerId, logger);
        logger = getLogger(loggerId);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.LOGGER, loggerId);
        systemEventLogService.loggerUpdated(logger.getLoggerName(), logger.getLevel().toString(),
                getExpirationDate(logger.getExpirationDate()), ApiRequestContext.getContext().getLiteYukonUser());
        return logger;
    }

    /**
     * Return Expiration Date after adding 1439 minutes
     */
    private Date getExpirationDate(Date expirationDate) {
        return expirationDate == null ? null : DateUtils.addMinutes(expirationDate, 1439);
    }

    @Override
    public int deleteLogger(int loggerId) {
        try {
            String loggerName = getLogger(loggerId).getLoggerName();
            if(SystemLogger.isSystemLogger(loggerName)) {
                throw new DeletionFailureException("System logger deletion not supported.");
            }
            yukonLoggerDao.deleteLogger(loggerId);
            dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.LOGGER, loggerId);
            systemEventLogService.loggerDeleted(loggerName, ApiRequestContext.getContext().getLiteYukonUser());
            return loggerId;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Logger Id not found");
        }
    }

    @Override
    public List<YukonLogger> getLoggers(String loggerName, SortBy sortBy, Direction direction, List<LoggerLevel> loggerLevels) {
        List<YukonLogger> loggers = yukonLoggerDao.getLoggers(loggerName, sortBy, direction, loggerLevels);
        loggers.forEach(logger -> {
            logger.setLoggerType(
                    SystemLogger.isSystemLogger(logger.getLoggerName()) ? LoggerType.SYSTEM_LOGGER : LoggerType.USER_LOGGER);
        });
        return loggers;
    }

}
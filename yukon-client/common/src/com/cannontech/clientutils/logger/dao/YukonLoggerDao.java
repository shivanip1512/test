package com.cannontech.clientutils.logger.dao;

import java.util.List;

import com.cannontech.clientutils.logger.service.YukonLoggerService.SortBy;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.Direction;

public interface YukonLoggerDao {

    /**
     * Return Logger for the specified loggerId.
     */
    YukonLogger getLogger(int loggerId);

    /**
     * Add a Logger
     */
    int addLogger(YukonLogger logger);

    /**
     * Update a Logger
     */
    void updateLogger(int loggerId, YukonLogger logger);

    /**
     * Delete a Logger
     */
    void deleteLogger(int loggerId);

    /**
     * Retrieve all Loggers
     */
    List<YukonLogger> getLoggers(String loggerName, SortBy sortBy, Direction direction, List<LoggerLevel> loggerLevels);

    /**
     * Delete expired loggers if any. Return true if any loggers are deleted else return false.
     */
    boolean deleteExpiredLoggers();

}
package com.cannontech.clientutils.logger.dao;

import java.util.List;

import com.cannontech.common.log.model.YukonLogger;

public interface YukonLoggerDao {

    /**
     * Return Logger for the specified loggerId.
     */
    YukonLogger getLogger(int loggerId);

    /**
     * Add a Logger
     */
    void addLogger(YukonLogger logger);

    /**
     * Update a Logger
     */
    void updateLogger(YukonLogger logger);

    /**
     * Delete a Logger
     */
    void deleteLogger(int loggerId);

    /**
     * Retrieve all Loggers
     */
    List<YukonLogger> getLoggers();

}
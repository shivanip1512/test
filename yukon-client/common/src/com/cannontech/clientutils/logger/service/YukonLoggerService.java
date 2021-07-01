package com.cannontech.clientutils.logger.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.cannontech.common.log.model.YukonLogger;

public interface YukonLoggerService {

    /**
     * Return Logger for the specified loggerId.
     */
    YukonLogger getLogger(int loggerId) throws ExecutionException;

    /**
     * Return the Logger after adding.
     */
    YukonLogger addLogger(YukonLogger logger) throws ExecutionException;

    /**
     * Return the Logger after updating.
     */
    YukonLogger updateLogger(YukonLogger logger) throws ExecutionException;

    /**
     * Return the Logger ID after deleting the logger for specified logger ID.
     */
    int deleteLogger(int loggerId);

    /**
     * Return all the Loggers
     */
    List<YukonLogger> getLoggers();
}

package com.cannontech.clientutils.logger.service;

import java.util.List;

import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.Direction;

public interface YukonLoggerService {

    /**
     * Return Logger for the specified loggerId.
     */
    YukonLogger getLogger(int loggerId);

    /**
     * Return the Logger after adding.
     */
    YukonLogger addLogger(YukonLogger logger);

    /**
     * Return the Logger after updating.
     */
    YukonLogger updateLogger(int loggerId, YukonLogger logger);

    /**
     * Return the loggerId after deleting the logger for specified loggerId.
     */
    int deleteLogger(int loggerId);

    /**
     * Return all the Loggers
     */
    List<YukonLogger> getLoggers(String loggerName, SortBy sortBy, Direction direction, List<LoggerLevel> loggerLevels);

    public enum SortBy {
        NAME("LoggerName"),
        LEVEL("LoggerLevel"),
        EXPIRATION("ExpirationDate");

        private final String dbString;

        private SortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }
}

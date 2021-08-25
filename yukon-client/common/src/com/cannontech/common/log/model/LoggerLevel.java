package com.cannontech.common.log.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum LoggerLevel implements DisplayableEnum {
    DEBUG,
    ERROR,
    FATAL,
    INFO,
    OFF,
    TRACE,
    WARN;

    private final static String baseKey =  "yukon.web.modules.adminSetup.config.loggers.";

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
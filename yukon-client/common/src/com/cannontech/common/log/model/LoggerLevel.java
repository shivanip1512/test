package com.cannontech.common.log.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum LoggerLevel implements DisplayableEnum {
    OFF,
    FATAL,
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE;

    private final static String baseKey =  "yukon.web.modules.adminSetup.config.loggers.";

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
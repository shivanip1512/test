package com.cannontech.watchdog.model;

import java.util.Map;

public class WatchdogWarnings { 
    private final WatchdogWarningType warningType;
    private final Map<String, Object> arguments;
    

    public WatchdogWarnings(WatchdogWarningType warningType, Map<String, Object> arguments) {
        super();
        this.warningType = warningType;
        this.arguments = arguments;
    }


    public WatchdogWarningType getWarningType() {
        return warningType;
    }


    public Map<String, Object> getArguments() {
        return arguments;
    }
    
}

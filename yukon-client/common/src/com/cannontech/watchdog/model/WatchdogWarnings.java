package com.cannontech.watchdog.model;

import java.util.List;

public class WatchdogWarnings {
    // TODO: This is temporary code, this will have to be replaced  
    private final WatchdogWarningType warningType;
    private final List<Object> arguments;
    

    public WatchdogWarnings(WatchdogWarningType warningType, List<Object> arguments) {
        super();
        this.warningType = warningType;
        this.arguments = arguments;
    }


    public WatchdogWarningType getWarningType() {
        return warningType;
    }


    public List<Object> getArguments() {
        return arguments;
    }
    
}

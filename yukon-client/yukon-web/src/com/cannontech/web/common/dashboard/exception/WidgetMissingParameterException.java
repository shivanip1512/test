package com.cannontech.web.common.dashboard.exception;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exception thrown when a widget parameter is missing during dashboard widget creation.
 */
public class WidgetMissingParameterException extends WidgetCreationException {
    private static final String key = "yukon.web.modules.dashboard.exception.missingParameters";
    private Set<String> missingParameterNames;
    
    public WidgetMissingParameterException(String message, Set<String> missingParameterNames) {
        super(message, key, missingParameterNames.stream().collect(Collectors.joining(", ")));
        this.missingParameterNames = missingParameterNames;
    }
    
    public WidgetMissingParameterException(String message, Throwable cause, Set<String> missingParameterNames) {
        super(message, cause, key, missingParameterNames.stream().collect(Collectors.joining(", ")));
        this.missingParameterNames = missingParameterNames;
    }
    
    public Set<String> getMissingParameterNames() {
        return missingParameterNames;
    }
}

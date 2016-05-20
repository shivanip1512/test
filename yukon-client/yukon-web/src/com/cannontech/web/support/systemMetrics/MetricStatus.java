package com.cannontech.web.support.systemMetrics;

/**
 * Indicates the overall health of a system metric. 
 */
public enum MetricStatus {
    UNKNOWN("icon-disabled"),       // Waiting for more data (usually shortly after startup)
    GOOD("icon-enabled"),           // All is well
    WARN("icon-error"),             // There may be problems
    ERROR("icon-exclamation"),      // There is definitely a problem
    ;
    
    private String iconName;
    
    private MetricStatus(String iconName) {
        this.iconName = iconName;
    }
    
    /**
     * This priority is used when multiple criteria pertain to a single metric. The overall status will be the 
     * highest-priority status (basically the worst). 
     */
    public int getPriority() {
        return ordinal();
    }
    
    /**
     * @return The name of the icon that visually represents this status.
     */
    public String getIconName() {
        return iconName;
    }
}

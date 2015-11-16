package com.cannontech.common.systemMetrics;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface SystemMetrics {

    /** Point offset to Notification Server CPU Utilization */
    public final static int NOTIFICATION_CPU = 1014;
    /** Point offset to Service Manager CPU Utilization */
    public final static int SERVICE_MANAGER_CPU = 1015;
    /** Point offset to Web Service CPU Utilization */
    public final static int WEB_SERVER_CPU = 1016;
    
    /**
     * Setter that takes the attribute Id for LoadAverage.
     * 
     * @param attribute attribute Id.
     */
    public void setLoadAverageAttribute(BuiltInAttribute attribute);
    
    /**
     * Since we went through all the trouble of calculating the process load, and to get rid of a
     * pesky warning, we export this so MBean can see it.
     * 
     * @return current load from this process.
     */
    public double getloadAverage();

    /**
     * Setter that takes the attribute Id for Memory Usage.
     * 
     * @param attribute attribute Id.
     */
    void setMemoryAttribute(BuiltInAttribute memoryAttribute);
}
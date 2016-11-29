package com.cannontech.broker.systemMetrics;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface BrokerSystemMetrics {
    
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
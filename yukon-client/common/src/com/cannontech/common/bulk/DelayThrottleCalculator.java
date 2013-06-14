package com.cannontech.common.bulk;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.common.config.ConfigurationSource;

@ManagedResource
public class DelayThrottleCalculator {
    private long lastTime = 0;
    private long maximumDelay = 0;
    private ConfigurationSource configurationSource;
    private String configurationPropertyName;
    
    public synchronized long getNextDelay(TimeUnit unit) {
        long currentTimeMillis = System.currentTimeMillis();
        long nextTime = lastTime + getMaximumDelay();
        if (nextTime < currentTimeMillis) {
            lastTime = currentTimeMillis;
            return 0;
        } else {
            long delay = unit.convert(nextTime -  currentTimeMillis, TimeUnit.MILLISECONDS);
            lastTime = nextTime;
            return delay;
        }
    }
    
    @PostConstruct
    public void init() throws Exception {
        setMaximumDelay(configurationSource.getInteger(configurationPropertyName, 500));
    }
    
    @Required
    public void setConfigurationPropertyName(String configurationPropertyName) {
        this.configurationPropertyName = configurationPropertyName;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @ManagedAttribute
    public void setMaximumDelay(long maximumDelay) {
        this.maximumDelay = maximumDelay;
    }

    @ManagedAttribute
    public long getMaximumDelay() {
        return maximumDelay;
    }
}

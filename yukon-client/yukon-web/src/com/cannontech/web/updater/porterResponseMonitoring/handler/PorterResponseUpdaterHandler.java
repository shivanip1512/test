package com.cannontech.web.updater.porterResponseMonitoring.handler;

import com.cannontech.web.updater.porterResponseMonitoring.PorterResponseMonitorUpdaterTypeEnum;

/**
 * Controls the data updating for the Porter Response Monitor
 */
public interface PorterResponseUpdaterHandler {
    
    /**
     * Gets the latest data value for the given porter response monitor id and the updater type
     * @param porterResponseMonitorId the id for the porter response monitor
     * @return the latest data value for the updater type
     */
    public String handle(int porterResponseMonitorId);

    public PorterResponseMonitorUpdaterTypeEnum getUpdaterType();
}

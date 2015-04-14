package com.cannontech.message.dispatch.command.service;

import com.cannontech.common.tdc.model.AltScanRate;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandService {

    /**
     * Enables or disables a point. If disable is set to true the point will be disabled
     * otherwise the point will be enabled
     */
    public void togglePointEnablement(int pointId, boolean disable, LiteYukonUser user);

    /**
     * Enables or disables all points on the device. If disable is set to true a all
     * points on the device will be disabled otherwise all points will be enabled
     */
    public void toggleDeviceEnablement(int deviceId, boolean disable, LiteYukonUser user);

    /**
     * Sends alt scan rate command to the device
     */
    public void sendAltScanRate(int deviceId, AltScanRate scanRate, LiteYukonUser user);

    /**
     * Sends acknowledge alarm command to the device
     */
    public void sendAcknowledgeAlarm(int pointId, int condition, LiteYukonUser user);

    /**
     * Sends open or close control request. If closed is set to true the control closed
     * command will be send otherwise control opened command will be send
     */
    public void toggleControlRequest(int deviceId, int pointId, boolean closed, LiteYukonUser user);

    /**
     * Sends analog output request
     */
    public void sendAnalogOutputRequest(int pointId, double outputValue, LiteYukonUser user);

    /**
     * Sends enable or disable control command. If disable is set to true the enable
     * command will be send otherwise disable command will be send
     */
    public void toggleControlEnablement(int deviceId, boolean disable, LiteYukonUser user);
    
    /**
     * Sends reset season control hrs command
     */
    public void resetSeasonControlHrs();
}

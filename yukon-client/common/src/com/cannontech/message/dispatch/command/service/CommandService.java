package com.cannontech.message.dispatch.command.service;

import com.cannontech.common.tdc.model.AltScanRateEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandService {

    /**
     * Method to enable or disable a point. If disable is set to true the point will be disabled
     * otherwise the point will be enabled
     * @param pointId
     * @param disable
     * @param user
     */
    public void togglePointEnablement(int pointId, boolean disable, LiteYukonUser user);

    /**
     * Method to enable or disable a all points on the device. If disable is set to true a all
     * points on the device will be disabled otherwise all points will be enabled
     * @param deviceId
     * @param disable
     * @param user
     */
    public void toggleDeviceEnablement(int deviceId, boolean disable, LiteYukonUser user);

    /**
     * Method to send alt scan rate command to the device
     * @param deviceId
     * @param scanRate
     * @param user
     */
    public void sendAltScanRate(int deviceId, AltScanRateEnum scanRate, LiteYukonUser user);

    /**
     * Method to send acknowledge alarm command to the device
     * @param pointId
     * @param condition
     * @param user
     */
    public void sendAcknowledgeAlarm(int pointId, int condition, LiteYukonUser user);

    /**
     * Method to send open or close control request. If closed is set to true the control closed
     * command will be send otherwise control opened command will be send
     * @param deviceId
     * @param pointId
     * @param closed
     * @param user
     */
    public void toggleControlRequest(int deviceId, int pointId, boolean closed, LiteYukonUser user);

    /**
     * Method to send analog output request
     * @param pointId
     * @param outputValue
     * @param user
     */
    public void sendAnalogOutputRequest(int pointId, double outputValue, LiteYukonUser user);

    /**
     * Method to send enable or disable control command. If disable is set to true the enable
     * command will be send otherwise disable command will be send
     * @param deviceId
     * @param disable
     * @param user
     */
    public void toggleControlEnablement(int deviceId, boolean disable, LiteYukonUser user);
}

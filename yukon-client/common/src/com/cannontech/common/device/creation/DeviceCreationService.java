package com.cannontech.common.device.creation;

import com.cannontech.common.device.model.SimpleDevice;


public interface DeviceCreationService {

    /**
     * Creates DLC device by template.
     *
     * @param templateName
     * @param newDeviceName
     * @param copyPoints
     * @return 
     * @throws DeviceCreationException
     */
    public SimpleDevice createDeviceByTemplate(String templateName, String newDeviceName, boolean copyPoints) throws DeviceCreationException;
    
    /**
     * Creates RFN device by template.
     *
     * @param templateName 
     * @param newDeviceName
     * @param model
     * @param manufacturer
     * @param serialNumber
     * @param copyPoints
     * @return 
     * @throws DeviceCreationException
     */
    public SimpleDevice createRfnDeviceByTemplate(String templateName, String newDeviceName, String model, String manufacturer, String serialNumber, boolean copyPoints) throws DeviceCreationException;
    
    /**
     * Creates the DLC device by device type.
     *
     * @param deviceType
     * @param name
     * @param address
     * @param routeId
     * @param createPoints
     * @return 
     * @throws DeviceCreationException
     */
    public SimpleDevice createCarrierDeviceByDeviceType(int deviceType, String name, int address, int routeId, boolean createPoints) throws DeviceCreationException;
    
    /**
     * Creates the RFN device by device type.
     *
     * @param deviceType
     * @param name
     * @param model
     * @param manufacturer
     * @param serialNumber
     * @param createPoints
     * @return 
     * @throws DeviceCreationException
     */
    public SimpleDevice createRfnDeviceByDeviceType(String deviceType, String name, String model, String manufacturer, String serialNumber, boolean createPoints) throws DeviceCreationException;
}

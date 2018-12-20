package com.cannontech.common.device.creation;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;


public interface DeviceCreationService {

    /**
     * Creates MCT device by template.
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
     * @throws BadConfigurationException 
     */
    public SimpleDevice createRfnDeviceByTemplate(String templateName, String newDeviceName,
            RfnIdentifier rfnIdentifier, boolean copyPoints) throws DeviceCreationException, BadConfigurationException;

    /**
     * Creates the MCT device by device type.
     *
     * @param deviceType
     * @param name
     * @param address
     * @param routeId
     * @param createPoints
     * @return 
     * @throws DeviceCreationException
     */
    public SimpleDevice createCarrierDeviceByDeviceType(PaoType deviceType, String name, int address, int routeId, boolean createPoints) throws DeviceCreationException;
    
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
    public SimpleDevice createRfnDeviceByDeviceType(PaoType type, String name, RfnIdentifier rfId, boolean createPoints) throws DeviceCreationException;

    SimpleDevice createIEDDeviceByDeviceType(PaoType paoType, String name, int portId, boolean createPoints)
            throws DeviceCreationException;

    /**
     * Creates device type by paoType and name
     */
    SimpleDevice createDeviceByDeviceType(PaoType paoType, String name) throws DeviceCreationException;
}

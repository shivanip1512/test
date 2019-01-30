package com.cannontech.dr.itron.service.impl;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddD2GAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.DeviceIdentifierAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIType;

public class DeviceManagerHelper {

    /**
     * <urn:AddHANDeviceRequest>
     *  <urn:D2GAttributes>
     *      <urn:ESI>
     *          <urn:DeviceIdentifiers>
     *              <urn:MacID>00beef0a00000002</urn:MacID>
     *          </urn:DeviceIdentifiers>
     *      </urn:ESI>
     *      <urn:ServicePointUtilID>ABC</urn:ServicePointUtilID>
     * </urn:D2GAttributes>
     */
    public static AddHANDeviceRequest buildRequest(String macId, int inventoryId) {
        AddHANDeviceRequest request = new AddHANDeviceRequest();
        DeviceIdentifierAttributeType identifier = new DeviceIdentifierAttributeType();
        identifier.setMacID(macId);
        ESIType type = new ESIType();
        type.setDeviceIdentifiers(identifier);
        AddD2GAttributeType attribute = new AddD2GAttributeType();
        attribute.setESI(type);
        
        attribute.setServicePointUtilID(String.valueOf(inventoryId));
        
        request.setD2GAttributes(attribute);
        return request;
    }
}

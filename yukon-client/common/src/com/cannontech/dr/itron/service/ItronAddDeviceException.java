package com.cannontech.dr.itron.service;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;

public class ItronAddDeviceException extends ItronCommunicationException {

    private AddHANDeviceResponse response;
   
    public ItronAddDeviceException(AddHANDeviceResponse response) {
        super("Error recieved from Itron:" + XmlUtils.getPrettyXml(response));
        this.response = response;
    }

    public AddHANDeviceResponse getResponse() {
        return response;
    }
}
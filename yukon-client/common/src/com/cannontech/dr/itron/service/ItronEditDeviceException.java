package com.cannontech.dr.itron.service;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;

public class ItronEditDeviceException extends ItronCommunicationException {

    private EditHANDeviceResponse response;    
    
    public ItronEditDeviceException(EditHANDeviceResponse response) {
        super("Error received from Itron:" + XmlUtils.getPrettyXml(response));
        this.response = response;
    }

    public EditHANDeviceResponse getResponse() {
        return response;
    }

}
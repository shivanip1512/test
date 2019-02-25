package com.cannontech.dr.itron.service;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class ItronAddDeviceException extends ItronCommunicationException {

    private AddHANDeviceResponse response;
   
    public ItronAddDeviceException(AddHANDeviceResponse response) {
        super("Error recieved from Itron:" + XmlUtils.getPrettyXml(response));
        this.response = response;
    }

    public AddHANDeviceResponse getResponse() {
        return response;
    }
    
    public YukonMessageSourceResolvable getItronMessage() {
        String errors = String.join(", ", getResponse().getErrors());
        return new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.error.itronErrors", errors);
    }
}
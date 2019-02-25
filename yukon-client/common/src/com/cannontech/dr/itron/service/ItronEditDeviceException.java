package com.cannontech.dr.itron.service;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class ItronEditDeviceException extends ItronCommunicationException {

    private EditHANDeviceResponse response;    
    
    public ItronEditDeviceException(EditHANDeviceResponse response) {
        super("Error recieved from Itron:" + XmlUtils.getPrettyXml(response));
        this.response = response;
    }

    public EditHANDeviceResponse getResponse() {
        return response;
    }
    
    public YukonMessageSourceResolvable getItronMessage() {
        String errors = String.join(", ", getResponse().getErrors());
        return new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.error.itronErrors", errors);
    }
}
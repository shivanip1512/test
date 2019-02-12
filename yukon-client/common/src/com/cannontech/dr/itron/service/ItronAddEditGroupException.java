package com.cannontech.dr.itron.service;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupResponseType;

public class ItronAddEditGroupException extends ItronCommunicationException {

    private ESIGroupResponseType response;
   
    public ItronAddEditGroupException(ESIGroupResponseType response) {
        super("Error recieved from Itron:" + XmlUtils.getPrettyXml(response));
        this.response = response;
    }
    
    public ESIGroupResponseType getResponse() {
        return response;
    }
}
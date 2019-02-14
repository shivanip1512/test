package com.cannontech.dr.itron.service.impl;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;

/**
 * Used for debugging purposes. ESIGroupRequestType doesn't contain @XmlRootElement, without it it can't be
 * unmarshalled, this class is a wrapper so we can log the request.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ESIGroupRequestTypeHolder 
{
    public ESIGroupRequestTypeHolder(ESIGroupRequestType type) {
        this.type = type;
    }
    
    public ESIGroupRequestTypeHolder() {
    }

    private ESIGroupRequestType type;
    
    public ESIGroupRequestType getType() {
        return type;
    }

    public void setType(ESIGroupRequestType type) {
        this.type = type;
    }
}
package com.cannontech.dr.itron.service.impl;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupResponseType;

/**
 * Used for debugging purposes. ESIGroupResponseType doesn't contain @XmlRootElement, without it it can't be
 * unmarshalled, this class is a wrapper so we can log the response.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ESIGroupResponseTypeHolder 
{
    public ESIGroupResponseTypeHolder(ESIGroupResponseType type) {
        this.type = type;
    }
    
    public ESIGroupResponseTypeHolder() {
    }

    private ESIGroupResponseType type;
    
    public ESIGroupResponseType getType() {
        return type;
    }

    public void setType(ESIGroupResponseType type) {
        this.type = type;
    }
}
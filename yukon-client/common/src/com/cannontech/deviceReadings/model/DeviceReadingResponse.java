package com.cannontech.deviceReadings.model;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.message.dispatch.message.LitePointData;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DeviceReadingResponse extends LitePointData {

    private Identifier identifier;
    private Attribute attribute;

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

}

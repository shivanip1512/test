package com.cannontech.deviceReadings.model;

import java.util.List;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class DeviceReadingsSelector {

    private Identifier identifier;
    private List<BuiltInAttribute> attributes;

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public List<BuiltInAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<BuiltInAttribute> attributes) {
        this.attributes = attributes;
    }

}

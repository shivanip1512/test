package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class AttributeMappingInput extends AttributeInput {
    private String pointName;

    public AttributeMappingInput() {
    }

    public AttributeMappingInput(BuiltInAttribute attribute, String pointName) {
        super(attribute);
        this.pointName = pointName;
    }

    public final String getPointName() {
        return pointName;
    }
    public final void setPointName(String pointName) {
        this.pointName = pointName;
    }
}
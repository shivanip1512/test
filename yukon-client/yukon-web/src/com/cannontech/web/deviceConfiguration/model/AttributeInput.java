package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public abstract class AttributeInput {
    private BuiltInAttribute attribute;

    public AttributeInput() {
    }

    public AttributeInput(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public final BuiltInAttribute getAttribute() {
        return attribute;
    }
    public final void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }
}
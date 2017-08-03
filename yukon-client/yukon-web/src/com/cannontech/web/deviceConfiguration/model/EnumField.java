package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.web.input.type.BaseEnumeratedType;

public class EnumField extends Field<String> {
    public EnumField(String displayName, String fieldName, String description, BaseEnumeratedType<String> enumField,
            String defaultValue) {
        super(displayName, fieldName, description, enumField, defaultValue, true, enumField.getValidator());
    }
}

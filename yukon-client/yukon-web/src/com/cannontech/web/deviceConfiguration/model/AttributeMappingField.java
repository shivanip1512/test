package com.cannontech.web.deviceConfiguration.model;

import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.input.type.InputType;

public class AttributeMappingField extends Field<List<AttributeMappingInput>> {
    private final Set<BuiltInAttribute> attributes;

    public AttributeMappingField(String displayName, String fieldName, String description, 
                        InputType<List<AttributeMappingInput>> attributeMappingType, Set<BuiltInAttribute> attributes) {

        super(displayName, fieldName, description, attributeMappingType, "", attributeMappingType.getValidator());
        this.attributes = attributes;
    }

    public Set<BuiltInAttribute> getAttributes() {
        return attributes;
    }
}
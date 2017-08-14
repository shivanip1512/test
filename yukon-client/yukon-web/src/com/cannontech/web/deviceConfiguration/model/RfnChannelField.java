package com.cannontech.web.deviceConfiguration.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.deviceConfiguration.enumeration.Read.ReadType;
import com.cannontech.web.input.type.InputType;

public class RfnChannelField extends Field<List<RfnChannelInput>> {
    private final Set<BuiltInAttribute> channelTypes;

    public RfnChannelField(String displayName, String fieldName, String description, 
                        InputType<List<RfnChannelInput>> channelType, Set<BuiltInAttribute> attributes) {

        super(displayName, fieldName, description, channelType, "", channelType.getValidator());
        this.channelTypes = attributes;
    }

    public List<ReadType> getReadTypes() {
        return Arrays.asList(ReadType.values());
    }
    public Set<BuiltInAttribute> getChannelTypes() {
        return channelTypes;
    }
}
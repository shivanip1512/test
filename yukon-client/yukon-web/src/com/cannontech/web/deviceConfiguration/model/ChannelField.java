package com.cannontech.web.deviceConfiguration.model;

import java.util.Arrays;
import java.util.List;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.deviceConfiguration.enumeration.Read.ReadType;
import com.cannontech.web.input.type.InputType;

public class ChannelField extends Field<List<ChannelInput>> {
    private final List<BuiltInAttribute> channelTypes;

    public ChannelField(String displayName, String fieldName, String description, 
                        InputType<List<ChannelInput>> channelType, List<BuiltInAttribute> attributes) {

        super(displayName, fieldName, description, channelType, "", channelType.getValidator());
        this.channelTypes = attributes;
    }

    public List<ReadType> getReadTypes() {
        return Arrays.asList(ReadType.values());
    }
    public List<BuiltInAttribute> getChannelTypes() {
        return channelTypes;
    }
}
package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.deviceConfiguration.enumeration.Read.ReadType;

public class ChannelInput {
    private BuiltInAttribute attribute;
    private ReadType read;

    public ChannelInput() {
    }

    public ChannelInput(BuiltInAttribute attribute, ReadType readType) {
        this.attribute = attribute;
        this.read = readType;
    }

    public final BuiltInAttribute getAttribute() {
        return attribute;
    }
    public final void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }
    public final ReadType getRead() {
        return read;
    }
    public final void setRead(ReadType read) {
        this.read = read;
    }
}
package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.deviceConfiguration.enumeration.Read.ReadType;

public class RfnChannelInput extends AttributeInput {
    private ReadType read;
    private boolean intervalApplicable;

    public RfnChannelInput() {
    }

    public RfnChannelInput(BuiltInAttribute attribute, ReadType readType) {
        super(attribute);
        this.read = readType;
        this.intervalApplicable = attribute.isIntervalApplicable();
    }

    public final ReadType getRead() {
        return read;
    }
    public final void setRead(ReadType read) {
        this.read = read;
    }

    public boolean isIntervalApplicable() {
        return intervalApplicable;
    }
}
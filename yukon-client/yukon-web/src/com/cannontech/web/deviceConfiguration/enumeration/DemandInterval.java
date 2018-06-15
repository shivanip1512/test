package com.cannontech.web.deviceConfiguration.enumeration;

public abstract class DemandInterval extends Interval {

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}
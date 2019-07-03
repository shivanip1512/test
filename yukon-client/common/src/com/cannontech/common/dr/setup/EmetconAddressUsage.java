package com.cannontech.common.dr.setup;

public enum EmetconAddressUsage {
    ADDRESS_USAGE_GOLD('G'), 
    ADDRESS_USAGE_SILVER('S');

    private final Character addressUsage;

    private EmetconAddressUsage(Character addressUsage) {
        this.addressUsage = addressUsage;
    }

    public Character getAddressUsage() {
        return addressUsage;
    }
}

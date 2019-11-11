package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableMap;

public enum AddressLevel {
    
    BRONZE("B"),
    LEAD("L"),
    MCT_ADDRESS("M");

    private String level;
    
    private final static ImmutableMap<String, AddressLevel> lookupByLevel;

    static {
        ImmutableMap.Builder<String, AddressLevel> addressBuilder = ImmutableMap.builder();
        for (AddressLevel addressLevel : values()) {
            addressBuilder.put(addressLevel.level, addressLevel);
        }
        lookupByLevel = addressBuilder.build();
    }

    AddressLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public static AddressLevel getForLevel(String value) {
        AddressLevel addressLevel = lookupByLevel.get(value);
        checkArgument(addressLevel != null, addressLevel);
        return addressLevel;
    }
}

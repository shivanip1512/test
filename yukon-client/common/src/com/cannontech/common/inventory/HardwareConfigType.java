package com.cannontech.common.inventory;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum HardwareConfigType {
    EXPRESSCOM(1),
    VERSACOM(2),
    SA205(3),
    SA305(4),
    SA_SIMPLE(5),
    SEP(6),
    ;

    private int hardwareConfigTypeId;

    private final static ImmutableMap<Integer, HardwareConfigType> lookupById;
    static {
        Builder<Integer, HardwareConfigType> idBuilder = ImmutableMap.builder();
        for (HardwareConfigType hardwareConfigType : values()) {
            idBuilder.put(hardwareConfigType.hardwareConfigTypeId, hardwareConfigType);
        }
        lookupById = idBuilder.build();
    }

    public static HardwareConfigType getForId(int hardwareConfigTypeId) throws IllegalArgumentException {
        HardwareConfigType deviceType = lookupById.get(hardwareConfigTypeId);
        Validate.notNull(deviceType, Integer.toString(hardwareConfigTypeId));
        return deviceType;
    }

    private HardwareConfigType(int hardwareConfigTypeId) {
        this.hardwareConfigTypeId = hardwareConfigTypeId;
    }

    public int getHardwareConfigTypeId() {
        return hardwareConfigTypeId;
    }
}

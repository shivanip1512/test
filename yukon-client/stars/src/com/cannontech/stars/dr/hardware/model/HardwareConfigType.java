package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.Validate;

import com.cannontech.stars.util.InventoryUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum HardwareConfigType {
    EXPRESSCOM(InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM),
    VERSACOM(InventoryUtils.HW_CONFIG_TYPE_VERSACOM),
    SA205(InventoryUtils.HW_CONFIG_TYPE_SA205),
    SA305(InventoryUtils.HW_CONFIG_TYPE_SA305),
    SA_SIMPLE(InventoryUtils.HW_CONFIG_TYPE_SA_SIMPLE),
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

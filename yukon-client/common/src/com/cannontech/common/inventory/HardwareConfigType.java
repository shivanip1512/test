package com.cannontech.common.inventory;

import org.apache.commons.lang.Validate;

import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum HardwareConfigType {
    EXPRESSCOM(1, PaoTag.DIRECT_PROGRAM_ENROLLMENT),
    VERSACOM(2, PaoTag.DIRECT_PROGRAM_ENROLLMENT),
    SA205(3, PaoTag.DIRECT_PROGRAM_ENROLLMENT),
    SA305(4, PaoTag.DIRECT_PROGRAM_ENROLLMENT),
    SA_SIMPLE(5, PaoTag.DIRECT_PROGRAM_ENROLLMENT),
    SEP(6, PaoTag.SEP_PROGRAM_ENROLLMENT),
    ;

    private int hardwareConfigTypeId;
    private PaoTag enrollmentTag;

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

    private HardwareConfigType(int hardwareConfigTypeId, PaoTag enrollmentTag) {
        this.hardwareConfigTypeId = hardwareConfigTypeId;
        this.enrollmentTag = enrollmentTag;
    }

    public int getHardwareConfigTypeId() {
        return hardwareConfigTypeId;
    }
    
    public PaoTag getEnrollmentTag() {
        return enrollmentTag;
    }
    
}
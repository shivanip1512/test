package com.cannontech.common.inventory;

import org.apache.commons.lang.Validate;

import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public enum HardwareConfigType {
    NOT_CONFIGURABLE(0, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    EXPRESSCOM(1, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    VERSACOM(2, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    SA205(3, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    SA305(4, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    SA_SIMPLE(5, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    SEP(6, PaoTag.SEP_PROGRAM_ENROLLMENT, false),
    ;

    private int hardwareConfigTypeId;
    private PaoTag enrollmentTag;
    private boolean supportsVirtualEnrollment;

    private final static ImmutableSet<HardwareConfigType> saTypes = ImmutableSet.of(SA_SIMPLE, SA205, SA305);
    private final static ImmutableSet<HardwareConfigType> supportsServiceInOut = ImmutableSet.of(EXPRESSCOM, VERSACOM);
    
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

    private HardwareConfigType(int hardwareConfigTypeId, PaoTag enrollmentTag, boolean supportsVirtualEnrollment) {
        this.hardwareConfigTypeId = hardwareConfigTypeId;
        this.enrollmentTag = enrollmentTag;
        this.supportsVirtualEnrollment = supportsVirtualEnrollment;
        
    }

    public int getHardwareConfigTypeId() {
        return hardwareConfigTypeId;
    }
    
    public PaoTag getEnrollmentTag() {
        return enrollmentTag;
    }
    
    public boolean isSupportsVirtualEnrollment() {
        return supportsVirtualEnrollment;
    }
    
    public boolean isSA() {
        return saTypes.contains(this);
    }
    
    public boolean isSupportsServiceInOut() {
        return supportsServiceInOut.contains(this);
    }
    
}
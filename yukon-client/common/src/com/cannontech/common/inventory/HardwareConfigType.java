package com.cannontech.common.inventory;

import org.apache.commons.lang3.Validate;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public enum HardwareConfigType implements DisplayableEnum {
    
    NOT_CONFIGURABLE(0, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    EXPRESSCOM(1, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true) {
        /**
         * Must be a valid integer, fit in DeviceCarrierSettings.Address (varchar(18)) and be less than
         * 2147483647
         */
        @Override
        public boolean isSerialNumberValid(String serialNumber) {
            try {
                Integer.parseInt(serialNumber);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    },
    VERSACOM(2, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    SA205(3, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    SA305(4, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    SA_SIMPLE(5, PaoTag.DIRECT_PROGRAM_ENROLLMENT, true),
    SEP(6, PaoTag.SEP_PROGRAM_ENROLLMENT, false),
    ECOBEE(7, PaoTag.ECOBEE_PROGRAM_ENROLLMENT, false),
    HONEYWELL(8, PaoTag.HONEYWELL_PROGRAM_ENROLLMENT, false),
    NEST(9, PaoTag.NEST_PROGRAM_ENROLLMENT, false),
    ITRON(10, PaoTag.ITRON_PROGRAM_ENROLLMENT, false),
    DISCONNECT_METER(11, PaoTag.METER_DISCONNECT_PROGRAM_ENROLLMENT, false)
    ;
    
    private int hardwareConfigTypeId;
    private PaoTag enrollmentTag;
    private boolean supportsVirtualEnrollment;
    private final static String keyPrefix = "yukon.web.dr.consumer.hardwareConfigType.";
    
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
    
    /**
     * Override this method to change the serial number validation for a specific enum
     * Default is an string value is valid. EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION is expected
     * to be validated against prior to this call.
     */
    public boolean isSerialNumberValid(String serialNumber) {
        return true;
    };
    
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
    
    @Override 
    public String getFormatKey() {
        return keyPrefix + name();
    }
    
    public String getValidationErrorKey() {
        return getFormatKey() + ".invalidSerialNumber";
    }
    
    public boolean isHasTamperDetect() {
        return this.isSA();
    }
    
    public boolean isHasProgramSplinter() {
        return this == EXPRESSCOM;
    }
    
    public int getNumRelays() {
        return this == ECOBEE || this == NEST || this == HONEYWELL ? 1 :
               this == ITRON || this == EXPRESSCOM ? 8 : 4;
    }
    
}
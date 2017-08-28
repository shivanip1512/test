
package com.cannontech.dr.rfn.tlv;

import com.google.common.collect.ImmutableMap;

/**
 * Holds Type ID information
 */
public enum FieldType {

    TLV_COUNT(0),
    UTC(1),
    SERIAL_NUMBER(2),
    SSPEC(3),
    FIRMWARE_VERSION(4),
    SPID(5),
    GEO_ADDRESS(6),
    FEEDER_ADDRESS(7),
    ZIP_ADDRESS(8),
    UDA_ADDRESS(9),
    REQUIRED_ADDRESS(10),
    SUBSTATION_ADDRESS(11),
    BLINK_COUNT(12),
    RELAY_INTERVAL_START_TIME(13),
    RELAY_N_RUNTIME(14),
    RELAY_N_SHEDTIME(15),
    RELAY_N_PROGRAM_ADDRESS(16),
    RELAY_N_SPLINTER_ADDRESS(17),
    RELAY_N_REMAINING_CONTROLTIME(18),
    PROTECTION_TIME_RELAY_N(19),
    CLP_TIME_FOR_RELAY_N(20),
    CLP_TIME_REMAINING_FOR_RELAY_N(21),
    BROADCAST_VERIFICATION_MESSAGES(22),
    LUF_EVENTS(23),
    LUV_EVENTS(24),
    SERVICE_STATE(25),
    CONTROL_STATE(26);

    private final Integer type;

    FieldType(Integer type) {
        this.type = type;
    }

    private static final ImmutableMap<Integer, FieldType> fieldTypes;

    static {
        final ImmutableMap.Builder<Integer, FieldType> builder = ImmutableMap.builder();
        for (FieldType fieldType : values()) {
            builder.put(fieldType.type, fieldType);
        }
        fieldTypes = builder.build();
    }

    public static boolean isFieldTypeSupported(Integer type) {
        return fieldTypes.containsKey(type);
    }

    public static FieldType getFieldType(Integer type) {
        return fieldTypes.get(type);
    }

}

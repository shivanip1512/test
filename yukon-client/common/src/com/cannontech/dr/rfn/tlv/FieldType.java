
package com.cannontech.dr.rfn.tlv;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Holds Type ID information
 * Enum represents name of the field and holds Identifier value and is used to get field value
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
    private final static ImmutableSet<FieldType> addressingFieldTypes;

    static {
        final ImmutableMap.Builder<Integer, FieldType> builder = ImmutableMap.builder();
        for (FieldType fieldType : values()) {
            builder.put(fieldType.type, fieldType);
        }
        fieldTypes = builder.build();
        addressingFieldTypes = ImmutableSet.of(SPID, GEO_ADDRESS, FEEDER_ADDRESS, ZIP_ADDRESS, UDA_ADDRESS,
            REQUIRED_ADDRESS, SUBSTATION_ADDRESS, RELAY_N_PROGRAM_ADDRESS, RELAY_N_SPLINTER_ADDRESS);
    }

    public static boolean isFieldTypeSupported(Integer type) {
        return fieldTypes.containsKey(type);
    }

    public static FieldType getFieldType(Integer type) {
        return fieldTypes.get(type);
    }

    public static ImmutableSet<FieldType> getAddressingFieldTypes() {
        return addressingFieldTypes;
    }

}

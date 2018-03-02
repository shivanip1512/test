
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
    CONTROL_STATE(26),
    LOV_TRIGGER(68),
    LOV_RESTORE(69),
    LOV_TRIGGER_TIME(70),
    LOV_RESTORE_TIME(71),
    LOV_EVENT_COUNT(72),
    LOF_TRIGGER(73),
    LOF_RESTORE(74),
    LOF_TRIGGER_TIME(75),
    LOF_RESTORE_TIME(76),
    LOF_EVENT_COUNT(77),
    LOF_START_RANDOM_TIME(78),
    LOF_END_RANDOM_TIME(79),
    LOV_START_RANDOM_TIME(80),
    LOV_END_RANDOM_TIME(81),
    LOF_MIN_EVENT_DURATION(82),
    LOV_MIN_EVENT_DURATION(83),
    LOF_MAX_EVENT_DURATION(84),
    LOV_MAX_EVENT_DURATION(85),
    MINIMUM_EVENT_SEPARATION(86),
    POWER_QUALITY_RESPONSE_LOG_BLOB(87),
    POWER_QUALITY_RESPONSE_ENABLED(88)
    ;

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

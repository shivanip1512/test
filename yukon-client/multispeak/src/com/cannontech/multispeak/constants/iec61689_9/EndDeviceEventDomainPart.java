package com.cannontech.multispeak.constants.iec61689_9;

/**
 * Codes as described by the standard "IEC 61968-9 – Interface Standard for Meter Reading &
 * Control [MR] [Published]".
 * 
 * http://en.wikipedia.org/wiki/IEC_61968
 */
public enum EndDeviceEventDomainPart {
    ACCESS("1"),
    BATTERY("2"),
    CARTRIDGE("3"),
    CIRCUIT("4"),
    CONTROL("5"),
    CURRENT("6"),
    CUSTOMER_INTERFACE("7"),
    DEMAND("8"),
    DER_SWITCH("9"),
    ENCLOSURE("10"),
    FIRMWARE("11"),
    FREQUENCY("12"),
    GAS_METROLOGY("13"),
    GATEWAY_DEVICE("14"),
    LOAD_CONTROL("15"),
    LOAD_PROFILE("16"),
    LOGS("17"),
    MEMORY("18"),
    MESSAGING("19"),
    METER_ASSET("20"),
    METROLOGY("21"),
    MODE("22"),
    NETWORK_INTERFACE("23"),
    PASSWORD("24"),
    PHASE("25"),
    POWER("26"),
    POWER_FACTOR("27"),
    POWER_QUALITY("28"),
    PRESSURE("29"),
    PROTECTIVE_DEVICE("30"),
    RCD_SWITCH("31"),
    SECURITY_KEY("32"),
    TAMPER("33"),
    TARIFF("34"),
    TEMPERATURE("35"),
    TIME("36"),
    TRANSFORMER_DEVICE("37"),
    VOLTAGE("38"),
    ;

    public final String code;

    EndDeviceEventDomainPart(String code) {
        this.code = code;
    }
}

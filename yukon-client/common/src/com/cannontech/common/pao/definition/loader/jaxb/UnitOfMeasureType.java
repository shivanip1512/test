
package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for unitOfMeasureType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="unitOfMeasureType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="KW"/>
 *     &lt;enumeration value="KWH"/>
 *     &lt;enumeration value="KVA"/>
 *     &lt;enumeration value="KVAR"/>
 *     &lt;enumeration value="KVAH"/>
 *     &lt;enumeration value="KVARH"/>
 *     &lt;enumeration value="KVOLTS"/>
 *     &lt;enumeration value="KQ"/>
 *     &lt;enumeration value="AMPS"/>
 *     &lt;enumeration value="COUNTS"/>
 *     &lt;enumeration value="DEGREES"/>
 *     &lt;enumeration value="DOLLARS"/>
 *     &lt;enumeration value="DOLLAR_CHAR"/>
 *     &lt;enumeration value="FEET"/>
 *     &lt;enumeration value="GALLONS"/>
 *     &lt;enumeration value="GAL_PM"/>
 *     &lt;enumeration value="GAS_CFT"/>
 *     &lt;enumeration value="HOURS"/>
 *     &lt;enumeration value="LEVELS"/>
 *     &lt;enumeration value="MINUTES"/>
 *     &lt;enumeration value="MW"/>
 *     &lt;enumeration value="MWH"/>
 *     &lt;enumeration value="MVA"/>
 *     &lt;enumeration value="MVAR"/>
 *     &lt;enumeration value="MVAH"/>
 *     &lt;enumeration value="MVARH"/>
 *     &lt;enumeration value="OPS"/>
 *     &lt;enumeration value="PF"/>
 *     &lt;enumeration value="PERCENT"/>
 *     &lt;enumeration value="PERCENT_CHAR"/>
 *     &lt;enumeration value="PSI"/>
 *     &lt;enumeration value="SECONDS"/>
 *     &lt;enumeration value="TEMP_F"/>
 *     &lt;enumeration value="TEMP_C"/>
 *     &lt;enumeration value="VARS"/>
 *     &lt;enumeration value="VOLTS"/>
 *     &lt;enumeration value="VOLTAMPS"/>
 *     &lt;enumeration value="VA"/>
 *     &lt;enumeration value="CUBIC_FEET"/>
 *     &lt;enumeration value="WATTS"/>
 *     &lt;enumeration value="HZ"/>
 *     &lt;enumeration value="VOLTS_V2H"/>
 *     &lt;enumeration value="AMPS_V2H"/>
 *     &lt;enumeration value="TAP"/>
 *     &lt;enumeration value="MILES"/>
 *     &lt;enumeration value="MS"/>
 *     &lt;enumeration value="PPM"/>
 *     &lt;enumeration value="MPH"/>
 *     &lt;enumeration value="INCHES"/>
 *     &lt;enumeration value="MILIBARS"/>
 *     &lt;enumeration value="KH_H"/>
 *     &lt;enumeration value="M_S"/>
 *     &lt;enumeration value="KV"/>
 *     &lt;enumeration value="UNDEF"/>
 *     &lt;enumeration value="CUBIC_METERS"/>
 *     &lt;enumeration value="MEGABYTES"/>
 *     &lt;enumeration value="DBM"/>
 *     &lt;enumeration value="THERMS"/>
 *     &lt;enumeration value="DB"/>
 *     &lt;enumeration value="CCF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "unitOfMeasureType")
@XmlEnum
public enum UnitOfMeasureType {

    KW("KW"),
    KWH("KWH"),
    KVA("KVA"),
    KVAR("KVAR"),
    KVAH("KVAH"),
    KVARH("KVARH"),
    KVOLTS("KVOLTS"),
    KQ("KQ"),
    AMPS("AMPS"),
    COUNTS("COUNTS"),
    DEGREES("DEGREES"),
    DOLLARS("DOLLARS"),
    DOLLAR_CHAR("DOLLAR_CHAR"),
    FEET("FEET"),
    GALLONS("GALLONS"),
    GAL_PM("GAL_PM"),
    GAS_CFT("GAS_CFT"),
    HOURS("HOURS"),
    LEVELS("LEVELS"),
    MINUTES("MINUTES"),
    MW("MW"),
    MWH("MWH"),
    MVA("MVA"),
    MVAR("MVAR"),
    MVAH("MVAH"),
    MVARH("MVARH"),
    OPS("OPS"),
    PF("PF"),
    PERCENT("PERCENT"),
    PERCENT_CHAR("PERCENT_CHAR"),
    PSI("PSI"),
    SECONDS("SECONDS"),
    TEMP_F("TEMP_F"),
    TEMP_C("TEMP_C"),
    VARS("VARS"),
    VOLTS("VOLTS"),
    VOLTAMPS("VOLTAMPS"),
    VA("VA"),
    CUBIC_FEET("CUBIC_FEET"),
    WATTS("WATTS"),
    HZ("HZ"),
    @XmlEnumValue("VOLTS_V2H")
    VOLTS_V_2_H("VOLTS_V2H"),
    @XmlEnumValue("AMPS_V2H")
    AMPS_V_2_H("AMPS_V2H"),
    TAP("TAP"),
    MILES("MILES"),
    MS("MS"),
    PPM("PPM"),
    MPH("MPH"),
    INCHES("INCHES"),
    MILIBARS("MILIBARS"),
    KH_H("KH_H"),
    M_S("M_S"),
    KV("KV"),
    UNDEF("UNDEF"),
    CUBIC_METERS("CUBIC_METERS"),
    MEGABYTES("MEGABYTES"),
    DBM("DBM"),
    THERMS("THERMS"),
    DB("DB"),
    CCF("CCF");
    private final String value;

    UnitOfMeasureType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UnitOfMeasureType fromValue(String v) {
        for (UnitOfMeasureType c: UnitOfMeasureType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.06.11 at 07:40:15 AM CDT 
//


package com.cannontech.common.device.config.model.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CategoryType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CategoryType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="dst"/>
 *     &lt;enumeration value="meterParameters"/>
 *     &lt;enumeration value="displayItems"/>
 *     &lt;enumeration value="mct440DemandLoadProfile"/>
 *     &lt;enumeration value="mct440Configuration"/>
 *     &lt;enumeration value="mct440Addressing"/>
 *     &lt;enumeration value="mct440PhaseLoss"/>
 *     &lt;enumeration value="mct440Tou"/>
 *     &lt;enumeration value="dnp"/>
 *     &lt;enumeration value="mct470DemandLoadProfile"/>
 *     &lt;enumeration value="mct470LoadProfileChannels"/>
 *     &lt;enumeration value="mct470ConfigurationByte"/>
 *     &lt;enumeration value="addressing"/>
 *     &lt;enumeration value="relays"/>
 *     &lt;enumeration value="mct470PrecannedTable"/>
 *     &lt;enumeration value="tou"/>
 *     &lt;enumeration value="mct430DemandLoadProfile"/>
 *     &lt;enumeration value="mct430LoadProfileChannels"/>
 *     &lt;enumeration value="mct430ConfigurationByte"/>
 *     &lt;enumeration value="mct430PrecannedTable"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CategoryType")
@XmlEnum
public enum CategoryType {

    @XmlEnumValue("dst")
    DST("dst"),
    @XmlEnumValue("meterParameters")
    METER_PARAMETERS("meterParameters"),
    @XmlEnumValue("displayItems")
    DISPLAY_ITEMS("displayItems"),
    @XmlEnumValue("mct440DemandLoadProfile")
    MCT_440_DEMAND_LOAD_PROFILE("mct440DemandLoadProfile"),
    @XmlEnumValue("mct440Configuration")
    MCT_440_CONFIGURATION("mct440Configuration"),
    @XmlEnumValue("mct440Addressing")
    MCT_440_ADDRESSING("mct440Addressing"),
    @XmlEnumValue("mct440PhaseLoss")
    MCT_440_PHASE_LOSS("mct440PhaseLoss"),
    @XmlEnumValue("mct440Tou")
    MCT_440_TOU("mct440Tou"),
    @XmlEnumValue("dnp")
    DNP("dnp"),
    @XmlEnumValue("mct470DemandLoadProfile")
    MCT_470_DEMAND_LOAD_PROFILE("mct470DemandLoadProfile"),
    @XmlEnumValue("mct470LoadProfileChannels")
    MCT_470_LOAD_PROFILE_CHANNELS("mct470LoadProfileChannels"),
    @XmlEnumValue("mct470ConfigurationByte")
    MCT_470_CONFIGURATION_BYTE("mct470ConfigurationByte"),
    @XmlEnumValue("addressing")
    ADDRESSING("addressing"),
    @XmlEnumValue("relays")
    RELAYS("relays"),
    @XmlEnumValue("mct470PrecannedTable")
    MCT_470_PRECANNED_TABLE("mct470PrecannedTable"),
    @XmlEnumValue("tou")
    TOU("tou"),
    @XmlEnumValue("mct430DemandLoadProfile")
    MCT_430_DEMAND_LOAD_PROFILE("mct430DemandLoadProfile"),
    @XmlEnumValue("mct430LoadProfileChannels")
    MCT_430_LOAD_PROFILE_CHANNELS("mct430LoadProfileChannels"),
    @XmlEnumValue("mct430ConfigurationByte")
    MCT_430_CONFIGURATION_BYTE("mct430ConfigurationByte"),
    @XmlEnumValue("mct430PrecannedTable")
    MCT_430_PRECANNED_TABLE("mct430PrecannedTable");
    private final String value;

    CategoryType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CategoryType fromValue(String v) {
        for (CategoryType c: CategoryType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

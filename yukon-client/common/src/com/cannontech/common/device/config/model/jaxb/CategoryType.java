
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
 *     &lt;enumeration value="timeZone"/>
 *     &lt;enumeration value="meterParameters"/>
 *     &lt;enumeration value="centronDisplayItems"/>
 *     &lt;enumeration value="focusAlDisplay"/>
 *     &lt;enumeration value="mct440Configuration"/>
 *     &lt;enumeration value="mct440Addressing"/>
 *     &lt;enumeration value="mct440PhaseLoss"/>
 *     &lt;enumeration value="mct440Tou"/>
 *     &lt;enumeration value="dnp"/>
 *     &lt;enumeration value="demand"/>
 *     &lt;enumeration value="profile"/>
 *     &lt;enumeration value="demandFreeze"/>
 *     &lt;enumeration value="mct470ProfileChannels"/>
 *     &lt;enumeration value="mct470ConfigurationByte"/>
 *     &lt;enumeration value="rfnChannelConfiguration"/>
 *     &lt;enumeration value="addressing"/>
 *     &lt;enumeration value="relays"/>
 *     &lt;enumeration value="mct470PrecannedTable"/>
 *     &lt;enumeration value="tou"/>
 *     &lt;enumeration value="mct430ProfileChannels"/>
 *     &lt;enumeration value="mct430ConfigurationByte"/>
 *     &lt;enumeration value="mct430PrecannedTable"/>
 *     &lt;enumeration value="rfnDisconnectConfiguration"/>
 *     &lt;enumeration value="mctDisconnectConfiguration"/>
 *     &lt;enumeration value="rfnOvUv"/>
 *     &lt;enumeration value="rfnTempAlarm"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CategoryType")
@XmlEnum
public enum CategoryType {

    @XmlEnumValue("timeZone")
    TIME_ZONE("timeZone"),
    @XmlEnumValue("meterParameters")
    METER_PARAMETERS("meterParameters"),
    @XmlEnumValue("centronDisplayItems")
    CENTRON_DISPLAY_ITEMS("centronDisplayItems"),
    @XmlEnumValue("focusAlDisplay")
    FOCUS_AL_DISPLAY("focusAlDisplay"),
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
    @XmlEnumValue("demand")
    DEMAND("demand"),
    @XmlEnumValue("profile")
    PROFILE("profile"),
    @XmlEnumValue("demandFreeze")
    DEMAND_FREEZE("demandFreeze"),
    @XmlEnumValue("mct470ProfileChannels")
    MCT_470_PROFILE_CHANNELS("mct470ProfileChannels"),
    @XmlEnumValue("mct470ConfigurationByte")
    MCT_470_CONFIGURATION_BYTE("mct470ConfigurationByte"),
    @XmlEnumValue("rfnChannelConfiguration")
    RFN_CHANNEL_CONFIGURATION("rfnChannelConfiguration"),
    @XmlEnumValue("addressing")
    ADDRESSING("addressing"),
    @XmlEnumValue("relays")
    RELAYS("relays"),
    @XmlEnumValue("mct470PrecannedTable")
    MCT_470_PRECANNED_TABLE("mct470PrecannedTable"),
    @XmlEnumValue("tou")
    TOU("tou"),
    @XmlEnumValue("mct430ProfileChannels")
    MCT_430_PROFILE_CHANNELS("mct430ProfileChannels"),
    @XmlEnumValue("mct430ConfigurationByte")
    MCT_430_CONFIGURATION_BYTE("mct430ConfigurationByte"),
    @XmlEnumValue("mct430PrecannedTable")
    MCT_430_PRECANNED_TABLE("mct430PrecannedTable"),
    @XmlEnumValue("rfnDisconnectConfiguration")
    RFN_DISCONNECT_CONFIGURATION("rfnDisconnectConfiguration"),
    @XmlEnumValue("mctDisconnectConfiguration")
    MCT_DISCONNECT_CONFIGURATION("mctDisconnectConfiguration"),
    @XmlEnumValue("rfnOvUv")
    RFN_OV_UV("rfnOvUv"),
    @XmlEnumValue("rfnTempAlarm")
    RFN_TEMP_ALARM("rfnTempAlarm");
    private final String value;

    CategoryType(String v) {
        value = v;
    }

    /**
     * CategoryType as stored in DB
     */
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

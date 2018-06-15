
package com.cannontech.common.pao.definition.loader.jaxb;

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
 *     &lt;enumeration value="lcdConfiguration"/>
 *     &lt;enumeration value="centron410DisplayItems"/>
 *     &lt;enumeration value="centron420DisplayItems"/>
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
 *     &lt;enumeration value="rfnWaterChannelConfiguration"/>
 *     &lt;enumeration value="rfnGasChannelConfiguration"/>
 *     &lt;enumeration value="addressing"/>
 *     &lt;enumeration value="relays"/>
 *     &lt;enumeration value="mct470PrecannedTable"/>
 *     &lt;enumeration value="tou"/>
 *     &lt;enumeration value="mct430ProfileChannels"/>
 *     &lt;enumeration value="mct430ConfigurationByte"/>
 *     &lt;enumeration value="mct430PrecannedTable"/>
 *     &lt;enumeration value="rfnDisconnectConfiguration"/>
 *     &lt;enumeration value="mctDisconnectConfiguration"/>
 *     &lt;enumeration value="rfnDemand"/>
 *     &lt;enumeration value="rfnVoltage"/>
 *     &lt;enumeration value="rfnTempAlarm"/>
 *     &lt;enumeration value="regulatorCategory"/>
 *     &lt;enumeration value="regulatorHeartbeat"/>
 *     &lt;enumeration value="cbcHeartbeat"/>
 *     &lt;enumeration value="cbcAttributeMapping"/>
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
    @XmlEnumValue("lcdConfiguration")
    LCD_CONFIGURATION("lcdConfiguration"),
    @XmlEnumValue("centron410DisplayItems")
    CENTRON_410_DISPLAY_ITEMS("centron410DisplayItems"),
    @XmlEnumValue("centron420DisplayItems")
    CENTRON_420_DISPLAY_ITEMS("centron420DisplayItems"),
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
    @XmlEnumValue("rfnWaterChannelConfiguration")
    RFN_WATER_CHANNEL_CONFIGURATION("rfnWaterChannelConfiguration"),
    @XmlEnumValue("rfnGasChannelConfiguration")
    RFN_GAS_CHANNEL_CONFIGURATION("rfnGasChannelConfiguration"),
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
    @XmlEnumValue("rfnDemand")
    RFN_DEMAND("rfnDemand"),
    @XmlEnumValue("rfnVoltage")
    RFN_VOLTAGE("rfnVoltage"),
    @XmlEnumValue("rfnTempAlarm")
    RFN_TEMP_ALARM("rfnTempAlarm"),
    @XmlEnumValue("regulatorCategory")
    REGULATOR_CATEGORY("regulatorCategory"),
    @XmlEnumValue("regulatorHeartbeat")
    REGULATOR_HEARTBEAT("regulatorHeartbeat"),
    @XmlEnumValue("cbcHeartbeat")
    CBC_HEARTBEAT("cbcHeartbeat"),
    @XmlEnumValue("cbcAttributeMapping")
    CBC_ATTRIBUTE_MAPPING("cbcAttributeMapping");
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

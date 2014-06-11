
package com.cannontech.common.device.config.model.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnumOption.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EnumOption">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TimeZoneInput"/>
 *     &lt;enumeration value="CentronDisplayItem"/>
 *     &lt;enumeration value="FocusAlDisplayItem"/>
 *     &lt;enumeration value="Interval"/>
 *     &lt;enumeration value="Rate"/>
 *     &lt;enumeration value="Schedule"/>
 *     &lt;enumeration value="Mct470MeterType"/>
 *     &lt;enumeration value="Mct430MeterType"/>
 *     &lt;enumeration value="PhysicalChannel"/>
 *     &lt;enumeration value="ProfileResolution"/>
 *     &lt;enumeration value="DemandResolution"/>
 *     &lt;enumeration value="LastIntervalDemandResolution"/>
 *     &lt;enumeration value="ElectronicMeter"/>
 *     &lt;enumeration value="RelayTiming"/>
 *     &lt;enumeration value="TableReadInterval"/>
 *     &lt;enumeration value="DisconnectLoadLimitConnectDelay"/>
 *     &lt;enumeration value="DisconnectMode"/>
 *     &lt;enumeration value="DisconnectDemandInterval"/>
 *     &lt;enumeration value="ReconnectParameter"/>
 *     &lt;enumeration value="ChannelType"/>
 *     &lt;enumeration value="ReadType"/>
 *     &lt;enumeration value="RecordingInterval"/>
 *     &lt;enumeration value="ReportingInterval"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EnumOption")
@XmlEnum
public enum EnumOption {

    @XmlEnumValue("TimeZoneInput")
    TIME_ZONE_INPUT("TimeZoneInput"),
    @XmlEnumValue("CentronDisplayItem")
    CENTRON_DISPLAY_ITEM("CentronDisplayItem"),
    @XmlEnumValue("FocusAlDisplayItem")
    FOCUS_AL_DISPLAY_ITEM("FocusAlDisplayItem"),
    @XmlEnumValue("Interval")
    INTERVAL("Interval"),
    @XmlEnumValue("Rate")
    RATE("Rate"),
    @XmlEnumValue("Schedule")
    SCHEDULE("Schedule"),
    @XmlEnumValue("Mct470MeterType")
    MCT_470_METER_TYPE("Mct470MeterType"),
    @XmlEnumValue("Mct430MeterType")
    MCT_430_METER_TYPE("Mct430MeterType"),
    @XmlEnumValue("PhysicalChannel")
    PHYSICAL_CHANNEL("PhysicalChannel"),
    @XmlEnumValue("ProfileResolution")
    PROFILE_RESOLUTION("ProfileResolution"),
    @XmlEnumValue("DemandResolution")
    DEMAND_RESOLUTION("DemandResolution"),
    @XmlEnumValue("LastIntervalDemandResolution")
    LAST_INTERVAL_DEMAND_RESOLUTION("LastIntervalDemandResolution"),
    @XmlEnumValue("ElectronicMeter")
    ELECTRONIC_METER("ElectronicMeter"),
    @XmlEnumValue("RelayTiming")
    RELAY_TIMING("RelayTiming"),
    @XmlEnumValue("TableReadInterval")
    TABLE_READ_INTERVAL("TableReadInterval"),
    @XmlEnumValue("DisconnectLoadLimitConnectDelay")
    DISCONNECT_LOAD_LIMIT_CONNECT_DELAY("DisconnectLoadLimitConnectDelay"),
    @XmlEnumValue("DisconnectMode")
    DISCONNECT_MODE("DisconnectMode"),
    @XmlEnumValue("DisconnectDemandInterval")
    DISCONNECT_DEMAND_INTERVAL("DisconnectDemandInterval"),
    @XmlEnumValue("ReconnectParameter")
    RECONNECT_PARAMETER("ReconnectParameter"),
    @XmlEnumValue("ChannelType")
    CHANNEL_TYPE("ChannelType"),
    @XmlEnumValue("ReadType")
    READ_TYPE("ReadType"),
    @XmlEnumValue("RecordingInterval")
    RECORDING_INTERVAL("RecordingInterval"),
    @XmlEnumValue("ReportingInterval")
    REPORTING_INTERVAL("ReportingInterval");
    private final String value;

    EnumOption(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumOption fromValue(String v) {
        for (EnumOption c: EnumOption.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

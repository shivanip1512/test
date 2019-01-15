
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeviceTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DeviceTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Range Extender"/>
 *     &lt;enumeration value="Metering Device"/>
 *     &lt;enumeration value="In-Premise Display"/>
 *     &lt;enumeration value="Load Control Device"/>
 *     &lt;enumeration value="Smart Appliance"/>
 *     &lt;enumeration value="PCT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DeviceTypeEnumeration")
@XmlEnum
public enum DeviceTypeEnumeration {

    @XmlEnumValue("Range Extender")
    RANGE_EXTENDER("Range Extender"),
    @XmlEnumValue("Metering Device")
    METERING_DEVICE("Metering Device"),
    @XmlEnumValue("In-Premise Display")
    IN_PREMISE_DISPLAY("In-Premise Display"),
    @XmlEnumValue("Load Control Device")
    LOAD_CONTROL_DEVICE("Load Control Device"),
    @XmlEnumValue("Smart Appliance")
    SMART_APPLIANCE("Smart Appliance"),
    PCT("PCT");
    private final String value;

    DeviceTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeviceTypeEnumeration fromValue(String v) {
        for (DeviceTypeEnumeration c: DeviceTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

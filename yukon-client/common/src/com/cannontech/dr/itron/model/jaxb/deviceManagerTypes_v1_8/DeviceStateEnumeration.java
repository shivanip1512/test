
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeviceStateEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DeviceStateEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="READY"/>
 *     &lt;enumeration value="NOT_READY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DeviceStateEnumeration")
@XmlEnum
public enum DeviceStateEnumeration {

    READY,
    NOT_READY;

    public String value() {
        return name();
    }

    public static DeviceStateEnumeration fromValue(String v) {
        return valueOf(v);
    }

}


package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeviceGroupType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DeviceGroupType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DYNAMIC_GROUP"/>
 *     &lt;enumeration value="STATIC_GROUP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DeviceGroupType")
@XmlEnum
public enum DeviceGroupType {

    DYNAMIC_GROUP,
    STATIC_GROUP;

    public String value() {
        return name();
    }

    public static DeviceGroupType fromValue(String v) {
        return valueOf(v);
    }

}

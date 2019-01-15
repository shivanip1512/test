
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HANDevicesErrorEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HANDevicesErrorEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ERROR_MACID_NOT_FOUND"/>
 *     &lt;enumeration value="ERROR_MACID_INVALID"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HANDevicesErrorEnumeration")
@XmlEnum
public enum HANDevicesErrorEnumeration {

    ERROR_MACID_NOT_FOUND,
    ERROR_MACID_INVALID;

    public String value() {
        return name();
    }

    public static HANDevicesErrorEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

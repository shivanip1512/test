
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ESIHANRadioStateEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ESIHANRadioStateEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ENABLED"/>
 *     &lt;enumeration value="DISABLED"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ESIHANRadioStateEnumeration")
@XmlEnum
public enum ESIHANRadioStateEnumeration {

    ENABLED,
    DISABLED,
    UNKNOWN;

    public String value() {
        return name();
    }

    public static ESIHANRadioStateEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

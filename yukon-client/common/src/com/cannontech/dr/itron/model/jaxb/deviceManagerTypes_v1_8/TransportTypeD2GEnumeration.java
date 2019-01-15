
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransportTypeD2GEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransportTypeD2GEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="D2G"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransportTypeD2GEnumeration")
@XmlEnum
public enum TransportTypeD2GEnumeration {

    @XmlEnumValue("D2G")
    D_2_G("D2G");
    private final String value;

    TransportTypeD2GEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransportTypeD2GEnumeration fromValue(String v) {
        for (TransportTypeD2GEnumeration c: TransportTypeD2GEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

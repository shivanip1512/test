
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransportTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransportTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ZIGBEE"/>
 *     &lt;enumeration value="D2G"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransportTypeEnumeration")
@XmlEnum
public enum TransportTypeEnumeration {

    ZIGBEE("ZIGBEE"),
    @XmlEnumValue("D2G")
    D_2_G("D2G");
    private final String value;

    TransportTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransportTypeEnumeration fromValue(String v) {
        for (TransportTypeEnumeration c: TransportTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

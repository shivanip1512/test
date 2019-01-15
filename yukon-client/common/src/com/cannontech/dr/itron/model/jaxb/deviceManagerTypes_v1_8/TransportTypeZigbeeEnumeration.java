
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransportTypeZigbeeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransportTypeZigbeeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ZIGBEE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransportTypeZigbeeEnumeration")
@XmlEnum
public enum TransportTypeZigbeeEnumeration {

    ZIGBEE;

    public String value() {
        return name();
    }

    public static TransportTypeZigbeeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

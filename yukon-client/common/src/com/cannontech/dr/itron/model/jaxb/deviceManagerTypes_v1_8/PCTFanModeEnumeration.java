
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PCTFanModeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PCTFanModeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AUTO"/>
 *     &lt;enumeration value="ON"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PCTFanModeEnumeration")
@XmlEnum
public enum PCTFanModeEnumeration {

    AUTO,
    ON;

    public String value() {
        return name();
    }

    public static PCTFanModeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

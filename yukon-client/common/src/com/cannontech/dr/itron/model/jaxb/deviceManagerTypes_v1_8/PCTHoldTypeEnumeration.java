
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PCTHoldTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PCTHoldTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OFF"/>
 *     &lt;enumeration value="PERMANENT"/>
 *     &lt;enumeration value="TEMPORARY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PCTHoldTypeEnumeration")
@XmlEnum
public enum PCTHoldTypeEnumeration {

    OFF,
    PERMANENT,
    TEMPORARY;

    public String value() {
        return name();
    }

    public static PCTHoldTypeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

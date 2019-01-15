
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PCTModeTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PCTModeTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OFF"/>
 *     &lt;enumeration value="AUTO"/>
 *     &lt;enumeration value="HEAT"/>
 *     &lt;enumeration value="COOL"/>
 *     &lt;enumeration value="EMERGENCY_HEAT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PCTModeTypeEnumeration")
@XmlEnum
public enum PCTModeTypeEnumeration {

    OFF,
    AUTO,
    HEAT,
    COOL,
    EMERGENCY_HEAT;

    public String value() {
        return name();
    }

    public static PCTModeTypeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}


package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ILCSupportedTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ILCSupportedTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADVANCED_OPTION_1"/>
 *     &lt;enumeration value="ADVANCED_OPTION_2"/>
 *     &lt;enumeration value="ADVANCED_OPTION_3"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ILCSupportedTypeEnumeration")
@XmlEnum
public enum ILCSupportedTypeEnumeration {

    ADVANCED_OPTION_1,
    ADVANCED_OPTION_2,
    ADVANCED_OPTION_3;

    public String value() {
        return name();
    }

    public static ILCSupportedTypeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

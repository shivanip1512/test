
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OwnerTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OwnerTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UTILITY"/>
 *     &lt;enumeration value="CUSTOMER"/>
 *     &lt;enumeration value="OTHER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OwnerTypeEnumeration")
@XmlEnum
public enum OwnerTypeEnumeration {

    UTILITY,
    CUSTOMER,
    OTHER;

    public String value() {
        return name();
    }

    public static OwnerTypeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

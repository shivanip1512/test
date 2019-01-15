
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ServiceTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ELECTRIC"/>
 *     &lt;enumeration value="GAS"/>
 *     &lt;enumeration value="WATER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ServiceTypeEnumeration")
@XmlEnum
public enum ServiceTypeEnumeration {

    ELECTRIC,
    GAS,
    WATER;

    public String value() {
        return name();
    }

    public static ServiceTypeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

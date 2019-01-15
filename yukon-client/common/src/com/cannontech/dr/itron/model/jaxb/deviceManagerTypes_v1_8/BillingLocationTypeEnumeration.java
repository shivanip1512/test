
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BillingLocationTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BillingLocationTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RESIDENTIAL"/>
 *     &lt;enumeration value="COMMERCIAL"/>
 *     &lt;enumeration value="INDUSTRIAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BillingLocationTypeEnumeration")
@XmlEnum
public enum BillingLocationTypeEnumeration {

    RESIDENTIAL,
    COMMERCIAL,
    INDUSTRIAL;

    public String value() {
        return name();
    }

    public static BillingLocationTypeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

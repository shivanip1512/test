
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConnectivityStatusEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ConnectivityStatusEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="JOINED"/>
 *     &lt;enumeration value="UNJOINED"/>
 *     &lt;enumeration value="UNASSOCIATED"/>
 *     &lt;enumeration value="ASSOCIATED"/>
 *     &lt;enumeration value="UNREACHABLE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ConnectivityStatusEnumeration")
@XmlEnum
public enum ConnectivityStatusEnumeration {

    JOINED,
    UNJOINED,
    UNASSOCIATED,
    ASSOCIATED,
    UNREACHABLE;

    public String value() {
        return name();
    }

    public static ConnectivityStatusEnumeration fromValue(String v) {
        return valueOf(v);
    }

}


package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocationTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LocationTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LOCATION_TYPE_PREMISE"/>
 *     &lt;enumeration value="LOCATION_TYPE_NETWORK"/>
 *     &lt;enumeration value="LOCATION_TYPE_POLE"/>
 *     &lt;enumeration value="LOCATION_TYPE_TRANSFORM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LocationTypeEnumeration", namespace = "urn:com:ssn:dr:xmlschema:service:v1.0:LocationTypes.xsd")
@XmlEnum
public enum LocationTypeEnumeration {

    LOCATION_TYPE_PREMISE,
    LOCATION_TYPE_NETWORK,
    LOCATION_TYPE_POLE,
    LOCATION_TYPE_TRANSFORM;

    public String value() {
        return name();
    }

    public static LocationTypeEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

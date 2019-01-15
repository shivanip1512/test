
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HANDeviceSEPClusterEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HANDeviceSEPClusterEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PRICE_CLIENT"/>
 *     &lt;enumeration value="PRICE_SERVER"/>
 *     &lt;enumeration value="DRLC_CLIENT"/>
 *     &lt;enumeration value="DRLC_SERVER"/>
 *     &lt;enumeration value="MESSAGE_CLIENT"/>
 *     &lt;enumeration value="MESSAGE_SERVER"/>
 *     &lt;enumeration value="SIMPLE_METERING_CLIENT"/>
 *     &lt;enumeration value="SIMPLE_METERING_SERVER"/>
 *     &lt;enumeration value="SMART_ENERGY_TUNNELING_CLIENT"/>
 *     &lt;enumeration value="SMART_ENERGY_TUNNELING_SERVER"/>
 *     &lt;enumeration value="KEY_ESTABLISHMENT_CLIENT"/>
 *     &lt;enumeration value="KEY_ESTABLISHMENT_SERVER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HANDeviceSEPClusterEnumeration")
@XmlEnum
public enum HANDeviceSEPClusterEnumeration {

    PRICE_CLIENT,
    PRICE_SERVER,
    DRLC_CLIENT,
    DRLC_SERVER,
    MESSAGE_CLIENT,
    MESSAGE_SERVER,
    SIMPLE_METERING_CLIENT,
    SIMPLE_METERING_SERVER,
    SMART_ENERGY_TUNNELING_CLIENT,
    SMART_ENERGY_TUNNELING_SERVER,
    KEY_ESTABLISHMENT_CLIENT,
    KEY_ESTABLISHMENT_SERVER;

    public String value() {
        return name();
    }

    public static HANDeviceSEPClusterEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

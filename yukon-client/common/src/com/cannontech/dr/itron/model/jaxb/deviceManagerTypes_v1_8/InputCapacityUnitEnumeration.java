
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InputCapacityUnitEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="InputCapacityUnitEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="KW"/>
 *     &lt;enumeration value="TON"/>
 *     &lt;enumeration value="BTU"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "InputCapacityUnitEnumeration")
@XmlEnum
public enum InputCapacityUnitEnumeration {

    KW,
    TON,
    BTU;

    public String value() {
        return name();
    }

    public static InputCapacityUnitEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

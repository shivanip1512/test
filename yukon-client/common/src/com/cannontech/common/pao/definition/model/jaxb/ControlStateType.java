
package com.cannontech.common.pao.definition.model.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for controlStateType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="controlStateType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OPEN"/>
 *     &lt;enumeration value="CLOSE"/>
 *     &lt;enumeration value="PULSE"/>
 *     &lt;enumeration value="DISABLE_OVUV_702X"/>
 *     &lt;enumeration value="ENABLE_OVUV_702X"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "controlStateType")
@XmlEnum
public enum ControlStateType {

    OPEN("OPEN"),
    CLOSE("CLOSE"),
    PULSE("PULSE"),
    @XmlEnumValue("DISABLE_OVUV_702X")
    DISABLE_OVUV_702_X("DISABLE_OVUV_702X"),
    @XmlEnumValue("ENABLE_OVUV_702X")
    ENABLE_OVUV_702_X("ENABLE_OVUV_702X");
    private final String value;

    ControlStateType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ControlStateType fromValue(String v) {
        for (ControlStateType c: ControlStateType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

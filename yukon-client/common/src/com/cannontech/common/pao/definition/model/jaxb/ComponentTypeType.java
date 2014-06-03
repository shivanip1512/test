
package com.cannontech.common.pao.definition.model.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for componentTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="componentTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Operation"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "componentTypeType")
@XmlEnum
public enum ComponentTypeType {

    @XmlEnumValue("Operation")
    OPERATION("Operation");
    private final String value;

    ComponentTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ComponentTypeType fromValue(String v) {
        for (ComponentTypeType c: ComponentTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}


package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="updateTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="On First Change"/>
 *     &lt;enumeration value="On All Change"/>
 *     &lt;enumeration value="On Timer"/>
 *     &lt;enumeration value="On Timer+Change"/>
 *     &lt;enumeration value="Constant"/>
 *     &lt;enumeration value="Historical"/>
 *     &lt;enumeration value="Backfilling"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "updateTypeType")
@XmlEnum
public enum UpdateTypeType {

    @XmlEnumValue("On First Change")
    ON_FIRST_CHANGE("On First Change"),
    @XmlEnumValue("On All Change")
    ON_ALL_CHANGE("On All Change"),
    @XmlEnumValue("On Timer")
    ON_TIMER("On Timer"),
    @XmlEnumValue("On Timer+Change")
    ON_TIMER_CHANGE("On Timer+Change"),
    @XmlEnumValue("Constant")
    CONSTANT("Constant"),
    @XmlEnumValue("Historical")
    HISTORICAL("Historical"),
    @XmlEnumValue("Backfilling")
    BACKFILLING("Backfilling");
    private final String value;

    UpdateTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UpdateTypeType fromValue(String v) {
        for (UpdateTypeType c: UpdateTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

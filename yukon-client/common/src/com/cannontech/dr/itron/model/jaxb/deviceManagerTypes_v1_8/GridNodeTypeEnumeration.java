
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GridNodeTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GridNodeTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Transmission"/>
 *     &lt;enumeration value="Substation"/>
 *     &lt;enumeration value="Feeder"/>
 *     &lt;enumeration value="Transformer"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "GridNodeTypeEnumeration")
@XmlEnum
public enum GridNodeTypeEnumeration {

    @XmlEnumValue("Transmission")
    TRANSMISSION("Transmission"),
    @XmlEnumValue("Substation")
    SUBSTATION("Substation"),
    @XmlEnumValue("Feeder")
    FEEDER("Feeder"),
    @XmlEnumValue("Transformer")
    TRANSFORMER("Transformer");
    private final String value;

    GridNodeTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GridNodeTypeEnumeration fromValue(String v) {
        for (GridNodeTypeEnumeration c: GridNodeTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

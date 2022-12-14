
package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for controlTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="controlTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="NORMAL"/>
 *     &lt;enumeration value="LATCH"/>
 *     &lt;enumeration value="PSEUDO"/>
 *     &lt;enumeration value="SBOLATCH"/>
 *     &lt;enumeration value="SBOPULSE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "controlTypeType")
@XmlEnum
public enum ControlTypeType {

    NONE,
    NORMAL,
    LATCH,
    PSEUDO,
    SBOLATCH,
    SBOPULSE;

    public String value() {
        return name();
    }

    public static ControlTypeType fromValue(String v) {
        return valueOf(v);
    }

}

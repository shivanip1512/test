
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PingStatusEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PingStatusEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RESULT_SUCCESS"/>
 *     &lt;enumeration value="RESULT_TIMEOUT"/>
 *     &lt;enumeration value="RESULT_UNREACHABLE"/>
 *     &lt;enumeration value="RESULT_ESI_REACHED"/>
 *     &lt;enumeration value="RESULT_ESI_NOT_FOUND"/>
 *     &lt;enumeration value="RESULT_ESI_UNREACHABLE"/>
 *     &lt;enumeration value="RESULT_PING_NO_RESULTS"/>
 *     &lt;enumeration value="JOB_NOT_STARTED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PingStatusEnumeration")
@XmlEnum
public enum PingStatusEnumeration {

    RESULT_SUCCESS,
    RESULT_TIMEOUT,
    RESULT_UNREACHABLE,
    RESULT_ESI_REACHED,
    RESULT_ESI_NOT_FOUND,
    RESULT_ESI_UNREACHABLE,
    RESULT_PING_NO_RESULTS,
    JOB_NOT_STARTED;

    public String value() {
        return name();
    }

    public static PingStatusEnumeration fromValue(String v) {
        return valueOf(v);
    }

}

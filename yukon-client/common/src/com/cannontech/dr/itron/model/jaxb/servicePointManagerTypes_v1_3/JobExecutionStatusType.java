
package com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JobExecutionStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="JobExecutionStatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PENDING"/>
 *     &lt;enumeration value="COMPLETE"/>
 *     &lt;enumeration value="FAILURE"/>
 *     &lt;enumeration value="RUNNING"/>
 *     &lt;enumeration value="CANCELLED"/>
 *     &lt;enumeration value="CANCELLATION_IN_PROGRESS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "JobExecutionStatusType", namespace = "urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd")
@XmlEnum
public enum JobExecutionStatusType {


    /**
     * 
     *                         Used when the job execution status is not yet available.
     *                     
     * 
     */
    PENDING,
    COMPLETE,
    FAILURE,
    RUNNING,
    CANCELLED,
    CANCELLATION_IN_PROGRESS;

    public String value() {
        return name();
    }

    public static JobExecutionStatusType fromValue(String v) {
        return valueOf(v);
    }

}

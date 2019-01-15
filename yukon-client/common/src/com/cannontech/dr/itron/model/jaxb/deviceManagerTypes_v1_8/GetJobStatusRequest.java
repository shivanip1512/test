
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                  Possible ErrorCode values if you receive a BasicFaultType:
 *                      generic
 *                      fatal_error
 *                      authorization_failure
 *                      InvalidID.Job.ID
 *             
 * 
 * <p>Java class for GetJobStatusRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetJobStatusRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="JobIdentifier" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetJobStatusRequestType", propOrder = {
    "jobIdentifier"
})
@XmlRootElement(name = "GetJobStatusRequest")
public class GetJobStatusRequest {

    @XmlElement(name = "JobIdentifier")
    protected long jobIdentifier;

    /**
     * Gets the value of the jobIdentifier property.
     * 
     */
    public long getJobIdentifier() {
        return jobIdentifier;
    }

    /**
     * Sets the value of the jobIdentifier property.
     * 
     */
    public void setJobIdentifier(long value) {
        this.jobIdentifier = value;
    }

}

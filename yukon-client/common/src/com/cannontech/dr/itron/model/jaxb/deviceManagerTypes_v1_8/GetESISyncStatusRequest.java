
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetESISyncStatusRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetESISyncStatusRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="JobID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetESISyncStatusRequestType", propOrder = {
    "jobID"
})
@XmlRootElement(name = "GetESISyncStatusRequest")
public class GetESISyncStatusRequest {

    @XmlElement(name = "JobID")
    protected long jobID;

    /**
     * Gets the value of the jobID property.
     * 
     */
    public long getJobID() {
        return jobID;
    }

    /**
     * Sets the value of the jobID property.
     * 
     */
    public void setJobID(long value) {
        this.jobID = value;
    }

}

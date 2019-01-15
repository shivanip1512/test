
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 You can use the returned JobIdentifier to poll the status of the set job via the getJobStatus operation or you can wait for the JMS queue message which will include the status.
 *             
 * 
 * <p>Java class for SetPCTStateResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetPCTStateResponseType">
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
@XmlType(name = "SetPCTStateResponseType", propOrder = {
    "jobIdentifier"
})
@XmlRootElement(name = "SetPCTStateResponse")
public class SetPCTStateResponse {

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

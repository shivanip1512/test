
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetJobStatusResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetJobStatusResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Status" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}JobStatusType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetJobStatusResponseType", propOrder = {
    "status"
})
@XmlRootElement(name = "GetJobStatusResponse")
public class GetJobStatusResponse {

    @XmlElement(name = "Status", required = true)
    protected JobStatusType status;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link JobStatusType }
     *     
     */
    public JobStatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobStatusType }
     *     
     */
    public void setStatus(JobStatusType value) {
        this.status = value;
    }

}

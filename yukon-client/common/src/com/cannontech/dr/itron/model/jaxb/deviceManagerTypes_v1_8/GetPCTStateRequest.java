
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 The results of the get are only delivered over the JMS queue configured at instlall time.  You can use the returned JobIdentifier to poll the status of the get job via the getJobStatus operation or you can wait for the JMS queue message which will include the status.
 *                 Possible ErrorCode values if you receive a BasicFaultType:
 *                     generic
 *                     fatal_error
 *                     authorization_failure
 *             
 * 
 * <p>Java class for GetPCTStateRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPCTStateRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PCTMacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPCTStateRequestType", propOrder = {
    "pctMacID"
})
@XmlRootElement(name = "GetPCTStateRequest")
public class GetPCTStateRequest {

    @XmlElement(name = "PCTMacID", required = true)
    protected String pctMacID;

    /**
     * Gets the value of the pctMacID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPCTMacID() {
        return pctMacID;
    }

    /**
     * Sets the value of the pctMacID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPCTMacID(String value) {
        this.pctMacID = value;
    }

}

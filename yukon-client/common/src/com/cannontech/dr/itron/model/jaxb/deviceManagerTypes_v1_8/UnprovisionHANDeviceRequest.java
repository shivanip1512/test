
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Possible ErrorCode values if you receive a BasicFaultType:
 *                     generic
 *                     fatal_error
 *                     authorization_failure
 *                     macID.Pattern.message
 *                     NotFound.deviceManager.hanDeviceNotFound
 *                     MACAddress.deviceManager.addressIsNotHANDevice
 *             
 * 
 * <p>Java class for UnprovisionHANDeviceRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnprovisionHANDeviceRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HANMacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnprovisionHANDeviceRequestType", propOrder = {
    "hanMacID"
})
@XmlRootElement(name = "UnprovisionHANDeviceRequest")
public class UnprovisionHANDeviceRequest {

    @XmlElement(name = "HANMacID", required = true)
    protected String hanMacID;

    /**
     * Gets the value of the hanMacID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHANMacID() {
        return hanMacID;
    }

    /**
     * Sets the value of the hanMacID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHANMacID(String value) {
        this.hanMacID = value;
    }

}

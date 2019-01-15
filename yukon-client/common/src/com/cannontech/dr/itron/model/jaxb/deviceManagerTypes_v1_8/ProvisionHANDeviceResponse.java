
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProvisionHANDeviceResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProvisionHANDeviceResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProvisionHANDeviceCommandID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionHANDeviceResponseType", propOrder = {
    "provisionHANDeviceCommandID"
})
@XmlRootElement(name = "ProvisionHANDeviceResponse")
public class ProvisionHANDeviceResponse {

    @XmlElement(name = "ProvisionHANDeviceCommandID")
    protected long provisionHANDeviceCommandID;

    /**
     * Gets the value of the provisionHANDeviceCommandID property.
     * 
     */
    public long getProvisionHANDeviceCommandID() {
        return provisionHANDeviceCommandID;
    }

    /**
     * Sets the value of the provisionHANDeviceCommandID property.
     * 
     */
    public void setProvisionHANDeviceCommandID(long value) {
        this.provisionHANDeviceCommandID = value;
    }

}

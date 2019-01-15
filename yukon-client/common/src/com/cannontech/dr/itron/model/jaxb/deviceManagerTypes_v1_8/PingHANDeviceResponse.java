
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PingHANDeviceResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PingHANDeviceResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PingHANDeviceCommandID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PingHANDeviceResponseType", propOrder = {
    "pingHANDeviceCommandID"
})
@XmlRootElement(name = "PingHANDeviceResponse")
public class PingHANDeviceResponse {

    @XmlElement(name = "PingHANDeviceCommandID")
    protected long pingHANDeviceCommandID;

    /**
     * Gets the value of the pingHANDeviceCommandID property.
     * 
     */
    public long getPingHANDeviceCommandID() {
        return pingHANDeviceCommandID;
    }

    /**
     * Sets the value of the pingHANDeviceCommandID property.
     * 
     */
    public void setPingHANDeviceCommandID(long value) {
        this.pingHANDeviceCommandID = value;
    }

}


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
 *             
 * 
 * <p>Java class for GetPingHANDeviceStatusRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPingHANDeviceStatusRequestType">
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
@XmlType(name = "GetPingHANDeviceStatusRequestType", propOrder = {
    "pingHANDeviceCommandID"
})
@XmlRootElement(name = "GetPingHANDeviceStatusRequest")
public class GetPingHANDeviceStatusRequest {

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

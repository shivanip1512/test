
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Used to get status for both provision and unprovision HAN Device.
 *                 Possible ErrorCode values if you receive a BasicFaultType:
 *                     generic
 *                     fatal_error
 *                     authorization_failure
 *                     InvalidID.provisionStatus.provisionCommandNotFound
 *             
 * 
 * <p>Java class for GetProvisionHANDeviceCommandStatusRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetProvisionHANDeviceCommandStatusRequestType">
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
@XmlType(name = "GetProvisionHANDeviceCommandStatusRequestType", propOrder = {
    "provisionHANDeviceCommandID"
})
@XmlRootElement(name = "GetProvisionHANDeviceCommandStatusRequest")
public class GetProvisionHANDeviceCommandStatusRequest {

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

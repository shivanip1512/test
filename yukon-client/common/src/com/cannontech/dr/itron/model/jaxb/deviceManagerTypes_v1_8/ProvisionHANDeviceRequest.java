
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
 *                     NotFound.deviceManager.esiNotFound
 *                     MACAddress.deviceManager.addressIsNotESI
 *                     NotFound.deviceManager.hanDeviceNotFound
 *                     MACAddress.deviceManager.addressIsNotHANDevice
 *                     macID.Pattern.message
 *             
 * 
 * <p>Java class for ProvisionHANDeviceRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProvisionHANDeviceRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HANMacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ESIMacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="JoinDurationSeconds" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}JoinDurationSecondsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionHANDeviceRequestType", propOrder = {
    "hanMacID",
    "esiMacID",
    "joinDurationSeconds"
})
@XmlRootElement(name = "ProvisionHANDeviceRequest")
public class ProvisionHANDeviceRequest {

    @XmlElement(name = "HANMacID", required = true)
    protected String hanMacID;
    @XmlElement(name = "ESIMacID", required = true)
    protected String esiMacID;
    @XmlElement(name = "JoinDurationSeconds")
    protected short joinDurationSeconds;

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

    /**
     * Gets the value of the esiMacID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getESIMacID() {
        return esiMacID;
    }

    /**
     * Sets the value of the esiMacID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setESIMacID(String value) {
        this.esiMacID = value;
    }

    /**
     * Gets the value of the joinDurationSeconds property.
     * 
     */
    public short getJoinDurationSeconds() {
        return joinDurationSeconds;
    }

    /**
     * Sets the value of the joinDurationSeconds property.
     * 
     */
    public void setJoinDurationSeconds(short value) {
        this.joinDurationSeconds = value;
    }

}

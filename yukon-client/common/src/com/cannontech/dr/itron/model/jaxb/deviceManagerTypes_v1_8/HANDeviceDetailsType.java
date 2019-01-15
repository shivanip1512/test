
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HANDeviceDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HANDeviceDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HANDevice" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ListHANDeviceType" minOccurs="0"/>
 *         &lt;element name="ErrorCode" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDevicesErrorEnumeration" minOccurs="0"/>
 *         &lt;element name="MacID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HANDeviceDetailsType", propOrder = {
    "hanDevice",
    "errorCode",
    "macID"
})
public class HANDeviceDetailsType {

    @XmlElement(name = "HANDevice")
    protected ListHANDeviceType hanDevice;
    @XmlElement(name = "ErrorCode")
    protected HANDevicesErrorEnumeration errorCode;
    @XmlElement(name = "MacID")
    protected String macID;

    /**
     * Gets the value of the hanDevice property.
     * 
     * @return
     *     possible object is
     *     {@link ListHANDeviceType }
     *     
     */
    public ListHANDeviceType getHANDevice() {
        return hanDevice;
    }

    /**
     * Sets the value of the hanDevice property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListHANDeviceType }
     *     
     */
    public void setHANDevice(ListHANDeviceType value) {
        this.hanDevice = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link HANDevicesErrorEnumeration }
     *     
     */
    public HANDevicesErrorEnumeration getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link HANDevicesErrorEnumeration }
     *     
     */
    public void setErrorCode(HANDevicesErrorEnumeration value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the macID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacID() {
        return macID;
    }

    /**
     * Sets the value of the macID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacID(String value) {
        this.macID = value;
    }

}

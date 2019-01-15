
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ZigbeeAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ZigbeeAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HANDeviceAdditionalInfo" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PrimaryDeviceExtType" minOccurs="0"/>
 *         &lt;element name="DeviceSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeviceType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceTypeEnumeration"/>
 *         &lt;element name="ESIMacID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SEPVersion" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}SEPVersionEnumeration" minOccurs="0"/>
 *         &lt;element name="HANDeviceClass" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDeviceClassEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZigbeeAttributeType", propOrder = {
    "hanDeviceAdditionalInfo",
    "deviceSerialNumber",
    "deviceType",
    "esiMacID",
    "sepVersion",
    "hanDeviceClass"
})
@XmlSeeAlso({
    AddZigbeeAttributeType.class,
    ListZigbeeAttributeType.class
})
public class ZigbeeAttributeType {

    @XmlElement(name = "HANDeviceAdditionalInfo")
    protected PrimaryDeviceExtType hanDeviceAdditionalInfo;
    @XmlElement(name = "DeviceSerialNumber")
    protected String deviceSerialNumber;
    @XmlElement(name = "DeviceType", required = true)
    protected DeviceTypeEnumeration deviceType;
    @XmlElement(name = "ESIMacID")
    protected String esiMacID;
    @XmlElement(name = "SEPVersion")
    protected String sepVersion;
    @XmlElement(name = "HANDeviceClass")
    protected HANDeviceClassEnumeration hanDeviceClass;

    /**
     * Gets the value of the hanDeviceAdditionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link PrimaryDeviceExtType }
     *     
     */
    public PrimaryDeviceExtType getHANDeviceAdditionalInfo() {
        return hanDeviceAdditionalInfo;
    }

    /**
     * Sets the value of the hanDeviceAdditionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrimaryDeviceExtType }
     *     
     */
    public void setHANDeviceAdditionalInfo(PrimaryDeviceExtType value) {
        this.hanDeviceAdditionalInfo = value;
    }

    /**
     * Gets the value of the deviceSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    /**
     * Sets the value of the deviceSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceSerialNumber(String value) {
        this.deviceSerialNumber = value;
    }

    /**
     * Gets the value of the deviceType property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceTypeEnumeration }
     *     
     */
    public DeviceTypeEnumeration getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the value of the deviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceTypeEnumeration }
     *     
     */
    public void setDeviceType(DeviceTypeEnumeration value) {
        this.deviceType = value;
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
     * Gets the value of the sepVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEPVersion() {
        return sepVersion;
    }

    /**
     * Sets the value of the sepVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEPVersion(String value) {
        this.sepVersion = value;
    }

    /**
     * Gets the value of the hanDeviceClass property.
     * 
     * @return
     *     possible object is
     *     {@link HANDeviceClassEnumeration }
     *     
     */
    public HANDeviceClassEnumeration getHANDeviceClass() {
        return hanDeviceClass;
    }

    /**
     * Sets the value of the hanDeviceClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link HANDeviceClassEnumeration }
     *     
     */
    public void setHANDeviceClass(HANDeviceClassEnumeration value) {
        this.hanDeviceClass = value;
    }

}

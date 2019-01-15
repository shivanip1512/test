
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrimaryDeviceExtType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrimaryDeviceExtType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ManufacturerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ManufacturerModel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HardwareVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Firmware1Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Firmware1Type" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Firmware2Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Firmware2Type" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="SoftwareVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrimaryDeviceExtType", propOrder = {
    "manufacturerName",
    "manufacturerModel",
    "hardwareVersion",
    "firmware1Version",
    "firmware1Type",
    "firmware2Version",
    "firmware2Type",
    "softwareVersion"
})
public class PrimaryDeviceExtType {

    @XmlElement(name = "ManufacturerName")
    protected String manufacturerName;
    @XmlElement(name = "ManufacturerModel")
    protected String manufacturerModel;
    @XmlElement(name = "HardwareVersion")
    protected String hardwareVersion;
    @XmlElement(name = "Firmware1Version")
    protected String firmware1Version;
    @XmlElement(name = "Firmware1Type")
    protected Integer firmware1Type;
    @XmlElement(name = "Firmware2Version")
    protected String firmware2Version;
    @XmlElement(name = "Firmware2Type")
    protected Integer firmware2Type;
    @XmlElement(name = "SoftwareVersion")
    protected String softwareVersion;

    /**
     * Gets the value of the manufacturerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the value of the manufacturerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturerName(String value) {
        this.manufacturerName = value;
    }

    /**
     * Gets the value of the manufacturerModel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturerModel() {
        return manufacturerModel;
    }

    /**
     * Sets the value of the manufacturerModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturerModel(String value) {
        this.manufacturerModel = value;
    }

    /**
     * Gets the value of the hardwareVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHardwareVersion() {
        return hardwareVersion;
    }

    /**
     * Sets the value of the hardwareVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHardwareVersion(String value) {
        this.hardwareVersion = value;
    }

    /**
     * Gets the value of the firmware1Version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirmware1Version() {
        return firmware1Version;
    }

    /**
     * Sets the value of the firmware1Version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirmware1Version(String value) {
        this.firmware1Version = value;
    }

    /**
     * Gets the value of the firmware1Type property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFirmware1Type() {
        return firmware1Type;
    }

    /**
     * Sets the value of the firmware1Type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFirmware1Type(Integer value) {
        this.firmware1Type = value;
    }

    /**
     * Gets the value of the firmware2Version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirmware2Version() {
        return firmware2Version;
    }

    /**
     * Sets the value of the firmware2Version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirmware2Version(String value) {
        this.firmware2Version = value;
    }

    /**
     * Gets the value of the firmware2Type property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFirmware2Type() {
        return firmware2Type;
    }

    /**
     * Sets the value of the firmware2Type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFirmware2Type(Integer value) {
        this.firmware2Type = value;
    }

    /**
     * Gets the value of the softwareVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets the value of the softwareVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSoftwareVersion(String value) {
        this.softwareVersion = value;
    }

}

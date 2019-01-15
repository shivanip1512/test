
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FindDeviceBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FindDeviceBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Manufacturer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Model" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeviceState" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceStateEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindDeviceBaseType", propOrder = {
    "manufacturer",
    "model",
    "deviceState"
})
@XmlSeeAlso({
    ESISearchRequestType.class,
    FindHANDeviceBaseType.class
})
public class FindDeviceBaseType {

    @XmlElement(name = "Manufacturer")
    protected String manufacturer;
    @XmlElement(name = "Model")
    protected String model;
    @XmlElement(name = "DeviceState")
    protected DeviceStateEnumeration deviceState;

    /**
     * Gets the value of the manufacturer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the value of the manufacturer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturer(String value) {
        this.manufacturer = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModel(String value) {
        this.model = value;
    }

    /**
     * Gets the value of the deviceState property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceStateEnumeration }
     *     
     */
    public DeviceStateEnumeration getDeviceState() {
        return deviceState;
    }

    /**
     * Sets the value of the deviceState property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceStateEnumeration }
     *     
     */
    public void setDeviceState(DeviceStateEnumeration value) {
        this.deviceState = value;
    }

}

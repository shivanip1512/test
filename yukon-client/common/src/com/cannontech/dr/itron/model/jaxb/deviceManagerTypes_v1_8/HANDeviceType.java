
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HANDeviceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HANDeviceType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PrimaryDeviceBaseType">
 *       &lt;sequence>
 *         &lt;element name="DeviceIdentifiers" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceIdentifierAttributeType" minOccurs="0"/>
 *         &lt;element name="BatteryPowered" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="LoadShedPotential" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}KiloWattType" minOccurs="0"/>
 *         &lt;element name="NullableInputCapacity" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableInputCapacityType" minOccurs="0"/>
 *         &lt;element name="InputSeerRating" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HANDeviceType", propOrder = {
    "deviceIdentifiers",
    "batteryPowered",
    "loadShedPotential",
    "nullableInputCapacity",
    "inputSeerRating"
})
@XmlSeeAlso({
    ListHANDeviceType.class,
    AddHANDeviceRequest.class
})
public class HANDeviceType
    extends PrimaryDeviceBaseType
{

    @XmlElement(name = "DeviceIdentifiers")
    protected DeviceIdentifierAttributeType deviceIdentifiers;
    @XmlElement(name = "BatteryPowered")
    protected Boolean batteryPowered;
    @XmlElement(name = "LoadShedPotential")
    protected Float loadShedPotential;
    @XmlElement(name = "NullableInputCapacity")
    protected NullableInputCapacityType nullableInputCapacity;
    @XmlElement(name = "InputSeerRating")
    protected Integer inputSeerRating;

    /**
     * Gets the value of the deviceIdentifiers property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceIdentifierAttributeType }
     *     
     */
    public DeviceIdentifierAttributeType getDeviceIdentifiers() {
        return deviceIdentifiers;
    }

    /**
     * Sets the value of the deviceIdentifiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceIdentifierAttributeType }
     *     
     */
    public void setDeviceIdentifiers(DeviceIdentifierAttributeType value) {
        this.deviceIdentifiers = value;
    }

    /**
     * Gets the value of the batteryPowered property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBatteryPowered() {
        return batteryPowered;
    }

    /**
     * Sets the value of the batteryPowered property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBatteryPowered(Boolean value) {
        this.batteryPowered = value;
    }

    /**
     * Gets the value of the loadShedPotential property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLoadShedPotential() {
        return loadShedPotential;
    }

    /**
     * Sets the value of the loadShedPotential property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLoadShedPotential(Float value) {
        this.loadShedPotential = value;
    }

    /**
     * Gets the value of the nullableInputCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link NullableInputCapacityType }
     *     
     */
    public NullableInputCapacityType getNullableInputCapacity() {
        return nullableInputCapacity;
    }

    /**
     * Sets the value of the nullableInputCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableInputCapacityType }
     *     
     */
    public void setNullableInputCapacity(NullableInputCapacityType value) {
        this.nullableInputCapacity = value;
    }

    /**
     * Gets the value of the inputSeerRating property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInputSeerRating() {
        return inputSeerRating;
    }

    /**
     * Sets the value of the inputSeerRating property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInputSeerRating(Integer value) {
        this.inputSeerRating = value;
    }

}

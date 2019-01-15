
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EditPrimaryHANDeviceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditPrimaryHANDeviceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DeviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Notes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="OwnedBy" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableOwnerTypeEnumeration" minOccurs="0"/>
 *         &lt;element name="BatteryPowered" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableBoolean" minOccurs="0"/>
 *         &lt;element name="LoadShedPotential" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}NullableKiloWattType" minOccurs="0"/>
 *         &lt;element name="NullableInputCapacity" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableInputCapacityType" minOccurs="0"/>
 *         &lt;element name="InputSeerRating" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableInt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditPrimaryHANDeviceType", propOrder = {
    "macID",
    "deviceName",
    "notes",
    "ownedBy",
    "batteryPowered",
    "loadShedPotential",
    "nullableInputCapacity",
    "inputSeerRating"
})
public class EditPrimaryHANDeviceType {

    @XmlElement(name = "MacID", required = true)
    protected String macID;
    @XmlElement(name = "DeviceName")
    protected String deviceName;
    @XmlElement(name = "Notes")
    protected NullableString notes;
    @XmlElement(name = "OwnedBy")
    protected NullableOwnerTypeEnumeration ownedBy;
    @XmlElement(name = "BatteryPowered")
    protected NullableBoolean batteryPowered;
    @XmlElement(name = "LoadShedPotential")
    protected NullableKiloWattType loadShedPotential;
    @XmlElement(name = "NullableInputCapacity")
    protected NullableInputCapacityType nullableInputCapacity;
    @XmlElement(name = "InputSeerRating")
    protected NullableInt inputSeerRating;

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

    /**
     * Gets the value of the deviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets the value of the deviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceName(String value) {
        this.deviceName = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setNotes(NullableString value) {
        this.notes = value;
    }

    /**
     * Gets the value of the ownedBy property.
     * 
     * @return
     *     possible object is
     *     {@link NullableOwnerTypeEnumeration }
     *     
     */
    public NullableOwnerTypeEnumeration getOwnedBy() {
        return ownedBy;
    }

    /**
     * Sets the value of the ownedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableOwnerTypeEnumeration }
     *     
     */
    public void setOwnedBy(NullableOwnerTypeEnumeration value) {
        this.ownedBy = value;
    }

    /**
     * Gets the value of the batteryPowered property.
     * 
     * @return
     *     possible object is
     *     {@link NullableBoolean }
     *     
     */
    public NullableBoolean getBatteryPowered() {
        return batteryPowered;
    }

    /**
     * Sets the value of the batteryPowered property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableBoolean }
     *     
     */
    public void setBatteryPowered(NullableBoolean value) {
        this.batteryPowered = value;
    }

    /**
     * Gets the value of the loadShedPotential property.
     * 
     * @return
     *     possible object is
     *     {@link NullableKiloWattType }
     *     
     */
    public NullableKiloWattType getLoadShedPotential() {
        return loadShedPotential;
    }

    /**
     * Sets the value of the loadShedPotential property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableKiloWattType }
     *     
     */
    public void setLoadShedPotential(NullableKiloWattType value) {
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
     *     {@link NullableInt }
     *     
     */
    public NullableInt getInputSeerRating() {
        return inputSeerRating;
    }

    /**
     * Sets the value of the inputSeerRating property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableInt }
     *     
     */
    public void setInputSeerRating(NullableInt value) {
        this.inputSeerRating = value;
    }

}

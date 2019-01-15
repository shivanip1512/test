
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EditESIType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditESIType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DeviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Notes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="OwnedBy" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableOwnerTypeEnumeration" minOccurs="0"/>
 *         &lt;element name="SerialNumber" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditESIType", propOrder = {
    "macID",
    "deviceName",
    "notes",
    "ownedBy",
    "serialNumber"
})
public class EditESIType {

    @XmlElement(name = "MacID", required = true)
    protected String macID;
    @XmlElement(name = "DeviceName")
    protected String deviceName;
    @XmlElement(name = "Notes")
    protected NullableString notes;
    @XmlElement(name = "OwnedBy")
    protected NullableOwnerTypeEnumeration ownedBy;
    @XmlElement(name = "SerialNumber")
    protected NullableString serialNumber;

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
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setSerialNumber(NullableString value) {
        this.serialNumber = value;
    }

}

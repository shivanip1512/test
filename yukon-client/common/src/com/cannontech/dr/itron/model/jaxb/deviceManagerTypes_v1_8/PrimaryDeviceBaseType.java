
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrimaryDeviceBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrimaryDeviceBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OwnedBy" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}OwnerTypeEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrimaryDeviceBaseType", propOrder = {
    "notes",
    "ownedBy"
})
@XmlSeeAlso({
    PrimaryDeviceBaseWithIdentifiersType.class,
    HANDeviceType.class
})
public class PrimaryDeviceBaseType {

    @XmlElement(name = "Notes")
    protected String notes;
    @XmlElement(name = "OwnedBy")
    protected OwnerTypeEnumeration ownedBy;

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the ownedBy property.
     * 
     * @return
     *     possible object is
     *     {@link OwnerTypeEnumeration }
     *     
     */
    public OwnerTypeEnumeration getOwnedBy() {
        return ownedBy;
    }

    /**
     * Sets the value of the ownedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link OwnerTypeEnumeration }
     *     
     */
    public void setOwnedBy(OwnerTypeEnumeration value) {
        this.ownedBy = value;
    }

}

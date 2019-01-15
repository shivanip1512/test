
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EditD2GAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditD2GAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrimaryAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}EditPrimaryHANDeviceType" minOccurs="0"/>
 *         &lt;element name="ESI" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}EditESIType" minOccurs="0"/>
 *         &lt;element name="ServicePointUtilID" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditD2GAttributeType", propOrder = {
    "primaryAttributes",
    "esi",
    "servicePointUtilID"
})
public class EditD2GAttributeType {

    @XmlElement(name = "PrimaryAttributes")
    protected EditPrimaryHANDeviceType primaryAttributes;
    @XmlElement(name = "ESI")
    protected EditESIType esi;
    @XmlElement(name = "ServicePointUtilID")
    protected NullableString servicePointUtilID;

    /**
     * Gets the value of the primaryAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link EditPrimaryHANDeviceType }
     *     
     */
    public EditPrimaryHANDeviceType getPrimaryAttributes() {
        return primaryAttributes;
    }

    /**
     * Sets the value of the primaryAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditPrimaryHANDeviceType }
     *     
     */
    public void setPrimaryAttributes(EditPrimaryHANDeviceType value) {
        this.primaryAttributes = value;
    }

    /**
     * Gets the value of the esi property.
     * 
     * @return
     *     possible object is
     *     {@link EditESIType }
     *     
     */
    public EditESIType getESI() {
        return esi;
    }

    /**
     * Sets the value of the esi property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditESIType }
     *     
     */
    public void setESI(EditESIType value) {
        this.esi = value;
    }

    /**
     * Gets the value of the servicePointUtilID property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getServicePointUtilID() {
        return servicePointUtilID;
    }

    /**
     * Sets the value of the servicePointUtilID property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setServicePointUtilID(NullableString value) {
        this.servicePointUtilID = value;
    }

}

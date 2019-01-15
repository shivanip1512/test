
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ESISearchRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ESISearchRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FindDeviceBaseType">
 *       &lt;sequence>
 *         &lt;element name="Readiness" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ESIReadinessType" minOccurs="0"/>
 *         &lt;element name="FindDeviceCommonAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FindDeviceCommonAttributeType" minOccurs="0"/>
 *         &lt;element name="RatePlanID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="AssociatedDevice" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}AssociatedHANDeviceType" minOccurs="0"/>
 *         &lt;element name="AssociatedToServicePoint" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="HANRadioState" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ESIHANRadioStateEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ESISearchRequestType", propOrder = {
    "readiness",
    "findDeviceCommonAttributes",
    "ratePlanID",
    "associatedDevice",
    "associatedToServicePoint",
    "hanRadioState"
})
@XmlSeeAlso({
    FindESIRequest.class
})
public class ESISearchRequestType
    extends FindDeviceBaseType
{

    @XmlElement(name = "Readiness")
    protected ESIReadinessType readiness;
    @XmlElement(name = "FindDeviceCommonAttributes")
    protected FindDeviceCommonAttributeType findDeviceCommonAttributes;
    @XmlElement(name = "RatePlanID")
    protected Long ratePlanID;
    @XmlElement(name = "AssociatedDevice")
    protected AssociatedHANDeviceType associatedDevice;
    @XmlElement(name = "AssociatedToServicePoint")
    protected Boolean associatedToServicePoint;
    @XmlElement(name = "HANRadioState")
    protected ESIHANRadioStateEnumeration hanRadioState;

    /**
     * Gets the value of the readiness property.
     * 
     * @return
     *     possible object is
     *     {@link ESIReadinessType }
     *     
     */
    public ESIReadinessType getReadiness() {
        return readiness;
    }

    /**
     * Sets the value of the readiness property.
     * 
     * @param value
     *     allowed object is
     *     {@link ESIReadinessType }
     *     
     */
    public void setReadiness(ESIReadinessType value) {
        this.readiness = value;
    }

    /**
     * Gets the value of the findDeviceCommonAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link FindDeviceCommonAttributeType }
     *     
     */
    public FindDeviceCommonAttributeType getFindDeviceCommonAttributes() {
        return findDeviceCommonAttributes;
    }

    /**
     * Sets the value of the findDeviceCommonAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindDeviceCommonAttributeType }
     *     
     */
    public void setFindDeviceCommonAttributes(FindDeviceCommonAttributeType value) {
        this.findDeviceCommonAttributes = value;
    }

    /**
     * Gets the value of the ratePlanID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRatePlanID() {
        return ratePlanID;
    }

    /**
     * Sets the value of the ratePlanID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRatePlanID(Long value) {
        this.ratePlanID = value;
    }

    /**
     * Gets the value of the associatedDevice property.
     * 
     * @return
     *     possible object is
     *     {@link AssociatedHANDeviceType }
     *     
     */
    public AssociatedHANDeviceType getAssociatedDevice() {
        return associatedDevice;
    }

    /**
     * Sets the value of the associatedDevice property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssociatedHANDeviceType }
     *     
     */
    public void setAssociatedDevice(AssociatedHANDeviceType value) {
        this.associatedDevice = value;
    }

    /**
     * Gets the value of the associatedToServicePoint property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAssociatedToServicePoint() {
        return associatedToServicePoint;
    }

    /**
     * Sets the value of the associatedToServicePoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAssociatedToServicePoint(Boolean value) {
        this.associatedToServicePoint = value;
    }

    /**
     * Gets the value of the hanRadioState property.
     * 
     * @return
     *     possible object is
     *     {@link ESIHANRadioStateEnumeration }
     *     
     */
    public ESIHANRadioStateEnumeration getHANRadioState() {
        return hanRadioState;
    }

    /**
     * Sets the value of the hanRadioState property.
     * 
     * @param value
     *     allowed object is
     *     {@link ESIHANRadioStateEnumeration }
     *     
     */
    public void setHANRadioState(ESIHANRadioStateEnumeration value) {
        this.hanRadioState = value;
    }

}

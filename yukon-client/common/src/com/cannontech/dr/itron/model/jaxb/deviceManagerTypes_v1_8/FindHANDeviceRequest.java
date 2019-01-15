
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Possible ErrorCode values if you receive a BasicFaultType:
 *                     generic
 *                     fatal_error
 *                     authorization_failure
 *             
 * 
 * <p>Java class for FindHANDeviceRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FindHANDeviceRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FindHANDeviceBaseType">
 *       &lt;sequence>
 *         &lt;element name="FindDeviceCommonAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FindDeviceCommonAttributeType" minOccurs="0"/>
 *         &lt;element name="HANDeviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ESIMacID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ESIDeviceState" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceStateEnumeration" minOccurs="0"/>
 *         &lt;element name="ESIReadiness" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ESIReadinessType" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="ZigbeeAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FindZigbeeAttributeType"/>
 *           &lt;element name="D2GAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FindD2GAttributeType"/>
 *         &lt;/choice>
 *         &lt;element name="Pagination" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}PaginationType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindHANDeviceRequestType", propOrder = {
    "findDeviceCommonAttributes",
    "hanDeviceName",
    "esiMacID",
    "esiDeviceState",
    "esiReadiness",
    "d2GAttributes",
    "zigbeeAttributes",
    "pagination"
})
@XmlRootElement(name = "FindHANDeviceRequest")
public class FindHANDeviceRequest
    extends FindHANDeviceBaseType
{

    @XmlElement(name = "FindDeviceCommonAttributes")
    protected FindDeviceCommonAttributeType findDeviceCommonAttributes;
    @XmlElement(name = "HANDeviceName")
    protected String hanDeviceName;
    @XmlElement(name = "ESIMacID")
    protected String esiMacID;
    @XmlElement(name = "ESIDeviceState")
    protected DeviceStateEnumeration esiDeviceState;
    @XmlElement(name = "ESIReadiness")
    protected ESIReadinessType esiReadiness;
    @XmlElement(name = "D2GAttributes")
    protected FindD2GAttributeType d2GAttributes;
    @XmlElement(name = "ZigbeeAttributes")
    protected FindZigbeeAttributeType zigbeeAttributes;
    @XmlElement(name = "Pagination", required = true)
    protected PaginationType pagination;

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
     * Gets the value of the hanDeviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHANDeviceName() {
        return hanDeviceName;
    }

    /**
     * Sets the value of the hanDeviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHANDeviceName(String value) {
        this.hanDeviceName = value;
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
     * Gets the value of the esiDeviceState property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceStateEnumeration }
     *     
     */
    public DeviceStateEnumeration getESIDeviceState() {
        return esiDeviceState;
    }

    /**
     * Sets the value of the esiDeviceState property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceStateEnumeration }
     *     
     */
    public void setESIDeviceState(DeviceStateEnumeration value) {
        this.esiDeviceState = value;
    }

    /**
     * Gets the value of the esiReadiness property.
     * 
     * @return
     *     possible object is
     *     {@link ESIReadinessType }
     *     
     */
    public ESIReadinessType getESIReadiness() {
        return esiReadiness;
    }

    /**
     * Sets the value of the esiReadiness property.
     * 
     * @param value
     *     allowed object is
     *     {@link ESIReadinessType }
     *     
     */
    public void setESIReadiness(ESIReadinessType value) {
        this.esiReadiness = value;
    }

    /**
     * Gets the value of the d2GAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link FindD2GAttributeType }
     *     
     */
    public FindD2GAttributeType getD2GAttributes() {
        return d2GAttributes;
    }

    /**
     * Sets the value of the d2GAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindD2GAttributeType }
     *     
     */
    public void setD2GAttributes(FindD2GAttributeType value) {
        this.d2GAttributes = value;
    }

    /**
     * Gets the value of the zigbeeAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link FindZigbeeAttributeType }
     *     
     */
    public FindZigbeeAttributeType getZigbeeAttributes() {
        return zigbeeAttributes;
    }

    /**
     * Sets the value of the zigbeeAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindZigbeeAttributeType }
     *     
     */
    public void setZigbeeAttributes(FindZigbeeAttributeType value) {
        this.zigbeeAttributes = value;
    }

    /**
     * Gets the value of the pagination property.
     * 
     * @return
     *     possible object is
     *     {@link PaginationType }
     *     
     */
    public PaginationType getPagination() {
        return pagination;
    }

    /**
     * Sets the value of the pagination property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaginationType }
     *     
     */
    public void setPagination(PaginationType value) {
        this.pagination = value;
    }

}

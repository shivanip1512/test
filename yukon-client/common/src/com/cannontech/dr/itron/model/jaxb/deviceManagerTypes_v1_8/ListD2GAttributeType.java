
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListD2GAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListD2GAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ESIAdditionalInfo" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ESIPrimaryDeviceExtType" minOccurs="0"/>
 *         &lt;element name="HANDeviceAdditionalInfo" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PrimaryDeviceExtType" minOccurs="0"/>
 *         &lt;element name="DeviceSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeviceType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceTypeEnumeration"/>
 *         &lt;element name="CLPSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ILCSupportedType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ILCSupportedTypeEnumeration" maxOccurs="3" minOccurs="0"/>
 *         &lt;element name="VirtualRelay" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}VirtualRelayType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ConfigHash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Configured" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ESI" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ListESIType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListD2GAttributeType", propOrder = {
    "esiAdditionalInfo",
    "hanDeviceAdditionalInfo",
    "deviceSerialNumber",
    "deviceType",
    "clpSupported",
    "ilcSupportedTypes",
    "virtualRelaies",
    "configHash",
    "configured",
    "esi"
})
public class ListD2GAttributeType {

    @XmlElement(name = "ESIAdditionalInfo")
    protected ESIPrimaryDeviceExtType esiAdditionalInfo;
    @XmlElement(name = "HANDeviceAdditionalInfo")
    protected PrimaryDeviceExtType hanDeviceAdditionalInfo;
    @XmlElement(name = "DeviceSerialNumber")
    protected String deviceSerialNumber;
    @XmlElement(name = "DeviceType", required = true)
    protected DeviceTypeEnumeration deviceType;
    @XmlElement(name = "CLPSupported")
    protected Boolean clpSupported;
    @XmlElement(name = "ILCSupportedType")
    protected List<ILCSupportedTypeEnumeration> ilcSupportedTypes;
    @XmlElement(name = "VirtualRelay")
    protected List<VirtualRelayType> virtualRelaies;
    @XmlElement(name = "ConfigHash")
    protected String configHash;
    @XmlElement(name = "Configured")
    protected Boolean configured;
    @XmlElement(name = "ESI", required = true)
    protected ListESIType esi;

    /**
     * Gets the value of the esiAdditionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ESIPrimaryDeviceExtType }
     *     
     */
    public ESIPrimaryDeviceExtType getESIAdditionalInfo() {
        return esiAdditionalInfo;
    }

    /**
     * Sets the value of the esiAdditionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ESIPrimaryDeviceExtType }
     *     
     */
    public void setESIAdditionalInfo(ESIPrimaryDeviceExtType value) {
        this.esiAdditionalInfo = value;
    }

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
     * Gets the value of the clpSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCLPSupported() {
        return clpSupported;
    }

    /**
     * Sets the value of the clpSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCLPSupported(Boolean value) {
        this.clpSupported = value;
    }

    /**
     * Gets the value of the ilcSupportedTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ilcSupportedTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getILCSupportedTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ILCSupportedTypeEnumeration }
     * 
     * 
     */
    public List<ILCSupportedTypeEnumeration> getILCSupportedTypes() {
        if (ilcSupportedTypes == null) {
            ilcSupportedTypes = new ArrayList<ILCSupportedTypeEnumeration>();
        }
        return this.ilcSupportedTypes;
    }

    /**
     * Gets the value of the virtualRelaies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the virtualRelaies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVirtualRelaies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VirtualRelayType }
     * 
     * 
     */
    public List<VirtualRelayType> getVirtualRelaies() {
        if (virtualRelaies == null) {
            virtualRelaies = new ArrayList<VirtualRelayType>();
        }
        return this.virtualRelaies;
    }

    /**
     * Gets the value of the configHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfigHash() {
        return configHash;
    }

    /**
     * Sets the value of the configHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfigHash(String value) {
        this.configHash = value;
    }

    /**
     * Gets the value of the configured property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isConfigured() {
        return configured;
    }

    /**
     * Sets the value of the configured property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setConfigured(Boolean value) {
        this.configured = value;
    }

    /**
     * Gets the value of the esi property.
     * 
     * @return
     *     possible object is
     *     {@link ListESIType }
     *     
     */
    public ListESIType getESI() {
        return esi;
    }

    /**
     * Sets the value of the esi property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListESIType }
     *     
     */
    public void setESI(ListESIType value) {
        this.esi = value;
    }

}

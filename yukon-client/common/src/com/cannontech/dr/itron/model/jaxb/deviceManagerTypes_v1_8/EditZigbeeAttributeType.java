
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EditZigbeeAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditZigbeeAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrimaryAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}EditPrimaryHANDeviceType"/>
 *         &lt;element name="ManufacturerName" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="ManufacturerModel" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="HardwareVersion" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="Firmware1Version" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="Firmware1Type" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableInt" minOccurs="0"/>
 *         &lt;element name="Firmware2Version" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="Firmware2Type" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableInt" minOccurs="0"/>
 *         &lt;element name="SerialNumber" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="DeviceType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceTypeEnumeration" minOccurs="0"/>
 *         &lt;element name="ESIMacID" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="UtilityEnrollmentGroup" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableUtilityEnrollmentGroupType" minOccurs="0"/>
 *         &lt;element name="SEPVersion" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}NullableSEPVersionType" minOccurs="0"/>
 *         &lt;element name="InstallCode" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="PreConfiguredLinkKey" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableString" minOccurs="0"/>
 *         &lt;element name="HANDeviceClass" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}NullableHANDeviceClassEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditZigbeeAttributeType", propOrder = {
    "primaryAttributes",
    "manufacturerName",
    "manufacturerModel",
    "hardwareVersion",
    "firmware1Version",
    "firmware1Type",
    "firmware2Version",
    "firmware2Type",
    "serialNumber",
    "deviceType",
    "esiMacID",
    "utilityEnrollmentGroup",
    "sepVersion",
    "installCode",
    "preConfiguredLinkKey",
    "hanDeviceClass"
})
public class EditZigbeeAttributeType {

    @XmlElement(name = "PrimaryAttributes", required = true)
    protected EditPrimaryHANDeviceType primaryAttributes;
    @XmlElement(name = "ManufacturerName")
    protected NullableString manufacturerName;
    @XmlElement(name = "ManufacturerModel")
    protected NullableString manufacturerModel;
    @XmlElement(name = "HardwareVersion")
    protected NullableString hardwareVersion;
    @XmlElement(name = "Firmware1Version")
    protected NullableString firmware1Version;
    @XmlElement(name = "Firmware1Type")
    protected NullableInt firmware1Type;
    @XmlElement(name = "Firmware2Version")
    protected NullableString firmware2Version;
    @XmlElement(name = "Firmware2Type")
    protected NullableInt firmware2Type;
    @XmlElement(name = "SerialNumber")
    protected NullableString serialNumber;
    @XmlElement(name = "DeviceType")
    protected DeviceTypeEnumeration deviceType;
    @XmlElement(name = "ESIMacID")
    protected NullableString esiMacID;
    @XmlElement(name = "UtilityEnrollmentGroup")
    protected NullableUtilityEnrollmentGroupType utilityEnrollmentGroup;
    @XmlElement(name = "SEPVersion")
    protected NullableSEPVersionType sepVersion;
    @XmlElement(name = "InstallCode")
    protected NullableString installCode;
    @XmlElement(name = "PreConfiguredLinkKey")
    protected NullableString preConfiguredLinkKey;
    @XmlElement(name = "HANDeviceClass")
    protected NullableHANDeviceClassEnumeration hanDeviceClass;

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
     * Gets the value of the manufacturerName property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the value of the manufacturerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setManufacturerName(NullableString value) {
        this.manufacturerName = value;
    }

    /**
     * Gets the value of the manufacturerModel property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getManufacturerModel() {
        return manufacturerModel;
    }

    /**
     * Sets the value of the manufacturerModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setManufacturerModel(NullableString value) {
        this.manufacturerModel = value;
    }

    /**
     * Gets the value of the hardwareVersion property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getHardwareVersion() {
        return hardwareVersion;
    }

    /**
     * Sets the value of the hardwareVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setHardwareVersion(NullableString value) {
        this.hardwareVersion = value;
    }

    /**
     * Gets the value of the firmware1Version property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getFirmware1Version() {
        return firmware1Version;
    }

    /**
     * Sets the value of the firmware1Version property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setFirmware1Version(NullableString value) {
        this.firmware1Version = value;
    }

    /**
     * Gets the value of the firmware1Type property.
     * 
     * @return
     *     possible object is
     *     {@link NullableInt }
     *     
     */
    public NullableInt getFirmware1Type() {
        return firmware1Type;
    }

    /**
     * Sets the value of the firmware1Type property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableInt }
     *     
     */
    public void setFirmware1Type(NullableInt value) {
        this.firmware1Type = value;
    }

    /**
     * Gets the value of the firmware2Version property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getFirmware2Version() {
        return firmware2Version;
    }

    /**
     * Sets the value of the firmware2Version property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setFirmware2Version(NullableString value) {
        this.firmware2Version = value;
    }

    /**
     * Gets the value of the firmware2Type property.
     * 
     * @return
     *     possible object is
     *     {@link NullableInt }
     *     
     */
    public NullableInt getFirmware2Type() {
        return firmware2Type;
    }

    /**
     * Sets the value of the firmware2Type property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableInt }
     *     
     */
    public void setFirmware2Type(NullableInt value) {
        this.firmware2Type = value;
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
     * Gets the value of the esiMacID property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getESIMacID() {
        return esiMacID;
    }

    /**
     * Sets the value of the esiMacID property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setESIMacID(NullableString value) {
        this.esiMacID = value;
    }

    /**
     * Gets the value of the utilityEnrollmentGroup property.
     * 
     * @return
     *     possible object is
     *     {@link NullableUtilityEnrollmentGroupType }
     *     
     */
    public NullableUtilityEnrollmentGroupType getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }

    /**
     * Sets the value of the utilityEnrollmentGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableUtilityEnrollmentGroupType }
     *     
     */
    public void setUtilityEnrollmentGroup(NullableUtilityEnrollmentGroupType value) {
        this.utilityEnrollmentGroup = value;
    }

    /**
     * Gets the value of the sepVersion property.
     * 
     * @return
     *     possible object is
     *     {@link NullableSEPVersionType }
     *     
     */
    public NullableSEPVersionType getSEPVersion() {
        return sepVersion;
    }

    /**
     * Sets the value of the sepVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableSEPVersionType }
     *     
     */
    public void setSEPVersion(NullableSEPVersionType value) {
        this.sepVersion = value;
    }

    /**
     * Gets the value of the installCode property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getInstallCode() {
        return installCode;
    }

    /**
     * Sets the value of the installCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setInstallCode(NullableString value) {
        this.installCode = value;
    }

    /**
     * Gets the value of the preConfiguredLinkKey property.
     * 
     * @return
     *     possible object is
     *     {@link NullableString }
     *     
     */
    public NullableString getPreConfiguredLinkKey() {
        return preConfiguredLinkKey;
    }

    /**
     * Sets the value of the preConfiguredLinkKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableString }
     *     
     */
    public void setPreConfiguredLinkKey(NullableString value) {
        this.preConfiguredLinkKey = value;
    }

    /**
     * Gets the value of the hanDeviceClass property.
     * 
     * @return
     *     possible object is
     *     {@link NullableHANDeviceClassEnumeration }
     *     
     */
    public NullableHANDeviceClassEnumeration getHANDeviceClass() {
        return hanDeviceClass;
    }

    /**
     * Sets the value of the hanDeviceClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullableHANDeviceClassEnumeration }
     *     
     */
    public void setHANDeviceClass(NullableHANDeviceClassEnumeration value) {
        this.hanDeviceClass = value;
    }

}

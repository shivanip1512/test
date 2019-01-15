
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FindDeviceCommonAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FindDeviceCommonAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MacID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServicePointID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProgramID" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ProgramIDType" minOccurs="0"/>
 *         &lt;element name="BillingLocationType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}BillingLocationTypeEnumeration" minOccurs="0"/>
 *         &lt;element name="GridNode" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}GridNodeType" minOccurs="0"/>
 *         &lt;element name="HardwareVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FirmwareVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServicePointCustomAttribute1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServicePointCustomAttribute2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServicePointCustomAttribute3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServicePointCustomAttribute4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServicePointCustomAttribute5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindDeviceCommonAttributeType", propOrder = {
    "serialNumber",
    "macID",
    "servicePointID",
    "programID",
    "billingLocationType",
    "gridNode",
    "hardwareVersion",
    "firmwareVersion",
    "city",
    "state",
    "postalCode",
    "servicePointCustomAttribute1",
    "servicePointCustomAttribute2",
    "servicePointCustomAttribute3",
    "servicePointCustomAttribute4",
    "servicePointCustomAttribute5"
})
public class FindDeviceCommonAttributeType {

    @XmlElement(name = "SerialNumber")
    protected String serialNumber;
    @XmlElement(name = "MacID")
    protected String macID;
    @XmlElement(name = "ServicePointID")
    protected String servicePointID;
    @XmlElement(name = "ProgramID")
    protected Long programID;
    @XmlElement(name = "BillingLocationType")
    protected BillingLocationTypeEnumeration billingLocationType;
    @XmlElement(name = "GridNode")
    protected GridNodeType gridNode;
    @XmlElement(name = "HardwareVersion")
    protected String hardwareVersion;
    @XmlElement(name = "FirmwareVersion")
    protected String firmwareVersion;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "State")
    protected String state;
    @XmlElement(name = "PostalCode")
    protected String postalCode;
    @XmlElement(name = "ServicePointCustomAttribute1")
    protected String servicePointCustomAttribute1;
    @XmlElement(name = "ServicePointCustomAttribute2")
    protected String servicePointCustomAttribute2;
    @XmlElement(name = "ServicePointCustomAttribute3")
    protected String servicePointCustomAttribute3;
    @XmlElement(name = "ServicePointCustomAttribute4")
    protected String servicePointCustomAttribute4;
    @XmlElement(name = "ServicePointCustomAttribute5")
    protected String servicePointCustomAttribute5;

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

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
     * Gets the value of the servicePointID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointID() {
        return servicePointID;
    }

    /**
     * Sets the value of the servicePointID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointID(String value) {
        this.servicePointID = value;
    }

    /**
     * Gets the value of the programID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getProgramID() {
        return programID;
    }

    /**
     * Sets the value of the programID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setProgramID(Long value) {
        this.programID = value;
    }

    /**
     * Gets the value of the billingLocationType property.
     * 
     * @return
     *     possible object is
     *     {@link BillingLocationTypeEnumeration }
     *     
     */
    public BillingLocationTypeEnumeration getBillingLocationType() {
        return billingLocationType;
    }

    /**
     * Sets the value of the billingLocationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillingLocationTypeEnumeration }
     *     
     */
    public void setBillingLocationType(BillingLocationTypeEnumeration value) {
        this.billingLocationType = value;
    }

    /**
     * Gets the value of the gridNode property.
     * 
     * @return
     *     possible object is
     *     {@link GridNodeType }
     *     
     */
    public GridNodeType getGridNode() {
        return gridNode;
    }

    /**
     * Sets the value of the gridNode property.
     * 
     * @param value
     *     allowed object is
     *     {@link GridNodeType }
     *     
     */
    public void setGridNode(GridNodeType value) {
        this.gridNode = value;
    }

    /**
     * Gets the value of the hardwareVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHardwareVersion() {
        return hardwareVersion;
    }

    /**
     * Sets the value of the hardwareVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHardwareVersion(String value) {
        this.hardwareVersion = value;
    }

    /**
     * Gets the value of the firmwareVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * Sets the value of the firmwareVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirmwareVersion(String value) {
        this.firmwareVersion = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the servicePointCustomAttribute1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointCustomAttribute1() {
        return servicePointCustomAttribute1;
    }

    /**
     * Sets the value of the servicePointCustomAttribute1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointCustomAttribute1(String value) {
        this.servicePointCustomAttribute1 = value;
    }

    /**
     * Gets the value of the servicePointCustomAttribute2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointCustomAttribute2() {
        return servicePointCustomAttribute2;
    }

    /**
     * Sets the value of the servicePointCustomAttribute2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointCustomAttribute2(String value) {
        this.servicePointCustomAttribute2 = value;
    }

    /**
     * Gets the value of the servicePointCustomAttribute3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointCustomAttribute3() {
        return servicePointCustomAttribute3;
    }

    /**
     * Sets the value of the servicePointCustomAttribute3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointCustomAttribute3(String value) {
        this.servicePointCustomAttribute3 = value;
    }

    /**
     * Gets the value of the servicePointCustomAttribute4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointCustomAttribute4() {
        return servicePointCustomAttribute4;
    }

    /**
     * Sets the value of the servicePointCustomAttribute4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointCustomAttribute4(String value) {
        this.servicePointCustomAttribute4 = value;
    }

    /**
     * Gets the value of the servicePointCustomAttribute5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointCustomAttribute5() {
        return servicePointCustomAttribute5;
    }

    /**
     * Sets the value of the servicePointCustomAttribute5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointCustomAttribute5(String value) {
        this.servicePointCustomAttribute5 = value;
    }

}

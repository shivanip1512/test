
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FindZigbeeAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FindZigbeeAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransportTypeZigbee" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}TransportTypeZigbeeEnumeration"/>
 *         &lt;element name="UtilityEnrollmentGroupNumber" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}UtilityEnrollmentGroupNumberType" minOccurs="0"/>
 *         &lt;element name="SupportedSEPCluster" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDeviceSEPClusterEnumeration" minOccurs="0"/>
 *         &lt;element name="ConnectivityStatus" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ConnectivityStatusEnumeration" minOccurs="0"/>
 *         &lt;element name="SEPVersion" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}SEPVersionEnumeration" minOccurs="0"/>
 *         &lt;element name="HANDeviceClass" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDeviceClassEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindZigbeeAttributeType", propOrder = {
    "transportTypeZigbee",
    "utilityEnrollmentGroupNumber",
    "supportedSEPCluster",
    "connectivityStatus",
    "sepVersion",
    "hanDeviceClass"
})
public class FindZigbeeAttributeType {

    @XmlElement(name = "TransportTypeZigbee", required = true)
    protected TransportTypeZigbeeEnumeration transportTypeZigbee;
    @XmlElement(name = "UtilityEnrollmentGroupNumber")
    protected Integer utilityEnrollmentGroupNumber;
    @XmlElement(name = "SupportedSEPCluster")
    protected HANDeviceSEPClusterEnumeration supportedSEPCluster;
    @XmlElement(name = "ConnectivityStatus")
    protected ConnectivityStatusEnumeration connectivityStatus;
    @XmlElement(name = "SEPVersion")
    protected String sepVersion;
    @XmlElement(name = "HANDeviceClass")
    protected HANDeviceClassEnumeration hanDeviceClass;

    /**
     * Gets the value of the transportTypeZigbee property.
     * 
     * @return
     *     possible object is
     *     {@link TransportTypeZigbeeEnumeration }
     *     
     */
    public TransportTypeZigbeeEnumeration getTransportTypeZigbee() {
        return transportTypeZigbee;
    }

    /**
     * Sets the value of the transportTypeZigbee property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportTypeZigbeeEnumeration }
     *     
     */
    public void setTransportTypeZigbee(TransportTypeZigbeeEnumeration value) {
        this.transportTypeZigbee = value;
    }

    /**
     * Gets the value of the utilityEnrollmentGroupNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUtilityEnrollmentGroupNumber() {
        return utilityEnrollmentGroupNumber;
    }

    /**
     * Sets the value of the utilityEnrollmentGroupNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUtilityEnrollmentGroupNumber(Integer value) {
        this.utilityEnrollmentGroupNumber = value;
    }

    /**
     * Gets the value of the supportedSEPCluster property.
     * 
     * @return
     *     possible object is
     *     {@link HANDeviceSEPClusterEnumeration }
     *     
     */
    public HANDeviceSEPClusterEnumeration getSupportedSEPCluster() {
        return supportedSEPCluster;
    }

    /**
     * Sets the value of the supportedSEPCluster property.
     * 
     * @param value
     *     allowed object is
     *     {@link HANDeviceSEPClusterEnumeration }
     *     
     */
    public void setSupportedSEPCluster(HANDeviceSEPClusterEnumeration value) {
        this.supportedSEPCluster = value;
    }

    /**
     * Gets the value of the connectivityStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectivityStatusEnumeration }
     *     
     */
    public ConnectivityStatusEnumeration getConnectivityStatus() {
        return connectivityStatus;
    }

    /**
     * Sets the value of the connectivityStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectivityStatusEnumeration }
     *     
     */
    public void setConnectivityStatus(ConnectivityStatusEnumeration value) {
        this.connectivityStatus = value;
    }

    /**
     * Gets the value of the sepVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEPVersion() {
        return sepVersion;
    }

    /**
     * Sets the value of the sepVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEPVersion(String value) {
        this.sepVersion = value;
    }

    /**
     * Gets the value of the hanDeviceClass property.
     * 
     * @return
     *     possible object is
     *     {@link HANDeviceClassEnumeration }
     *     
     */
    public HANDeviceClassEnumeration getHANDeviceClass() {
        return hanDeviceClass;
    }

    /**
     * Sets the value of the hanDeviceClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link HANDeviceClassEnumeration }
     *     
     */
    public void setHANDeviceClass(HANDeviceClassEnumeration value) {
        this.hanDeviceClass = value;
    }

}

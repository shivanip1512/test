
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AssociatedHANDeviceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AssociatedHANDeviceType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FindHANDeviceBaseType">
 *       &lt;sequence>
 *         &lt;element name="ZigbeeUtilityEnrollmentGroupNumber" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}UtilityEnrollmentGroupNumberType" minOccurs="0"/>
 *         &lt;element name="ZigbeeConnectivityStatus" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ConnectivityStatusEnumeration" minOccurs="0"/>
 *         &lt;element name="D2GCLPSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="TransportType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}TransportTypeEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssociatedHANDeviceType", propOrder = {
    "zigbeeUtilityEnrollmentGroupNumber",
    "zigbeeConnectivityStatus",
    "d2GCLPSupported",
    "transportType"
})
public class AssociatedHANDeviceType
    extends FindHANDeviceBaseType
{

    @XmlElement(name = "ZigbeeUtilityEnrollmentGroupNumber")
    protected Integer zigbeeUtilityEnrollmentGroupNumber;
    @XmlElement(name = "ZigbeeConnectivityStatus")
    protected ConnectivityStatusEnumeration zigbeeConnectivityStatus;
    @XmlElement(name = "D2GCLPSupported")
    protected Boolean d2GCLPSupported;
    @XmlElement(name = "TransportType")
    protected TransportTypeEnumeration transportType;

    /**
     * Gets the value of the zigbeeUtilityEnrollmentGroupNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getZigbeeUtilityEnrollmentGroupNumber() {
        return zigbeeUtilityEnrollmentGroupNumber;
    }

    /**
     * Sets the value of the zigbeeUtilityEnrollmentGroupNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setZigbeeUtilityEnrollmentGroupNumber(Integer value) {
        this.zigbeeUtilityEnrollmentGroupNumber = value;
    }

    /**
     * Gets the value of the zigbeeConnectivityStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectivityStatusEnumeration }
     *     
     */
    public ConnectivityStatusEnumeration getZigbeeConnectivityStatus() {
        return zigbeeConnectivityStatus;
    }

    /**
     * Sets the value of the zigbeeConnectivityStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectivityStatusEnumeration }
     *     
     */
    public void setZigbeeConnectivityStatus(ConnectivityStatusEnumeration value) {
        this.zigbeeConnectivityStatus = value;
    }

    /**
     * Gets the value of the d2GCLPSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isD2GCLPSupported() {
        return d2GCLPSupported;
    }

    /**
     * Sets the value of the d2GCLPSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setD2GCLPSupported(Boolean value) {
        this.d2GCLPSupported = value;
    }

    /**
     * Gets the value of the transportType property.
     * 
     * @return
     *     possible object is
     *     {@link TransportTypeEnumeration }
     *     
     */
    public TransportTypeEnumeration getTransportType() {
        return transportType;
    }

    /**
     * Sets the value of the transportType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportTypeEnumeration }
     *     
     */
    public void setTransportType(TransportTypeEnumeration value) {
        this.transportType = value;
    }

}

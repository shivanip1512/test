
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FindD2GAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FindD2GAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransportTypeD2G" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}TransportTypeD2GEnumeration"/>
 *         &lt;element name="CLPSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Configured" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindD2GAttributeType", propOrder = {
    "transportTypeD2G",
    "clpSupported",
    "configured"
})
public class FindD2GAttributeType {

    @XmlElement(name = "TransportTypeD2G", required = true)
    protected TransportTypeD2GEnumeration transportTypeD2G;
    @XmlElement(name = "CLPSupported")
    protected Boolean clpSupported;
    @XmlElement(name = "Configured")
    protected Boolean configured;

    /**
     * Gets the value of the transportTypeD2G property.
     * 
     * @return
     *     possible object is
     *     {@link TransportTypeD2GEnumeration }
     *     
     */
    public TransportTypeD2GEnumeration getTransportTypeD2G() {
        return transportTypeD2G;
    }

    /**
     * Sets the value of the transportTypeD2G property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportTypeD2GEnumeration }
     *     
     */
    public void setTransportTypeD2G(TransportTypeD2GEnumeration value) {
        this.transportTypeD2G = value;
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

}

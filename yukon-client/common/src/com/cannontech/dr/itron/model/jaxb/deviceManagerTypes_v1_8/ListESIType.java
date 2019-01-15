
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListESIType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListESIType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ESIType">
 *       &lt;sequence>
 *         &lt;element name="SupportsZigbee" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="SupportsD2GLCS" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="SupportsEVSE" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListESIType", propOrder = {
    "supportsZigbee",
    "supportsD2GLCS",
    "supportsEVSE"
})
public class ListESIType
    extends ESIType
{

    @XmlElement(name = "SupportsZigbee")
    protected Boolean supportsZigbee;
    @XmlElement(name = "SupportsD2GLCS")
    protected Boolean supportsD2GLCS;
    @XmlElement(name = "SupportsEVSE")
    protected Boolean supportsEVSE;

    /**
     * Gets the value of the supportsZigbee property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSupportsZigbee() {
        return supportsZigbee;
    }

    /**
     * Sets the value of the supportsZigbee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSupportsZigbee(Boolean value) {
        this.supportsZigbee = value;
    }

    /**
     * Gets the value of the supportsD2GLCS property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSupportsD2GLCS() {
        return supportsD2GLCS;
    }

    /**
     * Sets the value of the supportsD2GLCS property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSupportsD2GLCS(Boolean value) {
        this.supportsD2GLCS = value;
    }

    /**
     * Gets the value of the supportsEVSE property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSupportsEVSE() {
        return supportsEVSE;
    }

    /**
     * Sets the value of the supportsEVSE property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSupportsEVSE(Boolean value) {
        this.supportsEVSE = value;
    }

}


package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ESIReadinessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ESIReadinessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EnrolledInProgram" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Provisioned" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Connected" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Retired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ESIReadinessType", propOrder = {
    "enrolledInProgram",
    "provisioned",
    "connected",
    "retired"
})
public class ESIReadinessType {

    @XmlElement(name = "EnrolledInProgram")
    protected Boolean enrolledInProgram;
    @XmlElement(name = "Provisioned")
    protected Boolean provisioned;
    @XmlElement(name = "Connected")
    protected Boolean connected;
    @XmlElement(name = "Retired")
    protected Boolean retired;

    /**
     * Gets the value of the enrolledInProgram property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnrolledInProgram() {
        return enrolledInProgram;
    }

    /**
     * Sets the value of the enrolledInProgram property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnrolledInProgram(Boolean value) {
        this.enrolledInProgram = value;
    }

    /**
     * Gets the value of the provisioned property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isProvisioned() {
        return provisioned;
    }

    /**
     * Sets the value of the provisioned property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProvisioned(Boolean value) {
        this.provisioned = value;
    }

    /**
     * Gets the value of the connected property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isConnected() {
        return connected;
    }

    /**
     * Sets the value of the connected property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setConnected(Boolean value) {
        this.connected = value;
    }

    /**
     * Gets the value of the retired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRetired() {
        return retired;
    }

    /**
     * Sets the value of the retired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRetired(Boolean value) {
        this.retired = value;
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.14 at 09:30:13 AM CST 
//


package org.oasis_open.docs.ns.energyinterop._201110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Fully Qualified Event ID includes the eventID and the Modification Number
 * 
 * <p>Java class for QualifiedEventIDType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QualifiedEventIDType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}eventID"/>
 *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}modificationNumber"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QualifiedEventIDType", propOrder = {
    "eventID",
    "modificationNumber"
})
public class QualifiedEventIDType {

    @XmlElement(required = true)
    protected String eventID;
    @XmlSchemaType(name = "unsignedInt")
    protected long modificationNumber;

    /**
     * Gets the value of the eventID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the value of the eventID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventID(String value) {
        this.eventID = value;
    }

    /**
     * Gets the value of the modificationNumber property.
     * 
     */
    public long getModificationNumber() {
        return modificationNumber;
    }

    /**
     * Sets the value of the modificationNumber property.
     * 
     */
    public void setModificationNumber(long value) {
        this.modificationNumber = value;
    }

}

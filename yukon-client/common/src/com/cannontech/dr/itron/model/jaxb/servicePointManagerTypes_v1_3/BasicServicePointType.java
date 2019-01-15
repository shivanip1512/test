
package com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BasicServicePointType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BasicServicePointType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UtilServicePointID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FeederDistNetUtilID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubstationDistNetUtilID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransformerDistNetUtilID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransmissionDistNetUtilID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BasicServicePointType", propOrder = {
    "utilServicePointID",
    "feederDistNetUtilID",
    "substationDistNetUtilID",
    "transformerDistNetUtilID",
    "transmissionDistNetUtilID",
    "attribute1",
    "attribute2",
    "attribute3",
    "attribute4",
    "attribute5"
})
@XmlSeeAlso({
    AddServicePointType.class,
    EditServicePointType.class
})
public class BasicServicePointType {

    @XmlElement(name = "UtilServicePointID", required = true)
    protected String utilServicePointID;
    @XmlElement(name = "FeederDistNetUtilID")
    protected String feederDistNetUtilID;
    @XmlElement(name = "SubstationDistNetUtilID")
    protected String substationDistNetUtilID;
    @XmlElement(name = "TransformerDistNetUtilID")
    protected String transformerDistNetUtilID;
    @XmlElement(name = "TransmissionDistNetUtilID")
    protected String transmissionDistNetUtilID;
    @XmlElement(name = "Attribute1")
    protected String attribute1;
    @XmlElement(name = "Attribute2")
    protected String attribute2;
    @XmlElement(name = "Attribute3")
    protected String attribute3;
    @XmlElement(name = "Attribute4")
    protected String attribute4;
    @XmlElement(name = "Attribute5")
    protected String attribute5;

    /**
     * Gets the value of the utilServicePointID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUtilServicePointID() {
        return utilServicePointID;
    }

    /**
     * Sets the value of the utilServicePointID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUtilServicePointID(String value) {
        this.utilServicePointID = value;
    }

    /**
     * Gets the value of the feederDistNetUtilID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeederDistNetUtilID() {
        return feederDistNetUtilID;
    }

    /**
     * Sets the value of the feederDistNetUtilID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeederDistNetUtilID(String value) {
        this.feederDistNetUtilID = value;
    }

    /**
     * Gets the value of the substationDistNetUtilID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubstationDistNetUtilID() {
        return substationDistNetUtilID;
    }

    /**
     * Sets the value of the substationDistNetUtilID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubstationDistNetUtilID(String value) {
        this.substationDistNetUtilID = value;
    }

    /**
     * Gets the value of the transformerDistNetUtilID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransformerDistNetUtilID() {
        return transformerDistNetUtilID;
    }

    /**
     * Sets the value of the transformerDistNetUtilID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransformerDistNetUtilID(String value) {
        this.transformerDistNetUtilID = value;
    }

    /**
     * Gets the value of the transmissionDistNetUtilID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransmissionDistNetUtilID() {
        return transmissionDistNetUtilID;
    }

    /**
     * Sets the value of the transmissionDistNetUtilID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransmissionDistNetUtilID(String value) {
        this.transmissionDistNetUtilID = value;
    }

    /**
     * Gets the value of the attribute1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute1() {
        return attribute1;
    }

    /**
     * Sets the value of the attribute1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute1(String value) {
        this.attribute1 = value;
    }

    /**
     * Gets the value of the attribute2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute2() {
        return attribute2;
    }

    /**
     * Sets the value of the attribute2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute2(String value) {
        this.attribute2 = value;
    }

    /**
     * Gets the value of the attribute3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute3() {
        return attribute3;
    }

    /**
     * Sets the value of the attribute3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute3(String value) {
        this.attribute3 = value;
    }

    /**
     * Gets the value of the attribute4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute4() {
        return attribute4;
    }

    /**
     * Sets the value of the attribute4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute4(String value) {
        this.attribute4 = value;
    }

    /**
     * Gets the value of the attribute5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute5() {
        return attribute5;
    }

    /**
     * Sets the value of the attribute5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute5(String value) {
        this.attribute5 = value;
    }

}

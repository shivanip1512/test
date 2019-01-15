
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DistributionNodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DistributionNodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AmpCapacity" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="DistNetUtilID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistributionNodeType", propOrder = {
    "ampCapacity",
    "distNetUtilID"
})
@XmlSeeAlso({
    TransmissionCircuitType.class,
    FeederType.class,
    TransformerType.class,
    SubstationType.class
})
public class DistributionNodeType {

    @XmlElement(name = "AmpCapacity")
    protected Double ampCapacity;
    @XmlElement(name = "DistNetUtilID")
    protected String distNetUtilID;

    /**
     * Gets the value of the ampCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAmpCapacity() {
        return ampCapacity;
    }

    /**
     * Sets the value of the ampCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAmpCapacity(Double value) {
        this.ampCapacity = value;
    }

    /**
     * Gets the value of the distNetUtilID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistNetUtilID() {
        return distNetUtilID;
    }

    /**
     * Sets the value of the distNetUtilID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistNetUtilID(String value) {
        this.distNetUtilID = value;
    }

}

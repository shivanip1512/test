
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransformerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransformerType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DistributionNodeType">
 *       &lt;sequence>
 *         &lt;element name="TransTransDist" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransInstCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformerType", propOrder = {
    "transTransDist",
    "transInstCode"
})
public class TransformerType
    extends DistributionNodeType
{

    @XmlElement(name = "TransTransDist")
    protected String transTransDist;
    @XmlElement(name = "TransInstCode")
    protected Integer transInstCode;

    /**
     * Gets the value of the transTransDist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransTransDist() {
        return transTransDist;
    }

    /**
     * Sets the value of the transTransDist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransTransDist(String value) {
        this.transTransDist = value;
    }

    /**
     * Gets the value of the transInstCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTransInstCode() {
        return transInstCode;
    }

    /**
     * Sets the value of the transInstCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTransInstCode(Integer value) {
        this.transInstCode = value;
    }

}

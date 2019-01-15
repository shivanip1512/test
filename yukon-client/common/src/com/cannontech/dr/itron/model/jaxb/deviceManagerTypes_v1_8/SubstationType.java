
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SubstationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubstationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DistributionNodeType">
 *       &lt;sequence>
 *         &lt;element name="SubstationBankCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubstationFeederCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubstationType", propOrder = {
    "substationBankCode",
    "substationFeederCode"
})
public class SubstationType
    extends DistributionNodeType
{

    @XmlElement(name = "SubstationBankCode")
    protected String substationBankCode;
    @XmlElement(name = "SubstationFeederCode")
    protected String substationFeederCode;

    /**
     * Gets the value of the substationBankCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubstationBankCode() {
        return substationBankCode;
    }

    /**
     * Sets the value of the substationBankCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubstationBankCode(String value) {
        this.substationBankCode = value;
    }

    /**
     * Gets the value of the substationFeederCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubstationFeederCode() {
        return substationFeederCode;
    }

    /**
     * Sets the value of the substationFeederCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubstationFeederCode(String value) {
        this.substationFeederCode = value;
    }

}

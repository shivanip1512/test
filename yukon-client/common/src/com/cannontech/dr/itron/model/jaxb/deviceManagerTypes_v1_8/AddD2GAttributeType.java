
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddD2GAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddD2GAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ESI" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ESIType"/>
 *         &lt;element name="ServicePointUtilID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddD2GAttributeType", propOrder = {
    "esi",
    "servicePointUtilID"
})
public class AddD2GAttributeType {

    @XmlElement(name = "ESI", required = true)
    protected ESIType esi;
    @XmlElement(name = "ServicePointUtilID")
    protected String servicePointUtilID;

    /**
     * Gets the value of the esi property.
     * 
     * @return
     *     possible object is
     *     {@link ESIType }
     *     
     */
    public ESIType getESI() {
        return esi;
    }

    /**
     * Sets the value of the esi property.
     * 
     * @param value
     *     allowed object is
     *     {@link ESIType }
     *     
     */
    public void setESI(ESIType value) {
        this.esi = value;
    }

    /**
     * Gets the value of the servicePointUtilID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointUtilID() {
        return servicePointUtilID;
    }

    /**
     * Sets the value of the servicePointUtilID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointUtilID(String value) {
        this.servicePointUtilID = value;
    }

}


package com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EditServicePointResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditServicePointResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UtilServicePointID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditServicePointResponseType", propOrder = {
    "utilServicePointID"
})
@XmlRootElement(name = "EditServicePointResponse")
public class EditServicePointResponse {

    @XmlElement(name = "UtilServicePointID", required = true)
    protected String utilServicePointID;

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

}

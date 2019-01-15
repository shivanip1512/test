
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ESIGroupErrorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ESIGroupErrorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Error" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDevicesErrorEnumeration"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ESIGroupErrorType", propOrder = {
    "macID",
    "error"
})
public class ESIGroupErrorType {

    @XmlElement(name = "MacID", required = true)
    protected String macID;
    @XmlElement(name = "Error", required = true)
    protected HANDevicesErrorEnumeration error;

    /**
     * Gets the value of the macID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacID() {
        return macID;
    }

    /**
     * Sets the value of the macID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacID(String value) {
        this.macID = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link HANDevicesErrorEnumeration }
     *     
     */
    public HANDevicesErrorEnumeration getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link HANDevicesErrorEnumeration }
     *     
     */
    public void setError(HANDevicesErrorEnumeration value) {
        this.error = value;
    }

}

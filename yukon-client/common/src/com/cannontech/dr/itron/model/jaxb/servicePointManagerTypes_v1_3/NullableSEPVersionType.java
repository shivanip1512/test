
package com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NullableSEPVersionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NullableSEPVersionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SEPVersion" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}SEPVersionEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Null" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NullableSEPVersionType", namespace = "urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd", propOrder = {
    "sepVersion"
})
public class NullableSEPVersionType {

    @XmlElement(name = "SEPVersion")
    protected String sepVersion;
    @XmlAttribute(name = "Null")
    protected Boolean _null;

    /**
     * Gets the value of the sepVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEPVersion() {
        return sepVersion;
    }

    /**
     * Sets the value of the sepVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEPVersion(String value) {
        this.sepVersion = value;
    }

    /**
     * Gets the value of the null property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isNull() {
        if (_null == null) {
            return false;
        } else {
            return _null;
        }
    }

    /**
     * Sets the value of the null property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNull(Boolean value) {
        this._null = value;
    }

}

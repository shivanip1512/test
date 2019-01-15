
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NullableInputCapacityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NullableInputCapacityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InputCapacity" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}InputCapacityType" minOccurs="0"/>
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
@XmlType(name = "NullableInputCapacityType", propOrder = {
    "inputCapacity"
})
public class NullableInputCapacityType {

    @XmlElement(name = "InputCapacity")
    protected InputCapacityType inputCapacity;
    @XmlAttribute(name = "Null")
    protected Boolean _null;

    /**
     * Gets the value of the inputCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link InputCapacityType }
     *     
     */
    public InputCapacityType getInputCapacity() {
        return inputCapacity;
    }

    /**
     * Sets the value of the inputCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputCapacityType }
     *     
     */
    public void setInputCapacity(InputCapacityType value) {
        this.inputCapacity = value;
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

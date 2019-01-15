
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InputCapacityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputCapacityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InputCapacityValue" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="InputCapacityUnit" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}InputCapacityUnitEnumeration"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputCapacityType", propOrder = {
    "inputCapacityValue",
    "inputCapacityUnit"
})
public class InputCapacityType {

    @XmlElement(name = "InputCapacityValue")
    protected double inputCapacityValue;
    @XmlElement(name = "InputCapacityUnit", required = true)
    protected InputCapacityUnitEnumeration inputCapacityUnit;

    /**
     * Gets the value of the inputCapacityValue property.
     * 
     */
    public double getInputCapacityValue() {
        return inputCapacityValue;
    }

    /**
     * Sets the value of the inputCapacityValue property.
     * 
     */
    public void setInputCapacityValue(double value) {
        this.inputCapacityValue = value;
    }

    /**
     * Gets the value of the inputCapacityUnit property.
     * 
     * @return
     *     possible object is
     *     {@link InputCapacityUnitEnumeration }
     *     
     */
    public InputCapacityUnitEnumeration getInputCapacityUnit() {
        return inputCapacityUnit;
    }

    /**
     * Sets the value of the inputCapacityUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputCapacityUnitEnumeration }
     *     
     */
    public void setInputCapacityUnit(InputCapacityUnitEnumeration value) {
        this.inputCapacityUnit = value;
    }

}

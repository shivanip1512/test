
package com.cannontech.common.device.config.model.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InputFloat complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputFloat">
 *   &lt;complexContent>
 *     &lt;extension base="{}InputBase">
 *       &lt;attribute name="minValue" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="maxValue" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="decimalDigits" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputFloat")
public class InputFloat
    extends InputBase
{

    @XmlAttribute(name = "minValue")
    protected Float minValue;
    @XmlAttribute(name = "maxValue")
    protected Float maxValue;
    @XmlAttribute(name = "decimalDigits")
    protected Integer decimalDigits;

    /**
     * Gets the value of the minValue property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMinValue() {
        return minValue;
    }

    /**
     * Sets the value of the minValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMinValue(Float value) {
        this.minValue = value;
    }

    /**
     * Gets the value of the maxValue property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the value of the maxValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMaxValue(Float value) {
        this.maxValue = value;
    }

    /**
     * Gets the value of the decimalDigits property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    /**
     * Sets the value of the decimalDigits property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDecimalDigits(Integer value) {
        this.decimalDigits = value;
    }

}

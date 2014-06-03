
package com.cannontech.common.device.config.model.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InputEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputEnum">
 *   &lt;complexContent>
 *     &lt;extension base="{}InputBase">
 *       &lt;attribute name="type" use="required" type="{}EnumOption" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputEnum")
public class InputEnum
    extends InputBase
{

    @XmlAttribute(name = "type", required = true)
    protected EnumOption type;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EnumOption }
     *     
     */
    public EnumOption getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumOption }
     *     
     */
    public void setType(EnumOption value) {
        this.type = value;
    }

}

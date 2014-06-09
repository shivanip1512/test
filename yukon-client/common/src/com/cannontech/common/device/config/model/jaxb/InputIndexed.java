
package com.cannontech.common.device.config.model.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InputIndexed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputIndexed">
 *   &lt;complexContent>
 *     &lt;extension base="{}InputBase">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="integer" type="{}InputInteger"/>
 *         &lt;element name="float" type="{}InputFloat"/>
 *         &lt;element name="boolean" type="{}InputBoolean"/>
 *         &lt;element name="enum" type="{}InputEnum"/>
 *         &lt;element name="map" type="{}InputMap"/>
 *       &lt;/choice>
 *       &lt;attribute name="minOccurs" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="maxOccurs" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputIndexed", propOrder = {
    "integerOrFloatOrBoolean"
})
public class InputIndexed
    extends InputBase
{

    @XmlElements({
        @XmlElement(name = "integer", type = InputInteger.class),
        @XmlElement(name = "float", type = InputFloat.class),
        @XmlElement(name = "boolean", type = InputBoolean.class),
        @XmlElement(name = "enum", type = InputEnum.class),
        @XmlElement(name = "map", type = InputMap.class)
    })
    protected List<InputBase> integerOrFloatOrBoolean;
    @XmlAttribute(name = "minOccurs", required = true)
    protected int minOccurs;
    @XmlAttribute(name = "maxOccurs", required = true)
    protected int maxOccurs;

    /**
     * Gets the value of the integerOrFloatOrBoolean property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the integerOrFloatOrBoolean property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntegerOrFloatOrBoolean().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InputInteger }
     * {@link InputFloat }
     * {@link InputBoolean }
     * {@link InputEnum }
     * {@link InputMap }
     * 
     * 
     */
    public List<InputBase> getIntegerOrFloatOrBoolean() {
        if (integerOrFloatOrBoolean == null) {
            integerOrFloatOrBoolean = new ArrayList<InputBase>();
        }
        return this.integerOrFloatOrBoolean;
    }

    /**
     * Gets the value of the minOccurs property.
     * 
     */
    public int getMinOccurs() {
        return minOccurs;
    }

    /**
     * Sets the value of the minOccurs property.
     * 
     */
    public void setMinOccurs(int value) {
        this.minOccurs = value;
    }

    /**
     * Gets the value of the maxOccurs property.
     * 
     */
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * Sets the value of the maxOccurs property.
     * 
     */
    public void setMaxOccurs(int value) {
        this.maxOccurs = value;
    }

}

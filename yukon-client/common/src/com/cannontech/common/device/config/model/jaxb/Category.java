
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
 * <p>Java class for Category complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Category">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="integer" type="{}InputInteger"/>
 *         &lt;element name="float" type="{}InputFloat"/>
 *         &lt;element name="boolean" type="{}InputBoolean"/>
 *         &lt;element name="enum" type="{}InputEnum"/>
 *         &lt;element name="map" type="{}InputMap"/>
 *         &lt;element name="indexed" type="{}InputIndexed"/>
 *       &lt;/choice>
 *       &lt;attribute name="type" type="{}CategoryType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Category", propOrder = {
    "integerOrFloatOrBoolean"
})
public class Category {

    @XmlElements({
        @XmlElement(name = "integer", type = InputInteger.class),
        @XmlElement(name = "float", type = InputFloat.class),
        @XmlElement(name = "boolean", type = InputBoolean.class),
        @XmlElement(name = "enum", type = InputEnum.class),
        @XmlElement(name = "map", type = InputMap.class),
        @XmlElement(name = "indexed", type = InputIndexed.class)
    })
    protected List<InputBase> integerOrFloatOrBoolean;
    @XmlAttribute(name = "type")
    protected CategoryType type;

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
     * {@link InputIndexed }
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link CategoryType }
     *     
     */
    public CategoryType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryType }
     *     
     */
    public void setType(CategoryType value) {
        this.type = value;
    }

}

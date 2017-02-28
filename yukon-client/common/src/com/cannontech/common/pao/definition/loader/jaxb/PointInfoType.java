
package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for pointInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pointInfoType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="zzattributes" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="yyinit" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pointInfoType", propOrder = {
    "value"
})
public class PointInfoType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "zzattributes")
    protected String zzattributes;
    @XmlAttribute(name = "yyinit")
    protected Boolean yyinit;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the zzattributes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZzattributes() {
        return zzattributes;
    }

    /**
     * Sets the value of the zzattributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZzattributes(String value) {
        this.zzattributes = value;
    }

    /**
     * Gets the value of the yyinit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isYyinit() {
        if (yyinit == null) {
            return false;
        } else {
            return yyinit;
        }
    }

    /**
     * Sets the value of the yyinit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setYyinit(Boolean value) {
        this.yyinit = value;
    }

}

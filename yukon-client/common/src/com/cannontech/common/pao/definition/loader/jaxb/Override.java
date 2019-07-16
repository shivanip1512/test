
package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for override complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="override">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="paoTypes" type="{}paoTypes"/>
 *         &lt;element name="creatable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="tags" type="{}tags" minOccurs="0"/>
 *         &lt;element name="configurations" type="{}configurations" minOccurs="0"/>
 *         &lt;element name="pointInfos" type="{}pointInfos" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "override", propOrder = {
    "paoTypes",
    "creatable",
    "tags",
    "configurations",
    "pointInfos"
})
public class Override {

    @XmlElement(required = true)
    protected PaoTypes paoTypes;
    protected Boolean creatable;
    protected Tags tags;
    protected Configurations configurations;
    protected PointInfos pointInfos;

    /**
     * Gets the value of the paoTypes property.
     * 
     * @return
     *     possible object is
     *     {@link PaoTypes }
     *     
     */
    public PaoTypes getPaoTypes() {
        return paoTypes;
    }

    /**
     * Sets the value of the paoTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaoTypes }
     *     
     */
    public void setPaoTypes(PaoTypes value) {
        this.paoTypes = value;
    }

    /**
     * Gets the value of the creatable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCreatable() {
        return creatable;
    }

    /**
     * Sets the value of the creatable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCreatable(Boolean value) {
        this.creatable = value;
    }

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link Tags }
     *     
     */
    public Tags getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tags }
     *     
     */
    public void setTags(Tags value) {
        this.tags = value;
    }

    /**
     * Gets the value of the configurations property.
     * 
     * @return
     *     possible object is
     *     {@link Configurations }
     *     
     */
    public Configurations getConfigurations() {
        return configurations;
    }

    /**
     * Sets the value of the configurations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configurations }
     *     
     */
    public void setConfigurations(Configurations value) {
        this.configurations = value;
    }

    /**
     * Gets the value of the pointInfos property.
     * 
     * @return
     *     possible object is
     *     {@link PointInfos }
     *     
     */
    public PointInfos getPointInfos() {
        return pointInfos;
    }

    /**
     * Sets the value of the pointInfos property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointInfos }
     *     
     */
    public void setPointInfos(PointInfos value) {
        this.pointInfos = value;
    }

}

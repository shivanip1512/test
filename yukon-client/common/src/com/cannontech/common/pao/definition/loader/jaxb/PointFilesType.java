
package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pointFilesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pointFilesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pointFile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pointFilesType", propOrder = {
    "pointFile"
})
public class PointFilesType {

    @XmlElement(required = true)
    protected String pointFile;

    /**
     * Gets the value of the pointFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPointFile() {
        return pointFile;
    }

    /**
     * Sets the value of the pointFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPointFile(String value) {
        this.pointFile = value;
    }

}

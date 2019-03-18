
package com.cannontech.common.pao.definition.loader.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="highestOffsets" type="{}highestOffsets"/>
 *         &lt;element name="point" type="{}point" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "highestOffsets",
    "point"
})
@XmlRootElement(name = "points")
public class Points {

    @XmlElement(required = true)
    protected HighestOffsets highestOffsets;
    @XmlElement(required = true)
    protected List<Point> point;

    /**
     * Gets the value of the highestOffsets property.
     * 
     * @return
     *     possible object is
     *     {@link HighestOffsets }
     *     
     */
    public HighestOffsets getHighestOffsets() {
        return highestOffsets;
    }

    /**
     * Sets the value of the highestOffsets property.
     * 
     * @param value
     *     allowed object is
     *     {@link HighestOffsets }
     *     
     */
    public void setHighestOffsets(HighestOffsets value) {
        this.highestOffsets = value;
    }

    /**
     * Gets the value of the point property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the point property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Point }
     * 
     * 
     */
    public List<Point> getPoint() {
        if (point == null) {
            point = new ArrayList<Point>();
        }
        return this.point;
    }

}

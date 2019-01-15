
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GridNodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GridNodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GridNodeType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}GridNodeTypeEnumeration"/>
 *         &lt;element name="GridNodeNumber" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridNodeType", propOrder = {
    "gridNodeType",
    "gridNodeNumber"
})
public class GridNodeType {

    @XmlElement(name = "GridNodeType", required = true)
    protected GridNodeTypeEnumeration gridNodeType;
    @XmlElement(name = "GridNodeNumber")
    protected long gridNodeNumber;

    /**
     * Gets the value of the gridNodeType property.
     * 
     * @return
     *     possible object is
     *     {@link GridNodeTypeEnumeration }
     *     
     */
    public GridNodeTypeEnumeration getGridNodeType() {
        return gridNodeType;
    }

    /**
     * Sets the value of the gridNodeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link GridNodeTypeEnumeration }
     *     
     */
    public void setGridNodeType(GridNodeTypeEnumeration value) {
        this.gridNodeType = value;
    }

    /**
     * Gets the value of the gridNodeNumber property.
     * 
     */
    public long getGridNodeNumber() {
        return gridNodeNumber;
    }

    /**
     * Sets the value of the gridNodeNumber property.
     * 
     */
    public void setGridNodeNumber(long value) {
        this.gridNodeNumber = value;
    }

}

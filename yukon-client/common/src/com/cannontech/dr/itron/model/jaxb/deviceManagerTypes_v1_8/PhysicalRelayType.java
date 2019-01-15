
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PhysicalRelayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PhysicalRelayType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PhysicalRelayID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhysicalRelayType", propOrder = {
    "physicalRelayID"
})
public class PhysicalRelayType {

    @XmlElement(name = "PhysicalRelayID")
    protected int physicalRelayID;

    /**
     * Gets the value of the physicalRelayID property.
     * 
     */
    public int getPhysicalRelayID() {
        return physicalRelayID;
    }

    /**
     * Sets the value of the physicalRelayID property.
     * 
     */
    public void setPhysicalRelayID(int value) {
        this.physicalRelayID = value;
    }

}

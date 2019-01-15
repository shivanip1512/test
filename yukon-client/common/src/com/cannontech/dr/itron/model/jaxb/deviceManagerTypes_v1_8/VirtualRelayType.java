
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VirtualRelayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VirtualRelayType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VirtualRelayID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PhysicalRelay" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PhysicalRelayType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VirtualRelayType", propOrder = {
    "virtualRelayID",
    "physicalRelaies"
})
public class VirtualRelayType {

    @XmlElement(name = "VirtualRelayID")
    protected int virtualRelayID;
    @XmlElement(name = "PhysicalRelay")
    protected List<PhysicalRelayType> physicalRelaies;

    /**
     * Gets the value of the virtualRelayID property.
     * 
     */
    public int getVirtualRelayID() {
        return virtualRelayID;
    }

    /**
     * Sets the value of the virtualRelayID property.
     * 
     */
    public void setVirtualRelayID(int value) {
        this.virtualRelayID = value;
    }

    /**
     * Gets the value of the physicalRelaies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the physicalRelaies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhysicalRelaies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PhysicalRelayType }
     * 
     * 
     */
    public List<PhysicalRelayType> getPhysicalRelaies() {
        if (physicalRelaies == null) {
            physicalRelaies = new ArrayList<PhysicalRelayType>();
        }
        return this.physicalRelaies;
    }

}

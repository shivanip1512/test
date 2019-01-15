
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Enter zero to 1000 LCS DTG Mac IDs or device group ids.
 *                 Dynamic device groups must include the Direct To Grid transport type filter.
 *                 Static device groups should inlude only Direct to Grid LCS devices.
 *                 
 *                 Possible ErrorCode values if you receive a BasicFaultType:
 *                     generic
 *                     fatal_error
 *                     authorization_failure
 *                     errorMsg.group.includes.non.d2glcs
 *                     errorMsg.group.subject.not.d2glcs
 *                     errorMsg.macIDTypeNotValid
 *                     errorMsg.macIDInvalid
 *                     errorMsg.macIDNotFound                
 *             
 * 
 * <p>Java class for SetILCORequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetILCORequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="LCSMacID" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="1000"/>
 *           &lt;element name="GroupID" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="1000"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="Reset" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}EmptyType"/>
 *           &lt;element name="DaysInThePastToUseForOptimization" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ILCODaysInThePastToUseForOptimizationType"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="DeviceWideTarget" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}EmptyType"/>
 *           &lt;element name="TargetVirtualRelayID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetILCORequestType", propOrder = {
    "groupIDs",
    "lcsMacIDs",
    "daysInThePastToUseForOptimization",
    "reset",
    "targetVirtualRelayID",
    "deviceWideTarget"
})
@XmlRootElement(name = "SetILCORequest")
public class SetILCORequest {

    @XmlElement(name = "GroupID", type = Long.class)
    protected List<Long> groupIDs;
    @XmlElement(name = "LCSMacID")
    protected List<String> lcsMacIDs;
    @XmlElement(name = "DaysInThePastToUseForOptimization")
    protected Integer daysInThePastToUseForOptimization;
    @XmlElement(name = "Reset")
    protected EmptyType reset;
    @XmlElement(name = "TargetVirtualRelayID")
    protected Integer targetVirtualRelayID;
    @XmlElement(name = "DeviceWideTarget")
    protected EmptyType deviceWideTarget;

    /**
     * Gets the value of the groupIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getGroupIDs() {
        if (groupIDs == null) {
            groupIDs = new ArrayList<Long>();
        }
        return this.groupIDs;
    }

    /**
     * Gets the value of the lcsMacIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lcsMacIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLCSMacIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLCSMacIDs() {
        if (lcsMacIDs == null) {
            lcsMacIDs = new ArrayList<String>();
        }
        return this.lcsMacIDs;
    }

    /**
     * Gets the value of the daysInThePastToUseForOptimization property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDaysInThePastToUseForOptimization() {
        return daysInThePastToUseForOptimization;
    }

    /**
     * Sets the value of the daysInThePastToUseForOptimization property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDaysInThePastToUseForOptimization(Integer value) {
        this.daysInThePastToUseForOptimization = value;
    }

    /**
     * Gets the value of the reset property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyType }
     *     
     */
    public EmptyType getReset() {
        return reset;
    }

    /**
     * Sets the value of the reset property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyType }
     *     
     */
    public void setReset(EmptyType value) {
        this.reset = value;
    }

    /**
     * Gets the value of the targetVirtualRelayID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTargetVirtualRelayID() {
        return targetVirtualRelayID;
    }

    /**
     * Sets the value of the targetVirtualRelayID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTargetVirtualRelayID(Integer value) {
        this.targetVirtualRelayID = value;
    }

    /**
     * Gets the value of the deviceWideTarget property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyType }
     *     
     */
    public EmptyType getDeviceWideTarget() {
        return deviceWideTarget;
    }

    /**
     * Sets the value of the deviceWideTarget property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyType }
     *     
     */
    public void setDeviceWideTarget(EmptyType value) {
        this.deviceWideTarget = value;
    }

}

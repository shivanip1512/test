
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
 *                 Enter either a list of MacIDs (up to 1000 devices) or 
 *                 a list of Group Ids.
 *             
 * 
 * <p>Java class for UpdateDeviceEventLogsRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateDeviceEventLogsRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="ESIMacID" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="1000"/>
 *           &lt;element name="GroupID" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded"/>
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
@XmlType(name = "UpdateDeviceEventLogsRequestType", propOrder = {
    "groupIDs",
    "esiMacIDs"
})
@XmlRootElement(name = "UpdateDeviceEventLogsRequest")
public class UpdateDeviceEventLogsRequest {

    @XmlElement(name = "GroupID", type = Long.class)
    protected List<Long> groupIDs;
    @XmlElement(name = "ESIMacID")
    protected List<String> esiMacIDs;

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
     * Gets the value of the esiMacIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the esiMacIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getESIMacIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getESIMacIDs() {
        if (esiMacIDs == null) {
            esiMacIDs = new ArrayList<String>();
        }
        return this.esiMacIDs;
    }

}

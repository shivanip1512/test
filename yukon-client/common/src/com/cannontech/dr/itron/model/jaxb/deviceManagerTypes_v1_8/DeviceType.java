
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeviceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeviceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ESIMacID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ESICommandStatus" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ESICommandStatusType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeviceType", propOrder = {
    "esiMacID",
    "esiCommandStatuses"
})
public class DeviceType {

    @XmlElement(name = "ESIMacID")
    protected String esiMacID;
    @XmlElement(name = "ESICommandStatus", required = true)
    protected List<ESICommandStatusType> esiCommandStatuses;

    /**
     * Gets the value of the esiMacID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getESIMacID() {
        return esiMacID;
    }

    /**
     * Sets the value of the esiMacID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setESIMacID(String value) {
        this.esiMacID = value;
    }

    /**
     * Gets the value of the esiCommandStatuses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the esiCommandStatuses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getESICommandStatuses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ESICommandStatusType }
     * 
     * 
     */
    public List<ESICommandStatusType> getESICommandStatuses() {
        if (esiCommandStatuses == null) {
            esiCommandStatuses = new ArrayList<ESICommandStatusType>();
        }
        return this.esiCommandStatuses;
    }

}

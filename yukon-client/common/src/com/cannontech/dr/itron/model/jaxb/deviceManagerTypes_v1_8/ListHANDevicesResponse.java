
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListHANDevicesResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListHANDevicesResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HANDeviceDetail" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDeviceDetailsType" maxOccurs="100"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListHANDevicesResponseType", propOrder = {
    "hanDeviceDetails"
})
@XmlRootElement(name = "ListHANDevicesResponse")
public class ListHANDevicesResponse {

    @XmlElement(name = "HANDeviceDetail", required = true)
    protected List<HANDeviceDetailsType> hanDeviceDetails;

    /**
     * Gets the value of the hanDeviceDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hanDeviceDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHANDeviceDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HANDeviceDetailsType }
     * 
     * 
     */
    public List<HANDeviceDetailsType> getHANDeviceDetails() {
        if (hanDeviceDetails == null) {
            hanDeviceDetails = new ArrayList<HANDeviceDetailsType>();
        }
        return this.hanDeviceDetails;
    }

}

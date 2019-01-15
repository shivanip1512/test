
package com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Possible ErrorCode values if you receive a BasicFaultType:
 * 				generic
 * 				fatal_error
 * 				authorization_failure
 * 			
 * 
 * <p>Java class for GetServicePointEnrollmentRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetServicePointEnrollmentRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServicePointID" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetServicePointEnrollmentRequestType", propOrder = {
    "servicePointIDs"
})
@XmlRootElement(name = "GetServicePointEnrollmentRequest")
public class GetServicePointEnrollmentRequest {

    @XmlElement(name = "ServicePointID", required = true)
    protected List<String> servicePointIDs;

    /**
     * Gets the value of the servicePointIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the servicePointIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServicePointIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getServicePointIDs() {
        if (servicePointIDs == null) {
            servicePointIDs = new ArrayList<String>();
        }
        return this.servicePointIDs;
    }

}

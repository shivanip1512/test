
package com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetServicePointEnrollmentResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetServicePointEnrollmentResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServicePointEnrollment" type="{urn:com:ssn:dr:xmlschema:service:v1.3:ServicePointManager.xsd}ServicePointEnrollmentType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="UnknownSPID" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetServicePointEnrollmentResponseType", propOrder = {
    "servicePointEnrollments",
    "unknownSPIDs"
})
@XmlRootElement(name = "GetServicePointEnrollmentResponse")
public class GetServicePointEnrollmentResponse {

    @XmlElement(name = "ServicePointEnrollment")
    protected List<ServicePointEnrollmentType> servicePointEnrollments;
    @XmlElement(name = "UnknownSPID")
    protected List<String> unknownSPIDs;

    /**
     * Gets the value of the servicePointEnrollments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the servicePointEnrollments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServicePointEnrollments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServicePointEnrollmentType }
     * 
     * 
     */
    public List<ServicePointEnrollmentType> getServicePointEnrollments() {
        if (servicePointEnrollments == null) {
            servicePointEnrollments = new ArrayList<ServicePointEnrollmentType>();
        }
        return this.servicePointEnrollments;
    }

    /**
     * Gets the value of the unknownSPIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unknownSPIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnknownSPIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUnknownSPIDs() {
        if (unknownSPIDs == null) {
            unknownSPIDs = new ArrayList<String>();
        }
        return this.unknownSPIDs;
    }

}


package com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3;

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
 * <p>Java class for EditServicePointRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditServicePointRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServicePoint" type="{urn:com:ssn:dr:xmlschema:service:v1.3:ServicePointManager.xsd}EditServicePointType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditServicePointRequestType", propOrder = {
    "servicePoint"
})
@XmlRootElement(name = "EditServicePointRequest")
public class EditServicePointRequest {

    @XmlElement(name = "ServicePoint", required = true)
    protected EditServicePointType servicePoint;

    /**
     * Gets the value of the servicePoint property.
     * 
     * @return
     *     possible object is
     *     {@link EditServicePointType }
     *     
     */
    public EditServicePointType getServicePoint() {
        return servicePoint;
    }

    /**
     * Sets the value of the servicePoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditServicePointType }
     *     
     */
    public void setServicePoint(EditServicePointType value) {
        this.servicePoint = value;
    }

}

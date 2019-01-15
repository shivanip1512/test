
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for GetActivateESIHANRadioCommandStatusResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetActivateESIHANRadioCommandStatusResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ESIMacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Status" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}SignalStatusEnumeration"/>
 *         &lt;element name="UserHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Completed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Successful" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RequestTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetActivateESIHANRadioCommandStatusResponseType", propOrder = {
    "esiMacID",
    "status",
    "userHandle",
    "completed",
    "successful",
    "requestTime"
})
@XmlRootElement(name = "GetActivateESIHANRadioCommandStatusResponse")
public class GetActivateESIHANRadioCommandStatusResponse {

    @XmlElement(name = "ESIMacID", required = true)
    protected String esiMacID;
    @XmlElement(name = "Status", required = true)
    protected SignalStatusEnumeration status;
    @XmlElement(name = "UserHandle", required = true)
    protected String userHandle;
    @XmlElement(name = "Completed")
    protected boolean completed;
    @XmlElement(name = "Successful")
    protected boolean successful;
    @XmlElement(name = "RequestTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestTime;

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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link SignalStatusEnumeration }
     *     
     */
    public SignalStatusEnumeration getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignalStatusEnumeration }
     *     
     */
    public void setStatus(SignalStatusEnumeration value) {
        this.status = value;
    }

    /**
     * Gets the value of the userHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserHandle() {
        return userHandle;
    }

    /**
     * Sets the value of the userHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserHandle(String value) {
        this.userHandle = value;
    }

    /**
     * Gets the value of the completed property.
     * 
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the value of the completed property.
     * 
     */
    public void setCompleted(boolean value) {
        this.completed = value;
    }

    /**
     * Gets the value of the successful property.
     * 
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Sets the value of the successful property.
     * 
     */
    public void setSuccessful(boolean value) {
        this.successful = value;
    }

    /**
     * Gets the value of the requestTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestTime() {
        return requestTime;
    }

    /**
     * Sets the value of the requestTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestTime(XMLGregorianCalendar value) {
        this.requestTime = value;
    }

}

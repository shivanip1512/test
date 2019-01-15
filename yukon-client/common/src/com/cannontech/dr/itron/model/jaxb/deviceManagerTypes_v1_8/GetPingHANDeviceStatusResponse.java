
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for GetPingHANDeviceStatusResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPingHANDeviceStatusResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Status" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PingStatusEnumeration"/>
 *         &lt;element name="RoundTripTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="RunTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPingHANDeviceStatusResponseType", propOrder = {
    "status",
    "roundTripTime",
    "runTime"
})
@XmlRootElement(name = "GetPingHANDeviceStatusResponse")
public class GetPingHANDeviceStatusResponse {

    @XmlElement(name = "Status", required = true)
    protected PingStatusEnumeration status;
    @XmlElement(name = "RoundTripTime")
    protected long roundTripTime;
    @XmlElement(name = "RunTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar runTime;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link PingStatusEnumeration }
     *     
     */
    public PingStatusEnumeration getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link PingStatusEnumeration }
     *     
     */
    public void setStatus(PingStatusEnumeration value) {
        this.status = value;
    }

    /**
     * Gets the value of the roundTripTime property.
     * 
     */
    public long getRoundTripTime() {
        return roundTripTime;
    }

    /**
     * Sets the value of the roundTripTime property.
     * 
     */
    public void setRoundTripTime(long value) {
        this.roundTripTime = value;
    }

    /**
     * Gets the value of the runTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRunTime() {
        return runTime;
    }

    /**
     * Sets the value of the runTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRunTime(XMLGregorianCalendar value) {
        this.runTime = value;
    }

}

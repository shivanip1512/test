
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * 
 *                 You can use the returned JobIdentifier to poll the status of the set job via the getJobStatus operation or you can wait for the JMS queue message which will include the status.
 *                 Possible ErrorCode values if you receive a BasicFaultType:
 *                     generic
 *                     fatal_error
 *                     authorization_failure
 *             
 * 
 * <p>Java class for SetPCTStateRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetPCTStateRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PCTMacID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HeatSetpoint" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PCTTemperatureType" minOccurs="0"/>
 *         &lt;element name="CoolSetpoint" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PCTTemperatureType" minOccurs="0"/>
 *         &lt;element name="HoldType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PCTHoldTypeEnumeration" minOccurs="0"/>
 *         &lt;element name="HoldUntilTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="FanMode" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PCTFanModeEnumeration" minOccurs="0"/>
 *         &lt;element name="Mode" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PCTModeTypeEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetPCTStateRequestType", propOrder = {
    "pctMacID",
    "heatSetpoint",
    "coolSetpoint",
    "holdType",
    "holdUntilTime",
    "fanMode",
    "mode"
})
@XmlRootElement(name = "SetPCTStateRequest")
public class SetPCTStateRequest {

    @XmlElement(name = "PCTMacID", required = true)
    protected String pctMacID;
    @XmlElement(name = "HeatSetpoint")
    protected Double heatSetpoint;
    @XmlElement(name = "CoolSetpoint")
    protected Double coolSetpoint;
    @XmlElement(name = "HoldType")
    protected PCTHoldTypeEnumeration holdType;
    @XmlElement(name = "HoldUntilTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar holdUntilTime;
    @XmlElement(name = "FanMode")
    protected PCTFanModeEnumeration fanMode;
    @XmlElement(name = "Mode")
    protected PCTModeTypeEnumeration mode;

    /**
     * Gets the value of the pctMacID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPCTMacID() {
        return pctMacID;
    }

    /**
     * Sets the value of the pctMacID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPCTMacID(String value) {
        this.pctMacID = value;
    }

    /**
     * Gets the value of the heatSetpoint property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getHeatSetpoint() {
        return heatSetpoint;
    }

    /**
     * Sets the value of the heatSetpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setHeatSetpoint(Double value) {
        this.heatSetpoint = value;
    }

    /**
     * Gets the value of the coolSetpoint property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCoolSetpoint() {
        return coolSetpoint;
    }

    /**
     * Sets the value of the coolSetpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCoolSetpoint(Double value) {
        this.coolSetpoint = value;
    }

    /**
     * Gets the value of the holdType property.
     * 
     * @return
     *     possible object is
     *     {@link PCTHoldTypeEnumeration }
     *     
     */
    public PCTHoldTypeEnumeration getHoldType() {
        return holdType;
    }

    /**
     * Sets the value of the holdType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PCTHoldTypeEnumeration }
     *     
     */
    public void setHoldType(PCTHoldTypeEnumeration value) {
        this.holdType = value;
    }

    /**
     * Gets the value of the holdUntilTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHoldUntilTime() {
        return holdUntilTime;
    }

    /**
     * Sets the value of the holdUntilTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHoldUntilTime(XMLGregorianCalendar value) {
        this.holdUntilTime = value;
    }

    /**
     * Gets the value of the fanMode property.
     * 
     * @return
     *     possible object is
     *     {@link PCTFanModeEnumeration }
     *     
     */
    public PCTFanModeEnumeration getFanMode() {
        return fanMode;
    }

    /**
     * Sets the value of the fanMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PCTFanModeEnumeration }
     *     
     */
    public void setFanMode(PCTFanModeEnumeration value) {
        this.fanMode = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link PCTModeTypeEnumeration }
     *     
     */
    public PCTModeTypeEnumeration getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PCTModeTypeEnumeration }
     *     
     */
    public void setMode(PCTModeTypeEnumeration value) {
        this.mode = value;
    }

}


package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ESICommandStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ESICommandStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CommandName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommandStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommandDisplayStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommandRunTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ESICommandStatusType", propOrder = {
    "commandName",
    "commandStatus",
    "commandDisplayStatus",
    "commandRunTime"
})
public class ESICommandStatusType {

    @XmlElement(name = "CommandName")
    protected String commandName;
    @XmlElement(name = "CommandStatus")
    protected String commandStatus;
    @XmlElement(name = "CommandDisplayStatus")
    protected String commandDisplayStatus;
    @XmlElement(name = "CommandRunTime")
    protected String commandRunTime;

    /**
     * Gets the value of the commandName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Sets the value of the commandName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandName(String value) {
        this.commandName = value;
    }

    /**
     * Gets the value of the commandStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandStatus() {
        return commandStatus;
    }

    /**
     * Sets the value of the commandStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandStatus(String value) {
        this.commandStatus = value;
    }

    /**
     * Gets the value of the commandDisplayStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandDisplayStatus() {
        return commandDisplayStatus;
    }

    /**
     * Sets the value of the commandDisplayStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandDisplayStatus(String value) {
        this.commandDisplayStatus = value;
    }

    /**
     * Gets the value of the commandRunTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandRunTime() {
        return commandRunTime;
    }

    /**
     * Sets the value of the commandRunTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandRunTime(String value) {
        this.commandRunTime = value;
    }

}

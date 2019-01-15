
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActivateESIHANRadioResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActivateESIHANRadioResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ActivateESIHANRadioCommandID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivateESIHANRadioResponseType", propOrder = {
    "activateESIHANRadioCommandID"
})
@XmlRootElement(name = "ActivateESIHANRadioResponse")
public class ActivateESIHANRadioResponse {

    @XmlElement(name = "ActivateESIHANRadioCommandID")
    protected long activateESIHANRadioCommandID;

    /**
     * Gets the value of the activateESIHANRadioCommandID property.
     * 
     */
    public long getActivateESIHANRadioCommandID() {
        return activateESIHANRadioCommandID;
    }

    /**
     * Sets the value of the activateESIHANRadioCommandID property.
     * 
     */
    public void setActivateESIHANRadioCommandID(long value) {
        this.activateESIHANRadioCommandID = value;
    }

}

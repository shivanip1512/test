
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Possible ErrorCode values if you receive a BasicFaultType:
 *                     generic
 *                     fatal_error
 *                     authorization_failure
 *                     ESIMacID.Pattern.message
 *                     esiAssociate.hanDevice.notFound
 *                     esiAssociate.hanDevice.unmodifiable.deviceState
 *                     esiAssociate.hanDevice.provisioning.inProgress
 *                     esiAssociate.hanDevice.esiNotFound
 *                     esiAssociate.hanDevice.esiNotValid
 *                     esiAssociate.hanDevice.notESI
 *             
 * 
 * <p>Java class for EditHANDeviceRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditHANDeviceRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="ZigbeeAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}EditZigbeeAttributeType"/>
 *           &lt;element name="D2GAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}EditD2GAttributeType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditHANDeviceRequestType", propOrder = {
    "d2GAttributes",
    "zigbeeAttributes"
})
@XmlRootElement(name = "EditHANDeviceRequest")
public class EditHANDeviceRequest {

    @XmlElement(name = "D2GAttributes")
    protected EditD2GAttributeType d2GAttributes;
    @XmlElement(name = "ZigbeeAttributes")
    protected EditZigbeeAttributeType zigbeeAttributes;

    /**
     * Gets the value of the d2GAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link EditD2GAttributeType }
     *     
     */
    public EditD2GAttributeType getD2GAttributes() {
        return d2GAttributes;
    }

    /**
     * Sets the value of the d2GAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditD2GAttributeType }
     *     
     */
    public void setD2GAttributes(EditD2GAttributeType value) {
        this.d2GAttributes = value;
    }

    /**
     * Gets the value of the zigbeeAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link EditZigbeeAttributeType }
     *     
     */
    public EditZigbeeAttributeType getZigbeeAttributes() {
        return zigbeeAttributes;
    }

    /**
     * Sets the value of the zigbeeAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditZigbeeAttributeType }
     *     
     */
    public void setZigbeeAttributes(EditZigbeeAttributeType value) {
        this.zigbeeAttributes = value;
    }

}

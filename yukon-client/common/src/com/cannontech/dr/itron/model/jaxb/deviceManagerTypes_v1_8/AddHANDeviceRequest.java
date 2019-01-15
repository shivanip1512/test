
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddHANDeviceRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddHANDeviceRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDeviceType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="ZigbeeAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}AddZigbeeAttributeType"/>
 *           &lt;element name="D2GAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}AddD2GAttributeType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddHANDeviceRequestType", propOrder = {
    "d2GAttributes",
    "zigbeeAttributes"
})
@XmlRootElement(name = "AddHANDeviceRequest")
public class AddHANDeviceRequest
    extends HANDeviceType
{

    @XmlElement(name = "D2GAttributes")
    protected AddD2GAttributeType d2GAttributes;
    @XmlElement(name = "ZigbeeAttributes")
    protected AddZigbeeAttributeType zigbeeAttributes;

    /**
     * Gets the value of the d2GAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link AddD2GAttributeType }
     *     
     */
    public AddD2GAttributeType getD2GAttributes() {
        return d2GAttributes;
    }

    /**
     * Sets the value of the d2GAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddD2GAttributeType }
     *     
     */
    public void setD2GAttributes(AddD2GAttributeType value) {
        this.d2GAttributes = value;
    }

    /**
     * Gets the value of the zigbeeAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link AddZigbeeAttributeType }
     *     
     */
    public AddZigbeeAttributeType getZigbeeAttributes() {
        return zigbeeAttributes;
    }

    /**
     * Sets the value of the zigbeeAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddZigbeeAttributeType }
     *     
     */
    public void setZigbeeAttributes(AddZigbeeAttributeType value) {
        this.zigbeeAttributes = value;
    }

}


package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrimaryDeviceBaseWithIdentifiersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrimaryDeviceBaseWithIdentifiersType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}PrimaryDeviceBaseType">
 *       &lt;sequence>
 *         &lt;element name="DeviceIdentifiers" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceIdentifierAttributeType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrimaryDeviceBaseWithIdentifiersType", propOrder = {
    "deviceIdentifiers"
})
@XmlSeeAlso({
    ESIType.class
})
public class PrimaryDeviceBaseWithIdentifiersType
    extends PrimaryDeviceBaseType
{

    @XmlElement(name = "DeviceIdentifiers", required = true)
    protected DeviceIdentifierAttributeType deviceIdentifiers;

    /**
     * Gets the value of the deviceIdentifiers property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceIdentifierAttributeType }
     *     
     */
    public DeviceIdentifierAttributeType getDeviceIdentifiers() {
        return deviceIdentifiers;
    }

    /**
     * Sets the value of the deviceIdentifiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceIdentifierAttributeType }
     *     
     */
    public void setDeviceIdentifiers(DeviceIdentifierAttributeType value) {
        this.deviceIdentifiers = value;
    }

}

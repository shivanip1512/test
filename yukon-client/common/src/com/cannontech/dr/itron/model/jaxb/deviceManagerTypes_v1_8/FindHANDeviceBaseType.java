
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FindHANDeviceBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FindHANDeviceBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FindDeviceBaseType">
 *       &lt;sequence>
 *         &lt;element name="HANDeviceType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceTypeEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindHANDeviceBaseType", propOrder = {
    "hanDeviceType"
})
@XmlSeeAlso({
    AssociatedHANDeviceType.class,
    FindHANDeviceRequest.class
})
public class FindHANDeviceBaseType
    extends FindDeviceBaseType
{

    @XmlElement(name = "HANDeviceType")
    protected DeviceTypeEnumeration hanDeviceType;

    /**
     * Gets the value of the hanDeviceType property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceTypeEnumeration }
     *     
     */
    public DeviceTypeEnumeration getHANDeviceType() {
        return hanDeviceType;
    }

    /**
     * Sets the value of the hanDeviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceTypeEnumeration }
     *     
     */
    public void setHANDeviceType(DeviceTypeEnumeration value) {
        this.hanDeviceType = value;
    }

}

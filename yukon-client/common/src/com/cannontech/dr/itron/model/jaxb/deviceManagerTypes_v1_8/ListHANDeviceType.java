
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListHANDeviceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListHANDeviceType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDeviceType">
 *       &lt;sequence>
 *         &lt;element name="ServicePoint" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ServicePointType" minOccurs="0"/>
 *         &lt;element name="DeviceState" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceStateEnumeration" minOccurs="0"/>
 *         &lt;element name="ESIDeviceState" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceStateEnumeration" minOccurs="0"/>
 *         &lt;element name="ESIIsRetired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ESIIsProvisioned" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ESIIsConnected" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="InputLoad" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="ZigbeeAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ListZigbeeAttributeType"/>
 *           &lt;element name="D2GAttributes" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ListD2GAttributeType"/>
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
@XmlType(name = "ListHANDeviceType", propOrder = {
    "servicePoint",
    "deviceState",
    "esiDeviceState",
    "esiIsRetired",
    "esiIsProvisioned",
    "esiIsConnected",
    "inputLoad",
    "d2GAttributes",
    "zigbeeAttributes"
})
public class ListHANDeviceType
    extends HANDeviceType
{

    @XmlElement(name = "ServicePoint")
    protected ServicePointType servicePoint;
    @XmlElement(name = "DeviceState")
    protected DeviceStateEnumeration deviceState;
    @XmlElement(name = "ESIDeviceState")
    protected DeviceStateEnumeration esiDeviceState;
    @XmlElement(name = "ESIIsRetired")
    protected Boolean esiIsRetired;
    @XmlElement(name = "ESIIsProvisioned")
    protected Boolean esiIsProvisioned;
    @XmlElement(name = "ESIIsConnected")
    protected Boolean esiIsConnected;
    @XmlElement(name = "InputLoad")
    protected Double inputLoad;
    @XmlElement(name = "D2GAttributes")
    protected ListD2GAttributeType d2GAttributes;
    @XmlElement(name = "ZigbeeAttributes")
    protected ListZigbeeAttributeType zigbeeAttributes;

    /**
     * Gets the value of the servicePoint property.
     * 
     * @return
     *     possible object is
     *     {@link ServicePointType }
     *     
     */
    public ServicePointType getServicePoint() {
        return servicePoint;
    }

    /**
     * Sets the value of the servicePoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServicePointType }
     *     
     */
    public void setServicePoint(ServicePointType value) {
        this.servicePoint = value;
    }

    /**
     * Gets the value of the deviceState property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceStateEnumeration }
     *     
     */
    public DeviceStateEnumeration getDeviceState() {
        return deviceState;
    }

    /**
     * Sets the value of the deviceState property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceStateEnumeration }
     *     
     */
    public void setDeviceState(DeviceStateEnumeration value) {
        this.deviceState = value;
    }

    /**
     * Gets the value of the esiDeviceState property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceStateEnumeration }
     *     
     */
    public DeviceStateEnumeration getESIDeviceState() {
        return esiDeviceState;
    }

    /**
     * Sets the value of the esiDeviceState property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceStateEnumeration }
     *     
     */
    public void setESIDeviceState(DeviceStateEnumeration value) {
        this.esiDeviceState = value;
    }

    /**
     * Gets the value of the esiIsRetired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isESIIsRetired() {
        return esiIsRetired;
    }

    /**
     * Sets the value of the esiIsRetired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setESIIsRetired(Boolean value) {
        this.esiIsRetired = value;
    }

    /**
     * Gets the value of the esiIsProvisioned property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isESIIsProvisioned() {
        return esiIsProvisioned;
    }

    /**
     * Sets the value of the esiIsProvisioned property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setESIIsProvisioned(Boolean value) {
        this.esiIsProvisioned = value;
    }

    /**
     * Gets the value of the esiIsConnected property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isESIIsConnected() {
        return esiIsConnected;
    }

    /**
     * Sets the value of the esiIsConnected property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setESIIsConnected(Boolean value) {
        this.esiIsConnected = value;
    }

    /**
     * Gets the value of the inputLoad property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getInputLoad() {
        return inputLoad;
    }

    /**
     * Sets the value of the inputLoad property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setInputLoad(Double value) {
        this.inputLoad = value;
    }

    /**
     * Gets the value of the d2GAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link ListD2GAttributeType }
     *     
     */
    public ListD2GAttributeType getD2GAttributes() {
        return d2GAttributes;
    }

    /**
     * Sets the value of the d2GAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListD2GAttributeType }
     *     
     */
    public void setD2GAttributes(ListD2GAttributeType value) {
        this.d2GAttributes = value;
    }

    /**
     * Gets the value of the zigbeeAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link ListZigbeeAttributeType }
     *     
     */
    public ListZigbeeAttributeType getZigbeeAttributes() {
        return zigbeeAttributes;
    }

    /**
     * Sets the value of the zigbeeAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListZigbeeAttributeType }
     *     
     */
    public void setZigbeeAttributes(ListZigbeeAttributeType value) {
        this.zigbeeAttributes = value;
    }

}

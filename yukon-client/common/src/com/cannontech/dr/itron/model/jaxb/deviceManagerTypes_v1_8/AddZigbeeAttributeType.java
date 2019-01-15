
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddZigbeeAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddZigbeeAttributeType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ZigbeeAttributeType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="InstallCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="PreConfiguredLinkKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;element name="UtilityEnrollmentGroup" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}UtilityEnrollmentGroupNumberType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddZigbeeAttributeType", propOrder = {
    "preConfiguredLinkKey",
    "installCode",
    "utilityEnrollmentGroup"
})
public class AddZigbeeAttributeType
    extends ZigbeeAttributeType
{

    @XmlElement(name = "PreConfiguredLinkKey")
    protected String preConfiguredLinkKey;
    @XmlElement(name = "InstallCode")
    protected String installCode;
    @XmlElement(name = "UtilityEnrollmentGroup")
    protected Integer utilityEnrollmentGroup;

    /**
     * Gets the value of the preConfiguredLinkKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreConfiguredLinkKey() {
        return preConfiguredLinkKey;
    }

    /**
     * Sets the value of the preConfiguredLinkKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreConfiguredLinkKey(String value) {
        this.preConfiguredLinkKey = value;
    }

    /**
     * Gets the value of the installCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstallCode() {
        return installCode;
    }

    /**
     * Sets the value of the installCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstallCode(String value) {
        this.installCode = value;
    }

    /**
     * Gets the value of the utilityEnrollmentGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }

    /**
     * Sets the value of the utilityEnrollmentGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUtilityEnrollmentGroup(Integer value) {
        this.utilityEnrollmentGroup = value;
    }

}

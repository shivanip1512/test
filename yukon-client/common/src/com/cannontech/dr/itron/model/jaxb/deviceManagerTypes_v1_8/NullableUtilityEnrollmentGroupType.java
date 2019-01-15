
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NullableUtilityEnrollmentGroupType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NullableUtilityEnrollmentGroupType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UtilityEnrollmentGroup" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}UtilityEnrollmentGroupNumberType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Null" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NullableUtilityEnrollmentGroupType", propOrder = {
    "utilityEnrollmentGroup"
})
public class NullableUtilityEnrollmentGroupType {

    @XmlElement(name = "UtilityEnrollmentGroup")
    protected Integer utilityEnrollmentGroup;
    @XmlAttribute(name = "Null")
    protected Boolean _null;

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

    /**
     * Gets the value of the null property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isNull() {
        if (_null == null) {
            return false;
        } else {
            return _null;
        }
    }

    /**
     * Sets the value of the null property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNull(Boolean value) {
        this._null = value;
    }

}

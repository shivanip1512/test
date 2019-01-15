
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Possible ErrorCode values if you receive a BasicFaultType:
 *                     generic
 *                     fatal_error
 *                     authorization_failure
 *                     Unique.deviceGroup.groupName
 *                     group.import.error.groupType
 *                     group.import.error.groupEmpty
 *             
 * 
 * <p>Java class for ESIGroupRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ESIGroupRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GroupName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GroupDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GroupType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}DeviceGroupType" minOccurs="0"/>
 *         &lt;element name="StaticGroupMemberList" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}StaticGroupMemberListType" minOccurs="0"/>
 *         &lt;element name="ESISearchRequest" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ESISearchRequestType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ESIGroupRequestType", propOrder = {
    "groupName",
    "groupDescription",
    "groupType",
    "staticGroupMemberList",
    "esiSearchRequest"
})
public class ESIGroupRequestType {

    @XmlElement(name = "GroupName", required = true)
    protected String groupName;
    @XmlElement(name = "GroupDescription")
    protected String groupDescription;
    @XmlElement(name = "GroupType")
    protected DeviceGroupType groupType;
    @XmlElement(name = "StaticGroupMemberList")
    protected StaticGroupMemberListType staticGroupMemberList;
    @XmlElement(name = "ESISearchRequest")
    protected ESISearchRequestType esiSearchRequest;

    /**
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupName(String value) {
        this.groupName = value;
    }

    /**
     * Gets the value of the groupDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupDescription() {
        return groupDescription;
    }

    /**
     * Sets the value of the groupDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupDescription(String value) {
        this.groupDescription = value;
    }

    /**
     * Gets the value of the groupType property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceGroupType }
     *     
     */
    public DeviceGroupType getGroupType() {
        return groupType;
    }

    /**
     * Sets the value of the groupType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceGroupType }
     *     
     */
    public void setGroupType(DeviceGroupType value) {
        this.groupType = value;
    }

    /**
     * Gets the value of the staticGroupMemberList property.
     * 
     * @return
     *     possible object is
     *     {@link StaticGroupMemberListType }
     *     
     */
    public StaticGroupMemberListType getStaticGroupMemberList() {
        return staticGroupMemberList;
    }

    /**
     * Sets the value of the staticGroupMemberList property.
     * 
     * @param value
     *     allowed object is
     *     {@link StaticGroupMemberListType }
     *     
     */
    public void setStaticGroupMemberList(StaticGroupMemberListType value) {
        this.staticGroupMemberList = value;
    }

    /**
     * Gets the value of the esiSearchRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ESISearchRequestType }
     *     
     */
    public ESISearchRequestType getESISearchRequest() {
        return esiSearchRequest;
    }

    /**
     * Sets the value of the esiSearchRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ESISearchRequestType }
     *     
     */
    public void setESISearchRequest(ESISearchRequestType value) {
        this.esiSearchRequest = value;
    }

}

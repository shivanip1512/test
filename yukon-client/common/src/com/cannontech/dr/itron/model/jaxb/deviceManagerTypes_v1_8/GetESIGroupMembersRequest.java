
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
 *                     Invalid.group.groupIdentifier
 *             
 * 
 * <p>Java class for GetESIGroupMembersRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetESIGroupMembersRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ESIGroupID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Pagination" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}PaginationType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetESIGroupMembersRequestType", propOrder = {
    "esiGroupID",
    "pagination"
})
@XmlRootElement(name = "GetESIGroupMembersRequest")
public class GetESIGroupMembersRequest {

    @XmlElement(name = "ESIGroupID")
    protected long esiGroupID;
    @XmlElement(name = "Pagination", required = true)
    protected PaginationType pagination;

    /**
     * Gets the value of the esiGroupID property.
     * 
     */
    public long getESIGroupID() {
        return esiGroupID;
    }

    /**
     * Sets the value of the esiGroupID property.
     * 
     */
    public void setESIGroupID(long value) {
        this.esiGroupID = value;
    }

    /**
     * Gets the value of the pagination property.
     * 
     * @return
     *     possible object is
     *     {@link PaginationType }
     *     
     */
    public PaginationType getPagination() {
        return pagination;
    }

    /**
     * Sets the value of the pagination property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaginationType }
     *     
     */
    public void setPagination(PaginationType value) {
        this.pagination = value;
    }

}

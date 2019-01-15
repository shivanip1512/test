
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 A reusable encapsulation of a page number and a page size.
 *             
 * 
 * <p>Java class for PaginationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaginationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PageNumber" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}PageNumber"/>
 *         &lt;element name="PageSize" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}PageSize"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaginationType", namespace = "urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd", propOrder = {
    "pageNumber",
    "pageSize"
})
public class PaginationType {

    @XmlElement(name = "PageNumber")
    protected int pageNumber;
    @XmlElement(name = "PageSize")
    protected int pageSize;

    /**
     * Gets the value of the pageNumber property.
     * 
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the value of the pageNumber property.
     * 
     */
    public void setPageNumber(int value) {
        this.pageNumber = value;
    }

    /**
     * Gets the value of the pageSize property.
     * 
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the value of the pageSize property.
     * 
     */
    public void setPageSize(int value) {
        this.pageSize = value;
    }

}

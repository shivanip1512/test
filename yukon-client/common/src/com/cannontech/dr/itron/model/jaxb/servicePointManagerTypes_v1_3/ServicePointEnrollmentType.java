
package com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServicePointEnrollmentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServicePointEnrollmentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServicePointID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServicePointName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BaseRatePlan" type="{urn:com:ssn:dr:xmlschema:service:v1.3:ServicePointManager.xsd}EnrollmentInfoType" minOccurs="0"/>
 *         &lt;element name="LoadedRatePlan" type="{urn:com:ssn:dr:xmlschema:service:v1.3:ServicePointManager.xsd}EnrollmentInfoType" minOccurs="0"/>
 *         &lt;element name="Program" type="{urn:com:ssn:dr:xmlschema:service:v1.3:ServicePointManager.xsd}EnrollmentInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServicePointEnrollmentType", propOrder = {
    "servicePointID",
    "servicePointName",
    "baseRatePlan",
    "loadedRatePlan",
    "programs"
})
public class ServicePointEnrollmentType {

    @XmlElement(name = "ServicePointID", required = true)
    protected String servicePointID;
    @XmlElement(name = "ServicePointName")
    protected String servicePointName;
    @XmlElement(name = "BaseRatePlan")
    protected EnrollmentInfoType baseRatePlan;
    @XmlElement(name = "LoadedRatePlan")
    protected EnrollmentInfoType loadedRatePlan;
    @XmlElement(name = "Program")
    protected List<EnrollmentInfoType> programs;

    /**
     * Gets the value of the servicePointID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointID() {
        return servicePointID;
    }

    /**
     * Sets the value of the servicePointID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointID(String value) {
        this.servicePointID = value;
    }

    /**
     * Gets the value of the servicePointName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePointName() {
        return servicePointName;
    }

    /**
     * Sets the value of the servicePointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePointName(String value) {
        this.servicePointName = value;
    }

    /**
     * Gets the value of the baseRatePlan property.
     * 
     * @return
     *     possible object is
     *     {@link EnrollmentInfoType }
     *     
     */
    public EnrollmentInfoType getBaseRatePlan() {
        return baseRatePlan;
    }

    /**
     * Sets the value of the baseRatePlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnrollmentInfoType }
     *     
     */
    public void setBaseRatePlan(EnrollmentInfoType value) {
        this.baseRatePlan = value;
    }

    /**
     * Gets the value of the loadedRatePlan property.
     * 
     * @return
     *     possible object is
     *     {@link EnrollmentInfoType }
     *     
     */
    public EnrollmentInfoType getLoadedRatePlan() {
        return loadedRatePlan;
    }

    /**
     * Sets the value of the loadedRatePlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnrollmentInfoType }
     *     
     */
    public void setLoadedRatePlan(EnrollmentInfoType value) {
        this.loadedRatePlan = value;
    }

    /**
     * Gets the value of the programs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the programs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrograms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EnrollmentInfoType }
     * 
     * 
     */
    public List<EnrollmentInfoType> getPrograms() {
        if (programs == null) {
            programs = new ArrayList<EnrollmentInfoType>();
        }
        return this.programs;
    }

}

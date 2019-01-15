
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for JobStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JobStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="JobID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ExecutionStatus" type="{urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd}JobExecutionStatusType"/>
 *         &lt;element name="ExecutionStatusDetail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StartTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="FinishTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Canceled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CanceledTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Duration" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="TotalPending" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="TotalCanceled" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="TotalCompleted" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="TotalTimeout" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="TotalFailure" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JobStatusType", namespace = "urn:com:ssn:dr:xmlschema:service:v1.1:BasicTypes.xsd", propOrder = {
    "jobID",
    "executionStatus",
    "executionStatusDetail",
    "startTime",
    "finishTime",
    "canceled",
    "canceledTime",
    "duration",
    "totalPending",
    "totalCanceled",
    "totalCompleted",
    "totalTimeout",
    "totalFailure"
})
public class JobStatusType {

    @XmlElement(name = "JobID")
    protected long jobID;
    @XmlElement(name = "ExecutionStatus", required = true)
    protected JobExecutionStatusType executionStatus;
    @XmlElement(name = "ExecutionStatusDetail")
    protected String executionStatusDetail;
    @XmlElement(name = "StartTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startTime;
    @XmlElement(name = "FinishTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar finishTime;
    @XmlElement(name = "Canceled")
    protected Boolean canceled;
    @XmlElement(name = "CanceledTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar canceledTime;
    @XmlElement(name = "Duration")
    protected Integer duration;
    @XmlElement(name = "TotalPending")
    protected Integer totalPending;
    @XmlElement(name = "TotalCanceled")
    protected Integer totalCanceled;
    @XmlElement(name = "TotalCompleted")
    protected Integer totalCompleted;
    @XmlElement(name = "TotalTimeout")
    protected Integer totalTimeout;
    @XmlElement(name = "TotalFailure")
    protected Integer totalFailure;

    /**
     * Gets the value of the jobID property.
     * 
     */
    public long getJobID() {
        return jobID;
    }

    /**
     * Sets the value of the jobID property.
     * 
     */
    public void setJobID(long value) {
        this.jobID = value;
    }

    /**
     * Gets the value of the executionStatus property.
     * 
     * @return
     *     possible object is
     *     {@link JobExecutionStatusType }
     *     
     */
    public JobExecutionStatusType getExecutionStatus() {
        return executionStatus;
    }

    /**
     * Sets the value of the executionStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobExecutionStatusType }
     *     
     */
    public void setExecutionStatus(JobExecutionStatusType value) {
        this.executionStatus = value;
    }

    /**
     * Gets the value of the executionStatusDetail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecutionStatusDetail() {
        return executionStatusDetail;
    }

    /**
     * Sets the value of the executionStatusDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecutionStatusDetail(String value) {
        this.executionStatusDetail = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartTime(XMLGregorianCalendar value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the finishTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFinishTime() {
        return finishTime;
    }

    /**
     * Sets the value of the finishTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFinishTime(XMLGregorianCalendar value) {
        this.finishTime = value;
    }

    /**
     * Gets the value of the canceled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanceled() {
        return canceled;
    }

    /**
     * Sets the value of the canceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanceled(Boolean value) {
        this.canceled = value;
    }

    /**
     * Gets the value of the canceledTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCanceledTime() {
        return canceledTime;
    }

    /**
     * Sets the value of the canceledTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCanceledTime(XMLGregorianCalendar value) {
        this.canceledTime = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDuration(Integer value) {
        this.duration = value;
    }

    /**
     * Gets the value of the totalPending property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalPending() {
        return totalPending;
    }

    /**
     * Sets the value of the totalPending property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalPending(Integer value) {
        this.totalPending = value;
    }

    /**
     * Gets the value of the totalCanceled property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalCanceled() {
        return totalCanceled;
    }

    /**
     * Sets the value of the totalCanceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalCanceled(Integer value) {
        this.totalCanceled = value;
    }

    /**
     * Gets the value of the totalCompleted property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalCompleted() {
        return totalCompleted;
    }

    /**
     * Sets the value of the totalCompleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalCompleted(Integer value) {
        this.totalCompleted = value;
    }

    /**
     * Gets the value of the totalTimeout property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalTimeout() {
        return totalTimeout;
    }

    /**
     * Sets the value of the totalTimeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalTimeout(Integer value) {
        this.totalTimeout = value;
    }

    /**
     * Gets the value of the totalFailure property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalFailure() {
        return totalFailure;
    }

    /**
     * Sets the value of the totalFailure property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalFailure(Integer value) {
        this.totalFailure = value;
    }

}

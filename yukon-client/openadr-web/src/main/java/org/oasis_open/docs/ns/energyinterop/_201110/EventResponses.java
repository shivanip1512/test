//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.14 at 09:30:13 AM CST 
//


package org.oasis_open.docs.ns.energyinterop._201110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventResponse" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}responseCode"/>
 *                   &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}responseDescription" minOccurs="0"/>
 *                   &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110/payloads}requestID"/>
 *                   &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}qualifiedEventID"/>
 *                   &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}optType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "eventResponse"
})
@XmlRootElement(name = "eventResponses")
public class EventResponses {

    protected List<EventResponses.EventResponse> eventResponse;

    /**
     * Gets the value of the eventResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventResponses.EventResponse }
     * 
     * 
     */
    public List<EventResponses.EventResponse> getEventResponse() {
        if (eventResponse == null) {
            eventResponse = new ArrayList<EventResponses.EventResponse>();
        }
        return this.eventResponse;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}responseCode"/>
     *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}responseDescription" minOccurs="0"/>
     *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110/payloads}requestID"/>
     *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}qualifiedEventID"/>
     *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}optType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "responseCode",
        "responseDescription",
        "requestID",
        "qualifiedEventID",
        "optType"
    })
    public static class EventResponse {

        @XmlElement(required = true)
        protected String responseCode;
        protected String responseDescription;
        @XmlElement(namespace = "http://docs.oasis-open.org/ns/energyinterop/201110/payloads", required = true)
        protected String requestID;
        @XmlElement(required = true)
        protected QualifiedEventIDType qualifiedEventID;
        @XmlElement(required = true)
        protected OptTypeType optType;

        /**
         * Gets the value of the responseCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResponseCode() {
            return responseCode;
        }

        /**
         * Sets the value of the responseCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResponseCode(String value) {
            this.responseCode = value;
        }

        /**
         * Gets the value of the responseDescription property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResponseDescription() {
            return responseDescription;
        }

        /**
         * Sets the value of the responseDescription property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResponseDescription(String value) {
            this.responseDescription = value;
        }

        /**
         * Gets the value of the requestID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRequestID() {
            return requestID;
        }

        /**
         * Sets the value of the requestID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRequestID(String value) {
            this.requestID = value;
        }

        /**
         * Gets the value of the qualifiedEventID property.
         * 
         * @return
         *     possible object is
         *     {@link QualifiedEventIDType }
         *     
         */
        public QualifiedEventIDType getQualifiedEventID() {
            return qualifiedEventID;
        }

        /**
         * Sets the value of the qualifiedEventID property.
         * 
         * @param value
         *     allowed object is
         *     {@link QualifiedEventIDType }
         *     
         */
        public void setQualifiedEventID(QualifiedEventIDType value) {
            this.qualifiedEventID = value;
        }

        /**
         * Gets the value of the optType property.
         * 
         * @return
         *     possible object is
         *     {@link OptTypeType }
         *     
         */
        public OptTypeType getOptType() {
            return optType;
        }

        /**
         * Sets the value of the optType property.
         * 
         * @param value
         *     allowed object is
         *     {@link OptTypeType }
         *     
         */
        public void setOptType(OptTypeType value) {
            this.optType = value;
        }

    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.14 at 09:30:13 AM CST 
//


package org.openadr.oadr_2_0a._2012._07;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ns.energyinterop._201110.EiEventType;
import org.oasis_open.docs.ns.energyinterop._201110.EiResponse;


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
 *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}eiResponse" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110/payloads}requestID"/>
 *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}vtnID"/>
 *         &lt;element name="oadrEvent" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}eiEvent"/>
 *                   &lt;element name="oadrResponseRequired" type="{http://openadr.org/oadr-2.0a/2012/07}ResponseRequiredType"/>
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
    "eiResponse",
    "requestID",
    "vtnID",
    "oadrEvent"
})
@XmlRootElement(name = "oadrDistributeEvent")
public class OadrDistributeEvent {

    @XmlElement(namespace = "http://docs.oasis-open.org/ns/energyinterop/201110")
    protected EiResponse eiResponse;
    @XmlElement(namespace = "http://docs.oasis-open.org/ns/energyinterop/201110/payloads", required = true)
    protected String requestID;
    @XmlElement(namespace = "http://docs.oasis-open.org/ns/energyinterop/201110", required = true)
    protected String vtnID;
    protected List<OadrDistributeEvent.OadrEvent> oadrEvent;

    /**
     * Gets the value of the eiResponse property.
     * 
     * @return
     *     possible object is
     *     {@link EiResponse }
     *     
     */
    public EiResponse getEiResponse() {
        return eiResponse;
    }

    /**
     * Sets the value of the eiResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link EiResponse }
     *     
     */
    public void setEiResponse(EiResponse value) {
        this.eiResponse = value;
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
     * Gets the value of the vtnID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVtnID() {
        return vtnID;
    }

    /**
     * Sets the value of the vtnID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVtnID(String value) {
        this.vtnID = value;
    }

    /**
     * Gets the value of the oadrEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the oadrEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOadrEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OadrDistributeEvent.OadrEvent }
     * 
     * 
     */
    public List<OadrDistributeEvent.OadrEvent> getOadrEvent() {
        if (oadrEvent == null) {
            oadrEvent = new ArrayList<OadrDistributeEvent.OadrEvent>();
        }
        return this.oadrEvent;
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
     *         &lt;element ref="{http://docs.oasis-open.org/ns/energyinterop/201110}eiEvent"/>
     *         &lt;element name="oadrResponseRequired" type="{http://openadr.org/oadr-2.0a/2012/07}ResponseRequiredType"/>
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
        "eiEvent",
        "oadrResponseRequired"
    })
    public static class OadrEvent {

        @XmlElement(namespace = "http://docs.oasis-open.org/ns/energyinterop/201110", required = true)
        protected EiEventType eiEvent;
        @XmlElement(required = true)
        protected ResponseRequiredType oadrResponseRequired;

        /**
         * Gets the value of the eiEvent property.
         * 
         * @return
         *     possible object is
         *     {@link EiEventType }
         *     
         */
        public EiEventType getEiEvent() {
            return eiEvent;
        }

        /**
         * Sets the value of the eiEvent property.
         * 
         * @param value
         *     allowed object is
         *     {@link EiEventType }
         *     
         */
        public void setEiEvent(EiEventType value) {
            this.eiEvent = value;
        }

        /**
         * Gets the value of the oadrResponseRequired property.
         * 
         * @return
         *     possible object is
         *     {@link ResponseRequiredType }
         *     
         */
        public ResponseRequiredType getOadrResponseRequired() {
            return oadrResponseRequired;
        }

        /**
         * Sets the value of the oadrResponseRequired property.
         * 
         * @param value
         *     allowed object is
         *     {@link ResponseRequiredType }
         *     
         */
        public void setOadrResponseRequired(ResponseRequiredType value) {
            this.oadrResponseRequired = value;
        }

    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.14 at 09:30:13 AM CST 
//


package ietf.params.xml.ns.icalendar_2_0.stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ietf.params.xml.ns.icalendar_2.Dtstart;
import ietf.params.xml.ns.icalendar_2.DurationPropType;


/**
 * abstract base for communication of schedules for signals and observations
 * 
 * <p>Java class for StreamBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StreamBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:ietf:params:xml:ns:icalendar-2.0}dtstart" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:icalendar-2.0}duration" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:icalendar-2.0:stream}intervals" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StreamBaseType", propOrder = {
    "dtstart",
    "duration",
    "intervals"
})
public abstract class StreamBaseType {

    @XmlElement(namespace = "urn:ietf:params:xml:ns:icalendar-2.0")
    protected Dtstart dtstart;
    @XmlElement(namespace = "urn:ietf:params:xml:ns:icalendar-2.0")
    protected DurationPropType duration;
    protected Intervals intervals;

    /**
     * Indicates when the Designated Interval of the Stream begins. May be inherited from containing artifact.
     * 
     * @return
     *     possible object is
     *     {@link Dtstart }
     *     
     */
    public Dtstart getDtstart() {
        return dtstart;
    }

    /**
     * Sets the value of the dtstart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dtstart }
     *     
     */
    public void setDtstart(Dtstart value) {
        this.dtstart = value;
    }

    /**
     * Indicates the duration inherited by the intervals. May be inherited from containing artifact.
     * 
     * @return
     *     possible object is
     *     {@link DurationPropType }
     *     
     */
    public DurationPropType getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link DurationPropType }
     *     
     */
    public void setDuration(DurationPropType value) {
        this.duration = value;
    }

    /**
     * Gets the value of the intervals property.
     * 
     * @return
     *     possible object is
     *     {@link Intervals }
     *     
     */
    public Intervals getIntervals() {
        return intervals;
    }

    /**
     * Sets the value of the intervals property.
     * 
     * @param value
     *     allowed object is
     *     {@link Intervals }
     *     
     */
    public void setIntervals(Intervals value) {
        this.intervals = value;
    }

}

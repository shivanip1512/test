
package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for highestOffsets complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="highestOffsets">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="analog" type="{}offset" minOccurs="0"/>
 *         &lt;element name="calcAnalog" type="{}offset" minOccurs="0"/>
 *         &lt;element name="calcStatus" type="{}offset" minOccurs="0"/>
 *         &lt;element name="demandAccumulator" type="{}offset" minOccurs="0"/>
 *         &lt;element name="pulseAccumulator" type="{}offset" minOccurs="0"/>
 *         &lt;element name="status" type="{}offset" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "highestOffsets", propOrder = {
    "analog",
    "calcAnalog",
    "calcStatus",
    "demandAccumulator",
    "pulseAccumulator",
    "status"
})
public class HighestOffsets {

    protected Offset analog;
    protected Offset calcAnalog;
    protected Offset calcStatus;
    protected Offset demandAccumulator;
    protected Offset pulseAccumulator;
    protected Offset status;

    /**
     * Gets the value of the analog property.
     * 
     * @return
     *     possible object is
     *     {@link Offset }
     *     
     */
    public Offset getAnalog() {
        return analog;
    }

    /**
     * Sets the value of the analog property.
     * 
     * @param value
     *     allowed object is
     *     {@link Offset }
     *     
     */
    public void setAnalog(Offset value) {
        this.analog = value;
    }

    /**
     * Gets the value of the calcAnalog property.
     * 
     * @return
     *     possible object is
     *     {@link Offset }
     *     
     */
    public Offset getCalcAnalog() {
        return calcAnalog;
    }

    /**
     * Sets the value of the calcAnalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link Offset }
     *     
     */
    public void setCalcAnalog(Offset value) {
        this.calcAnalog = value;
    }

    /**
     * Gets the value of the calcStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Offset }
     *     
     */
    public Offset getCalcStatus() {
        return calcStatus;
    }

    /**
     * Sets the value of the calcStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Offset }
     *     
     */
    public void setCalcStatus(Offset value) {
        this.calcStatus = value;
    }

    /**
     * Gets the value of the demandAccumulator property.
     * 
     * @return
     *     possible object is
     *     {@link Offset }
     *     
     */
    public Offset getDemandAccumulator() {
        return demandAccumulator;
    }

    /**
     * Sets the value of the demandAccumulator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Offset }
     *     
     */
    public void setDemandAccumulator(Offset value) {
        this.demandAccumulator = value;
    }

    /**
     * Gets the value of the pulseAccumulator property.
     * 
     * @return
     *     possible object is
     *     {@link Offset }
     *     
     */
    public Offset getPulseAccumulator() {
        return pulseAccumulator;
    }

    /**
     * Sets the value of the pulseAccumulator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Offset }
     *     
     */
    public void setPulseAccumulator(Offset value) {
        this.pulseAccumulator = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Offset }
     *     
     */
    public Offset getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Offset }
     *     
     */
    public void setStatus(Offset value) {
        this.status = value;
    }

}

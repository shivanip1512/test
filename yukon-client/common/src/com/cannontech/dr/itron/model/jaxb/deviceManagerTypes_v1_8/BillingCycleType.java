
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BillingCycleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BillingCycleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BillingCycleNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BillingCycleMonth" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}BillingCycleMonthType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BillingCycleType", propOrder = {
    "billingCycleNumber",
    "billingCycleMonths"
})
public class BillingCycleType {

    @XmlElement(name = "BillingCycleNumber")
    protected String billingCycleNumber;
    @XmlElement(name = "BillingCycleMonth")
    protected List<BillingCycleMonthType> billingCycleMonths;

    /**
     * Gets the value of the billingCycleNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingCycleNumber() {
        return billingCycleNumber;
    }

    /**
     * Sets the value of the billingCycleNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingCycleNumber(String value) {
        this.billingCycleNumber = value;
    }

    /**
     * Gets the value of the billingCycleMonths property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the billingCycleMonths property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBillingCycleMonths().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BillingCycleMonthType }
     * 
     * 
     */
    public List<BillingCycleMonthType> getBillingCycleMonths() {
        if (billingCycleMonths == null) {
            billingCycleMonths = new ArrayList<BillingCycleMonthType>();
        }
        return this.billingCycleMonths;
    }

}

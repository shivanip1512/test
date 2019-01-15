
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServicePointType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServicePointType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DRMProgramID" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DRMRatePlanID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="BillingCycle" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}BillingCycleType" minOccurs="0"/>
 *         &lt;element name="Location" type="{urn:com:ssn:dr:xmlschema:service:v1.0:LocationTypes.xsd}LocationType"/>
 *         &lt;element name="UtilServicePointID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServiceType" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ServiceTypeEnumeration" minOccurs="0"/>
 *         &lt;element name="Account" type="{urn:com:ssn:dr:xmlschema:service:v1.0:LocationTypes.xsd}AccountType" minOccurs="0"/>
 *         &lt;element name="Transformer" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}TransformerType" minOccurs="0"/>
 *         &lt;element name="Substation" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}SubstationType" minOccurs="0"/>
 *         &lt;element name="Feeder" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}FeederType" minOccurs="0"/>
 *         &lt;element name="TransmissionCircuit" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}TransmissionCircuitType" minOccurs="0"/>
 *         &lt;element name="MeterPhase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MeterLocationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServicePhaseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServiceVoltLevelCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CurrentTransformerRatio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VoltageTransformerRatio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LineFrequency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GroundLevelEstHeight" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="DRMManaged" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Attribute1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attribute5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServicePointType", propOrder = {
    "drmProgramIDs",
    "drmRatePlanID",
    "billingCycle",
    "location",
    "utilServicePointID",
    "serviceType",
    "account",
    "transformer",
    "substation",
    "feeder",
    "transmissionCircuit",
    "meterPhase",
    "meterLocationCode",
    "servicePhaseCode",
    "serviceVoltLevelCode",
    "currentTransformerRatio",
    "voltageTransformerRatio",
    "lineFrequency",
    "groundLevelEstHeight",
    "drmManaged",
    "attribute1",
    "attribute2",
    "attribute3",
    "attribute4",
    "attribute5"
})
public class ServicePointType {

    @XmlElement(name = "DRMProgramID", type = Long.class)
    protected List<Long> drmProgramIDs;
    @XmlElement(name = "DRMRatePlanID")
    protected Long drmRatePlanID;
    @XmlElement(name = "BillingCycle")
    protected BillingCycleType billingCycle;
    @XmlElement(name = "Location", required = true)
    protected LocationType location;
    @XmlElement(name = "UtilServicePointID", required = true)
    protected String utilServicePointID;
    @XmlElement(name = "ServiceType")
    protected ServiceTypeEnumeration serviceType;
    @XmlElement(name = "Account")
    protected AccountType account;
    @XmlElement(name = "Transformer")
    protected TransformerType transformer;
    @XmlElement(name = "Substation")
    protected SubstationType substation;
    @XmlElement(name = "Feeder")
    protected FeederType feeder;
    @XmlElement(name = "TransmissionCircuit")
    protected TransmissionCircuitType transmissionCircuit;
    @XmlElement(name = "MeterPhase")
    protected String meterPhase;
    @XmlElement(name = "MeterLocationCode")
    protected String meterLocationCode;
    @XmlElement(name = "ServicePhaseCode")
    protected String servicePhaseCode;
    @XmlElement(name = "ServiceVoltLevelCode")
    protected String serviceVoltLevelCode;
    @XmlElement(name = "CurrentTransformerRatio")
    protected String currentTransformerRatio;
    @XmlElement(name = "VoltageTransformerRatio")
    protected String voltageTransformerRatio;
    @XmlElement(name = "LineFrequency")
    protected String lineFrequency;
    @XmlElement(name = "GroundLevelEstHeight")
    protected Integer groundLevelEstHeight;
    @XmlElement(name = "DRMManaged")
    protected Boolean drmManaged;
    @XmlElement(name = "Attribute1")
    protected String attribute1;
    @XmlElement(name = "Attribute2")
    protected String attribute2;
    @XmlElement(name = "Attribute3")
    protected String attribute3;
    @XmlElement(name = "Attribute4")
    protected String attribute4;
    @XmlElement(name = "Attribute5")
    protected String attribute5;

    /**
     * Gets the value of the drmProgramIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the drmProgramIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDRMProgramIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getDRMProgramIDs() {
        if (drmProgramIDs == null) {
            drmProgramIDs = new ArrayList<Long>();
        }
        return this.drmProgramIDs;
    }

    /**
     * Gets the value of the drmRatePlanID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDRMRatePlanID() {
        return drmRatePlanID;
    }

    /**
     * Sets the value of the drmRatePlanID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDRMRatePlanID(Long value) {
        this.drmRatePlanID = value;
    }

    /**
     * Gets the value of the billingCycle property.
     * 
     * @return
     *     possible object is
     *     {@link BillingCycleType }
     *     
     */
    public BillingCycleType getBillingCycle() {
        return billingCycle;
    }

    /**
     * Sets the value of the billingCycle property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillingCycleType }
     *     
     */
    public void setBillingCycle(BillingCycleType value) {
        this.billingCycle = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link LocationType }
     *     
     */
    public LocationType getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationType }
     *     
     */
    public void setLocation(LocationType value) {
        this.location = value;
    }

    /**
     * Gets the value of the utilServicePointID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUtilServicePointID() {
        return utilServicePointID;
    }

    /**
     * Sets the value of the utilServicePointID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUtilServicePointID(String value) {
        this.utilServicePointID = value;
    }

    /**
     * Gets the value of the serviceType property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceTypeEnumeration }
     *     
     */
    public ServiceTypeEnumeration getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceTypeEnumeration }
     *     
     */
    public void setServiceType(ServiceTypeEnumeration value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link AccountType }
     *     
     */
    public AccountType getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountType }
     *     
     */
    public void setAccount(AccountType value) {
        this.account = value;
    }

    /**
     * Gets the value of the transformer property.
     * 
     * @return
     *     possible object is
     *     {@link TransformerType }
     *     
     */
    public TransformerType getTransformer() {
        return transformer;
    }

    /**
     * Sets the value of the transformer property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransformerType }
     *     
     */
    public void setTransformer(TransformerType value) {
        this.transformer = value;
    }

    /**
     * Gets the value of the substation property.
     * 
     * @return
     *     possible object is
     *     {@link SubstationType }
     *     
     */
    public SubstationType getSubstation() {
        return substation;
    }

    /**
     * Sets the value of the substation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubstationType }
     *     
     */
    public void setSubstation(SubstationType value) {
        this.substation = value;
    }

    /**
     * Gets the value of the feeder property.
     * 
     * @return
     *     possible object is
     *     {@link FeederType }
     *     
     */
    public FeederType getFeeder() {
        return feeder;
    }

    /**
     * Sets the value of the feeder property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeederType }
     *     
     */
    public void setFeeder(FeederType value) {
        this.feeder = value;
    }

    /**
     * Gets the value of the transmissionCircuit property.
     * 
     * @return
     *     possible object is
     *     {@link TransmissionCircuitType }
     *     
     */
    public TransmissionCircuitType getTransmissionCircuit() {
        return transmissionCircuit;
    }

    /**
     * Sets the value of the transmissionCircuit property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransmissionCircuitType }
     *     
     */
    public void setTransmissionCircuit(TransmissionCircuitType value) {
        this.transmissionCircuit = value;
    }

    /**
     * Gets the value of the meterPhase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterPhase() {
        return meterPhase;
    }

    /**
     * Sets the value of the meterPhase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterPhase(String value) {
        this.meterPhase = value;
    }

    /**
     * Gets the value of the meterLocationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterLocationCode() {
        return meterLocationCode;
    }

    /**
     * Sets the value of the meterLocationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterLocationCode(String value) {
        this.meterLocationCode = value;
    }

    /**
     * Gets the value of the servicePhaseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicePhaseCode() {
        return servicePhaseCode;
    }

    /**
     * Sets the value of the servicePhaseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicePhaseCode(String value) {
        this.servicePhaseCode = value;
    }

    /**
     * Gets the value of the serviceVoltLevelCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceVoltLevelCode() {
        return serviceVoltLevelCode;
    }

    /**
     * Sets the value of the serviceVoltLevelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceVoltLevelCode(String value) {
        this.serviceVoltLevelCode = value;
    }

    /**
     * Gets the value of the currentTransformerRatio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentTransformerRatio() {
        return currentTransformerRatio;
    }

    /**
     * Sets the value of the currentTransformerRatio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentTransformerRatio(String value) {
        this.currentTransformerRatio = value;
    }

    /**
     * Gets the value of the voltageTransformerRatio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoltageTransformerRatio() {
        return voltageTransformerRatio;
    }

    /**
     * Sets the value of the voltageTransformerRatio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoltageTransformerRatio(String value) {
        this.voltageTransformerRatio = value;
    }

    /**
     * Gets the value of the lineFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineFrequency() {
        return lineFrequency;
    }

    /**
     * Sets the value of the lineFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineFrequency(String value) {
        this.lineFrequency = value;
    }

    /**
     * Gets the value of the groundLevelEstHeight property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGroundLevelEstHeight() {
        return groundLevelEstHeight;
    }

    /**
     * Sets the value of the groundLevelEstHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGroundLevelEstHeight(Integer value) {
        this.groundLevelEstHeight = value;
    }

    /**
     * Gets the value of the drmManaged property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDRMManaged() {
        return drmManaged;
    }

    /**
     * Sets the value of the drmManaged property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDRMManaged(Boolean value) {
        this.drmManaged = value;
    }

    /**
     * Gets the value of the attribute1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute1() {
        return attribute1;
    }

    /**
     * Sets the value of the attribute1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute1(String value) {
        this.attribute1 = value;
    }

    /**
     * Gets the value of the attribute2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute2() {
        return attribute2;
    }

    /**
     * Sets the value of the attribute2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute2(String value) {
        this.attribute2 = value;
    }

    /**
     * Gets the value of the attribute3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute3() {
        return attribute3;
    }

    /**
     * Sets the value of the attribute3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute3(String value) {
        this.attribute3 = value;
    }

    /**
     * Gets the value of the attribute4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute4() {
        return attribute4;
    }

    /**
     * Sets the value of the attribute4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute4(String value) {
        this.attribute4 = value;
    }

    /**
     * Gets the value of the attribute5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute5() {
        return attribute5;
    }

    /**
     * Sets the value of the attribute5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute5(String value) {
        this.attribute5 = value;
    }

}

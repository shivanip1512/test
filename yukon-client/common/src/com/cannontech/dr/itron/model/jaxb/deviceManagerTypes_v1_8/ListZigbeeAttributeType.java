
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListZigbeeAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListZigbeeAttributeType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ZigbeeAttributeType">
 *       &lt;sequence>
 *         &lt;element name="InstallCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PreConfiguredLinkKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UtilityEnrollmentGroup" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}UtilityEnrollmentGroupType" minOccurs="0"/>
 *         &lt;element name="LinkQualityIndicator" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ConnectivityStatus" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}ConnectivityStatusEnumeration" minOccurs="0"/>
 *         &lt;element name="SupportedSEPCluster" type="{urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd}HANDeviceSEPClusterEnumeration" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListZigbeeAttributeType", propOrder = {
    "installCode",
    "preConfiguredLinkKey",
    "utilityEnrollmentGroup",
    "linkQualityIndicator",
    "connectivityStatus",
    "supportedSEPClusters"
})
public class ListZigbeeAttributeType
    extends ZigbeeAttributeType
{

    @XmlElement(name = "InstallCode")
    protected String installCode;
    @XmlElement(name = "PreConfiguredLinkKey")
    protected String preConfiguredLinkKey;
    @XmlElement(name = "UtilityEnrollmentGroup")
    protected UtilityEnrollmentGroupType utilityEnrollmentGroup;
    @XmlElement(name = "LinkQualityIndicator")
    protected Integer linkQualityIndicator;
    @XmlElement(name = "ConnectivityStatus")
    protected ConnectivityStatusEnumeration connectivityStatus;
    @XmlElement(name = "SupportedSEPCluster")
    protected List<HANDeviceSEPClusterEnumeration> supportedSEPClusters;

    /**
     * Gets the value of the installCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstallCode() {
        return installCode;
    }

    /**
     * Sets the value of the installCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstallCode(String value) {
        this.installCode = value;
    }

    /**
     * Gets the value of the preConfiguredLinkKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreConfiguredLinkKey() {
        return preConfiguredLinkKey;
    }

    /**
     * Sets the value of the preConfiguredLinkKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreConfiguredLinkKey(String value) {
        this.preConfiguredLinkKey = value;
    }

    /**
     * Gets the value of the utilityEnrollmentGroup property.
     * 
     * @return
     *     possible object is
     *     {@link UtilityEnrollmentGroupType }
     *     
     */
    public UtilityEnrollmentGroupType getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }

    /**
     * Sets the value of the utilityEnrollmentGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link UtilityEnrollmentGroupType }
     *     
     */
    public void setUtilityEnrollmentGroup(UtilityEnrollmentGroupType value) {
        this.utilityEnrollmentGroup = value;
    }

    /**
     * Gets the value of the linkQualityIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLinkQualityIndicator() {
        return linkQualityIndicator;
    }

    /**
     * Sets the value of the linkQualityIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLinkQualityIndicator(Integer value) {
        this.linkQualityIndicator = value;
    }

    /**
     * Gets the value of the connectivityStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectivityStatusEnumeration }
     *     
     */
    public ConnectivityStatusEnumeration getConnectivityStatus() {
        return connectivityStatus;
    }

    /**
     * Sets the value of the connectivityStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectivityStatusEnumeration }
     *     
     */
    public void setConnectivityStatus(ConnectivityStatusEnumeration value) {
        this.connectivityStatus = value;
    }

    /**
     * Gets the value of the supportedSEPClusters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedSEPClusters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedSEPClusters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HANDeviceSEPClusterEnumeration }
     * 
     * 
     */
    public List<HANDeviceSEPClusterEnumeration> getSupportedSEPClusters() {
        if (supportedSEPClusters == null) {
            supportedSEPClusters = new ArrayList<HANDeviceSEPClusterEnumeration>();
        }
        return this.supportedSEPClusters;
    }

}


package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="pointFiles" type="{}pointFilesType" minOccurs="0"/>
 *         &lt;element name="tags" type="{}tagsType" minOccurs="0"/>
 *         &lt;element name="configuration" type="{}DeviceCategories" minOccurs="0"/>
 *         &lt;element name="pointInfos" type="{}pointInfosType" minOccurs="0"/>
 *         &lt;element name="commands" type="{}commandsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="aaid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="changeGroup" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="displayGroup" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="displayName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="creatable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pointFiles",
    "tags",
    "configuration",
    "pointInfos",
    "commands"
})
@XmlRootElement(name = "pao")
public class Pao {

    protected PointFilesType pointFiles;
    protected TagsType tags;
    protected DeviceCategories configuration;
    protected PointInfosType pointInfos;
    protected CommandsType commands;
    @XmlAttribute(name = "aaid", required = true)
    protected String aaid;
    @XmlAttribute(name = "changeGroup")
    protected String changeGroup;
    @XmlAttribute(name = "displayGroup")
    protected String displayGroup;
    @XmlAttribute(name = "displayName", required = true)
    protected String displayName;
    @XmlAttribute(name = "creatable")
    protected Boolean creatable;
    @XmlAttribute(name = "enabled")
    protected Boolean enabled;

    /**
     * Gets the value of the pointFiles property.
     * 
     * @return
     *     possible object is
     *     {@link PointFilesType }
     *     
     */
    public PointFilesType getPointFiles() {
        return pointFiles;
    }

    /**
     * Sets the value of the pointFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointFilesType }
     *     
     */
    public void setPointFiles(PointFilesType value) {
        this.pointFiles = value;
    }

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link TagsType }
     *     
     */
    public TagsType getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link TagsType }
     *     
     */
    public void setTags(TagsType value) {
        this.tags = value;
    }

    /**
     * Gets the value of the configuration property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceCategories }
     *     
     */
    public DeviceCategories getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceCategories }
     *     
     */
    public void setConfiguration(DeviceCategories value) {
        this.configuration = value;
    }

    /**
     * Gets the value of the pointInfos property.
     * 
     * @return
     *     possible object is
     *     {@link PointInfosType }
     *     
     */
    public PointInfosType getPointInfos() {
        return pointInfos;
    }

    /**
     * Sets the value of the pointInfos property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointInfosType }
     *     
     */
    public void setPointInfos(PointInfosType value) {
        this.pointInfos = value;
    }

    /**
     * Gets the value of the commands property.
     * 
     * @return
     *     possible object is
     *     {@link CommandsType }
     *     
     */
    public CommandsType getCommands() {
        return commands;
    }

    /**
     * Sets the value of the commands property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommandsType }
     *     
     */
    public void setCommands(CommandsType value) {
        this.commands = value;
    }

    /**
     * Gets the value of the aaid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAaid() {
        return aaid;
    }

    /**
     * Sets the value of the aaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAaid(String value) {
        this.aaid = value;
    }

    /**
     * Gets the value of the changeGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChangeGroup() {
        return changeGroup;
    }

    /**
     * Sets the value of the changeGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChangeGroup(String value) {
        this.changeGroup = value;
    }

    /**
     * Gets the value of the displayGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayGroup() {
        return displayGroup;
    }

    /**
     * Sets the value of the displayGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayGroup(String value) {
        this.displayGroup = value;
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the creatable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCreatable() {
        if (creatable == null) {
            return true;
        } else {
            return creatable;
        }
    }

    /**
     * Sets the value of the creatable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCreatable(Boolean value) {
        this.creatable = value;
    }

    /**
     * Gets the value of the enabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEnabled() {
        if (enabled == null) {
            return true;
        } else {
            return enabled;
        }
    }

    /**
     * Sets the value of the enabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnabled(Boolean value) {
        this.enabled = value;
    }

}

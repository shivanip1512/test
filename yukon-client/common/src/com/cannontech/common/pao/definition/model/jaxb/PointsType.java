
package com.cannontech.common.pao.definition.model.jaxb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pointsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pointsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="point" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="init" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="archive" type="{}archiveDefaults" minOccurs="0"/>
 *                   &lt;choice>
 *                     &lt;sequence>
 *                       &lt;element name="multiplier">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="unitofmeasure">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="decimalplaces">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="analogstategroup" minOccurs="0">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                               &lt;attribute name="initialState" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="dataOffset" minOccurs="0">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                     &lt;/sequence>
 *                     &lt;sequence>
 *                       &lt;element name="controlType" minOccurs="0">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{}controlTypeType" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="controlOffset" minOccurs="0">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="stateZeroControl" minOccurs="0">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{}controlStateType" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="stateOneControl" minOccurs="0">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{}controlStateType" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="stategroup">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                               &lt;attribute name="initialState" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                     &lt;/sequence>
 *                   &lt;/choice>
 *                   &lt;element name="calculation" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="forceQualityNormal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                             &lt;element name="periodicRate" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="updateType" type="{}updateTypeType" minOccurs="0"/>
 *                             &lt;element name="components">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="component" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="point" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="componentType" type="{}componentTypeType" default="Operation" />
 *                                               &lt;attribute name="operator" type="{http://www.w3.org/2001/XMLSchema}string" default="+" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{}pointAttributes"/>
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
@XmlType(name = "pointsType", propOrder = {
    "point"
})
public class PointsType {

    protected List<PointsType.Point> point;

    /**
     * Gets the value of the point property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the point property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PointsType.Point }
     * 
     * 
     */
    public List<PointsType.Point> getPoint() {
        if (point == null) {
            point = new ArrayList<PointsType.Point>();
        }
        return this.point;
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
     *         &lt;element name="init" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="archive" type="{}archiveDefaults" minOccurs="0"/>
     *         &lt;choice>
     *           &lt;sequence>
     *             &lt;element name="multiplier">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="unitofmeasure">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="decimalplaces">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="analogstategroup" minOccurs="0">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                     &lt;attribute name="initialState" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="dataOffset" minOccurs="0">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *           &lt;/sequence>
     *           &lt;sequence>
     *             &lt;element name="controlType" minOccurs="0">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{}controlTypeType" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="controlOffset" minOccurs="0">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="stateZeroControl" minOccurs="0">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{}controlStateType" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="stateOneControl" minOccurs="0">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{}controlStateType" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="stategroup">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                     &lt;attribute name="initialState" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *           &lt;/sequence>
     *         &lt;/choice>
     *         &lt;element name="calculation" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="forceQualityNormal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *                   &lt;element name="periodicRate" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *                   &lt;element name="updateType" type="{}updateTypeType" minOccurs="0"/>
     *                   &lt;element name="components">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="component" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;attribute name="point" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="componentType" type="{}componentTypeType" default="Operation" />
     *                                     &lt;attribute name="operator" type="{http://www.w3.org/2001/XMLSchema}string" default="+" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attGroup ref="{}pointAttributes"/>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "init",
        "name",
        "description",
        "archive",
        "multiplier",
        "unitofmeasure",
        "decimalplaces",
        "analogstategroup",
        "dataOffset",
        "controlType",
        "controlOffset",
        "stateZeroControl",
        "stateOneControl",
        "stategroup",
        "calculation"
    })
    public static class Point {

        @XmlElement(defaultValue = "false")
        protected Boolean init = false;
        protected String name;
        protected String description;
        protected ArchiveDefaults archive;
        protected PointsType.Point.Multiplier multiplier;
        protected PointsType.Point.Unitofmeasure unitofmeasure;
        protected PointsType.Point.Decimalplaces decimalplaces;
        protected PointsType.Point.Analogstategroup analogstategroup;
        protected PointsType.Point.DataOffset dataOffset;
        protected PointsType.Point.ControlType controlType;
        protected PointsType.Point.ControlOffset controlOffset;
        protected PointsType.Point.StateZeroControl stateZeroControl;
        protected PointsType.Point.StateOneControl stateOneControl;
        protected PointsType.Point.Stategroup stategroup;
        protected PointsType.Point.Calculation calculation;
        @XmlAttribute(name = "type", required = true)
        protected String type;
        @XmlAttribute(name = "offset", required = true)
        protected int offset;
        @XmlAttribute(name = "enabled")
        protected Boolean enabled;

        /**
         * Gets the value of the init property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isInit() {
            return init;
        }

        /**
         * Sets the value of the init property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setInit(Boolean value) {
            this.init = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * Gets the value of the archive property.
         * 
         * @return
         *     possible object is
         *     {@link ArchiveDefaults }
         *     
         */
        public ArchiveDefaults getArchive() {
            return archive;
        }

        /**
         * Sets the value of the archive property.
         * 
         * @param value
         *     allowed object is
         *     {@link ArchiveDefaults }
         *     
         */
        public void setArchive(ArchiveDefaults value) {
            this.archive = value;
        }

        /**
         * Gets the value of the multiplier property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.Multiplier }
         *     
         */
        public PointsType.Point.Multiplier getMultiplier() {
            return multiplier;
        }

        /**
         * Sets the value of the multiplier property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.Multiplier }
         *     
         */
        public void setMultiplier(PointsType.Point.Multiplier value) {
            this.multiplier = value;
        }

        /**
         * Gets the value of the unitofmeasure property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.Unitofmeasure }
         *     
         */
        public PointsType.Point.Unitofmeasure getUnitofmeasure() {
            return unitofmeasure;
        }

        /**
         * Sets the value of the unitofmeasure property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.Unitofmeasure }
         *     
         */
        public void setUnitofmeasure(PointsType.Point.Unitofmeasure value) {
            this.unitofmeasure = value;
        }

        /**
         * Gets the value of the decimalplaces property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.Decimalplaces }
         *     
         */
        public PointsType.Point.Decimalplaces getDecimalplaces() {
            return decimalplaces;
        }

        /**
         * Sets the value of the decimalplaces property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.Decimalplaces }
         *     
         */
        public void setDecimalplaces(PointsType.Point.Decimalplaces value) {
            this.decimalplaces = value;
        }

        /**
         * Gets the value of the analogstategroup property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.Analogstategroup }
         *     
         */
        public PointsType.Point.Analogstategroup getAnalogstategroup() {
            return analogstategroup;
        }

        /**
         * Sets the value of the analogstategroup property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.Analogstategroup }
         *     
         */
        public void setAnalogstategroup(PointsType.Point.Analogstategroup value) {
            this.analogstategroup = value;
        }

        /**
         * Gets the value of the dataOffset property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.DataOffset }
         *     
         */
        public PointsType.Point.DataOffset getDataOffset() {
            return dataOffset;
        }

        /**
         * Sets the value of the dataOffset property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.DataOffset }
         *     
         */
        public void setDataOffset(PointsType.Point.DataOffset value) {
            this.dataOffset = value;
        }

        /**
         * Gets the value of the controlType property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.ControlType }
         *     
         */
        public PointsType.Point.ControlType getControlType() {
            return controlType;
        }

        /**
         * Sets the value of the controlType property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.ControlType }
         *     
         */
        public void setControlType(PointsType.Point.ControlType value) {
            this.controlType = value;
        }

        /**
         * Gets the value of the controlOffset property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.ControlOffset }
         *     
         */
        public PointsType.Point.ControlOffset getControlOffset() {
            return controlOffset;
        }

        /**
         * Sets the value of the controlOffset property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.ControlOffset }
         *     
         */
        public void setControlOffset(PointsType.Point.ControlOffset value) {
            this.controlOffset = value;
        }

        /**
         * Gets the value of the stateZeroControl property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.StateZeroControl }
         *     
         */
        public PointsType.Point.StateZeroControl getStateZeroControl() {
            return stateZeroControl;
        }

        /**
         * Sets the value of the stateZeroControl property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.StateZeroControl }
         *     
         */
        public void setStateZeroControl(PointsType.Point.StateZeroControl value) {
            this.stateZeroControl = value;
        }

        /**
         * Gets the value of the stateOneControl property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.StateOneControl }
         *     
         */
        public PointsType.Point.StateOneControl getStateOneControl() {
            return stateOneControl;
        }

        /**
         * Sets the value of the stateOneControl property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.StateOneControl }
         *     
         */
        public void setStateOneControl(PointsType.Point.StateOneControl value) {
            this.stateOneControl = value;
        }

        /**
         * Gets the value of the stategroup property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.Stategroup }
         *     
         */
        public PointsType.Point.Stategroup getStategroup() {
            return stategroup;
        }

        /**
         * Sets the value of the stategroup property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.Stategroup }
         *     
         */
        public void setStategroup(PointsType.Point.Stategroup value) {
            this.stategroup = value;
        }

        /**
         * Gets the value of the calculation property.
         * 
         * @return
         *     possible object is
         *     {@link PointsType.Point.Calculation }
         *     
         */
        public PointsType.Point.Calculation getCalculation() {
            return calculation;
        }

        /**
         * Sets the value of the calculation property.
         * 
         * @param value
         *     allowed object is
         *     {@link PointsType.Point.Calculation }
         *     
         */
        public void setCalculation(PointsType.Point.Calculation value) {
            this.calculation = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * Gets the value of the offset property.
         * 
         */
        public int getOffset() {
            return offset;
        }

        /**
         * Sets the value of the offset property.
         * 
         */
        public void setOffset(int value) {
            this.offset = value;
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


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="initialState" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Analogstategroup {

            @XmlAttribute(name = "value", required = true)
            protected String value;
            @XmlAttribute(name = "initialState")
            protected String initialState;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the initialState property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInitialState() {
                return initialState;
            }

            /**
             * Sets the value of the initialState property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInitialState(String value) {
                this.initialState = value;
            }

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
         *         &lt;element name="forceQualityNormal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
         *         &lt;element name="periodicRate" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
         *         &lt;element name="updateType" type="{}updateTypeType" minOccurs="0"/>
         *         &lt;element name="components">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="component" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;attribute name="point" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="componentType" type="{}componentTypeType" default="Operation" />
         *                           &lt;attribute name="operator" type="{http://www.w3.org/2001/XMLSchema}string" default="+" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
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
            "forceQualityNormal",
            "periodicRate",
            "updateType",
            "components"
        })
        public static class Calculation {

            @XmlElement(defaultValue = "false")
            protected Boolean forceQualityNormal = false;
            @XmlElement(defaultValue = "1")
            protected Integer periodicRate = 1;
            @XmlElement(defaultValue = "On First Change")
            protected UpdateTypeType updateType = UpdateTypeType.ON_FIRST_CHANGE;
            @XmlElement(required = true)
            protected PointsType.Point.Calculation.Components components;

            /**
             * Gets the value of the forceQualityNormal property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isForceQualityNormal() {
                return forceQualityNormal;
            }

            /**
             * Sets the value of the forceQualityNormal property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setForceQualityNormal(Boolean value) {
                this.forceQualityNormal = value;
            }

            /**
             * Gets the value of the periodicRate property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getPeriodicRate() {
                return periodicRate;
            }

            /**
             * Sets the value of the periodicRate property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setPeriodicRate(Integer value) {
                this.periodicRate = value;
            }

            /**
             * Gets the value of the updateType property.
             * 
             * @return
             *     possible object is
             *     {@link UpdateTypeType }
             *     
             */
            public UpdateTypeType getUpdateType() {
                return updateType;
            }

            /**
             * Sets the value of the updateType property.
             * 
             * @param value
             *     allowed object is
             *     {@link UpdateTypeType }
             *     
             */
            public void setUpdateType(UpdateTypeType value) {
                this.updateType = value;
            }

            /**
             * Gets the value of the components property.
             * 
             * @return
             *     possible object is
             *     {@link PointsType.Point.Calculation.Components }
             *     
             */
            public PointsType.Point.Calculation.Components getComponents() {
                return components;
            }

            /**
             * Sets the value of the components property.
             * 
             * @param value
             *     allowed object is
             *     {@link PointsType.Point.Calculation.Components }
             *     
             */
            public void setComponents(PointsType.Point.Calculation.Components value) {
                this.components = value;
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
             *         &lt;element name="component" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;attribute name="point" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="componentType" type="{}componentTypeType" default="Operation" />
             *                 &lt;attribute name="operator" type="{http://www.w3.org/2001/XMLSchema}string" default="+" />
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
                "component"
            })
            public static class Components {

                @XmlElement(required = true)
                protected List<PointsType.Point.Calculation.Components.Component> component;

                /**
                 * Gets the value of the component property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the component property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getComponent().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link PointsType.Point.Calculation.Components.Component }
                 * 
                 * 
                 */
                public List<PointsType.Point.Calculation.Components.Component> getComponent() {
                    if (component == null) {
                        component = new ArrayList<PointsType.Point.Calculation.Components.Component>();
                    }
                    return this.component;
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
                 *       &lt;attribute name="point" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="componentType" type="{}componentTypeType" default="Operation" />
                 *       &lt;attribute name="operator" type="{http://www.w3.org/2001/XMLSchema}string" default="+" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class Component {

                    @XmlAttribute(name = "point", required = true)
                    protected String point;
                    @XmlAttribute(name = "componentType")
                    protected ComponentTypeType componentType;
                    @XmlAttribute(name = "operator")
                    protected String operator;

                    /**
                     * Gets the value of the point property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getPoint() {
                        return point;
                    }

                    /**
                     * Sets the value of the point property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setPoint(String value) {
                        this.point = value;
                    }

                    /**
                     * Gets the value of the componentType property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ComponentTypeType }
                     *     
                     */
                    public ComponentTypeType getComponentType() {
                        if (componentType == null) {
                            return ComponentTypeType.OPERATION;
                        } else {
                            return componentType;
                        }
                    }

                    /**
                     * Sets the value of the componentType property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ComponentTypeType }
                     *     
                     */
                    public void setComponentType(ComponentTypeType value) {
                        this.componentType = value;
                    }

                    /**
                     * Gets the value of the operator property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getOperator() {
                        if (operator == null) {
                            return "+";
                        } else {
                            return operator;
                        }
                    }

                    /**
                     * Sets the value of the operator property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setOperator(String value) {
                        this.operator = value;
                    }

                }

            }

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
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ControlOffset {

            @XmlAttribute(name = "value", required = true)
            protected int value;

            /**
             * Gets the value of the value property.
             * 
             */
            public int getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             */
            public void setValue(int value) {
                this.value = value;
            }

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
         *       &lt;attribute name="value" use="required" type="{}controlTypeType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ControlType {

            @XmlAttribute(name = "value", required = true)
            protected ControlTypeType value;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link ControlTypeType }
             *     
             */
            public ControlTypeType getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link ControlTypeType }
             *     
             */
            public void setValue(ControlTypeType value) {
                this.value = value;
            }

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
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class DataOffset {

            @XmlAttribute(name = "value", required = true)
            protected BigDecimal value;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setValue(BigDecimal value) {
                this.value = value;
            }

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
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Decimalplaces {

            @XmlAttribute(name = "value", required = true)
            protected int value;

            /**
             * Gets the value of the value property.
             * 
             */
            public int getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             */
            public void setValue(int value) {
                this.value = value;
            }

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
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Multiplier {

            @XmlAttribute(name = "value", required = true)
            protected BigDecimal value;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setValue(BigDecimal value) {
                this.value = value;
            }

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
         *       &lt;attribute name="value" use="required" type="{}controlStateType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class StateOneControl {

            @XmlAttribute(name = "value", required = true)
            protected ControlStateType value;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link ControlStateType }
             *     
             */
            public ControlStateType getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link ControlStateType }
             *     
             */
            public void setValue(ControlStateType value) {
                this.value = value;
            }

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
         *       &lt;attribute name="value" use="required" type="{}controlStateType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class StateZeroControl {

            @XmlAttribute(name = "value", required = true)
            protected ControlStateType value;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link ControlStateType }
             *     
             */
            public ControlStateType getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link ControlStateType }
             *     
             */
            public void setValue(ControlStateType value) {
                this.value = value;
            }

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
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="initialState" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Stategroup {

            @XmlAttribute(name = "value", required = true)
            protected String value;
            @XmlAttribute(name = "initialState")
            protected String initialState;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the initialState property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInitialState() {
                return initialState;
            }

            /**
             * Sets the value of the initialState property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInitialState(String value) {
                this.initialState = value;
            }

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
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Unitofmeasure {

            @XmlAttribute(name = "value", required = true)
            protected String value;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

}

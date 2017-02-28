
package com.cannontech.common.pao.definition.loader.jaxb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="point" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="archive" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="NONE" />
 *                           &lt;attribute name="interval" type="{http://www.w3.org/2001/XMLSchema}string" default="ZERO" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;choice>
 *                     &lt;sequence>
 *                       &lt;element name="multiplier">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="unitofmeasure">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="value" use="required" type="{}unitOfMeasureType" />
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
 *                 &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="offset" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
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
    "point"
})
@XmlRootElement(name = "points")
public class Points {

    protected List<Points.Point> point;

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
     * {@link Points.Point }
     * 
     * 
     */
    public List<Points.Point> getPoint() {
        if (point == null) {
            point = new ArrayList<Points.Point>();
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
     *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="archive" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="NONE" />
     *                 &lt;attribute name="interval" type="{http://www.w3.org/2001/XMLSchema}string" default="ZERO" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;choice>
     *           &lt;sequence>
     *             &lt;element name="multiplier">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *                   &lt;/restriction>
     *                 &lt;/complexContent>
     *               &lt;/complexType>
     *             &lt;/element>
     *             &lt;element name="unitofmeasure">
     *               &lt;complexType>
     *                 &lt;complexContent>
     *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                     &lt;attribute name="value" use="required" type="{}unitOfMeasureType" />
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
     *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="offset" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
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
        "name",
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

        @XmlElement(required = true)
        protected String name;
        protected Points.Point.Archive archive;
        protected Points.Point.Multiplier multiplier;
        protected Points.Point.Unitofmeasure unitofmeasure;
        protected Points.Point.Decimalplaces decimalplaces;
        protected Points.Point.Analogstategroup analogstategroup;
        protected Points.Point.DataOffset dataOffset;
        protected Points.Point.ControlType controlType;
        protected Points.Point.ControlOffset controlOffset;
        protected Points.Point.StateZeroControl stateZeroControl;
        protected Points.Point.StateOneControl stateOneControl;
        protected Points.Point.Stategroup stategroup;
        protected Points.Point.Calculation calculation;
        @XmlAttribute(name = "type", required = true)
        protected String type;
        @XmlAttribute(name = "offset", required = true)
        protected int offset;
        @XmlAttribute(name = "enabled")
        protected Boolean enabled;

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
         * Gets the value of the archive property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.Archive }
         *     
         */
        public Points.Point.Archive getArchive() {
            return archive;
        }

        /**
         * Sets the value of the archive property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.Archive }
         *     
         */
        public void setArchive(Points.Point.Archive value) {
            this.archive = value;
        }

        /**
         * Gets the value of the multiplier property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.Multiplier }
         *     
         */
        public Points.Point.Multiplier getMultiplier() {
            return multiplier;
        }

        /**
         * Sets the value of the multiplier property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.Multiplier }
         *     
         */
        public void setMultiplier(Points.Point.Multiplier value) {
            this.multiplier = value;
        }

        /**
         * Gets the value of the unitofmeasure property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.Unitofmeasure }
         *     
         */
        public Points.Point.Unitofmeasure getUnitofmeasure() {
            return unitofmeasure;
        }

        /**
         * Sets the value of the unitofmeasure property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.Unitofmeasure }
         *     
         */
        public void setUnitofmeasure(Points.Point.Unitofmeasure value) {
            this.unitofmeasure = value;
        }

        /**
         * Gets the value of the decimalplaces property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.Decimalplaces }
         *     
         */
        public Points.Point.Decimalplaces getDecimalplaces() {
            return decimalplaces;
        }

        /**
         * Sets the value of the decimalplaces property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.Decimalplaces }
         *     
         */
        public void setDecimalplaces(Points.Point.Decimalplaces value) {
            this.decimalplaces = value;
        }

        /**
         * Gets the value of the analogstategroup property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.Analogstategroup }
         *     
         */
        public Points.Point.Analogstategroup getAnalogstategroup() {
            return analogstategroup;
        }

        /**
         * Sets the value of the analogstategroup property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.Analogstategroup }
         *     
         */
        public void setAnalogstategroup(Points.Point.Analogstategroup value) {
            this.analogstategroup = value;
        }

        /**
         * Gets the value of the dataOffset property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.DataOffset }
         *     
         */
        public Points.Point.DataOffset getDataOffset() {
            return dataOffset;
        }

        /**
         * Sets the value of the dataOffset property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.DataOffset }
         *     
         */
        public void setDataOffset(Points.Point.DataOffset value) {
            this.dataOffset = value;
        }

        /**
         * Gets the value of the controlType property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.ControlType }
         *     
         */
        public Points.Point.ControlType getControlType() {
            return controlType;
        }

        /**
         * Sets the value of the controlType property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.ControlType }
         *     
         */
        public void setControlType(Points.Point.ControlType value) {
            this.controlType = value;
        }

        /**
         * Gets the value of the controlOffset property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.ControlOffset }
         *     
         */
        public Points.Point.ControlOffset getControlOffset() {
            return controlOffset;
        }

        /**
         * Sets the value of the controlOffset property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.ControlOffset }
         *     
         */
        public void setControlOffset(Points.Point.ControlOffset value) {
            this.controlOffset = value;
        }

        /**
         * Gets the value of the stateZeroControl property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.StateZeroControl }
         *     
         */
        public Points.Point.StateZeroControl getStateZeroControl() {
            return stateZeroControl;
        }

        /**
         * Sets the value of the stateZeroControl property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.StateZeroControl }
         *     
         */
        public void setStateZeroControl(Points.Point.StateZeroControl value) {
            this.stateZeroControl = value;
        }

        /**
         * Gets the value of the stateOneControl property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.StateOneControl }
         *     
         */
        public Points.Point.StateOneControl getStateOneControl() {
            return stateOneControl;
        }

        /**
         * Sets the value of the stateOneControl property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.StateOneControl }
         *     
         */
        public void setStateOneControl(Points.Point.StateOneControl value) {
            this.stateOneControl = value;
        }

        /**
         * Gets the value of the stategroup property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.Stategroup }
         *     
         */
        public Points.Point.Stategroup getStategroup() {
            return stategroup;
        }

        /**
         * Sets the value of the stategroup property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.Stategroup }
         *     
         */
        public void setStategroup(Points.Point.Stategroup value) {
            this.stategroup = value;
        }

        /**
         * Gets the value of the calculation property.
         * 
         * @return
         *     possible object is
         *     {@link Points.Point.Calculation }
         *     
         */
        public Points.Point.Calculation getCalculation() {
            return calculation;
        }

        /**
         * Sets the value of the calculation property.
         * 
         * @param value
         *     allowed object is
         *     {@link Points.Point.Calculation }
         *     
         */
        public void setCalculation(Points.Point.Calculation value) {
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
         *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="NONE" />
         *       &lt;attribute name="interval" type="{http://www.w3.org/2001/XMLSchema}string" default="ZERO" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Archive {

            @XmlAttribute(name = "type")
            protected String type;
            @XmlAttribute(name = "interval")
            protected String interval;

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getType() {
                if (type == null) {
                    return "NONE";
                } else {
                    return type;
                }
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
             * Gets the value of the interval property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInterval() {
                if (interval == null) {
                    return "ZERO";
                } else {
                    return interval;
                }
            }

            /**
             * Sets the value of the interval property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInterval(String value) {
                this.interval = value;
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
            protected Points.Point.Calculation.Components components;

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
             *     {@link Points.Point.Calculation.Components }
             *     
             */
            public Points.Point.Calculation.Components getComponents() {
                return components;
            }

            /**
             * Sets the value of the components property.
             * 
             * @param value
             *     allowed object is
             *     {@link Points.Point.Calculation.Components }
             *     
             */
            public void setComponents(Points.Point.Calculation.Components value) {
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
                protected List<Points.Point.Calculation.Components.Component> component;

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
                 * {@link Points.Point.Calculation.Components.Component }
                 * 
                 * 
                 */
                public List<Points.Point.Calculation.Components.Component> getComponent() {
                    if (component == null) {
                        component = new ArrayList<Points.Point.Calculation.Components.Component>();
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
         *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}decimal" />
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

            @XmlAttribute(name = "value")
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
         *       &lt;attribute name="value" use="required" type="{}unitOfMeasureType" />
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
            protected UnitOfMeasureType value;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link UnitOfMeasureType }
             *     
             */
            public UnitOfMeasureType getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link UnitOfMeasureType }
             *     
             */
            public void setValue(UnitOfMeasureType value) {
                this.value = value;
            }

        }

    }

}

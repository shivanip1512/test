
package com.cannontech.common.pao.definition.loader.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.cannontech.common.pao.definition.loader.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Creatable_QNAME = new QName("", "creatable");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.cannontech.common.pao.definition.loader.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Point }
     * 
     */
    public Point createPoint() {
        return new Point();
    }

    /**
     * Create an instance of {@link Point.Calculation }
     * 
     */
    public Point.Calculation createPointCalculation() {
        return new Point.Calculation();
    }

    /**
     * Create an instance of {@link Point.Calculation.Components }
     * 
     */
    public Point.Calculation.Components createPointCalculationComponents() {
        return new Point.Calculation.Components();
    }

    /**
     * Create an instance of {@link Overrides }
     * 
     */
    public Overrides createOverrides() {
        return new Overrides();
    }

    /**
     * Create an instance of {@link Override }
     * 
     */
    public Override createOverride() {
        return new Override();
    }

    /**
     * Create an instance of {@link OverridePointInfo }
     * 
     */
    public OverridePointInfo createOverridePointInfo() {
        return new OverridePointInfo();
    }

    /**
     * Create an instance of {@link Configurations }
     * 
     */
    public Configurations createConfigurations() {
        return new Configurations();
    }

    /**
     * Create an instance of {@link Tags }
     * 
     */
    public Tags createTags() {
        return new Tags();
    }

    /**
     * Create an instance of {@link PointInfos }
     * 
     */
    public PointInfos createPointInfos() {
        return new PointInfos();
    }

    /**
     * Create an instance of {@link PaoTypes }
     * 
     */
    public PaoTypes createPaoTypes() {
        return new PaoTypes();
    }

    /**
     * Create an instance of {@link OverrideTag }
     * 
     */
    public OverrideTag createOverrideTag() {
        return new OverrideTag();
    }

    /**
     * Create an instance of {@link OverrideCategory }
     * 
     */
    public OverrideCategory createOverrideCategory() {
        return new OverrideCategory();
    }

    /**
     * Create an instance of {@link Point.Archive }
     * 
     */
    public Point.Archive createPointArchive() {
        return new Point.Archive();
    }

    /**
     * Create an instance of {@link Point.Multiplier }
     * 
     */
    public Point.Multiplier createPointMultiplier() {
        return new Point.Multiplier();
    }

    /**
     * Create an instance of {@link Point.Unitofmeasure }
     * 
     */
    public Point.Unitofmeasure createPointUnitofmeasure() {
        return new Point.Unitofmeasure();
    }

    /**
     * Create an instance of {@link Point.Decimalplaces }
     * 
     */
    public Point.Decimalplaces createPointDecimalplaces() {
        return new Point.Decimalplaces();
    }

    /**
     * Create an instance of {@link Point.Analogstategroup }
     * 
     */
    public Point.Analogstategroup createPointAnalogstategroup() {
        return new Point.Analogstategroup();
    }

    /**
     * Create an instance of {@link Point.DataOffset }
     * 
     */
    public Point.DataOffset createPointDataOffset() {
        return new Point.DataOffset();
    }

    /**
     * Create an instance of {@link Point.ControlType }
     * 
     */
    public Point.ControlType createPointControlType() {
        return new Point.ControlType();
    }

    /**
     * Create an instance of {@link Point.ControlOffset }
     * 
     */
    public Point.ControlOffset createPointControlOffset() {
        return new Point.ControlOffset();
    }

    /**
     * Create an instance of {@link Point.StateZeroControl }
     * 
     */
    public Point.StateZeroControl createPointStateZeroControl() {
        return new Point.StateZeroControl();
    }

    /**
     * Create an instance of {@link Point.StateOneControl }
     * 
     */
    public Point.StateOneControl createPointStateOneControl() {
        return new Point.StateOneControl();
    }

    /**
     * Create an instance of {@link Point.Stategroup }
     * 
     */
    public Point.Stategroup createPointStategroup() {
        return new Point.Stategroup();
    }

    /**
     * Create an instance of {@link Point.Calculation.Components.Component }
     * 
     */
    public Point.Calculation.Components.Component createPointCalculationComponentsComponent() {
        return new Point.Calculation.Components.Component();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "creatable")
    public JAXBElement<Boolean> createCreatable(Boolean value) {
        return new JAXBElement<Boolean>(_Creatable_QNAME, Boolean.class, null, value);
    }

}

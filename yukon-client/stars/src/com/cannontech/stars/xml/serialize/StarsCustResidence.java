/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsCustResidence implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private ResidenceType _residenceType;

    private ConstructionMaterial _constructionMaterial;

    private DecadeBuilt _decadeBuilt;

    private SquareFeet _squareFeet;

    private InsulationDepth _insulationDepth;

    private GeneralCondition _generalCondition;

    private MainCoolingSystem _mainCoolingSystem;

    private MainHeatingSystem _mainHeatingSystem;

    private NumberOfOccupants _numberOfOccupants;

    private OwnershipType _ownershipType;

    private MainFuelType _mainFuelType;

    private java.lang.String _notes;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustResidence() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCustResidence()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'constructionMaterial'.
     * 
     * @return the value of field 'constructionMaterial'.
    **/
    public ConstructionMaterial getConstructionMaterial()
    {
        return this._constructionMaterial;
    } //-- ConstructionMaterial getConstructionMaterial() 

    /**
     * Returns the value of field 'decadeBuilt'.
     * 
     * @return the value of field 'decadeBuilt'.
    **/
    public DecadeBuilt getDecadeBuilt()
    {
        return this._decadeBuilt;
    } //-- DecadeBuilt getDecadeBuilt() 

    /**
     * Returns the value of field 'generalCondition'.
     * 
     * @return the value of field 'generalCondition'.
    **/
    public GeneralCondition getGeneralCondition()
    {
        return this._generalCondition;
    } //-- GeneralCondition getGeneralCondition() 

    /**
     * Returns the value of field 'insulationDepth'.
     * 
     * @return the value of field 'insulationDepth'.
    **/
    public InsulationDepth getInsulationDepth()
    {
        return this._insulationDepth;
    } //-- InsulationDepth getInsulationDepth() 

    /**
     * Returns the value of field 'mainCoolingSystem'.
     * 
     * @return the value of field 'mainCoolingSystem'.
    **/
    public MainCoolingSystem getMainCoolingSystem()
    {
        return this._mainCoolingSystem;
    } //-- MainCoolingSystem getMainCoolingSystem() 

    /**
     * Returns the value of field 'mainFuelType'.
     * 
     * @return the value of field 'mainFuelType'.
    **/
    public MainFuelType getMainFuelType()
    {
        return this._mainFuelType;
    } //-- MainFuelType getMainFuelType() 

    /**
     * Returns the value of field 'mainHeatingSystem'.
     * 
     * @return the value of field 'mainHeatingSystem'.
    **/
    public MainHeatingSystem getMainHeatingSystem()
    {
        return this._mainHeatingSystem;
    } //-- MainHeatingSystem getMainHeatingSystem() 

    /**
     * Returns the value of field 'notes'.
     * 
     * @return the value of field 'notes'.
    **/
    public java.lang.String getNotes()
    {
        return this._notes;
    } //-- java.lang.String getNotes() 

    /**
     * Returns the value of field 'numberOfOccupants'.
     * 
     * @return the value of field 'numberOfOccupants'.
    **/
    public NumberOfOccupants getNumberOfOccupants()
    {
        return this._numberOfOccupants;
    } //-- NumberOfOccupants getNumberOfOccupants() 

    /**
     * Returns the value of field 'ownershipType'.
     * 
     * @return the value of field 'ownershipType'.
    **/
    public OwnershipType getOwnershipType()
    {
        return this._ownershipType;
    } //-- OwnershipType getOwnershipType() 

    /**
     * Returns the value of field 'residenceType'.
     * 
     * @return the value of field 'residenceType'.
    **/
    public ResidenceType getResidenceType()
    {
        return this._residenceType;
    } //-- ResidenceType getResidenceType() 

    /**
     * Returns the value of field 'squareFeet'.
     * 
     * @return the value of field 'squareFeet'.
    **/
    public SquareFeet getSquareFeet()
    {
        return this._squareFeet;
    } //-- SquareFeet getSquareFeet() 

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * 
     * 
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'constructionMaterial'.
     * 
     * @param constructionMaterial the value of field
     * 'constructionMaterial'.
    **/
    public void setConstructionMaterial(ConstructionMaterial constructionMaterial)
    {
        this._constructionMaterial = constructionMaterial;
    } //-- void setConstructionMaterial(ConstructionMaterial) 

    /**
     * Sets the value of field 'decadeBuilt'.
     * 
     * @param decadeBuilt the value of field 'decadeBuilt'.
    **/
    public void setDecadeBuilt(DecadeBuilt decadeBuilt)
    {
        this._decadeBuilt = decadeBuilt;
    } //-- void setDecadeBuilt(DecadeBuilt) 

    /**
     * Sets the value of field 'generalCondition'.
     * 
     * @param generalCondition the value of field 'generalCondition'
    **/
    public void setGeneralCondition(GeneralCondition generalCondition)
    {
        this._generalCondition = generalCondition;
    } //-- void setGeneralCondition(GeneralCondition) 

    /**
     * Sets the value of field 'insulationDepth'.
     * 
     * @param insulationDepth the value of field 'insulationDepth'.
    **/
    public void setInsulationDepth(InsulationDepth insulationDepth)
    {
        this._insulationDepth = insulationDepth;
    } //-- void setInsulationDepth(InsulationDepth) 

    /**
     * Sets the value of field 'mainCoolingSystem'.
     * 
     * @param mainCoolingSystem the value of field
     * 'mainCoolingSystem'.
    **/
    public void setMainCoolingSystem(MainCoolingSystem mainCoolingSystem)
    {
        this._mainCoolingSystem = mainCoolingSystem;
    } //-- void setMainCoolingSystem(MainCoolingSystem) 

    /**
     * Sets the value of field 'mainFuelType'.
     * 
     * @param mainFuelType the value of field 'mainFuelType'.
    **/
    public void setMainFuelType(MainFuelType mainFuelType)
    {
        this._mainFuelType = mainFuelType;
    } //-- void setMainFuelType(MainFuelType) 

    /**
     * Sets the value of field 'mainHeatingSystem'.
     * 
     * @param mainHeatingSystem the value of field
     * 'mainHeatingSystem'.
    **/
    public void setMainHeatingSystem(MainHeatingSystem mainHeatingSystem)
    {
        this._mainHeatingSystem = mainHeatingSystem;
    } //-- void setMainHeatingSystem(MainHeatingSystem) 

    /**
     * Sets the value of field 'notes'.
     * 
     * @param notes the value of field 'notes'.
    **/
    public void setNotes(java.lang.String notes)
    {
        this._notes = notes;
    } //-- void setNotes(java.lang.String) 

    /**
     * Sets the value of field 'numberOfOccupants'.
     * 
     * @param numberOfOccupants the value of field
     * 'numberOfOccupants'.
    **/
    public void setNumberOfOccupants(NumberOfOccupants numberOfOccupants)
    {
        this._numberOfOccupants = numberOfOccupants;
    } //-- void setNumberOfOccupants(NumberOfOccupants) 

    /**
     * Sets the value of field 'ownershipType'.
     * 
     * @param ownershipType the value of field 'ownershipType'.
    **/
    public void setOwnershipType(OwnershipType ownershipType)
    {
        this._ownershipType = ownershipType;
    } //-- void setOwnershipType(OwnershipType) 

    /**
     * Sets the value of field 'residenceType'.
     * 
     * @param residenceType the value of field 'residenceType'.
    **/
    public void setResidenceType(ResidenceType residenceType)
    {
        this._residenceType = residenceType;
    } //-- void setResidenceType(ResidenceType) 

    /**
     * Sets the value of field 'squareFeet'.
     * 
     * @param squareFeet the value of field 'squareFeet'.
    **/
    public void setSquareFeet(SquareFeet squareFeet)
    {
        this._squareFeet = squareFeet;
    } //-- void setSquareFeet(SquareFeet) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

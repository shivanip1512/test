package com.cannontech.stars.xml.serialize;

public class WaterHeater {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private NumberOfGallons numberOfGallons;

    private EnergySource energySource;

    private int numberOfElements;

    /**
     * keeps track of state for field: _numberOfElements
    **/
    private boolean _has_numberOfElements;


      //----------------/
     //- Constructors -/
    //----------------/

    public WaterHeater() {
        super();
        this.numberOfGallons = new NumberOfGallons();
        this.energySource = new EnergySource();
    } //-- com.cannontech.stars.xml.serialize.WaterHeater()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteNumberOfElements()
    {
        this._has_numberOfElements= false;
    } //-- void deleteNumberOfElements() 

    /**
     * Returns the value of field 'energySource'.
     * 
     * @return the value of field 'energySource'.
    **/
    public EnergySource getEnergySource()
    {
        return this.energySource;
    } //-- EnergySource getEnergySource() 

    /**
     * Returns the value of field 'numberOfElements'.
     * 
     * @return the value of field 'numberOfElements'.
    **/
    public int getNumberOfElements()
    {
        return this.numberOfElements;
    } //-- int getNumberOfElements() 

    /**
     * Returns the value of field 'numberOfGallons'.
     * 
     * @return the value of field 'numberOfGallons'.
    **/
    public NumberOfGallons getNumberOfGallons()
    {
        return this.numberOfGallons;
    } //-- NumberOfGallons getNumberOfGallons() 

    /**
    **/
    public boolean hasNumberOfElements()
    {
        return this._has_numberOfElements;
    } //-- boolean hasNumberOfElements() 

    /**
     * Sets the value of field 'energySource'.
     * 
     * @param energySource the value of field 'energySource'.
    **/
    public void setEnergySource(EnergySource energySource)
    {
        this.energySource = energySource;
    } //-- void setEnergySource(EnergySource) 

    /**
     * Sets the value of field 'numberOfElements'.
     * 
     * @param numberOfElements the value of field 'numberOfElements'
    **/
    public void setNumberOfElements(int numberOfElements)
    {
        this.numberOfElements = numberOfElements;
        this._has_numberOfElements = true;
    } //-- void setNumberOfElements(int) 

    /**
     * Sets the value of field 'numberOfGallons'.
     * 
     * @param numberOfGallons the value of field 'numberOfGallons'.
    **/
    public void setNumberOfGallons(NumberOfGallons numberOfGallons)
    {
        this.numberOfGallons = numberOfGallons;
    } //-- void setNumberOfGallons(NumberOfGallons) 

}

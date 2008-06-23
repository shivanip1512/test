package com.cannontech.stars.xml.serialize;

public class StarsGetEnergyCompanySettings {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _energyCompanyID;

    /**
     * keeps track of state for field: _energyCompanyID
    **/
    private boolean _has_energyCompanyID;

    private java.lang.String _programCategory;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetEnergyCompanySettings() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettings()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteEnergyCompanyID()
    {
        this._has_energyCompanyID= false;
    } //-- void deleteEnergyCompanyID() 

    /**
     * Returns the value of field 'energyCompanyID'.
     * 
     * @return the value of field 'energyCompanyID'.
    **/
    public int getEnergyCompanyID()
    {
        return this._energyCompanyID;
    } //-- int getEnergyCompanyID() 

    /**
     * Returns the value of field 'programCategory'.
     * 
     * @return the value of field 'programCategory'.
    **/
    public java.lang.String getProgramCategory()
    {
        return this._programCategory;
    } //-- java.lang.String getProgramCategory() 

    /**
    **/
    public boolean hasEnergyCompanyID()
    {
        return this._has_energyCompanyID;
    } //-- boolean hasEnergyCompanyID() 

    /**
     * Sets the value of field 'energyCompanyID'.
     * 
     * @param energyCompanyID the value of field 'energyCompanyID'.
    **/
    public void setEnergyCompanyID(int energyCompanyID)
    {
        this._energyCompanyID = energyCompanyID;
        this._has_energyCompanyID = true;
    } //-- void setEnergyCompanyID(int) 

    /**
     * Sets the value of field 'programCategory'.
     * 
     * @param programCategory the value of field 'programCategory'.
    **/
    public void setProgramCategory(java.lang.String programCategory)
    {
        this._programCategory = programCategory;
    } //-- void setProgramCategory(java.lang.String) 

}

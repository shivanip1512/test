/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

public class StarsProgramSignUp {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _energyCompanyID;

    /**
     * keeps track of state for field: _energyCompanyID
    **/
    private boolean _has_energyCompanyID;

    private java.lang.String _accountNumber;

    private StarsSULMPrograms _starsSULMPrograms;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramSignUp() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramSignUp()


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
     * Returns the value of field 'accountNumber'.
     * 
     * @return the value of field 'accountNumber'.
    **/
    public java.lang.String getAccountNumber()
    {
        return this._accountNumber;
    } //-- java.lang.String getAccountNumber() 

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
     * Returns the value of field 'starsSULMPrograms'.
     * 
     * @return the value of field 'starsSULMPrograms'.
    **/
    public StarsSULMPrograms getStarsSULMPrograms()
    {
        return this._starsSULMPrograms;
    } //-- StarsSULMPrograms getStarsSULMPrograms() 

    /**
    **/
    public boolean hasEnergyCompanyID()
    {
        return this._has_energyCompanyID;
    } //-- boolean hasEnergyCompanyID() 

    /**
     * Sets the value of field 'accountNumber'.
     * 
     * @param accountNumber the value of field 'accountNumber'.
    **/
    public void setAccountNumber(java.lang.String accountNumber)
    {
        this._accountNumber = accountNumber;
    } //-- void setAccountNumber(java.lang.String) 

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
     * Sets the value of field 'starsSULMPrograms'.
     * 
     * @param starsSULMPrograms the value of field
     * 'starsSULMPrograms'.
    **/
    public void setStarsSULMPrograms(StarsSULMPrograms starsSULMPrograms)
    {
        this._starsSULMPrograms = starsSULMPrograms;
    } //-- void setStarsSULMPrograms(StarsSULMPrograms) 

}

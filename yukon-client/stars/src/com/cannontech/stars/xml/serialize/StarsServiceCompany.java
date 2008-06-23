/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

public class StarsServiceCompany {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _companyID;

    /**
     * keeps track of state for field: _companyID
    **/
    private boolean _has_companyID;

    private boolean _inherited;

    /**
     * keeps track of state for field: _inherited
    **/
    private boolean _has_inherited;

    private java.lang.String _companyName;

    private java.lang.String _mainPhoneNumber;

    private java.lang.String _mainFaxNumber;

    private CompanyAddress _companyAddress;

    private PrimaryContact _primaryContact;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsServiceCompany() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsServiceCompany()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteCompanyID()
    {
        this._has_companyID= false;
    } //-- void deleteCompanyID() 

    /**
    **/
    public void deleteInherited()
    {
        this._has_inherited= false;
    } //-- void deleteInherited() 

    /**
     * Returns the value of field 'companyAddress'.
     * 
     * @return the value of field 'companyAddress'.
    **/
    public CompanyAddress getCompanyAddress()
    {
        return this._companyAddress;
    } //-- CompanyAddress getCompanyAddress() 

    /**
     * Returns the value of field 'companyID'.
     * 
     * @return the value of field 'companyID'.
    **/
    public int getCompanyID()
    {
        return this._companyID;
    } //-- int getCompanyID() 

    /**
     * Returns the value of field 'companyName'.
     * 
     * @return the value of field 'companyName'.
    **/
    public java.lang.String getCompanyName()
    {
        return this._companyName;
    } //-- java.lang.String getCompanyName() 

    /**
     * Returns the value of field 'inherited'.
     * 
     * @return the value of field 'inherited'.
    **/
    public boolean getInherited()
    {
        return this._inherited;
    } //-- boolean getInherited() 

    /**
     * Returns the value of field 'mainFaxNumber'.
     * 
     * @return the value of field 'mainFaxNumber'.
    **/
    public java.lang.String getMainFaxNumber()
    {
        return this._mainFaxNumber;
    } //-- java.lang.String getMainFaxNumber() 

    /**
     * Returns the value of field 'mainPhoneNumber'.
     * 
     * @return the value of field 'mainPhoneNumber'.
    **/
    public java.lang.String getMainPhoneNumber()
    {
        return this._mainPhoneNumber;
    } //-- java.lang.String getMainPhoneNumber() 

    /**
     * Returns the value of field 'primaryContact'.
     * 
     * @return the value of field 'primaryContact'.
    **/
    public PrimaryContact getPrimaryContact()
    {
        return this._primaryContact;
    } //-- PrimaryContact getPrimaryContact() 

    /**
    **/
    public boolean hasCompanyID()
    {
        return this._has_companyID;
    } //-- boolean hasCompanyID() 

    /**
    **/
    public boolean hasInherited()
    {
        return this._has_inherited;
    } //-- boolean hasInherited() 

    /**
     * Sets the value of field 'companyAddress'.
     * 
     * @param companyAddress the value of field 'companyAddress'.
    **/
    public void setCompanyAddress(CompanyAddress companyAddress)
    {
        this._companyAddress = companyAddress;
    } //-- void setCompanyAddress(CompanyAddress) 

    /**
     * Sets the value of field 'companyID'.
     * 
     * @param companyID the value of field 'companyID'.
    **/
    public void setCompanyID(int companyID)
    {
        this._companyID = companyID;
        this._has_companyID = true;
    } //-- void setCompanyID(int) 

    /**
     * Sets the value of field 'companyName'.
     * 
     * @param companyName the value of field 'companyName'.
    **/
    public void setCompanyName(java.lang.String companyName)
    {
        this._companyName = companyName;
    } //-- void setCompanyName(java.lang.String) 

    /**
     * Sets the value of field 'inherited'.
     * 
     * @param inherited the value of field 'inherited'.
    **/
    public void setInherited(boolean inherited)
    {
        this._inherited = inherited;
        this._has_inherited = true;
    } //-- void setInherited(boolean) 

    /**
     * Sets the value of field 'mainFaxNumber'.
     * 
     * @param mainFaxNumber the value of field 'mainFaxNumber'.
    **/
    public void setMainFaxNumber(java.lang.String mainFaxNumber)
    {
        this._mainFaxNumber = mainFaxNumber;
    } //-- void setMainFaxNumber(java.lang.String) 

    /**
     * Sets the value of field 'mainPhoneNumber'.
     * 
     * @param mainPhoneNumber the value of field 'mainPhoneNumber'.
    **/
    public void setMainPhoneNumber(java.lang.String mainPhoneNumber)
    {
        this._mainPhoneNumber = mainPhoneNumber;
    } //-- void setMainPhoneNumber(java.lang.String) 

    /**
     * Sets the value of field 'primaryContact'.
     * 
     * @param primaryContact the value of field 'primaryContact'.
    **/
    public void setPrimaryContact(PrimaryContact primaryContact)
    {
        this._primaryContact = primaryContact;
    } //-- void setPrimaryContact(PrimaryContact) 

}

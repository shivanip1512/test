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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsEnergyCompany implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _energyCompanyID;

    /**
     * keeps track of state for field: _energyCompanyID
    **/
    private boolean _has_energyCompanyID;

    private java.lang.String _companyName;

    private java.lang.String _mainPhoneNumber;

    private java.lang.String _mainFaxNumber;

    private java.lang.String _email;

    private CompanyAddress _companyAddress;

    private java.lang.String _timeZone;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsEnergyCompany() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsEnergyCompany()


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
     * Returns the value of field 'companyAddress'.
     * 
     * @return the value of field 'companyAddress'.
    **/
    public CompanyAddress getCompanyAddress()
    {
        return this._companyAddress;
    } //-- CompanyAddress getCompanyAddress() 

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
     * Returns the value of field 'email'.
     * 
     * @return the value of field 'email'.
    **/
    public java.lang.String getEmail()
    {
        return this._email;
    } //-- java.lang.String getEmail() 

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
     * Returns the value of field 'timeZone'.
     * 
     * @return the value of field 'timeZone'.
    **/
    public java.lang.String getTimeZone()
    {
        return this._timeZone;
    } //-- java.lang.String getTimeZone() 

    /**
    **/
    public boolean hasEnergyCompanyID()
    {
        return this._has_energyCompanyID;
    } //-- boolean hasEnergyCompanyID() 

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
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

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
     * Sets the value of field 'companyName'.
     * 
     * @param companyName the value of field 'companyName'.
    **/
    public void setCompanyName(java.lang.String companyName)
    {
        this._companyName = companyName;
    } //-- void setCompanyName(java.lang.String) 

    /**
     * Sets the value of field 'email'.
     * 
     * @param email the value of field 'email'.
    **/
    public void setEmail(java.lang.String email)
    {
        this._email = email;
    } //-- void setEmail(java.lang.String) 

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
     * Sets the value of field 'timeZone'.
     * 
     * @param timeZone the value of field 'timeZone'.
    **/
    public void setTimeZone(java.lang.String timeZone)
    {
        this._timeZone = timeZone;
    } //-- void setTimeZone(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsEnergyCompany unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsEnergyCompany) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsEnergyCompany.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsEnergyCompany unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

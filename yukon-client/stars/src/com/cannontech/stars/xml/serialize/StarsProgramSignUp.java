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
public class StarsProgramSignUp implements java.io.Serializable {


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

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsProgramSignUp unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsProgramSignUp) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsProgramSignUp.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsProgramSignUp unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

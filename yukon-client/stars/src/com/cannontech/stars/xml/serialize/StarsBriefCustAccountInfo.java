/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsBriefCustAccountInfo.java,v 1.12 2004/10/26 21:15:41 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.12 $ $Date: 2004/10/26 21:15:41 $
**/
public class StarsBriefCustAccountInfo implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _accountID;

    /**
     * keeps track of state for field: _accountID
    **/
    private boolean _has_accountID;

    private int _energyCompanyID;

    /**
     * keeps track of state for field: _energyCompanyID
    **/
    private boolean _has_energyCompanyID;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsBriefCustAccountInfo() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsBriefCustAccountInfo()


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
     * Returns the value of field 'accountID'.
     * 
     * @return the value of field 'accountID'.
    **/
    public int getAccountID()
    {
        return this._accountID;
    } //-- int getAccountID() 

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
    **/
    public boolean hasAccountID()
    {
        return this._has_accountID;
    } //-- boolean hasAccountID() 

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
     * Sets the value of field 'accountID'.
     * 
     * @param accountID the value of field 'accountID'.
    **/
    public void setAccountID(int accountID)
    {
        this._accountID = accountID;
        this._has_accountID = true;
    } //-- void setAccountID(int) 

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
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsBriefCustAccountInfo unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsBriefCustAccountInfo) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsBriefCustAccountInfo.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsBriefCustAccountInfo unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

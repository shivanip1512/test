/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsCustomerAddress.java,v 1.59 2003/12/18 16:43:33 zyao Exp $
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
 * @version $Revision: 1.59 $ $Date: 2003/12/18 16:43:33 $
**/
public abstract class StarsCustomerAddress implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _addressID;

    /**
     * keeps track of state for field: _addressID
    **/
    private boolean _has_addressID;

    private java.lang.String _streetAddr1;

    private java.lang.String _streetAddr2;

    private java.lang.String _city;

    private java.lang.String _state;

    private java.lang.String _zip;

    private java.lang.String _county;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustomerAddress() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerAddress()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteAddressID()
    {
        this._has_addressID= false;
    } //-- void deleteAddressID() 

    /**
     * Returns the value of field 'addressID'.
     * 
     * @return the value of field 'addressID'.
    **/
    public int getAddressID()
    {
        return this._addressID;
    } //-- int getAddressID() 

    /**
     * Returns the value of field 'city'.
     * 
     * @return the value of field 'city'.
    **/
    public java.lang.String getCity()
    {
        return this._city;
    } //-- java.lang.String getCity() 

    /**
     * Returns the value of field 'county'.
     * 
     * @return the value of field 'county'.
    **/
    public java.lang.String getCounty()
    {
        return this._county;
    } //-- java.lang.String getCounty() 

    /**
     * Returns the value of field 'state'.
     * 
     * @return the value of field 'state'.
    **/
    public java.lang.String getState()
    {
        return this._state;
    } //-- java.lang.String getState() 

    /**
     * Returns the value of field 'streetAddr1'.
     * 
     * @return the value of field 'streetAddr1'.
    **/
    public java.lang.String getStreetAddr1()
    {
        return this._streetAddr1;
    } //-- java.lang.String getStreetAddr1() 

    /**
     * Returns the value of field 'streetAddr2'.
     * 
     * @return the value of field 'streetAddr2'.
    **/
    public java.lang.String getStreetAddr2()
    {
        return this._streetAddr2;
    } //-- java.lang.String getStreetAddr2() 

    /**
     * Returns the value of field 'zip'.
     * 
     * @return the value of field 'zip'.
    **/
    public java.lang.String getZip()
    {
        return this._zip;
    } //-- java.lang.String getZip() 

    /**
    **/
    public boolean hasAddressID()
    {
        return this._has_addressID;
    } //-- boolean hasAddressID() 

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
     * Sets the value of field 'addressID'.
     * 
     * @param addressID the value of field 'addressID'.
    **/
    public void setAddressID(int addressID)
    {
        this._addressID = addressID;
        this._has_addressID = true;
    } //-- void setAddressID(int) 

    /**
     * Sets the value of field 'city'.
     * 
     * @param city the value of field 'city'.
    **/
    public void setCity(java.lang.String city)
    {
        this._city = city;
    } //-- void setCity(java.lang.String) 

    /**
     * Sets the value of field 'county'.
     * 
     * @param county the value of field 'county'.
    **/
    public void setCounty(java.lang.String county)
    {
        this._county = county;
    } //-- void setCounty(java.lang.String) 

    /**
     * Sets the value of field 'state'.
     * 
     * @param state the value of field 'state'.
    **/
    public void setState(java.lang.String state)
    {
        this._state = state;
    } //-- void setState(java.lang.String) 

    /**
     * Sets the value of field 'streetAddr1'.
     * 
     * @param streetAddr1 the value of field 'streetAddr1'.
    **/
    public void setStreetAddr1(java.lang.String streetAddr1)
    {
        this._streetAddr1 = streetAddr1;
    } //-- void setStreetAddr1(java.lang.String) 

    /**
     * Sets the value of field 'streetAddr2'.
     * 
     * @param streetAddr2 the value of field 'streetAddr2'.
    **/
    public void setStreetAddr2(java.lang.String streetAddr2)
    {
        this._streetAddr2 = streetAddr2;
    } //-- void setStreetAddr2(java.lang.String) 

    /**
     * Sets the value of field 'zip'.
     * 
     * @param zip the value of field 'zip'.
    **/
    public void setZip(java.lang.String zip)
    {
        this._zip = zip;
    } //-- void setZip(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

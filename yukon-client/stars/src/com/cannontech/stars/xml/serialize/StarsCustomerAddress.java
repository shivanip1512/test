/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsCustomerAddress.java,v 1.1 2002/07/16 19:50:04 Yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:04 $
**/
public abstract class StarsCustomerAddress implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _streetAddr1;

    private java.lang.String _streetAddr2;

    private java.lang.String _city;

    private java.lang.String _state;

    private java.lang.String _zip;


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
    public java.lang.String getCity()
    {
        return this._city;
    } //-- java.lang.String getCity() 

    /**
    **/
    public java.lang.String getState()
    {
        return this._state;
    } //-- java.lang.String getState() 

    /**
    **/
    public java.lang.String getStreetAddr1()
    {
        return this._streetAddr1;
    } //-- java.lang.String getStreetAddr1() 

    /**
    **/
    public java.lang.String getStreetAddr2()
    {
        return this._streetAddr2;
    } //-- java.lang.String getStreetAddr2() 

    /**
    **/
    public java.lang.String getZip()
    {
        return this._zip;
    } //-- java.lang.String getZip() 

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
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param city
    **/
    public void setCity(java.lang.String city)
    {
        this._city = city;
    } //-- void setCity(java.lang.String) 

    /**
     * 
     * @param state
    **/
    public void setState(java.lang.String state)
    {
        this._state = state;
    } //-- void setState(java.lang.String) 

    /**
     * 
     * @param streetAddr1
    **/
    public void setStreetAddr1(java.lang.String streetAddr1)
    {
        this._streetAddr1 = streetAddr1;
    } //-- void setStreetAddr1(java.lang.String) 

    /**
     * 
     * @param streetAddr2
    **/
    public void setStreetAddr2(java.lang.String streetAddr2)
    {
        this._streetAddr2 = streetAddr2;
    } //-- void setStreetAddr2(java.lang.String) 

    /**
     * 
     * @param zip
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

/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: ExpressCom.java,v 1.3 2004/07/28 22:59:06 yao Exp $
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
 * @version $Revision: 1.3 $ $Date: 2004/07/28 22:59:06 $
**/
public class ExpressCom implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _serviceProvider;

    /**
     * keeps track of state for field: _serviceProvider
    **/
    private boolean _has_serviceProvider;

    private int _GEO;

    /**
     * keeps track of state for field: _GEO
    **/
    private boolean _has_GEO;

    private int _substation;

    /**
     * keeps track of state for field: _substation
    **/
    private boolean _has_substation;

    private int _feeder;

    /**
     * keeps track of state for field: _feeder
    **/
    private boolean _has_feeder;

    private int _zip;

    /**
     * keeps track of state for field: _zip
    **/
    private boolean _has_zip;

    private int _userAddress;

    /**
     * keeps track of state for field: _userAddress
    **/
    private boolean _has_userAddress;

    private java.lang.String _program;

    private java.lang.String _splinter;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExpressCom() {
        super();
    } //-- com.cannontech.stars.xml.serialize.ExpressCom()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'feeder'.
     * 
     * @return the value of field 'feeder'.
    **/
    public int getFeeder()
    {
        return this._feeder;
    } //-- int getFeeder() 

    /**
     * Returns the value of field 'GEO'.
     * 
     * @return the value of field 'GEO'.
    **/
    public int getGEO()
    {
        return this._GEO;
    } //-- int getGEO() 

    /**
     * Returns the value of field 'program'.
     * 
     * @return the value of field 'program'.
    **/
    public java.lang.String getProgram()
    {
        return this._program;
    } //-- java.lang.String getProgram() 

    /**
     * Returns the value of field 'serviceProvider'.
     * 
     * @return the value of field 'serviceProvider'.
    **/
    public int getServiceProvider()
    {
        return this._serviceProvider;
    } //-- int getServiceProvider() 

    /**
     * Returns the value of field 'splinter'.
     * 
     * @return the value of field 'splinter'.
    **/
    public java.lang.String getSplinter()
    {
        return this._splinter;
    } //-- java.lang.String getSplinter() 

    /**
     * Returns the value of field 'substation'.
     * 
     * @return the value of field 'substation'.
    **/
    public int getSubstation()
    {
        return this._substation;
    } //-- int getSubstation() 

    /**
     * Returns the value of field 'userAddress'.
     * 
     * @return the value of field 'userAddress'.
    **/
    public int getUserAddress()
    {
        return this._userAddress;
    } //-- int getUserAddress() 

    /**
     * Returns the value of field 'zip'.
     * 
     * @return the value of field 'zip'.
    **/
    public int getZip()
    {
        return this._zip;
    } //-- int getZip() 

    /**
    **/
    public boolean hasFeeder()
    {
        return this._has_feeder;
    } //-- boolean hasFeeder() 

    /**
    **/
    public boolean hasGEO()
    {
        return this._has_GEO;
    } //-- boolean hasGEO() 

    /**
    **/
    public boolean hasServiceProvider()
    {
        return this._has_serviceProvider;
    } //-- boolean hasServiceProvider() 

    /**
    **/
    public boolean hasSubstation()
    {
        return this._has_substation;
    } //-- boolean hasSubstation() 

    /**
    **/
    public boolean hasUserAddress()
    {
        return this._has_userAddress;
    } //-- boolean hasUserAddress() 

    /**
    **/
    public boolean hasZip()
    {
        return this._has_zip;
    } //-- boolean hasZip() 

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
     * Sets the value of field 'feeder'.
     * 
     * @param feeder the value of field 'feeder'.
    **/
    public void setFeeder(int feeder)
    {
        this._feeder = feeder;
        this._has_feeder = true;
    } //-- void setFeeder(int) 

    /**
     * Sets the value of field 'GEO'.
     * 
     * @param GEO the value of field 'GEO'.
    **/
    public void setGEO(int GEO)
    {
        this._GEO = GEO;
        this._has_GEO = true;
    } //-- void setGEO(int) 

    /**
     * Sets the value of field 'program'.
     * 
     * @param program the value of field 'program'.
    **/
    public void setProgram(java.lang.String program)
    {
        this._program = program;
    } //-- void setProgram(java.lang.String) 

    /**
     * Sets the value of field 'serviceProvider'.
     * 
     * @param serviceProvider the value of field 'serviceProvider'.
    **/
    public void setServiceProvider(int serviceProvider)
    {
        this._serviceProvider = serviceProvider;
        this._has_serviceProvider = true;
    } //-- void setServiceProvider(int) 

    /**
     * Sets the value of field 'splinter'.
     * 
     * @param splinter the value of field 'splinter'.
    **/
    public void setSplinter(java.lang.String splinter)
    {
        this._splinter = splinter;
    } //-- void setSplinter(java.lang.String) 

    /**
     * Sets the value of field 'substation'.
     * 
     * @param substation the value of field 'substation'.
    **/
    public void setSubstation(int substation)
    {
        this._substation = substation;
        this._has_substation = true;
    } //-- void setSubstation(int) 

    /**
     * Sets the value of field 'userAddress'.
     * 
     * @param userAddress the value of field 'userAddress'.
    **/
    public void setUserAddress(int userAddress)
    {
        this._userAddress = userAddress;
        this._has_userAddress = true;
    } //-- void setUserAddress(int) 

    /**
     * Sets the value of field 'zip'.
     * 
     * @param zip the value of field 'zip'.
    **/
    public void setZip(int zip)
    {
        this._zip = zip;
        this._has_zip = true;
    } //-- void setZip(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.ExpressCom unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.ExpressCom) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.ExpressCom.class, reader);
    } //-- com.cannontech.stars.xml.serialize.ExpressCom unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

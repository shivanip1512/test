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
 * @version $Revision$ $Date$
**/
public class StarsSubstation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _substationID;

    /**
     * keeps track of state for field: _substationID
    **/
    private boolean _has_substationID;

    private int _routeID;

    /**
     * keeps track of state for field: _routeID
    **/
    private boolean _has_routeID;

    private boolean _inherited;

    /**
     * keeps track of state for field: _inherited
    **/
    private boolean _has_inherited;

    private java.lang.String _substationName;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSubstation() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSubstation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteInherited()
    {
        this._has_inherited= false;
    } //-- void deleteInherited() 

    /**
    **/
    public void deleteRouteID()
    {
        this._has_routeID= false;
    } //-- void deleteRouteID() 

    /**
    **/
    public void deleteSubstationID()
    {
        this._has_substationID= false;
    } //-- void deleteSubstationID() 

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
     * Returns the value of field 'routeID'.
     * 
     * @return the value of field 'routeID'.
    **/
    public int getRouteID()
    {
        return this._routeID;
    } //-- int getRouteID() 

    /**
     * Returns the value of field 'substationID'.
     * 
     * @return the value of field 'substationID'.
    **/
    public int getSubstationID()
    {
        return this._substationID;
    } //-- int getSubstationID() 

    /**
     * Returns the value of field 'substationName'.
     * 
     * @return the value of field 'substationName'.
    **/
    public java.lang.String getSubstationName()
    {
        return this._substationName;
    } //-- java.lang.String getSubstationName() 

    /**
    **/
    public boolean hasInherited()
    {
        return this._has_inherited;
    } //-- boolean hasInherited() 

    /**
    **/
    public boolean hasRouteID()
    {
        return this._has_routeID;
    } //-- boolean hasRouteID() 

    /**
    **/
    public boolean hasSubstationID()
    {
        return this._has_substationID;
    } //-- boolean hasSubstationID() 

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
     * Sets the value of field 'routeID'.
     * 
     * @param routeID the value of field 'routeID'.
    **/
    public void setRouteID(int routeID)
    {
        this._routeID = routeID;
        this._has_routeID = true;
    } //-- void setRouteID(int) 

    /**
     * Sets the value of field 'substationID'.
     * 
     * @param substationID the value of field 'substationID'.
    **/
    public void setSubstationID(int substationID)
    {
        this._substationID = substationID;
        this._has_substationID = true;
    } //-- void setSubstationID(int) 

    /**
     * Sets the value of field 'substationName'.
     * 
     * @param substationName the value of field 'substationName'.
    **/
    public void setSubstationName(java.lang.String substationName)
    {
        this._substationName = substationName;
    } //-- void setSubstationName(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSubstation unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSubstation) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSubstation.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSubstation unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

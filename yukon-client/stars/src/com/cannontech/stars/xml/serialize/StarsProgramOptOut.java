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
import java.util.Date;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsProgramOptOut implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private java.util.Date _startDateTime;

    private int _period;

    /**
     * keeps track of state for field: _period
    **/
    private boolean _has_period;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramOptOut() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramOptOut()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
     * Returns the value of field 'inventoryID'.
     * 
     * @return the value of field 'inventoryID'.
    **/
    public int getInventoryID()
    {
        return this._inventoryID;
    } //-- int getInventoryID() 

    /**
     * Returns the value of field 'period'.
     * 
     * @return the value of field 'period'.
    **/
    public int getPeriod()
    {
        return this._period;
    } //-- int getPeriod() 

    /**
     * Returns the value of field 'startDateTime'.
     * 
     * @return the value of field 'startDateTime'.
    **/
    public java.util.Date getStartDateTime()
    {
        return this._startDateTime;
    } //-- java.util.Date getStartDateTime() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
    **/
    public boolean hasPeriod()
    {
        return this._has_period;
    } //-- boolean hasPeriod() 

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
     * Sets the value of field 'inventoryID'.
     * 
     * @param inventoryID the value of field 'inventoryID'.
    **/
    public void setInventoryID(int inventoryID)
    {
        this._inventoryID = inventoryID;
        this._has_inventoryID = true;
    } //-- void setInventoryID(int) 

    /**
     * Sets the value of field 'period'.
     * 
     * @param period the value of field 'period'.
    **/
    public void setPeriod(int period)
    {
        this._period = period;
        this._has_period = true;
    } //-- void setPeriod(int) 

    /**
     * Sets the value of field 'startDateTime'.
     * 
     * @param startDateTime the value of field 'startDateTime'.
    **/
    public void setStartDateTime(java.util.Date startDateTime)
    {
        this._startDateTime = startDateTime;
    } //-- void setStartDateTime(java.util.Date) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsProgramOptOut unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsProgramOptOut) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsProgramOptOut.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsProgramOptOut unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

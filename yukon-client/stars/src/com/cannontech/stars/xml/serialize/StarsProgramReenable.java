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
public class StarsProgramReenable implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private boolean _cancelScheduledOptOut;

    /**
     * keeps track of state for field: _cancelScheduledOptOut
    **/
    private boolean _has_cancelScheduledOptOut;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramReenable() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramReenable()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteCancelScheduledOptOut()
    {
        this._has_cancelScheduledOptOut= false;
    } //-- void deleteCancelScheduledOptOut() 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
     * Returns the value of field 'cancelScheduledOptOut'.
     * 
     * @return the value of field 'cancelScheduledOptOut'.
    **/
    public boolean getCancelScheduledOptOut()
    {
        return this._cancelScheduledOptOut;
    } //-- boolean getCancelScheduledOptOut() 

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
    **/
    public boolean hasCancelScheduledOptOut()
    {
        return this._has_cancelScheduledOptOut;
    } //-- boolean hasCancelScheduledOptOut() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

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
     * Sets the value of field 'cancelScheduledOptOut'.
     * 
     * @param cancelScheduledOptOut the value of field
     * 'cancelScheduledOptOut'.
    **/
    public void setCancelScheduledOptOut(boolean cancelScheduledOptOut)
    {
        this._cancelScheduledOptOut = cancelScheduledOptOut;
        this._has_cancelScheduledOptOut = true;
    } //-- void setCancelScheduledOptOut(boolean) 

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
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsProgramReenable unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsProgramReenable) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsProgramReenable.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsProgramReenable unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

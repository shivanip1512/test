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
public class StarsDeleteLMHardware implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private boolean _deleteFromInventory;

    /**
     * keeps track of state for field: _deleteFromInventory
    **/
    private boolean _has_deleteFromInventory;

    private boolean _deleteFromYukon;

    /**
     * keeps track of state for field: _deleteFromYukon
    **/
    private boolean _has_deleteFromYukon;

    private java.util.Date _removeDate;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsDeleteLMHardware() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteLMHardware()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteDeleteFromInventory()
    {
        this._has_deleteFromInventory= false;
    } //-- void deleteDeleteFromInventory() 

    /**
    **/
    public void deleteDeleteFromYukon()
    {
        this._has_deleteFromYukon= false;
    } //-- void deleteDeleteFromYukon() 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
     * Returns the value of field 'deleteFromInventory'.
     * 
     * @return the value of field 'deleteFromInventory'.
    **/
    public boolean getDeleteFromInventory()
    {
        return this._deleteFromInventory;
    } //-- boolean getDeleteFromInventory() 

    /**
     * Returns the value of field 'deleteFromYukon'.
     * 
     * @return the value of field 'deleteFromYukon'.
    **/
    public boolean getDeleteFromYukon()
    {
        return this._deleteFromYukon;
    } //-- boolean getDeleteFromYukon() 

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
     * Returns the value of field 'removeDate'.
     * 
     * @return the value of field 'removeDate'.
    **/
    public java.util.Date getRemoveDate()
    {
        return this._removeDate;
    } //-- java.util.Date getRemoveDate() 

    /**
    **/
    public boolean hasDeleteFromInventory()
    {
        return this._has_deleteFromInventory;
    } //-- boolean hasDeleteFromInventory() 

    /**
    **/
    public boolean hasDeleteFromYukon()
    {
        return this._has_deleteFromYukon;
    } //-- boolean hasDeleteFromYukon() 

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
     * Sets the value of field 'deleteFromInventory'.
     * 
     * @param deleteFromInventory the value of field
     * 'deleteFromInventory'.
    **/
    public void setDeleteFromInventory(boolean deleteFromInventory)
    {
        this._deleteFromInventory = deleteFromInventory;
        this._has_deleteFromInventory = true;
    } //-- void setDeleteFromInventory(boolean) 

    /**
     * Sets the value of field 'deleteFromYukon'.
     * 
     * @param deleteFromYukon the value of field 'deleteFromYukon'.
    **/
    public void setDeleteFromYukon(boolean deleteFromYukon)
    {
        this._deleteFromYukon = deleteFromYukon;
        this._has_deleteFromYukon = true;
    } //-- void setDeleteFromYukon(boolean) 

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
     * Sets the value of field 'removeDate'.
     * 
     * @param removeDate the value of field 'removeDate'.
    **/
    public void setRemoveDate(java.util.Date removeDate)
    {
        this._removeDate = removeDate;
    } //-- void setRemoveDate(java.util.Date) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsDeleteLMHardware unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsDeleteLMHardware) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsDeleteLMHardware.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteLMHardware unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

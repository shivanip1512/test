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
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsUpdateLMHardwareConfig implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private boolean _saveToBatch;

    /**
     * keeps track of state for field: _saveToBatch
    **/
    private boolean _has_saveToBatch;

    private java.util.Vector _starsLMHardwareConfigList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateLMHardwareConfig() {
        super();
        _starsLMHardwareConfigList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsLMHardwareConfig
    **/
    public void addStarsLMHardwareConfig(StarsLMHardwareConfig vStarsLMHardwareConfig)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareConfigList.addElement(vStarsLMHardwareConfig);
    } //-- void addStarsLMHardwareConfig(StarsLMHardwareConfig) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardwareConfig
    **/
    public void addStarsLMHardwareConfig(int index, StarsLMHardwareConfig vStarsLMHardwareConfig)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareConfigList.insertElementAt(vStarsLMHardwareConfig, index);
    } //-- void addStarsLMHardwareConfig(int, StarsLMHardwareConfig) 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
    **/
    public void deleteSaveToBatch()
    {
        this._has_saveToBatch= false;
    } //-- void deleteSaveToBatch() 

    /**
    **/
    public java.util.Enumeration enumerateStarsLMHardwareConfig()
    {
        return _starsLMHardwareConfigList.elements();
    } //-- java.util.Enumeration enumerateStarsLMHardwareConfig() 

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
     * Returns the value of field 'saveToBatch'.
     * 
     * @return the value of field 'saveToBatch'.
    **/
    public boolean getSaveToBatch()
    {
        return this._saveToBatch;
    } //-- boolean getSaveToBatch() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardwareConfig getStarsLMHardwareConfig(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareConfigList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsLMHardwareConfig) _starsLMHardwareConfigList.elementAt(index);
    } //-- StarsLMHardwareConfig getStarsLMHardwareConfig(int) 

    /**
    **/
    public StarsLMHardwareConfig[] getStarsLMHardwareConfig()
    {
        int size = _starsLMHardwareConfigList.size();
        StarsLMHardwareConfig[] mArray = new StarsLMHardwareConfig[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsLMHardwareConfig) _starsLMHardwareConfigList.elementAt(index);
        }
        return mArray;
    } //-- StarsLMHardwareConfig[] getStarsLMHardwareConfig() 

    /**
    **/
    public int getStarsLMHardwareConfigCount()
    {
        return _starsLMHardwareConfigList.size();
    } //-- int getStarsLMHardwareConfigCount() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
    **/
    public boolean hasSaveToBatch()
    {
        return this._has_saveToBatch;
    } //-- boolean hasSaveToBatch() 

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
    **/
    public void removeAllStarsLMHardwareConfig()
    {
        _starsLMHardwareConfigList.removeAllElements();
    } //-- void removeAllStarsLMHardwareConfig() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardwareConfig removeStarsLMHardwareConfig(int index)
    {
        java.lang.Object obj = _starsLMHardwareConfigList.elementAt(index);
        _starsLMHardwareConfigList.removeElementAt(index);
        return (StarsLMHardwareConfig) obj;
    } //-- StarsLMHardwareConfig removeStarsLMHardwareConfig(int) 

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
     * Sets the value of field 'saveToBatch'.
     * 
     * @param saveToBatch the value of field 'saveToBatch'.
    **/
    public void setSaveToBatch(boolean saveToBatch)
    {
        this._saveToBatch = saveToBatch;
        this._has_saveToBatch = true;
    } //-- void setSaveToBatch(boolean) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardwareConfig
    **/
    public void setStarsLMHardwareConfig(int index, StarsLMHardwareConfig vStarsLMHardwareConfig)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareConfigList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsLMHardwareConfigList.setElementAt(vStarsLMHardwareConfig, index);
    } //-- void setStarsLMHardwareConfig(int, StarsLMHardwareConfig) 

    /**
     * 
     * 
     * @param starsLMHardwareConfigArray
    **/
    public void setStarsLMHardwareConfig(StarsLMHardwareConfig[] starsLMHardwareConfigArray)
    {
        //-- copy array
        _starsLMHardwareConfigList.removeAllElements();
        for (int i = 0; i < starsLMHardwareConfigArray.length; i++) {
            _starsLMHardwareConfigList.addElement(starsLMHardwareConfigArray[i]);
        }
    } //-- void setStarsLMHardwareConfig(StarsLMHardwareConfig) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

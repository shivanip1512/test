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

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsLMHardwareHistory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private java.util.Vector _starsLMHardwareEventList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMHardwareHistory() {
        super();
        _starsLMHardwareEventList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsLMHardwareHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsLMHardwareEvent
    **/
    public void addStarsLMHardwareEvent(StarsLMHardwareEvent vStarsLMHardwareEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareEventList.addElement(vStarsLMHardwareEvent);
    } //-- void addStarsLMHardwareEvent(StarsLMHardwareEvent) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardwareEvent
    **/
    public void addStarsLMHardwareEvent(int index, StarsLMHardwareEvent vStarsLMHardwareEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareEventList.insertElementAt(vStarsLMHardwareEvent, index);
    } //-- void addStarsLMHardwareEvent(int, StarsLMHardwareEvent) 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
    **/
    public java.util.Enumeration enumerateStarsLMHardwareEvent()
    {
        return _starsLMHardwareEventList.elements();
    } //-- java.util.Enumeration enumerateStarsLMHardwareEvent() 

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
     * 
     * 
     * @param index
    **/
    public StarsLMHardwareEvent getStarsLMHardwareEvent(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsLMHardwareEvent) _starsLMHardwareEventList.elementAt(index);
    } //-- StarsLMHardwareEvent getStarsLMHardwareEvent(int) 

    /**
    **/
    public StarsLMHardwareEvent[] getStarsLMHardwareEvent()
    {
        int size = _starsLMHardwareEventList.size();
        StarsLMHardwareEvent[] mArray = new StarsLMHardwareEvent[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsLMHardwareEvent) _starsLMHardwareEventList.elementAt(index);
        }
        return mArray;
    } //-- StarsLMHardwareEvent[] getStarsLMHardwareEvent() 

    /**
    **/
    public int getStarsLMHardwareEventCount()
    {
        return _starsLMHardwareEventList.size();
    } //-- int getStarsLMHardwareEventCount() 

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
    **/
    public void removeAllStarsLMHardwareEvent()
    {
        _starsLMHardwareEventList.removeAllElements();
    } //-- void removeAllStarsLMHardwareEvent() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardwareEvent removeStarsLMHardwareEvent(int index)
    {
        java.lang.Object obj = _starsLMHardwareEventList.elementAt(index);
        _starsLMHardwareEventList.removeElementAt(index);
        return (StarsLMHardwareEvent) obj;
    } //-- StarsLMHardwareEvent removeStarsLMHardwareEvent(int) 

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
     * @param index
     * @param vStarsLMHardwareEvent
    **/
    public void setStarsLMHardwareEvent(int index, StarsLMHardwareEvent vStarsLMHardwareEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsLMHardwareEventList.setElementAt(vStarsLMHardwareEvent, index);
    } //-- void setStarsLMHardwareEvent(int, StarsLMHardwareEvent) 

    /**
     * 
     * 
     * @param starsLMHardwareEventArray
    **/
    public void setStarsLMHardwareEvent(StarsLMHardwareEvent[] starsLMHardwareEventArray)
    {
        //-- copy array
        _starsLMHardwareEventList.removeAllElements();
        for (int i = 0; i < starsLMHardwareEventArray.length; i++) {
            _starsLMHardwareEventList.addElement(starsLMHardwareEventArray[i]);
        }
    } //-- void setStarsLMHardwareEvent(StarsLMHardwareEvent) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsLMHardwareHistory unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsLMHardwareHistory) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsLMHardwareHistory.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsLMHardwareHistory unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

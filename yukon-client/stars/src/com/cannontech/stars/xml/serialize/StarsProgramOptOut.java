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
public class StarsProgramOptOut implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Date _startDateTime;

    private int _period;

    /**
     * keeps track of state for field: _period
    **/
    private boolean _has_period;

    private java.util.Vector _inventoryIDList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramOptOut() {
        super();
        _inventoryIDList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramOptOut()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vInventoryID
    **/
    public void addInventoryID(int vInventoryID)
        throws java.lang.IndexOutOfBoundsException
    {
        _inventoryIDList.addElement(new Integer(vInventoryID));
    } //-- void addInventoryID(int) 

    /**
     * 
     * 
     * @param index
     * @param vInventoryID
    **/
    public void addInventoryID(int index, int vInventoryID)
        throws java.lang.IndexOutOfBoundsException
    {
        _inventoryIDList.insertElementAt(new Integer(vInventoryID), index);
    } //-- void addInventoryID(int, int) 

    /**
    **/
    public java.util.Enumeration enumerateInventoryID()
    {
        return _inventoryIDList.elements();
    } //-- java.util.Enumeration enumerateInventoryID() 

    /**
     * 
     * 
     * @param index
    **/
    public int getInventoryID(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _inventoryIDList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return ((Integer)_inventoryIDList.elementAt(index)).intValue();
    } //-- int getInventoryID(int) 

    /**
    **/
    public int[] getInventoryID()
    {
        int size = _inventoryIDList.size();
        int[] mArray = new int[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = ((Integer)_inventoryIDList.elementAt(index)).intValue();
        }
        return mArray;
    } //-- int[] getInventoryID() 

    /**
    **/
    public int getInventoryIDCount()
    {
        return _inventoryIDList.size();
    } //-- int getInventoryIDCount() 

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
    **/
    public void removeAllInventoryID()
    {
        _inventoryIDList.removeAllElements();
    } //-- void removeAllInventoryID() 

    /**
     * 
     * 
     * @param index
    **/
    public int removeInventoryID(int index)
    {
        java.lang.Object obj = _inventoryIDList.elementAt(index);
        _inventoryIDList.removeElementAt(index);
        return ((Integer)obj).intValue();
    } //-- int removeInventoryID(int) 

    /**
     * 
     * 
     * @param index
     * @param vInventoryID
    **/
    public void setInventoryID(int index, int vInventoryID)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _inventoryIDList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _inventoryIDList.setElementAt(new Integer(vInventoryID), index);
    } //-- void setInventoryID(int, int) 

    /**
     * 
     * 
     * @param inventoryIDArray
    **/
    public void setInventoryID(int[] inventoryIDArray)
    {
        //-- copy array
        _inventoryIDList.removeAllElements();
        for (int i = 0; i < inventoryIDArray.length; i++) {
            _inventoryIDList.addElement(new Integer(inventoryIDArray[i]));
        }
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

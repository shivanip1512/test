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
public class StarsCustSelectionList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _listID;

    /**
     * keeps track of state for field: _listID
    **/
    private boolean _has_listID;

    private java.util.Vector _starsSelectionListEntryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustSelectionList() {
        super();
        _starsSelectionListEntryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsCustSelectionList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsSelectionListEntry
    **/
    public void addStarsSelectionListEntry(StarsSelectionListEntry vStarsSelectionListEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsSelectionListEntryList.addElement(vStarsSelectionListEntry);
    } //-- void addStarsSelectionListEntry(StarsSelectionListEntry) 

    /**
     * 
     * 
     * @param index
     * @param vStarsSelectionListEntry
    **/
    public void addStarsSelectionListEntry(int index, StarsSelectionListEntry vStarsSelectionListEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsSelectionListEntryList.insertElementAt(vStarsSelectionListEntry, index);
    } //-- void addStarsSelectionListEntry(int, StarsSelectionListEntry) 

    /**
    **/
    public void deleteListID()
    {
        this._has_listID= false;
    } //-- void deleteListID() 

    /**
    **/
    public java.util.Enumeration enumerateStarsSelectionListEntry()
    {
        return _starsSelectionListEntryList.elements();
    } //-- java.util.Enumeration enumerateStarsSelectionListEntry() 

    /**
     * Returns the value of field 'listID'.
     * 
     * @return the value of field 'listID'.
    **/
    public int getListID()
    {
        return this._listID;
    } //-- int getListID() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsSelectionListEntry getStarsSelectionListEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsSelectionListEntryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsSelectionListEntry) _starsSelectionListEntryList.elementAt(index);
    } //-- StarsSelectionListEntry getStarsSelectionListEntry(int) 

    /**
    **/
    public StarsSelectionListEntry[] getStarsSelectionListEntry()
    {
        int size = _starsSelectionListEntryList.size();
        StarsSelectionListEntry[] mArray = new StarsSelectionListEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsSelectionListEntry) _starsSelectionListEntryList.elementAt(index);
        }
        return mArray;
    } //-- StarsSelectionListEntry[] getStarsSelectionListEntry() 

    /**
    **/
    public int getStarsSelectionListEntryCount()
    {
        return _starsSelectionListEntryList.size();
    } //-- int getStarsSelectionListEntryCount() 

    /**
    **/
    public boolean hasListID()
    {
        return this._has_listID;
    } //-- boolean hasListID() 

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
    public void removeAllStarsSelectionListEntry()
    {
        _starsSelectionListEntryList.removeAllElements();
    } //-- void removeAllStarsSelectionListEntry() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsSelectionListEntry removeStarsSelectionListEntry(int index)
    {
        java.lang.Object obj = _starsSelectionListEntryList.elementAt(index);
        _starsSelectionListEntryList.removeElementAt(index);
        return (StarsSelectionListEntry) obj;
    } //-- StarsSelectionListEntry removeStarsSelectionListEntry(int) 

    /**
     * Sets the value of field 'listID'.
     * 
     * @param listID the value of field 'listID'.
    **/
    public void setListID(int listID)
    {
        this._listID = listID;
        this._has_listID = true;
    } //-- void setListID(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsSelectionListEntry
    **/
    public void setStarsSelectionListEntry(int index, StarsSelectionListEntry vStarsSelectionListEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsSelectionListEntryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsSelectionListEntryList.setElementAt(vStarsSelectionListEntry, index);
    } //-- void setStarsSelectionListEntry(int, StarsSelectionListEntry) 

    /**
     * 
     * 
     * @param starsSelectionListEntryArray
    **/
    public void setStarsSelectionListEntry(StarsSelectionListEntry[] starsSelectionListEntryArray)
    {
        //-- copy array
        _starsSelectionListEntryList.removeAllElements();
        for (int i = 0; i < starsSelectionListEntryArray.length; i++) {
            _starsSelectionListEntryList.addElement(starsSelectionListEntryArray[i]);
        }
    } //-- void setStarsSelectionListEntry(StarsSelectionListEntry) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsCustSelectionList unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsCustSelectionList) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsCustSelectionList.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsCustSelectionList unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

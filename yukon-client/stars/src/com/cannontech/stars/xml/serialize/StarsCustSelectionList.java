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

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsCustSelectionList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _listEntryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustSelectionList() {
        super();
        _listEntryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsCustSelectionList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vListEntry
    **/
    public void addListEntry(ListEntry vListEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _listEntryList.addElement(vListEntry);
    } //-- void addListEntry(ListEntry) 

    /**
     * 
     * 
     * @param index
     * @param vListEntry
    **/
    public void addListEntry(int index, ListEntry vListEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _listEntryList.insertElementAt(vListEntry, index);
    } //-- void addListEntry(int, ListEntry) 

    /**
    **/
    public java.util.Enumeration enumerateListEntry()
    {
        return _listEntryList.elements();
    } //-- java.util.Enumeration enumerateListEntry() 

    /**
     * 
     * 
     * @param index
    **/
    public ListEntry getListEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _listEntryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ListEntry) _listEntryList.elementAt(index);
    } //-- ListEntry getListEntry(int) 

    /**
    **/
    public ListEntry[] getListEntry()
    {
        int size = _listEntryList.size();
        ListEntry[] mArray = new ListEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ListEntry) _listEntryList.elementAt(index);
        }
        return mArray;
    } //-- ListEntry[] getListEntry() 

    /**
    **/
    public int getListEntryCount()
    {
        return _listEntryList.size();
    } //-- int getListEntryCount() 

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
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
    **/
    public void removeAllListEntry()
    {
        _listEntryList.removeAllElements();
    } //-- void removeAllListEntry() 

    /**
     * 
     * 
     * @param index
    **/
    public ListEntry removeListEntry(int index)
    {
        java.lang.Object obj = _listEntryList.elementAt(index);
        _listEntryList.removeElementAt(index);
        return (ListEntry) obj;
    } //-- ListEntry removeListEntry(int) 

    /**
     * 
     * 
     * @param index
     * @param vListEntry
    **/
    public void setListEntry(int index, ListEntry vListEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _listEntryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _listEntryList.setElementAt(vListEntry, index);
    } //-- void setListEntry(int, ListEntry) 

    /**
     * 
     * 
     * @param listEntryArray
    **/
    public void setListEntry(ListEntry[] listEntryArray)
    {
        //-- copy array
        _listEntryList.removeAllElements();
        for (int i = 0; i < listEntryArray.length; i++) {
            _listEntryList.addElement(listEntryArray[i]);
        }
    } //-- void setListEntry(ListEntry) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

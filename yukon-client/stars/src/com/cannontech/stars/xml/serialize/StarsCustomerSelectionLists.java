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
public class StarsCustomerSelectionLists implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsCustSelectionListList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustomerSelectionLists() {
        super();
        _starsCustSelectionListList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsCustSelectionList
    **/
    public void addStarsCustSelectionList(StarsCustSelectionList vStarsCustSelectionList)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCustSelectionListList.addElement(vStarsCustSelectionList);
    } //-- void addStarsCustSelectionList(StarsCustSelectionList) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCustSelectionList
    **/
    public void addStarsCustSelectionList(int index, StarsCustSelectionList vStarsCustSelectionList)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCustSelectionListList.insertElementAt(vStarsCustSelectionList, index);
    } //-- void addStarsCustSelectionList(int, StarsCustSelectionList) 

    /**
    **/
    public java.util.Enumeration enumerateStarsCustSelectionList()
    {
        return _starsCustSelectionListList.elements();
    } //-- java.util.Enumeration enumerateStarsCustSelectionList() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCustSelectionList getStarsCustSelectionList(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustSelectionListList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsCustSelectionList) _starsCustSelectionListList.elementAt(index);
    } //-- StarsCustSelectionList getStarsCustSelectionList(int) 

    /**
    **/
    public StarsCustSelectionList[] getStarsCustSelectionList()
    {
        int size = _starsCustSelectionListList.size();
        StarsCustSelectionList[] mArray = new StarsCustSelectionList[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsCustSelectionList) _starsCustSelectionListList.elementAt(index);
        }
        return mArray;
    } //-- StarsCustSelectionList[] getStarsCustSelectionList() 

    /**
    **/
    public int getStarsCustSelectionListCount()
    {
        return _starsCustSelectionListList.size();
    } //-- int getStarsCustSelectionListCount() 

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
    public void removeAllStarsCustSelectionList()
    {
        _starsCustSelectionListList.removeAllElements();
    } //-- void removeAllStarsCustSelectionList() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCustSelectionList removeStarsCustSelectionList(int index)
    {
        java.lang.Object obj = _starsCustSelectionListList.elementAt(index);
        _starsCustSelectionListList.removeElementAt(index);
        return (StarsCustSelectionList) obj;
    } //-- StarsCustSelectionList removeStarsCustSelectionList(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCustSelectionList
    **/
    public void setStarsCustSelectionList(int index, StarsCustSelectionList vStarsCustSelectionList)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustSelectionListList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCustSelectionListList.setElementAt(vStarsCustSelectionList, index);
    } //-- void setStarsCustSelectionList(int, StarsCustSelectionList) 

    /**
     * 
     * 
     * @param starsCustSelectionListArray
    **/
    public void setStarsCustSelectionList(StarsCustSelectionList[] starsCustSelectionListArray)
    {
        //-- copy array
        _starsCustSelectionListList.removeAllElements();
        for (int i = 0; i < starsCustSelectionListArray.length; i++) {
            _starsCustSelectionListList.addElement(starsCustSelectionListArray[i]);
        }
    } //-- void setStarsCustSelectionList(StarsCustSelectionList) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

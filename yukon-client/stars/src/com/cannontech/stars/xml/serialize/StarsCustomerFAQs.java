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
public class StarsCustomerFAQs implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsCustomerFAQGroupList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustomerFAQs() {
        super();
        _starsCustomerFAQGroupList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerFAQs()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsCustomerFAQGroup
    **/
    public void addStarsCustomerFAQGroup(StarsCustomerFAQGroup vStarsCustomerFAQGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCustomerFAQGroupList.addElement(vStarsCustomerFAQGroup);
    } //-- void addStarsCustomerFAQGroup(StarsCustomerFAQGroup) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCustomerFAQGroup
    **/
    public void addStarsCustomerFAQGroup(int index, StarsCustomerFAQGroup vStarsCustomerFAQGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCustomerFAQGroupList.insertElementAt(vStarsCustomerFAQGroup, index);
    } //-- void addStarsCustomerFAQGroup(int, StarsCustomerFAQGroup) 

    /**
    **/
    public java.util.Enumeration enumerateStarsCustomerFAQGroup()
    {
        return _starsCustomerFAQGroupList.elements();
    } //-- java.util.Enumeration enumerateStarsCustomerFAQGroup() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCustomerFAQGroup getStarsCustomerFAQGroup(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustomerFAQGroupList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsCustomerFAQGroup) _starsCustomerFAQGroupList.elementAt(index);
    } //-- StarsCustomerFAQGroup getStarsCustomerFAQGroup(int) 

    /**
    **/
    public StarsCustomerFAQGroup[] getStarsCustomerFAQGroup()
    {
        int size = _starsCustomerFAQGroupList.size();
        StarsCustomerFAQGroup[] mArray = new StarsCustomerFAQGroup[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsCustomerFAQGroup) _starsCustomerFAQGroupList.elementAt(index);
        }
        return mArray;
    } //-- StarsCustomerFAQGroup[] getStarsCustomerFAQGroup() 

    /**
    **/
    public int getStarsCustomerFAQGroupCount()
    {
        return _starsCustomerFAQGroupList.size();
    } //-- int getStarsCustomerFAQGroupCount() 

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
    public void removeAllStarsCustomerFAQGroup()
    {
        _starsCustomerFAQGroupList.removeAllElements();
    } //-- void removeAllStarsCustomerFAQGroup() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCustomerFAQGroup removeStarsCustomerFAQGroup(int index)
    {
        java.lang.Object obj = _starsCustomerFAQGroupList.elementAt(index);
        _starsCustomerFAQGroupList.removeElementAt(index);
        return (StarsCustomerFAQGroup) obj;
    } //-- StarsCustomerFAQGroup removeStarsCustomerFAQGroup(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCustomerFAQGroup
    **/
    public void setStarsCustomerFAQGroup(int index, StarsCustomerFAQGroup vStarsCustomerFAQGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustomerFAQGroupList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCustomerFAQGroupList.setElementAt(vStarsCustomerFAQGroup, index);
    } //-- void setStarsCustomerFAQGroup(int, StarsCustomerFAQGroup) 

    /**
     * 
     * 
     * @param starsCustomerFAQGroupArray
    **/
    public void setStarsCustomerFAQGroup(StarsCustomerFAQGroup[] starsCustomerFAQGroupArray)
    {
        //-- copy array
        _starsCustomerFAQGroupList.removeAllElements();
        for (int i = 0; i < starsCustomerFAQGroupArray.length; i++) {
            _starsCustomerFAQGroupList.addElement(starsCustomerFAQGroupArray[i]);
        }
    } //-- void setStarsCustomerFAQGroup(StarsCustomerFAQGroup) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsCustomerFAQs unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsCustomerFAQs) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsCustomerFAQs.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerFAQs unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

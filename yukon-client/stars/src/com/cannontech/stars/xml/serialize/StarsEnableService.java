/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsEnableService.java,v 1.2 2002/07/30 22:02:09 zyao Exp $
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
 * Enable programs of a customer account
 * 
 * @version $Revision: 1.2 $ $Date: 2002/07/30 22:02:09 $
**/
public class StarsEnableService implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Date _enableDateTime;

    private java.util.Vector _serialNumberList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsEnableService() {
        super();
        _serialNumberList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsEnableService()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vSerialNumber
    **/
    public void addSerialNumber(java.lang.String vSerialNumber)
        throws java.lang.IndexOutOfBoundsException
    {
        _serialNumberList.addElement(vSerialNumber);
    } //-- void addSerialNumber(java.lang.String) 

    /**
     * 
     * 
     * @param index
     * @param vSerialNumber
    **/
    public void addSerialNumber(int index, java.lang.String vSerialNumber)
        throws java.lang.IndexOutOfBoundsException
    {
        _serialNumberList.insertElementAt(vSerialNumber, index);
    } //-- void addSerialNumber(int, java.lang.String) 

    /**
    **/
    public java.util.Enumeration enumerateSerialNumber()
    {
        return _serialNumberList.elements();
    } //-- java.util.Enumeration enumerateSerialNumber() 

    /**
     * Returns the value of field 'enableDateTime'.
     * 
     * @return the value of field 'enableDateTime'.
    **/
    public java.util.Date getEnableDateTime()
    {
        return this._enableDateTime;
    } //-- java.util.Date getEnableDateTime() 

    /**
     * 
     * 
     * @param index
    **/
    public java.lang.String getSerialNumber(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _serialNumberList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_serialNumberList.elementAt(index);
    } //-- java.lang.String getSerialNumber(int) 

    /**
    **/
    public java.lang.String[] getSerialNumber()
    {
        int size = _serialNumberList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_serialNumberList.elementAt(index);
        }
        return mArray;
    } //-- java.lang.String[] getSerialNumber() 

    /**
    **/
    public int getSerialNumberCount()
    {
        return _serialNumberList.size();
    } //-- int getSerialNumberCount() 

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
    public void removeAllSerialNumber()
    {
        _serialNumberList.removeAllElements();
    } //-- void removeAllSerialNumber() 

    /**
     * 
     * 
     * @param index
    **/
    public java.lang.String removeSerialNumber(int index)
    {
        java.lang.Object obj = _serialNumberList.elementAt(index);
        _serialNumberList.removeElementAt(index);
        return (String)obj;
    } //-- java.lang.String removeSerialNumber(int) 

    /**
     * Sets the value of field 'enableDateTime'.
     * 
     * @param enableDateTime the value of field 'enableDateTime'.
    **/
    public void setEnableDateTime(java.util.Date enableDateTime)
    {
        this._enableDateTime = enableDateTime;
    } //-- void setEnableDateTime(java.util.Date) 

    /**
     * 
     * 
     * @param index
     * @param vSerialNumber
    **/
    public void setSerialNumber(int index, java.lang.String vSerialNumber)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _serialNumberList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _serialNumberList.setElementAt(vSerialNumber, index);
    } //-- void setSerialNumber(int, java.lang.String) 

    /**
     * 
     * 
     * @param serialNumberArray
    **/
    public void setSerialNumber(java.lang.String[] serialNumberArray)
    {
        //-- copy array
        _serialNumberList.removeAllElements();
        for (int i = 0; i < serialNumberArray.length; i++) {
            _serialNumberList.addElement(serialNumberArray[i]);
        }
    } //-- void setSerialNumber(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsEnableService unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsEnableService) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsEnableService.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsEnableService unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

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
public class StarsGetServiceRequestHistoryResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsServiceRequestHistoryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetServiceRequestHistoryResponse() {
        super();
        _starsServiceRequestHistoryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistoryResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsServiceRequestHistory
    **/
    public void addStarsServiceRequestHistory(StarsServiceRequestHistory vStarsServiceRequestHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsServiceRequestHistoryList.addElement(vStarsServiceRequestHistory);
    } //-- void addStarsServiceRequestHistory(StarsServiceRequestHistory) 

    /**
     * 
     * 
     * @param index
     * @param vStarsServiceRequestHistory
    **/
    public void addStarsServiceRequestHistory(int index, StarsServiceRequestHistory vStarsServiceRequestHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsServiceRequestHistoryList.insertElementAt(vStarsServiceRequestHistory, index);
    } //-- void addStarsServiceRequestHistory(int, StarsServiceRequestHistory) 

    /**
    **/
    public java.util.Enumeration enumerateStarsServiceRequestHistory()
    {
        return _starsServiceRequestHistoryList.elements();
    } //-- java.util.Enumeration enumerateStarsServiceRequestHistory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsServiceRequestHistory getStarsServiceRequestHistory(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsServiceRequestHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsServiceRequestHistory) _starsServiceRequestHistoryList.elementAt(index);
    } //-- StarsServiceRequestHistory getStarsServiceRequestHistory(int) 

    /**
    **/
    public StarsServiceRequestHistory[] getStarsServiceRequestHistory()
    {
        int size = _starsServiceRequestHistoryList.size();
        StarsServiceRequestHistory[] mArray = new StarsServiceRequestHistory[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsServiceRequestHistory) _starsServiceRequestHistoryList.elementAt(index);
        }
        return mArray;
    } //-- StarsServiceRequestHistory[] getStarsServiceRequestHistory() 

    /**
    **/
    public int getStarsServiceRequestHistoryCount()
    {
        return _starsServiceRequestHistoryList.size();
    } //-- int getStarsServiceRequestHistoryCount() 

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
    public void removeAllStarsServiceRequestHistory()
    {
        _starsServiceRequestHistoryList.removeAllElements();
    } //-- void removeAllStarsServiceRequestHistory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsServiceRequestHistory removeStarsServiceRequestHistory(int index)
    {
        java.lang.Object obj = _starsServiceRequestHistoryList.elementAt(index);
        _starsServiceRequestHistoryList.removeElementAt(index);
        return (StarsServiceRequestHistory) obj;
    } //-- StarsServiceRequestHistory removeStarsServiceRequestHistory(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsServiceRequestHistory
    **/
    public void setStarsServiceRequestHistory(int index, StarsServiceRequestHistory vStarsServiceRequestHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsServiceRequestHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsServiceRequestHistoryList.setElementAt(vStarsServiceRequestHistory, index);
    } //-- void setStarsServiceRequestHistory(int, StarsServiceRequestHistory) 

    /**
     * 
     * 
     * @param starsServiceRequestHistoryArray
    **/
    public void setStarsServiceRequestHistory(StarsServiceRequestHistory[] starsServiceRequestHistoryArray)
    {
        //-- copy array
        _starsServiceRequestHistoryList.removeAllElements();
        for (int i = 0; i < starsServiceRequestHistoryArray.length; i++) {
            _starsServiceRequestHistoryList.addElement(starsServiceRequestHistoryArray[i]);
        }
    } //-- void setStarsServiceRequestHistory(StarsServiceRequestHistory) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistoryResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistoryResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistoryResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistoryResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

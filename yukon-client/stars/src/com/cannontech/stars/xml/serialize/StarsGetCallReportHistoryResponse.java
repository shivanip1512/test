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
public class StarsGetCallReportHistoryResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsCallReportHistoryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetCallReportHistoryResponse() {
        super();
        _starsCallReportHistoryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsGetCallReportHistoryResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsCallReportHistory
    **/
    public void addStarsCallReportHistory(StarsCallReportHistory vStarsCallReportHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCallReportHistoryList.addElement(vStarsCallReportHistory);
    } //-- void addStarsCallReportHistory(StarsCallReportHistory) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCallReportHistory
    **/
    public void addStarsCallReportHistory(int index, StarsCallReportHistory vStarsCallReportHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCallReportHistoryList.insertElementAt(vStarsCallReportHistory, index);
    } //-- void addStarsCallReportHistory(int, StarsCallReportHistory) 

    /**
    **/
    public java.util.Enumeration enumerateStarsCallReportHistory()
    {
        return _starsCallReportHistoryList.elements();
    } //-- java.util.Enumeration enumerateStarsCallReportHistory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCallReportHistory getStarsCallReportHistory(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCallReportHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsCallReportHistory) _starsCallReportHistoryList.elementAt(index);
    } //-- StarsCallReportHistory getStarsCallReportHistory(int) 

    /**
    **/
    public StarsCallReportHistory[] getStarsCallReportHistory()
    {
        int size = _starsCallReportHistoryList.size();
        StarsCallReportHistory[] mArray = new StarsCallReportHistory[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsCallReportHistory) _starsCallReportHistoryList.elementAt(index);
        }
        return mArray;
    } //-- StarsCallReportHistory[] getStarsCallReportHistory() 

    /**
    **/
    public int getStarsCallReportHistoryCount()
    {
        return _starsCallReportHistoryList.size();
    } //-- int getStarsCallReportHistoryCount() 

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
    public void removeAllStarsCallReportHistory()
    {
        _starsCallReportHistoryList.removeAllElements();
    } //-- void removeAllStarsCallReportHistory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCallReportHistory removeStarsCallReportHistory(int index)
    {
        java.lang.Object obj = _starsCallReportHistoryList.elementAt(index);
        _starsCallReportHistoryList.removeElementAt(index);
        return (StarsCallReportHistory) obj;
    } //-- StarsCallReportHistory removeStarsCallReportHistory(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCallReportHistory
    **/
    public void setStarsCallReportHistory(int index, StarsCallReportHistory vStarsCallReportHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCallReportHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCallReportHistoryList.setElementAt(vStarsCallReportHistory, index);
    } //-- void setStarsCallReportHistory(int, StarsCallReportHistory) 

    /**
     * 
     * 
     * @param starsCallReportHistoryArray
    **/
    public void setStarsCallReportHistory(StarsCallReportHistory[] starsCallReportHistoryArray)
    {
        //-- copy array
        _starsCallReportHistoryList.removeAllElements();
        for (int i = 0; i < starsCallReportHistoryArray.length; i++) {
            _starsCallReportHistoryList.addElement(starsCallReportHistoryArray[i]);
        }
    } //-- void setStarsCallReportHistory(StarsCallReportHistory) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsGetCallReportHistoryResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsGetCallReportHistoryResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsGetCallReportHistoryResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsGetCallReportHistoryResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

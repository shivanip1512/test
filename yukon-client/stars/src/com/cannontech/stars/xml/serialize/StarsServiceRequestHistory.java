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
public class StarsServiceRequestHistory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsServiceRequestList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsServiceRequestHistory() {
        super();
        _starsServiceRequestList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsServiceRequestHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsServiceRequest
    **/
    public void addStarsServiceRequest(StarsServiceRequest vStarsServiceRequest)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsServiceRequestList.addElement(vStarsServiceRequest);
    } //-- void addStarsServiceRequest(StarsServiceRequest) 

    /**
     * 
     * 
     * @param index
     * @param vStarsServiceRequest
    **/
    public void addStarsServiceRequest(int index, StarsServiceRequest vStarsServiceRequest)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsServiceRequestList.insertElementAt(vStarsServiceRequest, index);
    } //-- void addStarsServiceRequest(int, StarsServiceRequest) 

    /**
    **/
    public java.util.Enumeration enumerateStarsServiceRequest()
    {
        return _starsServiceRequestList.elements();
    } //-- java.util.Enumeration enumerateStarsServiceRequest() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsServiceRequest getStarsServiceRequest(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsServiceRequestList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsServiceRequest) _starsServiceRequestList.elementAt(index);
    } //-- StarsServiceRequest getStarsServiceRequest(int) 

    /**
    **/
    public StarsServiceRequest[] getStarsServiceRequest()
    {
        int size = _starsServiceRequestList.size();
        StarsServiceRequest[] mArray = new StarsServiceRequest[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsServiceRequest) _starsServiceRequestList.elementAt(index);
        }
        return mArray;
    } //-- StarsServiceRequest[] getStarsServiceRequest() 

    /**
    **/
    public int getStarsServiceRequestCount()
    {
        return _starsServiceRequestList.size();
    } //-- int getStarsServiceRequestCount() 

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
    public void removeAllStarsServiceRequest()
    {
        _starsServiceRequestList.removeAllElements();
    } //-- void removeAllStarsServiceRequest() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsServiceRequest removeStarsServiceRequest(int index)
    {
        java.lang.Object obj = _starsServiceRequestList.elementAt(index);
        _starsServiceRequestList.removeElementAt(index);
        return (StarsServiceRequest) obj;
    } //-- StarsServiceRequest removeStarsServiceRequest(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsServiceRequest
    **/
    public void setStarsServiceRequest(int index, StarsServiceRequest vStarsServiceRequest)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsServiceRequestList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsServiceRequestList.setElementAt(vStarsServiceRequest, index);
    } //-- void setStarsServiceRequest(int, StarsServiceRequest) 

    /**
     * 
     * 
     * @param starsServiceRequestArray
    **/
    public void setStarsServiceRequest(StarsServiceRequest[] starsServiceRequestArray)
    {
        //-- copy array
        _starsServiceRequestList.removeAllElements();
        for (int i = 0; i < starsServiceRequestArray.length; i++) {
            _starsServiceRequestList.addElement(starsServiceRequestArray[i]);
        }
    } //-- void setStarsServiceRequest(StarsServiceRequest) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsServiceRequestHistory unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsServiceRequestHistory) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsServiceRequestHistory.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsServiceRequestHistory unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

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
public class StarsLMHardwareHistory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _LMHardwareEventList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMHardwareHistory() {
        super();
        _LMHardwareEventList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsLMHardwareHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vLMHardwareEvent
    **/
    public void addLMHardwareEvent(LMHardwareEvent vLMHardwareEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _LMHardwareEventList.addElement(vLMHardwareEvent);
    } //-- void addLMHardwareEvent(LMHardwareEvent) 

    /**
     * 
     * 
     * @param index
     * @param vLMHardwareEvent
    **/
    public void addLMHardwareEvent(int index, LMHardwareEvent vLMHardwareEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _LMHardwareEventList.insertElementAt(vLMHardwareEvent, index);
    } //-- void addLMHardwareEvent(int, LMHardwareEvent) 

    /**
    **/
    public java.util.Enumeration enumerateLMHardwareEvent()
    {
        return _LMHardwareEventList.elements();
    } //-- java.util.Enumeration enumerateLMHardwareEvent() 

    /**
     * 
     * 
     * @param index
    **/
    public LMHardwareEvent getLMHardwareEvent(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _LMHardwareEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (LMHardwareEvent) _LMHardwareEventList.elementAt(index);
    } //-- LMHardwareEvent getLMHardwareEvent(int) 

    /**
    **/
    public LMHardwareEvent[] getLMHardwareEvent()
    {
        int size = _LMHardwareEventList.size();
        LMHardwareEvent[] mArray = new LMHardwareEvent[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (LMHardwareEvent) _LMHardwareEventList.elementAt(index);
        }
        return mArray;
    } //-- LMHardwareEvent[] getLMHardwareEvent() 

    /**
    **/
    public int getLMHardwareEventCount()
    {
        return _LMHardwareEventList.size();
    } //-- int getLMHardwareEventCount() 

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
    public void removeAllLMHardwareEvent()
    {
        _LMHardwareEventList.removeAllElements();
    } //-- void removeAllLMHardwareEvent() 

    /**
     * 
     * 
     * @param index
    **/
    public LMHardwareEvent removeLMHardwareEvent(int index)
    {
        java.lang.Object obj = _LMHardwareEventList.elementAt(index);
        _LMHardwareEventList.removeElementAt(index);
        return (LMHardwareEvent) obj;
    } //-- LMHardwareEvent removeLMHardwareEvent(int) 

    /**
     * 
     * 
     * @param index
     * @param vLMHardwareEvent
    **/
    public void setLMHardwareEvent(int index, LMHardwareEvent vLMHardwareEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _LMHardwareEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _LMHardwareEventList.setElementAt(vLMHardwareEvent, index);
    } //-- void setLMHardwareEvent(int, LMHardwareEvent) 

    /**
     * 
     * 
     * @param LMHardwareEventArray
    **/
    public void setLMHardwareEvent(LMHardwareEvent[] LMHardwareEventArray)
    {
        //-- copy array
        _LMHardwareEventList.removeAllElements();
        for (int i = 0; i < LMHardwareEventArray.length; i++) {
            _LMHardwareEventList.addElement(LMHardwareEventArray[i]);
        }
    } //-- void setLMHardwareEvent(LMHardwareEvent) 

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

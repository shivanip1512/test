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
public class StarsLMProgramHistory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _LMProgramEventList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMProgramHistory() {
        super();
        _LMProgramEventList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsLMProgramHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vLMProgramEvent
    **/
    public void addLMProgramEvent(LMProgramEvent vLMProgramEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _LMProgramEventList.addElement(vLMProgramEvent);
    } //-- void addLMProgramEvent(LMProgramEvent) 

    /**
     * 
     * 
     * @param index
     * @param vLMProgramEvent
    **/
    public void addLMProgramEvent(int index, LMProgramEvent vLMProgramEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _LMProgramEventList.insertElementAt(vLMProgramEvent, index);
    } //-- void addLMProgramEvent(int, LMProgramEvent) 

    /**
    **/
    public java.util.Enumeration enumerateLMProgramEvent()
    {
        return _LMProgramEventList.elements();
    } //-- java.util.Enumeration enumerateLMProgramEvent() 

    /**
     * 
     * 
     * @param index
    **/
    public LMProgramEvent getLMProgramEvent(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _LMProgramEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (LMProgramEvent) _LMProgramEventList.elementAt(index);
    } //-- LMProgramEvent getLMProgramEvent(int) 

    /**
    **/
    public LMProgramEvent[] getLMProgramEvent()
    {
        int size = _LMProgramEventList.size();
        LMProgramEvent[] mArray = new LMProgramEvent[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (LMProgramEvent) _LMProgramEventList.elementAt(index);
        }
        return mArray;
    } //-- LMProgramEvent[] getLMProgramEvent() 

    /**
    **/
    public int getLMProgramEventCount()
    {
        return _LMProgramEventList.size();
    } //-- int getLMProgramEventCount() 

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
    public void removeAllLMProgramEvent()
    {
        _LMProgramEventList.removeAllElements();
    } //-- void removeAllLMProgramEvent() 

    /**
     * 
     * 
     * @param index
    **/
    public LMProgramEvent removeLMProgramEvent(int index)
    {
        java.lang.Object obj = _LMProgramEventList.elementAt(index);
        _LMProgramEventList.removeElementAt(index);
        return (LMProgramEvent) obj;
    } //-- LMProgramEvent removeLMProgramEvent(int) 

    /**
     * 
     * 
     * @param index
     * @param vLMProgramEvent
    **/
    public void setLMProgramEvent(int index, LMProgramEvent vLMProgramEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _LMProgramEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _LMProgramEventList.setElementAt(vLMProgramEvent, index);
    } //-- void setLMProgramEvent(int, LMProgramEvent) 

    /**
     * 
     * 
     * @param LMProgramEventArray
    **/
    public void setLMProgramEvent(LMProgramEvent[] LMProgramEventArray)
    {
        //-- copy array
        _LMProgramEventList.removeAllElements();
        for (int i = 0; i < LMProgramEventArray.length; i++) {
            _LMProgramEventList.addElement(LMProgramEventArray[i]);
        }
    } //-- void setLMProgramEvent(LMProgramEvent) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsLMProgramHistory unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsLMProgramHistory) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsLMProgramHistory.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsLMProgramHistory unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

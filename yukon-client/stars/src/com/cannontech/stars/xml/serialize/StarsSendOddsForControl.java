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
public class StarsSendOddsForControl implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsEnrLMProgramList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSendOddsForControl() {
        super();
        _starsEnrLMProgramList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSendOddsForControl()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsEnrLMProgram
    **/
    public void addStarsEnrLMProgram(StarsEnrLMProgram vStarsEnrLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsEnrLMProgramList.addElement(vStarsEnrLMProgram);
    } //-- void addStarsEnrLMProgram(StarsEnrLMProgram) 

    /**
     * 
     * 
     * @param index
     * @param vStarsEnrLMProgram
    **/
    public void addStarsEnrLMProgram(int index, StarsEnrLMProgram vStarsEnrLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsEnrLMProgramList.insertElementAt(vStarsEnrLMProgram, index);
    } //-- void addStarsEnrLMProgram(int, StarsEnrLMProgram) 

    /**
    **/
    public java.util.Enumeration enumerateStarsEnrLMProgram()
    {
        return _starsEnrLMProgramList.elements();
    } //-- java.util.Enumeration enumerateStarsEnrLMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsEnrLMProgram getStarsEnrLMProgram(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsEnrLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsEnrLMProgram) _starsEnrLMProgramList.elementAt(index);
    } //-- StarsEnrLMProgram getStarsEnrLMProgram(int) 

    /**
    **/
    public StarsEnrLMProgram[] getStarsEnrLMProgram()
    {
        int size = _starsEnrLMProgramList.size();
        StarsEnrLMProgram[] mArray = new StarsEnrLMProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsEnrLMProgram) _starsEnrLMProgramList.elementAt(index);
        }
        return mArray;
    } //-- StarsEnrLMProgram[] getStarsEnrLMProgram() 

    /**
    **/
    public int getStarsEnrLMProgramCount()
    {
        return _starsEnrLMProgramList.size();
    } //-- int getStarsEnrLMProgramCount() 

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
    public void removeAllStarsEnrLMProgram()
    {
        _starsEnrLMProgramList.removeAllElements();
    } //-- void removeAllStarsEnrLMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsEnrLMProgram removeStarsEnrLMProgram(int index)
    {
        java.lang.Object obj = _starsEnrLMProgramList.elementAt(index);
        _starsEnrLMProgramList.removeElementAt(index);
        return (StarsEnrLMProgram) obj;
    } //-- StarsEnrLMProgram removeStarsEnrLMProgram(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsEnrLMProgram
    **/
    public void setStarsEnrLMProgram(int index, StarsEnrLMProgram vStarsEnrLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsEnrLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsEnrLMProgramList.setElementAt(vStarsEnrLMProgram, index);
    } //-- void setStarsEnrLMProgram(int, StarsEnrLMProgram) 

    /**
     * 
     * 
     * @param starsEnrLMProgramArray
    **/
    public void setStarsEnrLMProgram(StarsEnrLMProgram[] starsEnrLMProgramArray)
    {
        //-- copy array
        _starsEnrLMProgramList.removeAllElements();
        for (int i = 0; i < starsEnrLMProgramArray.length; i++) {
            _starsEnrLMProgramList.addElement(starsEnrLMProgramArray[i]);
        }
    } //-- void setStarsEnrLMProgram(StarsEnrLMProgram) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSendOddsForControl unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSendOddsForControl) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSendOddsForControl.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSendOddsForControl unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsLMPrograms.java,v 1.59 2003/12/18 16:43:35 zyao Exp $
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
 * @version $Revision: 1.59 $ $Date: 2003/12/18 16:43:35 $
**/
public class StarsLMPrograms implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsLMProgramList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMPrograms() {
        super();
        _starsLMProgramList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsLMPrograms()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsLMProgram
    **/
    public void addStarsLMProgram(StarsLMProgram vStarsLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMProgramList.addElement(vStarsLMProgram);
    } //-- void addStarsLMProgram(StarsLMProgram) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMProgram
    **/
    public void addStarsLMProgram(int index, StarsLMProgram vStarsLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMProgramList.insertElementAt(vStarsLMProgram, index);
    } //-- void addStarsLMProgram(int, StarsLMProgram) 

    /**
    **/
    public java.util.Enumeration enumerateStarsLMProgram()
    {
        return _starsLMProgramList.elements();
    } //-- java.util.Enumeration enumerateStarsLMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMProgram getStarsLMProgram(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsLMProgram) _starsLMProgramList.elementAt(index);
    } //-- StarsLMProgram getStarsLMProgram(int) 

    /**
    **/
    public StarsLMProgram[] getStarsLMProgram()
    {
        int size = _starsLMProgramList.size();
        StarsLMProgram[] mArray = new StarsLMProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsLMProgram) _starsLMProgramList.elementAt(index);
        }
        return mArray;
    } //-- StarsLMProgram[] getStarsLMProgram() 

    /**
    **/
    public int getStarsLMProgramCount()
    {
        return _starsLMProgramList.size();
    } //-- int getStarsLMProgramCount() 

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
    public void removeAllStarsLMProgram()
    {
        _starsLMProgramList.removeAllElements();
    } //-- void removeAllStarsLMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMProgram removeStarsLMProgram(int index)
    {
        java.lang.Object obj = _starsLMProgramList.elementAt(index);
        _starsLMProgramList.removeElementAt(index);
        return (StarsLMProgram) obj;
    } //-- StarsLMProgram removeStarsLMProgram(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMProgram
    **/
    public void setStarsLMProgram(int index, StarsLMProgram vStarsLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsLMProgramList.setElementAt(vStarsLMProgram, index);
    } //-- void setStarsLMProgram(int, StarsLMProgram) 

    /**
     * 
     * 
     * @param starsLMProgramArray
    **/
    public void setStarsLMProgram(StarsLMProgram[] starsLMProgramArray)
    {
        //-- copy array
        _starsLMProgramList.removeAllElements();
        for (int i = 0; i < starsLMProgramArray.length; i++) {
            _starsLMProgramList.addElement(starsLMProgramArray[i]);
        }
    } //-- void setStarsLMProgram(StarsLMProgram) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsLMPrograms unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsLMPrograms) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsLMPrograms.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsLMPrograms unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

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
public class StarsSULMPrograms implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _SULMProgramList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSULMPrograms() {
        super();
        _SULMProgramList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSULMPrograms()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vSULMProgram
    **/
    public void addSULMProgram(SULMProgram vSULMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _SULMProgramList.addElement(vSULMProgram);
    } //-- void addSULMProgram(SULMProgram) 

    /**
     * 
     * 
     * @param index
     * @param vSULMProgram
    **/
    public void addSULMProgram(int index, SULMProgram vSULMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _SULMProgramList.insertElementAt(vSULMProgram, index);
    } //-- void addSULMProgram(int, SULMProgram) 

    /**
    **/
    public java.util.Enumeration enumerateSULMProgram()
    {
        return _SULMProgramList.elements();
    } //-- java.util.Enumeration enumerateSULMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public SULMProgram getSULMProgram(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _SULMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (SULMProgram) _SULMProgramList.elementAt(index);
    } //-- SULMProgram getSULMProgram(int) 

    /**
    **/
    public SULMProgram[] getSULMProgram()
    {
        int size = _SULMProgramList.size();
        SULMProgram[] mArray = new SULMProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (SULMProgram) _SULMProgramList.elementAt(index);
        }
        return mArray;
    } //-- SULMProgram[] getSULMProgram() 

    /**
    **/
    public int getSULMProgramCount()
    {
        return _SULMProgramList.size();
    } //-- int getSULMProgramCount() 

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
    public void removeAllSULMProgram()
    {
        _SULMProgramList.removeAllElements();
    } //-- void removeAllSULMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public SULMProgram removeSULMProgram(int index)
    {
        java.lang.Object obj = _SULMProgramList.elementAt(index);
        _SULMProgramList.removeElementAt(index);
        return (SULMProgram) obj;
    } //-- SULMProgram removeSULMProgram(int) 

    /**
     * 
     * 
     * @param index
     * @param vSULMProgram
    **/
    public void setSULMProgram(int index, SULMProgram vSULMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _SULMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _SULMProgramList.setElementAt(vSULMProgram, index);
    } //-- void setSULMProgram(int, SULMProgram) 

    /**
     * 
     * 
     * @param SULMProgramArray
    **/
    public void setSULMProgram(SULMProgram[] SULMProgramArray)
    {
        //-- copy array
        _SULMProgramList.removeAllElements();
        for (int i = 0; i < SULMProgramArray.length; i++) {
            _SULMProgramList.addElement(SULMProgramArray[i]);
        }
    } //-- void setSULMProgram(SULMProgram) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSULMPrograms unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSULMPrograms) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSULMPrograms.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSULMPrograms unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsUpdateLMPrograms.java,v 1.85 2004/07/28 22:59:09 yao Exp $
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
 * Update the LM programs for a customer account
 * 
 * @version $Revision: 1.85 $ $Date: 2004/07/28 22:59:09 $
**/
public class StarsUpdateLMPrograms implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _LMProgramList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateLMPrograms() {
        super();
        _LMProgramList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateLMPrograms()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vLMProgram
    **/
    public void addLMProgram(LMProgram vLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _LMProgramList.addElement(vLMProgram);
    } //-- void addLMProgram(LMProgram) 

    /**
     * 
     * 
     * @param index
     * @param vLMProgram
    **/
    public void addLMProgram(int index, LMProgram vLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _LMProgramList.insertElementAt(vLMProgram, index);
    } //-- void addLMProgram(int, LMProgram) 

    /**
    **/
    public java.util.Enumeration enumerateLMProgram()
    {
        return _LMProgramList.elements();
    } //-- java.util.Enumeration enumerateLMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public LMProgram getLMProgram(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _LMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (LMProgram) _LMProgramList.elementAt(index);
    } //-- LMProgram getLMProgram(int) 

    /**
    **/
    public LMProgram[] getLMProgram()
    {
        int size = _LMProgramList.size();
        LMProgram[] mArray = new LMProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (LMProgram) _LMProgramList.elementAt(index);
        }
        return mArray;
    } //-- LMProgram[] getLMProgram() 

    /**
    **/
    public int getLMProgramCount()
    {
        return _LMProgramList.size();
    } //-- int getLMProgramCount() 

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
    public void removeAllLMProgram()
    {
        _LMProgramList.removeAllElements();
    } //-- void removeAllLMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public LMProgram removeLMProgram(int index)
    {
        java.lang.Object obj = _LMProgramList.elementAt(index);
        _LMProgramList.removeElementAt(index);
        return (LMProgram) obj;
    } //-- LMProgram removeLMProgram(int) 

    /**
     * 
     * 
     * @param index
     * @param vLMProgram
    **/
    public void setLMProgram(int index, LMProgram vLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _LMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _LMProgramList.setElementAt(vLMProgram, index);
    } //-- void setLMProgram(int, LMProgram) 

    /**
     * 
     * 
     * @param LMProgramArray
    **/
    public void setLMProgram(LMProgram[] LMProgramArray)
    {
        //-- copy array
        _LMProgramList.removeAllElements();
        for (int i = 0; i < LMProgramArray.length; i++) {
            _LMProgramList.addElement(LMProgramArray[i]);
        }
    } //-- void setLMProgram(LMProgram) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsUpdateLMPrograms unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsUpdateLMPrograms) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsUpdateLMPrograms.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateLMPrograms unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

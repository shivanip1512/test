/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSavedThermostatSchedules.java,v 1.14 2004/10/06 20:59:25 zyao Exp $
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
 * @version $Revision: 1.14 $ $Date: 2004/10/06 20:59:25 $
**/
public class StarsSavedThermostatSchedules implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsThermostatProgramList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSavedThermostatSchedules() {
        super();
        _starsThermostatProgramList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSavedThermostatSchedules()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsThermostatProgram
    **/
    public void addStarsThermostatProgram(StarsThermostatProgram vStarsThermostatProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsThermostatProgramList.addElement(vStarsThermostatProgram);
    } //-- void addStarsThermostatProgram(StarsThermostatProgram) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatProgram
    **/
    public void addStarsThermostatProgram(int index, StarsThermostatProgram vStarsThermostatProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsThermostatProgramList.insertElementAt(vStarsThermostatProgram, index);
    } //-- void addStarsThermostatProgram(int, StarsThermostatProgram) 

    /**
    **/
    public java.util.Enumeration enumerateStarsThermostatProgram()
    {
        return _starsThermostatProgramList.elements();
    } //-- java.util.Enumeration enumerateStarsThermostatProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatProgram getStarsThermostatProgram(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsThermostatProgram) _starsThermostatProgramList.elementAt(index);
    } //-- StarsThermostatProgram getStarsThermostatProgram(int) 

    /**
    **/
    public StarsThermostatProgram[] getStarsThermostatProgram()
    {
        int size = _starsThermostatProgramList.size();
        StarsThermostatProgram[] mArray = new StarsThermostatProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsThermostatProgram) _starsThermostatProgramList.elementAt(index);
        }
        return mArray;
    } //-- StarsThermostatProgram[] getStarsThermostatProgram() 

    /**
    **/
    public int getStarsThermostatProgramCount()
    {
        return _starsThermostatProgramList.size();
    } //-- int getStarsThermostatProgramCount() 

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
    public void removeAllStarsThermostatProgram()
    {
        _starsThermostatProgramList.removeAllElements();
    } //-- void removeAllStarsThermostatProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatProgram removeStarsThermostatProgram(int index)
    {
        java.lang.Object obj = _starsThermostatProgramList.elementAt(index);
        _starsThermostatProgramList.removeElementAt(index);
        return (StarsThermostatProgram) obj;
    } //-- StarsThermostatProgram removeStarsThermostatProgram(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatProgram
    **/
    public void setStarsThermostatProgram(int index, StarsThermostatProgram vStarsThermostatProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatProgramList.setElementAt(vStarsThermostatProgram, index);
    } //-- void setStarsThermostatProgram(int, StarsThermostatProgram) 

    /**
     * 
     * 
     * @param starsThermostatProgramArray
    **/
    public void setStarsThermostatProgram(StarsThermostatProgram[] starsThermostatProgramArray)
    {
        //-- copy array
        _starsThermostatProgramList.removeAllElements();
        for (int i = 0; i < starsThermostatProgramArray.length; i++) {
            _starsThermostatProgramList.addElement(starsThermostatProgramArray[i]);
        }
    } //-- void setStarsThermostatProgram(StarsThermostatProgram) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSavedThermostatSchedules unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSavedThermostatSchedules) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSavedThermostatSchedules.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSavedThermostatSchedules unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

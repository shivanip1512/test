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
public abstract class StarsThermoSettings implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsThermoScheduleList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermoSettings() {
        super();
        _starsThermoScheduleList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsThermoSettings()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsThermoSchedule
    **/
    public void addStarsThermoSchedule(StarsThermoSchedule vStarsThermoSchedule)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsThermoScheduleList.size() < 4)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermoScheduleList.addElement(vStarsThermoSchedule);
    } //-- void addStarsThermoSchedule(StarsThermoSchedule) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermoSchedule
    **/
    public void addStarsThermoSchedule(int index, StarsThermoSchedule vStarsThermoSchedule)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsThermoScheduleList.size() < 4)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermoScheduleList.insertElementAt(vStarsThermoSchedule, index);
    } //-- void addStarsThermoSchedule(int, StarsThermoSchedule) 

    /**
    **/
    public java.util.Enumeration enumerateStarsThermoSchedule()
    {
        return _starsThermoScheduleList.elements();
    } //-- java.util.Enumeration enumerateStarsThermoSchedule() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermoSchedule getStarsThermoSchedule(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermoScheduleList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsThermoSchedule) _starsThermoScheduleList.elementAt(index);
    } //-- StarsThermoSchedule getStarsThermoSchedule(int) 

    /**
    **/
    public StarsThermoSchedule[] getStarsThermoSchedule()
    {
        int size = _starsThermoScheduleList.size();
        StarsThermoSchedule[] mArray = new StarsThermoSchedule[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsThermoSchedule) _starsThermoScheduleList.elementAt(index);
        }
        return mArray;
    } //-- StarsThermoSchedule[] getStarsThermoSchedule() 

    /**
    **/
    public int getStarsThermoScheduleCount()
    {
        return _starsThermoScheduleList.size();
    } //-- int getStarsThermoScheduleCount() 

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
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
    **/
    public void removeAllStarsThermoSchedule()
    {
        _starsThermoScheduleList.removeAllElements();
    } //-- void removeAllStarsThermoSchedule() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermoSchedule removeStarsThermoSchedule(int index)
    {
        java.lang.Object obj = _starsThermoScheduleList.elementAt(index);
        _starsThermoScheduleList.removeElementAt(index);
        return (StarsThermoSchedule) obj;
    } //-- StarsThermoSchedule removeStarsThermoSchedule(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermoSchedule
    **/
    public void setStarsThermoSchedule(int index, StarsThermoSchedule vStarsThermoSchedule)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermoScheduleList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 4)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermoScheduleList.setElementAt(vStarsThermoSchedule, index);
    } //-- void setStarsThermoSchedule(int, StarsThermoSchedule) 

    /**
     * 
     * 
     * @param starsThermoScheduleArray
    **/
    public void setStarsThermoSchedule(StarsThermoSchedule[] starsThermoScheduleArray)
    {
        //-- copy array
        _starsThermoScheduleList.removeAllElements();
        for (int i = 0; i < starsThermoScheduleArray.length; i++) {
            _starsThermoScheduleList.addElement(starsThermoScheduleArray[i]);
        }
    } //-- void setStarsThermoSchedule(StarsThermoSchedule) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

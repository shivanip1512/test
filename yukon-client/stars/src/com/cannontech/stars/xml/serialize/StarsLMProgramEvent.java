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

import java.util.Vector;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsLMProgramEvent extends com.cannontech.stars.xml.serialize.StarsLMCustomerEvent {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _programIDList;

    private int _duration;

    /**
     * keeps track of state for field: _duration
    **/
    private boolean _has_duration;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMProgramEvent() {
        super();
        _programIDList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsLMProgramEvent()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vProgramID
    **/
    public void addProgramID(int vProgramID)
        throws java.lang.IndexOutOfBoundsException
    {
        _programIDList.addElement(new Integer(vProgramID));
    } //-- void addProgramID(int) 

    /**
     * 
     * 
     * @param index
     * @param vProgramID
    **/
    public void addProgramID(int index, int vProgramID)
        throws java.lang.IndexOutOfBoundsException
    {
        _programIDList.insertElementAt(new Integer(vProgramID), index);
    } //-- void addProgramID(int, int) 

    /**
    **/
    public void deleteDuration()
    {
        this._has_duration= false;
    } //-- void deleteDuration() 

    /**
    **/
    public java.util.Enumeration enumerateProgramID()
    {
        return _programIDList.elements();
    } //-- java.util.Enumeration enumerateProgramID() 

    /**
     * Returns the value of field 'duration'.
     * 
     * @return the value of field 'duration'.
    **/
    public int getDuration()
    {
        return this._duration;
    } //-- int getDuration() 

    /**
     * 
     * 
     * @param index
    **/
    public int getProgramID(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _programIDList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return ((Integer)_programIDList.elementAt(index)).intValue();
    } //-- int getProgramID(int) 

    /**
    **/
    public int[] getProgramID()
    {
        int size = _programIDList.size();
        int[] mArray = new int[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = ((Integer)_programIDList.elementAt(index)).intValue();
        }
        return mArray;
    } //-- int[] getProgramID() 

    /**
    **/
    public int getProgramIDCount()
    {
        return _programIDList.size();
    } //-- int getProgramIDCount() 

    /**
    **/
    public boolean hasDuration()
    {
        return this._has_duration;
    } //-- boolean hasDuration() 

    /**
    **/
    public void removeAllProgramID()
    {
        _programIDList.removeAllElements();
    } //-- void removeAllProgramID() 

    /**
     * 
     * 
     * @param index
    **/
    public int removeProgramID(int index)
    {
        java.lang.Object obj = _programIDList.elementAt(index);
        _programIDList.removeElementAt(index);
        return ((Integer)obj).intValue();
    } //-- int removeProgramID(int) 

    /**
     * Sets the value of field 'duration'.
     * 
     * @param duration the value of field 'duration'.
    **/
    public void setDuration(int duration)
    {
        this._duration = duration;
        this._has_duration = true;
    } //-- void setDuration(int) 

    /**
     * 
     * 
     * @param index
     * @param vProgramID
    **/
    public void setProgramID(int index, int vProgramID)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _programIDList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _programIDList.setElementAt(new Integer(vProgramID), index);
    } //-- void setProgramID(int, int) 

    /**
     * 
     * 
     * @param programIDArray
    **/
    public void setProgramID(int[] programIDArray)
    {
        //-- copy array
        _programIDList.removeAllElements();
        for (int i = 0; i < programIDArray.length; i++) {
            _programIDList.addElement(new Integer(programIDArray[i]));
        }
    } //-- void setProgramID(int) 

}

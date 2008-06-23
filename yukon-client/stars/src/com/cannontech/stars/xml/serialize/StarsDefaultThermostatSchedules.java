package com.cannontech.stars.xml.serialize;

import java.util.Vector;

import com.cannontech.stars.xml.serialize.types.StarsThermostatTypes;

public class StarsDefaultThermostatSchedules {
    private java.util.Vector _starsThermostatProgramList;

    public StarsDefaultThermostatSchedules() {
        _starsThermostatProgramList = new Vector();
    }

    /**
     * 
     * 
     * @param vStarsThermostatProgram
    **/
    public void addStarsThermostatProgram(StarsThermostatProgram vStarsThermostatProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsThermostatProgramList.size() < StarsThermostatTypes.NUMBER_OF_THERMO_TYPES)) {
            throw new IndexOutOfBoundsException();
        }
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
        if (!(_starsThermostatProgramList.size() < 3)) {
            throw new IndexOutOfBoundsException();
        }
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
        if (!(index < 3)) {
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

}

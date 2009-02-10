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
public class StarsThermostatSettings {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsThermostatManualEventList;

    private StarsThermostatDynamicData _starsThermostatDynamicData;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermostatSettings() {
        super();
        _starsThermostatManualEventList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatSettings()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsThermostatManualEvent
    **/
    public void addStarsThermostatManualEvent(StarsThermostatManualEvent vStarsThermostatManualEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsThermostatManualEventList.addElement(vStarsThermostatManualEvent);
    } //-- void addStarsThermostatManualEvent(StarsThermostatManualEvent) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatManualEvent
    **/
    public void addStarsThermostatManualEvent(int index, StarsThermostatManualEvent vStarsThermostatManualEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsThermostatManualEventList.insertElementAt(vStarsThermostatManualEvent, index);
    } //-- void addStarsThermostatManualEvent(int, StarsThermostatManualEvent) 

    /**
    **/
    public java.util.Enumeration enumerateStarsThermostatManualEvent()
    {
        return _starsThermostatManualEventList.elements();
    } //-- java.util.Enumeration enumerateStarsThermostatManualEvent() 

    /**
     * Returns the value of field 'starsThermostatDynamicData'.
     * 
     * @return the value of field 'starsThermostatDynamicData'.
    **/
    public StarsThermostatDynamicData getStarsThermostatDynamicData()
    {
        return this._starsThermostatDynamicData;
    } //-- StarsThermostatDynamicData getStarsThermostatDynamicData() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatManualEvent getStarsThermostatManualEvent(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatManualEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsThermostatManualEvent) _starsThermostatManualEventList.elementAt(index);
    } //-- StarsThermostatManualEvent getStarsThermostatManualEvent(int) 

    /**
    **/
    public StarsThermostatManualEvent[] getStarsThermostatManualEvent()
    {
        int size = _starsThermostatManualEventList.size();
        StarsThermostatManualEvent[] mArray = new StarsThermostatManualEvent[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsThermostatManualEvent) _starsThermostatManualEventList.elementAt(index);
        }
        return mArray;
    } //-- StarsThermostatManualEvent[] getStarsThermostatManualEvent() 

    /**
    **/
    public int getStarsThermostatManualEventCount()
    {
        return _starsThermostatManualEventList.size();
    } //-- int getStarsThermostatManualEventCount() 

    /**
    **/
    public void removeAllStarsThermostatManualEvent()
    {
        _starsThermostatManualEventList.removeAllElements();
    } //-- void removeAllStarsThermostatManualEvent() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatManualEvent removeStarsThermostatManualEvent(int index)
    {
        java.lang.Object obj = _starsThermostatManualEventList.elementAt(index);
        _starsThermostatManualEventList.removeElementAt(index);
        return (StarsThermostatManualEvent) obj;
    } //-- StarsThermostatManualEvent removeStarsThermostatManualEvent(int) 

    /**
     * Sets the value of field 'starsThermostatDynamicData'.
     * 
     * @param starsThermostatDynamicData the value of field
     * 'starsThermostatDynamicData'.
    **/
    public void setStarsThermostatDynamicData(StarsThermostatDynamicData starsThermostatDynamicData)
    {
        this._starsThermostatDynamicData = starsThermostatDynamicData;
    } //-- void setStarsThermostatDynamicData(StarsThermostatDynamicData) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatManualEvent
    **/
    public void setStarsThermostatManualEvent(int index, StarsThermostatManualEvent vStarsThermostatManualEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatManualEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatManualEventList.setElementAt(vStarsThermostatManualEvent, index);
    } //-- void setStarsThermostatManualEvent(int, StarsThermostatManualEvent) 

    /**
     * 
     * 
     * @param starsThermostatManualEventArray
    **/
    public void setStarsThermostatManualEvent(StarsThermostatManualEvent[] starsThermostatManualEventArray)
    {
        //-- copy array
        _starsThermostatManualEventList.removeAllElements();
        for (int i = 0; i < starsThermostatManualEventArray.length; i++) {
            _starsThermostatManualEventList.addElement(starsThermostatManualEventArray[i]);
        }
    } //-- void setStarsThermostatManualEvent(StarsThermostatManualEvent) 

}

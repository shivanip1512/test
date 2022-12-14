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
public class StarsLMProgramHistory {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsLMProgramEventList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMProgramHistory() {
        super();
        _starsLMProgramEventList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsLMProgramHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsLMProgramEvent
    **/
    public void addStarsLMProgramEvent(StarsLMProgramEvent vStarsLMProgramEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMProgramEventList.addElement(vStarsLMProgramEvent);
    } //-- void addStarsLMProgramEvent(StarsLMProgramEvent) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMProgramEvent
    **/
    public void addStarsLMProgramEvent(int index, StarsLMProgramEvent vStarsLMProgramEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMProgramEventList.insertElementAt(vStarsLMProgramEvent, index);
    } //-- void addStarsLMProgramEvent(int, StarsLMProgramEvent) 

    /**
    **/
    public java.util.Enumeration enumerateStarsLMProgramEvent()
    {
        return _starsLMProgramEventList.elements();
    } //-- java.util.Enumeration enumerateStarsLMProgramEvent() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMProgramEvent getStarsLMProgramEvent(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMProgramEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsLMProgramEvent) _starsLMProgramEventList.elementAt(index);
    } //-- StarsLMProgramEvent getStarsLMProgramEvent(int) 

    /**
    **/
    public StarsLMProgramEvent[] getStarsLMProgramEvent()
    {
        int size = _starsLMProgramEventList.size();
        StarsLMProgramEvent[] mArray = new StarsLMProgramEvent[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsLMProgramEvent) _starsLMProgramEventList.elementAt(index);
        }
        return mArray;
    } //-- StarsLMProgramEvent[] getStarsLMProgramEvent() 

    /**
    **/
    public int getStarsLMProgramEventCount()
    {
        return _starsLMProgramEventList.size();
    } //-- int getStarsLMProgramEventCount() 

    /**
    **/
    public void removeAllStarsLMProgramEvent()
    {
        _starsLMProgramEventList.removeAllElements();
    } //-- void removeAllStarsLMProgramEvent() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMProgramEvent removeStarsLMProgramEvent(int index)
    {
        java.lang.Object obj = _starsLMProgramEventList.elementAt(index);
        _starsLMProgramEventList.removeElementAt(index);
        return (StarsLMProgramEvent) obj;
    } //-- StarsLMProgramEvent removeStarsLMProgramEvent(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMProgramEvent
    **/
    public void setStarsLMProgramEvent(int index, StarsLMProgramEvent vStarsLMProgramEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMProgramEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsLMProgramEventList.setElementAt(vStarsLMProgramEvent, index);
    } //-- void setStarsLMProgramEvent(int, StarsLMProgramEvent) 

    /**
     * 
     * 
     * @param starsLMProgramEventArray
    **/
    public void setStarsLMProgramEvent(StarsLMProgramEvent[] starsLMProgramEventArray)
    {
        //-- copy array
        _starsLMProgramEventList.removeAllElements();
        for (int i = 0; i < starsLMProgramEventArray.length; i++) {
            _starsLMProgramEventList.addElement(starsLMProgramEventArray[i]);
        }
    } //-- void setStarsLMProgramEvent(StarsLMProgramEvent) 

}

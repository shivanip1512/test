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
public class StarsSendOddsForControl {


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

}

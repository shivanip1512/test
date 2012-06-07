/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsLMPrograms.java,v 1.96 2008/06/23 20:01:32 nmeverden Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;

/**
 * 
 * 
 * @version $Revision: 1.96 $ $Date: 2008/06/23 20:01:32 $
**/
public class StarsLMPrograms {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsLMProgramList;

    private StarsLMProgramHistory _starsLMProgramHistory;


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
     * Returns the value of field 'starsLMProgramHistory'.
     * 
     * @return the value of field 'starsLMProgramHistory'.
    **/
    public StarsLMProgramHistory getStarsLMProgramHistory()
    {
        return this._starsLMProgramHistory;
    } //-- StarsLMProgramHistory getStarsLMProgramHistory() 

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
     * Sets the value of field 'starsLMProgramHistory'.
     * 
     * @param starsLMProgramHistory the value of field
     * 'starsLMProgramHistory'.
    **/
    public void setStarsLMProgramHistory(StarsLMProgramHistory starsLMProgramHistory)
    {
        this._starsLMProgramHistory = starsLMProgramHistory;
    } //-- void setStarsLMProgramHistory(StarsLMProgramHistory) 

}

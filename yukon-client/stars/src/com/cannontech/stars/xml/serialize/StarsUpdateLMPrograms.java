/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsUpdateLMPrograms.java,v 1.96 2008/06/23 20:01:25 nmeverden Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;

/**
 * Update the LM programs for a customer account
 * 
 * @version $Revision: 1.96 $ $Date: 2008/06/23 20:01:25 $
**/
public class StarsUpdateLMPrograms {


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

}

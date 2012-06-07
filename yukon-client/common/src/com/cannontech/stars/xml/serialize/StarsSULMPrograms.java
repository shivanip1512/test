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
public class StarsSULMPrograms {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _SULMProgramList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSULMPrograms() {
        super();
        _SULMProgramList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSULMPrograms()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vSULMProgram
    **/
    public void addSULMProgram(SULMProgram vSULMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _SULMProgramList.addElement(vSULMProgram);
    } //-- void addSULMProgram(SULMProgram) 

    /**
     * 
     * 
     * @param index
     * @param vSULMProgram
    **/
    public void addSULMProgram(int index, SULMProgram vSULMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _SULMProgramList.insertElementAt(vSULMProgram, index);
    } //-- void addSULMProgram(int, SULMProgram) 

    /**
    **/
    public java.util.Enumeration enumerateSULMProgram()
    {
        return _SULMProgramList.elements();
    } //-- java.util.Enumeration enumerateSULMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public SULMProgram getSULMProgram(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _SULMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (SULMProgram) _SULMProgramList.elementAt(index);
    } //-- SULMProgram getSULMProgram(int) 

    /**
    **/
    public SULMProgram[] getSULMProgram()
    {
        int size = _SULMProgramList.size();
        SULMProgram[] mArray = new SULMProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (SULMProgram) _SULMProgramList.elementAt(index);
        }
        return mArray;
    } //-- SULMProgram[] getSULMProgram() 

    /**
    **/
    public int getSULMProgramCount()
    {
        return _SULMProgramList.size();
    } //-- int getSULMProgramCount() 

    /**
    **/
    public void removeAllSULMProgram()
    {
        _SULMProgramList.removeAllElements();
    } //-- void removeAllSULMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public SULMProgram removeSULMProgram(int index)
    {
        java.lang.Object obj = _SULMProgramList.elementAt(index);
        _SULMProgramList.removeElementAt(index);
        return (SULMProgram) obj;
    } //-- SULMProgram removeSULMProgram(int) 

    /**
     * 
     * 
     * @param index
     * @param vSULMProgram
    **/
    public void setSULMProgram(int index, SULMProgram vSULMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _SULMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _SULMProgramList.setElementAt(vSULMProgram, index);
    } //-- void setSULMProgram(int, SULMProgram) 

    /**
     * 
     * 
     * @param SULMProgramArray
    **/
    public void setSULMProgram(SULMProgram[] SULMProgramArray)
    {
        //-- copy array
        _SULMProgramList.removeAllElements();
        for (int i = 0; i < SULMProgramArray.length; i++) {
            _SULMProgramList.addElement(SULMProgramArray[i]);
        }
    } //-- void setSULMProgram(SULMProgram) 

}

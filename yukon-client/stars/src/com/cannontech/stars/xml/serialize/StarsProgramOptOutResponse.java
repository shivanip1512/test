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
public class StarsProgramOptOutResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsLMProgramHistoryList;

    private java.util.Vector _starsLMHardwareHistoryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramOptOutResponse() {
        super();
        _starsLMProgramHistoryList = new Vector();
        _starsLMHardwareHistoryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramOptOutResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsLMHardwareHistory
    **/
    public void addStarsLMHardwareHistory(StarsLMHardwareHistory vStarsLMHardwareHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareHistoryList.addElement(vStarsLMHardwareHistory);
    } //-- void addStarsLMHardwareHistory(StarsLMHardwareHistory) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardwareHistory
    **/
    public void addStarsLMHardwareHistory(int index, StarsLMHardwareHistory vStarsLMHardwareHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareHistoryList.insertElementAt(vStarsLMHardwareHistory, index);
    } //-- void addStarsLMHardwareHistory(int, StarsLMHardwareHistory) 

    /**
     * 
     * 
     * @param vStarsLMProgramHistory
    **/
    public void addStarsLMProgramHistory(StarsLMProgramHistory vStarsLMProgramHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMProgramHistoryList.addElement(vStarsLMProgramHistory);
    } //-- void addStarsLMProgramHistory(StarsLMProgramHistory) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMProgramHistory
    **/
    public void addStarsLMProgramHistory(int index, StarsLMProgramHistory vStarsLMProgramHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMProgramHistoryList.insertElementAt(vStarsLMProgramHistory, index);
    } //-- void addStarsLMProgramHistory(int, StarsLMProgramHistory) 

    /**
    **/
    public java.util.Enumeration enumerateStarsLMHardwareHistory()
    {
        return _starsLMHardwareHistoryList.elements();
    } //-- java.util.Enumeration enumerateStarsLMHardwareHistory() 

    /**
    **/
    public java.util.Enumeration enumerateStarsLMProgramHistory()
    {
        return _starsLMProgramHistoryList.elements();
    } //-- java.util.Enumeration enumerateStarsLMProgramHistory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardwareHistory getStarsLMHardwareHistory(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsLMHardwareHistory) _starsLMHardwareHistoryList.elementAt(index);
    } //-- StarsLMHardwareHistory getStarsLMHardwareHistory(int) 

    /**
    **/
    public StarsLMHardwareHistory[] getStarsLMHardwareHistory()
    {
        int size = _starsLMHardwareHistoryList.size();
        StarsLMHardwareHistory[] mArray = new StarsLMHardwareHistory[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsLMHardwareHistory) _starsLMHardwareHistoryList.elementAt(index);
        }
        return mArray;
    } //-- StarsLMHardwareHistory[] getStarsLMHardwareHistory() 

    /**
    **/
    public int getStarsLMHardwareHistoryCount()
    {
        return _starsLMHardwareHistoryList.size();
    } //-- int getStarsLMHardwareHistoryCount() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMProgramHistory getStarsLMProgramHistory(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMProgramHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsLMProgramHistory) _starsLMProgramHistoryList.elementAt(index);
    } //-- StarsLMProgramHistory getStarsLMProgramHistory(int) 

    /**
    **/
    public StarsLMProgramHistory[] getStarsLMProgramHistory()
    {
        int size = _starsLMProgramHistoryList.size();
        StarsLMProgramHistory[] mArray = new StarsLMProgramHistory[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsLMProgramHistory) _starsLMProgramHistoryList.elementAt(index);
        }
        return mArray;
    } //-- StarsLMProgramHistory[] getStarsLMProgramHistory() 

    /**
    **/
    public int getStarsLMProgramHistoryCount()
    {
        return _starsLMProgramHistoryList.size();
    } //-- int getStarsLMProgramHistoryCount() 

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
    public void removeAllStarsLMHardwareHistory()
    {
        _starsLMHardwareHistoryList.removeAllElements();
    } //-- void removeAllStarsLMHardwareHistory() 

    /**
    **/
    public void removeAllStarsLMProgramHistory()
    {
        _starsLMProgramHistoryList.removeAllElements();
    } //-- void removeAllStarsLMProgramHistory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardwareHistory removeStarsLMHardwareHistory(int index)
    {
        java.lang.Object obj = _starsLMHardwareHistoryList.elementAt(index);
        _starsLMHardwareHistoryList.removeElementAt(index);
        return (StarsLMHardwareHistory) obj;
    } //-- StarsLMHardwareHistory removeStarsLMHardwareHistory(int) 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMProgramHistory removeStarsLMProgramHistory(int index)
    {
        java.lang.Object obj = _starsLMProgramHistoryList.elementAt(index);
        _starsLMProgramHistoryList.removeElementAt(index);
        return (StarsLMProgramHistory) obj;
    } //-- StarsLMProgramHistory removeStarsLMProgramHistory(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardwareHistory
    **/
    public void setStarsLMHardwareHistory(int index, StarsLMHardwareHistory vStarsLMHardwareHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsLMHardwareHistoryList.setElementAt(vStarsLMHardwareHistory, index);
    } //-- void setStarsLMHardwareHistory(int, StarsLMHardwareHistory) 

    /**
     * 
     * 
     * @param starsLMHardwareHistoryArray
    **/
    public void setStarsLMHardwareHistory(StarsLMHardwareHistory[] starsLMHardwareHistoryArray)
    {
        //-- copy array
        _starsLMHardwareHistoryList.removeAllElements();
        for (int i = 0; i < starsLMHardwareHistoryArray.length; i++) {
            _starsLMHardwareHistoryList.addElement(starsLMHardwareHistoryArray[i]);
        }
    } //-- void setStarsLMHardwareHistory(StarsLMHardwareHistory) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMProgramHistory
    **/
    public void setStarsLMProgramHistory(int index, StarsLMProgramHistory vStarsLMProgramHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMProgramHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsLMProgramHistoryList.setElementAt(vStarsLMProgramHistory, index);
    } //-- void setStarsLMProgramHistory(int, StarsLMProgramHistory) 

    /**
     * 
     * 
     * @param starsLMProgramHistoryArray
    **/
    public void setStarsLMProgramHistory(StarsLMProgramHistory[] starsLMProgramHistoryArray)
    {
        //-- copy array
        _starsLMProgramHistoryList.removeAllElements();
        for (int i = 0; i < starsLMProgramHistoryArray.length; i++) {
            _starsLMProgramHistoryList.addElement(starsLMProgramHistoryArray[i]);
        }
    } //-- void setStarsLMProgramHistory(StarsLMProgramHistory) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsProgramOptOutResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsProgramOptOutResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsProgramOptOutResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsProgramOptOutResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

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
public class StarsProgramReenableResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsLMProgramHistory _starsLMProgramHistory;

    private java.util.Vector _starsLMHardwareHistoryList;

    private java.lang.String _description;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramReenableResponse() {
        super();
        _starsLMHardwareHistoryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramReenableResponse()


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
    **/
    public java.util.Enumeration enumerateStarsLMHardwareHistory()
    {
        return _starsLMHardwareHistoryList.elements();
    } //-- java.util.Enumeration enumerateStarsLMHardwareHistory() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'description'.
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

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
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
    **/
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

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
     * Sets the value of field 'starsLMProgramHistory'.
     * 
     * @param starsLMProgramHistory the value of field
     * 'starsLMProgramHistory'.
    **/
    public void setStarsLMProgramHistory(StarsLMProgramHistory starsLMProgramHistory)
    {
        this._starsLMProgramHistory = starsLMProgramHistory;
    } //-- void setStarsLMProgramHistory(StarsLMProgramHistory) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsProgramReenableResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsProgramReenableResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsProgramReenableResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsProgramReenableResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

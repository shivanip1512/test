/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsLMControlHistory.java,v 1.90 2004/12/09 16:25:38 zyao Exp $
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
 * @version $Revision: 1.90 $ $Date: 2004/12/09 16:25:38 $
**/
public class StarsLMControlHistory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private boolean _beingControlled;

    /**
     * keeps track of state for field: _beingControlled
    **/
    private boolean _has_beingControlled;

    private java.util.Vector _controlHistoryList;

    private ControlSummary _controlSummary;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMControlHistory() {
        super();
        _controlHistoryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsLMControlHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vControlHistory
    **/
    public void addControlHistory(ControlHistory vControlHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _controlHistoryList.addElement(vControlHistory);
    } //-- void addControlHistory(ControlHistory) 

    /**
     * 
     * 
     * @param index
     * @param vControlHistory
    **/
    public void addControlHistory(int index, ControlHistory vControlHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _controlHistoryList.insertElementAt(vControlHistory, index);
    } //-- void addControlHistory(int, ControlHistory) 

    /**
    **/
    public void deleteBeingControlled()
    {
        this._has_beingControlled= false;
    } //-- void deleteBeingControlled() 

    /**
    **/
    public java.util.Enumeration enumerateControlHistory()
    {
        return _controlHistoryList.elements();
    } //-- java.util.Enumeration enumerateControlHistory() 

    /**
     * Returns the value of field 'beingControlled'.
     * 
     * @return the value of field 'beingControlled'.
    **/
    public boolean getBeingControlled()
    {
        return this._beingControlled;
    } //-- boolean getBeingControlled() 

    /**
     * 
     * 
     * @param index
    **/
    public ControlHistory getControlHistory(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _controlHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ControlHistory) _controlHistoryList.elementAt(index);
    } //-- ControlHistory getControlHistory(int) 

    /**
    **/
    public ControlHistory[] getControlHistory()
    {
        int size = _controlHistoryList.size();
        ControlHistory[] mArray = new ControlHistory[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ControlHistory) _controlHistoryList.elementAt(index);
        }
        return mArray;
    } //-- ControlHistory[] getControlHistory() 

    /**
    **/
    public int getControlHistoryCount()
    {
        return _controlHistoryList.size();
    } //-- int getControlHistoryCount() 

    /**
     * Returns the value of field 'controlSummary'.
     * 
     * @return the value of field 'controlSummary'.
    **/
    public ControlSummary getControlSummary()
    {
        return this._controlSummary;
    } //-- ControlSummary getControlSummary() 

    /**
    **/
    public boolean hasBeingControlled()
    {
        return this._has_beingControlled;
    } //-- boolean hasBeingControlled() 

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
    public void removeAllControlHistory()
    {
        _controlHistoryList.removeAllElements();
    } //-- void removeAllControlHistory() 

    /**
     * 
     * 
     * @param index
    **/
    public ControlHistory removeControlHistory(int index)
    {
        java.lang.Object obj = _controlHistoryList.elementAt(index);
        _controlHistoryList.removeElementAt(index);
        return (ControlHistory) obj;
    } //-- ControlHistory removeControlHistory(int) 

    /**
     * Sets the value of field 'beingControlled'.
     * 
     * @param beingControlled the value of field 'beingControlled'.
    **/
    public void setBeingControlled(boolean beingControlled)
    {
        this._beingControlled = beingControlled;
        this._has_beingControlled = true;
    } //-- void setBeingControlled(boolean) 

    /**
     * 
     * 
     * @param index
     * @param vControlHistory
    **/
    public void setControlHistory(int index, ControlHistory vControlHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _controlHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _controlHistoryList.setElementAt(vControlHistory, index);
    } //-- void setControlHistory(int, ControlHistory) 

    /**
     * 
     * 
     * @param controlHistoryArray
    **/
    public void setControlHistory(ControlHistory[] controlHistoryArray)
    {
        //-- copy array
        _controlHistoryList.removeAllElements();
        for (int i = 0; i < controlHistoryArray.length; i++) {
            _controlHistoryList.addElement(controlHistoryArray[i]);
        }
    } //-- void setControlHistory(ControlHistory) 

    /**
     * Sets the value of field 'controlSummary'.
     * 
     * @param controlSummary the value of field 'controlSummary'.
    **/
    public void setControlSummary(ControlSummary controlSummary)
    {
        this._controlSummary = controlSummary;
    } //-- void setControlSummary(ControlSummary) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsLMControlHistory unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsLMControlHistory) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsLMControlHistory.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsLMControlHistory unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

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
import java.util.Date;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsCallReport implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _callNumber;

    private java.util.Date _callDate;

    private CallType _callType;

    private java.lang.String _takenBy;

    private java.lang.String _description;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCallReport() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCallReport()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'callDate'.
     * 
     * @return the value of field 'callDate'.
    **/
    public java.util.Date getCallDate()
    {
        return this._callDate;
    } //-- java.util.Date getCallDate() 

    /**
     * Returns the value of field 'callNumber'.
     * 
     * @return the value of field 'callNumber'.
    **/
    public java.lang.String getCallNumber()
    {
        return this._callNumber;
    } //-- java.lang.String getCallNumber() 

    /**
     * Returns the value of field 'callType'.
     * 
     * @return the value of field 'callType'.
    **/
    public CallType getCallType()
    {
        return this._callType;
    } //-- CallType getCallType() 

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
     * Returns the value of field 'takenBy'.
     * 
     * @return the value of field 'takenBy'.
    **/
    public java.lang.String getTakenBy()
    {
        return this._takenBy;
    } //-- java.lang.String getTakenBy() 

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
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'callDate'.
     * 
     * @param callDate the value of field 'callDate'.
    **/
    public void setCallDate(java.util.Date callDate)
    {
        this._callDate = callDate;
    } //-- void setCallDate(java.util.Date) 

    /**
     * Sets the value of field 'callNumber'.
     * 
     * @param callNumber the value of field 'callNumber'.
    **/
    public void setCallNumber(java.lang.String callNumber)
    {
        this._callNumber = callNumber;
    } //-- void setCallNumber(java.lang.String) 

    /**
     * Sets the value of field 'callType'.
     * 
     * @param callType the value of field 'callType'.
    **/
    public void setCallType(CallType callType)
    {
        this._callType = callType;
    } //-- void setCallType(CallType) 

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
     * Sets the value of field 'takenBy'.
     * 
     * @param takenBy the value of field 'takenBy'.
    **/
    public void setTakenBy(java.lang.String takenBy)
    {
        this._takenBy = takenBy;
    } //-- void setTakenBy(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

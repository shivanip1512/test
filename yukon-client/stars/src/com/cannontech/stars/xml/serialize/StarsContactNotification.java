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
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsContactNotification implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _notifCatID;

    /**
     * keeps track of state for field: _notifCatID
    **/
    private boolean _has_notifCatID;

    private boolean _disabled;

    /**
     * keeps track of state for field: _disabled
    **/
    private boolean _has_disabled;

    private java.lang.String _notification;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsContactNotification() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsContactNotification()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteDisabled()
    {
        this._has_disabled= false;
    } //-- void deleteDisabled() 

    /**
    **/
    public void deleteNotifCatID()
    {
        this._has_notifCatID= false;
    } //-- void deleteNotifCatID() 

    /**
     * Returns the value of field 'disabled'.
     * 
     * @return the value of field 'disabled'.
    **/
    public boolean getDisabled()
    {
        return this._disabled;
    } //-- boolean getDisabled() 

    /**
     * Returns the value of field 'notifCatID'.
     * 
     * @return the value of field 'notifCatID'.
    **/
    public int getNotifCatID()
    {
        return this._notifCatID;
    } //-- int getNotifCatID() 

    /**
     * Returns the value of field 'notification'.
     * 
     * @return the value of field 'notification'.
    **/
    public java.lang.String getNotification()
    {
        return this._notification;
    } //-- java.lang.String getNotification() 

    /**
    **/
    public boolean hasDisabled()
    {
        return this._has_disabled;
    } //-- boolean hasDisabled() 

    /**
    **/
    public boolean hasNotifCatID()
    {
        return this._has_notifCatID;
    } //-- boolean hasNotifCatID() 

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
     * Sets the value of field 'disabled'.
     * 
     * @param disabled the value of field 'disabled'.
    **/
    public void setDisabled(boolean disabled)
    {
        this._disabled = disabled;
        this._has_disabled = true;
    } //-- void setDisabled(boolean) 

    /**
     * Sets the value of field 'notifCatID'.
     * 
     * @param notifCatID the value of field 'notifCatID'.
    **/
    public void setNotifCatID(int notifCatID)
    {
        this._notifCatID = notifCatID;
        this._has_notifCatID = true;
    } //-- void setNotifCatID(int) 

    /**
     * Sets the value of field 'notification'.
     * 
     * @param notification the value of field 'notification'.
    **/
    public void setNotification(java.lang.String notification)
    {
        this._notification = notification;
    } //-- void setNotification(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

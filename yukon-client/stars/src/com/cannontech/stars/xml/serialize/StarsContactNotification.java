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



/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsContactNotification implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private boolean _enabled;

    /**
     * keeps track of state for field: _enabled
    **/
    private boolean _has_enabled;

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
     * Returns the value of field 'enabled'.
     * 
     * @return the value of field 'enabled'.
    **/
    public boolean getEnabled()
    {
        return this._enabled;
    } //-- boolean getEnabled() 

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
    public boolean hasEnabled()
    {
        return this._has_enabled;
    } //-- boolean hasEnabled() 

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
     * Sets the value of field 'enabled'.
     * 
     * @param enabled the value of field 'enabled'.
    **/
    public void setEnabled(boolean enabled)
    {
        this._enabled = enabled;
        this._has_enabled = true;
    } //-- void setEnabled(boolean) 

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

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
public abstract class StarsUsr implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _userID;

    /**
     * keeps track of state for field: _userID
    **/
    private boolean _has_userID;

    private java.lang.String _username;

    private java.lang.String _password;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUsr() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUsr()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteUserID()
    {
        this._has_userID= false;
    } //-- void deleteUserID() 

    /**
     * Returns the value of field 'password'.
     * 
     * @return the value of field 'password'.
    **/
    public java.lang.String getPassword()
    {
        return this._password;
    } //-- java.lang.String getPassword() 

    /**
     * Returns the value of field 'userID'.
     * 
     * @return the value of field 'userID'.
    **/
    public int getUserID()
    {
        return this._userID;
    } //-- int getUserID() 

    /**
     * Returns the value of field 'username'.
     * 
     * @return the value of field 'username'.
    **/
    public java.lang.String getUsername()
    {
        return this._username;
    } //-- java.lang.String getUsername() 

    /**
    **/
    public boolean hasUserID()
    {
        return this._has_userID;
    } //-- boolean hasUserID() 

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
     * Sets the value of field 'password'.
     * 
     * @param password the value of field 'password'.
    **/
    public void setPassword(java.lang.String password)
    {
        this._password = password;
    } //-- void setPassword(java.lang.String) 

    /**
     * Sets the value of field 'userID'.
     * 
     * @param userID the value of field 'userID'.
    **/
    public void setUserID(int userID)
    {
        this._userID = userID;
        this._has_userID = true;
    } //-- void setUserID(int) 

    /**
     * Sets the value of field 'username'.
     * 
     * @param username the value of field 'username'.
    **/
    public void setUsername(java.lang.String username)
    {
        this._username = username;
    } //-- void setUsername(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}

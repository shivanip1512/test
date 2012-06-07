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
public abstract class StarsUsr {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _userID;

    /**
     * keeps track of state for field: _userID
    **/
    private boolean _has_userID;

    private int _groupID;

    /**
     * keeps track of state for field: _groupID
    **/
    private boolean _has_groupID;

    private java.lang.String _username;

    private java.lang.String _password;

    private com.cannontech.stars.xml.serialize.types.StarsLoginStatus _status;


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
    public void deleteGroupID()
    {
        this._has_groupID= false;
    } //-- void deleteGroupID() 

    /**
    **/
    public void deleteUserID()
    {
        this._has_userID= false;
    } //-- void deleteUserID() 

    /**
     * Returns the value of field 'groupID'.
     * 
     * @return the value of field 'groupID'.
    **/
    public int getGroupID()
    {
        return this._groupID;
    } //-- int getGroupID() 

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
     * Returns the value of field 'status'.
     * 
     * @return the value of field 'status'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsLoginStatus getStatus()
    {
        return this._status;
    } //-- com.cannontech.stars.xml.serialize.types.StarsLoginStatus getStatus() 

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
    public boolean hasGroupID()
    {
        return this._has_groupID;
    } //-- boolean hasGroupID() 

    /**
    **/
    public boolean hasUserID()
    {
        return this._has_userID;
    } //-- boolean hasUserID() 

    /**
     * Sets the value of field 'groupID'.
     * 
     * @param groupID the value of field 'groupID'.
    **/
    public void setGroupID(int groupID)
    {
        this._groupID = groupID;
        this._has_groupID = true;
    } //-- void setGroupID(int) 

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
     * Sets the value of field 'status'.
     * 
     * @param status the value of field 'status'.
    **/
    public void setStatus(com.cannontech.stars.xml.serialize.types.StarsLoginStatus status)
    {
        this._status = status;
    } //-- void setStatus(com.cannontech.stars.xml.serialize.types.StarsLoginStatus) 

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

}

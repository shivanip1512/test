/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

public class StarsLMHardwareConfig {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _applianceID;

    /**
     * keeps track of state for field: _applianceID
    **/
    private boolean _has_applianceID;

    private int _groupID;

    /**
     * keeps track of state for field: _groupID
    **/
    private boolean _has_groupID;

    private int _programID;

    /**
     * keeps track of state for field: _programID
    **/
    private boolean _has_programID;

    private int _loadNumber;

    /**
     * keeps track of state for field: _loadNumber
    **/
    private boolean _has_loadNumber;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMHardwareConfig() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsLMHardwareConfig()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteApplianceID()
    {
        this._has_applianceID= false;
    } //-- void deleteApplianceID() 

    /**
    **/
    public void deleteGroupID()
    {
        this._has_groupID= false;
    } //-- void deleteGroupID() 

    /**
    **/
    public void deleteLoadNumber()
    {
        this._has_loadNumber= false;
    } //-- void deleteLoadNumber() 

    /**
    **/
    public void deleteProgramID()
    {
        this._has_programID= false;
    } //-- void deleteProgramID() 

    /**
     * Returns the value of field 'applianceID'.
     * 
     * @return the value of field 'applianceID'.
    **/
    public int getApplianceID()
    {
        return this._applianceID;
    } //-- int getApplianceID() 

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
     * Returns the value of field 'loadNumber'.
     * 
     * @return the value of field 'loadNumber'.
    **/
    public int getLoadNumber()
    {
        return this._loadNumber;
    } //-- int getLoadNumber() 

    /**
     * Returns the value of field 'programID'.
     * 
     * @return the value of field 'programID'.
    **/
    public int getProgramID()
    {
        return this._programID;
    } //-- int getProgramID() 

    /**
    **/
    public boolean hasApplianceID()
    {
        return this._has_applianceID;
    } //-- boolean hasApplianceID() 

    /**
    **/
    public boolean hasGroupID()
    {
        return this._has_groupID;
    } //-- boolean hasGroupID() 

    /**
    **/
    public boolean hasLoadNumber()
    {
        return this._has_loadNumber;
    } //-- boolean hasLoadNumber() 

    /**
    **/
    public boolean hasProgramID()
    {
        return this._has_programID;
    } //-- boolean hasProgramID() 

    /**
     * Sets the value of field 'applianceID'.
     * 
     * @param applianceID the value of field 'applianceID'.
    **/
    public void setApplianceID(int applianceID)
    {
        this._applianceID = applianceID;
        this._has_applianceID = true;
    } //-- void setApplianceID(int) 

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
     * Sets the value of field 'loadNumber'.
     * 
     * @param loadNumber the value of field 'loadNumber'.
    **/
    public void setLoadNumber(int loadNumber)
    {
        this._loadNumber = loadNumber;
        this._has_loadNumber = true;
    } //-- void setLoadNumber(int) 

    /**
     * Sets the value of field 'programID'.
     * 
     * @param programID the value of field 'programID'.
    **/
    public void setProgramID(int programID)
    {
        this._programID = programID;
        this._has_programID = true;
    } //-- void setProgramID(int) 

}

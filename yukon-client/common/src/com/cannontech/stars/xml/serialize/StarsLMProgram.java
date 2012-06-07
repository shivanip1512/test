package com.cannontech.stars.xml.serialize;

public class StarsLMProgram {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _programID;

    /**
     * keeps track of state for field: _programID
    **/
    private boolean _has_programID;

    private int _groupID;

    /**
     * keeps track of state for field: _groupID
    **/
    private boolean _has_groupID;

    private int _applianceCategoryID;

    /**
     * keeps track of state for field: _applianceCategoryID
    **/
    private boolean _has_applianceCategoryID;

    private java.lang.String _programName;

    private java.lang.String _status;

    private java.util.Date _dateEnrolled;

    private StarsLMControlHistory _starsLMControlHistory;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMProgram() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsLMProgram()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteApplianceCategoryID()
    {
        this._has_applianceCategoryID= false;
    } //-- void deleteApplianceCategoryID() 

    /**
    **/
    public void deleteGroupID()
    {
        this._has_groupID= false;
    } //-- void deleteGroupID() 

    /**
     * Returns the value of field 'applianceCategoryID'.
     * 
     * @return the value of field 'applianceCategoryID'.
    **/
    public int getApplianceCategoryID()
    {
        return this._applianceCategoryID;
    } //-- int getApplianceCategoryID() 

    /**
     * Returns the value of field 'dateEnrolled'.
     * 
     * @return the value of field 'dateEnrolled'.
    **/
    public java.util.Date getDateEnrolled()
    {
        return this._dateEnrolled;
    } //-- java.util.Date getDateEnrolled() 

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
     * Returns the value of field 'programID'.
     * 
     * @return the value of field 'programID'.
    **/
    public int getProgramID()
    {
        return this._programID;
    } //-- int getProgramID() 

    /**
     * Returns the value of field 'programName'.
     * 
     * @return the value of field 'programName'.
    **/
    public java.lang.String getProgramName()
    {
        return this._programName;
    } //-- java.lang.String getProgramName() 

    /**
     * Returns the value of field 'starsLMControlHistory'.
     * 
     * @return the value of field 'starsLMControlHistory'.
    **/
    public StarsLMControlHistory getStarsLMControlHistory()
    {
        return this._starsLMControlHistory;
    } //-- StarsLMControlHistory getStarsLMControlHistory() 

    /**
     * Returns the value of field 'status'.
     * 
     * @return the value of field 'status'.
    **/
    public java.lang.String getStatus()
    {
        return this._status;
    } //-- java.lang.String getStatus() 

    /**
    **/
    public boolean hasApplianceCategoryID()
    {
        return this._has_applianceCategoryID;
    } //-- boolean hasApplianceCategoryID() 

    /**
    **/
    public boolean hasGroupID()
    {
        return this._has_groupID;
    } //-- boolean hasGroupID() 

    /**
    **/
    public boolean hasProgramID()
    {
        return this._has_programID;
    } //-- boolean hasProgramID() 

    /**
     * Sets the value of field 'applianceCategoryID'.
     * 
     * @param applianceCategoryID the value of field
     * 'applianceCategoryID'.
    **/
    public void setApplianceCategoryID(int applianceCategoryID)
    {
        this._applianceCategoryID = applianceCategoryID;
        this._has_applianceCategoryID = true;
    } //-- void setApplianceCategoryID(int) 

    /**
     * Sets the value of field 'dateEnrolled'.
     * 
     * @param dateEnrolled the value of field 'dateEnrolled'.
    **/
    public void setDateEnrolled(java.util.Date dateEnrolled)
    {
        this._dateEnrolled = dateEnrolled;
    } //-- void setDateEnrolled(java.util.Date) 

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
     * Sets the value of field 'programID'.
     * 
     * @param programID the value of field 'programID'.
    **/
    public void setProgramID(int programID)
    {
        this._programID = programID;
        this._has_programID = true;
    } //-- void setProgramID(int) 

    /**
     * Sets the value of field 'programName'.
     * 
     * @param programName the value of field 'programName'.
    **/
    public void setProgramName(java.lang.String programName)
    {
        this._programName = programName;
    } //-- void setProgramName(java.lang.String) 

    /**
     * Sets the value of field 'starsLMControlHistory'.
     * 
     * @param starsLMControlHistory the value of field
     * 'starsLMControlHistory'.
    **/
    public void setStarsLMControlHistory(StarsLMControlHistory starsLMControlHistory)
    {
        this._starsLMControlHistory = starsLMControlHistory;
    } //-- void setStarsLMControlHistory(StarsLMControlHistory) 

    /**
     * Sets the value of field 'status'.
     * 
     * @param status the value of field 'status'.
    **/
    public void setStatus(java.lang.String status)
    {
        this._status = status;
    } //-- void setStatus(java.lang.String) 

}

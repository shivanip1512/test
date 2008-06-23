package com.cannontech.stars.xml.serialize;

public class StarsGetLMControlHistory {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _groupID;

    /**
     * keeps track of state for field: _groupID
    **/
    private boolean _has_groupID;

    private com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod _period;

    private boolean _getSummary;

    /**
     * keeps track of state for field: _getSummary
    **/
    private boolean _has_getSummary;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetLMControlHistory() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsGetLMControlHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteGetSummary()
    {
        this._has_getSummary= false;
    } //-- void deleteGetSummary() 

    /**
     * Returns the value of field 'getSummary'.
     * 
     * @return the value of field 'getSummary'.
    **/
    public boolean getGetSummary()
    {
        return this._getSummary;
    } //-- boolean getGetSummary() 

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
     * Returns the value of field 'period'.
     * 
     * @return the value of field 'period'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod getPeriod()
    {
        return this._period;
    } //-- com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod getPeriod() 

    /**
    **/
    public boolean hasGetSummary()
    {
        return this._has_getSummary;
    } //-- boolean hasGetSummary() 

    /**
    **/
    public boolean hasGroupID()
    {
        return this._has_groupID;
    } //-- boolean hasGroupID() 

    /**
     * Sets the value of field 'getSummary'.
     * 
     * @param getSummary the value of field 'getSummary'.
    **/
    public void setGetSummary(boolean getSummary)
    {
        this._getSummary = getSummary;
        this._has_getSummary = true;
    } //-- void setGetSummary(boolean) 

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
     * Sets the value of field 'period'.
     * 
     * @param period the value of field 'period'.
    **/
    public void setPeriod(com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod period)
    {
        this._period = period;
    } //-- void setPeriod(com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod) 

}

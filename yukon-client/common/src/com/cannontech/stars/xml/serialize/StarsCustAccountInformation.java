package com.cannontech.stars.xml.serialize;

import com.cannontech.database.data.lite.LiteYukonUser;

public class StarsCustAccountInformation {
    private java.util.Date _lastActiveTime;
    private StarsCustomerAccount _starsCustomerAccount;
    private StarsResidenceInformation _starsResidenceInformation;
    private StarsLMPrograms _starsLMPrograms;
    private StarsAppliances _starsAppliances;
    private StarsAppliances _unassignedStarsAppliances;
    private StarsInventories _starsInventories;
    private StarsCallReportHistory _starsCallReportHistory;
    private StarsServiceRequestHistory _starsServiceRequestHistory;
    private LiteYukonUser _starsUser;

    public StarsCustAccountInformation() {
        
    }

    public java.util.Date getLastActiveTime() {
        return this._lastActiveTime;
    } 

    public StarsAppliances getStarsAppliances() {
        return this._starsAppliances;
    }

    public StarsAppliances getUnassignedStarsAppliances() {
        return this._unassignedStarsAppliances;
    }

    public StarsCallReportHistory getStarsCallReportHistory() {
        return this._starsCallReportHistory;
    } 

    public StarsCustomerAccount getStarsCustomerAccount() {
        return this._starsCustomerAccount;
    }

    public StarsInventories getStarsInventories() {
        return this._starsInventories;
    }

    /**
     * Returns the value of field 'starsLMPrograms'.
     * 
     * @return the value of field 'starsLMPrograms'.
    **/
    public StarsLMPrograms getStarsLMPrograms()
    {
        return this._starsLMPrograms;
    } //-- StarsLMPrograms getStarsLMPrograms() 

    /**
     * Returns the value of field 'starsResidenceInformation'.
     * 
     * @return the value of field 'starsResidenceInformation'.
    **/
    public StarsResidenceInformation getStarsResidenceInformation()
    {
        return this._starsResidenceInformation;
    } //-- StarsResidenceInformation getStarsResidenceInformation() 

    /**
     * Returns the value of field 'starsServiceRequestHistory'.
     * 
     * @return the value of field 'starsServiceRequestHistory'.
    **/
    public StarsServiceRequestHistory getStarsServiceRequestHistory()
    {
        return this._starsServiceRequestHistory;
    } //-- StarsServiceRequestHistory getStarsServiceRequestHistory() 

    /**
     * Returns the value of field 'starsUser'.
     * 
     * @return the value of field 'starsUser'.
    **/
    public LiteYukonUser getStarsUser()
    {
        return this._starsUser;
    } //-- StarsUser getStarsUser() 

    public void setLastActiveTime(java.util.Date lastActiveTime)
    {
        this._lastActiveTime = lastActiveTime;
    } //-- void setLastActiveTime(java.util.Date) 

    /**
     * Sets the value of field 'starsAppliances'.
     * 
     * @param starsAppliances the value of field 'starsAppliances'.
    **/
    public void setStarsAppliances(StarsAppliances starsAppliances)
    {
        this._starsAppliances = starsAppliances;
    } //-- void setStarsAppliances(StarsAppliances) 

    /**
     * Sets the value of field 'unassignedStarsAppliances'.
     * 
     * @param unassignedStarsAppliances the value of field 'unassignedStarsAppliances'.
    **/
    public void setUnassignedStarsAppliances(StarsAppliances unassignedStarsAppliances)
    {
        this._unassignedStarsAppliances = unassignedStarsAppliances;
    } //-- void setUnassignedStarsAppliances(StarsAppliances) 

    /**
     * Sets the value of field 'starsCallReportHistory'.
     * 
     * @param starsCallReportHistory the value of field
     * 'starsCallReportHistory'.
    **/
    public void setStarsCallReportHistory(StarsCallReportHistory starsCallReportHistory)
    {
        this._starsCallReportHistory = starsCallReportHistory;
    } //-- void setStarsCallReportHistory(StarsCallReportHistory) 

    /**
     * Sets the value of field 'starsCustomerAccount'.
     * 
     * @param starsCustomerAccount the value of field
     * 'starsCustomerAccount'.
    **/
    public void setStarsCustomerAccount(StarsCustomerAccount starsCustomerAccount)
    {
        this._starsCustomerAccount = starsCustomerAccount;
    } //-- void setStarsCustomerAccount(StarsCustomerAccount) 

    /**
     * Sets the value of field 'starsInventories'.
     * 
     * @param starsInventories the value of field 'starsInventories'
    **/
    public void setStarsInventories(StarsInventories starsInventories)
    {
        this._starsInventories = starsInventories;
    } //-- void setStarsInventories(StarsInventories) 

    /**
     * Sets the value of field 'starsLMPrograms'.
     * 
     * @param starsLMPrograms the value of field 'starsLMPrograms'.
    **/
    public void setStarsLMPrograms(StarsLMPrograms starsLMPrograms)
    {
        this._starsLMPrograms = starsLMPrograms;
    } //-- void setStarsLMPrograms(StarsLMPrograms) 

    /**
     * Sets the value of field 'starsResidenceInformation'.
     * 
     * @param starsResidenceInformation the value of field
     * 'starsResidenceInformation'.
    **/
    public void setStarsResidenceInformation(StarsResidenceInformation starsResidenceInformation)
    {
        this._starsResidenceInformation = starsResidenceInformation;
    } //-- void setStarsResidenceInformation(StarsResidenceInformation) 

    /**
     * Sets the value of field 'starsServiceRequestHistory'.
     * 
     * @param starsServiceRequestHistory the value of field
     * 'starsServiceRequestHistory'.
    **/
    public void setStarsServiceRequestHistory(StarsServiceRequestHistory starsServiceRequestHistory) {
        this._starsServiceRequestHistory = starsServiceRequestHistory;
    } //-- void setStarsServiceRequestHistory(StarsServiceRequestHistory) 

    /**
     * Sets the value of field 'starsUser'.
     * 
     * @param starsUser the value of field 'starsUser'.
    **/
    public void setStarsUser(LiteYukonUser starsUser) {
        this._starsUser = starsUser;
    } //-- void setStarsUser(StarsUser) 

}

/*
 * Created on May 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.data.device.capcontrol;

import java.util.Date;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CapControlActivityData
{
    public Date dateTime = null;
    public Integer subID = null;
    public Integer feederID = null;
    public Integer capBankID = null;
    public Integer bankSize = null;
    public Integer cbcID = null;
    public String action = null;
    public String desc = null;
    
    /**
     * @param dateTime
     * @param subID
     * @param feederID
     * @param capBankID
     * @param bankSize
     * @param cbcID
     * @param action
     * @param desc
     */
    public CapControlActivityData(Date dateTime, Integer subID, Integer feederID, Integer capBankID, Integer bankSize, Integer cbcID, String action, String desc) {
        super();
        this.dateTime = dateTime;
        this.subID = subID;
        this.feederID = feederID;
        this.capBankID = capBankID;
        this.bankSize = bankSize;
        this.cbcID = cbcID;
        this.action = action;
        this.desc = desc;
    }
    /**
     * @return Returns the action.
     */
    public String getAction()
    {
        return action;
    }
    /**
     * @return Returns the capBankID.
     */
    public Integer getCapBankID()
    {
        return capBankID;
    }
    /**
     * @return Returns the cbcID.
     */
    public Integer getCbcID()
    {
        return cbcID;
    }
    /**
     * @return Returns the dateTime.
     */
    public Date getDateTime()
    {
        return dateTime;
    }
    /**
     * @return Returns the desc.
     */
    public String getDesc()
    {
        return desc;
    }
    /**
     * @return Returns the feederID.
     */
    public Integer getFeederID()
    {
        return feederID;
    }
    /**
     * @return Returns the subID.
     */
    public Integer getSubID()
    {
        return subID;
    }
    /**
     * @return Returns the bankSize.
     */
    public Integer getBankSize()
    {
        return bankSize;
    }
}

/*
 * Created on Nov 11, 2005
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.data.device.capcontrol;

import java.util.Date;

/**
 * @author stacey
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CapControlStatusData
{
    private Integer subBusPaoID = null;
    private String subBusName;
    private Integer capBankPaoID = null;
    private String bankName;
    private Integer feederPaoID = null;
    private String feederName;
    private Integer controlStatus = null;
    private Date changeDateTime = null;
    private Integer controlOrder = null;
    private String operationalState = null;
    private String capBankAddress = null;
    private String capBankDriveDir = null;
    private String disableFlag = null;
    private String userName = null;
    private String userComment = null;
    private String pointName = null;
    private String eventText = null;
    private Integer eventType = null; // CCEventLog

    /**
	 * 
	 */
    public CapControlStatusData() {
        super();
    }

    /**
     * Constructor used for CapControlCurrentStatus
     * @param capBankPaoID
     * @param subBusPaoID
     * @param feederPaoID
     * @param controlStatus
     * @param changeDateTime
     * @param controlOrder
     */
    public CapControlStatusData(Integer capBankPaoID, String bankName, Integer subBusPaoID,
                                String busName, Integer feederPaoID, String feederName,
                                Integer controlStatus, Date changeDateTime,
                                String operationalState, String capAddress, String capDriveDir,
                                String disableFlag, String userName, String userComment,
                                Integer controlOrder) {
        this.capBankPaoID = capBankPaoID;
        this.bankName = bankName;
        this.subBusPaoID = subBusPaoID;
        this.subBusName = busName;
        this.feederName = feederName;
        this.feederPaoID = feederPaoID;
        this.controlStatus = controlStatus;
        this.changeDateTime = changeDateTime;
        this.operationalState = operationalState;
        this.capBankAddress = capAddress;
        this.capBankDriveDir = capDriveDir;
        this.disableFlag = disableFlag;
        this.userName = userName;
        this.userComment = userComment;

        this.controlOrder = controlOrder;
    }

    /**
     * Constructor used for CapControlEventLog
     * @param capBankPaoID
     * @param subBusPaoID
     * @param feederPaoID
     * @param pointName
     * @param changeDateTime
     * @param eventText
     * @param controlStatus
     */
    public CapControlStatusData(Integer capBankPaoID, String bankName, Integer subBusPaoID,
                                String busName, Integer feederPaoID, String feederName,
                                String pointName, Date changeDateTime, String eventText,
                                Integer controlStatus, Integer eventType) {
        this.capBankPaoID = capBankPaoID;
        this.bankName = bankName;
        this.subBusPaoID = subBusPaoID;
        this.subBusName = busName;
        this.feederName = feederName;
        this.feederPaoID = feederPaoID;
        this.pointName = pointName;
        this.changeDateTime = changeDateTime;
        this.eventText = eventText;
        this.controlStatus = controlStatus;
        this.eventType = eventType;

    }

    public Integer getCapBankPaoID()
    {
        return capBankPaoID;
    }

    public Integer getControlStatus()
    {
        return controlStatus;
    }

    public Integer getFeederPaoID()
    {
        return feederPaoID;
    }

    public Date getChangeDateTime()
    {
        return changeDateTime;
    }

    public Integer getSubBusPaoID()
    {
        return subBusPaoID;
    }

    public String getOperationalState()
    {
        return operationalState;
    }

    public String getDisableFlag()
    {
        return disableFlag;
    }

    /**
     * @return
     */
    public Integer getControlOrder()
    {
        return controlOrder;
    }

    public String getPointName() {
        return pointName;
    }

    public String getEventText() {
        return eventText;
    }

    public Integer getEventType() {
        return eventType;
    }

    public String getCapBankAddress() {
        return capBankAddress;
    }

    public void setCapBankAddress(String capBankAddress) {
        this.capBankAddress = capBankAddress;
    }

    public String getCapBankDriveDir() {
        return capBankDriveDir;
    }

    public void setCapBankDriveDir(String capBankDriveDir) {
        this.capBankDriveDir = capBankDriveDir;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getSubBusName() {
        return subBusName;
    }

    public void setSubBusName(String subBusName) {
        this.subBusName = subBusName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getFeederName() {
        return feederName;
    }

    public void setFeederName(String feederName) {
        this.feederName = feederName;
    }

}
/*
 * Created on Nov 11, 2005
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
public class CapControlStatusData
{
	private Integer subBusPaoID = null;
	private Integer capBankPaoID = null;
	private Integer feederPaoID = null;
	private Integer controlStatus = null;
	private Date changeDateTime = null;
	private Integer controlOrder = null;
    private String operationalState = null;
    private String disableFlag = null;
	private String pointName = null;
	private String eventText = null;
	private Integer eventType = null;	//CCEventLog
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
	public CapControlStatusData(Integer capBankPaoID, Integer subBusPaoID, Integer feederPaoID, Integer controlStatus, Date changeDateTime, String operationalState, String disableFlag, Integer controlOrder) {
		super();
		this.capBankPaoID = capBankPaoID;
		this.subBusPaoID = subBusPaoID;
		this.feederPaoID = feederPaoID;
		this.controlStatus = controlStatus;
		this.changeDateTime = changeDateTime;
        this.operationalState = operationalState;
        this.disableFlag = disableFlag;
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
	public CapControlStatusData(Integer capBankPaoID, Integer subBusPaoID, Integer feederPaoID, String pointName, Date changeDateTime, String eventText, Integer controlStatus, Integer eventType) {
		super();
		this.capBankPaoID = capBankPaoID;
		this.subBusPaoID = subBusPaoID;
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

}
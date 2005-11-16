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
	private Integer capBankPaoID = null;
	private Integer subBusPaoID = null;
	private Integer feederPaoID = null;
	private Integer controlStatus = null;
	private Date lastChangeDateTime = null;
	private Integer controlOrder = null;
	/**
	 * 
	 */
	public CapControlStatusData() {
		super();
	}
	/**
	 * @param capBankPaoID
	 * @param subBusPaoID
	 * @param feederPaoID
	 * @param controlStatus
	 * @param lastChangeDateTime
	 */
	public CapControlStatusData(Integer capBankPaoID, Integer subBusPaoID, Integer feederPaoID, Integer controlStatus, Date lastChangeDateTime, Integer controlOrder) {
		super();
		this.capBankPaoID = capBankPaoID;
		this.subBusPaoID = subBusPaoID;
		this.feederPaoID = feederPaoID;
		this.controlStatus = controlStatus;
		this.lastChangeDateTime = lastChangeDateTime;
		this.controlOrder = controlOrder;
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
	public Date getLastChangeDateTime()
	{
		return lastChangeDateTime;
	}
	public Integer getSubBusPaoID()
	{
		return subBusPaoID;
	}
	/**
	 * @return
	 */
	public Integer getControlOrder()
	{
		return controlOrder;
	}

}
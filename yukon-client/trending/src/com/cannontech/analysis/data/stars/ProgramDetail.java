package com.cannontech.analysis.data.stars;

import com.cannontech.common.constants.YukonListEntryTypes;



/**
 * Created on Dec 15, 2003
 * SystemLogModel TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  Integer pointID 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class ProgramDetail
{	
	private String programName = null;
	private Integer custID = null;	//used to get the primary contact
	private String acctNumber = null;
	private Integer acctID = null;
	private Integer action = null;
	private String status = null;
	private String ecName = null;
	
	/**
	 * Default Constructor
	 */
	public ProgramDetail(String ecName_, String progName_, Integer custID_, String acctNum_, Integer acctID_, Integer action_)
	{
		super();
		ecName = ecName_;
		programName = progName_;
		custID = custID_;
		acctNumber = acctNum_;
		acctID = acctID_;
		action = action_;
	}

	/**
	 * @return
	 */
	public String getAcctNumber()
	{
		return acctNumber;
	}

	/**
	 * @return
	 */
	public Integer getCustID()
	{
		return custID;
	}

	/**
	 * @return
	 */
	public String getECName()
	{
		return ecName;
	}

	/**
	 * @return
	 */
	public Integer getAcctID()
	{
		return acctID;
	}

	/**
	 * @return
	 */
	public String getEcName()
	{
		return ecName;
	}

	/**
	 * @return
	 */
	public String getProgramName()
	{
		return programName;
	}

	/**
	 * @return
	 */
	public String getStatus()
	{
		if( status == null)
			status = "Not Enrolled";	//default init
			
		if (getAction().intValue() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED ||
			getAction().intValue() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP)
		{
			status = "In service";
		}
		else if (getAction().intValue() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION)
			status = "Out of service";
		else if (getAction().intValue() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)
			status = "Terminated";

		return status;
	}

	/**
	 * @return
	 */
	public Integer getAction()
	{
		return action;
	}

	/**
	 * @param integer
	 */
	public void setAction(Integer integer)
	{
		action = integer;
	}

	/**
	 * @param string
	 */
	public void setStatus(String string)
	{
		status = string;
	}

}

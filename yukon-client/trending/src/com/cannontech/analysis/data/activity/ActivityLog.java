package com.cannontech.analysis.data.activity;

import java.util.Date;


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
public class ActivityLog
{	
	private Integer ecID = null;
	private Integer userID = null;
	private Integer custID = null;	//used to get the primary contact
	private String acctNumber = null;
	private Integer acctID = null;
	private Integer actionCount = new Integer(0);			
//	private Date dateTime = null;
	private String action = null;
//	private String description = null;
	
	/**
	 * Default Constructor
	 */
	public ActivityLog(Integer ecID_, Integer userID_, Integer custID_, String acctNum_, Integer acctID_, Integer actionCount_, String action_)
	{
		super();
		ecID = ecID_;
		userID = userID_;
		custID = custID_;
		acctNumber = acctNum_;
		acctID = acctID_;
		actionCount = actionCount_;
		action = action_;
	}
	/**
	 * Default Constructor
	 */
	public ActivityLog(Integer ecID_, Integer userID_, Integer custID_, Integer acctID_, Integer actionCount_, 
						Date dateTime_, String action_, String description_)
	{
		super();
		ecID = ecID_;
		userID = userID_;
		custID = custID_;
		actionCount = actionCount_;
//		dateTime = dateTime_;
		action = action_;
//		description = description_;		
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
	public String getAction()
	{
		return action;
	}

	/**
	 * @return
	 */
	public Integer getActionCount()
	{
		return actionCount;
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
	public Integer getECID()
	{
		return ecID;
	}

	/**
	 * @return
	 */
	public Integer getUserID()
	{
		return userID;
	}

	/**
	 * @return
	 */
	public Integer getAcctID()
	{
		return acctID;
	}

}

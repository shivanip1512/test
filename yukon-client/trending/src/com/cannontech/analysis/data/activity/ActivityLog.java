package com.cannontech.analysis.data.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


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
//	private Integer ecID = null;
//	private Integer userID = null;
	private String userName = null;
	private Integer custID = null;	//used to get the primary contact
	private String acctNumber = null;
	private Integer acctID = null;
	private Integer actionCount = new Integer(0);			
	private Date dateTime = null;
	private String action = null;
	private String description = null;

	private String ecName = null;
	/**
	 * Default Constructor
	 */
	public ActivityLog(String ecName_, String userName_, Integer custID_, String acctNum_, Integer acctID_, Integer actionCount_, String action_)
	{
		super();
		ecName = ecName_;
		userName = userName_;
		custID = custID_;
		acctNumber = acctNum_;
		acctID = acctID_;
		actionCount = actionCount_;
		action = action_;
	}
	/**
	 * Default Constructor
	 */
	public ActivityLog(String ecName_, String userName_, Integer custID_, Integer acctID_, String acctNum_,
						Date dateTime_, String action_, String description_)
	{
		super();
		ecName = ecName_;
		userName = userName_;
		custID = custID_;
		acctID = acctID_;
		acctNumber = acctNum_;
		dateTime = dateTime_;
		action = action_;
		description = description_;		
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
	public String getECName()
	{
		return ecName;
	}

	/**
	 * @return
	 */
	public String getUserName()
	{
		return userName;
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
	public Date getDateTime()
	{
		return dateTime;
	}

	/**
	 * @return
	 */
	public Date getDateOnly()
	{
		GregorianCalendar zeroedDate = new GregorianCalendar();
		zeroedDate.setTime((Date)dateTime.clone());
		zeroedDate.set(Calendar.HOUR_OF_DAY, 0);
		zeroedDate.set(Calendar.MINUTE, 0);
		zeroedDate.set(Calendar.SECOND, 0);
		zeroedDate.set(Calendar.MILLISECOND, 0);
		return zeroedDate.getTime();
	}


	/**
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

}

package com.cannontech.export.record;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class IONEventLogRecord implements RecordBase
{
	private String node = null;			//ION Meter Name
	private String nLog = null;			//SystemLog.logid	//UNKNOWN
	private String timeStamp = null;		//SystemLog.dateTime
	private Integer priority = null;			//SystemLog.description.ION_Pri
	private Integer recordID = null;			//SystemLog.action.Record
	private String replacement = null;	//UNKNOWN
	private String cause_ion = null;		//SystemLog.action.ION_Cause_Handle
	private String cause_value = null;	//SystemLog.description.ION_Cause
	private String effect_ion = null;		//SystemLog.action.ION_Effect_Handle
	private String effect_value = null;	//SystemLog.description.ION_Effect
	private String ackTime = null;		//Current date and time
	private String userName = "yukon";		//Static value
	private String remark = null;			//UNKNOWN
	
	private final java.text.SimpleDateFormat DATE_TIME_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Constructor for IONEventLogRecord.
	 */
	public IONEventLogRecord()
	{
		super();
	}

	/**
	 * Constructor for IONEventLogRecord.
	 */
	public IONEventLogRecord(String node, String nLog, java.util.Date timestamp, Integer priority, Integer recordID, String replacement, 
								String cause_ion, String cause_value, String effect_ion, String effect_value)
	{
		super();
		setNode(node);
		setNLog(nLog);
		setTimeStamp(timestamp);
		setPriority(priority);
		setRecordID(recordID);
		setReplacement(replacement);
		setCause_ion(cause_ion);
		setCause_value(cause_value);
		setEffect_ion(effect_ion);
		setEffect_value(effect_value);
	}

	/**
	 * @see com.cannontech.export.record.RecordBase#dataToString()
	 */
	public String dataToString()
	{
		StringBuffer dataString = new StringBuffer();
		
		dataString.append(getNode() + ",");
		
		dataString.append(getNLog() + ",");
		
		dataString.append(getTimeStamp() + ",");
		
		dataString.append(getPriority() + ",");

		dataString.append(getRecordID() + ",");
		
		dataString.append(getReplacement() + ",");
		
		dataString.append(getCause_ion() + ",");
		
		dataString.append(getCause_value() + ",");
		
		dataString.append(getEffect_ion() + ",");
		
		dataString.append(getEffect_value() + ",");

		dataString.append(getAckTime() + ",");

		dataString.append(getUserName() + ",");
		
		dataString.append(getRemark());
		
		dataString.append("\r\n");
		
		return dataString.toString();
	}

	/**
	 * Returns the ackTime.
	 * @return String
	 */
	public String getAckTime()
	{
		if( ackTime == null)
		{
			java.util.Date d = new java.util.Date();
			ackTime = getDATE_TIME_FORMAT().format(d);
		}
		return ackTime;
	}

	/**
	 * Returns the cause_ion.
	 * @return String
	 */
	public String getCause_ion()
	{
		if( cause_ion == null)
			cause_ion = new String();
		return cause_ion;
	}

	/**
	 * Returns the cause_value.
	 * @return String
	 */
	public String getCause_value()
	{
		if( cause_value == null)
			cause_value = new String();
		return cause_value;
	}

	/**
	 * Returns the effect_ion.
	 * @return String
	 */
	public String getEffect_ion()
	{
		if( effect_ion == null)
			effect_ion = new String();
		return effect_ion;
	}

	/**
	 * Returns the effect_value.
	 * @return String
	 */
	public String getEffect_value()
	{
		if( effect_value == null)
			effect_value = new String();
		return effect_value;
	}

	/**
	 * Returns the nLog.
	 * @return String
	 */
	public String getNLog()
	{
		if( nLog == null)
			nLog = new String();
		return nLog;
	}

	/**
	 * Returns the node.
	 * @return String
	 */
	public String getNode()
	{
		return node;
	}

	/**
	 * Returns the priority.
	 * @return Integer
	 */
	public Integer getPriority()
	{
		return priority;
	}

	/**
	 * Returns the recordID.
	 * @return Integer
	 */
	public Integer getRecordID()
	{
		return recordID;
	}

	/**
	 * Returns the remark.
	 * @return String
	 */
	public String getRemark()
	{
		if( remark == null)
			remark = new String();
		return remark;
	}

	/**
	 * Returns the replacement.
	 * @return String
	 */
	public String getReplacement()
	{
		if( replacement == null)
			replacement = new String();
		return replacement;
	}

	/**
	 * Returns the timeStamp.
	 * @return String
	 */
	public String getTimeStamp()
	{
		return timeStamp;
	}

	/**
	 * Returns the userName.
	 * @return String
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the ackTime.
	 * @param ackTime The ackTime to set
	 */
	public void setAckTime(java.util.Date date)
	{
		this.ackTime = getDATE_TIME_FORMAT().format(date);
	}

	/**
	 * Sets the cause_ion.
	 * @param cause_ion The cause_ion to set
	 */
	public void setCause_ion(String cause_ion)
	{
		this.cause_ion = cause_ion;
	}

	/**
	 * Sets the cause_value.
	 * @param cause_value The cause_value to set
	 */
	public void setCause_value(String cause_value)
	{
		this.cause_value = cause_value;
	}

	/**
	 * Sets the effect_ion.
	 * @param effect_ion The effect_ion to set
	 */
	public void setEffect_ion(String effect_ion)
	{
		this.effect_ion = effect_ion;
	}

	/**
	 * Sets the effect_value.
	 * @param effect_value The effect_value to set
	 */
	public void setEffect_value(String effect_value)
	{
		this.effect_value = effect_value;
	}

	/**
	 * Sets the nLog.
	 * @param nLog The nLog to set
	 */
	public void setNLog(String nLog)
	{
		this.nLog = nLog;
	}

	/**
	 * Sets the node.
	 * @param node The node to set
	 */
	public void setNode(String node)
	{
		this.node = node;
	}

	/**
	 * Sets the priority.
	 * @param priority The priority to set
	 */
	public void setPriority(Integer priority)
	{
		this.priority = priority;
	}

	/**
	 * Sets the recordID.
	 * @param recordID The recordID to set
	 */
	public void setRecordID(Integer recordID)
	{
		this.recordID = recordID;
	}

	/**
	 * Sets the remark.
	 * @param remark The remark to set
	 */
	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	/**
	 * Sets the replacement.
	 * @param replacement The replacement to set
	 */
	public void setReplacement(String replacement)
	{
		this.replacement = replacement;
	}

	/**
	 * Sets the timeStamp.
	 * @param timeStamp The timeStamp to set
	 */
	public void setTimeStamp(java.util.Date date)
	{
		timeStamp = getDATE_TIME_FORMAT().format(date);
	}

	/**
	 * Sets the userName.
	 * @param userName The userName to set
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Returns the dATE_TIME_FORMAT.
	 * @return java.text.SimpleDateFormat
	 */
	public java.text.SimpleDateFormat getDATE_TIME_FORMAT()
	{
		return DATE_TIME_FORMAT;
	}

}

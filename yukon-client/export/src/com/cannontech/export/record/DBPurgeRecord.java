package com.cannontech.export.record;

/**
 * Insert the type's description here.
 * Creation date: (3/21/2002 10:14:52 AM)
 * @author: 
 */
public class DBPurgeRecord implements RecordBase
{
	private String logText = null;		//140
	private Integer logId = null;				//7
	private java.util.GregorianCalendar dateTime = null;
	private Integer origType = null;
	private String originator = null;	//18
	private String logType = null;		//12
	private String laorMask = null;		//6
	private Integer severity = null;	//4
	private String alarmMask = null;	//6
	private Integer idAlarm = null;

	private static java.text.SimpleDateFormat TIME_DATE_FORMAT = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
	
	/**
	 * DBPurgeRecord constructor comment.
	 */
	public DBPurgeRecord()
	{
		super();
	}
	/**
	 * DBPurgeRecord constructor comment.
	 */
	public DBPurgeRecord(String wLogText, Integer wLogId, java.util.GregorianCalendar wDateTime, Integer wOrigType, String wOriginator, String wLogType, String wLaorMask, Integer wSeverity, String wAlarmMask, Integer wIdAlarm)
	{
		super();
		setLogText(wLogText);
		setLogId(wLogId);
		setDateTime(wDateTime);
		setOrigType(wOrigType);
		setOriginator(wOriginator);
		setLogType(wLogType);
		setSeverity(wSeverity);
		setAlarmMask(wAlarmMask);
		setIdAlarm(wIdAlarm);
	}
	/**
	 * DBPurgeRecord  constructor comment.
	 */
	public DBPurgeRecord(String wLogText, Integer wLogId, java.util.GregorianCalendar wDateTime, String wOriginator, String wLogType, Integer wSeverity)
	{
		super();
		setLogText(wLogText);
		setLogId(wLogId);
		setDateTime(wDateTime);
		setOriginator(wOriginator);
		setLogType(wLogType);
		setSeverity(wSeverity);
	}
	
	/**
	 * @see com.cannontech.export.record.RecordBase#dataToString()
	 * @return java.lang.String
	 * @param record com.cannontech.export.DBPurgeRecord
	 */
	public String dataToString()
	{
		String dataString = new String("\"" + getLogText() + "\", ");
		
		dataString += getLogId() + ", ";
		
		dataString += "\"" + TIME_DATE_FORMAT.format(new java.util.Date(getDateTime().getTime().getTime())) + "\", ";
	
		if( getOrigType() == null)
			dataString += ", ";
		else
			dataString += getOrigType() + ", ";
	
		dataString += "\"" + getOriginator() + "\", ";
	
		dataString += "\"" + getLogType() + "\", ";
	
		if( getLaorMask() == null)
			dataString += ", ";
		else
			dataString += getLaorMask() + ", ";
	
		dataString += getServerity() + ", ";
	
		if( getAlarmMask() == null)
			dataString += ", ";
		else
			dataString += getAlarmMask() + ", ";
	
		dataString += getIdAlarm() + "\r\n";
	
		return dataString;
	}
	
	/**
	 * Returns the alarmMask.
	 * @return String
	 */
	public String getAlarmMask()
	{
		//This field is never imported into DBPurge's archive system
		// so we can just leave it blank (a.k.a. return a null;
		if( alarmMask == null)
			return null;
		return alarmMask;
	}
	
	/**
	 * Returns the dateTime.
	 * @return GregorianCalendar
	 */
	public java.util.GregorianCalendar getDateTime()
	{
		return dateTime;
	}
	/**
	 * Returns the idAlarm.
	 * This field is never imported into DBPurge's archive system, leave it blank (a.k.a. return a null)
	 * @return Integer
	 */
	public Integer getIdAlarm()
	{
		if( idAlarm == null)
			return new Integer(0);
		return idAlarm;
	}
	
	/**
	 * Returns the laorMask.
	 * This field is never imported into DBPurge's archive system, leave it blank (a.k.a. return a null)
	 * @return String
	 */
	public String getLaorMask()
	{
		if( laorMask == null)
			return null;
		return laorMask;
	}
	
	/**
	 * Returns the logID.
	 * @return Integer
	 */
	public Integer getLogId()
	{
		return logId;
	}
	
	/**
	 * Returns the logText.
	 * @return String
	 */
	public String getLogText()
	{
		return logText;
	}
	
	/**
	 * Returns the logType.
	 * If logType is null, set using 12 blanks("            ").
	 * @return String
	 */
	public String getLogType()
	{
		if( logType == null)
			logType = new String ("            ");
		return logType;
	}
	
	/**
	 * Returns the originator.
	 * If originator is null, set using final String "Yukon").
	 * @return String
	 */
	public String getOriginator()
	{
		if( originator == null)
			originator = new String("Yukon");
		return originator;
	}
	
	/**
	 * Returns the origType.
	 * This field is never imported into DBPurge's archive system, leave it blank (a.k.a. return a null)
	 * @return Integer
	 */
	public Integer getOrigType()
	{
		if( origType == null)
			return null;
		return origType;
	}

	/**
	 * Returns the severity.
	 * If severity is null, set using default Integer(99).
	 * @return Integer
	 */
	public Integer getServerity()
	{
		if( severity == null)
			severity = new Integer(99);
		return severity;
	}
	
	/**
	 * Sets the alarmMask.
	 * @param newAlarmMask java.lang.String
	 */
	public void setAlarmMask(String newAlarmMask)
	{
		alarmMask = newAlarmMask;
	}
	
	/**
	 * Sets the dateTime.
	 * @param newDateTime java.util.GregorianCalendar
	 */
	public void setDateTime(java.util.GregorianCalendar newDateTime)
	{
		dateTime = newDateTime;
	}
	
	/**
	 * Sets the idAlarm.
	 * @param newIdAlarm java.lang.Integer
	 */
	public void setIdAlarm(Integer newIdAlarm)
	{
		idAlarm = newIdAlarm;
	}
	
	/**
	 * Sets the laorMask.
	 * @param newLaorMask java.lang.String
	 */
	public void setLaorMask(String newLaorMask)
	{
		laorMask = newLaorMask;
	}
	
	/**
	 * Sets the logId.
	 * @param newLogId java.lang.Integer
	 */
	public void setLogId(Integer newLogId)
	{
		logId = newLogId;
	}
	
	/**
	 * Sets the logText.
	 * @param newLogText java.lang.String
	 */
	public void setLogText(String newLogText)
	{
		if( newLogText.length() > 140)
			logText = newLogText.substring(0, 139);
		else
			logText = newLogText;
	}
	
	/**
	 * Sets the logType.
	 * Truncates newLogType to 12 chars.
	 * @param newLogType java.lang.String
	 */
	public void setLogType(String newLogType)
	{
		if( newLogType.length() > 12)
			logType = newLogType.substring(0, 11);
		else
			logType = newLogType;
	}
	
	/**
	 * Sets the originator.
	 * Truncates newOriginator to 18 chars.
	 * @param newOriginator java.lang.String
	 */
	public void setOriginator(String newOriginator)
	{
		if( newOriginator.length() > 18)
			originator = newOriginator.substring(0, 17);
		else
			originator = newOriginator;
	}
	
	/**
	 * Sets the origType.
	 * @param newOrigType java.lang.Integer
	 */
	public void setOrigType(Integer newOrigType)
	{
		origType = newOrigType;
	}
	
	/**
	 * Sets the severity.
	 * @param newSeverity java.lang.Integer
	 */
	public void setSeverity(Integer newSeverity)
	{
		severity = newSeverity;
	}
}

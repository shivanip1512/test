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
 * DBPurgeFormat constructor comment.
 */
public DBPurgeRecord() {
	super();
}
/**
 * DBPurgeFormat constructor comment.
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
 * DBPurgeFormat constructor comment.
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
 * Insert the method's description here.
 * Creation date: (3/21/2002 1:11:49 PM)
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
public String getAlarmMask()
{
	//This field is never imported into DBPurge's archive system
	// so we can just leave it blank (a.k.a. return a null;
	if( alarmMask == null)
		return null;
	return alarmMask;
}
public java.util.GregorianCalendar getDateTime()
{
	return dateTime;
}
public Integer getIdAlarm()
{
	//This field is never imported into DBPurge's archive system
	// so we can just leave it blank (a.k.a. return a null;
	if( idAlarm == null)
		return new Integer(0);
	return idAlarm;
}
public String getLaorMask()
{
	//This field is never imported into DBPurge's archive system
	// so we can just leave it blank (a.k.a. return a null;
	if( laorMask == null)
		return null;
	return laorMask;
}
public Integer getLogId()
{
	return logId;
}
public String getLogText()
{
	return logText;
}
public String getLogType()
{
	if( logType == null)
		logType = new String ("            ");
	return logType;
}
public String getOriginator()
{
	if( originator == null)
		originator = new String("Yukon");
	return originator;
}
public Integer getOrigType()
{
	//This field is never imported into DBPurge's archive system
	// so we can just leave it blank (a.k.a. return a null;
	if( origType == null)
		return null;
	return origType;
}
public Integer getServerity()
{
	if( severity == null)
		severity = new Integer(99);
	return severity;
}
public void setAlarmMask(String newAlarmMask)
{
	alarmMask = newAlarmMask;
}
public void setDateTime(java.util.GregorianCalendar newDateTime)
{
	dateTime = newDateTime;
}
public void setIdAlarm(Integer newIdAlarm)
{
	idAlarm = newIdAlarm;
}
public void setLaorMask(String newLaorMask)
{
	laorMask = newLaorMask;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2002 10:22:19 AM)
 * @param newLogId int
 */
public void setLogId(Integer newLogId)
{
	logId = newLogId;
}
public void setLogText(String newLogText)
{
	if( newLogText.length() > 140)
		logText = newLogText.substring(0, 139);
	else
		logText = newLogText;
}
public void setLogType(String newLogType)
{
	if( newLogType.length() > 12)
		logType = newLogType.substring(0, 11);
	else
		logType = newLogType;
}
public void setOriginator(String newOriginator)
{
	if( newOriginator.length() > 18)
		originator = newOriginator.substring(0, 17);
	else
		originator = newOriginator;
}
public void setOrigType(Integer newOrigType)
{
	origType = newOrigType;
}
public void setSeverity(Integer newSeverity)
{
	severity = newSeverity;
}
}

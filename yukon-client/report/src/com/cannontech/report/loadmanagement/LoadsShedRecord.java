package com.cannontech.report.loadmanagement;

/**
 * Insert the type's description here.
 * Creation date: (6/28/00 11:55:04 AM)
 * @author: 
 */
import com.cannontech.report.ReportRecordBase;
public class LoadsShedRecord implements ReportRecordBase
{
	private String controlAreaName = null;
	private Integer paobjectId = null;
	private String paoName = null;
	private java.util.GregorianCalendar startDateTime = null;
	private Integer soeTag = null;
	private String controlType = null;
	private Integer currentDailyTime = null;
	private Integer currentMonthlyTime = null;
	private Integer currentSeasonalTime = null;
	private Integer currentAnnualTime = null;
	private String activeRestore = null;
	private Double reductionValue = null;
	private java.util.GregorianCalendar stopDateTime = null;
/**
 * CADPFormat constructor comment.
 */
public LoadsShedRecord()
{
	super();
}
/**
 * CADPFormat constructor comment.
 */
public LoadsShedRecord(String controlAreaName, Integer paobjectId, String paoName,
											java.util.GregorianCalendar startDateTime, Integer soeTag,
											String controlType, Integer currentDailyTime,
											Integer currentMonthlyTime,	Integer currentSeasonalTime,
											Integer currentAnnualTime,
											String activeRestore, Double reductionValue,
											java.util.GregorianCalendar stopDateTime)
{
	super();
	setControlAreaName(controlAreaName);
	setPaobjectId(paobjectId);
	setPaoName(paoName);
	setStartDateTime(startDateTime);
	setSoeTag(soeTag);
	setControlType(controlType);
	setCurrentDailyTime(currentDailyTime);
	setCurrentMonthlyTime(currentMonthlyTime);
	setCurrentSeasonalTime(currentSeasonalTime);
	setCurrentAnnualTime(currentAnnualTime);
	setActiveRestore(activeRestore);
	setReductionValue(reductionValue);
	setStopDateTime(stopDateTime);
}
/**
 * dataToString method comment.
 */
public String dataToString()
{
	StringBuffer returnBuffer = new StringBuffer();

	java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
	java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("HH:mm:ss");

	returnBuffer.append(dateFormatter.format(getStartDateTime().getTime()));
	returnBuffer.append(" ");

	returnBuffer.append(timeFormatter.format(getStartDateTime().getTime()));
	returnBuffer.append(" ");

	returnBuffer.append(timeFormatter.format(getStopDateTime().getTime()));
	returnBuffer.append("  ");

	if( getControlType().length() <= 15 )
	{
		returnBuffer.append(getControlType());
	}
	else
	{
		returnBuffer.append(getControlType().substring(0,15));
	}
	for(int i=0;i<(15-getControlType().length());i++)
	{
		returnBuffer.append(" ");
	}
	returnBuffer.append(" ");

	if( getActiveRestore().equalsIgnoreCase("T") )
	{
		returnBuffer.append("N");
	}
	else
	{
		returnBuffer.append("Y");
	}
	for(int i=0;i<7;i++)
	{
		returnBuffer.append(" ");
	}

	String tempString = secondsToHourMinuteSecondString( (int)((getStopDateTime().getTime().getTime() - getStartDateTime().getTime().getTime())/1000) );
	for(int i=0;i<(8-tempString.length());i++)
	{
		returnBuffer.append(" ");
	}
	returnBuffer.append(tempString);
	returnBuffer.append("  ");

	tempString = secondsToHourMinuteSecondString(getCurrentDailyTime().intValue());
	for(int i=0;i<(8-tempString.length());i++)
	{
		returnBuffer.append(" ");
	}
	returnBuffer.append(tempString);
	returnBuffer.append(" ");

	tempString = secondsToHourMinuteString(getCurrentMonthlyTime().intValue());
	for(int i=0;i<(7-tempString.length());i++)
	{
		returnBuffer.append(" ");
	}
	returnBuffer.append(tempString);
	returnBuffer.append(" ");

	tempString = secondsToHourMinuteString(getCurrentSeasonalTime().intValue());
	for(int i=0;i<(6-tempString.length());i++)
	{
		returnBuffer.append(" ");
	}
	returnBuffer.append(tempString);
	returnBuffer.append(" ");

	tempString = secondsToHourMinuteString(getCurrentAnnualTime().intValue());
	for(int i=0;i<(6-tempString.length());i++)
	{
		returnBuffer.append(" ");
	}
	returnBuffer.append(tempString);

	return returnBuffer.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.String
 */
public java.lang.String getActiveRestore() {
	return activeRestore;
}
/**
 */
public java.util.Vector getControlAreaHeaderVector()
{
	java.util.Vector returnVector = new java.util.Vector();

	returnVector.add("Printed on: " + dateFormatter.format(new java.util.Date()));
	returnVector.add("Control Summary for Control Area: " + getControlAreaName());

	return returnVector;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 9:49:24 AM)
 * @return java.lang.String
 */
public java.lang.String getControlAreaName() {
	return controlAreaName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.String
 */
public java.lang.String getControlType() {
	return controlType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentAnnualTime() {
	return currentAnnualTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentDailyTime() {
	return currentDailyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentMonthlyTime() {
	return currentMonthlyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentSeasonalTime() {
	return currentSeasonalTime;
}
/**
 */
public java.util.Vector getGroupHeaderVector()
{
	java.util.Vector returnVector = new java.util.Vector();

	returnVector.add("Date       Control  Control   Control         Active  Duration  Daily    Monthly Season Annual");
	returnVector.add("           Started  Stopped   Type            Restore hh:mm:ss  hh:mm:ss   hh:mm  hh:mm  hh:mm");
	returnVector.add("---------- -------- --------  --------------- ------- --------  -------- ------- ------ ------");
	//sample template and ruler
	//returnVector.add("MM/dd/yyyy HH:mm:ss HH:mm:ss  CYCLE 50%       Y       HH:mm:ss  HH:mm:ss   HH:mm  HH:mm  HH:mm");
	//returnVector.add("         1         2         3         4         5         6         7         8         9        10        11");
	//returnVector.add("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

	return returnVector;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPaobjectId() {
	return paobjectId;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.String
 */
public java.lang.String getPaoName() {
	return paoName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.Double
 */
public java.lang.Double getReductionValue() {
	return reductionValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSoeTag() {
	return soeTag;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStartDateTime() {
	return startDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStopDateTime() {
	return stopDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.String
 */
public java.lang.String secondsToHourMinuteSecondString(int secondsControlled)
{
	StringBuffer returnStringBuff = new StringBuffer();

	int tempHours = secondsControlled / 3600;
	int tempMinutes = (secondsControlled - (tempHours * 3600)) / 60;
	int tempSeconds = secondsControlled % 60;

	String hourString = Integer.toString(tempHours);
	for(int i=0;i<(2-hourString.length());i++)
	{
		returnStringBuff.append("0");
	}
	returnStringBuff.append(hourString);
	returnStringBuff.append(":");
	String minuteString = Integer.toString(tempMinutes);
	for(int i=0;i<(2-minuteString.length());i++)
	{
		returnStringBuff.append("0");
	}
	returnStringBuff.append(minuteString);
	returnStringBuff.append(":");
	String secondString = Integer.toString(tempSeconds);
	for(int i=0;i<(2-secondString.length());i++)
	{
		returnStringBuff.append("0");
	}
	returnStringBuff.append(secondString);

	return returnStringBuff.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @return java.lang.String
 */
public java.lang.String secondsToHourMinuteString(int secondsControlled)
{
	StringBuffer returnStringBuff = new StringBuffer();

	int tempHours = secondsControlled / 3600;
	int tempMinutes = (secondsControlled - (tempHours * 3600)) / 60;
	int tempSeconds = secondsControlled % 60;

	String hourString = Integer.toString(tempHours);
	for(int i=0;i<(2-hourString.length());i++)
	{
		returnStringBuff.append("0");
	}
	returnStringBuff.append(hourString);
	returnStringBuff.append(":");
	String minuteString = null;
	if( tempSeconds >= 30 )
	{
		minuteString = Integer.toString(tempMinutes + 1);
	}
	else
	{
		minuteString = Integer.toString(tempMinutes);
	}

	for(int i=0;i<(2-minuteString.length());i++)
	{
		returnStringBuff.append("0");
	}
	returnStringBuff.append(minuteString);

	return returnStringBuff.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newActiveRestore java.lang.String
 */
public void setActiveRestore(java.lang.String newActiveRestore) {
	activeRestore = newActiveRestore;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 9:49:24 AM)
 * @param newControlAreaName java.lang.String
 */
public void setControlAreaName(java.lang.String newControlAreaName) {
	controlAreaName = newControlAreaName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newControlType java.lang.String
 */
public void setControlType(java.lang.String newControlType) {
	controlType = newControlType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newCurrentAnnualTime java.lang.Integer
 */
public void setCurrentAnnualTime(java.lang.Integer newCurrentAnnualTime) {
	currentAnnualTime = newCurrentAnnualTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newCurrentDailyTime java.lang.Integer
 */
public void setCurrentDailyTime(java.lang.Integer newCurrentDailyTime) {
	currentDailyTime = newCurrentDailyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newCurrentMonthlyTime java.lang.Integer
 */
public void setCurrentMonthlyTime(java.lang.Integer newCurrentMonthlyTime) {
	currentMonthlyTime = newCurrentMonthlyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newCurrentSeasonalTime java.lang.Integer
 */
public void setCurrentSeasonalTime(java.lang.Integer newCurrentSeasonalTime) {
	currentSeasonalTime = newCurrentSeasonalTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newPaobjectId java.lang.Integer
 */
public void setPaobjectId(java.lang.Integer newPaobjectId) {
	paobjectId = newPaobjectId;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newPaoName java.lang.String
 */
public void setPaoName(java.lang.String newPaoName) {
	paoName = newPaoName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newReductionValue java.lang.Double
 */
public void setReductionValue(java.lang.Double newReductionValue) {
	reductionValue = newReductionValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newSoeTag java.lang.Integer
 */
public void setSoeTag(java.lang.Integer newSoeTag) {
	soeTag = newSoeTag;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newStartDateTime java.util.GregorianCalendar
 */
public void setStartDateTime(java.util.GregorianCalendar newStartDateTime) {
	startDateTime = newStartDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2002 3:14:17 PM)
 * @param newStopDateTime java.util.GregorianCalendar
 */
public void setStopDateTime(java.util.GregorianCalendar newStopDateTime) {
	stopDateTime = newStopDateTime;
}
}

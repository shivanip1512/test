package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMCurtailCustomer extends LMGroupBase
{
	//possible values for ackStatus
	public static final String ACK_UNACKNOWLEDGED = "UnAcknowledged";
	public static final String ACK_ACKNOWLEDGED = "Acknowledged";
	public static final String ACK_NOT_REQUIRED = "Not Required";
	public static final String ACK_VERBAL = "Verbal";
	
	private String custTimeZone = null;
	private Boolean requireAck = null;
	private int curtailRefID = 0;
	private String ackStatus = null;
	private java.util.Date ackDateTime = null;
	private String ipAddress = null;
	private String userIDname = null;
	private String nameOfAckPerson = null;
	private String curtailmentNotes = null;
	private Boolean ackLateFlag = null;
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return java.util.Date
 */
public java.util.Date getAckDateTime() {
	return ackDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:35:50 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getAckLateFlag() {
	return ackLateFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return java.lang.String
 */
public java.lang.String getAckStatus() {
	return ackStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return java.lang.String
 */
public java.lang.String getCurtailmentNotes() {
	return curtailmentNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return int
 */
public int getCurtailRefID() {
	return curtailRefID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 10:08:06 AM)
 */
public Double getCustomerPDL() 
{
	return getKwCapacity();
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 1:46:12 PM)
 * @return java.lang.String
 */
public java.lang.String getCustTimeZone() {
	return custTimeZone;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 8:56:31 AM)
 * @return java.lang.String
 */
public String getGroupControlStateString()
{
	return getAckStatus();
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return java.lang.String
 */
public java.util.Date getGroupTime()
{
	return getAckDateTime();
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return java.lang.String
 */
public java.lang.String getIpAddress() {
	return ipAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return java.lang.String
 */
public java.lang.String getNameOfAckPerson() {
	return nameOfAckPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 8:39:15 AM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getRequireAck() {
	return requireAck;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 8:56:31 AM)
 * @return java.lang.String
 */
public String getStatistics()
{
	return getUserIDname() + "@" + getIpAddress();
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return java.lang.String
 */
public java.lang.String getUserIDname() {
	return userIDname;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @param newAckDateTime java.util.Date
 */
public void setAckDateTime(java.util.Date newAckDateTime) {
	ackDateTime = newAckDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:35:50 PM)
 * @param newAckLateFlag java.lang.Boolean
 */
public void setAckLateFlag(java.lang.Boolean newAckLateFlag) {
	ackLateFlag = newAckLateFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @param newAckStatus java.lang.String
 */
public void setAckStatus(java.lang.String newAckStatus) {
	ackStatus = newAckStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @param newCurtailmentNotes java.lang.String
 */
public void setCurtailmentNotes(java.lang.String newCurtailmentNotes) {
	curtailmentNotes = newCurtailmentNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @param newCurtailRefID int
 */
public void setCurtailRefID(int newCurtailRefID) {
	curtailRefID = newCurtailRefID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 10:08:06 AM)
 */
public void setCustomerPDL( Double value )
{
	setKwCapacity( value );
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 1:46:12 PM)
 * @param newCustTimeZone java.lang.String
 */
public void setCustTimeZone(java.lang.String newCustTimeZone) {
	custTimeZone = newCustTimeZone;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @param newIpAddress java.lang.String
 */
public void setIpAddress(java.lang.String newIpAddress) {
	ipAddress = newIpAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @param newNameOfAckPerson java.lang.String
 */
public void setNameOfAckPerson(java.lang.String newNameOfAckPerson) {
	nameOfAckPerson = newNameOfAckPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 8:39:15 AM)
 * @param newRequireAck java.lang.Boolean
 */
public void setRequireAck(java.lang.Boolean newRequireAck) {
	requireAck = newRequireAck;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @param newUserIDname java.lang.String
 */
public void setUserIDname(java.lang.String newUserIDname) {
	userIDname = newUserIDname;
}
/**
 * Creation date: (6/3/2001 2:27:44 PM)
 * @return java.lang.String
 */
public String toString() {
	return getYukonName();
}
}

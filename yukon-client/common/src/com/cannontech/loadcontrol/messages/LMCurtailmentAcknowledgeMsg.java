package com.cannontech.loadcontrol.messages;

/**
 * ScheduleCommand objects are sent to the CBC server to request that an operation
 * be done on the given strategy.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class LMCurtailmentAcknowledgeMsg extends LMMessage 
{
	private int yukonID;
	private int curtailReferenceID;
	private String acknowledgeStatus = null;
	private String ipAddressOfAckUser = null;
	private String userIdName = null;
	private String nameOfAckPerson = null;
	private String curtailmentNotes = null;

	//The following are the different statuses that an acknowledgement can be in
  public static final String UNACKNOWLEDGED = "Unacknowledged";
  public static final String ACKNOWLEDGED = "Acknowledged";
  public static final String NOT_REQUIRED = "NotRequired";
  public static final String VERBAL = "Verbal";
/**
 * ScheduleCommand constructor comment.
 */
public LMCurtailmentAcknowledgeMsg()
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 10:24:26 AM)
 * @return java.lang.String
 */
public java.lang.String getAcknowledgeStatus() {
	return acknowledgeStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @return java.lang.String
 */
public java.lang.String getCurtailmentNotes() {
	return curtailmentNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @return int
 */
public int getCurtailReferenceID() {
	return curtailReferenceID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @return java.lang.String
 */
public java.lang.String getIpAddressOfAckUser() {
	return ipAddressOfAckUser;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @return java.lang.String
 */
public java.lang.String getNameOfAckPerson() {
	return nameOfAckPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @return java.lang.String
 */
public java.lang.String getUserIdName() {
	return userIdName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:50:23 AM)
 * @return int
 */
public int getYukonID() {
	return yukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 10:24:26 AM)
 * @param newAcknowledgeStatus java.lang.String
 */
public void setAcknowledgeStatus(java.lang.String newAcknowledgeStatus) {
	acknowledgeStatus = newAcknowledgeStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @param newCurtailmentNotes java.lang.String
 */
public void setCurtailmentNotes(java.lang.String newCurtailmentNotes) {
	curtailmentNotes = newCurtailmentNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @param newCurtailReferenceID int
 */
public void setCurtailReferenceID(int newCurtailReferenceID) {
	curtailReferenceID = newCurtailReferenceID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @param newIpAddressOfAckUser java.lang.String
 */
public void setIpAddressOfAckUser(java.lang.String newIpAddressOfAckUser) {
	ipAddressOfAckUser = newIpAddressOfAckUser;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @param newNameOfAckPerson java.lang.String
 */
public void setNameOfAckPerson(java.lang.String newNameOfAckPerson) {
	nameOfAckPerson = newNameOfAckPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 12:34:30 PM)
 * @param newUserIdName java.lang.String
 */
public void setUserIdName(java.lang.String newUserIdName) {
	userIdName = newUserIdName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:50:23 AM)
 * @param newYukonID int
 */
public void setYukonID(int newYukonID) {
	yukonID = newYukonID;
}
}

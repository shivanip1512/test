package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/28/2001 2:29:41 PM)
 * @author: Aaron Lauinger
 */
import java.util.Vector;

public class LMProgramEnergyExchange extends LMProgramBase
{
	private Integer minNotifyTime = null;
	private String heading = null;
	private String messageHeader= null;
	private String messageFooter = null;
	private String canceledMsg = null;
	private String stoppedEarlyMsg = null;

	//stores com.cannontech.loadcontrol.data.LMEnergyExchangeOffer objects	
	private Vector energyExchangeOffers = null;

	//stores com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer objects
	private Vector energyExchangeCustomers = null;	
/**
 * LMProgramEnergyExchange constructor comment.
 */
public LMProgramEnergyExchange() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public com.cannontech.loadcontrol.messages.LMManualControlRequest createScheduledStartMsg( java.util.Date start, java.util.Date stop, int gearNumber, java.util.Date notifyTime, String additionalInfo )
{
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public com.cannontech.loadcontrol.messages.LMManualControlRequest createScheduledStopMsg( java.util.Date start, java.util.Date stop, int gearNumber, String additionalInfo )
{
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 8:50:05 AM)
 */
public com.cannontech.loadcontrol.messages.LMManualControlRequest createStartStopNowMsg( java.util.Date stopTime, int gearNumber, String additionalInfo, boolean isStart )
{
	return null;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @return java.lang.String
 */
public java.lang.String getCanceledMsg() {
	return canceledMsg;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @return java.util.Vector
 */
public java.util.Vector getEnergyExchangeCustomers() {
	return energyExchangeCustomers;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @return java.util.Vector
 */
public java.util.Vector getEnergyExchangeOffers() {
	return energyExchangeOffers;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @return java.lang.String
 */
public java.lang.String getHeading() {
	return heading;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @return java.lang.String
 */
public java.lang.String getMessageFooter() {
	return messageFooter;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @return java.lang.String
 */
public java.lang.String getMessageHeader() {
	return messageHeader;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:23:56 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinNotifyTime() {
	return minNotifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.util.GregorianCalendar getStartTime()
{
	//not implemented yet
	return null;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @return java.lang.String
 */
public java.lang.String getStoppedEarlyMsg() {
	return stoppedEarlyMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:53:52 PM)
 * @return java.lang.String
 */
public java.util.GregorianCalendar getStopTime()
{
	//not implemented yet
	return null;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @param newCanceledMsg java.lang.String
 */
public void setCanceledMsg(java.lang.String newCanceledMsg) {
	canceledMsg = newCanceledMsg;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @param newEnergyExchangeCustomers java.util.Vector
 */
public void setEnergyExchangeCustomers(java.util.Vector newEnergyExchangeCustomers) {
	energyExchangeCustomers = newEnergyExchangeCustomers;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @param newEnergyExchangeOffers java.util.Vector
 */
public void setEnergyExchangeOffers(java.util.Vector newEnergyExchangeOffers) {
	energyExchangeOffers = newEnergyExchangeOffers;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @param newHeading java.lang.String
 */
public void setHeading(java.lang.String newHeading) {
	heading = newHeading;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @param newMessageFooter java.lang.String
 */
public void setMessageFooter(java.lang.String newMessageFooter) {
	messageFooter = newMessageFooter;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @param newMessageHeader java.lang.String
 */
public void setMessageHeader(java.lang.String newMessageHeader) {
	messageHeader = newMessageHeader;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:23:56 PM)
 * @param newMinNotifyTime java.lang.Integer
 */
public void setMinNotifyTime(java.lang.Integer newMinNotifyTime) {
	minNotifyTime = newMinNotifyTime;
}
/**
 * Creation date: (5/28/2001 2:31:46 PM)
 * @param newStoppedEarlyMsg java.lang.String
 */
public void setStoppedEarlyMsg(java.lang.String newStoppedEarlyMsg) {
	stoppedEarlyMsg = newStoppedEarlyMsg;
}
}

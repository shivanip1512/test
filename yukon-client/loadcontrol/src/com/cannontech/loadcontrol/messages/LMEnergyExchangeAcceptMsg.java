package com.cannontech.loadcontrol.messages;

/**
 * Creation date: (5/29/2001 12:04:07 PM)
 * @author: Aaron Lauinger
 */
public class LMEnergyExchangeAcceptMsg extends LMMessage 
{
	public static final String NO_RESPONSE_ACCEPT_STATUS = "NoResponse";
	public static final String ACCEPTED_ACCEPT_STATUS = "Accepted";
	public static final String DECLINED_ACCEPT_STATUS = "Declined";
	
	private Integer yukonID = null;
	private Integer offerID = null;
	private Integer revisionNumber = null;
	private String acceptStatus = "(none)";
	private String ipAddressOfCustomer = "(none)";
	private String userIDName = "(none)";
	private String nameOfAcceptPerson = "(none)";
	private String energyExchangeNotes = "(none)";
	private Double[] amountCommitted = null; //24 hourly amounts
/**
 * LMEnergyExchangeAcceptMsg constructor comment.
 */
public LMEnergyExchangeAcceptMsg() {
	super();
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @return java.lang.String
 */
public java.lang.String getAcceptStatus() {
	return acceptStatus;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @return java.lang.Double[]
 */
public java.lang.Double[] getAmountCommitted() {
	if( amountCommitted == null )
	{
		amountCommitted = new Double[24];
		Double val = new Double(0.0);
		
		for( int i = 0; i < 24; i++ )
			amountCommitted[i] = val;
	}
	
	return amountCommitted;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @return java.lang.String
 */
public java.lang.String getEnergyExchangeNotes() {
	return energyExchangeNotes;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @return java.lang.String
 */
public java.lang.String getIpAddressOfCustomer() {
	return ipAddressOfCustomer;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @return java.lang.String
 */
public java.lang.String getNameOfAcceptPerson() {
	return nameOfAcceptPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOfferID() {
	return offerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRevisionNumber() {
	return revisionNumber;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @return java.lang.String
 */
public java.lang.String getUserIDName() {
	return userIDName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonID() {
	return yukonID;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @param newAcceptStatus java.lang.String
 */
public void setAcceptStatus(java.lang.String newAcceptStatus) {
	acceptStatus = newAcceptStatus;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @param newAmountCommitted java.lang.Double[]
 */
public void setAmountCommitted(java.lang.Double[] newAmountCommitted) {
	amountCommitted = newAmountCommitted;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @param newEnergyExchangeNotes java.lang.String
 */
public void setEnergyExchangeNotes(java.lang.String newEnergyExchangeNotes) {
	energyExchangeNotes = newEnergyExchangeNotes;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @param newIpAddressOfCustomer java.lang.String
 */
public void setIpAddressOfCustomer(java.lang.String newIpAddressOfCustomer) {
	ipAddressOfCustomer = newIpAddressOfCustomer;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @param newNameOfAcceptPerson java.lang.String
 */
public void setNameOfAcceptPerson(java.lang.String newNameOfAcceptPerson) {
	nameOfAcceptPerson = newNameOfAcceptPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:47 PM)
 * @param newOfferID java.lang.Integer
 */
public void setOfferID(java.lang.Integer newOfferID) {
	offerID = newOfferID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:47 PM)
 * @param newRevisionNumber java.lang.Integer
 */
public void setRevisionNumber(java.lang.Integer newRevisionNumber) {
	revisionNumber = newRevisionNumber;
}
/**
 * Creation date: (5/29/2001 12:05:55 PM)
 * @param newUserIDName java.lang.String
 */
public void setUserIDName(java.lang.String newUserIDName) {
	userIDName = newUserIDName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:47 PM)
 * @param newYukonID java.lang.Integer
 */
public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}
}

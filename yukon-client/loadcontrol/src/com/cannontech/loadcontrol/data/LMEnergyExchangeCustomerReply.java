package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/29/2001 10:38:24 AM)
 * @author: Aaron Lauinger
 */
import java.util.Date;
import java.util.Vector;

public class LMEnergyExchangeCustomerReply 
{
	private Integer customerID = null;
	private Integer offerID = null;
	private String acceptStatus = null;
	private Date acceptDateTime = null;
	private Integer revisionNumber = null;
	private String ipAddressOfAcceptUser = null;
	private String userIDName = null;
	private String nameOfAcceptPerson = null;
	private String energyExchangeNotes = null;

	//contains com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer
	private Vector energyExchangeHourlyCustomer = null;

	//possible states for the acceptStatus string
	public static final String STRING_NO_RESPONSE = "NoResponse";
	public static final String STRING_ACCEPTED = "Accepted";
	public static final String STRING_DECLINED = "Declined";
/**
 * LMEnergyExchangeCustomerReply constructor comment.
 */
public LMEnergyExchangeCustomerReply() {
	super();
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @return java.util.Date
 */
public java.util.Date getAcceptDateTime() {
	return acceptDateTime;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @return java.lang.String
 */
public java.lang.String getAcceptStatus() {
	return acceptStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:20:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerID() {
	return customerID;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @return java.util.Vector
 */
public java.util.Vector getEnergyExchangeHourlyCustomer() {
	return energyExchangeHourlyCustomer;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @return java.lang.String
 */
public java.lang.String getEnergyExchangeNotes() {
	return energyExchangeNotes;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @return java.lang.String
 */
public java.lang.String getIpAddressOfAcceptUser() {
	return ipAddressOfAcceptUser;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @return java.lang.String
 */
public java.lang.String getNameOfAcceptPerson() {
	return nameOfAcceptPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:20:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOfferID() {
	return offerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:20:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRevisionNumber() {
	return revisionNumber;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @return java.lang.String
 */
public java.lang.String getUserIDName() {
	return userIDName;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @param newAcceptDateTime java.util.Date
 */
public void setAcceptDateTime(java.util.Date newAcceptDateTime) {
	acceptDateTime = newAcceptDateTime;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @param newAcceptStatus java.lang.String
 */
public void setAcceptStatus(java.lang.String newAcceptStatus) {
	acceptStatus = newAcceptStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:20:32 PM)
 * @param newCustomerID java.lang.Integer
 */
public void setCustomerID(java.lang.Integer newCustomerID) {
	customerID = newCustomerID;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @param newEnergyExchangeHourlyCustomer java.util.Vector
 */
public void setEnergyExchangeHourlyCustomer(java.util.Vector newEnergyExchangeHourlyCustomer) {
	energyExchangeHourlyCustomer = newEnergyExchangeHourlyCustomer;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @param newEnergyExchangeNotes java.lang.String
 */
public void setEnergyExchangeNotes(java.lang.String newEnergyExchangeNotes) {
	energyExchangeNotes = newEnergyExchangeNotes;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @param newIpAddressOfAcceptUser java.lang.String
 */
public void setIpAddressOfAcceptUser(java.lang.String newIpAddressOfAcceptUser) {
	ipAddressOfAcceptUser = newIpAddressOfAcceptUser;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @param newNameOfAcceptPerson java.lang.String
 */
public void setNameOfAcceptPerson(java.lang.String newNameOfAcceptPerson) {
	nameOfAcceptPerson = newNameOfAcceptPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:20:32 PM)
 * @param newOfferID java.lang.Integer
 */
public void setOfferID(java.lang.Integer newOfferID) {
	offerID = newOfferID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:20:32 PM)
 * @param newRevisionNumber java.lang.Integer
 */
public void setRevisionNumber(java.lang.Integer newRevisionNumber) {
	revisionNumber = newRevisionNumber;
}
/**
 * Creation date: (5/29/2001 10:41:10 AM)
 * @param newUserIDName java.lang.String
 */
public void setUserIDName(java.lang.String newUserIDName) {
	userIDName = newUserIDName;
}
/**
 * Creation date: (6/3/2001 2:28:10 PM)
 * @return java.lang.String
 */
public String toString() {
	return "( LMEnergyExchangeCustomerReply, Offer ID: " + getOfferID() +
	       " customer ID: " + getCustomerID() + 
	       " accept status: " + getAcceptStatus() +
	       " accept timestamp: " + getAcceptDateTime() +
	       " )";
}
}

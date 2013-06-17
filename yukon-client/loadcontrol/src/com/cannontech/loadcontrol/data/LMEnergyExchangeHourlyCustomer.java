package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/29/2001 10:27:52 AM)
 * @author: Aaron Lauinger
 */

public class LMEnergyExchangeHourlyCustomer 
{
	private Integer customerID = null;
	private Integer offerID = null;
	private Integer revisionNumber = null;
	private Integer hour = null; //0-23
	private Double amountCommitted = null;
	
/**
 * LMEnergyExchangeHourlyCustomer constructor comment.
 */
public LMEnergyExchangeHourlyCustomer() {
	super();
}
/**
 * Creation date: (5/29/2001 10:29:01 AM)
 * @return java.lang.Double
 */
public java.lang.Double getAmountCommitted() {
	return amountCommitted;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerID() {
	return customerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHour() {
	return hour;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOfferID() {
	return offerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRevisionNumber() {
	return revisionNumber;
}
/**
 * Creation date: (5/29/2001 10:29:01 AM)
 * @param newAmountCommitted java.lang.Double
 */
public void setAmountCommitted(java.lang.Double newAmountCommitted) {
	amountCommitted = newAmountCommitted;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:28 PM)
 * @param newCustomerID java.lang.Integer
 */
public void setCustomerID(java.lang.Integer newCustomerID) {
	customerID = newCustomerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:28 PM)
 * @param newHour java.lang.Integer
 */
public void setHour(java.lang.Integer newHour) {
	hour = newHour;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:28 PM)
 * @param newOfferID java.lang.Integer
 */
public void setOfferID(java.lang.Integer newOfferID) {
	offerID = newOfferID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:28 PM)
 * @param newRevisionNumber java.lang.Integer
 */
public void setRevisionNumber(java.lang.Integer newRevisionNumber) {
	revisionNumber = newRevisionNumber;
}
/**
 * Creation date: (6/3/2001 2:29:55 PM)
 * @return java.lang.String
 */
public String toString() {
	return "( LMEnergyExchangeHourlyCustomer, cust ID: " + getCustomerID() +
	       " offer ID-rev: " + getOfferID() + "-" + getRevisionNumber() +
	       " hour: " + getHour() + " amt commited: " + getAmountCommitted() +
	       " )";
}
}

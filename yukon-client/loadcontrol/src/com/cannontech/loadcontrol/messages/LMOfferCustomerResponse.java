package com.cannontech.loadcontrol.messages;

/**
 * Message to tell the load management server about a customer
 * response to an Energy Excange offer.
 *
 *
 * Creation date: (5/16/2001 11:32:03 AM)
 * @author: Aaron Lauinger
 */

import java.util.Date;

public class LMOfferCustomerResponse extends LMMessage {

	// Every message will be one of these
	public static final int ACCEPTED = 1;
	public static final int DECLINED = 2;
	
	private int offerID;
	private int revision;
	
	// Either accepted or declined
	// If declined then none of the fields below this matter
	private int responseType;
	
	private Date offerDate = null;

	// represents load committed by said customer
	// for each of the 24 hours of the day	
	private int[] loadCommited = new int[24];	
/**
 * LMOfferAcceptMsg constructor comment.
 */
public LMOfferCustomerResponse() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @return int[]
 */
public int[] getLoadCommited() {
	return loadCommited;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @return java.util.Date
 */
public java.util.Date getOfferDate() {
	return offerDate;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @return int
 */
public int getOfferID() {
	return offerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @return int
 */
public int getResponseType() {
	return responseType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @return int
 */
public int getRevision() {
	return revision;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @param newLoadCommited int[]
 */
public void setLoadCommited(int[] newLoadCommited) {
	loadCommited = newLoadCommited;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @param newOfferDate java.util.Date
 */
public void setOfferDate(java.util.Date newOfferDate) {
	offerDate = newOfferDate;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @param newOfferID int
 */
public void setOfferID(int newOfferID) {
	offerID = newOfferID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @param newResponseType int
 */
public void setResponseType(int newResponseType) {
	responseType = newResponseType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:36:48 PM)
 * @param newRevision int
 */
public void setRevision(int newRevision) {
	revision = newRevision;
}
}

package com.cannontech.loadcontrol.messages;

/**
 * Submit a new offer or a revision to an existing offer.
 *
 * Creation date: (5/16/2001 12:31:09 PM)
 * @author: Aaron Lauinger
 */
import java.util.Date;

public class LMOfferSubmission extends LMMessage {

	private int programID;
	
	private Date offerDate = null;
	private Date notificationDate = null;	
	private Date expirationDate = null;
	
	private int[] targetLoad = new int[24];
	private int[] pricePerHour = new int[24]; //in cents
/**
 * LMOfferSubmit constructor comment.
 */
public LMOfferSubmission() {
	super();
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @return java.util.Date
 */
public java.util.Date getExpirationDate() {
	return expirationDate;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @return java.util.Date
 */
public java.util.Date getNotificationDate() {
	return notificationDate;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @return java.util.Date
 */
public java.util.Date getOfferDate() {
	return offerDate;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @return double[]
 */
public int[] getPricePerHour() {
	return pricePerHour;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:10 PM)
 * @return int
 */
public int getProgramID() {
	return programID;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @return int[]
 */
public int[] getTargetLoad() {
	return targetLoad;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @param newExpirationDate java.util.Date
 */
public void setExpirationDate(java.util.Date newExpirationDate) {
	expirationDate = newExpirationDate;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @param newNotificationDate java.util.Date
 */
public void setNotificationDate(java.util.Date newNotificationDate) {
	notificationDate = newNotificationDate;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @param newOfferDate java.util.Date
 */
public void setOfferDate(java.util.Date newOfferDate) {
	offerDate = newOfferDate;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @param newPricePerHour double[]
 */
public void setPricePerHour(int[] newPricePerHour) {
	pricePerHour = newPricePerHour;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:10 PM)
 * @param newProgramID int
 */
public void setProgramID(int newProgramID) {
	programID = newProgramID;
}
/**
 * Creation date: (5/16/2001 1:03:01 PM)
 * @param newTargetLoad int[]
 */
public void setTargetLoad(int[] newTargetLoad) {
	targetLoad = newTargetLoad;
}
}

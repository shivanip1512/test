package com.cannontech.loadcontrol.messages;

/**
 * Creation date: (5/29/2001 11:37:37 AM)
 * @author: Aaron Lauinger
 */
import java.util.Date;

public class LMEnergyExchangeControlMsg extends LMMessage {

	// command enumeration
	public static final int NEW_OFFER = 0;
	public static final int OFFER_UPDATE = 1;
	public static final int OFFER_REVISION = 2;
	public static final int CLOSE_OFFER = 3;
	public static final int CANCEL_OFFER = 4;
	
	private Integer command = null;
	private Integer yukonID = null;
	private Integer offerID = null;
	private Date offerDate = null;
	private Date notificationDateTime = null;
	private Date expirationDateTime = null;
	private String additionalInfo = null;
	private Double[] amountRequested = null; //24 hourly values
	private Integer[] pricesOffered = null; //24 hourly values
	
/**
 * LMEnergyExchangeControlMsg constructor comment.
 */
public LMEnergyExchangeControlMsg() {
	super();
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @return java.lang.String
 */
public java.lang.String getAdditionalInfo() {
	return additionalInfo;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @return java.lang.Double[]
 */
public java.lang.Double[] getAmountRequested() {
	return amountRequested;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCommand() {
	return command;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @return java.util.Date
 */
public java.util.Date getExpirationDateTime() {
	return expirationDateTime;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @return java.util.Date
 */
public java.util.Date getNotificationDateTime() {
	return notificationDateTime;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @return java.util.Date
 */
public java.util.Date getOfferDate() {
	return offerDate;
}
/**
 * Creation date: (4/2/2002 9:55:00 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOfferID() {
	return offerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:32 PM)
 * @return java.lang.Integer[]
 */
public java.lang.Integer[] getPricesOffered() {
	return pricesOffered;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonID() {
	return yukonID;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @param newAdditionalInfo java.lang.String
 */
public void setAdditionalInfo(java.lang.String newAdditionalInfo) {
	additionalInfo = newAdditionalInfo;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @param newAmountRequested java.lang.Double[]
 */
public void setAmountRequested(java.lang.Double[] newAmountRequested) {
	amountRequested = newAmountRequested;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:32 PM)
 * @param newCommand java.lang.Integer
 */
public void setCommand(java.lang.Integer newCommand) {
	command = newCommand;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @param newExpirationDateTime java.util.Date
 */
public void setExpirationDateTime(java.util.Date newExpirationDateTime) {
	expirationDateTime = newExpirationDateTime;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @param newNotificationDateTime java.util.Date
 */
public void setNotificationDateTime(java.util.Date newNotificationDateTime) {
	notificationDateTime = newNotificationDateTime;
}
/**
 * Creation date: (5/29/2001 11:40:14 AM)
 * @param newOfferDate java.util.Date
 */
public void setOfferDate(java.util.Date newOfferDate) {
	offerDate = newOfferDate;
}
/**
 * Creation date: (4/2/2002 9:55:00 AM)
 * @param newOfferID java.lang.Integer
 */
public void setOfferID(java.lang.Integer newOfferID) {
	offerID = newOfferID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:32 PM)
 * @param newPricesOffered java.lang.Integer[]
 */
public void setPricesOffered(java.lang.Integer[] newPricesOffered) {
	pricesOffered = newPricesOffered;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:37:32 PM)
 * @param newYukonID java.lang.Integer
 */
public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}
}

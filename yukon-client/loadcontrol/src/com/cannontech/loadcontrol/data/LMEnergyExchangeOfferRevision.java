package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/28/2001 2:48:24 PM)
 * @author: Aaron Lauinger
 */

import java.util.Date;
import java.util.Vector;

public class LMEnergyExchangeOfferRevision 
{
	private Integer offerID = null;
	private Integer revisionNumber = null;
	private Date actionDateTime = null;
	private Date notificationDateTime = null;
	private Date offerExpirationDateTime = null;
	private String additionalInfo = null;

	// contains com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer
	private Vector energyExchangeHourlyOffers = null;	
/**
 * LMEnergyExchangeOfferRevision constructor comment.
 */
public LMEnergyExchangeOfferRevision() {
	super();
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @return java.util.Date
 */
public java.util.Date getActionDateTime() {
	return actionDateTime;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @return java.lang.String
 */
public java.lang.String getAdditionalInfo() {
	return additionalInfo;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @return java.util.Vector
 */
public java.util.Vector getEnergyExchangeHourlyOffers() {
	return energyExchangeHourlyOffers;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @return java.util.Date
 */
public java.util.Date getNotificationDateTime() {
	return notificationDateTime;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @return java.util.Date
 */
public java.util.Date getOfferExpirationDateTime() {
	return offerExpirationDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:23:05 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOfferID() {
	return offerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:23:05 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRevisionNumber() {
	return revisionNumber;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @param newActionDateTime java.util.Date
 */
public void setActionDateTime(java.util.Date newActionDateTime) {
	actionDateTime = newActionDateTime;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @param newAdditionalInfo java.lang.String
 */
public void setAdditionalInfo(java.lang.String newAdditionalInfo) {
	additionalInfo = newAdditionalInfo;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @param newEnergyExchangeHourlyOffers java.util.Vector
 */
public void setEnergyExchangeHourlyOffers(java.util.Vector newEnergyExchangeHourlyOffers) {
	energyExchangeHourlyOffers = newEnergyExchangeHourlyOffers;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @param newNotificationDateTime java.util.Date
 */
public void setNotificationDateTime(java.util.Date newNotificationDateTime) {
	notificationDateTime = newNotificationDateTime;
}
/**
 * Creation date: (5/28/2001 2:50:03 PM)
 * @param newOfferExpirationDateTime java.util.Date
 */
public void setOfferExpirationDateTime(java.util.Date newOfferExpirationDateTime) {
	offerExpirationDateTime = newOfferExpirationDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:23:05 PM)
 * @param newOfferID java.lang.Integer
 */
public void setOfferID(java.lang.Integer newOfferID) {
	offerID = newOfferID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:23:05 PM)
 * @param newRevisionNumber java.lang.Integer
 */
public void setRevisionNumber(java.lang.Integer newRevisionNumber) {
	revisionNumber = newRevisionNumber;
}
/**
 * Creation date: (6/3/2001 2:35:41 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("( LMEnergyExchangeOfferRevision, Offer ID-rev #: ");
	buf.append(getOfferID());
	buf.append("-");
	buf.append(getRevisionNumber());
	buf.append(" notification timestamp: ");
	buf.append(getNotificationDateTime());
	buf.append(" offer expiration: ");
	buf.append(getOfferExpirationDateTime());
	buf.append("\n Hourly price/amount \n");

	
	java.util.Iterator iter = getEnergyExchangeHourlyOffers().iterator();
	while(iter.hasNext())
	{
		buf.append(iter.next().toString());
	}

	return buf.toString();
}
}

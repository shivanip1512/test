package com.cannontech.loadcontrol.data;

/**
 * Creation date: (6/1/2001 10:39:08 AM)
 * @author: Aaron Lauinger
 */
public class LMEnergyExchangeHourlyOffer 
{
	private Integer offerID = null;
	private Integer revisionNumber = null;
	private Integer hour = null;
	private Double price = null;
	private Double amountRequested = null;	
/**
 * LMEnergyExchangeHourlyOffer constructor comment.
 */
public LMEnergyExchangeHourlyOffer() {
	super();
}
/**
 * Creation date: (6/1/2001 10:40:08 AM)
 * @return java.lang.Double
 */
public java.lang.Double getAmountRequested() {
	return amountRequested;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:58 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHour() {
	return hour;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:58 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOfferID() {
	return offerID;
}
/**
 * Creation date: (6/1/2001 10:40:08 AM)
 * @return java.lang.Double
 */
public java.lang.Double getPrice() {
	return price;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:58 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRevisionNumber() {
	return revisionNumber;
}
/**
 * Creation date: (6/1/2001 10:40:08 AM)
 * @param newAmountRequested java.lang.Double
 */
public void setAmountRequested(java.lang.Double newAmountRequested) {
	amountRequested = newAmountRequested;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:58 PM)
 * @param newHour java.lang.Integer
 */
public void setHour(java.lang.Integer newHour) {
	hour = newHour;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:58 PM)
 * @param newOfferID java.lang.Integer
 */
public void setOfferID(java.lang.Integer newOfferID) {
	offerID = newOfferID;
}
/**
 * Creation date: (6/1/2001 10:40:08 AM)
 * @param newPrice java.lang.Double
 */
public void setPrice(java.lang.Double newPrice) {
	price = newPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:21:58 PM)
 * @param newRevisionNumber java.lang.Integer
 */
public void setRevisionNumber(java.lang.Integer newRevisionNumber) {
	revisionNumber = newRevisionNumber;
}
/**
 * Creation date: (6/3/2001 2:33:07 PM)
 * @return java.lang.String
 */
public String toString() {
	return "( LMEnergyExchangeHourlyOffer, offer ID-rev: " + getOfferID() + "-" + getRevisionNumber() +
	       " hour: " + getHour() + " price: " + getPrice() + " amt requested: " + getAmountRequested() +	       
	       " )";
}
}

package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * Insert the type's description here.
 * Creation date: (8/10/2001 9:44:00 AM)
 * @author: 
 */

public class RevisionHistoryRowData 
{
	private int offerID = 0;
	private int revisionNumber = 0;
	private String programName = null;
	private java.util.Date notifyTime = null;
	private java.util.Date expireTime = null;
	private Double amountCommitted = null;
	private Double amountRequested = null;

	//store the owner history program, beware of calling its getters since
	// most hit the database!!
	private com.cannontech.web.history.HEnergyExchangeProgram historyProgram = null;

	private double[] price = null;
	private double[] hrAmountRequested = null;
/**
 * RevisionHistoryRowData constructor comment.
 */
public RevisionHistoryRowData() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 2:34:50 PM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) 
{
	return ( (o != null) &&
			   (o instanceof RevisionHistoryRowData) &&
  			   ((RevisionHistoryRowData)o).getOfferIDString().equalsIgnoreCase(getOfferIDString()) );
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @return java.lang.Double
 */
public java.lang.Double getAmountCommitted() {
	return amountCommitted;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @return java.lang.Double
 */
public java.lang.Double getAmountRequested() {
	return amountRequested;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @return java.util.Date
 */
public java.util.Date getExpireTime() {
	return expireTime;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:37:07 PM)
 * @return com.cannontech.web.history.HEnergyExchangeProgram
 */
public com.cannontech.web.history.HEnergyExchangeProgram getHistoryProgram() {
	return historyProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:41:08 AM)
 * @return double[]
 */
public double[] getHrAmountRequested() {
	return hrAmountRequested;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @return java.util.Date
 */
public java.util.Date getNotifyTime() {
	return notifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:43:07 PM)
 * @return int
 */
public int getOfferID() {
	return offerID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @return java.lang.String
 */
public java.lang.String getOfferIDString() {
	return getOfferID() + "-" + getRevisionNumber();
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:41:08 AM)
 * @return double[]
 */
public double[] getPrice() {
	return price;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @return java.lang.String
 */
public java.lang.String getProgramName() {
	return programName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:43:07 PM)
 * @return int
 */
public int getRevisionNumber() {
	return revisionNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @param newAmountCommitted java.lang.Double
 */
public void setAmountCommitted(java.lang.Double newAmountCommitted) {
	amountCommitted = newAmountCommitted;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @param newAmountRequested java.lang.Double
 */
public void setAmountRequested(java.lang.Double newAmountRequested) {
	amountRequested = newAmountRequested;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @param newExpireTime java.util.Date
 */
public void setExpireTime(java.util.Date newExpireTime) {
	expireTime = newExpireTime;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:37:07 PM)
 * @param newHistoryProgram com.cannontech.web.history.HEnergyExchangeProgram
 */
public void setHistoryProgram(com.cannontech.web.history.HEnergyExchangeProgram newHistoryProgram) {
	historyProgram = newHistoryProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:41:08 AM)
 * @param newHrAmountRequested double[]
 */
public void setHrAmountRequested(double[] newHrAmountRequested) {
	hrAmountRequested = newHrAmountRequested;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @param newNotifyTime java.util.Date
 */
public void setNotifyTime(java.util.Date newNotifyTime) {
	notifyTime = newNotifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:43:07 PM)
 * @param newOfferID int
 */
public void setOfferID(int newOfferID) {
	offerID = newOfferID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:41:08 AM)
 * @param newPrice double[]
 */
public void setPrice(double[] newPrice) {
	price = newPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:27:09 AM)
 * @param newProgramName java.lang.String
 */
public void setProgramName(java.lang.String newProgramName) {
	programName = newProgramName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:43:07 PM)
 * @param newRevisionNumber int
 */
public void setRevisionNumber(int newRevisionNumber) {
	revisionNumber = newRevisionNumber;
}
}

package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/12/2001 3:42:48 PM)
 * @author: 
 */
public class HEnergyExchangeHourlyCustomer {
	private java.sql.Statement stmt = null;
	private long customerId = 0;
	private long offerId = 0;
	private int revisionNumber = 0;
	private int hour = 0;
	private double amountCommitted = 0;
/**
 * HEnergyExchangeHourlyCustomer constructor comment.
 */
public HEnergyExchangeHourlyCustomer() {
	super();
}
	public double getAmountCommitted() {
		return amountCommitted;
	}
	public long getCustomerId() {
		return customerId;
	}
	public int getHour() {
		return hour;
	}
	public long getOfferId() {
		return offerId;
	}
	public int getRevisionNumber() {
		return revisionNumber;
	}
	public void setAmountCommitted(double amountCommitted) {
		this.amountCommitted = amountCommitted;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public void setOfferId(long offerId) {
		this.offerId = offerId;
	}
	public void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}
	public void setStatement(java.sql.Statement stmt) {
		this.stmt = stmt;
	}
}

package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/12/2001 3:22:48 PM)
 * @author: 
 */
public class HEnergyExchangeHourlyOffer {
	private java.sql.Statement stmt = null;
	private long offerId = 0;
	private int revisionNumber = 0;
	private int hour = 0;
	private long price = 0;
	private double amountRequested = 0;
/**
 * HEnergyExchangeHourlyOffer constructor comment.
 */
public HEnergyExchangeHourlyOffer() {
	super();
}
	public double getAmountRequested() {
		return amountRequested;
	}
	public int getHour() {
		return hour;
	}
	public long getOfferId() {
		return offerId;
	}
	public long getPrice() {
		return price;
	}
	public int getRevisionNumber() {
		return revisionNumber;
	}
	public void setAmountRequested(double amountRequested) {
		this.amountRequested = amountRequested;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public void setOfferId(long offerId) {
		this.offerId = offerId;
	}
	public void setPrice(long price) {
		this.price = price;
	}
	public void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}
	public void setStatement(java.sql.Statement stmt) {
		this.stmt = stmt;
	}
}

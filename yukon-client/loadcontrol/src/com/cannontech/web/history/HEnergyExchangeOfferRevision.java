package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2001 4:45:26 PM)
 * @author: 
 */
public class HEnergyExchangeOfferRevision {
	private java.sql.Statement stmt = null;
	private long deviceId = 0;
	private long offerId = 0;
	private int revisionNumber = 0;
	private java.util.Date actionDateTime = null;
	private java.util.Date notificationDateTime = null;
	private java.util.Date expirationDateTime = null;
	private String additionalInfo = null;
/**
 * HEnergyExchangeOfferRevision constructor comment.
 */
public HEnergyExchangeOfferRevision() {
	super();
}
	public java.util.Date getActionDateTime() {
		return actionDateTime;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 5:22:25 PM)
 * @return double
 */
public double getAmountCommitted() {
	String queryStr = "SELECT SUM(AMOUNTCOMMITTED) AS TOTALCOMMITED FROM LMENERGYEXCHANGEHOURLYCUSTOMER ";
	queryStr += "WHERE OFFERID = " + offerId + " AND REVISIONNUMBER = " + revisionNumber;
	queryStr += " AND CUSTOMERID IN ( ";
	queryStr += "SELECT CUSTOMERID FROM LMENERGYEXCHANGECUSTOMERLIST WHERE PROGRAMID = " + deviceId;
	queryStr += " )";

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		if (rset.next())
			return rset.getDouble(1);
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
	
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 5:13:25 PM)
 * @return double[]
 */
public double getAmountRequested() {
	String queryStr = "SELECT SUM(AMOUNTREQUESTED) AS TOTALREQUESTED FROM LMENERGYEXCHANGEHOURLYOFFER ";
	queryStr += "WHERE OFFERID = " + offerId + " AND REVISIONNUMBER = " + revisionNumber;

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		if (rset.next())
			return rset.getDouble(1);
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
	
	return 0;
}
	public long getDeviceId() {
		return deviceId;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 3:33:19 PM)
 * @return com.cannontech.web.history.HEnergyExchangeHourlyOffer[]
 */
public HEnergyExchangeHourlyOffer[] getEnergyExchangeHourlyOffers() {
	String queryStr = "SELECT * FROM LMENERGYEXCHANGEHOURLYOFFER ";
	queryStr += "WHERE OFFERID = " + offerId + " AND REVISIONNUMBER = " + revisionNumber;
	queryStr += " ORDER BY HOUR";

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		java.util.ArrayList hourlyOfferList = new java.util.ArrayList();
		while (rset.next()) {
			HEnergyExchangeHourlyOffer hourlyOffer = new HEnergyExchangeHourlyOffer();
			
			hourlyOffer.setStatement(stmt);
			hourlyOffer.setOfferId(offerId);
			hourlyOffer.setRevisionNumber(revisionNumber);
			hourlyOffer.setHour( rset.getInt(3) );
			hourlyOffer.setPrice( rset.getLong(4) );
			hourlyOffer.setAmountRequested( rset.getDouble(5) );

			hourlyOfferList.add(hourlyOffer);
		}

		HEnergyExchangeHourlyOffer[] ret = new HEnergyExchangeHourlyOffer[ hourlyOfferList.size() ];
		hourlyOfferList.toArray(ret);
		
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeHourlyOffer[0];
	}
}
	public java.util.Date getExpirationDateTime() {
		return expirationDateTime;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 5:08:03 PM)
 * @return double[]
 */
public double[] getHourlyPrices() {
	String queryStr = "SELECT HOUR, PRICE FROM LMENERGYEXCHANGEHOURLYOFFER ";
	queryStr += "WHERE OFFERID = " + offerId + " AND REVISIONNUMBER = " + revisionNumber;

	double[] ret = new double[24];
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		while (rset.next()) {
			int hour = rset.getInt(1);
			ret[hour] = rset.getLong(2) / 100;
		}	
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
	
	return ret;
}
	public java.util.Date getNotificationDateTime() {
		return notificationDateTime;
	}
	public long getOfferId() {
		return offerId;
	}
	public int getRevisionNumber() {
		return revisionNumber;
	}
	public java.sql.Statement getStatement() {
		return stmt;
	}
	public void setActionDateTime(java.util.Date actionDateTime) {
		this.actionDateTime = actionDateTime;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public void setExpirationDateTime(java.util.Date expirationDateTime) {
		this.expirationDateTime = expirationDateTime;
	}
	public void setNotificationDateTime(java.util.Date notificationDateTime) {
		this.notificationDateTime = notificationDateTime;
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

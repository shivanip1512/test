package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2001 3:49:07 PM)
 * @author: 
 */
public class HEnergyExchangeProgram {
	private java.sql.Statement stmt = null;
	private long deviceId = 0;
	private String programName = null;
/**
 * HEnergyExchangeProgram constructor comment.
 */
public HEnergyExchangeProgram() {
	super();
}
	public long getDeviceId() {
		return deviceId;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 11:34:36 AM)
 * @return com.cannontech.web.history.HEnergyExchangeCustomer[]
 */
public HEnergyExchangeCustomer[] getEnergyExchangeCustomers() {
	String queryStr = "SELECT CICUSTOMERBASE.CUSTOMERID,CICUSTOMERBASE.COMPANYNAME FROM LMENERGYEXCHANGECUSTOMERLIST, CICUSTOMERBASE";
    queryStr += " WHERE LMENERGYEXCHANGECUSTOMERLIST.PROGRAMID = " + deviceId + " AND LMENERGYEXCHANGECUSTOMERLIST.CUSTOMERID = CICUSTOMERBASE.CUSTOMERID ";
	queryStr += " ORDER BY CICUSTOMERBASE.CUSTOMERID";
	
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		java.util.ArrayList customerList = new java.util.ArrayList();
		while (rset.next()) {
			HEnergyExchangeCustomer customer = new HEnergyExchangeCustomer();
			
			customer.setStatement(stmt);
			customer.setDeviceId(deviceId);
			customer.setCustomerId( rset.getLong(1) );
			customer.setCustomerName( rset.getString(2) );

			customerList.add(customer);
		}

		HEnergyExchangeCustomer[] ret = new HEnergyExchangeCustomer[ customerList.size() ];
		customerList.toArray(ret);
		
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeCustomer[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 4:01:33 PM)
 * @return com.cannontech.web.history.HEnergyExchangeProgramOffer[]
 */
public HEnergyExchangeProgramOffer[] getEnergyExchangeProgramOffers() {
	String queryStr = "SELECT * FROM LMENERGYEXCHANGEPROGRAMOFFER ";
	queryStr += "WHERE DEVICEID = " + deviceId + " ORDER BY OFFERDATE DESC";
	
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);
	
		java.util.ArrayList offerList = new java.util.ArrayList();
		while (rset.next()) {
			HEnergyExchangeProgramOffer offer = new HEnergyExchangeProgramOffer();
			
			offer.setStatement(stmt);
			offer.setDeviceId( rset.getLong(1) );
			offer.setOfferId( rset.getLong(2) );
			offer.setRunStatus( rset.getString(3) );
			offer.setOfferDate( rset.getTimestamp(4) );

			offerList.add(offer);
		}

		HEnergyExchangeProgramOffer[] ret = new HEnergyExchangeProgramOffer[offerList.size()];
		offerList.toArray(ret);
	
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeProgramOffer[0];
	}
}
	public String getProgramName() {
		return programName;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public void setProgramName(String name) {
		this.programName = name;
	}
	public void setStatement(java.sql.Statement stmt) {
		this.stmt = stmt;
	}
}

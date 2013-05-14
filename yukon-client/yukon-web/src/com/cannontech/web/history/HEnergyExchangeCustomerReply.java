package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/12/2001 11:44:27 AM)
 * @author: 
 */
public class HEnergyExchangeCustomerReply {
	private java.sql.Statement stmt = null;
	private long customerId = 0;
	private long offerId = 0;
	private int revisionNumber = 0;
	private String acceptStatus = null;
	private java.util.Date acceptDateTime = null;
	private String userIdName = null;
	private String nameOfAcceptPerson = null;
/**
 * HEnergyExchangeCustomerReply constructor comment.
 */
public HEnergyExchangeCustomerReply() {
	super();
}
	public java.util.Date getAcceptDateTime() {
		return acceptDateTime;
	}
	public String getAcceptStatus() {
		return acceptStatus;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 2:41:01 PM)
 * @return double
 */
public double getAmountCommitted() {
	String queryStr = "SELECT SUM(AMOUNTCOMMITTED) AS TOTALCOMMITED FROM LMENERGYEXCHANGEHOURLYCUSTOMER ";
	queryStr += "WHERE CUSTOMERID = " + customerId + " AND OFFERID = " + offerId + " AND REVISIONNUMBER = " + revisionNumber;

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
	public long getCustomerId() {
		return customerId;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 3:52:09 PM)
 * @return com.cannontech.web.history.HEnergyExchangeHourlyCustomer[]
 */
public HEnergyExchangeHourlyCustomer[] getEnergyExchangeHourlyCustomers() {
	String queryStr = "SELECT * FROM LMENERGYEXCHANGEHOURLYCUSTOMER ";
	queryStr += "WHERE CUSTOMERID = " + customerId + " AND OFFERID = " + offerId + " AND REVISIONNUMBER = " + revisionNumber;
	queryStr += " ORDER BY HOUR";

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		java.util.ArrayList hourlyCustomerList = new java.util.ArrayList();
		while (rset.next()) {
			HEnergyExchangeHourlyCustomer hourlyCustomer = new HEnergyExchangeHourlyCustomer();
			
			hourlyCustomer.setStatement(stmt);
			hourlyCustomer.setCustomerId(customerId);
			hourlyCustomer.setOfferId(offerId);
			hourlyCustomer.setRevisionNumber(revisionNumber);
			hourlyCustomer.setHour( rset.getInt(4) );
			hourlyCustomer.setAmountCommitted( rset.getDouble(5) );

			hourlyCustomerList.add(hourlyCustomer);
		}

		HEnergyExchangeHourlyCustomer[] ret = new HEnergyExchangeHourlyCustomer[ hourlyCustomerList.size() ];
		hourlyCustomerList.toArray(ret);
		
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeHourlyCustomer[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 3:52:09 PM)
 * @return com.cannontech.web.history.HEnergyExchangeHourlyCustomer[]
 */
public HEnergyExchangeHourlyCustomer[] getEnergyExchangeHourlyCustomers(long offerId, int revisionNumber) {
	String queryStr = "SELECT * FROM LMENERGYEXCHANGEHOURLYCUSTOMER ";
	queryStr += "WHERE CUSTOMERID = " + customerId + " AND OFFERID = " + offerId + " AND REVISIONNUMBER = " + revisionNumber;
	queryStr += " ORDER BY HOUR";

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		java.util.ArrayList hourlyCustomerList = new java.util.ArrayList();
		while (rset.next()) {
			HEnergyExchangeHourlyCustomer hourlyCustomer = new HEnergyExchangeHourlyCustomer();
			
			hourlyCustomer.setStatement(stmt);
			hourlyCustomer.setCustomerId(customerId);
			hourlyCustomer.setOfferId(offerId);
			hourlyCustomer.setRevisionNumber(revisionNumber);
			hourlyCustomer.setHour( rset.getInt(4) );
			hourlyCustomer.setAmountCommitted( rset.getDouble(5) );

			hourlyCustomerList.add(hourlyCustomer);
		}

		HEnergyExchangeHourlyCustomer[] ret = new HEnergyExchangeHourlyCustomer[ hourlyCustomerList.size() ];
		hourlyCustomerList.toArray(ret);
		
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeHourlyCustomer[0];
	}
}
	public String getNameOfAcceptPerson() {
		return nameOfAcceptPerson;
	}
	public long getOfferId() {
		return offerId;
	}
	public int getRevisionNumber() {
		return revisionNumber;
	}
	public String getUserIdName() {
		return userIdName;
	}
	public void setAcceptDateTime(java.util.Date acceptDateTime) {
		this.acceptDateTime = acceptDateTime;
	}
	public void setAcceptStatus(String acceptStatus) {
		this.acceptStatus = acceptStatus;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public void setNameOfAcceptPerson(String nameOfAcceptPerson) {
		this.nameOfAcceptPerson = nameOfAcceptPerson;
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
	public void setUserIdName(String userIdName) {
		this.userIdName = userIdName;
	}
}

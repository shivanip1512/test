package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/12/2001 11:24:56 AM)
 * @author: 
 */
public class HEnergyExchangeCustomer {
	private java.sql.Statement stmt = null;
	private long deviceId = 0;
	private long customerId = 0;
	private String customerName = null;
/**
 * HEnergyExchangeCustomer constructor comment.
 */
public HEnergyExchangeCustomer() {
	super();
}
	public long getCustomerId() {
		return customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public long getDeviceId() {
		return deviceId;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 2:17:24 PM)
 * @return com.cannontech.web.history.HEnergyExchangeCustomerReply
 */
public HEnergyExchangeCustomerReply[] getEnergyExchangeCustomerReplies() {
	String queryStr = "SELECT * FROM LMENERGYEXCHANGECUSTOMERREPLY";
	queryStr += " WHERE CUSTOMERID = " + customerId;
	queryStr += " ORDER BY OFFERID DESC";

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		long prevOfferId = -1;
		int maxRevisionNumber = -1;
		boolean foundReply = true;
		HEnergyExchangeCustomerReply reply = null;

		java.util.ArrayList replyList = new java.util.ArrayList();
		
		// find one reply for each offer ID
		// if there is no reply, then use the latest revision
		while (rset.next()) {
			long offerId = rset.getLong(2);
			int revisionNumber = rset.getInt(5);
			String acceptStatus = rset.getString(3);
			
			if (offerId == prevOfferId) {
				if ( foundReply )
					continue;
					
				if ( acceptStatus.equalsIgnoreCase("Accepted") )
					foundReply = true;
					
				if ( revisionNumber <= maxRevisionNumber )
					continue;					
				maxRevisionNumber = revisionNumber;
			}
			else {
				if ( !foundReply )
					replyList.add(reply);
				foundReply = false;
					
				if ( acceptStatus.equalsIgnoreCase("Accepted") )
					foundReply = true;					
				prevOfferId = offerId;
				maxRevisionNumber = revisionNumber;
			}
			
			reply = new HEnergyExchangeCustomerReply();
			reply.setStatement(stmt);
			reply.setCustomerId(customerId);
			reply.setOfferId( rset.getLong(2) );
			reply.setRevisionNumber( rset.getInt(5) );
			reply.setAcceptStatus( rset.getString(3) );
			reply.setAcceptDateTime( rset.getTimestamp(4) );
			reply.setUserIdName( rset.getString(7) );
			reply.setNameOfAcceptPerson( rset.getString(8) );

			if ( foundReply )
				replyList.add(reply);
		}

		if ( !foundReply )
			replyList.add(reply);

		HEnergyExchangeCustomerReply[] ret = new HEnergyExchangeCustomerReply[ replyList.size() ];
		replyList.toArray(ret);
		
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeCustomerReply[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 2:17:24 PM)
 * @return com.cannontech.web.history.HEnergyExchangeCustomerReply
 */
public HEnergyExchangeCustomerReply getEnergyExchangeCustomerReply(long offerId, int revisionNumber) {
	String queryStr = "SELECT * FROM LMENERGYEXCHANGECUSTOMERREPLY ";
	queryStr += "WHERE CUSTOMERID = " + customerId + " AND OFFERID = " + offerId + " AND REVISIONNUMBER = " + revisionNumber;

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		HEnergyExchangeCustomerReply ret = new HEnergyExchangeCustomerReply();
		if (rset.next()) {
			ret.setStatement(stmt);
			ret.setCustomerId(customerId);
			ret.setOfferId(offerId);
			ret.setRevisionNumber(revisionNumber);
			ret.setAcceptStatus( rset.getString(3) );
			ret.setAcceptDateTime( rset.getTimestamp(4) );
			ret.setUserIdName( rset.getString(7) );
			ret.setNameOfAcceptPerson( rset.getString(8) );
			return ret;
		}
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
	
	return null;
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
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public void setStatement(java.sql.Statement stmt) {
		this.stmt = stmt;
	}
}

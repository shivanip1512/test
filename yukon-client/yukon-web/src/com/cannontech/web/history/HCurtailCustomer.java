package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/13/2001 3:04:26 PM)
 * @author: 
 */
public class HCurtailCustomer {
	private java.sql.Statement stmt = null;
	private long deviceId = 0;
	private long customerId = 0;
	private String customerName = null;
	private String requireAck = null;
/**
 * HCurtailCustomer constructor comment.
 */
public HCurtailCustomer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 3:46:19 PM)
 * @return com.cannontech.web.history.HCurtailCustomerActivity
 */
public HCurtailCustomerActivity getCurtailCustomerActivity(long curtailReferenceId) {
	String queryStr = "SELECT * FROM LMCURTAILCUSTOMERACTIVITY_VIEW ";
	queryStr += "WHERE CUSTOMERID = " + customerId + " AND CURTAILREFERENCEID = " + curtailReferenceId;

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		HCurtailCustomerActivity ret = new HCurtailCustomerActivity();
		if (rset.next()) {
			ret.setStatement(stmt);
			ret.setCustomerId(customerId);
			ret.setCurtailReferenceId(curtailReferenceId);
			ret.setAckStatus( rset.getString(5) );
			ret.setAckDateTime( rset.getTimestamp(6) );
			ret.setNameOfAckPerson( rset.getString(7) );
			ret.setAckLateFlag( rset.getString(8) );
			return ret;
		}
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
	
	return null;
}
	public long getCustomerId() {
		return customerId;
	}
	public String getCompanyName() {
		return customerName;
	}
	public long getDeviceId() {
		return deviceId;
	}
	public String getRequireAck() {
		return requireAck;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public void setCompanyName(String customerName) {
		this.customerName = customerName;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public void setRequireAck(String requireAck) {
		this.requireAck = requireAck;
	}
	public void setStatement(java.sql.Statement stmt) {
		this.stmt = stmt;
	}
}

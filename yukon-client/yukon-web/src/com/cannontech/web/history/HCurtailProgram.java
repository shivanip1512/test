package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2001 3:49:07 PM)
 * @author: 
 */
public class HCurtailProgram {
	private java.sql.Statement stmt = null;
	private long deviceId = 0;
	private String programName = null;
/**
 * HEnergyExchangeProgram constructor comment.
 */
public HCurtailProgram() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 11:34:36 AM)
 * @return com.cannontech.web.history.HEnergyExchangeCustomer[]
 */
public HCurtailCustomer[] getCurtailCustomers() {
	String queryStr = "SELECT CICUSTOMERBASE.CUSTOMERID,CICUSTOMERBASE.COMPANYNAME,LMPROGRAMCURTAILCUSTOMERLIST.REQUIREACK " +
								" FROM LMPROGRAMCURTAILCUSTOMERLIST,CICUSTOMERBASE " +
								" WHERE LMPROGRAMCURTAILCUSTOMERLIST.PROGRAMID=" + deviceId + " AND LMPROGRAMCURTAILCUSTOMERLIST.CUSTOMERID=CICUSTOMERBASE.CUSTOMERID" +
								" ORDER BY CICUSTOMERBASE.CUSTOMERID";
																							
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		java.util.ArrayList customerList = new java.util.ArrayList();
		while (rset.next()) {
			HCurtailCustomer customer = new HCurtailCustomer();
			
			customer.setStatement(stmt);
			customer.setDeviceId(deviceId);
			customer.setCustomerId( rset.getLong(1) );
			customer.setCompanyName( rset.getString(2) );
			customer.setRequireAck( rset.getString(3) );

			customerList.add(customer);
		}

		HCurtailCustomer[] ret = new HCurtailCustomer[ customerList.size() ];
		customerList.toArray(ret);
		
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HCurtailCustomer[0];
	}
}
	public long getDeviceId() {
		return deviceId;
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

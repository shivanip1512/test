/*
 * Created on Feb 11, 2004
 */
package com.cannontech.database.db.activity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.DBPersistent;

/**
 * @author aaron
  */
public class ActivityLog extends DBPersistent {
	private static final Integer UNUSED_ID = new Integer(-1);
	
	private Integer _activityLogID = UNUSED_ID;
	private Date _timestamp = new Date();
	private Integer _userID = UNUSED_ID; 
	private Integer _accountID = UNUSED_ID;
	private Integer _energyCompanyID = UNUSED_ID;
	private Integer _customerID = UNUSED_ID;
    private Integer _paoID = UNUSED_ID;
    private String _action = "";
    private String _description = "";
    
    public static final String TABLE_NAME = "ActivityLog";	
    public static final String CONSTRAINT_COLUMNS[] = { "ActivityLogID" };
    public static final String COLUMNS[] = { 
		"TIMESTAMP", "USERID", "ACCOUNTID",
		"ENERGYCOMPANYID", "CUSTOMERID", "PAOID", "ACTION", "DESCRIPTION" };
        
	public void add() throws SQLException {
		if(getActivityLogID() == UNUSED_ID) {
			setActivityLogID(getNextUserID(getDbConnection()));
		}
		Object addValues[] = { 
			getActivityLogID(), getTimestamp(), getUserID(), getAccountID(),
			getEnergyCompanyID(), getCustomerID(), getPaoID(), getAction(), getDescription()
		};
		add(TABLE_NAME, addValues);
	}

	public void delete() throws SQLException {
		delete(TABLE_NAME, "ActivityLogID", getActivityLogID());		
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object constraintValues[] = { getActivityLogID() };
		Object results[] = retrieve(COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == COLUMNS.length) {
			setActivityLogID((Integer) results[0]);
			setTimestamp((Date) results[1]);
			setUserID((Integer) results[2]);
			setAccountID((Integer) results[3]);
			setEnergyCompanyID((Integer) results[4]);
			setCustomerID((Integer) results[5]);
			setPaoID((Integer) results[6]);
			setAction((String) results[7]);
			setDescription((String) results[8]);
		}
		else 
			throw new Error(getClass() + " - Incorrect Number of results retreived");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		throw new RuntimeException("Update not implemented");
	}

	/**
	 * @return
	 */
	public Integer getAccountID() {
		return _accountID;
	}

	/**
	 * @return
	 */
	public String getAction() {
		return _action;
	}

	/**
	 * @return
	 */
	public Integer getActivityLogID() {
		return _activityLogID;
	}

	/**
	 * @return
	 */
	public Integer getCustomerID() {
		return _customerID;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return _description;
	}

	/**
	 * @return
	 */
	public Integer getEnergyCompanyID() {
		return _energyCompanyID;
	}

	/**
	 * @return
	 */
	public Integer getPaoID() {
		return _paoID;
	}

	/**
	 * @return
	 */
	public Date getTimestamp() {
		return _timestamp;
	}

	/**
	 * @return
	 */
	public Integer getUserID() {
		return _userID;
	}

	/**
	 * @param integer
	 */
	public void setAccountID(Integer accountID) {
		_accountID = accountID;
	}

	public void setAccountID(int accountID) {
		setAccountID(new Integer(accountID));
	}
	
	/**
	 * @param string
	 */
	public void setAction(String action) {
		_action = action;
	}

	/**
	 * @param integer
	 */
	public void setActivityLogID(Integer logid) {
		_activityLogID = logid;
	}

	public void setActivityLogID(int logid) {
		setActivityLogID(new Integer(logid));
	}
	
	/**
	 * @param integer
	 */
	public void setCustomerID(Integer customerID) {
		_customerID = customerID;
	}
	
	public void setCustomerID(int customerID) {
		setCustomerID(new Integer(customerID));
	}
	
	/**
	 * @param string
	 */
	public void setDescription(String string) {
		_description = string;
	}

	/**
	 * @param integer
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		_energyCompanyID = energyCompanyID;
	}

	public void setEnergyCompanyID(int energyCompanyID) {
		setEnergyCompanyID(new Integer(energyCompanyID));
	}
	/**
	 * @param integer
	 */
	public void setPaoID(Integer paoID) {
		_paoID = paoID; 
	}
	
	public void setPaoID(int paoID) {
		setPaoID(new Integer(paoID));
	}

	/**
	 * @param date
	 */
	public void setTimestamp(Date timestamp) {
		_timestamp = timestamp;
	}

	/**
	 * @param integer
	 */
	public void setUserID(Integer userID) {
		_userID = userID;
	}

	public void setUserID(int userID) {
		setUserID(new Integer(userID));
	}
	

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public static final Integer getNextUserID( java.sql.Connection conn ) {
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be null.");
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		String sql = "SELECT max(ActivityLogID)+1 " + "FROM " + TABLE_NAME;
		int newID = 0;
		
		try	{
			pstmt = conn.prepareStatement(sql.toString());
			rset = pstmt.executeQuery();							
			
			if( rset.next() ) {
				newID = rset.getInt(1);
			}
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if( pstmt != null ) pstmt.close();
			} 
			catch( java.sql.SQLException e2 ) {
				CTILogger.error( e2.getMessage(), e2 ); //something is up
			}	
		}			
		return new Integer(newID);
	}
}

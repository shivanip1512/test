package com.cannontech.database.data.user;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/*** 
 * @author alauinger
 */
public class YukonUser extends DBPersistent implements com.cannontech.database.db.CTIDbChange {
	
	private com.cannontech.database.db.user.YukonUser yukonUser;
	
	private Vector yukonGroups; //type = com.cannontech.database.db.user.YukonGroup
	//private Vector yukonRoles;  //type = com.cannontech.database.db.user.YukonRole
	
	public YukonUser() {
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getYukonUser().setDbConnection( conn );
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getYukonUser().add();
		getYukonUser().setDbConnection(null);
		
		for (int i = 0; i < getYukonGroups().size(); i++) {
			Object[] addValues = {
				getYukonUser().getUserID(), ((com.cannontech.database.db.user.YukonGroup) getYukonGroups().get(i)).getGroupID()
			};
			add( "YukonUserGroup", addValues );
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete( "YukonUserGroup", "UserID", getYukonUser().getUserID() );
		delete( "YukonUserRole", "UserID", getYukonUser().getUserID() );
		getYukonUser().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getYukonUser().retrieve();
		getYukonUser().setDbConnection(null);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getYukonUser().update();
		getYukonUser().setDbConnection(null);
	}

	/**
	 * Returns the yukonGroups.
	 * @return Vector
	 */
	public Vector getYukonGroups() {
		if (yukonGroups == null)
			yukonGroups = new Vector();
		return yukonGroups;
	}

	/**
	 * Returns the yukonRoles.
	 * @return Vector
	 */
/*	public Vector getYukonRoles() {
		if (yukonRoles == null)
			yukonRoles = new Vector();
		return yukonRoles;
	}
*/
	/**
	 * Returns the yukonUser.
	 * @return com.cannontech.database.db.user.YukonUser
	 */
	public com.cannontech.database.db.user.YukonUser getYukonUser() {
		if(yukonUser == null)
			yukonUser = new com.cannontech.database.db.user.YukonUser();
		return yukonUser;
	}

	/**
	 * Sets the yukonGroups.
	 * @param yukonGroups The yukonGroups to set
	 */
	public void setYukonGroups(Vector yukonGroups) {
		this.yukonGroups = yukonGroups;
	}

	/**
	 * Sets the yukonRoles.
	 * @param yukonRoles The yukonRoles to set
	 */
/*	public void setYukonRoles(Vector yukonRoles) {
		this.yukonRoles = yukonRoles;
	}
*/
	/**
	 * Sets the yukonUser.
	 * @param yukonUser The yukonUser to set
	 */
	public void setYukonUser(
		com.cannontech.database.db.user.YukonUser yukonUser) {
		this.yukonUser = yukonUser;
	}
	
		/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return getYukonUser().getPassword();
	}


	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public Integer getUserID() {
		return getYukonUser().getUserID();
	}


	/**
	 * Returns the username.
	 * @return String
	 */
	public String getUsername() {
		return getYukonUser().getUsername();
	}


	/**
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		getYukonUser().setPassword(password);
	}


	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	public void setUserID(Integer userID) {
		getYukonUser().setUserID(userID);		
	}


	/**
	 * Sets the username.
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		getYukonUser().setUsername(username);		
	}


	/**
	 * Returns the lastLogin.
	 * @return Date
	 */
	public Date getLastLogin() {
		return getYukonUser().getLastLogin();	
	}


	/**
	 * Returns the loginCount.
	 * @return Integer
	 */
	public Integer getLoginCount() {
		return getYukonUser().getLoginCount();		
	}


	/**
	 * Returns the status.
	 * @return String
	 */
	public String getStatus() {
		return getYukonUser().getStatus();		
	}


	/**
	 * Sets the lastLogin.
	 * @param lastLogin The lastLogin to set
	 */
	public void setLastLogin(Date lastLogin) {
		getYukonUser().setLastLogin(lastLogin);
	}


	/**
	 * Sets the loginCount.
	 * @param loginCount The loginCount to set
	 */
	public void setLoginCount(Integer loginCount) {
		getYukonUser().setLoginCount(loginCount);		
	}


	/**
	 * Sets the status.
	 * @param status The status to set
	 */
	public void setStatus(String status) {
		getYukonUser().setStatus(status);		
	}




	/**
	 * @see com.cannontech.database.db.CTIDbChange#getDBChangeMsgs(int)
	 */
	public DBChangeMsg[] getDBChangeMsgs(int typeOfChange) {
		com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
		{
			new com.cannontech.message.dispatch.message.DBChangeMsg(
					getYukonUser().getUserID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_YUKON_USER_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_YUKON_USER,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_YUKON_USER,
					typeOfChange)
		};

		return msgs;
	}

}

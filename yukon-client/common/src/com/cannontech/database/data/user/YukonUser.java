package com.cannontech.database.data.user;

import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonUser extends DBPersistent {
	
	private com.cannontech.database.db.user.YukonUser yukonUser;
	private Vector yukonGroups; //type = com.cannontech.database.db.user.YukonGroup
	private Vector yukonRoles;  //type = com.cannontech.database.db.user.YukonRole
	
	public YukonUser() {
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
	}

	/**
	 * Returns the yukonGroups.
	 * @return Vector
	 */
	public Vector getYukonGroups() {
		return yukonGroups;
	}

	/**
	 * Returns the yukonRoles.
	 * @return Vector
	 */
	public Vector getYukonRoles() {
		return yukonRoles;
	}

	/**
	 * Returns the yukonUser.
	 * @return com.cannontech.database.db.user.YukonUser
	 */
	public com.cannontech.database.db.user.YukonUser getYukonUser() {
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
	public void setYukonRoles(Vector yukonRoles) {
		this.yukonRoles = yukonRoles;
	}

	/**
	 * Sets the yukonUser.
	 * @param yukonUser The yukonUser to set
	 */
	public void setYukonUser(
		com.cannontech.database.db.user.YukonUser yukonUser) {
		this.yukonUser = yukonUser;
	}

}

package com.cannontech.database.data.user;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.user.YukonUserRole;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/*** 
 * @author alauinger
 */
public class YukonUser extends DBPersistent implements com.cannontech.database.db.CTIDbChange, EditorPanel
{	
	private com.cannontech.database.db.user.YukonUser yukonUser;
	
	private Vector yukonGroups; //type = com.cannontech.database.db.user.YukonGroup
	private Vector yukonUserRoles;  //type = com.cannontech.database.db.user.YukonUserRole
	
	public YukonUser() 
	{
		super();
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getYukonUser().setDbConnection( conn );
		
		for (int i = 0; i < getYukonUserRoles().size(); i++) 
		{
			((DBPersistent)getYukonUserRoles().get(i)).setDbConnection( conn );
		}
		
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException 
	{
		if( getYukonUser().getUserID() == null )
			setUserID(
				com.cannontech.database.db.user.YukonUser.getNextUserID(getDbConnection()) );

		getYukonUser().add();

		
		for (int i = 0; i < getYukonGroups().size(); i++) 
		{
			Object[] addValues = 
			{
				getYukonUser().getUserID(), 
				((com.cannontech.database.db.user.YukonGroup) getYukonGroups().get(i)).getGroupID()
			};

			add( "YukonUserGroup", addValues );
		}

		
		for (int i = 0; i < getYukonUserRoles().size(); i++) 
		{
			((DBPersistent)getYukonUserRoles().get(i)).add();
		}
		
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete( YukonUserRole.TABLE_NAME, "UserID", getYukonUser().getUserID() );
		delete( "YukonUserGroup", "UserID", getYukonUser().getUserID() );
		getYukonUser().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException 
	{
		getYukonUser().retrieve();
		
		
		//add the roles this user has
		YukonUserRole[] roles = YukonUserRole.getYukonUserRoles( 
						getUserID().intValue(), getDbConnection() );
 
 		for( int i = 0; i < roles.length; i++ )
 			getYukonUserRoles().add( roles[i] );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {

		setUserID( getYukonUser().getUserID() );

		getYukonUser().update();
		
		//first delete the current userRoles
		delete( YukonUserRole.TABLE_NAME, "UserID", getYukonUser().getUserID() );		
		for (int i = 0; i < getYukonUserRoles().size(); i++) 
		{
			((DBPersistent)getYukonUserRoles().get(i)).add();
		}
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
	 * Sets the yukonUser.
	 * @param yukonUser The yukonUser to set
	 */
	public void setYukonUser(
		com.cannontech.database.db.user.YukonUser yukonUser) {
		this.yukonUser = yukonUser;
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public Integer getUserID() {
		return getYukonUser().getUserID();
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public void setUserID( Integer userID_ ) {
		getYukonUser().setUserID( userID_ );

		for (int i = 0; i < getYukonUserRoles().size(); i++) 
		{
			((YukonUserRole)getYukonUserRoles().get(i)).setUserID( userID_ );
		}
	}

	
	public String toString()
	{
		return getYukonUser().getUsername();
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

	/**
	 * Returns the yukonUserRoles.
	 * @return Vector
	 */
	public Vector getYukonUserRoles() {
		if( yukonUserRoles == null )
			yukonUserRoles = new Vector(10);

		return yukonUserRoles;
	}

}

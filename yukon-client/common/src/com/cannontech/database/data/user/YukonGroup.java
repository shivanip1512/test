package com.cannontech.database.data.user;

import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/*** 
 * @author rneuharth
 */
public class YukonGroup extends DBPersistent implements com.cannontech.database.db.CTIDbChange, EditorPanel, IYukonRoleContainer
{	
	private com.cannontech.database.db.user.YukonGroup yukonGroup;
	private Vector yukonGroupRoles;  //type = com.cannontech.database.db.user.YukonGroupRole
	
	
	public YukonGroup() 
	{
		super();
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getYukonGroup().setDbConnection( conn );
		
		for (int i = 0; i < getYukonGroupRoles().size(); i++) 
		{
			((DBPersistent)getYukonGroupRoles().get(i)).setDbConnection( conn );
		}
		
	}


	public Integer getID()
	{
		return getGroupID();
	}
	
	public Vector getYukonRoles()
	{
		return getYukonGroupRoles();	
	}


	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException 
	{
		if( getYukonGroup().getGroupID() == null )
			setGroupID(
				com.cannontech.database.db.user.YukonGroup.getNextGroupID(getDbConnection()) );

		getYukonGroup().add();

		
		for (int i = 0; i < getYukonGroupRoles().size(); i++) 
		{
			com.cannontech.database.db.user.YukonGroupRole groupRole =
					(com.cannontech.database.db.user.YukonGroupRole) getYukonGroupRoles().get(i);
			groupRole.setGroupID( getYukonGroup().getGroupID() );
			groupRole.add();
		}
		
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException 
	{
		delete( YukonGroupRole.TABLE_NAME, "GroupID", getYukonGroup().getGroupID() );
		delete( com.cannontech.database.db.user.YukonGroup.TBL_YUKON_USER_GROUP, 
					"GroupID", getYukonGroup().getGroupID() );

		getYukonGroup().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException 
	{
		getYukonGroup().retrieve();
		

		//add the roles this user has
		YukonGroupRole[] roles = YukonGroupRole.getYukonGroupRoles( 
						getGroupID().intValue(), getDbConnection() );
 
 		for( int i = 0; i < roles.length; i++ )
 			getYukonGroupRoles().add( roles[i] );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException 
	{
		setGroupID( getYukonGroup().getGroupID() );

		getYukonGroup().update();
		
		//first delete the current userRoles
		delete( YukonGroupRole.TABLE_NAME, "GroupID", getYukonGroup().getGroupID() );		
		
		// Insert the old entries (entries with ID set) first to avoid primary key conflict
		Vector grpRolesNoID = new Vector();		
		for (int i = 0; i < getYukonGroupRoles().size(); i++) 
		{
			YukonGroupRole grpRole = (YukonGroupRole) getYukonGroupRoles().get(i);
			if (grpRole.getGroupRoleID() != null)
				grpRole.add();
			else
				grpRolesNoID.addElement( grpRole );
		}
		for (int i = 0; i < grpRolesNoID.size(); i++)
			((YukonGroupRole) grpRolesNoID.get(i)).add();
	}


	/**
	 * Returns the yukonUser.
	 * @return com.cannontech.database.db.user.YukonUser
	 */
	public com.cannontech.database.db.user.YukonGroup getYukonGroup() 
	{
		if(yukonGroup == null)
			yukonGroup = new com.cannontech.database.db.user.YukonGroup();
		
		return yukonGroup;
	}

	/**
	 * Sets the yukonUser.
	 * @param yukonUser The yukonUser to set
	 */
	public void setYukonGroup(
		com.cannontech.database.db.user.YukonGroup yukonGroup) {
		this.yukonGroup = yukonGroup;
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public Integer getGroupID() 
	{
		return getYukonGroup().getGroupID();
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public void setGroupID( Integer groupID_ ) 
	{
		getYukonGroup().setGroupID( groupID_ );

		for (int i = 0; i < getYukonGroupRoles().size(); i++) 
		{
			((YukonGroupRole)getYukonGroupRoles().get(i)).setGroupID( groupID_ );
		}
	}

	
	public String toString()
	{
		return getYukonGroup().getGroupName();
	}


	/**
	 * @see com.cannontech.database.db.CTIDbChange#getDBChangeMsgs(int)
	 */
	public DBChangeMsg[] getDBChangeMsgs(int typeOfChange) {
		com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
		{
			new com.cannontech.message.dispatch.message.DBChangeMsg(
					getYukonGroup().getGroupID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_YUKON_USER_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_YUKON_USER_GROUP,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_YUKON_USER,
					typeOfChange)
		};

		return msgs;
	}

	/**
	 * Returns the yukonUserRoles.
	 * @return Vector
	 */
	public Vector getYukonGroupRoles() {
		if( yukonGroupRoles == null )
			yukonGroupRoles = new Vector(10);

		return yukonGroupRoles;
	}

}

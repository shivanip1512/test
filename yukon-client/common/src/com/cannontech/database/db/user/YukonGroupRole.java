package com.cannontech.database.db.user;

import java.sql.SQLException;
import java.util.ArrayList;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author alauinger
 */
public class YukonGroupRole extends DBPersistent implements IDefinedYukonRole, CTIDbChange
{
	public static final String TABLE_NAME = "YukonGroupRole";

	public static final String[] SETTER_COLUMNS  = 
	{ 
		"GroupID","RoleID","RolePropertyID","Value" 
	};

	public static final String[] CONSTRAINT_COLUMNS  = { "GroupRoleID" }; 
	
	private Integer groupRoleID = null;
	private Integer groupID = null;
	private Integer roleID = null;
	private Integer rolePropertyID = null;
	private String value = null;

	public YukonGroupRole() {
		super();
	}

	public YukonGroupRole(Integer groupRoleID_, Integer groupID_, Integer roleID_, Integer rolePropertyID_, String value_) {
		this();
		setGroupRoleID(groupRoleID_);
		setGroupID( groupID_ );
		setRoleID( roleID_ );
		setRolePropertyID(rolePropertyID_);
		setValue( value_ );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Integer
	 */
	public static synchronized YukonGroupRole[] getYukonGroupRoles( int groupID_, java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		ArrayList list = new ArrayList(32);
		
		try 
		{		
			 pstmt = conn.prepareStatement(
					"select GroupRoleID, GroupID, RoleID, RolePropertyID, Value " +
					"from " + TABLE_NAME + " " +
					"where GroupID = ?" );
		    	
			 pstmt.setInt( 1, groupID_ );
			 
			 rset = pstmt.executeQuery();
				
			 //get the first returned result
			 while( rset.next() )
			 {
				YukonGroupRole r = new YukonGroupRole(
					new Integer(rset.getInt("GroupRoleID")),
					new Integer(rset.getInt("GroupID")),
					new Integer(rset.getInt("RoleID")),
					new Integer(rset.getInt("RolePropertyID")),
					rset.getString("Value") );
			 	
				list.add( r );
			 }
		    
		}
		catch (java.sql.SQLException e) 
		{
			 e.printStackTrace();
		}
		finally 
		{
			SqlUtils.close(rset, pstmt);
		}
		
		YukonGroupRole[] roles = new YukonGroupRole[list.size()];
		return (YukonGroupRole[])list.toArray( roles );
	}

	public static final Integer getNextGroupRoleID() {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("YukonGroupRole");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException 
	{
		if( getGroupRoleID() == null )
			setGroupRoleID(getNextGroupRoleID());

		Object[] addValues = 
		{ 
			getGroupRoleID(), getGroupID(), getRoleID(), getRolePropertyID(), getValue() 
		};
		add(TABLE_NAME, addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException 
	{
		Object[] vals = 
		{ 
			getGroupRoleID()
		};
		
		delete(TABLE_NAME, CONSTRAINT_COLUMNS, vals);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException 
	{
		Object[] constraintValues = { getGroupRoleID() };
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == SETTER_COLUMNS.length) 
		{
			setGroupID( (Integer) results[0]);
			setRoleID( (Integer) results[1]);
			setRolePropertyID( (Integer) results[2]);
			setValue( (String) results[3] );
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException 
	{
		Object[] setValues = 
		{ 
			getGroupID(),
			getRoleID(),
			getRolePropertyID(),
			getValue() 
		};

		Object[] constraintValues = { getGroupRoleID() };		
		
		update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
	}

	/**
	 * Returns the roleID.
	 * @return Integer
	 */
	public Integer getRoleID() {
		return roleID;
	}

	/**
	 * Sets the roleID.
	 * @param roleID The roleID to set
	 */
	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}


	/**
	 * Returns the groupID.
	 * @return Integer
	 */
	public Integer getGroupID() {
		return groupID;
	}

	/**
	 * Returns the value.
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the groupID.
	 * @param groupID The groupID to set
	 */
	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the groupRoleID.
	 * @return Integer
	 */
	public Integer getGroupRoleID() {
		return groupRoleID;
	}

	/**
	 * Sets the groupRoleID.
	 * @param groupRoleID The groupRoleID to set
	 */
	public void setGroupRoleID(Integer userRoleID) {
		this.groupRoleID = userRoleID;
	}

	/**
	 * Returns the rolePropertyID.
	 * @return Integer
	 */
	public Integer getRolePropertyID() {
		return rolePropertyID;
	}

	/**
	 * Sets the rolePropertyID.
	 * @param rolePropertyID The rolePropertyID to set
	 */
	public void setRolePropertyID(Integer rolePropertyID) {
		this.rolePropertyID = rolePropertyID;
	}

    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] msgs =
        {
            new DBChangeMsg(getGroupID().intValue(),
                            DBChangeMsg.CHANGE_YUKON_USER_DB,
                            DBChangeMsg.CAT_YUKON_USER_GROUP,
                            DBChangeMsg.CAT_YUKON_USER,
                            dbChangeType)
        };

        return msgs;
    }
}

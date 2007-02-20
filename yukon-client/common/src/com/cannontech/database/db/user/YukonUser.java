package com.cannontech.database.db.user;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.UserUtils;

/**
 * @author alauinger
 */
public class YukonUser extends DBPersistent 
{		
	public static final String TABLE_NAME = "YukonUser";	
		
	private Integer userID = null;
	private String username = null;
	private String status = UserUtils.STATUS_DISABLED;
    private AuthType authType = AuthType.PLAIN;

	private static final String[] SELECT_COLUMNS = 
	{ 	
		"Username", 
		"Status",
        "AuthType"
	};
	
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = { getUserID(), getUsername(), getStatus(), getAuthType().name() };
		add(TABLE_NAME, addValues);
	}


	/**
	 * This method was created in VisualAge.
	 * @return LMControlAreaTrigger[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final YukonUser getCustomerUser(Integer userID, Connection conn) throws java.sql.SQLException
	{
		YukonUser user = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		String sql = "SELECT l.UserID, l.UserName, l.AuthType, l.LoginCount, l.LastLogin, l.Status " +
			"FROM " + TABLE_NAME + " l " +
			"WHERE l.UserID = ?";// and l.UserID > " + UserUtils.USER_INVALID_ID;
	
		try
		{		
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setInt( 1, userID.intValue() );
				
				rset = pstmt.executeQuery();							
		
				while( rset.next() )
				{
					user = new YukonUser();
					
					user.setDbConnection(conn);
					user.setUserID( new Integer(rset.getInt("UserID")) );
					user.setUsername( rset.getString("UserName") );
					user.setStatus( rset.getString("Status") );
                    user.setAuthType(AuthType.valueOf(rset.getString("AuthType")));
				}
						
			}
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
	
		return user;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public static final Integer getNextUserID( java.sql.Connection conn )
	{
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        int nextValue = nextValueHelper.getNextValue(TABLE_NAME);
        return nextValue;
	}
	
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete(TABLE_NAME, "UserID", getUserID());
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		String[] constraintColumns = { "UserID" };
		Object[] constraintValues = { getUserID() };
		
		Object[] results = retrieve(SELECT_COLUMNS, TABLE_NAME, constraintColumns, constraintValues);
		
		if(results.length == SELECT_COLUMNS.length) {
			setUsername((String) results[0]);
			setStatus((String) results[1]);
			setAuthType(AuthType.valueOf((String) results[2]));
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = { getUsername(), getStatus(), getAuthType().name() };
		
		String[] constraintColumns = { "UserID" };
		Object[] constraintValues = { getUserID() };
		
		update(TABLE_NAME, SELECT_COLUMNS, setValues, constraintColumns, constraintValues);
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public Integer getUserID() {
		return userID;
	}

	/**
	 * Returns the username.
	 * @return String
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	/**
	 * Sets the username.
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the status.
	 * @return String
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * @param status The status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


    public AuthType getAuthType() {
        return authType;
    }


    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

}

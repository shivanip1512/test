package com.cannontech.database.db.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.user.UserUtils;

/**
 * @author alauinger
 */
public class YukonUser extends DBPersistent 
{		
	public static final String TABLE_NAME = "YukonUser";	
		
	private Integer userID = null;
	private String username = null;
	private String password = null;
	private String status = UserUtils.STATUS_DISABLED;

	private static final String[] SELECT_COLUMNS = 
	{ 	
		"Username", 
		"Password",
		"Status" 
	};
	
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = { getUserID(), getUsername(), getPassword(), getStatus() };
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
	
		String sql = "SELECT l.UserID, l.UserName, l.Password, l.LoginCount, l.LastLogin, l.Status " +
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
					user.setPassword( rset.getString("Password") );
					user.setStatus( rset.getString("Status") );
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
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
		
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		String sql = "SELECT max(UserID) as UserID " + "FROM " + TABLE_NAME;
		int newID = 0;
		
		try
		{
			pstmt = conn.prepareStatement(sql.toString());
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				newID = rset.getInt("UserID") + 1;
				if( newID < 1 )
					newID = 1;
				break;
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
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 ); //something is up
			}	
		}	
		
		return new Integer( newID );
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
			setPassword((String) results[1]);
			setStatus((String) results[2]);
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = { getUsername(), getPassword(), getStatus() };
		
		String[] constraintColumns = { "UserID" };
		Object[] constraintValues = { getUserID() };
		
		update(TABLE_NAME, SELECT_COLUMNS, setValues, constraintColumns, constraintValues);
	}

	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
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
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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

}

package com.cannontech.database.data.lite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cannontech.database.db.user.YukonUser;

/**
 * @author alauinger
 */
public class LiteYukonUser extends LiteBase {
	private String username;
	private String password;
	private String status;
	
	public LiteYukonUser() {
		this(0,null,null,null);
	}
	
	public LiteYukonUser(int id) {
		this(id,null,null,null);
	}
	
	public LiteYukonUser(int id, String username, String password, String status) {
		setLiteType(LiteTypes.YUKON_USER);
		setUserID(id);
		setUsername(username);
		setPassword(password);		
		setStatus(status);
	}
	
	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}


	public void retrieve( String dbAlias )
	{
		
		String sql = 
			"SELECT Username,Password,Status FROM " + YukonUser.TABLE_NAME + " " +
			"WHERE UserID = " + getUserID();
   		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		
		try 
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(
							dbAlias );

			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);

			
			if( rset.next() ) 
			{
      		setUsername( rset.getString(1).trim() );
      		setPassword( rset.getString(2).trim() );
      		setStatus( rset.getString(3) );
         }
         
		}
		catch(SQLException e ) 
		{
      	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
      finally 
      {
         	try {
            	if( stmt != null )
               	stmt.close();
            	if( conn != null )
               	conn.close();
         	}
         	catch( java.sql.SQLException e ) {
            	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
         	}
      }
      
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
	 * Sets the username.
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the userID.
	 * @return int
	 */
	public int getUserID() {
		return getLiteID();
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	public void setUserID(int userID) {
		setLiteID(userID);
	}
	
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString()
	{
		return getUsername();
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

package com.cannontech.database.db.graph;

/**
 * Insert the type's description here.
 * Creation date: (12/6/99 4:08:49 PM)
 * @author: 
 */
public class UserInfo extends com.cannontech.database.db.DBPersistent implements java.io.Serializable {
	public static final String USERINFO = "USERINFO";
	
	private java.lang.Long userID = null;
	private java.lang.String username = null;
	private java.lang.String password = null;
	private java.lang.String databaseAlias = "yukon";
	
	private final String tableName = "UserInfo";
	
/**
 * UserInfo constructor comment.
 */
public UserInfo() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:53:50 AM)
 */
public void add() throws java.sql.SQLException
{
	Object addValues[] = { getUserID(), getUsername(), getPassword(), getDatabaseAlias() };
	
	add( tableName, addValues );	
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/99 10:01:20 AM)
 * @return boolean
 * @param username java.lang.String
 * @param password java.lang.String
 */
public static boolean authenticate(UserInfo info) {

	return authenticate(info, "yukon");
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/99 10:01:20 AM)
 * @return boolean
 * @param username java.lang.String
 * @param password java.lang.String
 */
public static boolean authenticate(UserInfo info, String databaseAlias) {

	boolean validUser = false;
	
	try
	{
		com.cannontech.database.SqlStatement stmt = 
				new com.cannontech.database.SqlStatement("SELECT Password,UserID,DatabaseAlias FROM UserInfo WHERE Username='" + info.getUsername() + "'", databaseAlias );

	    stmt.execute();
	    
		if( stmt.getRowCount() > 0 )
		{
	
			String realPassword = (String) stmt.getRow(0)[0];
com.cannontech.clientutils.CTILogger.info("realpassword for " + info.getUsername() + " is " + realPassword );
			if( realPassword.equals( info.getPassword() ) )
			{
				validUser = true;
			}

			info.setUserID( new Long( ((java.math.BigDecimal) stmt.getRow(0)[1]).longValue()));
			info.setDatabaseAlias( (String) stmt.getRow(0)[2] );			
		}
	}
	catch( Exception e )
	{
		System.err.println( e.getMessage() );
	}
	
	return validUser;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:54:01 AM)
 */
public void delete() throws java.sql.SQLException
{
	delete( tableName, "UserID", getUserID() );	
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/00 4:09:46 PM)
 * @return java.lang.String
 */
public java.lang.String getDatabaseAlias() {
	return databaseAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/99 10:03:32 AM)
 * @return java.lang.String
 */
public java.lang.String getPassword() {
	return password;
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/99 4:09:05 PM)
 * @return java.lang.Integer
 */
public java.lang.Long getUserID() {
	return userID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/99 10:03:21 AM)
 * @return java.lang.String
 */
public java.lang.String getUsername() {
	return username;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:53:41 AM)
 */
public void retrieve() throws java.sql.SQLException
{
	String selectColumns[] = { "Username", "Password", "DatabaseAlias" };
	String constraintColumns[] = { "UserID" };
	Object constraintValues[] = { getUserID() };
	
	Object results[] = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setUsername( (String) results[0] );
		setPassword( (String) results[1] );
		setDatabaseAlias( (String) results[2] );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/00 4:09:46 PM)
 * @param newDatabaseAlias java.lang.String
 */
public void setDatabaseAlias(java.lang.String newDatabaseAlias) {
	databaseAlias = newDatabaseAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/99 10:03:32 AM)
 * @param newPassword java.lang.String
 */
public void setPassword(java.lang.String newPassword) {
	password = newPassword;
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/99 4:09:05 PM)
 * @param newUserID java.lang.Integer
 */
public void setUserID(java.lang.Long newUserID) {
	userID = newUserID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/99 10:03:21 AM)
 * @param newUsername java.lang.String
 */
public void setUsername(java.lang.String newUsername) {
	username = newUsername;
}
/**
 * Insert the method's description here.
 * Creation date: (12/17/99 9:49:48 AM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buf = new StringBuffer();

	buf.append("UserInfo - " + getUserID() + " - " + getUsername() + " - " + getPassword() + " - " + getDatabaseAlias() + "\n");
	
	return buf.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:54:09 AM)
 */
public void update() throws java.sql.SQLException
{
	String setColumns[] = { "Username", "Password", "DatabaseAlias" };
	Object setValues[] = { getUsername(), getPassword(), getDatabaseAlias() };

	String constraintColumns[] = { "UserID" };
	Object constraintValues[] = { getUserID() };

	update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}

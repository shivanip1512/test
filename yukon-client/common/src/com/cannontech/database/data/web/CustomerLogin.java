package com.cannontech.database.data.web;

/**
 * Creation date: (3/27/2001 1:20:13 PM)
 * @author: Aaron Lauinger
 */
public class CustomerLogin extends com.cannontech.database.db.DBPersistent {

	public static final String tableName = "CustomerLogin";

	private long id;
	private String username = "";
	private String password = "";	
	private String loginType = "";
	private int loginCount;
	java.util.Date lastLogin;
	
/**
 * CustomerLogin constructor comment.
 */
public CustomerLogin() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
		{ 
			new Long(getId()), 
			getUsername(),
			getPassword(),
			getLoginType(),
			new Integer(getLoginCount()),
			getLastLogin()
		};

	super.add( tableName, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException
{
	super.delete( tableName, "LoginID", new Long(getId()) );
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @return long
 */
public long getId() {
	return id;
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @return java.util.Date
 */
public java.util.Date getLastLogin() {
	return lastLogin;
}
/**
 * Creation date: (3/27/2001 1:49:02 PM)
 * @return int
 */
public int getLoginCount() {
	return loginCount;
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @return java.lang.String
 */
public java.lang.String getLoginType() {
	return loginType;
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @return java.lang.String
 */
public java.lang.String getPassword() {
	return password;
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @return java.lang.String
 */
public java.lang.String getUsername() {
	return username;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	String[] selectColumns =
		{
			"Username",
			"Password",
			"LoginType",
			"LoginCount",
			"LastLogin"
		};
	
	String[] constraintColumns = { "LoginID" };
	Object[] constraintValues = { new Long(getId()) };

	Object[] results = super.retrieve(selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setUsername( (String) results[0] );
		setPassword( (String) results[1] );
		setLoginType( (String) results[2] );
		setLoginCount( ((Integer) results[3]).intValue() );
		setLastLogin( (java.util.Date) results[4] );
	}
		
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @param newId long
 */
public void setId(long newId) {
	id = newId;
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @param newLastLogin java.util.Date
 */
public void setLastLogin(java.util.Date newLastLogin) {
	lastLogin = newLastLogin;
}
/**
 * Creation date: (3/27/2001 1:49:02 PM)
 * @param newLoginCount int
 */
public void setLoginCount(int newLoginCount) {
	loginCount = newLoginCount;
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @param newLoginType java.lang.String
 */
public void setLoginType(java.lang.String newLoginType) {
	loginType = newLoginType;
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @param newPassword java.lang.String
 */
public void setPassword(java.lang.String newPassword) {
	password = newPassword;
}
/**
 * Creation date: (3/27/2001 1:42:22 PM)
 * @param newUsername java.lang.String
 */
public void setUsername(java.lang.String newUsername) {
	username = newUsername;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	String[] setColumns =
		{
			"Username",
			"Password",
			"LoginType",
			"LoginCount",
			"LastLogin"
		};

	Object[] setValues =
		{
			getUsername(),
			getPassword(),
			getLoginType(),
			new Integer( getLoginCount() ),
			getLastLogin()
		};

	String[] constraintColumns = { "LoginID" };
	Object[] constraintValues = { new Long(getId()) };

	super.update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}

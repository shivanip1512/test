package com.cannontech.database.db.web;

/**
 * Creation date: (6/2/2001 2:30:35 PM)
 * @author: Aaron Lauinger
 */
import java.util.Date;

public class OperatorLogin extends com.cannontech.database.db.DBPersistent 
{
	public static final String tableName = "OperatorLogin";

	public static final String READMETER = "RDMT";
	public static final String CURTAILMENT = "CURT";
	public static final String LOADCONTROL = "LC";
	public static final String ENERGYEXCHANGE = "EXCH";
	
	private long operatorLoginID = -1;
	private String username = null;
	private String password = null;
	private String loginType = null;
	private long loginCount = -1;
	private Date lastLogin = null;
	private String status = null;

	private static final String[] selectColumns =
	{
		"Username",
		"Password",
		"LoginType",
		"LoginCount",
		"LastLogin",
		"Status"
	};

	private static final String[] constraintColumns =
	{
		"LoginID"
	};
/**
 * Operator constructor comment.
 */
public OperatorLogin() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @return java.util.Date
 */
public java.util.Date getLastLogin() {
	return lastLogin;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @return long
 */
public long getLoginCount() {
	return loginCount;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @return java.lang.String
 */
public java.lang.String getLoginType() {
	return loginType;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @return long
 */
public long getOperatorLoginID() {
	return operatorLoginID;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @return java.lang.String
 */
public java.lang.String getPassword() {
	return password;
}
/**
 * Creation date: (6/2/2001 3:09:14 PM)
 * @return java.lang.String
 */
public java.lang.String getStatus() {
	return status;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @return java.lang.String
 */
public java.lang.String getUsername() {
	return username;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { new Integer( (int) getOperatorLoginID()) };

System.out.println("operator login id is:  " + constraintValues[0].toString() );

System.out.println("selectColumns: " );
for( int i = 0; i < selectColumns.length; i++ )
System.out.println(selectColumns[i]);

System.out.println("tablename: " + tableName );

System.out.println("constraintColumns: " );
for( int i = 0; i < constraintColumns.length; i++ )
System.out.println(constraintColumns[i]);

System.out.println("constraintValues: " );
for( int i = 0; i < constraintValues.length; i++ )
System.out.println(constraintValues[i]);
	
	Object results[] = retrieve( selectColumns, tableName, constraintColumns, constraintValues );


	if( results.length == selectColumns.length )
	{
		setUsername( (String) results[0] );
		setPassword( (String) results[1] );
		setLoginType( (String) results[2] );
		setLoginCount( ((Integer) results[3]).longValue() );
		setLastLogin( (Date) results[4]);
		setStatus( (String) results[5] );
	}
	else
	{
		throw new RuntimeException("Incorrect number of columns in result");
	}
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @param newLastLogin java.util.Date
 */
public void setLastLogin(java.util.Date newLastLogin) {
	lastLogin = newLastLogin;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @param newLoginCount long
 */
public void setLoginCount(long newLoginCount) {
	loginCount = newLoginCount;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @param newLoginType java.lang.String
 */
public void setLoginType(java.lang.String newLoginType) {
	loginType = newLoginType;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @param newOperatorLoginID long
 */
public void setOperatorLoginID(long newOperatorLoginID) {
	operatorLoginID = newOperatorLoginID;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @param newPassword java.lang.String
 */
public void setPassword(java.lang.String newPassword) {
	password = newPassword;
}
/**
 * Creation date: (6/2/2001 3:09:14 PM)
 * @param newStatus java.lang.String
 */
public void setStatus(java.lang.String newStatus) {
	status = newStatus;
}
/**
 * Creation date: (6/2/2001 2:36:55 PM)
 * @param newUsername java.lang.String
 */
public void setUsername(java.lang.String newUsername) {
	username = newUsername;
}
/**
 * Creation date: (6/3/2001 2:23:32 PM)
 * @return java.lang.String
 */
public String toString() {
	return "( OperatorLoginID:  " + getOperatorLoginID() +
	       " username:  " + getUsername() +
	       " password:  " + getPassword() +
	       " logintype: " + getLoginType() +
	       " last login: " + getLastLogin() +
	       " status: " + getStatus() +
	       " )";
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
}

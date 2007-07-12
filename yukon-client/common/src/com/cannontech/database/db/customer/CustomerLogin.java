package com.cannontech.database.db.customer;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */

public class CustomerLogin extends com.cannontech.database.db.DBPersistent implements com.cannontech.common.editor.EditorPanel
{
	public static final int NONE_LOGIN_ID = -1;
	public static final String READMETER = "RDMT";
	public static final String CURTAILMENT = "CURT";
	public static final String LOADCONTROL = "LC";
	public static final String ENERGYEXCHANGE = "EXCH";
	public static final String STATUS_DISABLED = "Disabled";
	public static final String STATUS_ENABLED = "Enabled";
	public static final String STATUS_LOGGEDIN = "LoggedIn";
	public static final String STATUS_LOGGEDOUT = "LoggedOut";


	// rights of a loggin customer
	public static final String RIGHTS_VIEW_ONLY = "View-Only";
	public static final String RIGHTS_ALL_CONTROL = "Control";

	private Integer loginID = null;
	private String userName = null;
	private String userPassword = null;
	private String loginType = null;
	private int loginCount = 0;
	private java.util.Date lastLogin = new java.util.Date();
	private String status = STATUS_DISABLED;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"UserName", "Password", "LoginType", 
		"LoginCount", "LastLogin", "Status"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "LoginID" };

	public static final String TABLE_NAME = "CustomerLogin";
/**
 * LMGroupVersacomSerial constructor comment.
 */
public CustomerLogin() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getLoginID() == null )
		setLoginID( getNextLoginID() );
	else if ( getLoginID().intValue() == NONE_LOGIN_ID )
		return;  //do not insert a NONE login
		
	Object addValues[] = { getLoginID(), getUserName(), getUserPassword(),
					getLoginType(), new Integer(getLoginCount()), getLastLogin(),
					getStatus() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	// delete the actual contact
	String values[] = { getLoginID().toString() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}
/**
 * This method was created in VisualAge.
 * @return LMControlAreaTrigger[]
 * @param stateGroup java.lang.Integer
 */
public static final CustomerLogin getCustomerLogin(Integer loginID) throws java.sql.SQLException 
{
	return getCustomerLogin(loginID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return LMControlAreaTrigger[]
 * @param stateGroup java.lang.Integer
 */
public static final CustomerLogin getCustomerLogin(Integer loginID, String databaseAlias) throws java.sql.SQLException
{
	CustomerLogin item = null;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT l.loginID, l.UserName, l.Password, l.LoginType, l.Status " +
		"FROM " + TABLE_NAME + " l " +
		"WHERE l.LoginID= ? and l.LoginID > " + NONE_LOGIN_ID;

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, loginID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				item = new CustomerLogin();
				
				item.setDbConnection(conn);
				item.setLoginID( new Integer(rset.getInt("LoginID")) );
				item.setUserName( rset.getString("UserName") );
				item.setUserPassword( rset.getString("Password") );
				item.setLoginType( rset.getString("LoginType") );
				item.setLoginType( rset.getString("Status") );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt, conn );
	}


	return item;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:01:39 AM)
 * @return java.util.Date
 */
public java.util.Date getLastLogin() {
	return lastLogin;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:03:03 AM)
 * @return int
 */
public int getLoginCount() {
	return loginCount;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:27:41 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLoginID() {
	return loginID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:27:41 AM)
 * @return java.lang.String
 */
public java.lang.String getLoginType() {
	return loginType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public static final Integer getNextLoginID()
{
	java.util.ArrayList logins = new java.util.ArrayList(30);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT LoginID " + "FROM " + TABLE_NAME + " order by LoginID";

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
		pstmt = conn.prepareStatement(sql.toString());
		
		rset = pstmt.executeQuery();							

		while( rset.next() )
		{
			logins.add( new Integer(rset.getInt("LoginID")) );
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt, conn );
	}


	//look for the next loginID	
	int counter = 1;
	int currentID;
	 														
	for(int i = 0; i < logins.size(); i++)
	{
		currentID = ((Integer)logins.get(i)).intValue();

		if( currentID > counter )
			break;
		else
			counter = currentID + 1;
	}		
	
	return new Integer( counter );
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:06:39 AM)
 * @return java.lang.String
 */
public java.lang.String getStatus() {
	return status;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:27:41 AM)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	return userName;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:27:41 AM)
 * @return java.lang.String
 */
public java.lang.String getUserPassword() {
	return userPassword;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getLoginID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setUserName( (String) results[0] );
		setUserPassword( (String) results[1] );
		setLoginType( (String) results[2] );
		setLoginCount( ((Integer)results[3]).intValue() );
		setLastLogin( (java.util.Date) results[4] );
		setStatus( (String) results[5] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:01:39 AM)
 * @param newLastLogin java.util.Date
 */
public void setLastLogin(java.util.Date newLastLogin) {
	lastLogin = newLastLogin;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:03:03 AM)
 * @param newLoginCount int
 */
public void setLoginCount(int newLoginCount) {
	loginCount = newLoginCount;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:27:41 AM)
 * @param newLoginID java.lang.Integer
 */
public void setLoginID(java.lang.Integer newLoginID) {
	loginID = newLoginID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:27:41 AM)
 * @param newLoginType java.lang.String
 */
public void setLoginType(java.lang.String newLoginType) {
	loginType = newLoginType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 10:06:39 AM)
 * @param newStatus java.lang.String
 */
public void setStatus(java.lang.String newStatus) {
	status = newStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:27:41 AM)
 * @param newUserName java.lang.String
 */
public void setUserName(java.lang.String newUserName) {
	userName = newUserName;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:27:41 AM)
 * @param newUserPassword java.lang.String
 */
public void setUserPassword(java.lang.String newUserPassword) {
	userPassword = newUserPassword;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 9:46:10 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getUserName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getUserName(), getUserPassword(),
				getLoginType(), new Integer(getLoginCount()), getLastLogin(),
				getStatus() };

	Object constraintValues[] = { getLoginID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}

package com.cannontech.servlet;

/**
 * LoginController authenticates and redirects a user based on the follong parameters
 * These four are required
 * USERNAME -
 * PASSWORD -
 * DATABASEALIAS - corresponds to a pool in the db.properties file.
 * SERVICE - which service are we logging into?
 *
 * alternate - the encoded url of where the user wants to be redirected
 *  		   Used only for a successful change of password request.
 * Creation date: (12/7/99 9:46:12 AM)
 * @author:	Aaron Lauinger 
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.common.util.LogWriter;
import com.cannontech.database.data.web.User;

public class LoginController extends javax.servlet.http.HttpServlet {
	
	private static final String LOGFILE = "login.log";

	private static final String USER_BASE = "/user";
	private static final String OPERATOR_BASE = "/operator";
	
	private static final String DEFAULT_USER_URI = "user_trending.jsp?tab=graph";
	private static final String DEFAULT_OPERATOR_URI = "oper_trending.jsp?tab=graph";

	private static final String INVALID_URI = "/login.jsp?failed=true";
	private static final String LOG_OUT_URI = "/login.jsp";

//	private static final String CURTAILMEMT_URI = "/UserMand.jsp";
//	private static final String TRENDING_URI =    "/trending.jsp?tab=graph";

	private static final String CHANGE_OF_PASSWORD_CONFIRMED_URI = "/ConfirmRM.jsp?redirect=";
	private static final String CHANGE_OF_PASSWORD_FAILED_URI = "/GetPasswordRM.jsp";
		
	private static final String ACTION = "ACTION";

	private static final String LOGIN = "LOGIN";
	private static final String LOGIN_NO_FORWARD = "QLOGIN";
	private static final String LOGOUT = "LOGOUT";
	private static final String CHANGE_PASSWORD = "CHANGE";
	private static final String REDIRECT = "REDIRECT";

	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String NEW_PASSWORD = "NEW_PASSWORD";
	private static final String NEW_PASSWORD_CONFIRMED = "NEW_PASSWORD_CONFIRMED";
	private static final String DATABASEALIAS = "DATABASEALIAS";
	private static final String SERVICE = "SERVICE";

	// Used to identify the esubstation service as opposed to others
	// This is a special case and it should go away
	// If SERVICE=ESUBSTATION_SERVICE then the user should be
	// redirected to the users home directory + DEFAULT_ESUBSTATION_URI
	private static final String ESUBSTATION_SERVICE = "ESUBSTATION";
	private static final String DEFAULT_ESUBSTATION_URI = "SubstationList.jsp";

	private LogWriter logger = null;
/**
 */
public LoginController() {
	super();
}
/**
 * Will authenticate a user base on username/password/dbalias combination
 * returns null if authentication fails
 * Creation date: (4/28/00 11:00:07 AM)
 * @return com.cannontech.database.data.user.WebUser
 * @param username java.lang.String
 * @param password java.lang.String
 * @param dbAlias java.lang.String
 */
private com.cannontech.database.data.web.Operator authenticateOperator(String username, String password, String dbAlias)
{	
	com.cannontech.database.data.web.Operator retVal = null;
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();
		rset = stmt.executeQuery("SELECT LoginID FROM OperatorLogin WHERE Username='" + username + "' AND Password='" + password + "'");

		if( rset.next() )
		{			
			retVal = new com.cannontech.database.data.web.Operator();			
			retVal.setLoginID(rset.getLong(1));
		
			retVal.setDbConnection(conn);
			retVal.retrieve();
			retVal.setDbConnection(null);	
		}

		stmt.close();

		if( retVal != null )
		{	
			retVal.setDbConnection(conn);
			retVal.retrieve();
		}
	}
	catch( java.sql.SQLException e )
	{
		logger.log( e.getMessage(), LogWriter.ERROR );
	}
	finally
	{
		try
		{
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} catch( Exception e ) { }
	}
		
	return retVal;
}
/**
 * Will authenticate a user base on username/password/dbalias combination
 * returns null if authentication fails
 * Creation date: (4/28/00 11:00:07 AM)
 * @return com.cannontech.database.data.user.WebUser
 * @param username java.lang.String
 * @param password java.lang.String
 * @param dbAlias java.lang.String
 */
private com.cannontech.database.data.web.User authenticateUser(String username, String password, String dbAlias)
{
	logger.log("authenticateUser - username: " + username, com.cannontech.common.util.LogWriter.INFO);
logger.log("authenticateUser - password: " + password, LogWriter.INFO);
logger.log("authenticateUser - databaseAlias: " + dbAlias, LogWriter.INFO);
logger.log("authenticateUser - databaseAliasLen: " + dbAlias.length(), LogWriter.INFO);		
	com.cannontech.database.data.web.User retVal = null;
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
logger.log("authenticateUser - conn: " + conn , LogWriter.INFO);		
		stmt = conn.createStatement();
		rset = stmt.executeQuery("SELECT LoginID FROM CustomerLogin WHERE Username='" + username + "' AND Password='" + password + "'");

		if( rset.next() )
		{			
			retVal = new com.cannontech.database.data.web.User();			
			retVal.setId(rset.getLong(1));								
		}

		stmt.close();

		if( retVal != null )
		{	
			retVal.setDbConnection(conn);
			retVal.retrieve();
			retVal.setDbConnection(null);
		}		
	}
	catch( java.sql.SQLException e )
	{
		logger.log( e.getMessage(), LogWriter.ERROR );
	}
	finally
	{
		try
		{
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} catch( Exception e ) { }
	}
		
	return retVal;
}
/**
 * Will change a users password.
 * Creation date: (7/19/00 3:42:05 PM)
 * @return boolean
 * @param user com.cannontech.database.db.graph.UserInfo
 * @param oldPassword java.lang.String
 * @param newPassword java.lang.String
 */
private boolean changePassword(String username, String oldPassword, String newPassword, String dbAlias) {
	com.cannontech.database.data.web.User user = authenticateUser(username, oldPassword, dbAlias);

	//Check for failed username/password combo
	if( user == null )
	 	return false;
	   
	logger.log("Change password request, username: " + username + " old: " + oldPassword + " new:  " + newPassword + " dbalias:  " + dbAlias, LogWriter.INFO );
		    
	String sql = "UPDATE CustomerLogin SET Password='" + newPassword + "' WHERE LoginID=" + user.getId();
	
	com.cannontech.database.SqlStatement stmt = 
		new com.cannontech.database.SqlStatement("UPDATE CustomerLogin SET Password='" + newPassword + "' WHERE LoginID=" + user.getId(), dbAlias);

	try
	{
		stmt.execute();
	}
	catch( com.cannontech.common.util.CommandExecutionException cee )
	{
		return false;
	}

	logger.log("User:  " + username + " - password changed", LogWriter.INFO );
	return true;
}
/**
 * Creation date: (3/27/2001 2:54:11 PM)
 */
public void destroy() 
{
	logger.getPrintWriter().close();
	logger = null;

	super.destroy();
}
/**
 * Checks to see if the given user should be immediately
 * directed to the curtailment page after login.
 * This is somewhat wasteful as it hits the database and throws
 * away the info.
 * Creation date: (4/13/2001 6:37:03 AM)
 * @return boolean
 * @param u com.cannontech.database.data.web.User
 */
private boolean foundCurtailment(User user, String dbAlias) {
System.out.println(user.getLoginType());
System.out.println(user.getLoginType().indexOf("CURT"));
	if( !(user.getLoginType().indexOf("curt") > 0 ||
		  user.getLoginType().indexOf("CURT") > 0 ))
	{
		return false;
	}
		
	boolean ret = false;
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();
		rset = stmt.executeQuery("select curtailmentstarttime,curtailmentstoptime,curtailreferenceid,acknowledgestatus,ackdatetime from lmcurtailcustomeractivity_view where CustomerID=" + user.getCustomerId() + " AND curtailmentstarttime=(select max(curtailmentstarttime) from lmcurtailcustomeractivity_view) AND AcknowledgeStatus='UnAcknowledged'");

		System.out.println("select curtailmentstarttime,curtailmentstoptime,curtailreferenceid,acknowledgestatus,ackdatetime from lmcurtailcustomeractivity_view where CustomerID=" + user.getCustomerId() + " AND curtailmentstarttime=(select max(curtailmentstarttime) from lmcurtailcustomeractivity_view) AND AcknowledgeStatus='UnAcknowledged'");
		if( rset.next() )
		{
			 java.util.Date stopTime = rset.getTimestamp(2);
	System.out.println("start curtail: " + stopTime);
	 				System.out.println(stopTime.getTime());
				System.out.println(System.currentTimeMillis());
			 if( stopTime.getTime() > System.currentTimeMillis() )
			{
				System.out.println("start curtail is after now");
				logger.log("Found unacknowledged mandatory curtiailment for user: " + user.getUsername(), LogWriter.INFO );
				ret = true;
			}
			else
			{
				System.out.println("start curtail is before now");
			}
		}
	}
	catch( java.sql.SQLException e )
	{
		logger.log( e.getMessage(), LogWriter.ERROR );
	}
	finally
	{
		try
		{
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} catch( Exception e ) { }
	}
			
	return ret;
}
/**
 * Creation date: (6/4/2001 11:23:20 AM)
 * @return boolean
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 */
private boolean handleChangeOfPassword(HttpServletRequest req, HttpServletResponse resp) {

	User user = (User) req.getSession().getValue("USER");

	if( user != null )
	{		
		// Use the info in the session rather than
		// relay on what parameters were passed to us
		String currentUsername = user.getUsername();
		String oldPassword = user.getPassword();
		String dbAlias = user.getDatabaseAlias();
			
		String newPassword = req.getParameter(NEW_PASSWORD);
					
		logger.log(currentUsername + " change of password request", LogWriter.DEBUG );
					
		// Make sure the new and confirmed passwords match
		if( newPassword != null && 
			newPassword.equals( req.getParameter(NEW_PASSWORD_CONFIRMED)) &&		
			changePassword(currentUsername, oldPassword, newPassword, dbAlias ) )
		{
			logger.log(currentUsername + " change of password succeeded", LogWriter.DEBUG );
			user.setPassword(newPassword);

			return true;
		}
	}

	return false;
}
/**
 * Creation date: (6/4/2001 11:23:20 AM)
 * @return String - The users home URL
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 */
private String handleEsubLogin(HttpServletRequest req, HttpServletResponse resp) {

	String username = req.getParameter(USERNAME);
	String password = req.getParameter(PASSWORD);
	String databaseAlias = req.getParameter(DATABASEALIAS);
	
	User user = authenticateUser(username, password, databaseAlias);

	if( user != null )
	{
		logger.log("Esub user validated, username:  " + username, LogWriter.INFO );
		incrementUserLoginCount(username, databaseAlias);

		// Create a session for them and store user in it
		HttpSession session = req.getSession(true);
		session.putValue("USER", user);
		logger.log("Esub users home url is:  " + user.getHomeURL(), LogWriter.INFO );
		return user.getHomeURL();
	}
	else
	{
		logger.log("Invalid user login, username:  " + username + " password:  " + password, com.cannontech.common.util.LogWriter.INFO );			
	}

	return null;
}
/**
 * Creation date: (6/4/2001 11:23:20 AM)
 * @return boolean
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 */
private boolean handleLogout(HttpServletRequest req, HttpServletResponse resp) {

	HttpSession session = req.getSession();
		
	if( session != null )		
	{
		session.invalidate();
		return true;
	}
	else
	{
		return false;
	}


}
/**
 * Creation date: (6/4/2001 11:23:20 AM)
 * @return boolean
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 */
private boolean handleOperatorLogin(HttpServletRequest req, HttpServletResponse resp) {

	String username = req.getParameter(USERNAME);
	String password = req.getParameter(PASSWORD);
	String databaseAlias = req.getParameter(DATABASEALIAS);
	
	com.cannontech.database.data.web.Operator operator = authenticateOperator(username, password, databaseAlias);

	if( operator != null )
	{
		logger.log("Operator validated, username:  " + username, LogWriter.INFO );
		incrementOperatorLoginCount(username, databaseAlias);

		// Create a session for them and store user in it
		HttpSession session = req.getSession(true);
		session.putValue("OPERATOR", operator);

		return true; 
	}
	else
	{
		logger.log("Invalid operator login, username:  " + username + " password:  " + password, com.cannontech.common.util.LogWriter.INFO );			
	}
		
	return false;
}
/**
 * Creation date: (6/4/2001 11:23:20 AM)
 * @return boolean
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 */
private boolean handleUserLogin(HttpServletRequest req, HttpServletResponse resp) {
logger.log("*** handlerUserLogin ***", LogWriter.INFO);
	
	String username = req.getParameter(USERNAME);
	String password = req.getParameter(PASSWORD);
	String databaseAlias = req.getParameter(DATABASEALIAS);
logger.log("handlerUserLogin - username: " + username, com.cannontech.common.util.LogWriter.INFO);
logger.log("handlerUserLogin - password: " + password, LogWriter.INFO);
logger.log("handlerUserLogin - databaseAlias: " + databaseAlias, LogWriter.INFO);
	
	User user = authenticateUser(username, password, databaseAlias);

	if( user != null )
	{
		logger.log("User validated, username:  " + username, LogWriter.INFO );
		incrementUserLoginCount(username, databaseAlias);

		// Create a session for them and store user in it
		HttpSession session = req.getSession(true);
		session.putValue("USER", user);

		return true;
	}
	else
	{
		logger.log("Invalid user login, username:  " + username + " password:  " + password, com.cannontech.common.util.LogWriter.INFO );			
	}
		
	return false;
}
/**
 * Increments the users login count.
 * Creation date: (6/8/00 12:31:25 PM)
 * @param userID long
 * @param dbAlias java.lang.String
 */
private void incrementOperatorLoginCount(String name, String dbAlias) 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("UPDATE OperatorLogin set LoginCount= (SELECT LoginCount+1 from OperatorLogin WHERE Username='" + name +"') where Username='" + name + "'", dbAlias );

	try
	{
		stmt.execute();
	}
	catch(Exception e )
	{
		e.printStackTrace();
	}
}
/**
 * Increments the users login count.
 * Creation date: (6/8/00 12:31:25 PM)
 * @param userID long
 * @param dbAlias java.lang.String
 */
private void incrementUserLoginCount(String name, String dbAlias) 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("UPDATE CustomerLogin set LoginCount= (SELECT LoginCount+1 from CustomerLogin WHERE Username='" + name +"') where Username='" + name + "'", dbAlias );

	try
	{
		stmt.execute();
	}
	catch(Exception e )
	{
		e.printStackTrace();
	}
}
/**
 * Creation date: (12/7/99 9:55:11 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException 
{
	super.init(config);	
	synchronized (this)
	{		
		try
		{
			java.io.PrintWriter writer =
				new java.io.PrintWriter( new java.io.FileOutputStream("login.log", true ), true );

			logger = new LogWriter("LoginController", com.cannontech.common.util.LogWriter.DEBUG, writer);
			logger.log("Starting up....", LogWriter.INFO );
		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( java.io.IOException ioe )
		{
			ioe.printStackTrace();
		}	
	}
}
/**
 * Handles login authentication, logout, change of password.
 * Creation date: (5/5/00 10:48:46 AM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{
logger.log("*** Request to logincontroller ***", LogWriter.INFO);

	String action = req.getParameter(ACTION).toString();
	Object redirectURL = req.getParameter(REDIRECT);
	String service = req.getParameter(SERVICE); //only esub uses this!
	String nextURI = INVALID_URI;


	// Check for esub login!! special case
	if( service != null &&
		service.equalsIgnoreCase(ESUBSTATION_SERVICE) )
	{
		String userHome = handleEsubLogin(req,resp);

		if( action.equalsIgnoreCase(LOGIN_NO_FORWARD) && 
			userHome != null ) {
				resp.getWriter().write(userHome);
				resp.getWriter().flush();
		}
		else {	//traditional esub login	
		
			if( userHome != null )
				nextURI = userHome + "/" + DEFAULT_ESUBSTATION_URI;
			else
				nextURI = INVALID_URI;
		}
	}
	else
	if( action.equalsIgnoreCase(CHANGE_PASSWORD) )
	{			
		if( handleChangeOfPassword(req, resp) )
		{
			// alternate tells use where to send the user
			// after the change is confirmed.
			String redirectURI = req.getParameter("alternate");				
			nextURI = CHANGE_OF_PASSWORD_CONFIRMED_URI;

			if( redirectURI != null )
				nextURI += java.net.URLEncoder.encode(redirectURI);
		}
		else
		{
			nextURI = CHANGE_OF_PASSWORD_FAILED_URI;
		}							
	}  // END CHANGE_PASSWORD
	else
	if( action.equalsIgnoreCase(LOGIN) || action.equalsIgnoreCase(LOGIN_NO_FORWARD) )
	{
logger.log("*** Ready to log in ***", com.cannontech.common.util.LogWriter.INFO);
logger.log("Action is: " + action, LogWriter.INFO );	
		// Try users first, if that fails try operators
		if( handleUserLogin(req, resp) )
		{
			if( !action.equalsIgnoreCase(LOGIN_NO_FORWARD) )
			{
				if( redirectURL != null )			
					nextURI = redirectURL.toString();			
				else			
					nextURI = USER_BASE + "/" + DEFAULT_USER_URI;
			}
			else
			{
				resp.getWriter().write("OK");
				resp.getWriter().flush();
			}
		}
		else
		if( handleOperatorLogin(req, resp) )
		{
			if( !action.equalsIgnoreCase(LOGIN_NO_FORWARD) )
			{
				if( redirectURL != null )			
					nextURI = redirectURL.toString();			
				else
					nextURI = OPERATOR_BASE + "/" + DEFAULT_OPERATOR_URI;
			}
			else
			{
				resp.getWriter().write("OK");
				resp.getWriter().flush();
			}
		}
		else
		{
			nextURI = INVALID_URI;
		}	
	}
	else  // END LOGIN
	if( action.equalsIgnoreCase(LOGOUT) )
	{
		handleLogout(req, resp);			
		nextURI = LOG_OUT_URI;		
	} // END LOGOUT

	if( action.equalsIgnoreCase(LOGIN_NO_FORWARD) )
	{		
		logger.log( req.getRemoteAddr() + " - no forward indicated", LogWriter.INFO );
	}
	else
	{	
		logger.log( req.getRemoteAddr() + " - redirected to:  " + nextURI, LogWriter.INFO );
	
		nextURI = resp.encodeRedirectURL(nextURI);
		resp.sendRedirect(nextURI);
	}
}
}

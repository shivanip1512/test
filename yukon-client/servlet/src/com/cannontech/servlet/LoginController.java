package com.cannontech.servlet;

/**
 * LoginController authenticates and redirects a user based on the follong parameters
 * Required parameters:
 * USERNAME -
 * PASSWORD -
*
* TODO: Pull out contstant strings HOME_URL and YUKON_USER 
* 
 * Creation date: (12/7/99 9:46:12 AM)
 * @author:	Aaron Lauinger 
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.common.constants.RoleTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LoginController extends javax.servlet.http.HttpServlet {
	
	// These should be moved into global type properties
	// so we can change them at runtime FIXFIX 
	private static final String INVALID_URI = "/login.jsp?failed=true";
	private static final String LOG_OUT_URI = "/login.jsp";
		
	private static final String ACTION = "ACTION";

	private static final String LOGIN = "LOGIN";
	private static final String LOGOUT = "LOGOUT";
	
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
		
/**
 * Handles login authentication, logout.
 * TODO: add change of password
 * Creation date: (5/5/00 10:48:46 AM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{
	String action = req.getParameter(ACTION).toString();
	String nextURI = INVALID_URI;
		
	if(action.equalsIgnoreCase(LOGIN)) {
		String username = req.getParameter(USERNAME);
		String password = req.getParameter(PASSWORD);	
		LiteYukonUser user = AuthFuncs.login(username,password);
		
		if(user != null) {
			HttpSession session = req.getSession(true);
			try {						
				initSession(user, session);
			} catch(TransactionException e) {
				session.invalidate();
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			String home_url = AuthFuncs.getRoleValue(user,RoleTypes.HOME_URL);
			resp.sendRedirect(home_url);
		}
		else {
			resp.sendRedirect(INVALID_URI);
		}		
	}
	else 
	if(action.equalsIgnoreCase(LOGOUT))  {
		HttpSession session = req.getSession();
		resp.sendRedirect(LOG_OUT_URI);
		if(session != null) {
			session.invalidate();
		}			
	} 
	else {
		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}	
}

private void initSession(LiteYukonUser user, HttpSession session) throws TransactionException  {

/*	com.cannontech.database.data.user.YukonUser dbUser = 
		(com.cannontech.database.data.user.YukonUser) LiteFactory.createDBPersistent(user);
	
	Transaction trans = Transaction.createTransaction(Transaction.RETRIEVE,dbUser);
	trans.execute();
	
	//update user stats
	dbUser.setLoginCount(new Integer(dbUser.getLoginCount().intValue()+1));
	dbUser.setLastLogin(new java.util.Date());
		
	trans = Transaction.createTransaction(Transaction.UPDATE,dbUser);
	*/
	session.setAttribute("YUKON_USER", user);
}
}

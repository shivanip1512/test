/*
 * Created on Jul 1, 2003
 */
package com.cannontech.common.login;

import java.awt.Frame;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Holds session information for client programs.
 * Start with the static establishSession methods.
 * 
 * If a db.properties exists in the classpath a 'local login' will be attempted
 * instead of a 'remote login'
 * 
 * In either case if there is a currentuserid,currentsessionid an attempt will be made to
 * use it, if that fails the user will be asked for their credentials.
 * @author aaron
 */
public class ClientSession {
		
	private LiteYukonUser user;
	private String sessionID;
	private String host;
	private int port;
	
	private boolean localLogin = true;
	private String errMsg;
	
	private static int INVALID_CURRENT_USERID = Integer.MIN_VALUE;
	
	/**
	 * Return the user associated with this session.
	 * @return
	 */
	public LiteYukonUser getUser() {
		return user;	
	}
	
	/**
	 * Checks if the current user has the given role
	 * @param roleid
	 * @return
	 */
	public boolean checkRole(int roleid) {
		return AuthFuncs.checkRole(getUser(), roleid) != null;
	}
	
	/**
	 * Checks if the current user has the give roleproperty id
	 * @param rolePropertyID
	 * @return
	 */
	public boolean checkRoleProperty(int rolePropertyID) {
		return AuthFuncs.checkRoleProperty(getUser(), rolePropertyID);
	}
	
	public static synchronized ClientSession getInstance() {
		if(instance == null) {
			instance = new ClientSession();
		}
		return instance;
	}
	
	public synchronized void closeSession() {
		if(LoginPrefs.getInstance().getCurrentSessionID().length() > 0 &&
			LoginPrefs.getInstance().getCurrentUserID() != INVALID_CURRENT_USERID) {
		
			LoginPrefs.getInstance().setCurrentSessionID("");
			LoginPrefs.getInstance().setCurrentUserID(INVALID_CURRENT_USERID);		

			if(!localLogin) {
				LoginSupport.closeSession(sessionID, host, port);
			}
		}
	}


	/**
		 * Attempt to establish a session
		 * @return
		 */
		public synchronized boolean establishSession() {
			return establishSession(null);
		}
		
	public synchronized boolean establishSession(Frame parent) {
		Properties dbProps = PoolManager.loadDBProperties();
		
		boolean success = false;
		if(dbProps != null && !dbProps.isEmpty()) {
			localLogin = (success = doLocalLogin(parent, dbProps));
		}
		else {
			localLogin = !(success = doRemoteLogin(parent));
		}			
		
		if(success) {
			LoginPrefs prefs = LoginPrefs.getInstance();
			prefs.setCurrentSessionID(sessionID);
			prefs.setCurrentUserID(user.getUserID());
			prefs.setCurrentYukonHost(host);
			prefs.setCurrentYukonPort(port);
			return true;
		}
		
		return false;
	}
	
	private boolean doLocalLogin(Frame p, Properties props) {
		PoolManager.setDBProperties(props);
		
		LoginPrefs prefs = LoginPrefs.getInstance();
		int userID = prefs.getCurrentUserID();
		if(userID != INVALID_CURRENT_USERID) {
			//already 'logged in' so just try to use it
			LiteYukonUser u = YukonUserFuncs.getLiteYukonUser(userID);
			if(u != null) {
				setSessionInfo(u, Integer.toString(u.getUserID()), "", Integer.MIN_VALUE);		
				return true;
			}
			//Couldn't find the supposedly logged in user, disregard current login
		}

		LoginPanel lp = makeLocalLoginPanel();
		while(collectInfo(lp)) {
			LiteYukonUser u = AuthFuncs.login(lp.getUsername(), lp.getPassword());
			if(u != null) {
				//score! we found them
				setSessionInfo(u, Integer.toString(u.getUserID()), "", Integer.MIN_VALUE);
				return true;
			}
			else {
				// bad username or password
				displayMessage(p, "Invalid Username or Password.  Usernames and Passwords are case sensitive, be sure to use correct upper and lower case.", "Yukon Login");
			}
		}
		
		// They gave up trying to login
		return false;
	}
	
	private boolean doRemoteLogin(Frame p) {
		LoginPrefs prefs = LoginPrefs.getInstance();
		
		String sessionID = prefs.getCurrentSessionID();
		int userID = prefs.getCurrentUserID();
		String host = prefs.getCurrentYukonHost();
		int port = prefs.getCurrentYukonPort();
		
		if(sessionID.length() > 0 && host.length() > 0 && port > 0) {
			// have a session info already lets try it
			Properties dbProps = LoginSupport.getDBProperties(sessionID, host, port);
			if(!dbProps.isEmpty()) {
				LiteYukonUser u = YukonUserFuncs.getLiteYukonUser(userID);
				if(u != null) {
					//score! we found them
					setSessionInfo(u, sessionID, host, port);
					return true;
				}
			}
		}  // current session didn't work out, lets try to establish a new one
		
		LoginPanel lp = makeRemoteLoginPanel();
		while(collectInfo(lp)) {
			try {
				sessionID = LoginSupport.getSessionID(lp.getYukonHost(), lp.getYukonPort(), lp.getUsername(), lp.getPassword());
			}
			catch(RuntimeException re) {
				displayMessage(p, re.getMessage(), "Yukon Login");
			}
			
			Properties dbProps = LoginSupport.getDBProperties(sessionID, lp.getYukonHost(), lp.getYukonPort());
			if(!dbProps.isEmpty()) {
				PoolManager.setDBProperties(dbProps);
				LiteYukonUser u = AuthFuncs.login(lp.getUsername(), lp.getPassword());
				if(u != null) {
					//score! we found them
				  	setSessionInfo(u, sessionID, lp.getYukonHost(), lp.getYukonPort());
				  	return true;
				}
				else {
					//ooh, thats bad.
					displayMessage(p, "Server returned valid session ID but user id: " + userID + " couldn't be found in the local cache.  This is either a bug or a configuration problem.", "Error");					
				}
			}
		}
		return false;		 
	}
	
	private void setSessionInfo(LiteYukonUser u, String sessionID, String host, int port) {
		this.user = u;
		this.sessionID = sessionID;
		this.host = host;
		this.port = port;			
	}
	
	private LoginPanel makeLocalLoginPanel() {
		LoginPrefs prefs = LoginPrefs.getInstance();
		return  new LoginPanel(	prefs.getCurrentYukonHost(),
								prefs.getAvailableYukonHosts(),
								prefs.getDefaultUsername(),
								prefs.getDefaultPassword(),
								prefs.getDefaultRememberPassword(), true);
	}
		
	private LoginPanel makeRemoteLoginPanel() {
			LoginPrefs prefs = LoginPrefs.getInstance();
			return  new LoginPanel(	prefs.getCurrentYukonHost(),
									prefs.getAvailableYukonHosts(),
									prefs.getDefaultUsername(),
									prefs.getDefaultPassword(),
									prefs.getDefaultRememberPassword(), false);
		}
		
	private boolean collectInfo(LoginPanel lp) {
		return JOptionPane.showConfirmDialog(null, lp, "Yukon Login", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
	}
	
	private void displayMessage(Frame p, String msg, String title) {
		JOptionPane.showMessageDialog(p, msg, title, JOptionPane.WARNING_MESSAGE); 
	}
	
	
	// My pretty private instance
	private static ClientSession instance;
}

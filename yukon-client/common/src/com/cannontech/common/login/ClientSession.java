/*
 * Created on Jul 1, 2003
 */
package com.cannontech.common.login;

import java.awt.Frame;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Holds session information for client programs.
 * Start with the static establishSession methods.
 * 
 * If a db.properties exists in the classpath it will be used
 * instead of loggin in.  The default yukon user with id -1 will be used.
 * 
 * Current connection process:
 * 1) Look up current session id via preferences api
 * 2) Use session id to make a request to the servlet that returns db properties.
 * 		If we get db properties goto 9
 * 3) Ask the user for a yukon server location, a username, and a password.  
 * 4) Connect to the login servlet and try to get a session id from it.
 * 5) goto 2
 * 
 * 9) All is good, store anything golden via preferences 
 * 		and give out a reference to a property initialized ClientSession.
 *10) have a beer.
 *
 * @author aaron
 */
public class ClientSession {
	
	private LiteYukonUser user;
	private String sessionID;
	private String host;
	private int port;
	
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
	
		
	/**
	 * Attempt to establish a session
	 * @return
	 */
	public static synchronized ClientSession establishSession() {
		return establishSession(null);
	}
	
	/**
	 * Attempt to establish a session.  Takes a parent frame, useful to
	 * set the frame icon if we have to pop up a dialog.  You decide.
	 * @param parent
	 * @return
	 */
	public static synchronized ClientSession establishSession(Frame parent) {
		String errMsg = null;
		
		if(instance == null) {				
			ClientSession session = new ClientSession();

			LoginPrefs prefs = LoginPrefs.getInstance();
			LiteYukonUser user;	
							
			Properties dbProps = PoolManager.loadDBProperties();
			if(dbProps != null && !dbProps.isEmpty()) {
				CTILogger.info("Local database properties found, skipping logon");
				user = YukonUserFuncs.getLiteYukonUser(-1);
				if(user == null) {
					CTILogger.error("Couldn't locate default user in the database");
					return null;
				}
				session.user = user;
				instance = session;
				return instance;
			}
			

			String sessionID = prefs.getCurrentSessionID();
			String host = prefs.getCurrentYukonHost();
			int port = prefs.getCurrentYukonPort();
					
			try {										
				dbProps = 
					LoginSupport.getDBProperties(sessionID, host, port);
			}
			catch(RuntimeException re) {
				//dismiss 
			}
				
			if(dbProps != null && !dbProps.isEmpty()) {
				PoolManager.setDBProperties(dbProps);
				user = YukonUserFuncs.getLiteYukonUser(prefs.getCurrentUserID());
				
				if(user != null) {
					session.user = user;
					session.sessionID = sessionID;
					session.host = host;
					session.port = port;
					instance = session;					
				}
				else {
					errMsg = "Couldn't locate user in the database";
				}
			}
			else
			{
				LoginPanel lp = new LoginPanel(
					prefs.getCurrentYukonHost(),
					prefs.getAvailableYukonHosts(),
					prefs.getDefaultUsername(),
					prefs.getDefaultPassword(),
					prefs.getDefaultRememberPassword());
				
				sessionID = lp.showLoginDialog(parent);
				
				if(sessionID != null) {
					dbProps = LoginSupport.getDBProperties(sessionID,lp.getYukonHost(),lp.getYukonPort());
					
					if(dbProps != null) {
						PoolManager.setDBProperties(dbProps);
						user = AuthFuncs.login(lp.getUsername(), lp.getPassword());
						
						if(user != null) {
							session.user = user;
							session.sessionID = sessionID;
							session.host = host;
							session.port = port;
							instance = session;
							prefs.setCurrentUserID(user.getUserID());
							prefs.setCurrentSessionID(sessionID);
							prefs.addAvailableYukonHost(lp.getYukonHost());
							prefs.setCurrentYukonHost(lp.getYukonHost());
							prefs.setCurrentYukonPort(lp.getYukonPort());
							prefs.setDefaultUsername(lp.getUsername());
							prefs.setDefaultPassword(lp.getPassword());
							prefs.setDefaultRememberPassword(lp.isRememberPassword());
						}
						else {
							errMsg = "couldn't locat user inthe db";
						}
					}
					else {
						errMsg = "Couldn't get db.properties";
					}
										
				}
				else {
					errMsg = "Couldn't get a sessionid";
				}
			}
			
		}
				
		if(errMsg != null) {
			JOptionPane.showMessageDialog(null, errMsg, "Unable to establish session...", JOptionPane.ERROR_MESSAGE);
		}
		
		return instance;
	}

	// My pretty private instance
	private static ClientSession instance;
}

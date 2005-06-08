/*
 * Created on Jul 1, 2003
 */
package com.cannontech.common.login;

import java.awt.Frame;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import com.cannontech.clientutils.CTILogManager;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.UserUtils;

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
	
	private Timer refreshTimer; 
	
	private static int SESSION_REFRESH_FREQUENCY = 1000*60*15; //15 minutes
	
	private static int INVALID_CURRENT_USERID = Integer.MIN_VALUE;
	private static int DEF_PORT = 8080;

	/*
	 * SessionRefreshTimerTask refreshes a client session on a periodic basis 
	 * to avoid session timeouts while the client is still running.  
	 */
	class SessionRefreshTimerTask extends TimerTask {
		public void run() {
			Properties p = null;
			try {			
				CTILogger.debug("Refreshing client session: " + sessionID + " with host: " + host + " port: " + port);
				p = LoginSupport.getDBProperties(sessionID, host, port);
			}
			catch(Exception e) {
				CTILogger.warn("Unable to refresh client  session: " + sessionID + " with host: " + host + " port: " + port, e);							
			}

			if(p != null && p.size() > 0) {
				CTILogger.info("Refreshed client session: " + sessionID + " with host: " + host + " port: " + port);
			}
		}
	}
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
	 * Returns the value of the given role property for the current user.
	 * Checks if the current user has the give roleproperty id
	 * @param rolePropertyID
	 * @param defaultValue
	 * @return
	 */
	public String getRolePropertyValue(int rolePropertyID, String defaultValue) {
		return AuthFuncs.getRolePropertyValue(getUser(), rolePropertyID, defaultValue);
	}
		
	/**
	 * Returns the value of the given role property for the current user.
	 * @param rolePropertyID
	 * @return
	 */
	public String getRolePropertyValue(int rolePropertyID) {
		return AuthFuncs.getRolePropertyValue(getUser(), rolePropertyID);
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
				//Stop session refreshes and close the session
				refreshTimer.cancel();
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
			CTILogger.info("Attempting local load of database properties...");
			localLogin = (success = doLocalLogin(parent, dbProps));
		}
		else {
			CTILogger.info("Attempting remote load of database properties...");
			localLogin = !(success = doRemoteLogin(parent));
		}			
		
		if(success) {
			LoginPrefs prefs = LoginPrefs.getInstance();
			prefs.setCurrentSessionID(sessionID);
			prefs.setCurrentUserID(user.getUserID());
			prefs.setCurrentYukonHost(host);
			prefs.setCurrentYukonPort(port);
			
			if(!localLogin) {
				//fire up a timer to refresh the session 
				refreshTimer = new Timer(true);
				refreshTimer.schedule(new SessionRefreshTimerTask(), SESSION_REFRESH_FREQUENCY, SESSION_REFRESH_FREQUENCY);
			}
			return true;
		}
		
		return false;
	}

    /**
     * Establishes local login with a default user. Used by Java servers that should
     * not prompt for login credentials.
     * 
     * Assumes the loginID of USER_ADMIN_ID (Admin user).
     * 
     * @return
     */
    public synchronized boolean establishDefServerSession()
    {
        Properties dbProps = PoolManager.loadDBProperties();
        
        boolean success = false;
        if(dbProps != null && !dbProps.isEmpty())
        {
            CTILogger.info("Attempting local load of database properties for SERVER login...");

            int userID = UserUtils.USER_ADMIN_ID;
            LiteYukonUser u = YukonUserFuncs.getLiteYukonUser(userID);
            if(u != null)
            {
                CTILogger.debug("  Assuming SERVER loginID to be " + userID );
                setSessionInfo(u, Integer.toString(u.getUserID()), "", DEF_PORT);      
                success = true;
            }
            else
            {
                CTILogger.info("Unable to find default SERVER loginID of " + userID );
                success = false;
            }
                
        }
        else
        {
            CTILogger.info("Unable to find local copy of db.properties for SERVER login");
            success = false;
        }           
        
        return success;
    }
    
	private boolean doLocalLogin(Frame p, Properties props) {
		PoolManager.setDBProperties(props);
		
		LoginPrefs prefs = LoginPrefs.getInstance();
		int userID = prefs.getCurrentUserID();
		if(userID != INVALID_CURRENT_USERID) {
			//already 'logged in' so we try to re-establish the login
			// with the existing credentials
			String oldUserName = prefs.getDefaultUsername();
			String oldPassword = prefs.getDefaultPassword();
			LiteYukonUser u = AuthFuncs.login(oldUserName, oldPassword);			
			if(u != null) {
				setSessionInfo(u, Integer.toString(u.getUserID()), "", DEF_PORT);		
				return true;
			}
			//Couldn't find the supposedly logged in user, disregard current login
		}

		LoginPanel lp = makeLocalLoginPanel();
		while(collectInfo(lp)) {
			LiteYukonUser u = AuthFuncs.login(lp.getUsername(), lp.getPassword());
			if(u != null) {
				//score! we found them
				setSessionInfo(u, Integer.toString(u.getUserID()), "", DEF_PORT);
				boolean saveInfo = lp.isRememberPassword();
				prefs.setDefaultRememberPassword(saveInfo);
				if(saveInfo) {
					prefs.setDefaultUsername(lp.getUsername());
					prefs.setDefaultPassword(lp.getPassword());
				}
				else {
					prefs.setDefaultUsername("");
					prefs.setDefaultPassword("");
				}
				
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
		
		try
		{
			if(sessionID.length() > 0 && host.length() > 0 && port > 0) {
				// have a session info already lets try it
				Properties props = LoginSupport.getDBProperties(sessionID, host, port);
				if(!props.isEmpty())
                {
                    //load both DB and log properties
					PoolManager.setDBProperties(props);
                    CTILogManager.setLogProperties(props);
                    
					//LiteYukonUser u = YukonUserFuncs.getLiteYukonUser(userID);
					//already 'logged in' so we try to re-establish the login
					// with the existing credentials
					String oldUserName = prefs.getDefaultUsername();
					String oldPassword = prefs.getDefaultPassword();
					LiteYukonUser u = AuthFuncs.login(oldUserName, oldPassword);					
					if(u != null) {
						//score! we found them
						setSessionInfo(u, sessionID, host, port);
						return true;
					}
				}
			}  // current session didn't work out, lets try to establish a new one
		}
		catch( Exception ex ) //did not work for whatever reason, force them to login again
		{
			CTILogger.error("Unable to use old credentials, forcing login process");
		}
		
		
				
		LoginPanel lp = makeRemoteLoginPanel();
		while(collectInfo(lp)) {
			try {
				sessionID = LoginSupport.getSessionID(lp.getYukonHost(), lp.getYukonPort(), lp.getUsername(), lp.getPassword());
			}
			catch(RuntimeException re) {
				displayMessage(p, re.getMessage(), "Yukon Login"); //blew up, try again?
				continue;
			}
			
			Properties dbProps = LoginSupport.getDBProperties(sessionID, lp.getYukonHost(), lp.getYukonPort());
			if(!dbProps.isEmpty()) {
				PoolManager.setDBProperties(dbProps);
				//Do not log in the user again
				LiteYukonUser u = YukonUserFuncs.getLiteYukonUser(lp.getUsername());
				if(u != null) {
					//score! we found them
				  	setSessionInfo(u, sessionID, lp.getYukonHost(), lp.getYukonPort());
				  	
					boolean saveInfo = lp.isRememberPassword();
					prefs.setDefaultRememberPassword(saveInfo);
					if(saveInfo) {
						prefs.setDefaultUsername(lp.getUsername());
						prefs.setDefaultPassword(lp.getPassword());
					}
					else {
						prefs.setDefaultUsername("");
						prefs.setDefaultPassword("");
					}
				  	return true;
				}
				else {
					//ooh, thats bad.
					displayMessage(p, "Server returned valid session ID but user id: " + userID + " couldn't be found in the local cache.  This is either a bug or a configuration problem.", "Error");					
				}
			}
			//sometimes inside the IDE this returns no properties
			// make the programmer aware
			else
				displayMessage(p, "Successful login returned ZERO database properties.  This is either a bug or DBProps servlet is not responding.", "Error");
				
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
		return  new LoginPanel(prefs.getCurrentYukonHost(),
								prefs.getAvailableYukonHosts(),
								prefs.getCurrentYukonPort(),
								prefs.getDefaultUsername(),
								prefs.getDefaultPassword(),
								prefs.getDefaultRememberPassword(), true);
	}
		
	private LoginPanel makeRemoteLoginPanel() {
			LoginPrefs prefs = LoginPrefs.getInstance();
			return  new LoginPanel(prefs.getCurrentYukonHost(),
									prefs.getAvailableYukonHosts(),
									prefs.getCurrentYukonPort(),
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

/*
 * Created on Jul 1, 2003
 */
package com.cannontech.common.login;

import java.awt.Frame;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.ThemeUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;

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
	
	private static int INVALID_CURRENT_USERID = Integer.MIN_VALUE;
	private static int DEF_PORT = 8080;

	/**
	 * Return the user associated with this session.
	 * @return
	 */
	public LiteYukonUser getUser() {
		return user;	
	}
	
	public static YukonUserContext getUserContext() {
		LiteYukonUser thisUser = getInstance().getUser();
		SimpleYukonUserContext result = new SimpleYukonUserContext(thisUser, Locale.getDefault(), TimeZone.getDefault(), ThemeUtils.getDefaultThemeName());
		return result;
	}
	
	/**
	 * Checks if the current user has the given role
	 * @param roleid
	 * @return
	 */
	public boolean checkRole(int roleid) {
		return DaoFactory.getAuthDao().getRole(getUser(), roleid) != null;
	}
	
	/**
	 * Returns the value of the given role property for the current user.
	 * Checks if the current user has the give roleproperty id
	 * @param rolePropertyID
	 * @param defaultValue
	 * @return
	 */
	public String getRolePropertyValue(int rolePropertyID, String defaultValue) {
		return DaoFactory.getAuthDao().getRolePropertyValue(getUser(), rolePropertyID);
	}
		
	/**
	 * Returns the value of the given role property for the current user.
	 * @param rolePropertyID
	 * @return
	 */
	public String getRolePropertyValue(int rolePropertyID) {
		return DaoFactory.getAuthDao().getRolePropertyValue(getUser(), rolePropertyID);
	}
	
	public static synchronized ClientSession getInstance() {
		if(instance == null) {
			instance = new ClientSession();
		}
		return instance;
	}
	
	/**
		 * Attempt to establish a session
		 * @return
		 */
		public synchronized boolean establishSession() {
			return establishSession(null);
		}
		
	public synchronized boolean establishSession(Frame parent) {
        // disable CookieHandler so that weird things don't happen when used with Java Web Start
        CookieHandler.setDefault(null);
        
		boolean success = false;
        
        boolean isJws = StringUtils.isNotBlank(System.getProperty("yukon.jws.host"));
        CTILogger.info("Java Web Start property found: " + isJws);
        // "getBoolean" has to be the oddest Java method ever, but it is exactly what I want
        boolean forceRemoteLogin = Boolean.getBoolean("com.cannontech.yukon.forceRemoteLogin");
		boolean useLocalConfig = MasterConfigHelper.isLocalConfigAvailable() && !forceRemoteLogin;
        if (!isJws && useLocalConfig) {
		    CTILogger.info("Attempting local load of database properties...");
			success = doLocalLogin(parent, MasterConfigHelper.getLocalConfiguration());
		}
		else {
		    if (isJws && !forceRemoteLogin) {
		        CTILogger.info("Attempting JWS load of database properties...");
		        success = doJwsLogin(parent);
		    } else {
		        CTILogger.info("Attempting remote load of database properties...");
		        success = doRemoteLogin(parent);
		    }
		}		
		if (success) {
		    CTILogger.debug("Login was successful for " + getUser());
		} else {
		    CTILogger.debug("Login was not successful");
		}
		return success;
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
        boolean success = false;
        CTILogger.info("Attempting local load of database properties for SERVER login...");

        int userID = UserUtils.USER_ADMIN_ID;
        LiteYukonUser u = DaoFactory.getYukonUserDao().getLiteYukonUser(userID);
        if(u != null)
        {
            CTILogger.debug("  Assuming SERVER loginID to be " + userID );
            setSessionInfo(u);      
            success = true;
        }
        else
        {
            CTILogger.info("Unable to find default SERVER loginID of " + userID );
            success = false;
        }
            
        return success;
    }
    
	private boolean doLocalLogin(Frame p, ConfigurationSource configSource) {
		//PoolManager.setDBProperties(props);
        PoolManager.setConfigurationSource(configSource);
		
		LoginPrefs prefs = LoginPrefs.getInstance();

        try {
            // force load of the application context
            YukonSpringHook.getContext();

            LoginPanel lp = makeLocalLoginPanel();
            while(collectInfo(p, lp)) {
            	LiteYukonUser u = DaoFactory.getAuthDao().login(lp.getUsername(), lp.getPassword());
            	if(u != null) {
            		//score! we found them
            		setSessionInfo(u);
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
            		displayMessage(p, "Invalid Username or Password.  Usernames and Passwords are case sensitive, be sure to use correct upper and lower case.", "Error");
            	}
            }
        } catch (RuntimeException e) {
            handleOtherExceptions(p, e);
        }
		
		// They gave up trying to login
		return false;
	}
	
	private boolean doJwsLogin(Frame p) {
	    String jwsHost = System.getProperty("yukon.jws.host");
	    String jwsUser = System.getProperty("yukon.jws.user");
	    LoginPanel lp = makeJwsLoginPanel(jwsHost, jwsUser);
	    
        return doLoginLoop(p, lp);
	}

    private boolean doLoginLoop(Frame p, LoginPanel lp) {
        CTILogger.debug("Starting login dialog loop");
        LoginPrefs prefs = LoginPrefs.getInstance();
        while(collectInfo(p, lp)) {
            try {
                // test host
                URL url = new URL(lp.getYukonHost());
                
                MasterConfigHelper.setRemoteHostAndPort(lp.getYukonHost(), lp.getUsername(), lp.getPassword());
                System.setProperty("yukon.jws.pass", lp.getPassword());
                
                ConfigurationSource configuration = MasterConfigHelper.getConfiguration();
                // test the config by getting something that should always exist
                // (if we don't do this here, it will fail deep inside the context startup)
                configuration.getRequiredString("DB_USERNAME");
                
                PoolManager.setConfigurationSource(configuration);
                // force load of the application context
                YukonSpringHook.getContext();
                
                //Do not log in the user again
                LiteYukonUser u = DaoFactory.getYukonUserDao().findUserByUsername(lp.getUsername());
                if(u != null) {
                    CTILogger.debug("Got not null user: " + u);
                    //score! we found them
                    setSessionInfo(u);
                    
                    boolean saveInfo = lp.isRememberPassword();
                    prefs.setDefaultRememberPassword(saveInfo);
                    if(saveInfo) {
                        prefs.setDefaultUsername(lp.getUsername());
                        prefs.setDefaultPassword(lp.getPassword());
                    } else {
                        prefs.setDefaultUsername("");
                        prefs.setDefaultPassword("");
                    }
                    
                    // setup remote logging
                    YukonLogManager.initialize(lp.getYukonHost(), lp.getUsername(), lp.getPassword());
                    return true;
                } else {
                    // ooh, thats bad.
                    String msg =
                        "Unable to find user '" + lp.getUsername() + "' in database.  This probably a configuration problem.";
                    displayMessage(p, msg, "Error");
                    YukonSpringHook.shutdownContext();
                }
            } catch (MalformedURLException e) {
                CTILogger.warn("Invalid host: " + e.getMessage());
                Throwable cause = CtiUtilities.getRootCause(e);
                String msg = "Invalid host. (Example entry: http://yukon-app:8080) \n" + cause.getMessage();
                displayMessage(p, msg, "Error");
            } catch (RemoteConnectFailureException e) {
                CTILogger.warn("Got an exception during login", e);
                Throwable cause = CtiUtilities.getRootCause(e);
                String msg = "Unable to access the Yukon Web Application Service \n" + cause.getMessage();
                displayMessage(p, msg, "Error");
            } catch (RemoteAccessException e) {
                CTILogger.warn("Got an exception during login", e);
                Throwable cause = CtiUtilities.getRootCause(e);
                String msg = "Unable to login to the Yukon Web Application Service \n" + cause.getMessage();
                displayMessage(p, msg, "Error");
            } catch (RuntimeException e) {
                handleOtherExceptions(p, e);
            }
        }
        return false;
    }

    private void handleOtherExceptions(Frame p, RuntimeException e) {
        String msg;
        int indexOfJdbcException = ExceptionUtils.indexOfType(e, CannotGetJdbcConnectionException.class);
        if (indexOfJdbcException != -1) {
            msg = "Unable to connect to database\n";
            Throwable jdbcException = ExceptionUtils.getThrowables(e)[indexOfJdbcException];
            msg += jdbcException.getMessage();
        } else {
            CTILogger.warn("Got an exception during login", e);
            msg = "Unable to login: \n" + e.getMessage();
            Throwable cause = CtiUtilities.getRootCause(e);
            if (!e.equals(cause)) {
                msg += "\n" + cause.getMessage();
            }
        }
        displayMessage(p, msg, "Error");
    }
    
    private boolean doRemoteLogin(Frame p) {
        LoginPanel lp = makeRemoteLoginPanel();

        boolean success = doLoginLoop(p, lp);

        if(success) {
            LoginPrefs prefs = LoginPrefs.getInstance();
            prefs.setCurrentYukonHost(lp.getYukonHost());
        }
        return success;

    }
	
	private void setSessionInfo(LiteYukonUser u) {
        CTILogger.debug("Setting session: user=" + u);
		this.user = u;
	}
	
	private LoginPanel makeLocalLoginPanel() {
		LoginPrefs prefs = LoginPrefs.getInstance();
		return  new LoginPanel(prefs.getCurrentYukonHost(),
								prefs.getDefaultUsername(),
								prefs.getDefaultPassword(),
								prefs.getDefaultRememberPassword(), true);
	}
		
	private LoginPanel makeRemoteLoginPanel() {
	    LoginPrefs prefs = LoginPrefs.getInstance();
	    
	    // check stored host
	    String currentYukonHost = prefs.getCurrentYukonHost();
	    String host = "http://localhost:8080";
	    if (StringUtils.isNotBlank(currentYukonHost)) {
	        URL testUrl = null;
	        try {
                testUrl = new URL(currentYukonHost);
	        } catch (MalformedURLException e) {
	            try {
                    testUrl = new URL("http", currentYukonHost, prefs.getCurrentYukonPort(), "");
                } catch (MalformedURLException e1) {
                }
	        }
	        if (testUrl != null) {
	            host = testUrl.toExternalForm();
	        }
	    }

	    return  new LoginPanel(host,
	                           prefs.getDefaultUsername(),
	                           prefs.getDefaultPassword(),
	                           prefs.getDefaultRememberPassword(), false);
	}
		
	private LoginPanel makeJwsLoginPanel(String host, String userName) {
        LoginPrefs prefs = LoginPrefs.getInstance();
	    
	    LoginPanel loginPanel = new LoginPanel(host,
	                           userName,
	                           prefs.getDefaultPassword(userName),
                               prefs.getDefaultRememberPassword(), false);
	    loginPanel.setHostEditable(false);
	    loginPanel.setUserEditable(false);
        return loginPanel;
	}
	
	private boolean collectInfo(Frame parent, LoginPanel lp) {
	    return LoginFrame.showLogin(parent, lp);
	}
	
	private void displayMessage(Frame p, String msg, String title) {
        CTILogger.info(title + ": " + msg);
		JOptionPane.showMessageDialog(p, msg, title, JOptionPane.WARNING_MESSAGE); 
	}
	
	
	// My pretty private instance
	private static ClientSession instance;
}

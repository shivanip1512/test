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

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.ClientApplicationRememberMe;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.config.RemoteLoginSession;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.ThemeUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
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
 */
public class ClientSession {
    private LiteYukonUser user;
    private static ClientSession instance;
    private static RemoteLoginSession remoteSession;

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
	    RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
        return rolePropertyDao.checkRole(YukonRole.getForId(roleid), getUser());
	}
		
	/**
	 * Returns the value of the given role property for the current user.
	 * Utilizes rolePropertyDao.getPropertyStringValue, so this method
	 * will never return null. Instead returns "" for undefined rolePropertyIds. 
	 * @param rolePropertyID
	 * @return String representing a given role property ID
	 */
	public String getRolePropertyValue(int rolePropertyID) {
	    return getRolePropertyValue(YukonRoleProperty.getForId(rolePropertyID));
	}
	
	public String getRolePropertyValue(YukonRoleProperty yukonRoleProperty) {
	    RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
	    return rolePropertyDao.getPropertyStringValue(yukonRoleProperty, getUser());
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
        
        boolean isJws = StringUtils.isNotBlank(System.getProperty("jnlp.yukon.host"));
        CTILogger.info("Java Web Start property found: " + isJws);
        // "getBoolean" has to be the oddest Java method ever, but it is exactly what I want
        boolean forceRemoteLogin = Boolean.getBoolean("com.cannontech.yukon.forceRemoteLogin");
		boolean useLocalConfig = MasterConfigHelper.isLocalConfigAvailable() && !forceRemoteLogin;
        if (!isJws && useLocalConfig) {
		    CTILogger.info("Attempting local load of database properties...");
		    success = doLocalLogin(parent, MasterConfigHelper.getLocalConfiguration());
		} else {
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
        LiteYukonUser u = YukonSpringHook.getBean(YukonUserDao.class).getLiteYukonUser(userID);
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
        PoolManager.setConfigurationSource(configSource);

        try {
            ClientApplicationRememberMe rememberMeSetting
                = YukonSpringHook.getBean(GlobalSettingDao.class)
                    .getEnum(GlobalSettingType.CLIENT_APPLICATIONS_REMEMBER_ME, ClientApplicationRememberMe.class);

            LoginPanel lp = makeLocalLoginPanel(rememberMeSetting);

            while(collectInfo(p, lp)) {
                try {
                	LiteYukonUser loggingInUser = YukonSpringHook.getBean(AuthenticationService.class).login(lp.getUsername(), lp.getPassword());
                	
                	if(loggingInUser != null) {
                		//score! we found them
                		setSessionInfo(loggingInUser);
                        setPreferences(lp.getUsername(), lp.getPassword(), lp.isRememberMe(), rememberMeSetting);

                		return true;
                	} else {
                		// bad username or password
                		displayMessage(p, "Invalid Username or Password.  Usernames and Passwords are case sensitive, be sure to use correct upper and lower case.", "Error");
                	}
                } catch (PasswordExpiredException e) {
                    CTILogger.debug("The password for "+lp.getUsername()+" is expired.", e);
                    displayMessage(p, "The password for "+lp.getUsername()+" is expired.  Please login to the web to reset it.", "Error");
                } catch (AuthenticationThrottleException e) {
                    CTILogger.debug("Authentication failed for "+lp.getUsername()+" .", e);
                    displayMessage(p, "Login disabled, please retry after "+e.getThrottleSeconds()+" seconds. If the problem persists, contact your system administrator.", "Error");
                } catch (BadAuthenticationException e) {
                    CTILogger.debug("Authentication failed for "+lp.getUsername()+" .", e);
                    displayMessage(p, "Authentication failed for "+lp.getUsername()+". Check that CAPS LOCK is off, and try again. If the problem persists, contact your system administrator.", "Error");
                }
            }
        } catch (RuntimeException e) {
            handleOtherExceptions(p, e);
        }
		
		// They gave up trying to login
		return false;
	}
	
	private boolean doJwsLogin(Frame p) {
	    String jwsHost = System.getProperty("jnlp.yukon.host");
        String jwsUser = System.getProperty("jnlp.yukon.user");
	    LoginPanel lp = makeJwsLoginPanel(jwsHost, jwsUser);
	    
        return doRemoteLoginLoop(p, lp);
	}

    private boolean doRemoteLogin(Frame p) {
        LoginPanel lp = makeRemoteLoginPanel();

        boolean success = doRemoteLoginLoop(p, lp);

        if(success) {
            LoginPrefs prefs = LoginPrefs.getInstance();
            prefs.setCurrentYukonHost(lp.getYukonHost());
        }
        return success;
    }

    private boolean doRemoteLoginLoop(Frame p, LoginPanel lp) {
        CTILogger.debug("Starting login dialog loop");
        while(collectInfo(p, lp)) {
            try {
                remoteSession = new RemoteLoginSession(lp.getYukonHost(), lp.getUsername(), lp.getPassword());
                if(remoteSession.isValid()){
                    LiteYukonUser u = YukonSpringHook.getBean(YukonUserDao.class).findUserByUsername(lp.getUsername());
                    CTILogger.debug("user: " + u);
                    
                    // test host                                
                    ConfigurationSource configuration = MasterConfigHelper.getConfiguration();
                    // test the config by getting something that should always exist
                    // (if we don't do this here, it will fail deep inside the context startup)
                    configuration.getRequiredString("DB_USERNAME");
                    
                    PoolManager.setConfigurationSource(configuration);
                    // force load of the application context
                    YukonSpringHook.getContext();
                    
                    setSessionInfo(u);
                    ClientApplicationRememberMe rememberMeSetting 
                        = ClientApplicationRememberMe.fromString(System.getProperty("jnlp.yukon.rememberMe", ""));
                    setPreferences(lp.getUsername(), lp.getPassword(), lp.isRememberMe(), rememberMeSetting);
    
                    // setup remote logging
                    YukonLogManager.initialize(remoteSession);
                    return true;
                } else {
                    displayMessage(p, remoteSession.getErrorMsg(), "Error");
                    YukonSpringHook.shutdownContext();
                }
            } catch (RuntimeException e) {
                handleOtherExceptions(p, e);
            }
        }
        return false;
    }

    private void setPreferences(String username, String password, boolean shouldRememberCred,
            ClientApplicationRememberMe rememberMeSetting) {
        LoginPrefs prefs = LoginPrefs.getInstance();
        prefs.shouldRememberCredentials(shouldRememberCred);

        if(shouldRememberCred) {
            prefs.rememberCredentials(username, password, rememberMeSetting);
        } else {
            prefs.forgetCredentials();
        }
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
    
	private void setSessionInfo(LiteYukonUser u) {
        CTILogger.debug("Setting session: user=" + u);
		this.user = u;
	}

	private LoginPanel makeLocalLoginPanel(ClientApplicationRememberMe rememberMeSetting) {
		LoginPrefs prefs = LoginPrefs.getInstance();
		return  new LoginPanel(prefs.getCurrentYukonHost(),
								prefs.getRememberedUsername(),
								prefs.getRememberedPassword(),
								prefs.shouldRememberCredentials(), true, rememberMeSetting);
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

        ClientApplicationRememberMe rememberMeSetting 
            = ClientApplicationRememberMe.fromString(System.getProperty("jnlp.yukon.rememberMe", ""));

	    return  new LoginPanel(host,
	                           prefs.getRememberedUsername(),
	                           prefs.getRememberedPassword(),
	                           prefs.shouldRememberCredentials(), false, rememberMeSetting);
	}
		
	private LoginPanel makeJwsLoginPanel(String host, String userName) {
        LoginPrefs prefs = LoginPrefs.getInstance();
        ClientApplicationRememberMe rememberMeSetting 
            = ClientApplicationRememberMe.fromString(System.getProperty("jnlp.yukon.rememberMe", ""));
	    LoginPanel loginPanel = new LoginPanel(host,
	                           userName,
	                           prefs.getRememberedPassword(userName),
                               prefs.shouldRememberCredentials(), false, rememberMeSetting);
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

    public static RemoteLoginSession getRemoteSession() {
        return remoteSession;
    }
    
    public static boolean isRemoteSession(){
        if(getRemoteSession() == null){
            return false;
        }
        return true; 
    }
}

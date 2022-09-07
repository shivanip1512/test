package com.cannontech.common.login;

import java.awt.Frame;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.ClientApplicationRememberMe;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthenticationService;
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
import com.cannontech.user.YukonUserContext;

/**
 * Holds session information for client programs.
 * Start with the static establishSession methods.
 *
 */
public class ClientSession {
    private static ClientSession instance;

    private LiteYukonUser user;

    /**
     * Return the user associated with this session.
     */
    public LiteYukonUser getUser() {
        return user;
    }

    public static YukonUserContext getUserContext() {
        LiteYukonUser thisUser = getInstance().getUser();
        SimpleYukonUserContext result = new SimpleYukonUserContext(thisUser, Locale.getDefault(),
            TimeZone.getDefault(), ThemeUtils.getDefaultThemeName());
        return result;
    }

    /**
     * Checks if the current user has the given role
     */
    public boolean checkRole(int roleid) {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
        return rolePropertyDao.checkRole(YukonRole.getForId(roleid), getUser());
    }

    /**
     * Returns the value of the given role property for the current user.
     * Utilizes rolePropertyDao.getPropertyStringValue, so this method
     * will never return null. Instead returns "" for undefined rolePropertyIds.
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
        if (instance == null) {
            instance = new ClientSession();
        }
        return instance;
    }

    /**
     * Attempt to establish a session
     */
    public synchronized boolean establishSession() {
        return establishSession(null);
    }

    public synchronized boolean establishSession(Frame parent) {
        // disable CookieHandler so that weird things don't happen when used with Java Web Start
        CookieHandler.setDefault(null);

        boolean success = false;

        // "getBoolean" has to be the oddest Java method ever, but it is exactly what I want
        boolean useLocalConfig = MasterConfigHelper.isLocalConfigAvailable();
        if (useLocalConfig) {
            CTILogger.info("Attempting local load of database properties...");
            success = doLocalLogin(parent, MasterConfigHelper.getLocalConfiguration());
        } 
        if (success) {
            CTILogger.debug("Login was successful for " + getUser());
        } else {
            CTILogger.debug("Login was not successful");
        }
        return success;
    }

    private boolean doLocalLogin(Frame p, ConfigurationSource configSource) {
        PoolManager.setConfigurationSource(configSource);

        try {
            ClientApplicationRememberMe rememberMeSetting =
                YukonSpringHook.getBean(GlobalSettingDao.class).getEnum(
                    GlobalSettingType.CLIENT_APPLICATIONS_REMEMBER_ME, ClientApplicationRememberMe.class);

            LoginPanel lp = makeLocalLoginPanel(rememberMeSetting);

            while (collectInfo(p, lp)) {
                try {
                    LiteYukonUser loggingInUser =
                        YukonSpringHook.getBean(AuthenticationService.class).login(lp.getUsername(), lp.getPassword());

                    if (loggingInUser != null) {
                        // score! we found them
                        setSessionInfo(loggingInUser);
                        setPreferences(lp.getUsername(), lp.getPassword(), lp.isRememberMe(), rememberMeSetting);

                        return true;
                    } else {
                        // bad username or password
                        displayMessage(p, "Invalid Username or Password.  Usernames and Passwords are case sensitive, "
                                + "be sure to use correct upper and lower case.", "Error");
                    }
                } catch (PasswordExpiredException e) {
                    CTILogger.debug("The password for " + lp.getUsername() + " is expired.", e);
                    displayMessage(p, "The password for " + lp.getUsername()
                        + " is expired.  Please login to the web to reset it.", "Error");
                } catch (AuthenticationThrottleException e) {
                    CTILogger.debug("Authentication failed for " + lp.getUsername() + " .", e);
                    displayMessage(p, "Login disabled, please retry after " + e.getThrottleSeconds()
                        + " seconds. If the problem persists, contact your system administrator.", "Error");
                } catch (BadAuthenticationException e) {
                    CTILogger.debug("Authentication failed for " + lp.getUsername() + " .", e);
                    displayMessage(p, "Authentication failed for " + lp.getUsername() + ". Check that CAPS LOCK is "
                            + "off, and try again. If the problem persists, contact your system administrator.",
                            "Error");
                }
            }
        } catch (RuntimeException e) {
            handleOtherExceptions(p, e);
        }

        // They gave up trying to login
        return false;
    }

    /**
     * Stores the username and or password based on rememberMeSetting.
     *
     * For local logins the rememberMeSetting should come straight from globalSettingDao.
     * For remote logins, the preference will be set so LoginPrefs.getRememberMeSetting() should be
     * used.
     */
    private void setPreferences(String username, String password, boolean shouldRememberCred,
        ClientApplicationRememberMe rememberMeSetting) {
        LoginPrefs prefs = LoginPrefs.getInstance();
        prefs.shouldRememberCredentials(shouldRememberCred);

        if (shouldRememberCred) {
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
        user = u;
    }

    private LoginPanel makeLocalLoginPanel(ClientApplicationRememberMe rememberMeSetting) {
        LoginPrefs prefs = LoginPrefs.getInstance();
        String rememberedPassword = "";
        if (rememberMeSetting == ClientApplicationRememberMe.USERNAME_AND_PASSWORD) {
            rememberedPassword = prefs.getRememberedPassword();
        }

        return new LoginPanel(prefs.getCurrentYukonHost(), prefs.getRememberedUsername(), rememberedPassword,
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
                } catch (MalformedURLException e1) {}
            }
            if (testUrl != null) {
                host = testUrl.toExternalForm();
            }
        }

        String rememberedPassword = "";
        if (prefs.getRememberMeSetting() == ClientApplicationRememberMe.USERNAME_AND_PASSWORD) {
            rememberedPassword = prefs.getRememberedPassword();
        }

        return new LoginPanel(host, prefs.getRememberedUsername(), rememberedPassword,
            prefs.shouldRememberCredentials(), false, prefs.getRememberMeSetting());
    }

    private LoginPanel makeJwsLoginPanel(String host, String userName) {
        LoginPrefs prefs = LoginPrefs.getInstance();

        String rememberedPassword = "";
        if (prefs.getRememberMeSetting() == ClientApplicationRememberMe.USERNAME_AND_PASSWORD) {
            rememberedPassword = prefs.getRememberedPassword(userName);
        }

        LoginPanel loginPanel = new LoginPanel(host, userName, rememberedPassword, prefs.shouldRememberCredentials(),
            false, prefs.getRememberMeSetting());
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

}

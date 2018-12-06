package com.cannontech.web.login.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.login.LoginService;
import com.cannontech.web.login.SessionInitializer;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.security.csrf.CsrfTokenService;
import com.cannontech.web.stars.service.PasswordResetService;
import com.cannontech.web.util.SavedSession;

public class LoginServiceImpl implements LoginService {
    
    @Autowired private SessionInitializer sessionInitializer;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private AuthDao authDao;
    @Autowired private ContactDao contactDao;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private YukonUserDao yukonUserDao;
    
    private final Logger log = YukonLogManager.getLogger(LoginServiceImpl.class);
    
    private static final String INVALID_PARAMS = "failed=true";
    private static final String VOICE_ROOT = "/voice";
    private static final String USERNAME = LoginController.USERNAME;
    private static final String PASSWORD = LoginController.PASSWORD;
    private static final String TOKEN = LoginController.TOKEN;
    private static final String YUKON_USER = LoginController.YUKON_USER;
    private static final String SAVED_YUKON_USERS = LoginController.SAVED_YUKON_USERS;
    private static final String LOGIN_CLIENT_ACTIVITY_ACTION = ActivityLogActions.LOGIN_CLIENT_ACTIVITY_ACTION;
    private static final String LOGOUT_ACTIVITY_LOG = ActivityLogActions.LOGOUT_ACTIVITY_LOG;
    
    @Override
    public void login(HttpServletRequest request, String username, String password) 
    throws AuthenticationThrottleException, BadAuthenticationException, PasswordExpiredException {
        
        try {
            // Need to find the user so we can check role _before_ authentication.
            LiteYukonUser user = yukonUserDao.findUserByUsername(username);
            if (user == null) {
                log.info("Authentication failed (unknown user): username=" + username);
                throw new BadAuthenticationException(BadAuthenticationException.Type.UNKNOWN_USER);
            }
            rolePropertyDao.verifyRole(YukonRole.WEB_CLIENT, user);
            
            user = authenticationService.login(username, password);
            createSession(request, user);
            log.info("User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " 
                    + request.getRemoteAddr());
        } catch (AuthenticationThrottleException e) {
            log.info("Login attempt as " + username + " failed from " + request.getRemoteAddr()
                + "due to incorrect Password!Account Locked, throttleSeconds=" + e.getThrottleSeconds());
            throw e;
        } catch (BadAuthenticationException e) {
            if (e.getType() == BadAuthenticationException.Type.DISABLED_USER
                || e.getType() == BadAuthenticationException.Type.INVALID_PASSWORD)
                systemEventLogService.loginWebFailed(username, request.getRemoteAddr(), e.getType());
            throw e;
        }
    }
    
    @Override
    public final void createSession(HttpServletRequest request, LiteYukonUser user) {
        String csrfToken = null;
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(CsrfTokenService.SESSION_CSRF_TOKEN) != null) {
            csrfToken = (String) session.getAttribute(CsrfTokenService.SESSION_CSRF_TOKEN);
        }
        if (session != null) {
            session.invalidate();
        }
        session = request.getSession(true);
        if (csrfToken != null) {
            session.setAttribute(CsrfTokenService.SESSION_CSRF_TOKEN, csrfToken);
        }
        initSession(user, session, request);
        systemEventLogService.loginWeb(user, request.getRemoteAddr());
    }

    @Override
    public void clientLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletRequestBindingException, IOException {
        
        String username = ServletRequestUtils.getRequiredStringParameter(request, USERNAME);
        String password = ServletRequestUtils.getRequiredStringParameter(request, PASSWORD);
        
        try {
            LiteYukonUser user = authenticationService.login(username, password);
            
            HttpSession session = request.getSession(true);
            initSession(user, session, request);
            ActivityLogger.logEvent(user.getUserID(), LOGIN_CLIENT_ACTIVITY_ACTION, "User " + user.getUsername() 
                    + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());
            systemEventLogService.loginClient(user, request.getRemoteAddr());
            
        } catch (BadAuthenticationException e) {
            if (e.getType() == BadAuthenticationException.Type.DISABLED_USER
                || e.getType() == BadAuthenticationException.Type.INVALID_PASSWORD)
                systemEventLogService.loginClientFailed(username, request.getRemoteAddr(), e.getType());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (PasswordExpiredException e) {
            String passwordResetUrl = passwordResetService.getPasswordResetUrl(username, request, false);
            response.sendRedirect(passwordResetUrl);
        }
    }
    
    @Override
    public void invalidateSession(HttpServletRequest request, String reason) {
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        HttpSession session = request.getSession(false);
        session.invalidate();
        log.info("User " + user + " (userid=" + user.getUserID() + ") has been logged out from "
            + request.getRemoteAddr() + " Reason :" + reason);
        systemEventLogService.logoutWeb(user, request.getRemoteAddr(), reason);
        ActivityLogger.logEvent(user.getUserID(),LOGOUT_ACTIVITY_LOG, "User " + user.getUsername() 
                + " (userid=" + user.getUserID() + ") has been logged out from " 
                + request.getRemoteAddr() + ". Reason: " + reason);
    }
    
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String redirect = this.getRedirect(request);
        
        log.trace("redirect = '" + redirect + "'");
        
        try {
            LiteYukonUser user = ServletUtil.getYukonUser(request);
            log.trace("user = '" + user + "'");
            HttpSession session = request.getSession(false);
            SavedSession savedUsers = (SavedSession) session.getAttribute(SAVED_YUKON_USERS);
            log.trace("savedUsers = '" + savedUsers + "'");
            session.invalidate();
            
            redirect = "/" + rolePropertyDao.getPropertyStringValue(YukonRoleProperty.LOG_IN_URL, user);
            log.trace("Role Property: redirect = '" + redirect + "'");
            log.info("User " + user + " (userid=" + user.getUserID() + ") has logged out from " + request.getRemoteAddr());
            systemEventLogService.logoutWeb(user, request.getRemoteAddr(), "User initiated");
            ActivityLogger.logEvent(user.getUserID(),LOGOUT_ACTIVITY_LOG, "User " + user.getUsername() + " (userid="
                + user.getUserID() + ") has logged out from " + request.getRemoteAddr());
            if (savedUsers != null) {
                
                Properties oldContext = savedUsers.getProperties();
                redirect = savedUsers.getReferer();
                log.trace("SavedUsers: redirect = '" + redirect + "'");
                
                // Restore saved session context
                session = request.getSession( true );
                Enumeration<?> attNames = oldContext.propertyNames();
                while (attNames.hasMoreElements()) {
                    String attName = (String) attNames.nextElement();
                    session.setAttribute( attName, oldContext.get(attName) );
                }
            } else {
                ServletUtil.deleteAllCookies(request, response);
            }
        } catch (NotLoggedInException | UserNotInRoleException e) {
            redirect = LoginController.LOGIN_URL;
            log.trace("Caught Exception: redirect = '" + redirect + "'");
        }
        
        redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
        log.trace("Safe redirect = '" + redirect + "'");
        
        response.sendRedirect(redirect);
    }
    
    @Override
    public void outboundVoiceLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String username = request.getParameter(USERNAME);  //contactid
        String password = request.getParameter(PASSWORD);  //pin
        String redirect = this.getRedirect(request);
        String referer = this.getReferer(request);
        
        //if the pin is valid...log us in
        int contactid = Integer.MIN_VALUE;
        try {
            contactid = Integer.parseInt(username);
        } catch(NumberFormatException nfe) {
            log.debug("Unable to parse given ContactID value into an Integer, value = " + contactid);
        }
        
        LiteContact lContact = contactDao.getContact(contactid); //store this for logging purposes only
        LiteYukonUser user = authDao.voiceLogin(contactid, password);
        
        String voice_home_url = "/voice/notification.jsp";
        
        if (user != null) {
            HttpSession session = request.getSession();
            
            if (session != null && session.getAttribute(YUKON_USER) != null) {
                session.invalidate();
                session = request.getSession(true);
            }
            
            initSession(user, session, request);
            session.setAttribute( TOKEN, request.getParameter(TOKEN) );
            systemEventLogService.loginOutboundVoice(user, request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + voice_home_url);
        } else {  // Login failed, send them on their way using one of
            // REDIRECT parameter, referer, INVALID_URI in that order
            if (redirect == null) {
                if(referer != null) {
                    redirect = referer;
                    if(!redirect.endsWith(INVALID_PARAMS)) {
                        redirect += "?" + INVALID_PARAMS;
                    }
                } else {

                    redirect = VOICE_ROOT + LoginController.INVALID_URI;
                }
            }
            systemEventLogService.loginOutboundVoiceFailed(username, request.getRemoteAddr());
            log.info("VOICE Login attempt for contact " + lContact.toString() + " failed from "
                + request.getRemoteAddr());
            redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
            response.sendRedirect(redirect);
        }
    }
    
    @Override
    public LiteYukonUser internalLogin(HttpServletRequest request, HttpSession session, String username, 
            boolean saveCurrentUser) {
        
        LiteYukonUser user = yukonUserDao.findUserByUsername(username);
        if (user == null) {
            return null;
        }
        
        Properties oldContext = null;
        if (saveCurrentUser && session.getAttribute(YUKON_USER) != null) {
            oldContext = new Properties();
            Enumeration<String> attNames = session.getAttributeNames();
            while (attNames.hasMoreElements()) {
                String attName = attNames.nextElement();
                oldContext.put(attName, session.getAttribute(attName));
            }
        }
        
        session.invalidate();
        session = request.getSession(true);
        
        String referer = this.getReferer(request);
        if (referer == null) {
            referer = "/home";
        }
        
        // Save the old session context and where to direct the browser when the new user logs off
        if (oldContext != null) {
            session.setAttribute( SAVED_YUKON_USERS, SavedSession.of(oldContext, referer));
        }
        
        initSession(user, session, request);
        ActivityLogger.logEvent(user.getUserID(), LoginService.LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername() 
                + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());
        systemEventLogService.loginWeb(user, request.getRemoteAddr());
        CtiNavObject nav = (CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE);
        
        if (nav == null) {
            nav = new CtiNavObject();
        }
        nav.setInternalLogin(true);
        session.setAttribute(ServletUtils.NAVIGATE, nav);
        
        return user;
    }
    
    private String getReferer(final HttpServletRequest request) {
        String referer = request.getHeader("referer");
        return referer;
    }
    
    private String getRedirect(final HttpServletRequest request) {
        String redirect = request.getParameter(ServletUtil.ATT_REDIRECT);
        return  redirect;
    }
    
    private void initSession(final LiteYukonUser user, final HttpSession session, final HttpServletRequest request) {
        
        session.setAttribute(YUKON_USER, user);
        sessionInitializer.initSession(user, session);
        
        /* Add tracking for last activity time. */
        SessionInfo sessionInfo = new SessionInfo(request.getRemoteAddr());
        
        session.setAttribute(ServletUtil.SESSION_INFO, sessionInfo);
        
        // Do not log sessionId here for security reasons
        log.info("Created session for user:" + user);
    }
    
}
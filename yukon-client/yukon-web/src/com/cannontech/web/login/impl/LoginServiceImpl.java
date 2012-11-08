package com.cannontech.web.login.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.util.Pair;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.login.LoginService;
import com.cannontech.web.login.SessionInitializer;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.stars.service.PasswordResetService;

public class LoginServiceImpl implements LoginService {
    private static final String INVALID_PARAMS = "failed=true";
    private static final String INVALID_INBOUND_URI = "/voice/inboundLogin.jsp";
    private static final String VOICE_ROOT = "/voice";
    private static final String PHONE_NUMBER = "PHONE";
    private static final String PIN = "PIN";
    private static final String USERNAME = com.cannontech.common.constants.LoginController.USERNAME;
    private static final String PASSWORD = com.cannontech.common.constants.LoginController.PASSWORD;
    private static final String TOKEN = com.cannontech.common.constants.LoginController.TOKEN;
    private static final String REDIRECT = com.cannontech.common.constants.LoginController.REDIRECT;
    private static final String YUKON_USER = com.cannontech.common.constants.LoginController.YUKON_USER;
    private static final String SAVED_YUKON_USERS = com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS;
    private static final String LOGIN_CLIENT_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_CLIENT_ACTIVITY_ACTION;
    private static final String LOGOUT_ACTIVITY_LOG = com.cannontech.database.data.activity.ActivityLogActions.LOGOUT_ACTIVITY_LOG;
    private static final String LOGIN_FAILED_ACTIVITY_LOG = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_FAILED_ACTIVITY_LOG;
    private static final String OUTBOUND_LOGIN_VOICE_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_VOICE_ACTIVITY_ACTION;
    private static final String INBOUND_LOGIN_VOICE_ACTIVITY_ACTION = "LOG IN (INBOUND VOICE)";

    private List<SessionInitializer> sessionInitializers;

    @Autowired private AuthenticationService authenticationService;
    @Autowired private AuthDao authDao;
    @Autowired private ContactDao contactDao;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private YukonUserDao yukonUserDao;

    @Override
    public void login(HttpServletRequest request, String username, String password) 
    throws AuthenticationThrottleException, BadAuthenticationException, PasswordExpiredException {
        try {
            final LiteYukonUser user = authenticationService.login(username, password);
            
            rolePropertyDao.verifyRole(YukonRole.WEB_CLIENT, user);
            
            createSession(request, user);

        } catch (AuthenticationThrottleException e) {
            ActivityLogger.logEvent(LOGIN_FAILED_ACTIVITY_LOG,
                                    "Login attempt as " + username + " failed from " + request.getRemoteAddr() + ", throttleSeconds=" + e.getThrottleSeconds());
            throw e;
        } catch (BadAuthenticationException e) {
            ActivityLogger.logEvent(LOGIN_FAILED_ACTIVITY_LOG, "Login attempt as " + username + " failed from " + request.getRemoteAddr());
            throw e;
        }
    }
    
    public final void createSession(HttpServletRequest request, LiteYukonUser user) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        session = request.getSession(true);
        initSession(user, session, request);
        systemEventLogService.loginWeb(user, request.getRemoteAddr());
    }
    
    @Override
    public void clientLogin(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException {
        String username = ServletRequestUtils.getRequiredStringParameter(request, USERNAME);
        String password = ServletRequestUtils.getRequiredStringParameter(request, PASSWORD);
        
        try {
            LiteYukonUser user = authenticationService.login(username, password);

            HttpSession session = request.getSession(true);
            initSession(user, session, request);
            ActivityLogger.logEvent(user.getUserID(), LOGIN_CLIENT_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());
            systemEventLogService.loginClient(user, request.getRemoteAddr());
        } catch (BadAuthenticationException e) {
            ActivityLogger.logEvent(LOGIN_FAILED_ACTIVITY_LOG, "Login attempt as " + username + " failed from " + request.getRemoteAddr());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (PasswordExpiredException e) {
            String passwordResetUrl = passwordResetService.getPasswordResetUrl(username, request);
            response.sendRedirect(passwordResetUrl);
        }
    }

    @Override
    public void invalidateSession(HttpServletRequest request, String reason) {
    	
    	LiteYukonUser user = ServletUtil.getYukonUser(request);
    	HttpSession session = request.getSession(false);
    	session.invalidate();
    	
    	ActivityLogger.logEvent(user.getUserID(),LOGOUT_ACTIVITY_LOG, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has been logged out from " + request.getRemoteAddr() + ". Reason: " + reason);
    }
    
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirect = this.getRedirect(request);
        
        try {
            LiteYukonUser user = ServletUtil.getYukonUser(request);
            HttpSession session = request.getSession(false);
            Pair<?,?> p = (Pair<?,?>) session.getAttribute(SAVED_YUKON_USERS);
            session.invalidate();
            
            redirect = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.LOG_IN_URL, user);
            ActivityLogger.logEvent(user.getUserID(),LOGOUT_ACTIVITY_LOG, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged out from " + request.getRemoteAddr());

            if (p != null) {
                Properties oldContext = (Properties) p.getFirst();
                redirect = (String) p.getSecond();
                
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
        } catch (NotLoggedInException e) {
            redirect = ServletUtil.createSafeUrl(request, LoginController.LOGIN_URL);
        } catch (UserNotInRoleException e) {
            redirect = ServletUtil.createSafeUrl(request, LoginController.LOGIN_URL);
        }        

        response.sendRedirect(redirect);
    }

    @Override
    public void inboundVoiceLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String phone = request.getParameter(PHONE_NUMBER); // phone number
        String pin = request.getParameter(PIN); // pin
        String tries = request.getParameter("TRIES"); // pin

        LiteYukonUser user = authDao.inboundVoiceLogin(phone, pin);

        if( user != null) {
            HttpSession session = request.getSession();

            if (session != null && session.getAttribute(YUKON_USER) != null) {
                session.invalidate();
                session = request.getSession(true);
            }                   

            initSession(user, session, request);
            ActivityLogger.logEvent(
                                    INBOUND_LOGIN_VOICE_ACTIVITY_ACTION, 
                                    "INBOUND VOICE User " + user.getUsername() + " (userid=" + 
                                    user.getUserID() + ") has logged in from " + request.getRemoteAddr());
            systemEventLogService.loginInboundVoice(user, request.getRemoteAddr());
            response.sendRedirect(
                                  request.getContextPath() + 
                                  rolePropertyDao.getPropertyStringValue(
                                		  YukonRoleProperty.INBOUND_VOICE_HOME_URL, user));
        } else {

            ActivityLogger.logEvent(
                                    INBOUND_LOGIN_VOICE_ACTIVITY_ACTION, 
                                    "INBOUND VOICE User could not be logged in with phone: " + 
                                    phone + " from " + 
                                    request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + INVALID_INBOUND_URI + "?TRIES=" + tries);
        }
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
        } catch(NumberFormatException nfe) { CTILogger.debug("Unable to parse given ContactID value into an Integer, value = " + contactid);}


        LiteContact lContact = contactDao.getContact( contactid ); //store this for logging purposes only
        LiteYukonUser user = authDao.voiceLogin( contactid, password );

        String voice_home_url = "/voice/notification.jsp";

        if( user != null 
        /*&& (voice_home_url = authDao.getRolePropertyValue(user,WebClientRole.HOME_URL)) != null*/ )
        {
            HttpSession session = request.getSession();

            if (session != null && session.getAttribute(YUKON_USER) != null) {
                session.invalidate();
                session = request.getSession(true);
            }                   

            initSession(user, session, request);
            session.setAttribute( TOKEN, request.getParameter(TOKEN) );
            ActivityLogger.logEvent(user.getUserID(), OUTBOUND_LOGIN_VOICE_ACTIVITY_ACTION, "VOICE User " + user.getUsername() + " (userid=" + user.getUserID() + ") (Contact=" + lContact.toString() + ") has logged in from " + request.getRemoteAddr());
            systemEventLogService.loginOutboundVoice(user, request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + voice_home_url);
        }
        else {  // Login failed, send them on their way using one of
            // REDIRECT parameter, referer, INVALID_URI in that order 
            if (redirect == null) {
                if(referer != null) {
                    redirect = referer;
                    if(!redirect.endsWith(INVALID_PARAMS)) 
                        redirect += "?" + INVALID_PARAMS;    
                }
                else {
                    redirect = request.getContextPath() + VOICE_ROOT + LoginController.INVALID_URI;
                }
            }
            ActivityLogger.logEvent(LOGIN_FAILED_ACTIVITY_LOG, "VOICE Login attempt for contact " + lContact.toString() + " failed from " + request.getRemoteAddr());
            redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
            response.sendRedirect(redirect);
        }
    }
    
    @Override
    public LiteYukonUser internalLogin(HttpServletRequest request, HttpSession session, String username, boolean saveCurrentUser) {
        LiteYukonUser user = yukonUserDao.findUserByUsername(username);
        if (user == null || StringUtils.isBlank(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user)))
            return null;
        
        Properties oldContext = null;
        if (saveCurrentUser && session.getAttribute(YUKON_USER) != null) {
            oldContext = new Properties();
            Enumeration<?> attNames = session.getAttributeNames();
            while (attNames.hasMoreElements()) {
                String attName = (String) attNames.nextElement();
                oldContext.put( attName, session.getAttribute(attName) );
            }
        }
        
        session.invalidate();
        session = request.getSession( true );
        
        
        String referer = this.getReferer(request);
        if(referer == null)
            referer = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user);
        
        // Save the old session context and where to direct the browser when the new user logs off
        if (oldContext != null) {
            session.setAttribute( SAVED_YUKON_USERS, new Pair<Properties, String>(oldContext, referer) );
        }
        
        initSession(user, session, request);
        ActivityLogger.logEvent(user.getUserID(), LoginService.LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());
        systemEventLogService.loginWeb(user, request.getRemoteAddr());
        CtiNavObject nav = (CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE);
        if(nav == null) nav = new CtiNavObject();
        nav.setInternalLogin(true);
        session.setAttribute(ServletUtils.NAVIGATE, nav);  
        
        return user;
    }

    private String getReferer(final HttpServletRequest request) {
        String referer = request.getHeader("referer");
        return referer;
    }

    private String getRedirect(final HttpServletRequest request) {
        String redirect = request.getParameter(REDIRECT);
        return  redirect;
    }

    private void initSession(final LiteYukonUser user, final HttpSession session, final HttpServletRequest request) {
        session.setAttribute(YUKON_USER, user);
        for (final SessionInitializer initializer : sessionInitializers) {
            initializer.initSession(user, session);
        }
        
        /* Add tracking for last activity time. */
        SessionInfo sessionInfo = new SessionInfo(request.getRemoteAddr());
        
        session.setAttribute(ServletUtil.SESSION_INFO, sessionInfo);
        
        CTILogger.info("Created session " + session.getId() + " for " + user);
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public void setSessionInitializers(List<SessionInitializer> sessionInitializers) {
        this.sessionInitializers = sessionInitializers;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}

}

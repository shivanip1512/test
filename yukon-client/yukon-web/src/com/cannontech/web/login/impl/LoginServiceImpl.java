package com.cannontech.web.login.impl;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.login.LoginService;
import com.cannontech.web.navigation.CtiNavObject;

public class LoginServiceImpl implements LoginService {
    private static final String LOGIN_URI = "/login.jsp";
    private static final String INVALID_PARAMS = "failed=true";
    private static final String INVALID_URI = "/login.jsp?failed=true";
    private static final String INVALID_INBOUND_URI = "/voice/inboundLogin.jsp";
    private static final String VOICE_ROOT = "/voice";
    private static final String PHONE_NUMBER = "PHONE";
    private static final String PIN = "PIN";
    private static final String USERNAME = com.cannontech.common.constants.LoginController.USERNAME;
    private static final String PASSWORD = com.cannontech.common.constants.LoginController.PASSWORD;
    private static final String TOKEN = com.cannontech.common.constants.LoginController.TOKEN;
    private static final String REDIRECT = com.cannontech.common.constants.LoginController.REDIRECT;
    private static final String SAVE_CURRENT_USER = com.cannontech.common.constants.LoginController.SAVE_CURRENT_USER;
    private static final String YUKON_USER = com.cannontech.common.constants.LoginController.YUKON_USER;
    private static final String SAVED_YUKON_USERS = com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS;
    private static final String LOGIN_URL_COOKIE = com.cannontech.common.constants.LoginController.LOGIN_URL_COOKIE;
    private static final String LOGIN_WEB_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_WEB_ACTIVITY_ACTION;
    private static final String LOGIN_CLIENT_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_CLIENT_ACTIVITY_ACTION;
    private static final String LOGOUT_ACTIVITY_LOG = com.cannontech.database.data.activity.ActivityLogActions.LOGOUT_ACTIVITY_LOG;
    private static final String LOGIN_FAILED_ACTIVITY_LOG = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_FAILED_ACTIVITY_LOG;
    private static final String OUTBOUND_LOGIN_VOICE_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_VOICE_ACTIVITY_ACTION;
    private static final String INBOUND_LOGIN_VOICE_ACTIVITY_ACTION = "LOG IN (INBOUND VOICE)";
    private AuthDao authDao;
    private ContactDao contactDao;
    private YukonUserDao yukonUserDao;

    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public void clientLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = request.getParameter(USERNAME);
        String password = request.getParameter(PASSWORD);   
        LiteYukonUser user = authDao.login(username,password);

        if(user != null) {
            HttpSession session = request.getSession(true);
            initSession(user, session);
            ActivityLogger.logEvent(user.getUserID(), LOGIN_CLIENT_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());
        }
        else{
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    public boolean login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return login(request, response, null, null, null);
    }
    
    public boolean login(HttpServletRequest request, HttpServletResponse response, String username, String password) throws Exception {
        return login(request, response, username, password, null);
    }
    
    public boolean login(HttpServletRequest request, HttpServletResponse response, String username, String password, 
            String redirectedFrom) throws Exception {
        
        username = (username != null) ? username : ServletRequestUtils.getStringParameter(request, USERNAME);
        password = (password != null) ? password : ServletRequestUtils.getStringParameter(request, PASSWORD);
        
        String referer = this.getReferer(request);
        String redirect = this.getRedirect(request);
        
        LiteYukonUser user = authDao.login(username,password);
        String home_url = null;

        if(user != null && 
                (home_url = authDao.getRolePropertyValue(user,WebClientRole.HOME_URL)) != null) {
            HttpSession session = request.getSession();

            if (request.getParameter(SAVE_CURRENT_USER) != null) {
                if (session != null && session.getAttribute(YUKON_USER) != null) {
                    Properties oldContext = new Properties();
                    Enumeration attNames = session.getAttributeNames();
                    while (attNames.hasMoreElements()) {
                        String attName = (String) attNames.nextElement();
                        oldContext.put( attName, session.getAttribute(attName) );
                    }

                    session.invalidate();
                    session = request.getSession(true);

                    if(referer == null)
                        referer = authDao.getRolePropertyValue(user,WebClientRole.HOME_URL);

                    // Save the old session context and where to direct the browser when the new user logs off
                    session.setAttribute( SAVED_YUKON_USERS, new Pair(oldContext, referer) );
                }
            }
            else {
                if (session != null && session.getAttribute(YUKON_USER) != null) {
                    session.invalidate();
                    session = request.getSession(true);
                }

                //stash a cookie that might tell us later where they log in at                              
                String loginUrl = authDao.getRolePropertyValue(user, WebClientRole.LOG_IN_URL);
                if (loginUrl.startsWith("/")) loginUrl = request.getContextPath() + loginUrl;

                Cookie c = new Cookie(LOGIN_URL_COOKIE, loginUrl);
                c.setPath("/"+request.getContextPath());
                c.setMaxAge(Integer.MAX_VALUE);
                response.addCookie(c);
            }

            initSession(user, session);
            ActivityLogger.logEvent(user.getUserID(), LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());

            String location = (redirectedFrom != null) ? redirectedFrom : request.getContextPath() + home_url ;
            response.sendRedirect(location);
            return true;
        }
        
        // Login failed, send them on their way using one of
        // REDIRECT parameter, referer, INVALID_URI in that order 
        if (redirect == null) {
            if(referer != null) {
                redirect = referer;
                if(!redirect.endsWith(INVALID_PARAMS)) 
                    redirect += "?" + INVALID_PARAMS;    
            }
            else {
                redirect = request.getContextPath() + INVALID_URI;
            }
        }
        ActivityLogger.logEvent(LOGIN_FAILED_ACTIVITY_LOG, "Login attempt as " + username + " failed from " + request.getRemoteAddr());
        response.sendRedirect(redirect);
        return false;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        String redirect = this.getRedirect(request);

        if(session != null) {
            LiteYukonUser user = (LiteYukonUser) session.getAttribute(YUKON_USER);          
            Pair p = (Pair) session.getAttribute(SAVED_YUKON_USERS);
            session.invalidate();

            if(user != null) {
                redirect = authDao.getRolePropertyValue(user, WebClientRole.LOG_IN_URL);
                ActivityLogger.logEvent(user.getUserID(),LOGOUT_ACTIVITY_LOG, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged out from " + request.getRemoteAddr());
            }

            if (p != null) {
                Properties oldContext = (Properties) p.getFirst();
                redirect = (String) p.getSecond();

                // Restore saved session context
                session = request.getSession( true );
                Enumeration attNames = oldContext.propertyNames();
                while (attNames.hasMoreElements()) {
                    String attName = (String) attNames.nextElement();
                    session.setAttribute( attName, oldContext.get(attName) );
                }
            }
        }

        //Try to send them back to where they logged in from
        if (redirect == null) {
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {       
                for(int i = 0; i < cookies.length; i++) {
                    Cookie c = cookies[i];
                    if(c.getName().equalsIgnoreCase(LOGIN_URL_COOKIE)) {
                        redirect = c.getValue();
                        break;
                    }
                }
            }
        }

        if (redirect == null)
            redirect = request.getContextPath() + LOGIN_URI;

        redirect = (!redirect.equals(LOGIN_URI)) ? redirect : redirect + "?force=true";
        response.sendRedirect(redirect);
    }

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

            initSession(user, session);
            ActivityLogger.logEvent(
                                    INBOUND_LOGIN_VOICE_ACTIVITY_ACTION, 
                                    "INBOUND VOICE User " + user.getUsername() + " (userid=" + 
                                    user.getUserID() + ") has logged in from " + request.getRemoteAddr());

            response.sendRedirect(
                                  request.getContextPath() + 
                                  authDao.getRolePropertyValue(user.getUserID(), 
                                                               WebClientRole.INBOUND_VOICE_HOME_URL));
        } else {

            ActivityLogger.logEvent(
                                    INBOUND_LOGIN_VOICE_ACTIVITY_ACTION, 
                                    "INBOUND VOICE User could not be logged in with phone: " + 
                                    phone + " from " + 
                                    request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + INVALID_INBOUND_URI + "?TRIES=" + tries);
        }
    }

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

            initSession(user, session);
            session.setAttribute( TOKEN, request.getParameter(TOKEN) );
            ActivityLogger.logEvent(user.getUserID(), OUTBOUND_LOGIN_VOICE_ACTIVITY_ACTION, "VOICE User " + user.getUsername() + " (userid=" + user.getUserID() + ") (Contact=" + lContact.toString() + ") has logged in from " + request.getRemoteAddr());

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
                    redirect = request.getContextPath() + VOICE_ROOT + INVALID_URI;
                }
            }
            ActivityLogger.logEvent(LOGIN_FAILED_ACTIVITY_LOG, "VOICE Login attempt for contact " + lContact.toString() + " failed from " + request.getRemoteAddr());
            response.sendRedirect(redirect);
        }
    }
    
    public LiteYukonUser internalLogin(HttpServletRequest request, HttpSession session, String username, boolean saveCurrentUser) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(username);
        if (user == null || authDao.getRolePropertyValue(user,WebClientRole.HOME_URL) == null)
            return null;
        
        Properties oldContext = null;
        if (saveCurrentUser && session.getAttribute(YUKON_USER) != null) {
            oldContext = new Properties();
            Enumeration attNames = session.getAttributeNames();
            while (attNames.hasMoreElements()) {
                String attName = (String) attNames.nextElement();
                oldContext.put( attName, session.getAttribute(attName) );
            }
        }
        
        session.invalidate();
        session = request.getSession( true );
        
        
        String referer = this.getReferer(request);
        if(referer == null)
            referer = authDao.getRolePropertyValue(user,WebClientRole.HOME_URL);
        
        // Save the old session context and where to direct the browser when the new user logs off
        if (oldContext != null)
            session.setAttribute( SAVED_YUKON_USERS, new Pair(oldContext, referer) );

        initSession(user, session);
        ActivityLogger.logEvent(user.getUserID(), LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());

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

    private static void initSession(LiteYukonUser user, HttpSession session) {
        session.setAttribute(YUKON_USER, user);
        CTILogger.info("Created session " + session.getId() + " for " + user);
    }


    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

}

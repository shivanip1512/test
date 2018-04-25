package com.cannontech.web.login;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.exception.SessionTimeoutException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.login.access.UrlAccessChecker;
import com.cannontech.web.login.impl.SessionInfo;
import com.cannontech.web.util.YukonUserContextResolver;
import com.google.common.collect.ImmutableList;

public class LoginFilter implements Filter {
    private static final Logger log = YukonLogManager.getLogger(LoginFilter.class);
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private YukonUserContextResolver userContextResolver;
    private UrlAccessChecker urlAccessChecker;
    private RolePropertyDao rolePropertyDao;
    private LoginService loginService;
    private LoginCookieHelper loginCookieHelper;

    // Setup ant-style paths that should be processed even if the user is not logged in.
    // All paths should start with a slash because that's just the way it works.
    private final static ImmutableList<String> excludedFilePaths =
        ImmutableList.of(LoginController.LOGIN_URL,
                         "/remoteLogin",
                         "/integrationLogin",
                         "/login/forgotPassword",
                         "/login/forgottenPassword",
                         "/login/change-password",
                         "/login/check-password",
                         "/servlet/LoginController",
                         "/soap/*",
                         "/multispeak/v3/*",
                         "/multispeak/v5/*", 
                         "/servlet/PWordRequest",
                         "/servlet/StarsPWordRequest",
                         "/**/*.js",
                         "/**/*.css",
                         "/**/*.png",
                         "/**/*.gif",
                         "/**/*.jpg",
                         "/**/*.html",
                         // Web start jars are protected by custom token filter. See JwsController.
                         "/jws/*.jar",
                         "/jws/bc.jnlp",
                         "/jws/client_libs.jnlp",
                         "/jws/sqlserver_jdbc.jnlp",
                         "/common/images/*",
                         "/resources/favicon.ico",
                		 "/dev/mockecobee/**");

    private final static ImmutableList<String> excludedRedirectedPaths =
        ImmutableList.of("/servlet/SOAPClient/**",
                         "/jws/*",
                         "/common/config/deviceDefinition",
                         "/common/config/rfn",
                         "/remote/MasterConfig",
                         // URL to get remote logging configuration.
                         "/servlet/LoggingServlet",
                         // URL to actually do remote logging.
                         "/remote/remoteLogin");
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Support accented chars in request parameters.
        servletRequest.setCharacterEncoding("UTF-8");

        boolean isAjaxRequest = ServletUtil.isAjaxRequest(servletRequest);
        boolean excludedRequest = ServletUtil.isPathMatch(request, excludedFilePaths);

        // For excluded requests, try to attach the userContext, but they may not be logged in.
        if (excludedRequest) {
            log.debug("Proceeding with request that passes exclusion filter");
            attachYukonUserContext(request, false);
            chain.doFilter(request, response);
            return;
        }

        if (!isJsessionCookieLoggedIn(request) && !doRememberMeCookieLogin(request, response)
            && !doDefaultParameterLogin(request)) {
            // If we got here, they couldn't be authenticated, send an error response or redirect to login page.
            log.debug("All login attempts failed, returning error");
            doLoginRedirect(isAjaxRequest, request, response);
            return;
        }

        log.debug("Proceeding with request after successful handler login");
        // At this point the user has been authenticated, check for timeout and authorization.
        LiteYukonUser user = attachYukonUserContext(request, true).getYukonUser();

        // Check session timeout, update last activity time if successful, redirect to login page if expired.
        try {
            checkSessionTimeout(request, isAjaxRequest);
        } catch (SessionTimeoutException e) {
            log.info("User " + user.getUsername() + " logged out due to inactivity");
            loginService.invalidateSession(request, "TIMEOUT");
            doLoginRedirect(isAjaxRequest, request, response);
            
            // If rememberMe option is selected by user and session gets expired, let the user be logged in
            // then using credentials from rememberMeCookie instead of creating separate session for each ajax
            // request.
            doRememberMeCookieLogin(request, response);
            return;
        }

        // Check for access authorization
        try {
            verifyPathAccess(request);
        } catch (NotAuthorizedException naexception) {
            log.error(naexception.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    private void checkSessionTimeout(HttpServletRequest request, boolean isAjaxRequest) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            SessionInfo sessionInfo = (SessionInfo) session.getAttribute(ServletUtil.SESSION_INFO);
            // Update the IP Address
            sessionInfo.setIpAddress(request.getRemoteAddr());

            Instant lastActivityTime = sessionInfo.getLastActivityTime();
            LiteYukonUser user = YukonUserContextUtils.getYukonUserContext(request).getYukonUser();
            int sessionTimeout = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.SESSION_TIMEOUT, user);

            Instant validActivityThreshold = new Instant().minus(Duration.standardMinutes(sessionTimeout));

            if (lastActivityTime.isBefore(validActivityThreshold)) {
                // Timeout
                log.debug("Session Timeout for request: " + request.getRequestURI());
                throw new SessionTimeoutException();
            }
            // Update the last activity time to now for non ajax requests.
            if (!isAjaxRequest) {
                sessionInfo.setLastActivityTime(new Instant());
            }
        } else {
            // Timeout
            log.debug("Session Timeout for request: " + request.getRequestURI());
            throw new SessionTimeoutException();
        }
    }

    private void verifyPathAccess(final HttpServletRequest request) {
        boolean hasUrlAccess = urlAccessChecker.hasUrlAccess(request);
        if (!hasUrlAccess) {
            throw new NotAuthorizedException("Invalid path access for user: "
                + urlPathHelper.getPathWithinApplication(request));
        }
    }

    /**
     * This method will do a redirect to the login page if the page is not an AJAX page or one of those listed
     * in {@link #excludedRedirectedPaths}.  In those cases, it returns {@link HttpServletResponse#SC_FORBIDDEN}.
     */
    private void doLoginRedirect(boolean isAjaxRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        boolean noRedirect = ServletUtil.isPathMatch(request, excludedRedirectedPaths);

        if (isAjaxRequest || noRedirect) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Authenticated!");
        } else {
            String redirectFromUrl = request.getRequestURI().substring(request.getContextPath().length());

            String urlParams = request.getQueryString();
            String unencodedNavUrl = redirectFromUrl + ((urlParams != null) ? "?" + urlParams : "");
            String encodedNavUrl = ServletUtil.urlEncode(unencodedNavUrl);

            String loginUrl = request.getContextPath() + LoginController.LOGIN_URL;
            if (!"/".equals(redirectFromUrl) && !StringUtils.isBlank(encodedNavUrl)) {
                loginUrl += "?" + LoginController.REDIRECTED_FROM + "=" + encodedNavUrl;
            }
            response.sendRedirect(loginUrl);
        }
    }

    private boolean isJsessionCookieLoggedIn(ServletRequest request) {
        try {
            ServletUtil.getYukonUser(request);
            return true;
        } catch (NotLoggedInException e) {
            return false;
        }
    }

    private YukonUserContext attachYukonUserContext(HttpServletRequest request, boolean isFailureAnError) {
        try {
            YukonUserContext userContext = userContextResolver.resolveContext(request);
            request.setAttribute(YukonUserContextUtils.userContextAttrName, userContext);
            return userContext;
        } catch (RuntimeException e) {
            request.setAttribute(YukonUserContextUtils.userContextAttrName, e);
            if (isFailureAnError) {
                log.info("Unable to attach YukonUserContext to request", e);
            } else {
                log.debug("Unable to attach YukonUserContext to request");
            }
            return null;
        }
    }

    private boolean doRememberMeCookieLogin(HttpServletRequest request, HttpServletResponse response) {
        UserPasswordHolder userPass = loginCookieHelper.readRememberMeCookie(request);
        if (userPass != null) {
            try {
                loginService.login(request, userPass.getUsername(), userPass.getPassword());
                LiteYukonUser user = ServletUtil.getYukonUser(request);

                ActivityLogger.logEvent(user.getUserID(), LoginService.LOGIN_WEB_ACTIVITY_ACTION,
                    "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from "
                        + request.getRemoteAddr() + " (cookie)");

                log.info("Proceeding with request after successful Remember Me login");
                return true;
            } catch (AuthenticationThrottleException e) {
                log.error("AuthenticationThrottleException: " + e.getThrottleSeconds(), e);
            } catch (BadAuthenticationException e) {
                log.info("Remember Me login failed");
            } catch (PasswordExpiredException e) {
                log.error("The password for " + userPass.getUsername() + " is expired.");
            }
            // Cookie login failed, remove cookies.
            ServletUtil.deleteAllCookies(request, response);
        }
        return false;
    }

    private boolean doDefaultParameterLogin(HttpServletRequest request) throws ServletRequestBindingException {
        String username = ServletRequestUtils.getStringParameter(request, LoginController.USERNAME);
        String password = ServletRequestUtils.getStringParameter(request, LoginController.PASSWORD);
        if (username == null || password == null) {
            return false;
        }

        try {
            loginService.login(request, username, password);

            log.info("Proceeding with request after successful Param login");
            return true;
        } catch (AuthenticationThrottleException e) {
            log.error("AuthenticationThrottleException: " + e.getThrottleSeconds(), e);
        } catch (BadAuthenticationException e) {
            log.error(e);
        } catch (PasswordExpiredException e) {
            log.debug("The password for " + username + " is expired.");
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext applicationContext =
            WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        userContextResolver = applicationContext.getBean(YukonUserContextResolver.class);
        loginCookieHelper = applicationContext.getBean(LoginCookieHelper.class);
        urlAccessChecker = applicationContext.getBean(UrlAccessChecker.class);
        rolePropertyDao = applicationContext.getBean(RolePropertyDao.class);
        loginService = applicationContext.getBean(LoginService.class);
    }

    @Override
    public void destroy() {
        // Nothing to tear down for this filter.
    }
}

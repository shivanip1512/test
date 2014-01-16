package com.cannontech.web.login;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.NotLoggedInException;
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
    private final Logger log = YukonLogManager.getLogger(getClass());
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private WebApplicationContext context;
    private YukonUserContextResolver userContextResolver;
    private List<LoginRequestHandler> loginRequestHandlers;
    private UrlAccessChecker urlAccessChecker;
    private RolePropertyDao rolePropertyDao;
    private LoginService loginService;

    // Setup ant-style paths that should be processed even if the user is not logged in.
    // All paths should start with a slash because that's just the way it works.
    private final static ImmutableList<String> excludedFilePaths =
        ImmutableList.of(LoginController.LOGIN_URL,
                         "/remoteLogin",
                         "/integrationLogin",
                         "/login/forgotPassword",
                         "/login/forgottenPassword",
                         "/login/changePassword",
                         "/login/checkPassword",
                         "/servlet/LoginController",
                         "/soap/**",
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
                         "/common/images/*",
                         "/favicon.ico");

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
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // Support accented chars in request parameters.
        req.setCharacterEncoding("UTF-8");

        boolean isAjaxRequest = ServletUtil.isAjaxRequest(req);
        boolean excludedRequest = ServletUtil.isExcludedRequest(request, excludedFilePaths);

        // For excluded requests, try to attach the userContext, but they may not be logged in.
        if (excludedRequest) {
            log.debug("Proceeding with request that passes exclusion filter");
            attachYukonUserContext(request, false);
            chain.doFilter(request, response);
            return;
        }

        boolean loggedIn = isLoggedIn(request);

        if (!loggedIn) {
            // Try to log them in using a LoginRequestHandler to handle the login request.
            // Send error response if we fail to log them in.
            boolean success = false;
            for (LoginRequestHandler handler : loginRequestHandlers) {
                success = handler.handleLoginRequest(request, response);
                if (success) {
                    log.debug("Proceeding with request after successful handler login");
                    break;
                }
            }

            if (!success) {
                // If we got here, they couldn't be authenticated, send an error response.
                log.debug("All login attempts failed, returning error");

                doLoginRedirect(isAjaxRequest, request, response);
                return;
            }
        }

        // At this point the user has been authenticated, check for timeout and authorization.
        LiteYukonUser user = attachYukonUserContext(request, true).getYukonUser();

        // Check session timeout, update last activity time if successful, redirect to login page if expired.
        try {
            checkSessionTimeout(request, isAjaxRequest);
        } catch (SessionTimeoutException e) {
            log.info("User " + user.getUsername() + " logged out due to inactivity");
            loginService.invalidateSession(request, "TIMEOUT");
            doLoginRedirect(isAjaxRequest, request, response);
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

        chain.doFilter(req, resp);
    }

    private void checkSessionTimeout(HttpServletRequest request, boolean ajaxRequest) {
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
            } else {
                // Update the last activity time to now for non ajax requests.
                if (!ajaxRequest) {
                    sessionInfo.setLastActivityTime(new Instant());
                }
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
        boolean noRedirect = ServletUtil.isExcludedRequest(request, excludedRedirectedPaths);

        if (isAjaxRequest || noRedirect) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Authenticated!");
        } else {
            String url = request.getRequestURL().toString();
            String urlParams = request.getQueryString();
            String unencodedNavUrl = url + ((urlParams != null) ? "?" + urlParams : "");

            // strip any unsafe navigation from the URL before it gets encoded.
            String safeNavUrl = ServletUtil.createSafeRedirectUrl(request, unencodedNavUrl);
            String encodedNavUrl = ServletUtil.urlEncode(safeNavUrl);

            String redirectURL = LoginController.LOGIN_URL;
            if (!StringUtils.isBlank(encodedNavUrl)) {
                redirectURL += "?" + LoginController.REDIRECTED_FROM + "=" + encodedNavUrl;
            }
            response.sendRedirect(redirectURL);
        }
    }

    private boolean isLoggedIn(ServletRequest request) {
        try {
            ServletUtil.getYukonUser(request);
            return true;
        } catch (NotLoggedInException e) {
            return false;
        }
    }

    private YukonUserContext attachYukonUserContext(HttpServletRequest request, boolean isFailureAnError) {
        try {
            YukonUserContext context = userContextResolver.resolveContext(request);
            request.setAttribute(YukonUserContextUtils.userContextAttrName, context);
            return context;
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

    @Override
    @SuppressWarnings("unchecked")
    public void init(FilterConfig filterConfig) throws ServletException {
        context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        userContextResolver = context.getBean("userContextResolver", YukonUserContextResolver.class);
        loginRequestHandlers = context.getBean("loginRequestHandlers", List.class);
        urlAccessChecker = context.getBean("urlAccessChecker", UrlAccessChecker.class);
        rolePropertyDao = context.getBean("rolePropertyDao", RolePropertyDao.class);
        loginService = context.getBean("loginService", LoginService.class);
    }

    @Override
    public void destroy() {
    }
}

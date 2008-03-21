package com.cannontech.web.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.YukonUserContextResolver;

public class LoginFilter implements Filter {
    private Logger log = YukonLogManager.getLogger(LoginFilter.class);
    private static final String[] excludedFilePaths;
    private static final String[] excludedRedirectedPaths;
    private WebApplicationContext context;
    private LoginService loginService;
    private LoginCookieHelper loginCookieHelper;
    private YukonUserContextResolver userContextResolver;
    private PathMatcher pathMatcher = new AntPathMatcher();
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    static {
        // setup ant-style paths that should be processed even if the 
        // user is not logged in
        // all paths should start with a slash because that's just the way it works
        excludedFilePaths = new String[] {
            LoginController.LOGIN_URL, // aka /login.jsp
            "/servlet/LoginController", 
            "/servlet/LoggingServlet", 
            "/voice/login.jsp", 
            "/voice/inboundLogin.jsp", 
            "/soap/**", 
            "/servlet/PWordRequest",
            "/pwordreq.jsp", 
            "/**/prototype.js", 
            "/**/CtiMenu.js",
            "/**/*.css", 
            "/**/*.png", 
            "/**/*.gif", 
            "/**/*.jpg", 
            "/**/*.html",
            "/jws/*.jar"};
        
        excludedRedirectedPaths = new String[] {
            "/capcontrol/**",
            "/operator/**",
            "/editor/**",
            "/user/**",
        };
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;
        
        // try to attach the user to the request
        try {
            YukonUserContext resolveContext = userContextResolver.resolveContext(request);
            request.setAttribute(YukonUserContextUtils.userContextAttrName, resolveContext);
        } catch(RuntimeException e) {
            request.setAttribute(YukonUserContextUtils.userContextAttrName, e);
            log.debug("Unable to attach YukonUserContext to request", e);
        }

        boolean loggedIn = isLoggedIn(request);
        if (loggedIn) {
            chain.doFilter(req, resp);
            return;
        }

        boolean excludedRequest = isExcludedRequest(request, excludedFilePaths);
        if (excludedRequest) {
            log.debug("Proceeding with request that passes exclusion filter");
            chain.doFilter(req, resp);
            return;
        }

        // first, try to use the remember me cookie
        Cookie rememberMeCookie = ServletUtil.getCookie(request, LoginController.REMEMBER_ME_COOKIE);
        if (rememberMeCookie != null) {
            try {
                String encryptedValue = rememberMeCookie.getValue();
                UserPasswordHolder holder = loginCookieHelper.decodeCookieValue(encryptedValue);

                boolean success = loginService.login(request, holder.getUsername(), holder.getPassword());
                if (success) {
                    log.info("Proceeding with request after successful Remember Me login");
                    chain.doFilter(req, resp);
                    return;
                }
                log.info("Remember Me login failed");

                //cookie login failed, remove cookie and go on to second try.
                ServletUtil.deleteAllCookies(request, response);
            } catch (GeneralSecurityException e) {
                log.error("Unable to decrypt cookie value", e);
                ServletUtil.deleteAllCookies(request, response);
            }
        }

        // second try the username/password parameters
        String username = ServletRequestUtils.getStringParameter(request, LoginController.USERNAME);
        String password = ServletRequestUtils.getStringParameter(request, LoginController.PASSWORD);
        if (username != null && password != null) {
            boolean success = loginService.login(request, username, password);
            if (success) {
                log.debug("Proceeding with request after sucessful request parameter login");
                chain.doFilter(req, resp);
                return;
            }
        }
        
        log.debug("All login attempts failed, returning error");

        // okay, if we got here, they weren't authenticated
        boolean ajaxRequest = ServletUtil.isAjaxRequest(req);
        if (ajaxRequest) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Authenticated!");
        } else {
            sendLoginRedirect(request, response);
        }
        return;
    }

    private boolean isExcludedRequest(HttpServletRequest request, String... patterns) {
        String pathWithinApplication = urlPathHelper.getPathWithinApplication(request);
        
        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, pathWithinApplication)) {
                return true;
            }
        }

        return false;
    }

    private String getRedirectedFrom(HttpServletRequest request) {
        boolean isExcludedRedirectedFromRequest = isExcludedRequest(request, excludedRedirectedPaths);
        if (isExcludedRedirectedFromRequest) return "";
        
        String url = request.getRequestURL().toString();
        String urlParams = request.getQueryString();
        String navUrl = url + ((urlParams != null) ? "?" + urlParams : "");
        try {
            String encodedNavUrl = URLEncoder.encode(navUrl, "UTF-8");
            return encodedNavUrl;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendLoginRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String navUrl = getRedirectedFrom(request);
        String redirectURL = LoginController.LOGIN_URL + "?" + LoginController.REDIRECTED_FROM + "=" + navUrl;
        response.sendRedirect(redirectURL);
    }

    private boolean isLoggedIn(ServletRequest request) {
        try {
            ServletUtil.getYukonUser(request);
            return true;
        } catch (NotLoggedInException e) {
            return false;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        loginService = (LoginService) context.getBean("loginService", LoginService.class);
        loginCookieHelper = (LoginCookieHelper) context.getBean("loginCookieHelper", LoginCookieHelper.class);
        userContextResolver = (YukonUserContextResolver) context.getBean("userContextResolver", YukonUserContextResolver.class);
    }

    @Override
    public void destroy() {
    }

}

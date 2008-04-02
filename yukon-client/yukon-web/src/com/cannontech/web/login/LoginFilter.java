package com.cannontech.web.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
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
    private YukonUserContextResolver userContextResolver;
    private List<LoginRequestHandler> loginRequestHandlers;
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
            "/jws/*.jar"
        };
        
        excludedRedirectedPaths = new String[] {
            "/capcontrol/**",
            "/operator/**",
            "/editor/**",
            "/user/**"
        };
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;
        
        boolean loggedIn = isLoggedIn(request);
        if (loggedIn) {
            attachYukonUserContext(request);
            chain.doFilter(req, resp);
            return;
        }

        boolean excludedRequest = isExcludedRequest(request, excludedFilePaths);
        if (excludedRequest) {
            log.debug("Proceeding with request that passes exclusion filter");
            attachYukonUserContext(request); //try to attach, but may not be logged in.
            chain.doFilter(req, resp);
            return;
        }

        // try to use a LoginRequestHandler to handle the login request.
        for (final LoginRequestHandler handler : loginRequestHandlers) {
            boolean success = handler.handleLoginRequest(request, response);
            if (!success) continue;

            log.debug("Proceeding with request after successful handler login");
            attachYukonUserContext(request);
            // FilterChain.doFilter() cannot be in a try/catch, it would break the ErrorHelperFilter
            chain.doFilter(req, resp);
            return;
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
        // strip any unsafe navigation from the URL before it gets encoded.
        String safeNavUrl = ServletUtil.createSafeRedirectUrl(request, navUrl);
        try {
            String encodedNavUrl = URLEncoder.encode(safeNavUrl, "UTF-8");
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
    
    private void attachYukonUserContext(HttpServletRequest request) {
        try {
            YukonUserContext resolveContext = userContextResolver.resolveContext(request);
            request.setAttribute(YukonUserContextUtils.userContextAttrName, resolveContext);
        } catch(RuntimeException e) {
            request.setAttribute(YukonUserContextUtils.userContextAttrName, e);
            log.debug("Unable to attach YukonUserContext to request", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(FilterConfig filterConfig) throws ServletException {
        context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        userContextResolver = (YukonUserContextResolver) context.getBean("userContextResolver", YukonUserContextResolver.class);
        loginRequestHandlers = (List<LoginRequestHandler>) context.getBean("loginRequestHandlers", List.class);
    }

    @Override
    public void destroy() {
    }

}

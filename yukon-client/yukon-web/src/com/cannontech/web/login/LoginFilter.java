package com.cannontech.web.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.util.ServletUtil;

public class LoginFilter implements Filter {
    private static final String[] exclusionServletNames;
    private static final String[] exclusionFileNames;
    private static final String[] exclusionFileTypes;
    private WebApplicationContext context;
    private LoginService loginService;
    
    static {
        exclusionServletNames = new String[]{"/servlet/LoginController"};
        exclusionFileNames = new String[]{LoginController.LOGIN_URL, "setup.jsp", "prototype.js", "CtiMenu.js"};
        exclusionFileTypes = new String[]{".css", ".png", ".gif", ".jpg"};
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;

        boolean loggedIn = isLoggedIn(request);
        if (loggedIn) {
            chain.doFilter(req, resp);
            return;
        }
        
        boolean excludedRequest = isExcludedRequest(request);
        if (excludedRequest) {
            chain.doFilter(req, resp);
            return;
        }

        try {
            Cookie rememberMeCookie = ServletUtil.getCookie(request, LoginController.REMEMBER_ME_COOKIE); 
            if (rememberMeCookie == null) {
                
                boolean ajaxRequest = ServletUtil.isAjaxRequest(req);
                if (ajaxRequest) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Authenticated!");
                    return;
                }
                
                String username = ServletRequestUtils.getStringParameter(request, LoginController.USERNAME);
                String password = ServletRequestUtils.getStringParameter(request, LoginController.PASSWORD);
                if (username != null && password != null) {
                    boolean success = loginService.login(request, response, username, password, false);
                    if (success) {
                        chain.doFilter(req, resp);
                    }
                    return;
                }
                
                sendLoginRedirect(request, response);
                return;
            }

            boolean success = loginService.login(request, response, rememberMeCookie);
            if (success) {
                chain.doFilter(req, resp);
            }
        } catch (Exception e) {
            CTILogger.warn(e);
            sendLoginRedirect(request, response);
        } 
    }

    private boolean isExcludedRequest(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        boolean matched = requestUrl.matches("^.+\\.\\w{2,3}$");
        
        if (matched) {
            for (final String fileName : exclusionFileNames) {
                if (requestUrl.endsWith(fileName)) return true;
            }
            
            for (final String fileType : exclusionFileTypes) {
                if (requestUrl.endsWith(fileType)) return true;
            }    
            return false;
        }

        for (final String servletName : exclusionServletNames) {
            if (requestUrl.endsWith(servletName)) return true;
        }

        return false;
    }
    
    private String getRedirectedFrom(HttpServletRequest request) {
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
    }

    @Override
    public void destroy() {
    }
    
}

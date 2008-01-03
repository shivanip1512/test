package com.cannontech.servlet.filter;

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

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;

/**
 * Filter that keeps track of referrer pages and sets referrer variables in the
 * session for "Back" button functionality.
 * @author jdayton
 */
public class ReferrerPageFilter implements Filter {

    private static final String[] excludedPages;
    private PathMatcher pathMatcher = new AntPathMatcher();
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    // Save the filter config for getting at servlet context
    private FilterConfig config;
    
    static {
        // setup ant-style paths
        // all paths should start with a slash because that's just the way it works
        excludedPages = new String[] {
            "/capcontrol/cbcPointTimestamps.jsp", // aka /login.jsp
            "/capcontrol/tempmove.jsp",
            "/spring/capcontrol/capAddInfo"};
    }
    /**
     * @see javax.servlet.Filter#init(FilterConfig)
     */
    public void init(FilterConfig fc) throws ServletException {
        config = fc;
    }
    private boolean isExcludedRequest( HttpServletRequest req ) {

        String pathWithinApplication = urlPathHelper.getPathWithinApplication(req);
        
        for (String pattern : excludedPages) {
            if (pathMatcher.match(pattern, pathWithinApplication)) {
                return true;
            }
        }
        
        if( ServletUtil.isAjaxRequest( req ) )
            return true;

        return false;
    }
    /**
     * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse,
     *      FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        boolean excludedRequest = isExcludedRequest(request);
        if (excludedRequest) {
            chain.doFilter(req, resp);
            return;
        }
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            CtiNavObject navigator = (CtiNavObject) session.getAttribute(ServletUtil.NAVIGATE);
            if (navigator == null)
                navigator = new CtiNavObject();

            String url = request.getRequestURL().toString();
            String urlParams = request.getQueryString();
            String navUrl = url + ((urlParams != null) ? "?" + urlParams : "");
            navigator.setNavigation(navUrl);

            session.setAttribute(ServletUtil.NAVIGATE, navigator);
        }

        chain.doFilter(req, resp);
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        config = null;
    }

}

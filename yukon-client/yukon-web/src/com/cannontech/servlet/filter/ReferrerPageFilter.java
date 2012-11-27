package com.cannontech.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;
import com.google.common.collect.ImmutableList;

/**
 * Filter that keeps track of referrer pages and sets referrer variables in the
 * session for "Back" button functionality.
 * @author jdayton
 */
public class ReferrerPageFilter implements Filter {

    // Save the filter config for getting at servlet context
    private FilterConfig config;

    // setup ant-style paths
    // all paths should start with a slash because that's just the way it works
    private final static ImmutableList<String> excludedPages =
        ImmutableList.of("/capcontrol/ivvc/zone/chart",
                         "/capcontrol/ivvc/bus/chart",
                         "/capcontrol/cbcPointTimestamps.jsp",
                         "/capcontrol/standardPageWrapper.jsp",
                         "/capcontrol/move/bankMove",
                         "/capcontrol/capAddInfo",
                         "/capcontrol/oneline/popupmenu/*");
    
    public void init(FilterConfig fc) throws ServletException {
        config = fc;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        boolean excludedRequest = ServletUtil.isExcludedRequest(request, excludedPages);
        boolean isAjaxRequest = ServletUtil.isAjaxRequest( req );
        if (excludedRequest || isAjaxRequest) {
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

    public void destroy() {
        config = null;
    }
}

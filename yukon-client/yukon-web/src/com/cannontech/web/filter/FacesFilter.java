package com.cannontech.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class FacesFilter implements Filter {

    private final static Logger log = YukonLogManager.getLogger(FacesFilter.class);
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private final static ImmutableList<String> excludedFilePaths =
        ImmutableList.of(
            "/**/org.apache.myfaces.renderkit.html.util.MyFacesResourceLoader/*/prototype.PrototypeResourceLoader/*",
            "/**/org.apache.myfaces.renderkit.html.util.MyFacesResourceLoader/*/calendar.HtmlCalendarRenderer/popcalendar.js");
    private final static String capcontrolFacesPath = "/editor/cbcBase.jsf*";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        // Block any undesirable resources
        boolean excludedRequest = ServletUtil.isPathMatch(request, excludedFilePaths);
        if (excludedRequest) {
            log.debug("Filtering out unneeded resource: " + urlPathHelper.getPathWithinApplication(request));
            return;
        }


        // Add navigation bean to session for capcontrol jsf if not present
        if (ServletUtil.isPathMatch(request, Lists.newArrayList(capcontrolFacesPath))) {
            HttpSession session = request.getSession();
            CtiNavObject nav = (CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE);
            if (nav == null) {
                nav = new CtiNavObject();
                session.setAttribute(ServletUtils.NAVIGATE, nav);
            }
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        // Nothing to tear down for this filter.
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Nothing to init for this filter.
    }
}

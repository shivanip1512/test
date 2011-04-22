package com.cannontech.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.ImmutableList;

public class FacesFilter implements Filter {
    private final Logger log = YukonLogManager.getLogger(getClass());
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private final static ImmutableList<String> excludedFilePaths = 
        ImmutableList.of("/**/org.apache.myfaces.renderkit.html.util.MyFacesResourceLoader/*/prototype.PrototypeResourceLoader/*");

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        boolean excludedRequest = ServletUtil.isExcludedRequest(request, excludedFilePaths);
        if (excludedRequest) {
            log.debug("Filtering out unneeded resource: " + urlPathHelper.getPathWithinApplication(request));
            return;
        }

        chain.doFilter(req, resp);
    }

    public void destroy() {
    }

    public void init(FilterConfig arg0) throws ServletException {
    }
}

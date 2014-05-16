package com.cannontech.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter for all JSP that ensure no caching
 */
public class NoCacheFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // This simple filter has no state.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setDateHeader("Expires", 0);

        chain.doFilter(request, resp);
    }

    @Override
    public void destroy() {
        // This simple filter has no state.
    }
}

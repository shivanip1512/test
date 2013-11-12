package com.cannontech.servlet.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Filter to protect against click jacking via iframes
 */
@WebFilter(description = "Defeats iframe clickjack attacks", urlPatterns = { "/ClickjackFilter" })
public class ClickjackFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
    throws IOException, ServletException {
        
        // Allow iframes from the current site only
        HttpServletResponse res = (HttpServletResponse)response;
        res.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");

        // pass the request along the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig arg0) throws ServletException {}

}
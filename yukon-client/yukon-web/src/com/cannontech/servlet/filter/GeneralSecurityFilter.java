package com.cannontech.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filters against click jacking via iframes, and Mimesniffing attacks
 * 
 */
public class GeneralSecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse)response;
        res.addHeader("X-Frame-Options", "SAMEORIGIN");     // click jacking prevention
        res.addHeader("X-Content-Type-Options", "nosniff"); // avoid mime sniffing

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig arg0) throws ServletException {}

}
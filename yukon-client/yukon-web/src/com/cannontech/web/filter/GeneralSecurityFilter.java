package com.cannontech.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.web.security.csrf.impl.AuthenticationRequestWrapper;

/**
 * Filters against click jacking via iframes, and Mimesniffing attacks
 */
public class GeneralSecurityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        AuthenticationRequestWrapper request = new AuthenticationRequestWrapper((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("X-Frame-Options", "SAMEORIGIN"); // click jacking prevention
        response.addHeader("X-Content-Type-Options", "nosniff"); // avoid mime sniffing

        chain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {
        // Unused but must implement.
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Unused but must implement.
    }
}

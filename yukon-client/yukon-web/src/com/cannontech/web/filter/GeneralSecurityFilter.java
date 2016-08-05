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
        response.setHeader("X-XSS-Protection", "1; mode=block"); // avoid XSS attack
        response.setHeader(
            "Content-Security-Policy",
                "default-src 'self' http://java.sun.com http://cannontech.com http://docs.spring.io " 
                        + "https://getbootstrap.com http://www.springframework.org http://api.tiles.mapbox.com/ http://*.tiles.mapbox.com/; " // Trusted sites for source
                + "script-src 'self' https://api.tiles.mapbox.com 'unsafe-inline' 'unsafe-eval' http://www.google-analytics.com; "
                + "connect-src 'self' https://api.mapbox.com https://*.tiles.mapbox.com; " 
                + "img-src 'self' data: http://www.google-analytics.com Access-Control-Allow-Origin: *;" // Access-Control-Allow-Origin: * used for cross origin resource sharing for map images
                + "style-src 'self' 'unsafe-inline' https://api.tiles.mapbox.com;"
                + "plugin-types 'none'"
                + "media-src 'none'"
                + "child-src 'self'"
                + "font-src 'self'"); // header blocks popups, plugins and script execution from unknown source 
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubdomains");//HTTP Strict Transport Security 1 year as max-age
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

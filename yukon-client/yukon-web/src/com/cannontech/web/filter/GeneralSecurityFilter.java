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
            "Content-Security-Policy", // header blocks popups, plugins and script execution from unknown source
            ContentSecurityPolicyFilterType.DEFAULT_SRC.getValue()
                + ContentSecurityPolicyFilterType.SCRIPT_SRC.getValue()
                + ContentSecurityPolicyFilterType.CONNECT_SRC.getValue()
                + ContentSecurityPolicyFilterType.IMG_SRC.getValue()
                + ContentSecurityPolicyFilterType.STYLE_SRC.getValue()
                + ContentSecurityPolicyFilterType.MEDIA_SRC.getValue()
                + ContentSecurityPolicyFilterType.CHILD_SRC.getValue()
                + ContentSecurityPolicyFilterType.WORKER_SRC.getValue()
                + ContentSecurityPolicyFilterType.OBJECT_SRC.getValue()
                + ContentSecurityPolicyFilterType.FONT_SRC.getValue()
                + ContentSecurityPolicyFilterType.FRAME_SRC.getValue()
                + ContentSecurityPolicyFilterType.FRAME_ANCESTORS.getValue()
                + ContentSecurityPolicyFilterType.FORM_ACTION.getValue()
                );
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

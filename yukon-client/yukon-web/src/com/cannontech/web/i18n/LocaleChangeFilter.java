package com.cannontech.web.i18n;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.LocaleUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.clientutils.CTILogger;

public class LocaleChangeFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String newLocale = request.getParameter("locale");
        if (newLocale != null) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(req);
            if (localeResolver == null) {
                CTILogger.debug("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            Locale value = LocaleUtils.toLocale(newLocale);
            localeResolver.setLocale(req, resp, value);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}

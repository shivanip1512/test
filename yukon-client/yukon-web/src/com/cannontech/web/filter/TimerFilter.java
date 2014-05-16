package com.cannontech.web.filter;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.ImmutableSet;

/**
 * Filter that times how long a request takes.
 */
public class TimerFilter implements Filter {
    private final static Logger log = YukonLogManager.getLogger(TimerFilter.class);
    private final static Logger ajaxLog = YukonLogManager.getLogger(TimerFilter.class.getName() + ".ajax");
    private final static Logger staticLog = YukonLogManager.getLogger(TimerFilter.class.getName() + ".static");

    private final static Set<String> staticRequestExtensions =
            ImmutableSet.of("png", "jpg", "jpeg", "gif", "css", "js");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        long before = System.currentTimeMillis();
        chain.doFilter(servletRequest, response);
        long after = System.currentTimeMillis();

        String name = "";
        if (servletRequest instanceof HttpServletRequest) {
            name = ((HttpServletRequest) servletRequest).getRequestURI();
        }
        if (ServletUtil.isAjaxRequest(servletRequest)) {
            ajaxLog.debug(name + ": " + (after - before) + "ms");
        } else if (isStaticPage(name, response.getContentType())) {
            staticLog.debug(name + ": " + (after - before) + "ms");
        } else {
            log.info(name + ": " + (after - before) + "ms");
        }
    }

    /**
     * Calling these "static" pages is a bit of a lie but close enough for logging.  (Images, CSS, etc. can be
     * generated dynamically too.)
     */
    private static boolean isStaticPage(String name, String contentType) {
        String extension = null;
        int lastPeriod = name.lastIndexOf('.');
        if (lastPeriod != -1 && lastPeriod < name.length() - 1) {
            extension = name.substring(lastPeriod + 1).toLowerCase(Locale.US);
        }
        if (log.isTraceEnabled()) {
            log.trace("isStaticPage(" + name + ", " + contentType + "); extension = " + extension);
        }
        return staticRequestExtensions.contains(extension)
                || contentType != null && (contentType.startsWith("image/") || contentType.equals("text/css"));
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

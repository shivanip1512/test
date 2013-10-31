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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.util.ServletUtil;

/**
 * Filter that times how long a request takes.
 */
public class TimerFilter implements Filter {
    private Logger log = YukonLogManager.getLogger(TimerFilter.class);

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
            log.debug(name + ": " + (after - before) + "ms");
        } else {
            log.info(name + ": " + (after - before) + "ms");
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}

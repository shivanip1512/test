package com.cannontech.esub.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class SVGFilter implements Filter {

    private FilterConfig config;

    /**
     * @see javax.servlet.Filter#init(FilterConfig)
     */
    @Override
    public void init(FilterConfig fc) throws ServletException {
        config = fc;
    }

    /**
     * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse,
     *      FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hreq = (HttpServletRequest) req;

        String uri = hreq.getRequestURI();
        String conPath = hreq.getContextPath();

        if (!(uri.endsWith(".svg") || (uri.endsWith(".svgz")))) {
            chain.doFilter(req, resp);
            return;
        }
        String jsPath = uri.replaceFirst(conPath, "");
        if (jsPath.startsWith("/oneline/")) {

            chain.doFilter(req, resp);
            return;
        }

        else if (jsPath.startsWith("/esub/")){
            jsPath = StringUtils.replace(jsPath, "/esub/", "/esub/svgGenerator/");
            config.getServletContext()
                  .getRequestDispatcher(jsPath).
                  forward(req, resp);

        }

    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        config = null;
    }

}

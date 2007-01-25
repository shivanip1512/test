package com.cannontech.esub.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SVGFilter implements Filter {

    private FilterConfig config;

    /**
     * @see javax.servlet.Filter#init(FilterConfig)
     */
    public void init(FilterConfig fc) throws ServletException {
        config = fc;
    }

    /**
     * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse,
     *      FilterChain)
     */
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
        if (jsPath.startsWith("/capcontrol/oneline/")) {

            chain.doFilter(req, resp);
            return;
        }

        else if (jsPath.startsWith("/esub/")){
            String esubSVGGenerator = "/esub/svgGenerator/";
            config.getServletContext()
                  .getRequestDispatcher(esubSVGGenerator)
                  .forward(req, resp);

        }

    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        config = null;
    }

}

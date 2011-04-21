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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.YukonLogManager;

public class FacesFilter implements Filter {
    private Logger log = YukonLogManager.getLogger(FacesFilter.class);
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private static final String[] excludedFilePaths;

    static {
        /* All paths should start with a slash because that's just the way it works. */
        /*
         * Keeping this as an array in case we need to filter any other faces
         * resources in the future
         */
        excludedFilePaths =
            new String[] {
            // filter that stops MyFaces from injecting its own version of prototype (1.4.0)
            "/**/org.apache.myfaces.renderkit.html.util.MyFacesResourceLoader/*/prototype.PrototypeResourceLoader/*"
        };
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        boolean excludedRequest = isExcludedRequest(request, excludedFilePaths);
        if (excludedRequest) {
            return;
        }

        chain.doFilter(req, resp);
    }

    private boolean isExcludedRequest(HttpServletRequest request, String... patterns) {
        String pathWithinApplication = urlPathHelper.getPathWithinApplication(request);

        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, pathWithinApplication)) {
                log.info("Filtering out unneeded resource: " + pathWithinApplication);
                return true;
            }
        }

        return false;
    }

    public void destroy() {
    }

    public void init(FilterConfig arg0) throws ServletException {
    }
}

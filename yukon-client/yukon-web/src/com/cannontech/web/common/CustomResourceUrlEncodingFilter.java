package com.cannontech.web.common;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import com.cannontech.clientutils.YukonLogManager;
import org.apache.commons.lang3.StringUtils;

/**
 * This class is custom filter which is same as ResourceUrlEncodingFilter.java of Spring MVC 5.1.5
 * except one piece of code i.e. initLookupPath() is removed from setAttribute() method as it is not
 * able to handle empty request URI and this filter wraps the {@link HttpServletResponse} and overrides
 * its {@link HttpServletResponse#encodeURL(String) encodeURL} method in order to translate internal
 * resource request URLs into public URL paths for external use
 */
public class CustomResourceUrlEncodingFilter extends GenericFilterBean {
    private static final Logger log = YukonLogManager.getLogger(CustomResourceUrlEncodingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("CustomResourceUrlEncodingFilter only supports HTTP requests");
        }
        ResourceUrlEncodingRequestWrapper wrappedRequest =
            new ResourceUrlEncodingRequestWrapper((HttpServletRequest) request);
        ResourceUrlEncodingResponseWrapper wrappedResponse =
            new ResourceUrlEncodingResponseWrapper(wrappedRequest, (HttpServletResponse) response);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
    }

    private static class ResourceUrlEncodingRequestWrapper extends HttpServletRequestWrapper {

        @Nullable private ResourceUrlProvider resourceUrlProvider;

        @Nullable private Integer indexLookupPath;

        private String prefixLookupPath = StringUtils.EMPTY;

        ResourceUrlEncodingRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public void setAttribute(String name, Object value) {
            super.setAttribute(name, value);
            // Here I have removed initLookupPath() method as it is not able to handle empty request URI
        }

        @Nullable
        public String resolveUrlPath(String url) {
            if (this.resourceUrlProvider == null) {
                log.trace("ResourceUrlProvider not available via request attribute "
                    + "ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR");
                return null;
            }
            if (this.indexLookupPath != null && url.startsWith(this.prefixLookupPath)) {
                int suffixIndex = getEndPathIndex(url);
                String suffix = url.substring(suffixIndex);
                String lookupPath = url.substring(this.indexLookupPath, suffixIndex);
                lookupPath = this.resourceUrlProvider.getForLookupPath(lookupPath);
                if (lookupPath != null) {
                    return this.prefixLookupPath + lookupPath + suffix;
                }
            }
            return null;
        }

        private int getEndPathIndex(String path) {
            int end = path.indexOf('?');
            int fragmentIndex = path.indexOf('#');
            if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
                end = fragmentIndex;
            }
            if (end == -1) {
                end = path.length();
            }
            return end;
        }
    }

    private static class ResourceUrlEncodingResponseWrapper extends HttpServletResponseWrapper {

        private final ResourceUrlEncodingRequestWrapper request;

        ResourceUrlEncodingResponseWrapper(ResourceUrlEncodingRequestWrapper request, HttpServletResponse wrapped) {
            super(wrapped);
            this.request = request;
        }

        @Override
        public String encodeURL(String url) {
            String urlPath = this.request.resolveUrlPath(url);
            if (urlPath != null) {
                return super.encodeURL(urlPath);
            }
            return super.encodeURL(url);
        }
    }

}

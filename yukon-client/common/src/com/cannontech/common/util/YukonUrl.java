package com.cannontech.common.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * A simple, immutable class for building URLs from several segments, such as a base URL + endpoint + query parameters.
 * Attempts to prevent some common issues with String concatenation, and validates the URL as it is constructed.
 * To get the URL string, call .build() or .toString()
 */
public class YukonUrl {
    private static UrlValidator urlValidator;
    
    static {
        String[] schemes = {"http", "https"};
        urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
    }
    
    private final String url;
    
    /**
     * Static factory method.
     */
    public static YukonUrl of(String base) {
        return new YukonUrl(base);
    }
    
    public YukonUrl(String url) {
        this(url, true);
    }
    
    /**
     * Private constructor that can skip the usual validation. Only intended to be called for multi-step construction,
     * where validation will be called once at the end of all operations.
     */
    private YukonUrl(String url, boolean validate) {
        if (validate && !urlValidator.isValid(url)) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
        this.url = url;
    }
    
    /**
     * Appends segments to the end of the URL. Handles leading and trailing slashes to ensure only one slash between
     * appended segments.<p>
     * 
     * For example,
     *   <br> 
     *   new YukonUrl("http://localhost/").append("/api").build() == "http://localhost/api/".
     *   <br>
     *   new YukonUrl("http://localhost").append("api").build()    == "http://localhost/api".
     *   <br>
     *   new YukonUrl("http://localhost").append("api", "dr").build()    == "http://localhost/api/dr".
     * @param segment
     * @return A YukonUrl with the segment appended.
     */
    public YukonUrl append(String... segments) {
        YukonUrl newUrl = this;
        for (String segment : segments) {
            newUrl = newUrl.appendSegmentWithoutValidation(segment);
        }
        newUrl.validate();
        return newUrl;
    }
    
    /**
     * Appends a segment without validating the resulting URL. This is intended to allow multiple calls, with validation
     * only being done at the end.
     */
    private YukonUrl appendSegmentWithoutValidation(String segment) {
        if (url.endsWith("/")) {
            if (segment.startsWith("/")) {
                // Trailing slash + leading slash. Remove one.
                segment = segment.substring(1);
            }
        } else {
            if (!segment.startsWith("/")) {
                // No trailing or leading slash. Add one.
                segment = "/" + segment;
            }
        }
        String appendedUrl = url + segment;
        return new YukonUrl(appendedUrl, false);
    }
    
    private void validate() {
        if (!urlValidator.isValid(url)) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }
    
    /**
     * Appends a parameter name and value to the end of the URL. Handles all the necessary punctuation.<p>
     * 
     * For example,
     *   <br> 
     *   new YukonUrl("http://localhost").appendParam("id", 30).build() == "http://localhost?id=30".
     *   <br>
     *   new YukonUrl("http://localhost").appendParam("id", 30).appendParam("color", Color.RED).build() == "http://localhost?id=30&color=RED".
     * @return A YukonUrl with the parameter name and value appended.
     */
    public YukonUrl appendParam(String name, Object value) {
        return appendParamInternal(name, value, true);
    }
    
    /**
     * Appends multiple parameter name/value pairs to the end of the URL. Handles all the necessary punctuation.
     * @return A YukonUrl with the parameters appended.
     */
    public YukonUrl appendParams(Map<String, Object> params) {
        YukonUrl url = this;
        for (var param : params.entrySet()) {
            url = url.appendParamInternal(param.getKey(), param.getValue(), false);
        }
        url.validate();
        return url;
    }
    
    /**
     * Internal private method for appending parameters to the URL. In cases where multiple parameters are appended at
     * once, this can be called with validate == false. The caller must then call .validate() after all append calls
     * are complete.
     */
    private YukonUrl appendParamInternal(String name, Object value, boolean validate) {
        if (url.contains("?")) {
            return new YukonUrl(url + "&" + name + "=" + value, validate);
        }
        return new YukonUrl(url + "?" + name + "=" + value, validate);
    }
    
    public String build() {
        return url;
    }
    
    public URL buildUrl() throws MalformedURLException {
        return new URL(url);
    }
    
    public URI buildUri() throws URISyntaxException {
        return new URI(url);
    }
    
    @Override
    public String toString() {
        return url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        YukonUrl other = (YukonUrl) obj;
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }
    
}

package com.cannontech.web.dr.setup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.web.api.validation.ApiControllerHelper;

public class ApiControllerHelperTest {
    private HttpServletRequest req;
    private ApiControllerHelper helper;

    @Before
    public void setUp() throws Exception {
        req = createMockRequest();
        helper = new ApiControllerHelper();
    }

    @After
    public void tearDown() {
        req = null;
    }

    @Test
    public void testgetValidApiURL() {
        String pathURL = "/setup/loadGroup/save";
        String url = helper.getApiURL(req, pathURL);
        assertTrue("The url is valid", url.equals("http://localhost:8080/api/dr/setup/loadGroup/save"));
    }

    @Test
    public void testgetInvalidApiURL() {
        String pathURL = "/setup/loadGroup/save";
        String url = helper.getApiURL(req, pathURL);
        assertFalse("The url is invalid", url.equals("http://localhost:8080/dr/setup/loadGroup/save"));
    }

    private HttpServletRequest createMockRequest() {
        return new HttpServletRequest() {

            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public String getContextPath() {
                return "";
            }

            @Override
            public Cookie[] getCookies() {
                return null;
            }

            @Override
            public long getDateHeader(String name) {
                return 0;
            }

            @Override
            public String getHeader(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                return null;
            }

            @Override
            public int getIntHeader(String name) {
                return 0;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return "/setup/loadGroup/save";
            }

            @Override
            public String getPathTranslated() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return "/dr/setup/loadGroup/save";
            }

            @Override
            public StringBuffer getRequestURL() {
                StringBuffer baseURL = new StringBuffer("http://localhost:8080/dr/setup/loadGroup/save");
                return baseURL;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public String getServletPath() {
                return "/dr";
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public HttpSession getSession(boolean create) {
                return null;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public String getParameter(String name) {
                return null;
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;
            }

            @Override
            public String getRealPath(String path) {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return null;
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public void removeAttribute(String name) {
                // mock
            }

            @Override
            public void setAttribute(String name, Object o) {
                // mock
            }

            @Override
            public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
                // mock
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public AsyncContext startAsync() {
                return null;
            }

            @Override
            public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }

            @Override
            public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
                return false;
            }

            @Override
            public void login(String username, String password) throws ServletException {
                // mock
            }

            @Override
            public void logout() throws ServletException {
                // mock
            }

            @Override
            public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
                return null;
            }

            @Override
            public Part getPart(String name) throws IOException, IllegalStateException, ServletException {
                return null;
            }

            @Override
            public long getContentLengthLong() {
                return 0;
            }

            @Override
            public String changeSessionId() {
                return null;
            }

            @Override
            public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
                return null;
            }
        };
    }
}

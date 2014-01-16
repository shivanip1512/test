package com.cannontech.common.config;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import com.cannontech.clientutils.YukonLogManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RemoteLoginSession {
    private final static Logger log = YukonLogManager.getLogger(RemoteLoginSession.class);

    private final String baseURL;
    private final String username;
    private final String password;

    private String jsessionId;
    // This is used to prevent reconnect logic from retrying when a login is no longer valid.
    private boolean loginHasFailedPreviously;
    private String errorMsg;

    private ObjectMapper jsonObjectMapper = new ObjectMapper();
    private TypeReference<Map<String, String>> stringStringMapType = new TypeReference<Map<String, String>>() {};

    public RemoteLoginSession(String baseURL, String username, String password) {
        this.baseURL = baseURL;
        this.username = username;
        this.password = password;
    }

    private HttpURLConnection openConnectionToLocation(String urlPath) throws IOException {
        log.debug("inputStreamFromUrl " + baseURL + urlPath);
        URL url = new URL(baseURL + urlPath);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Cookie", "JSESSIONID=" + jsessionId);
        return (HttpURLConnection) connection;
    }

    /**
     * This method returns an input stream for the given path.  It will return null if the stream is empty,
     * emptyIsOk is true and the servlet handling the specified URL sets the content length header properly.
     * 
     * This method implements reconnect logic by logging in again and trying once more if the first response
     * returns fails with {@link HttpServletResponse#SC_FORBIDDEN}.
     */
    public InputStream getInputStreamForUrl(String urlPath, boolean emptyIsOk) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = openConnectionToLocation(urlPath);
            return getInputStream(connection, emptyIsOk);
        } catch (IOException ioe) {
            if (connection != null && connection.getResponseCode() == HttpServletResponse.SC_FORBIDDEN) {
                // The session timed out.  Log in and try again.
                log.info("Web Start client session timed out, logging in again and retrying.");
                login();
                connection = openConnectionToLocation(urlPath);
                return getInputStream(connection, emptyIsOk);
            }
            throw ioe;
        }
    }

    /**
     * Get the input stream without the reconnect logic built-in to {@link #getInputStreamForUrl(String, boolean)}.
     * Used by {@link #getInputStreamForUrl(String, boolean)}.
     */
    private InputStream getInputStream(HttpURLConnection connection, boolean emptyIsOk) throws IOException {
        if (emptyIsOk) {
            long contentLength = connection.getContentLengthLong();
            if (contentLength < 0) {
                throw new RuntimeException("content length not set properly in controller");
            }
            if (contentLength == 0) {
                log.info("No custom deviceDefinition.xml file.");
                return null;
            }
        }
        InputStream inputStream = connection.getInputStream();
        return inputStream;
    }

    /**
     * Create a proxy which will use connect via HTTP to the server to execut the methods in the specified interface.
     * This proxy will reconnect when the session expires.
     */
    public <T> T getReconnectingInteceptorProxy(Class<T> proxyInterface, String urlPath) {
        HttpInvokerClientInterceptor interceptor = new HttpInvokerClientInterceptor();
        HttpInvokerRequestExecutor requestExecutor = new SimpleHttpInvokerRequestExecutor() {
            @Override
            public void prepareConnection(HttpURLConnection connection, int contentLength) throws IOException {
                super.prepareConnection(connection, contentLength);
                connection.setRequestProperty("Cookie", "JSESSIONID=" + jsessionId);
            }
        };
        interceptor.setHttpInvokerRequestExecutor(requestExecutor);
        interceptor.setServiceUrl(baseURL + urlPath);

        // Get the Spring proxy that uses HTTP to invoke the methods.
        final T springProxy = ProxyFactory.getProxy(proxyInterface, interceptor);

        // Then, proxy it ourselves to include reconnect logic.
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return method.invoke(springProxy, args);
                } catch (InvocationTargetException ite) {
                    if (ite.getCause() instanceof RemoteAccessException) {
                        login();
                        return method.invoke(springProxy, args);
                    }
                    throw ite;
                }
            }
        };

        Object obj = Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class[] { proxyInterface },
            invocationHandler);

        return proxyInterface.cast(obj);
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * This method post the given data to the requested URL and return a Map<String, String> from
     * response.
     * 
     * @param postData The data to post to the form. This is simply a String to String map so if
     *            multiple parameters of the same name are needed, this method will need to be updated.
     * @param sessionless true if the sending the session cookie should be skipped.  Useful for the login request.
     */
    private Map<String, String> readJsonFromUrl(String urlStr, Map<String, String> postData, boolean sessionless)
            throws IOException {
        log.debug("posting for JSON to " + urlStr);
        URL url = new URL(baseURL + urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (!sessionless) {
            conn.setRequestProperty("Cookie", "JSESSIONID=" + jsessionId);
        }
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        try (DataOutputStream writer = new DataOutputStream(conn.getOutputStream())) {
            boolean isFirst = true;
            for (Map.Entry<String, String> entry : postData.entrySet()) {
                if (!isFirst) {
                    writer.writeByte('&');
                }
                writer.writeBytes(entry.getKey());
                writer.writeByte('=');
                writer.writeBytes(URLEncoder.encode(entry.getValue(), "UTF-8"));
                isFirst = false;
            }
            writer.flush();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            return jsonObjectMapper.readValue(reader, stringStringMapType);
        }
    }

    /**
     * This method will attempt to create a new session.  If it succeeds the jsessionId will be set true is returned.
     * If it fails it will set the error message and false is returned.
     * 
     * This guy is synchronized to prevent multiple threads messing with it at the same time.  Technically, each
     * thread should synchronize on this object around its entire reconnect logic but the worst that will happen if
     * we don't do that is that a two sessions will be created one right after the other with the first begin
     * garbage collected.
     */
    public synchronized boolean login() {
        log.debug("Creating new session.");
        if (loginHasFailedPreviously) {
            // Once we fail, we never want to try again with this instance...they'll have to restart.
            // The initial login recreates this object so this won't break the user entering a bad password.
            // This can happen if they change their password while a client is running.  Ideally we would ask them
            // for their credentials again but this is inordinately difficult.  They'll just have to restart.
            throw new IllegalStateException("login credentials have failed before; not retrying");
        }
        boolean loggedIn = false;

        Map<String, String> postData = new HashMap<>();
        postData.put("username", username);
        postData.put("password", password);
        try {
            Map<String, String> json = readJsonFromUrl("/remoteLogin", postData, true);
            if (json.get("result").equals("success")) {
                jsessionId = json.get("jsessionId");
                loggedIn = true;
                log.debug("Succesfully retrieved jsessionId");
            } else if (json.get("result").equals("failure")) {
                errorMsg = json.get("errorMsg");
                log.debug("User failed to login: " + errorMsg);
            } else {
                log.error("User failed to login. Invalid connect result");
                errorMsg = "User failed to login";
            }
        } catch (IOException e) {
            log.error("User failed to login", e);
            errorMsg = "User failed to login";
        }

        if (!loggedIn) {
            loginHasFailedPreviously = true;
        }

        return loggedIn;
    }
}

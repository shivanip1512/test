package com.cannontech.common.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;

import com.cannontech.clientutils.YukonHttpInvokerRequestExecutor;
import com.cannontech.clientutils.YukonLogManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RemoteLoginSession {
    private final static Logger log = YukonLogManager.getLogger(RemoteLoginSession.class);

    private String jsessionId;
    private String host;
    private boolean isValid;
    private String errorMsg;
    private String username;
    private String password;
    private ObjectMapper jsonObjectMapper = new ObjectMapper();
    private TypeReference<Map<String, String>> stringStringMapType = new TypeReference<Map<String, String>>() {};

    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    public RemoteLoginSession(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;

        connect();
        if (isValid) {
            keepSessionAlive();
        }
    }

    public URLConnection openConnectionToLocation(String location) throws IOException {
        log.debug("inputStreamFromUrl " + host + location);
        URL url = new URL(host + location);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Cookie", "JSESSIONID=" + getJsessionId());
        return connection;
    }

    /**
     * This method creates an intercepter with the jsessionId to be set in a cookie.
     */
    public HttpInvokerClientInterceptor getClientInterceptor(String location) {
        HttpInvokerClientInterceptor interceptor = new HttpInvokerClientInterceptor();
        YukonHttpInvokerRequestExecutor requestExecutor = new YukonHttpInvokerRequestExecutor(jsessionId);
        interceptor.setHttpInvokerRequestExecutor(requestExecutor);
        interceptor.setServiceUrl(host + location);
        return interceptor;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getHost() {
        return host;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getJsessionId() {
        return jsessionId;
    }

    /**
     * This method is checking if the session is alive every 5 minutes if session is not found it
     * will attempt to call connect() to create a new session
     */
    private void keepSessionAlive() {
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = host + "/checkConnection";
                    Map<String, String> json = readJsonFromUrl(url, "", true);
                    if (json.get("result").equals("failure")) {
                        log.debug("Session timed out attempting to reconnect");
                        connect();
                    }
                } catch (IOException e) {
                    log.error("Failed to connect to " + host + "/checkConnection", e);
                }
            }

        }, 0, 5, TimeUnit.MINUTES);
        log.info("Scheduled a task to keep session alive.");
    }

    /**
     * This method will return a Map<String, String> from URL
     * 
     * @param addJsessionId - true if the Cookie with JSESSIONID should be set
     */
    private Map<String, String> readJsonFromUrl(String url, String urlParameters, boolean addJsessionId)
            throws IOException {
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            if (addJsessionId) {
                conn.setRequestProperty("Cookie", "JSESSIONID=" + getJsessionId());
            }
            conn.setDoOutput(true);
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(urlParameters);
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
            return jsonObjectMapper.readValue(reader, stringStringMapType);
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * This method will attempt to create a new session. If it succeeds the jsessionId will be set.
     * If it fails it will set the error message.
     */
    private void connect() {
        String url = host + "/remoteLogin";
        log.debug("Creating new session. Getting jsessionId from " + url);
        String urlParameters = "username=" + username + "&password=" + password + "&noLoginRedirect=true";
        try {
            Map<String, String> json = readJsonFromUrl(url, urlParameters, false);
            if (json.get("result").equals("success")) {
                jsessionId = json.get("jsessionId");
                isValid = true;
                log.debug("Succesfully retrieved jsessionId");
            } else if (json.get("result").equals("failure")) {
                errorMsg = json.get("errorMsg");
                log.debug("User failed to login: " + getErrorMsg());
            } else {
                log.error("User failed to login. Invalid connect result");
                errorMsg = "User failed to login";
            }
        } catch (IOException e) {
            log.error("User failed to login", e);
            errorMsg = "User failed to login";
        }
    }
}

package com.cannontech.common.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.sf.jsonOLD.JSONException;
import net.sf.jsonOLD.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;

import com.cannontech.clientutils.YukonHttpInvokerRequestExecutor;
import com.cannontech.clientutils.YukonLogManager;

public class RemoteLoginSession {

    private static final Logger log = YukonLogManager.getLogger(RemoteLoginSession.class);
    private String jsessionId;
    private String host;
    private boolean isValid;
    private String errorMsg;
    private String username;
    private String password;
    
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
 
    public RemoteLoginSession(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
        connect();
        if(isValid){
            keepSessionAlive();
        }
    }
    
    public InputStream getInputStreamFromUrl(String location) throws IOException{
        log.debug("inputStreamFromUrl " + host +location);
        URL url = new URL(host +location);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Cookie", "JSESSIONID="+ getJsessionId());
        return conn.getInputStream();  
    }
    
    /**
     * This method creates an intercepter with the jsessionId to be set in a cookie.
     */
    public HttpInvokerClientInterceptor getClientInterceptor(String location){
        HttpInvokerClientInterceptor interceptor = new HttpInvokerClientInterceptor();
        YukonHttpInvokerRequestExecutor requestExecutor = new YukonHttpInvokerRequestExecutor(jsessionId);
        interceptor.setHttpInvokerRequestExecutor(requestExecutor);
        interceptor.setServiceUrl(host + location);
        return interceptor;
    }
    
    public boolean isValid(){
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
     * This method is checking if the session is alive if session is not found it will attempt to call connect() to create a new session
     */
    private void keepSessionAlive() {
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = host+"/checkConnection";
                    JSONObject json = readJsonFromUrl(url, "", true);
                    if(json.get("result").equals("failure")){
                        log.debug("Session timed out attempting to reconnect");
                        connect();
                    }
                } catch (IOException e) {
                    log.error("Failed to connect to " + host + "/checkConnection", e);
                }
            }

        }, 0, 1, TimeUnit.MINUTES);
        log.info("Scheduled a task to keep session alive.");
    }
    
    private JSONObject readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return new JSONObject(sb.toString());
    }

    /**
     * This method will return a JSONObject from URL
     * 
     * @param addJsessionId - true if the Cookie with JSESSIONID should be set
     */
    private JSONObject readJsonFromUrl(String url, String urlParameters, boolean addJsessionId) throws IOException, JSONException {
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            if(addJsessionId){
                conn.setRequestProperty("Cookie", "JSESSIONID="+ getJsessionId());
            }
            conn.setDoOutput(true);
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(urlParameters);
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),
                                                         Charset.forName("UTF-8")));
            return readAll(reader);
        } finally {
            if(writer != null){
                writer.close();
            }
            if(reader != null){
                reader.close();    
            }
        }
    }
    
    /**
     * This method will attempt to create a new session. If it succeeds the jsessionId will be set. If it fails it will set the error message.
     */
    private void connect(){
        String url = host+"/remoteLogin";
        log.debug("Creating new session. Getting jsessionId from " + url);
        String urlParameters = "username=" + username + "&password=" + password+"&noLoginRedirect=true";
        try {
            JSONObject json = readJsonFromUrl(url, urlParameters, false);
            if(json.get("result").equals("success")){
                jsessionId = (String)json.get("jsessionId");
                isValid = true;
                log.debug("Succesfully retrieved jsessionId");
            }else if(json.get("result").equals("failure")){
                errorMsg = (String)json.get("errorMsg");
                log.debug("User failed to login: " + getErrorMsg());
            }else{
                log.error("User failed to login. Invalid connect result");
                errorMsg = "User failed to login";
            }
        } catch (JSONException | IOException e) {
            log.error("User failed to login", e);
            errorMsg = "User failed to login";
        }     
    }
}

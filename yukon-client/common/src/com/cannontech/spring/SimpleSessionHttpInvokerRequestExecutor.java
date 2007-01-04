package com.cannontech.spring;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

public class SimpleSessionHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {
    private String sessionId;

    @Override
    protected void prepareConnection(HttpURLConnection con, int contentLength) throws IOException {
        super.prepareConnection(con, contentLength);
        con.setRequestProperty("Cookie", sessionId);
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

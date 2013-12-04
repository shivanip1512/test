package com.cannontech.clientutils;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

public class YukonHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor{
    
    private String jsessionId;
    
    public YukonHttpInvokerRequestExecutor(String jsessionId){
        super();
        this.jsessionId = jsessionId;
    }

    @Override
    public void prepareConnection(HttpURLConnection connection, int contentLength) throws IOException {
        super.prepareConnection(connection, contentLength);
        connection.setRequestProperty("Cookie", "JSESSIONID="+ jsessionId);
    }
}

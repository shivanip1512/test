package com.cannontech.notif.voice;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;


public abstract class Dialer {

    String _phonePrefix = "";

    public String getPhonePrefix() {
        return _phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        _phonePrefix = phonePrefix;
    }

    public void makeCall(Call call) {
        try {
            int count = 1;
            
            dialCall(call);
            if (call.isRetry()) {
                Thread.sleep(3000);
                while (call.isReady()) {
                    CTILogger.info("Retrying call (try " + ++count + "): "
                                       + call);
       
                    dialCall(call);
                    Thread.sleep(3000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    abstract protected void dialCall(Call call);
    
    public static String generateQueryString(Map parameters) throws UnsupportedEncodingException {
        StringBuffer result = new StringBuffer("");
        for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
            Map.Entry pair = (Map.Entry) iter.next();
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(URLEncoder.encode(pair.getKey().toString(),"UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue().toString(),"UTF-8"));
        }
        return result.toString();
    }
    
    public abstract String toString();
}

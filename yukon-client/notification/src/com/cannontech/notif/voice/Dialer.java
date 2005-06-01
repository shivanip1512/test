package com.cannontech.notif.voice;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;


public abstract class Dialer {

    String _phonePrefix = "";
    private static final int RETRY_DELAY = 3000;

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
                Thread.sleep(RETRY_DELAY);
                while (call.isReady()) {       
                    dialCall(call);
                    Thread.sleep(RETRY_DELAY);
                }
            }
        } catch (InterruptedException e) {
            call.changeState(new com.cannontech.notif.voice.callstates.UnknownError(e));
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

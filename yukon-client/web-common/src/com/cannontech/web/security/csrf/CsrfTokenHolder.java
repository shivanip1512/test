package com.cannontech.web.security.csrf;

import java.util.Map;
import java.util.UUID;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.util.MaxEntryLinkedHashMap;

public class CsrfTokenHolder {
    private final Duration maxLife;
    
    private final Map<String, Instant> tokenStore;

    public static final String prefix = "yct-";
    
    public CsrfTokenHolder(int maxTokens, Duration maxLife) {
        this.maxLife = maxLife;
        
        tokenStore = new MaxEntryLinkedHashMap<String, Instant>(maxTokens, false);
    }
    
    public synchronized String getToken() {
        String token = prefix + UUID.randomUUID().toString();
        
        tokenStore.put(token, new Instant());
        return token;
    }
    
    public synchronized boolean validateToken(String token) {
        Instant creationTime = tokenStore.remove(token);
        if (creationTime == null) {
            return false;
        }
        
        boolean expired = creationTime.plus(maxLife).isBeforeNow();
        return !expired;
    }
}

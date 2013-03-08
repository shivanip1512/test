package com.cannontech.openadr;

import com.google.common.collect.ImmutableMap;

public enum OadrResponseCode {
    // SUCCESS - 2xx
    SUCCESS(200),
    
    // REQUESTOR ERROR - 4xx
    BAD_REQUEST(400),
    NOT_FOUND(404),
    NOT_ALLOWED(405),
    CONFLICT(409),
    
    // RESPONDER ERROR - 5xx
    RESPONDER_ERROR(500),
    RESPONDER_TIMEOUT(508),
    ;
    
    private final int responseCode;
    
    private final static ImmutableMap<Integer, OadrResponseCode> lookupByErrorCode;
    
    static {
        ImmutableMap.Builder<Integer, OadrResponseCode> idBuilder = ImmutableMap.builder();
        for (OadrResponseCode value : values()) {
            idBuilder.put(value.responseCode, value);
        }
        lookupByErrorCode = idBuilder.build();
    }
    
    private OadrResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    public int getResponseCode() {
        return responseCode;
    }
    
    public String codeString() {
        return Integer.toString(responseCode);
    }
    
    public static OadrResponseCode getForErrorCode(String errorCode) {
        try {
            int parseInt = Integer.parseInt(errorCode);
            return lookupByErrorCode.get(parseInt);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

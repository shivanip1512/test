package com.cannontech.dr.nest.model;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

public enum NestURL{

    CREATE_EVENT,
    GET_CUSTOMER_LIST,
    CANCEL_EVENT,
    STOP_EVENT,
    ENROLLMENT
    ;
    
    public static int CURRENT_VERSION = 3;
    private static Map<Integer, Map<NestURL, String>> versionMapping;

    static {
        ImmutableMap.Builder<Integer, Map<NestURL, String>> versionMappingBuilder = ImmutableMap.builder();
        versionMappingBuilder.put(new Integer(3), buildMappingForV3());
        //example of adding another version
        //versionMappingBuilder.put(new Integer(4), buildMappingForV4());
        versionMapping = versionMappingBuilder.build();
    }

    // NestURLs to url mapping for version 2
    private static ImmutableMap<NestURL, String> buildMappingForV3() {
        ImmutableMap.Builder<NestURL, String> builder = ImmutableMap.builder();
        builder.put(NestURL.CREATE_EVENT, "/v3/partners/{partnerId}/rushHourEvents/{eventType}");
        builder.put(NestURL.STOP_EVENT, "/v3/partners/{partnerId}/rushHourEvents/{eventId}:stop");
        builder.put(NestURL.CANCEL_EVENT, "/v3/partners/{partnerId}/rushHourEvents/{eventId}:cancel");
        builder.put(NestURL.ENROLLMENT, "/v3/partners/{partnerId}/customers");
        builder.put(NestURL.GET_CUSTOMER_LIST, "/v3/partners/{partnerId}/customers");
        return builder.build();
    }
    

    public String buildUrl(String globalUrl) {
        return buildUrl(CURRENT_VERSION, globalUrl);
    }
    
    public String buildUrl(int version, String globalUrl) {
        String url = versionMapping.get(version).get(this);
        return globalUrl + url;
    }
    
    // Returns all available versions
    public static Set<Integer> getAllVersions() {
        return versionMapping.keySet();
    }
}

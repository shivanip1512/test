package com.cannontech.services;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ServiceType {
    CLASS_NAME_TYPE("CLASS_NAME_TYPE"),
    CONTEXT_FILE_TYPE("CONTEXT_FILE_TYPE");

    private final String serviceTypeString;
    private final static Map<String, ServiceType> byServiceType;
    static {
        Builder<String, ServiceType> builder = ImmutableMap.builder();
        for (ServiceType serviceType : values()) {
            builder.put(serviceType.serviceTypeString, serviceType);
        }
        byServiceType = builder.build();
    }

    private ServiceType(String serviceTypeString) {
        this.serviceTypeString = serviceTypeString;
    }

    public static ServiceType valueOfServiceType(String serviceTypeString) {
        return byServiceType.get(serviceTypeString);
    }
}

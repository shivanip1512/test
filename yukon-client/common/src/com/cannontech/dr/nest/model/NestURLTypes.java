package com.cannontech.dr.nest.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

public enum NestURLTypes {

    CONTROL,
    CONTROL_CRITICAL,
    CONTROL_STANDARD,
    PENDING,
    EXISTING;

    private static Map<NestURLTypes, String> urlMappingV1;
    private static Map<NestURLTypes, String> urlMappingV2;
    private static Map<Integer, Map<NestURLTypes, String>> versionMapping;

    static {
        ImmutableMap.Builder<Integer, Map<NestURLTypes, String>> versionMappingBuilder = ImmutableMap.builder();

        buildMappingForV1();
        buildMappingForV2();
        versionMappingBuilder.put(new Integer(1), urlMappingV1);
        versionMappingBuilder.put(new Integer(2), urlMappingV2);

        versionMapping = versionMappingBuilder.build();
    }

    // TODO: URL's here are the ones which are available now. This can be upadated when new URL are available.
    // NestURLTypes to url mapping for version 1
    private static void buildMappingForV1() {
        ImmutableMap.Builder<NestURLTypes, String> urlMappingV1Builder = ImmutableMap.builder();
        urlMappingV1Builder.put(NestURLTypes.CONTROL, "/energy/v2/rush_hour_rewards/events/");
        urlMappingV1Builder.put(NestURLTypes.CONTROL_CRITICAL, "/energy/v2/rush_hour_rewards/events/critical");
        urlMappingV1Builder.put(NestURLTypes.CONTROL_STANDARD, "/energy/v2/rush_hour_rewards/events/standard");
        urlMappingV1Builder.put(NestURLTypes.PENDING, "/v1/users/pending");
        urlMappingV1Builder.put(NestURLTypes.EXISTING, "/v1/users/current");

        urlMappingV1 = urlMappingV1Builder.build();
    }

    // NestURLTypes to url mapping for version 2
    private static void buildMappingForV2() {
        ImmutableMap.Builder<NestURLTypes, String> urlMappingV2Builder = ImmutableMap.builder();
        urlMappingV2Builder.put(NestURLTypes.CONTROL_CRITICAL, "/energy/v2/rush_hour_rewards/events/critical");
        urlMappingV2Builder.put(NestURLTypes.CONTROL_STANDARD, "/energy/v2/rush_hour_rewards/events/standard");
        urlMappingV2Builder.put(NestURLTypes.PENDING, "/v1/users/pending");
        urlMappingV2Builder.put(NestURLTypes.EXISTING, "/v1/users/current");

        urlMappingV2 = urlMappingV2Builder.build();
    }

    // Returns the URL of latest version
    public String getUrl() {
        Integer latestVersion = getLatestVersion();
        return getUrlForVersion(latestVersion, this);
    }

    // Returns the URL of specified version
    public String getUrl(Integer version) {
        return getUrlForVersion(version, this);
    }

    // Returns latest version
    public static Integer getLatestVersion() {
        return Collections.max(versionMapping.keySet());
    }

    private String getUrlForVersion(Integer latestVersion, NestURLTypes type) {
        return versionMapping.get(latestVersion).get(type);
    }

    // Returns all available versions
    public static Set<Integer> getAllVersions() {
        return versionMapping.keySet();
    }
}

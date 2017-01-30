package com.cannontech.multispeak.client;

import com.google.common.collect.ImmutableSet;


/**
 * Enum for version and namespace
 */

public enum MultiSpeakVersion {

    V3("3.0", "http://www.multispeak.org/Version_3.0"), 
    V5("5.0", "http://www.multispeak.org/V5.0"),;

    String version;
    String namespace;
    private final static ImmutableSet<String> supportedMspVerions = ImmutableSet.of(V3.version, V5.version);

    private MultiSpeakVersion(String version, String namespace) {
        this.version = version;
        this.namespace = namespace;
    }

    public String getVersion() {
        return version;
    }

    public String getNamespace() {
        return namespace;
    }

    public static ImmutableSet<String> getSupportedMspVersions() {
        return supportedMspVerions;
    }

}
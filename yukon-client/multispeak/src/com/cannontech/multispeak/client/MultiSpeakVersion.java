package com.cannontech.multispeak.client;


/**
 * Enum for version and namespace
 */

public enum MultiSpeakVersion {

    V3("3.0", "http://www.multispeak.org/Version_3.0"), ;

    String version;
    String namespace;

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
}
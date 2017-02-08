package com.cannontech.common.rfn.model;

/**
 * Utility class representing a gateway firmware version number. This is a three-part version in the form "X.Y.Z".
 */
public final class GatewayFirmwareVersion implements Comparable<GatewayFirmwareVersion> {
    private final int major;
    private final int minor;
    private final int revision;
    
    public GatewayFirmwareVersion(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }
    
    @Override
    public int compareTo(GatewayFirmwareVersion other) {
        int comparison = Integer.compare(major, other.major);
        if (comparison == 0) {
            comparison = Integer.compare(minor, other.minor);
            if (comparison == 0) {
                comparison = Integer.compare(revision, other.revision);
            }
        }
        return comparison;
    }
    
    public static GatewayFirmwareVersion parse(String versionString) throws IllegalArgumentException {
        if (versionString == null) {
            throw new IllegalArgumentException("Null firmware version string.");
        }
        String[] parts = versionString.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid firmware version string: " + versionString);
        }
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int revision = Integer.parseInt(parts[2]);
            
            return new GatewayFirmwareVersion(major, minor, revision);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid firmware version string: " + versionString, e);
        }
    }
}

package com.cannontech.common.rfn.model;

import java.util.Arrays;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;

/**
 * Utility class representing a gateway firmware version number. This is a three-part version in the form "X.Y.Z".
 */
public final class GatewayFirmwareVersion implements Comparable<GatewayFirmwareVersion> {
    private static final Logger log = YukonLogManager.getLogger(GatewayFirmwareVersion.class);
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
        
        // Trim leading and trailing whitespace and split into parts
        versionString = versionString.trim();
        String[] parts = versionString.split("\\.");
        int partsLength = parts.length;
        
        // Check for valid format - either x.y or x.y.z
        // If it has more than 3 parts, but is correctly formatted, truncate to 3 parts
        // (QA test versions have 4 parts, but we want to ignore the 4th part)
        if (partsLength < 2) {
            throw new IllegalArgumentException("Invalid firmware version string: " + versionString);
        } else if (partsLength > 4) {
            parts = Arrays.copyOf(parts, 3);
            partsLength = 3;
            log.info("Truncated long firmware version string (" + versionString + ")");
        }
        
        // Attempt to assemble a new GatewayFirmwareVersion object from the parts
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            // If Firmware Version is in x.y format. Convert it to x.y.0
            int revision = partsLength == 2 ? 0 : Integer.parseInt(parts[2]);
            return new GatewayFirmwareVersion(major, minor, revision);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid firmware version string: " + versionString, e);
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + revision;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GatewayFirmwareVersion other = (GatewayFirmwareVersion) obj;
        if (major != other.major) {
            return false;
        }
        if (minor != other.minor) {
            return false;
        }
        if (revision != other.revision) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(major) + "." + Integer.toString(minor) + "." + Integer.toString(revision);
    }
}

package com.cannontech.common.rfn.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class GatewayFirmwareVersionParserTest {

    @Test
    public void test_firmwareVersion_majorMinorRevision_format() {
        GatewayFirmwareVersion actualVersion = GatewayFirmwareVersion.parse("6.3.2");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 2);
        assertEquals("Parsed version string incorrectly", expectedVersion, actualVersion);
    }

    @Test
    public void test_firmwareVersion_majorMinor_format() {
        GatewayFirmwareVersion actualVersion = GatewayFirmwareVersion.parse("6.3");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 0);
        assertEquals("Failed to parse version string without revision", expectedVersion, actualVersion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidFirmwareVersion_tooManyParts() {
        GatewayFirmwareVersion.parse("6.3.2.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidFirmwareVersion_majorOnly() {
        GatewayFirmwareVersion.parse("6");
    }
    
    @Test
    public void test_whitespaceTrimming() {
        GatewayFirmwareVersion actualVersionWithSpaces = GatewayFirmwareVersion.parse(" 6.3.0  ");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 0);
        assertEquals("Parsed version string with leading and trailing whitespace incorrectly", expectedVersion, actualVersionWithSpaces);
        
        GatewayFirmwareVersion actualVersionWithNewlines = GatewayFirmwareVersion.parse("\n6.3.0 \n\n");
        assertEquals("Parsed version string with leading and trailing newlines incorrectly", expectedVersion, actualVersionWithNewlines);
        
        GatewayFirmwareVersion actualVersionMajorMinorWithNewlines = GatewayFirmwareVersion.parse("\n6.3 \n\n");
        assertEquals("Parsed version string with leading and trailing newlines incorrectly", expectedVersion, actualVersionMajorMinorWithNewlines);
    }
}


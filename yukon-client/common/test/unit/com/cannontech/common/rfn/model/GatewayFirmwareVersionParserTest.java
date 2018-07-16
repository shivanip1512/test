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

    @Test
    public void test_firmwareVersion_moreThan3Parts() {
        GatewayFirmwareVersion actualVersion1 = GatewayFirmwareVersion.parse("6.3.2.1");
        GatewayFirmwareVersion actualVersion2 = GatewayFirmwareVersion.parse("6.3.2.9");
        GatewayFirmwareVersion actualVersion3 = GatewayFirmwareVersion.parse("6.3.2.0.0");
        GatewayFirmwareVersion actualVersion4 = GatewayFirmwareVersion.parse("6.3.2.somethingotherthandecimal ");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 2);
        assertEquals("Failed to truncate 4-part version string into 3 parts", expectedVersion, actualVersion1);
        assertEquals("Failed to truncate 4-part version string into 3 parts", expectedVersion, actualVersion2);
        assertEquals("Failed to truncate 5-part version string into 3 parts", expectedVersion, actualVersion3);
        assertEquals("Failed to truncate version string with non-numerics after the 3rd segment", expectedVersion, actualVersion4);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_invalidFirmwareVersion_alphaInFirstThreeSegments() {
        // Non-numerics in the first 3 segments will cause a parsing error
        GatewayFirmwareVersion.parse("6.3.2alpha");
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


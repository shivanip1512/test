package com.cannontech.common.rfn.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GatewayFirmwareVersionParserTest {

    @Test
    public void test_firmwareVersion_majorMinorRevision_format() {
        GatewayFirmwareVersion actualVersion = GatewayFirmwareVersion.parse("6.3.2");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 2);
        assertEquals(expectedVersion, actualVersion, "Parsed version string incorrectly");
    }

    @Test
    public void test_firmwareVersion_majorMinor_format() {
        GatewayFirmwareVersion actualVersion = GatewayFirmwareVersion.parse("6.3");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 0);
        assertEquals(expectedVersion, actualVersion, "Failed to parse version string without revision");
    }

    @Test
    public void test_firmwareVersion_moreThan3Parts() {
        GatewayFirmwareVersion actualVersion1 = GatewayFirmwareVersion.parse("6.3.2.1");
        GatewayFirmwareVersion actualVersion2 = GatewayFirmwareVersion.parse("6.3.2.9");
        GatewayFirmwareVersion actualVersion3 = GatewayFirmwareVersion.parse("6.3.2.0.0");
        GatewayFirmwareVersion actualVersion4 = GatewayFirmwareVersion.parse("6.3.2.somethingotherthandecimal ");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 2);
        assertEquals(expectedVersion, actualVersion1, "Failed to truncate 4-part version string into 3 parts");
        assertEquals(expectedVersion, actualVersion2, "Failed to truncate 4-part version string into 3 parts");
        assertEquals(expectedVersion, actualVersion3, "Failed to truncate 5-part version string into 3 parts");
        assertEquals(expectedVersion, actualVersion4, "Failed to truncate version string with non-numerics after the 3rd segment");
    }
    
    @Test
    public void test_invalidFirmwareVersion_alphaInFirstThreeSegments() {
        // Non-numerics in the first 3 segments will cause a parsing error
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GatewayFirmwareVersion.parse("6.3.2alpha");
        });
    }

    @Test
    public void test_invalidFirmwareVersion_majorOnly() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GatewayFirmwareVersion.parse("6");
        });
    }
    
    @Test
    public void test_whitespaceTrimming() {
        GatewayFirmwareVersion actualVersionWithSpaces = GatewayFirmwareVersion.parse(" 6.3.0  ");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 0);
        assertEquals(expectedVersion, actualVersionWithSpaces, "Parsed version string with leading and trailing whitespace incorrectly");
        
        GatewayFirmwareVersion actualVersionWithNewlines = GatewayFirmwareVersion.parse("\n6.3.0 \n\n");
        assertEquals(expectedVersion, actualVersionWithNewlines, "Parsed version string with leading and trailing newlines incorrectly");
        
        GatewayFirmwareVersion actualVersionMajorMinorWithNewlines = GatewayFirmwareVersion.parse("\n6.3 \n\n");
        assertEquals(expectedVersion, actualVersionMajorMinorWithNewlines, "Parsed version string with leading and trailing newlines incorrectly");
    }
}


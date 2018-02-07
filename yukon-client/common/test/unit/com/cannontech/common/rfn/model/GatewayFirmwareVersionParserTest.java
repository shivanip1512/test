package com.cannontech.common.rfn.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GatewayFirmwareVersionParserTest {

    @Test
    public void test_firmwareVersion_xyz_format() {
        GatewayFirmwareVersion gatewayFirmwareVersion = GatewayFirmwareVersion.parse("6.3.2");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 2);
        assertTrue(expectedVersion.compareTo(gatewayFirmwareVersion) == 0);
    }

    @Test
    public void test_firmwareVersion_xy_format() {
        GatewayFirmwareVersion gatewayFirmwareVersion = GatewayFirmwareVersion.parse("6.3");
        GatewayFirmwareVersion expectedVersion = new GatewayFirmwareVersion(6, 3, 0);
        assertTrue(expectedVersion.compareTo(gatewayFirmwareVersion) == 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidFirmwareVersion_1() {
        GatewayFirmwareVersion.parse("6.3.2.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidFirmwareVersion_2() {
        GatewayFirmwareVersion.parse("6");
    }
}


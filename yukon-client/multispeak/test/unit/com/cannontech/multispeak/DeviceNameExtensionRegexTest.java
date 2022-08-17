package com.cannontech.multispeak;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.multispeak.service.MultispeakMeterServiceBase;

public class DeviceNameExtensionRegexTest {
    private ArrayList<String> validDeviceNameExtList = new ArrayList<>();
    private ArrayList<String> inValidDeviceNameExtList = new ArrayList<>();
    String deviceNameExtRegex = MultispeakMeterServiceBase.DEVICE_NAME_EXT_REGEX;

    @Before
    public void setUp() {
        validDeviceNameExtList.add(" [CisSubstation01]");
        validDeviceNameExtList.add(" [CisSubstation02]");
        validDeviceNameExtList.add(" [CisSubstation03]");
        validDeviceNameExtList.add(" [CisSubstation04]");

        inValidDeviceNameExtList.add("[CisSubstation01]");
        inValidDeviceNameExtList.add(" [@CisSubstation02]");
        inValidDeviceNameExtList.add(" [CisSubstation03#]");
        inValidDeviceNameExtList.add("[(CisSubstation04)]");
        inValidDeviceNameExtList.add(" [CisSubstation04");
        inValidDeviceNameExtList.add(" CisSubstation04]");
        inValidDeviceNameExtList.add(" ");
    }

    @Test
    public void testValidDeviceNameExtension() {
        boolean test = false;
        for (String deviceName : validDeviceNameExtList) {
            test = Pattern.matches(deviceNameExtRegex, deviceName);
            assertTrue(test);
        }
    }

    @Test
    public void testInValidDeviceNameExtension() {
        boolean test = false;
        for (String deviceName : inValidDeviceNameExtList) {
            test = Pattern.matches(deviceNameExtRegex, deviceName);
            assertFalse(test);
        }
    }

    @After
    public void tearDown() {
        validDeviceNameExtList.clear();
        inValidDeviceNameExtList.clear();
    }
}

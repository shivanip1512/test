package com.cannontech.common.device.groups.util;

import static com.cannontech.common.device.groups.util.DeviceGroupUtil.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.cannontech.common.device.groups.IllegalGroupNameException;

/**
 * Test class for {@link DeviceGroupUtil}.
 */
public class DeviceGroupUtilTest {
    @Test
    public void testIsValidName() {
        assertTrue("isValidName(My Device Group)", isValidName("My Device Group"));
        assertFalse("isValidName(My Device Group)", isValidName("/group"));
        assertFalse("isValidName(My Device Group)", isValidName("group/other"));
        assertFalse("isValidName(My Device Group)", isValidName("group/"));
        assertFalse("isValidName(My Device Group)", isValidName("\\group"));
        assertFalse("isValidName(My Device Group)", isValidName("group\\other"));
        assertFalse("isValidName(My Device Group)", isValidName("group\\"));
    }

    private void testValidateNameSuccess(String name) {
        try {
            validateName(name);
        } catch (IllegalGroupNameException igne) {
            assertFalse("name " + name + " failed but should not have", true);
        }
    }

    private void testValidateNameError(String name) {
        try {
            validateName(name);
            assertFalse("name " + name + " did not fail but should have", true);
        } catch (IllegalGroupNameException igne) {
        }
    }

    @Test
    public void testValidateName() {
        testValidateNameSuccess("My Device Group");
        testValidateNameError("/group");
        testValidateNameError("group/other");
        testValidateNameError("group/");
        testValidateNameError("\\group");
        testValidateNameError("group\\other");
        testValidateNameError("group\\");
    }

    private void testRemoval(String input, String expectedValue) {
        assertTrue("", expectedValue.equals(removeInvalidDeviceGroupNameCharacters(input)));
    }

    @Test
    public void testRemoveInvalidDeviceGroupNameCharacters() {
        testRemoval("My Device Group", "My Device Group");
        testRemoval("/group", "_group");
        testRemoval("group/other", "group_other");
        testRemoval("group/", "group_");
        testRemoval("\\group", "_group");
        testRemoval("group\\other", "group_other");
        testRemoval("group\\", "group_");
    }
}

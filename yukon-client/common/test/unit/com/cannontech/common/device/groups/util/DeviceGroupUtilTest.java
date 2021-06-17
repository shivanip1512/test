package com.cannontech.common.device.groups.util;

import static com.cannontech.common.device.groups.util.DeviceGroupUtil.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.cannontech.common.device.groups.IllegalGroupNameException;

/**
 * Test class for {@link DeviceGroupUtil}.
 */
public class DeviceGroupUtilTest {
    @Test
    public void testIsValidName() {
        assertTrue(isValidName("My Device Group"), "isValidName(My Device Group)");
        assertFalse(isValidName("/group"), "isValidName(My Device Group)");
        assertFalse(isValidName("group/other"), "isValidName(My Device Group)");
        assertFalse(isValidName("group/"), "isValidName(My Device Group)");
        assertFalse(isValidName("\\group"), "isValidName(My Device Group)");
        assertFalse(isValidName("group\\other"), "isValidName(My Device Group)");
        assertFalse(isValidName("group\\"), "isValidName(My Device Group)");
    }

    private void testValidateNameSuccess(String name) {
        try {
            validateName(name);
        } catch (IllegalGroupNameException igne) {
            assertFalse(true, "name " + name + " failed but should not have");
        }
    }

    private void testValidateNameError(String name) {
        try {
            validateName(name);
            assertFalse(true, "name " + name + " did not fail but should have");
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
        assertTrue(expectedValue.equals(removeInvalidDeviceGroupNameCharacters(input)));
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

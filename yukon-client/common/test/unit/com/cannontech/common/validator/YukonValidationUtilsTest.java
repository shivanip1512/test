package com.cannontech.common.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;

public class YukonValidationUtilsTest {
    BindException error = null;

    @Test
    public void checkExceedsMaxLength_Test() {
        error = new BindException(Object.class, "testObject");
        assertFalse(YukonValidationUtils.checkExceedsMaxLength(error, "name", null, 3));
        assertFalse(YukonValidationUtils.checkExceedsMaxLength(error, "name", "", 3));
        assertFalse(YukonValidationUtils.checkExceedsMaxLength(error, "name", " ", 3));
        assertFalse(YukonValidationUtils.checkExceedsMaxLength(error, "name", "fun", 3));
        assertFalse(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkExceedsMaxLength(error, "name", "    ", 3));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkExceedsMaxLength(error, "name", "funny", 3));
        assertTrue(error.hasFieldErrors("name"));
    }

    @Test
    public void checkBlacklistedCharacter_Test() {
        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkBlacklistedCharacter(error, "name", "\""));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkBlacklistedCharacter(error, "name", "*"));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkBlacklistedCharacter(error, "name", ","));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertFalse(YukonValidationUtils.checkBlacklistedCharacter(error, "name", null));
        assertFalse(YukonValidationUtils.checkBlacklistedCharacter(error, "name", ""));
        assertFalse(YukonValidationUtils.checkBlacklistedCharacter(error, "name", "   "));
        assertFalse(YukonValidationUtils.checkBlacklistedCharacter(error, "name", "fun"));
        assertFalse(error.hasFieldErrors("name"));
    }

    @Test
    public void checkIllegalCharacter_Test() {
        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkIllegalCharacter(error, "name", "\""));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkIllegalCharacter(error, "name", ","));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertFalse(YukonValidationUtils.checkIllegalCharacter(error, "name", "*"));
        assertFalse(YukonValidationUtils.checkIllegalCharacter(error, "name", null));
        assertFalse(YukonValidationUtils.checkIllegalCharacter(error, "name", ""));
        assertFalse(YukonValidationUtils.checkIllegalCharacter(error, "name", "fun"));
        assertFalse(error.hasFieldErrors("name"));
    }

    @Test
    public void checkIsBlank_Test() {
        error = new BindException(Object.class, "testObject");
        assertFalse(YukonValidationUtils.checkIsBlank(error, "name", null, true));
        assertFalse(YukonValidationUtils.checkIsBlank(error, "name", "fun", true));
        assertFalse(YukonValidationUtils.checkIsBlank(error, "name", "fun", false));
        assertFalse(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkIsBlank(error, "name", null, false));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkIsBlank(error, "name", "", true));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkIsBlank(error, "name", "", false));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkIsBlank(error, "name", "   ", true));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkIsBlank(error, "name", "   ", false));
        assertTrue(error.hasFieldErrors("name"));
    }

    @Test
    public void checkIsValidDouble_Test() {
        error = new BindException(Object.class, "testObject");
        assertFalse(YukonValidationUtils.checkIsValidDouble(error, "name", null));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertFalse(YukonValidationUtils.checkIsValidDouble(error, "name", Double.NaN));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertFalse(YukonValidationUtils.checkIsValidDouble(error, "name", Double.POSITIVE_INFINITY));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertFalse(YukonValidationUtils.checkIsValidDouble(error, "name", Double.NEGATIVE_INFINITY));
        assertTrue(error.hasFieldErrors("name"));

        error = new BindException(Object.class, "testObject");
        assertTrue(YukonValidationUtils.checkIsValidDouble(error, "name", (double) 78));
        assertFalse(error.hasFieldErrors("name"));
    }

    @Test
    public void ipHostNameValidator_Test() {
        // TODO
        /*
         * BindException error = new BindException(Object.class, "testObject");
         * assertTrue(YukonValidationUtils.ipHostNameValidator(error, "ipAddress", "127.0.0.1"));
         * assertFalse("Invalid IP Address",YukonValidationUtils.ipHostNameValidator(error, "ipAddress",
         * "300.225.255.0"));
         */
    }

    @Test
    public void isUrlPath_Test() {
        assertFalse(YukonValidationUtils.isUrlPath(null));
        assertTrue(YukonValidationUtils.isUrlPath("/login.jsp"));
    }

    @Test
    public void isRfnSerialNumberValid_Test() {
        assertTrue(YukonValidationUtils.isRfnSerialNumberValid(null));
        assertTrue(YukonValidationUtils.isRfnSerialNumberValid("a10kl"));
        assertFalse(YukonValidationUtils.isRfnSerialNumberValid("Morethan30characterrrrrrrrrrrrrrrrrrrrrrrr"));
    }

    @Test
    public void isLatitudeInRange_Test() {
        assertFalse(YukonValidationUtils.isLatitudeInRange(null));
        assertFalse(YukonValidationUtils.isLatitudeInRange(Double.NaN));
        assertFalse(YukonValidationUtils.isLatitudeInRange((double) -95));
        assertFalse(YukonValidationUtils.isLatitudeInRange((double) 95));
        assertTrue(YukonValidationUtils.isLatitudeInRange((double) 70));
    }

    @Test
    public void isLongitudeInRange_Test() {
        assertFalse(YukonValidationUtils.isLongitudeInRange(null));
        assertFalse(YukonValidationUtils.isLongitudeInRange(Double.NaN));
        assertFalse(YukonValidationUtils.isLongitudeInRange((double) -187));
        assertFalse(YukonValidationUtils.isLongitudeInRange((double) 189));
        assertTrue(YukonValidationUtils.isLongitudeInRange((double) 177));
    }
}

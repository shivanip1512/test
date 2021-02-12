package com.cannontech.common.validator;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.BindException;

public class YukonValidationUtilsTest {
    BindException error = new BindException(Object.class, "testObject");

    @Test
    public void checkExceedsMaxLength_Test() {
        Assert.assertFalse(YukonValidationUtils.checkExceedsMaxLength(error, "name", null, 3));
        Assert.assertFalse(YukonValidationUtils.checkExceedsMaxLength(error, "name", "", 3));
        Assert.assertFalse(YukonValidationUtils.checkExceedsMaxLength(error, "name", " ", 3));
        Assert.assertTrue(YukonValidationUtils.checkExceedsMaxLength(error, "name", "    ", 3));
        Assert.assertFalse(YukonValidationUtils.checkExceedsMaxLength(error, "name", "fun", 3));
        Assert.assertTrue(YukonValidationUtils.checkExceedsMaxLength(error, "name", "funny", 3));
    }

    @Test
    public void checkBlacklistedCharacter_Test() {
        Assert.assertTrue(YukonValidationUtils.checkBlacklistedCharacter(error, "name", "\""));
        Assert.assertTrue(YukonValidationUtils.checkBlacklistedCharacter(error, "name", "*"));
        Assert.assertTrue(YukonValidationUtils.checkBlacklistedCharacter(error, "name", ","));

        Assert.assertFalse(YukonValidationUtils.checkBlacklistedCharacter(error, "name", null));
        Assert.assertFalse(YukonValidationUtils.checkBlacklistedCharacter(error, "name", ""));
        Assert.assertFalse(YukonValidationUtils.checkBlacklistedCharacter(error, "name", "   "));
        Assert.assertFalse(YukonValidationUtils.checkBlacklistedCharacter(error, "name", "fun"));
    }

    @Test
    public void checkIllegalCharacter_Test() {

        Assert.assertTrue(YukonValidationUtils.checkIllegalCharacter(error, "name", "\""));
        Assert.assertFalse(YukonValidationUtils.checkIllegalCharacter(error, "name", "*"));
        Assert.assertTrue(YukonValidationUtils.checkIllegalCharacter(error, "name", ","));

        Assert.assertFalse(YukonValidationUtils.checkIllegalCharacter(error, "name", null));
        Assert.assertFalse(YukonValidationUtils.checkIllegalCharacter(error, "name", ""));
        Assert.assertFalse(YukonValidationUtils.checkIllegalCharacter(error, "name", "fun"));
    }

    @Test
    public void checkIsBlank_Test() {
        Assert.assertFalse(YukonValidationUtils.checkIsBlank(error, "name", null, true));
        Assert.assertTrue(YukonValidationUtils.checkIsBlank(error, "name", null, false));

        Assert.assertTrue(YukonValidationUtils.checkIsBlank(error, "name", "", true));
        Assert.assertTrue(YukonValidationUtils.checkIsBlank(error, "name", "", false));

        Assert.assertTrue(YukonValidationUtils.checkIsBlank(error, "name", "   ", true));
        Assert.assertTrue(YukonValidationUtils.checkIsBlank(error, "name", "   ", false));

        Assert.assertFalse(YukonValidationUtils.checkIsBlank(error, "name", "fun", true));
        Assert.assertFalse(YukonValidationUtils.checkIsBlank(error, "name", "fun", false));
    }

    @Test
    public void checkIsValidDouble_Test() {
        Assert.assertFalse(YukonValidationUtils.checkIsValidDouble(error, "name", null));
        Assert.assertFalse(YukonValidationUtils.checkIsValidDouble(error, "name", Double.NaN));
        Assert.assertFalse(YukonValidationUtils.checkIsValidDouble(error, "name", Double.POSITIVE_INFINITY));
        Assert.assertFalse(YukonValidationUtils.checkIsValidDouble(error, "name", Double.NEGATIVE_INFINITY));
        Assert.assertTrue(YukonValidationUtils.checkIsValidDouble(error, "name", (double) 78));
    }

    @Test
    public void ipHostNameValidator_Test() {
        // TODO
        /*
         * BindException error = new BindException(Object.class, "testObject");
         * Assert.assertTrue(YukonValidationUtils.ipHostNameValidator(error, "ipAddress", "127.0.0.1"));
         * Assert.assertFalse("Invalid IP Address",YukonValidationUtils.ipHostNameValidator(error, "ipAddress",
         * "300.225.255.0"));
         */
    }

    @Test
    public void isUrlPath_Test() {
        Assert.assertFalse(YukonValidationUtils.isUrlPath(null));
        Assert.assertTrue(YukonValidationUtils.isUrlPath("/login.jsp"));
    }

    @Test
    public void isRfnSerialNumberValid_Test() {
        Assert.assertTrue(YukonValidationUtils.isRfnSerialNumberValid(null));
        Assert.assertTrue(YukonValidationUtils.isRfnSerialNumberValid("a10kl"));
        Assert.assertFalse(YukonValidationUtils.isRfnSerialNumberValid("Morethan30characterrrrrrrrrrrrrrrrrrrrrrrr"));
    }

    @Test
    public void isLatitudeInRange_Test() {
        Assert.assertFalse(YukonValidationUtils.isLatitudeInRange(null));
        Assert.assertFalse(YukonValidationUtils.isLatitudeInRange(Double.NaN));
        Assert.assertFalse(YukonValidationUtils.isLatitudeInRange((double) -95));
        Assert.assertFalse(YukonValidationUtils.isLatitudeInRange((double) 95));
        Assert.assertTrue(YukonValidationUtils.isLatitudeInRange((double) 70));
    }

    @Test
    public void isLongitudeInRange_Test() {
        Assert.assertFalse(YukonValidationUtils.isLongitudeInRange(null));
        Assert.assertFalse(YukonValidationUtils.isLongitudeInRange(Double.NaN));
        Assert.assertFalse(YukonValidationUtils.isLongitudeInRange((double) -187));
        Assert.assertFalse(YukonValidationUtils.isLongitudeInRange((double) 189));
        Assert.assertTrue(YukonValidationUtils.isLongitudeInRange((double) 177));
    }
}

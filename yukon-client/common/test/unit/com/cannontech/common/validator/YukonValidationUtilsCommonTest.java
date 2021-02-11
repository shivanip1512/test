package com.cannontech.common.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.cannontech.common.user.User;
import com.cannontech.common.util.Range;

public class YukonValidationUtilsCommonTest {

    @Test
    public void checkExceedsMaxLength_Test() {
        Assert.assertFalse(YukonValidationUtilsCommon.checkExceedsMaxLength(null, 3));
        Assert.assertFalse(YukonValidationUtilsCommon.checkExceedsMaxLength("", 3));
        Assert.assertFalse(YukonValidationUtilsCommon.checkExceedsMaxLength(" ", 3));
        Assert.assertTrue(YukonValidationUtilsCommon.checkExceedsMaxLength("    ", 3));
        Assert.assertFalse(YukonValidationUtilsCommon.checkExceedsMaxLength("fun", 3));
        Assert.assertTrue(YukonValidationUtilsCommon.checkExceedsMaxLength("funny", 3));
    }

    @Test
    public void checkBlacklistedCharacter_Test() {
        // TODO
        Assert.assertTrue(YukonValidationUtilsCommon.checkBlacklistedCharacter("\""));
        Assert.assertTrue(YukonValidationUtilsCommon.checkBlacklistedCharacter("*"));
        Assert.assertTrue(YukonValidationUtilsCommon.checkBlacklistedCharacter(","));

        Assert.assertFalse(YukonValidationUtilsCommon.checkBlacklistedCharacter(null));
        Assert.assertFalse(YukonValidationUtilsCommon.checkBlacklistedCharacter(""));
        Assert.assertFalse(YukonValidationUtilsCommon.checkBlacklistedCharacter("   "));
        Assert.assertFalse(YukonValidationUtilsCommon.checkBlacklistedCharacter("fun"));
    }

    @Test
    public void checkIllegalCharacter_Test() {

        Assert.assertTrue(YukonValidationUtilsCommon.checkIllegalCharacter("\""));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIllegalCharacter("*"));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIllegalCharacter(","));

        Assert.assertFalse(YukonValidationUtilsCommon.checkIllegalCharacter(null));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIllegalCharacter(""));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIllegalCharacter("fun"));
    }

    @Test
    public void checkIsBlank_Test() {
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsBlank(null, true));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsBlank(null, false));

        Assert.assertTrue(YukonValidationUtilsCommon.checkIsBlank("", true));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsBlank("", false));

        Assert.assertTrue(YukonValidationUtilsCommon.checkIsBlank("   ", true));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsBlank("   ", false));

        Assert.assertFalse(YukonValidationUtilsCommon.checkIsBlank("fun", true));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsBlank("fun", false));
    }

    @Test
    public void checkIsPositiveShort_Test() {
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsPositiveShort(null));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsPositiveShort(Short.MIN_VALUE));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsPositiveShort((short) 10));
    }

    @Test
    public void checkIsPositiveInt_Test() {
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsPositiveInt(null));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsPositiveInt(Integer.MIN_VALUE));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsPositiveInt(45));
    }

    @Test
    public void checkIsPositiveDouble_Test() {
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsPositiveDouble(Double.MIN_VALUE));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsPositiveDouble((double) 78));
    }

    @Test
    public void checkIsValidDouble_Test() {
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsValidDouble(null));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsValidDouble(Double.NaN));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsValidDouble(Double.POSITIVE_INFINITY));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsValidDouble(Double.NEGATIVE_INFINITY));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsValidDouble((double)78));
    }

    @Test
    public void checkIsNumberPositiveDouble_Test() {
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsNumberPositiveDouble(-56));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsNumberPositiveDouble(78));
    }

    @Test
    public void checkIsNumberPositiveInt_Test() {
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsNumberPositiveInt(Integer.MIN_VALUE));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsNumberPositiveInt(77));
    }

    @Test
    public void checkRangeMinMax_Test() {
        Assert.assertTrue(YukonValidationUtilsCommon.checkRange(10, 70, 105));
        Assert.assertTrue(YukonValidationUtilsCommon.checkRange(108, 70, 105));
        Assert.assertFalse(YukonValidationUtilsCommon.checkRange(80, 70, 105));
    }

    @Test
    public void checkRangeRange_Test() {
        Range<Integer> range = Range.inclusive(0, 1000);
        Assert.assertTrue(YukonValidationUtilsCommon.checkRange(null, range));
        Assert.assertTrue(YukonValidationUtilsCommon.checkRange(10000, range));
        Assert.assertFalse(YukonValidationUtilsCommon.checkRange(100, range));
    }

    @Test
    public void ipHostNameValidator_Test() {
        // TODO
        /*
         * BindException error = new BindException(Object.class, "testObject");
         * Assert.assertTrue(YukonValidationUtilsCommon.ipHostNameValidator(error, "ipAddress", "127.0.0.1"));
         * Assert.assertFalse("Invalid IP Address",YukonValidationUtilsCommon.ipHostNameValidator(error, "ipAddress",
         * "300.225.255.0"));
         */
    }

    @Test
    public void checkExactLength_Test() {
        Assert.assertTrue(YukonValidationUtilsCommon.checkExactLength(null, 0));
        Assert.assertTrue(YukonValidationUtilsCommon.checkExactLength("fun", 3));
        Assert.assertFalse(YukonValidationUtilsCommon.checkExactLength("fun", 4));
    }

    @Test
    public void checkIfListRequired_Test() {
        Assert.assertTrue(YukonValidationUtilsCommon.checkIfListRequired(null));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIfListRequired(new ArrayList<String>()));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIfListRequired(new ArrayList<String>(Arrays.asList("Yuk1", "Yuk2"))));
    }

    @Test
    public void checkIfFieldRequired_Test() {
        Assert.assertTrue(YukonValidationUtilsCommon.checkIfFieldRequired(null));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIfFieldRequired(50));
    }

    @Test
    public void checkIsFieldValueGreaterThenTargetValueInt_Test() {
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsFieldValueGreaterThenTargetValueInt(null, 50));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIsFieldValueGreaterThenTargetValueInt(25, 50));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIsFieldValueGreaterThenTargetValueInt(55, 50));
    }

    @Test
    public void checkIfEndDateGreaterThenStartDate_Test() {
        // TODO
        Instant now = Instant.now();
        Assert.assertFalse(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now, now, true));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now, now, false));
        
        Assert.assertFalse(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now.minus(1), now, true));
        Assert.assertFalse(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now.minus(1), now, false));
        
        Assert.assertTrue(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now, now.minus(1), true));
        Assert.assertTrue(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now, now.minus(1), false));
    }
}
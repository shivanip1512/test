package com.cannontech.common.validator;

import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;

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
        // TODO
        // YukonValidationUtilsCommon.checkIsPositiveShort(fieldValue);
    }

    @Test
    public void  checkIsPositiveInt_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkIsPositiveInt(fieldValue);
    }

    @Test
    public void checkIsPositiveDouble_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkIsPositiveDouble(fieldValue);
    }

    @Test
    public void checkIsValidDouble_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkIsValidDouble(fieldValue);
    }

    @Test
    public void checkIsNumberPositiveDouble_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkIsNumberPositiveDouble(fieldValue);
    }

    @Test
    public void checkIsNumberPositiveInt_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkIsNumberPositiveInt(fieldValue);
    }

    @Test
    public void checkRangeMinMax_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkRange(fieldValue, min, max);
    }

    @Test
    public void checkRangeRange_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkRange(fieldValue, range);
    }

    @Test
    public void ipHostNameValidator_Test() {
        // TODO
        // YukonValidationUtilsCommon.ipHostNameValidator(errors, field, fieldValue);
    }

    @Test
    public void checkExactLength_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkExactLength(fieldValue, stringLength);
    }

    @Test
    public void checkIfListRequired_Test() {
        // TODO
        //YukonValidationUtilsCommon.checkIfListRequired(fieldValue);
    }

    @Test
    public void checkIfFieldRequired_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkIfFieldRequired(fieldValue);
    }

    @Test
    public void checkIsFieldValueGreaterThenTargetValueInt_Test() {
        // TODO
        // YukonValidationUtilsCommon.checkIsFieldValueGreaterThenTargetValueInt(fieldValue, targetValue);
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
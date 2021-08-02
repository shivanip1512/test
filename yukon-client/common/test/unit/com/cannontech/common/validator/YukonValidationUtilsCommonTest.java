package com.cannontech.common.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import com.cannontech.common.util.Range;

public class YukonValidationUtilsCommonTest {

    @Test
    public void checkExceedsMaxLength_Test() {
        assertFalse(YukonValidationUtilsCommon.checkExceedsMaxLength(null, 3));
        assertFalse(YukonValidationUtilsCommon.checkExceedsMaxLength("", 3));
        assertFalse(YukonValidationUtilsCommon.checkExceedsMaxLength(" ", 3));
        assertTrue(YukonValidationUtilsCommon.checkExceedsMaxLength("    ", 3));
        assertFalse(YukonValidationUtilsCommon.checkExceedsMaxLength("fun", 3));
        assertTrue(YukonValidationUtilsCommon.checkExceedsMaxLength("funny", 3));
    }

    @Test
    public void checkBlacklistedCharacter_Test() {
        // TODO
        assertTrue(YukonValidationUtilsCommon.checkBlacklistedCharacter("\""));
        assertTrue(YukonValidationUtilsCommon.checkBlacklistedCharacter("*"));
        assertTrue(YukonValidationUtilsCommon.checkBlacklistedCharacter(","));

        assertFalse(YukonValidationUtilsCommon.checkBlacklistedCharacter(null));
        assertFalse(YukonValidationUtilsCommon.checkBlacklistedCharacter(""));
        assertFalse(YukonValidationUtilsCommon.checkBlacklistedCharacter("   "));
        assertFalse(YukonValidationUtilsCommon.checkBlacklistedCharacter("fun"));
    }

    @Test
    public void checkIllegalCharacter_Test() {

        assertTrue(YukonValidationUtilsCommon.checkIllegalCharacter("\""));
        assertFalse(YukonValidationUtilsCommon.checkIllegalCharacter("*"));
        assertTrue(YukonValidationUtilsCommon.checkIllegalCharacter(","));

        assertFalse(YukonValidationUtilsCommon.checkIllegalCharacter(null));
        assertFalse(YukonValidationUtilsCommon.checkIllegalCharacter(""));
        assertFalse(YukonValidationUtilsCommon.checkIllegalCharacter("fun"));
    }

    @Test
    public void checkIsBlank_Test() {
        assertFalse(YukonValidationUtilsCommon.checkIsBlank(null, true));
        assertTrue(YukonValidationUtilsCommon.checkIsBlank(null, false));

        assertTrue(YukonValidationUtilsCommon.checkIsBlank("", true));
        assertTrue(YukonValidationUtilsCommon.checkIsBlank("", false));

        assertTrue(YukonValidationUtilsCommon.checkIsBlank("   ", true));
        assertTrue(YukonValidationUtilsCommon.checkIsBlank("   ", false));

        assertFalse(YukonValidationUtilsCommon.checkIsBlank("fun", true));
        assertFalse(YukonValidationUtilsCommon.checkIsBlank("fun", false));
    }

    @Test
    public void checkIsPositiveShort_Test() {
        assertTrue(YukonValidationUtilsCommon.checkIsPositiveShort(null));
        assertTrue(YukonValidationUtilsCommon.checkIsPositiveShort(Short.MIN_VALUE));
        assertFalse(YukonValidationUtilsCommon.checkIsPositiveShort((short) 10));
    }

    @Test
    public void checkIsPositiveInt_Test() {
        assertTrue(YukonValidationUtilsCommon.checkIsPositiveInt(null));
        assertTrue(YukonValidationUtilsCommon.checkIsPositiveInt(Integer.MIN_VALUE));
        assertFalse(YukonValidationUtilsCommon.checkIsPositiveInt(45));
    }

    @Test
    public void checkIsPositiveDouble_Test() {
        assertFalse(YukonValidationUtilsCommon.checkIsPositiveDouble(Double.MIN_VALUE));
        assertFalse(YukonValidationUtilsCommon.checkIsPositiveDouble((double) 78));
    }

    @Test
    public void checkIsValidDouble_Test() {
        assertFalse(YukonValidationUtilsCommon.checkIsValidDouble(null));
        assertFalse(YukonValidationUtilsCommon.checkIsValidDouble(Double.NaN));
        assertFalse(YukonValidationUtilsCommon.checkIsValidDouble(Double.POSITIVE_INFINITY));
        assertFalse(YukonValidationUtilsCommon.checkIsValidDouble(Double.NEGATIVE_INFINITY));
        assertTrue(YukonValidationUtilsCommon.checkIsValidDouble((double)78));
    }

    @Test
    public void checkIsNumberPositiveDouble_Test() {
        assertFalse(YukonValidationUtilsCommon.checkIsNumberPositiveDouble(-56));
        assertTrue(YukonValidationUtilsCommon.checkIsNumberPositiveDouble(78));
    }

    @Test
    public void checkIsNumberPositiveInt_Test() {
        assertFalse(YukonValidationUtilsCommon.checkIsNumberPositiveInt(Integer.MIN_VALUE));
        assertTrue(YukonValidationUtilsCommon.checkIsNumberPositiveInt(77));
    }

    @Test
    public void checkRangeMinMax_Test() {
        assertTrue(YukonValidationUtilsCommon.checkRange(10, 70, 105));
        assertTrue(YukonValidationUtilsCommon.checkRange(108, 70, 105));
        assertFalse(YukonValidationUtilsCommon.checkRange(80, 70, 105));
    }

    @Test
    public void checkRangeRange_Test() {
        Range<Integer> range = Range.inclusive(0, 1000);
        assertTrue(YukonValidationUtilsCommon.checkRange(null, range));
        assertTrue(YukonValidationUtilsCommon.checkRange(10000, range));
        assertFalse(YukonValidationUtilsCommon.checkRange(100, range));
    }

    @Test
    public void ipHostNameValidator_Test() {
        // TODO
        /*
         * BindException error = new BindException(Object.class, "testObject");
         * assertTrue(YukonValidationUtilsCommon.ipHostNameValidator(error, "ipAddress", "127.0.0.1"));
         * assertFalse("Invalid IP Address",YukonValidationUtilsCommon.ipHostNameValidator(error, "ipAddress",
         * "300.225.255.0"));
         */
    }

    @Test
    public void checkExactLength_Test() {
        assertTrue(YukonValidationUtilsCommon.checkExactLength(null, 0));
        assertTrue(YukonValidationUtilsCommon.checkExactLength("fun", 3));
        assertFalse(YukonValidationUtilsCommon.checkExactLength("fun", 4));
    }

    @Test
    public void checkIfListRequired_Test() {
        assertTrue(YukonValidationUtilsCommon.checkIfListRequired(null));
        assertTrue(YukonValidationUtilsCommon.checkIfListRequired(new ArrayList<String>()));
        assertFalse(YukonValidationUtilsCommon.checkIfListRequired(new ArrayList<String>(Arrays.asList("Yuk1", "Yuk2"))));
    }

    @Test
    public void checkIfFieldRequired_Test() {
        assertTrue(YukonValidationUtilsCommon.checkIfFieldRequired(null));
        assertFalse(YukonValidationUtilsCommon.checkIfFieldRequired(50));
    }

    @Test
    public void checkIsFieldValueGreaterThenTargetValueInt_Test() {
        assertTrue(YukonValidationUtilsCommon.checkIsFieldValueGreaterThenTargetValueInt(null, 50));
        assertTrue(YukonValidationUtilsCommon.checkIsFieldValueGreaterThenTargetValueInt(25, 50));
        assertFalse(YukonValidationUtilsCommon.checkIsFieldValueGreaterThenTargetValueInt(55, 50));
    }

    @Test
    public void checkIfEndDateGreaterThenStartDate_Test() {
        // TODO
        Instant now = Instant.now();
        assertFalse(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now, now, true));
        assertTrue(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now, now, false));
        
        assertFalse(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now.minus(1), now, true));
        assertFalse(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now.minus(1), now, false));
        
        assertTrue(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now, now.minus(1), true));
        assertTrue(YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(now, now.minus(1), false));
    }
}
package com.cannontech.common.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public class YukonValidationUtilsTest {
    Errors error = null;

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

    AbstractBindingResult makeBindingResult(String field, String value) {
        return  new AbstractBindingResult("testObject") {
            @Override
            public Object getTarget() {
                return null;
            }
            @Override
            public Object getFieldValue(String arg0) {
                return getActualFieldValue(arg0);
            }
            @Override
            protected Object getActualFieldValue(String arg0) {
                return arg0.equals(field)
                        ? value
                        : null;
            }
        };
    }

    @Test
    public void ipHostNameValidator_Test() {
        final String IP_ADDRESS = "ipAddress";

        //  Failure cases
        Stream.of(
                null, 
                "    ",
                "\t    tabs vs spaces\n",
                // 100 chars
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                // 64 chars, longer than an individual domain segment
                "1234567890123456789012345678901234567890123456789012345678901234",
                // Same, but with alphanumeric and dashes
                "1234567890abcdefghijklmnopqrstuvwxyz-123456789012345678901234567",
                // Segment cannot start with a dash
                "-not-a-domain.com",
                // Segment cannot end with a dash
                "not-a-domain-.com",
                // Cannot contain underscores
                "not_a_domain.com",
                "not a domain",
                // @ is invalid 
                "joe@eaton.com"
                ).forEach(value -> { 
            error = makeBindingResult(IP_ADDRESS, value);
            YukonValidationUtils.ipHostNameValidator(error, IP_ADDRESS, value);
            assertTrue(error.hasFieldErrors(IP_ADDRESS), "Unexpectedly passed validation: " + value);
        });    

        // Success cases
        Stream.of(
                "127.0.0.1", 
                "blu-dr.eaton.com", 
                //  No range validation on entries that look like IPs
                "300.225.255.0",
                //  63 char domain is okay
                "123456789012345678901234567890123456789012345678901234567890123",
                "123456789-123456789-123456789-123456789-123456789-123456789-123",
                "123456789-123456789-123456789-123456789-123456789-123456789-123.com",
                "1234567890abcdefghijklmnopqrstuvwxyz-12345678901234567890123456",
                //  Repeating dashes are okay as long as they start and end with alphanumeric 
                "1-------------------------------------------------------------z",
                "0.a-hyphenated-domain.com",
                "a.a"
                ).forEach(value -> { 
            error = makeBindingResult(IP_ADDRESS, value);
            YukonValidationUtils.ipHostNameValidator(error, IP_ADDRESS, value);
            assertFalse(error.hasFieldErrors(IP_ADDRESS), error.toString());
        });    
    }
    
    @Test
    public void validatePort_Test() {
        final String PORT = "port";

        //  Failure cases
        Stream.of(
                null, 
                "    ", 
                "null", 
                "bananaphone", 
                "0", 
                "-17", 
                "65536"
                ).forEach(value -> { 
            error = makeBindingResult(PORT, value);
            YukonValidationUtils.validatePort(error, PORT, "TCP port", value);
            assertTrue(error.hasFieldErrors(PORT), error.toString());
        });    

        // Success cases
        Stream.of(
                "1", 
                "65535", 
                "32768"
                ).forEach(value -> { 
            error = makeBindingResult(PORT, value);
            YukonValidationUtils.validatePort(error, PORT, "TCP port", value);
            assertFalse(error.hasFieldErrors(PORT), error.toString());
        });    
    }

    @Test
    public void isUrlPath_Test() {
        assertFalse(YukonValidationUtils.isUrlPath(null));
        assertTrue(YukonValidationUtils.isUrlPath("/login.jsp"));
    }

    @Test
    public void isRfnSerialNumberValid_Test() {
        assertTrue(YukonValidationUtils.isRfnSerialNumberValid(null));
        assertTrue(YukonValidationUtils.isRfnSerialNumberValid(""));
        assertTrue(YukonValidationUtils.isRfnSerialNumberValid("a10kl"));
        assertFalse(YukonValidationUtils.isRfnSerialNumberValid("Morethan30characterrrrrrrrrrrrrrrrrrrrrrrr"));
        assertFalse(YukonValidationUtils.isRfnSerialNumberValid("1234asb%^&$#$%^"));
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

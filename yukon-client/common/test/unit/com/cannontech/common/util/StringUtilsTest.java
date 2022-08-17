package com.cannontech.common.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testElideCenter_even_even() {
        int maxSize = 4;
        String result = StringUtils.elideCenter("12345678", maxSize);
        assertEquals("12\u20268", result);
        assertTrue(result.length() <= maxSize);
    }

    @Test
    public void testElideCenter_even_odd() {
        int maxSize = 5;
        String result = StringUtils.elideCenter("12345678", maxSize);
        assertEquals("12\u202678", result);
        assertTrue(result.length() <= maxSize);
    }
    
    @Test
    public void testElideCenter_odd_even() {
        int maxSize = 4;
        String result = StringUtils.elideCenter("123456789", maxSize);
        assertEquals("12\u20269", result);
        assertTrue(result.length() <= maxSize);
    }
    
    @Test
    public void testElideCenter_odd_odd() {
        int maxSize = 5;
        String result = StringUtils.elideCenter("123456789", maxSize);
        assertEquals("12\u202689", result);
        assertTrue(result.length() <= maxSize);
    }
    
    @Test
    public void testElideCenter_small_1() {
        int maxSize = 1;
        String result = StringUtils.elideCenter("123456789", maxSize);
        assertEquals("\u2026", result);
        assertTrue(result.length() <= maxSize);
    }
    
    @Test
    public void testElideCenter_small_2() {
        int maxSize = 2;
        String result = StringUtils.elideCenter("123456789", maxSize);
        assertEquals("1\u2026", result);
        assertTrue(result.length() <= maxSize);
    }
    
    @Test
    public void testElideCenter_small_3() {
        int maxSize = 3;
        String result = StringUtils.elideCenter("123456789", maxSize);
        assertEquals("1\u20269", result);
        assertTrue(result.length() <= maxSize);
    }
    
    @Test
    public void testElideCenter_one_less() {
        int maxSize = 8;
        String result = StringUtils.elideCenter("123456789", maxSize);
        assertEquals("1234\u2026789", result);
        assertTrue(result.length() <= maxSize);
    }
    
    @Test
    public void testElideCenter_exact() {
        int maxSize = 9;
        String result = StringUtils.elideCenter("123456789", maxSize);
        assertEquals("123456789", result);
        assertTrue(result.length() <= maxSize);
    }
    
    @Test
    public void testElideCenter_one_more() {
        int maxSize = 10;
        String result = StringUtils.elideCenter("123456789", maxSize);
        assertEquals("123456789", result);
        assertTrue(result.length() <= maxSize);
    }

    @Test
    public void isValidString_with_no_repeat() {
        String testValue = "ADBC";
        String pattern = "ABCD";
        boolean result = StringUtils.isStringMatchesWithPattern(testValue, pattern, false);
        assertTrue(result);
    }

    @Test
    public void isValidString_with_no_repeat_2() {
        String testValue = "ABBC";
        String pattern = "ABCD";
        boolean result = StringUtils.isStringMatchesWithPattern(testValue, pattern, false);
        assertTrue(result == false);
    }

    @Test
    public void isValidString_with_no_repeat_invalid() {
        String testValue = "ABX";
        String pattern = "ABCD";
        boolean result = StringUtils.isStringMatchesWithPattern(testValue, pattern, false);
        assertTrue(result == false);
    }

    @Test
    public void isValidString_with_repeat() {
        String testValue = "ABBC";
        String pattern = "ABCD";
        boolean result = StringUtils.isStringMatchesWithPattern(testValue, pattern, true);
        assertTrue(result);
    }
    
    @Test
    public void isformatString_valid_string() {
        String testValue = "DAC";
        String pattern = "ABCD";
        String result = StringUtils.formatStringWithPattern(testValue, pattern);
        assertTrue(result.equals("ACD"));
    }

    @Test
    public void isformatString_Valid_number() {
        String testValue = "513";
        String pattern = "12345";
        String result = StringUtils.formatStringWithPattern(testValue, pattern);
        assertTrue(result.equals("135"));
    }
    
    @Test
    public void isValidBinary() {
        Integer testValue = 1;
        String result = StringUtils.convertIntegerToBinary(testValue);
        assertTrue(result.equals("1000000000000000"));
    }
    
    @Test
    public void isValidInteger() {
        String testValue = "1000000000000000";
        Integer result = StringUtils.convertBinaryToInteger(testValue);
        assertTrue(result.equals(1));
    }
}

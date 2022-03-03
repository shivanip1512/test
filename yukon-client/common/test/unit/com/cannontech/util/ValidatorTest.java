package com.cannontech.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.cannontech.core.dao.NotFoundException;


public class ValidatorTest {

    @Test
    public void test_isEmailAddress() throws NotFoundException{
                
        assertTrue(Validator.isEmailAddress("test@cannontech.com") );
        assertTrue(Validator.isEmailAddress("test1@cannontech.com"));
        assertTrue(Validator.isEmailAddress("test.test@cannontech.com"));
        assertTrue(Validator.isEmailAddress("test!#$%&'*+-/=?^_`{|}~test@cannontech.com"));
        assertTrue(Validator.isEmailAddress("test@cannon.tech.com"));
        
        assertFalse(Validator.isEmailAddress(""));
        assertFalse(Validator.isEmailAddress("@cannontech.com"));
        assertFalse(Validator.isEmailAddress("test@"));
        assertFalse(Validator.isEmailAddress("test@.com"));
        assertFalse(Validator.isEmailAddress("test@cannontech.c"));
        assertFalse(Validator.isEmailAddress("@"));
        assertFalse(Validator.isEmailAddress(".test.@cannontech.com"));
        assertFalse(Validator.isEmailAddress(".test@cannontech.com"));
        assertFalse(Validator.isEmailAddress("test.@cannontech.com"));
        assertFalse(Validator.isEmailAddress("test@cannontech..com"));
        assertFalse(Validator.isEmailAddress("test@.cannontech.com"));
        assertFalse(Validator.isEmailAddress("test@@cannontech.com"));
        
    }
    
    @Test
    public void test_isHEx() throws NotFoundException {
        assertTrue(Validator.isHex("123fbc"));
        assertTrue(Validator.isHex("0123456789abcdefABCDEF"));
        
        assertFalse(Validator.isHex("123t45"));
        assertFalse(Validator.isHex("g23a45"));
    }


}

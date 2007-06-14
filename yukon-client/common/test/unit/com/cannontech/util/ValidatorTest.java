package com.cannontech.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import com.cannontech.core.dao.NotFoundException;


public class ValidatorTest {

    @Before
    public void setUp() {
        ;
    }

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


}

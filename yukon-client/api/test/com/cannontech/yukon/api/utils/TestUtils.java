package com.cannontech.yukon.api.utils;

import org.junit.Assert;

import com.cannontech.yukon.api.util.SimpleXPathTemplate;

public class TestUtils {

    public static void runSuccessAssertion(SimpleXPathTemplate outputTemplate, String serviceResponseName) {
        
        Assert.assertNotNull("Missing success mode.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:success"));
    }
    
    public static void runFailureAssertions(SimpleXPathTemplate outputTemplate, String serviceResponseName, String expectedErrorCode) {
        
        Assert.assertNull("Should not have success node.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:success"));
        Assert.assertNotNull("No failure node present.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:failure"));
        Assert.assertEquals("Incorrect errorCode.", expectedErrorCode, outputTemplate.evaluateAsString("/y:" + serviceResponseName + "/y:failure/y:errorCode"));
    }
}

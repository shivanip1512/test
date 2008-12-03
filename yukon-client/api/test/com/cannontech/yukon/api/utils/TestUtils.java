package com.cannontech.yukon.api.utils;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;


public class TestUtils {

    private static Logger log = YukonLogManager.getLogger(TestUtils.class);
    
    public static void runSuccessAssertion(SimpleXPathTemplate outputTemplate, String serviceResponseName) {
        
        Assert.assertNotNull("Missing success node.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:success"));
    }
    
    public static void runFailureAssertions(SimpleXPathTemplate outputTemplate, String serviceResponseName, String expectedErrorCode) {
        
        Assert.assertNull("Should not have success node.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:success"));
        Assert.assertNotNull("No failure node present.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:failure"));
        Assert.assertEquals("Incorrect errorCode.", expectedErrorCode, outputTemplate.evaluateAsString("/y:" + serviceResponseName + "/y:failure/y:errorCode"));
    }
    
    public static void runVersionAssertion(SimpleXPathTemplate outputTemplate,
            String serviceResponseName, String version) {

        String serviceResponseNameWithNS = serviceResponseName;
        if (!serviceResponseNameWithNS.startsWith("/y:")) {
            serviceResponseNameWithNS += "/y:";
        }
        Assert.assertNotNull("No version node present",
                             outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/@version"));
        Assert.assertEquals("Incorrect Response version",
                            version,
                            outputTemplate.evaluateAsString(serviceResponseNameWithNS + "/@version"));
    }    
    
}

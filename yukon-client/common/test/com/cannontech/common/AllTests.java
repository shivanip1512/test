package com.cannontech.common;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.cannontech.common");
        //$JUnit-BEGIN$
        suite.addTestSuite(com.cannontech.dynamic.TestAsyncDynamicDataSource.class);
        //$JUnit-END$
        return suite;
    }

}

package com.cannontech.web.test.capcontrolarea;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.cannontech.web.test.capcontrolarea");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestCapControlArea.class);
        suite.addTestSuite(TestSubAreaAssignment.class);
        //$JUnit-END$
        return suite;
    }

}

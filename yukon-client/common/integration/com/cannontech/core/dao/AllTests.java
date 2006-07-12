package com.cannontech.core.dao;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.cannontech.core.dao");
        //$JUnit-BEGIN$
        suite.addTestSuite(PointDaoIntTest.class);
        suite.addTestSuite(PaoDaoIntTest.class);
        //$JUnit-END$
        return suite;
    }

}

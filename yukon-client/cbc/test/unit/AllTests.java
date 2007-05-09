package unit;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Cap Control Object Creation");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestCapControlFeederCreate.class);
        suite.addTestSuite(TestCapControlAreaCreate.class);
        suite.addTestSuite(TestCapControlSubCreate.class);
        suite.addTestSuite(TestCapControlCapbank.class);
        suite.addTestSuite(TestCapControlCBC.class);
        //$JUnit-END$
        return suite;
    }

}

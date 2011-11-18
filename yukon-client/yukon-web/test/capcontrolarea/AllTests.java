package capcontrolarea;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for capcontrolarea");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestAreaDataModel.class);
        //$JUnit-END$
        return suite;
    }

}

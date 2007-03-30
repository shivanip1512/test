package capbankadditionalinfo;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for capbankadditionalinfo");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestCapBankAddInfo.class);
        //$JUnit-END$
        return suite;
    }

}

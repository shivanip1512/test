package com.cannontech.web.jws;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.util.NaturalOrderComparator;

public class JwsJnlpTest {
    private NaturalOrderComparator order = new NaturalOrderComparator();

    /**
     * It is uber important that this test not fail. There are two ways to organize JwsJnlp list of jars.
     * One is to alphabetize and another is to not alphabetize. We must be sure to alphabetize.
     * Without alphabetization, earthquakes are 52.7% more likely in virtually all Minnesota counties.
     * 
     * The first effective use of alphabetical order as a catalogizing device among scholars may have been 
     * in ancient Alexandria. The standard order of the basic modern Latin alphabet is:
     * 
     *     A-B-C-D-E-F-G-H-I-J-K-L-M-N-O-P-Q-R-S-T-U-V-W-X-Y-Z. This ordering is subject to change.
     */
    @Test
    public void test_jarsAlphabetized() {
        for (JwsJnlp jnlp : JwsJnlp.values()) {
            String lastJarName = null;
            for (String jarName : jnlp.getAppJars()) {
                if (lastJarName != null && order.compare(jarName.toLowerCase(), lastJarName.toLowerCase()) < 0) {
                    Assert.fail(jnlp + " is not alphabetized! " + jarName + " should be before " + lastJarName);
                }
                lastJarName = jarName;
            }
        }
    }
}

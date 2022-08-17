package com.cannontech.common.pao.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test a couple of the .equals methods on a couple of complicated PAO persistence PAOs.
 */
public class CompleteYukonPaoEqualsTest {
    @Test
    public void testEquals_CompleteYukonPao() {
        CompleteYukonPao pao1 = new CompleteYukonPao();
        CompleteYukonPao pao2 = new CompleteYukonPao();

        assertFalse("the test doesn't do any good if we're comparing the same exact instances", pao1 == pao2);
        assertEquals("default objects should be the same", pao1, pao2);

        pao1.setPaoName("a name");
        pao2.setPaoName("a different name");
        assertNotEquals("objects with different names matched", pao1, pao2);
    }

    @Test
    public void testEquals_CompleteDevice() {
        CompleteDevice pao1 = new CompleteDevice();
        CompleteDevice pao2 = new CompleteDevice();

        assertFalse("the test doesn't do any good if we're comparing the same exact instances", pao1 == pao2);
        assertEquals("default objects should be the same", pao1, pao2);
    }

    @Test
    public void testEquals_CompleteCapBank() {
        CompleteCapBank pao1 = new CompleteCapBank();
        CompleteCapBank pao2 = new CompleteCapBank();

        assertFalse("the test doesn't do any good if we're comparing the same exact instances", pao1 == pao2);
        assertEquals("default objects should be the same", pao1, pao2);
    }

    @Test
    public void testEquals_CompleteOneWayCbc() {
        CompleteOneWayCbc pao1 = new CompleteOneWayCbc();
        CompleteOneWayCbc pao2 = new CompleteOneWayCbc();

        assertFalse("the test doesn't do any good if we're comparing the same exact instances", pao1 == pao2);
        assertEquals("default objects should be the same", pao1, pao2);
    }

    @Test
    public void testEquals_CompleteTwoWayCbc() {
        CompleteTwoWayCbc pao1 = new CompleteTwoWayCbc();
        CompleteTwoWayCbc pao2 = new CompleteTwoWayCbc();

        assertFalse("the test doesn't do any good if we're comparing the same exact instances", pao1 == pao2);
        assertEquals("default objects should be the same", pao1, pao2);

        pao1.setPaoName("pao 1");
        pao2.setPaoName("pao 2");
        assertNotEquals("objects with different names matched", pao1, pao2);

        pao1.setMasterAddress(5);
        pao2.setMasterAddress(7);
        pao2.setPaoName("pao 1");
        assertNotEquals("objects with different masterAddress matched", pao1, pao2);

        pao2.setMasterAddress(5);
        assertEquals("objects with different names matched", pao1, pao2);

        pao2.setPaoName("something different");
        assertNotEquals("objects with different names matched", pao1, pao2);
    }

    @Test
    public void testEquals_CompleteCbcLogical() {
        CompleteCbcLogical pao1 = new CompleteCbcLogical();
        CompleteCbcLogical pao2 = new CompleteCbcLogical();

        assertFalse("the test doesn't do any good if we're comparing the same exact instances", pao1 == pao2);
        assertEquals("default objects should be the same", pao1, pao2);

        pao1.setPaoName("pao 1");
        pao2.setPaoName("pao 2");
        assertNotEquals("objects with different names matched", pao1, pao2);

        pao1.setParentDeviceId(5);
        pao2.setParentDeviceId(7);
        pao2.setPaoName("pao 1");
        assertNotEquals("objects with different deviceParentId matched", pao1, pao2);

        pao2.setParentDeviceId(5);
        assertEquals("objects with different names matched", pao1, pao2);

        pao2.setPaoName("something different");
        assertNotEquals("objects with different deviceParentId matched", pao1, pao2);
    }
}

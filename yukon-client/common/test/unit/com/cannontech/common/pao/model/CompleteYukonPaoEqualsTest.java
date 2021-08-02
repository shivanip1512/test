package com.cannontech.common.pao.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test a couple of the .equals methods on a couple of complicated PAO persistence PAOs.
 */
public class CompleteYukonPaoEqualsTest {
    @Test
    public void testEquals_CompleteYukonPao() {
        CompleteYukonPao pao1 = new CompleteYukonPao();
        CompleteYukonPao pao2 = new CompleteYukonPao();

        assertFalse(pao1 == pao2, "the test doesn't do any good if we're comparing the same exact instances");
        assertEquals(pao1, pao2, "default objects should be the same");

        pao1.setPaoName("a name");
        pao2.setPaoName("a different name");
        assertNotEquals(pao1, pao2, "objects with different names matched");
    }

    @Test
    public void testEquals_CompleteDevice() {
        CompleteDevice pao1 = new CompleteDevice();
        CompleteDevice pao2 = new CompleteDevice();

        assertFalse(pao1 == pao2, "the test doesn't do any good if we're comparing the same exact instances");
        assertEquals(pao1, pao2, "default objects should be the same");
    }

    @Test
    public void testEquals_CompleteCapBank() {
        CompleteCapBank pao1 = new CompleteCapBank();
        CompleteCapBank pao2 = new CompleteCapBank();

        assertFalse(pao1 == pao2, "the test doesn't do any good if we're comparing the same exact instances");
        assertEquals(pao1, pao2, "default objects should be the same");
    }

    @Test
    public void testEquals_CompleteOneWayCbc() {
        CompleteOneWayCbc pao1 = new CompleteOneWayCbc();
        CompleteOneWayCbc pao2 = new CompleteOneWayCbc();

        assertFalse(pao1 == pao2, "the test doesn't do any good if we're comparing the same exact instances");
        assertEquals(pao1, pao2, "default objects should be the same");
    }

    @Test
    public void testEquals_CompleteTwoWayCbc() {
        CompleteTwoWayCbc pao1 = new CompleteTwoWayCbc();
        CompleteTwoWayCbc pao2 = new CompleteTwoWayCbc();

        assertFalse(pao1 == pao2, "the test doesn't do any good if we're comparing the same exact instances");
        assertEquals(pao1, pao2, "default objects should be the same");

        pao1.setPaoName("pao 1");
        pao2.setPaoName("pao 2");
        assertNotEquals(pao1, pao2, "objects with different names matched");

        pao1.setMasterAddress(5);
        pao2.setMasterAddress(7);
        pao2.setPaoName("pao 1");
        assertNotEquals(pao1, pao2, "objects with different masterAddress matched");

        pao2.setMasterAddress(5);
        assertEquals(pao1, pao2, "objects with different names matched");

        pao2.setPaoName("something different");
        assertNotEquals(pao1, pao2, "objects with different names matched");
    }

    @Test
    public void testEquals_CompleteCbcLogical() {
        CompleteCbcLogical pao1 = new CompleteCbcLogical();
        CompleteCbcLogical pao2 = new CompleteCbcLogical();

        assertFalse(pao1 == pao2, "the test doesn't do any good if we're comparing the same exact instances");
        assertEquals(pao1, pao2, "default objects should be the same");

        pao1.setPaoName("pao 1");
        pao2.setPaoName("pao 2");
        assertNotEquals(pao1, pao2, "objects with different names matched");

        pao1.setParentDeviceId(5);
        pao2.setParentDeviceId(7);
        pao2.setPaoName("pao 1");
        assertNotEquals(pao1, pao2, "objects with different deviceParentId matched");

        pao2.setParentDeviceId(5);
        assertEquals(pao1, pao2, "objects with different names matched");

        pao2.setPaoName("something different");
        assertNotEquals(pao1, pao2, "objects with different deviceParentId matched");
    }
}

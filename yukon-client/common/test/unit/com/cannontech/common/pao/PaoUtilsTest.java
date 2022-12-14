package com.cannontech.common.pao;

import static com.cannontech.common.pao.PaoUtils.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PaoUtilsTest {
    @Test
    public void testIsValidPaoName() {
        assertTrue(isValidPaoName("this"));

        assertFalse(isValidPaoName("this'"));
        assertFalse(isValidPaoName("this'that"));
        assertFalse(isValidPaoName("'that"));

        assertFalse(isValidPaoName("this,"));
        assertFalse(isValidPaoName("this,that"));
        assertFalse(isValidPaoName(",that"));

        assertFalse(isValidPaoName("this|that"));
        assertFalse(isValidPaoName("this\"that"));
    }
}

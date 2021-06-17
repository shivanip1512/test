package com.cannontech.common.util;

import static com.cannontech.common.util.BinaryPrefix.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSourceResolvable;

public class BinaryPrefixTest {
    private final static double tolerance = 0.0000001;
    private final static long kibiSize = 1024;
    private final static long mebiSize = kibiSize * 1024;
    private final static long gibiSize = mebiSize * 1024;
    private final static long tebiSize = gibiSize * 1024;
    private final static long pebiSize = tebiSize * 1024;
    private final static long exbiSize = pebiSize * 1024;

    @Test
    public void testKibi() {
        assertEquals(0.0, kibi.convertValue(0), tolerance);
        assertEquals(1.0 / 1024, kibi.convertValue(1), tolerance);
        assertEquals(1023.0 / 1024, kibi.convertValue(1023), tolerance);
        assertEquals(1025.0 / 1024, kibi.convertValue(1025), tolerance);
        assertEquals(1024.0, kibi.convertValue(1024 * 1024), tolerance);
    }

    @Test
    public void testMebi() {
        assertEquals(0.0, mebi.convertValue(0), tolerance);
        assertEquals(1.0 / (1024 * 1024), mebi.convertValue(1), tolerance);
        assertEquals((1024.0 * 1024 - 1) / (1024 * 1024),
                     mebi.convertValue(1024 * 1024 - 1), tolerance);
        assertEquals((1024.0 * 1024 + 1) / (1024 * 1024),
                     mebi.convertValue(1024 * 1024 + 1), tolerance);
        assertEquals(1.0, mebi.convertValue((1024 * 1024)), tolerance);
    }

    @Test
    public void testGibi() {
        assertEquals(0.0, gibi.convertValue(0), tolerance);
        assertEquals(1.0 / Math.pow(1024, 3), gibi.convertValue(1), tolerance);
        assertEquals((Math.pow(1024, 3) - 1) / Math.pow(1024, 3),
                     gibi.convertValue((long) Math.pow(1024, 3) - 1), tolerance);
        assertEquals((Math.pow(1024, 3) + 1) / Math.pow(1024, 3),
                     gibi.convertValue((long) Math.pow(1024, 3) + 1), tolerance);
        assertEquals(1.0, gibi.convertValue((long) Math.pow(1024, 3)), tolerance);
    }

    @Test
    public void testCompact() {
        MessageSourceResolvable resolvable = BinaryPrefix.getCompactRepresentation(1);
        String[] expectedCodes = new String[] { "yukon.common.prefixedByteValue.noPrefix" };
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {1.0}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(1023);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {kibiSize - 1.0}, resolvable.getArguments());

        expectedCodes = new String[] { "yukon.common.prefixedByteValue.kibi" };
        resolvable = BinaryPrefix.getCompactRepresentation(kibiSize);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {1.0}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(kibiSize + 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(kibiSize + 1.0) / kibiSize}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(mebiSize - 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(mebiSize - 1.0) / kibiSize}, resolvable.getArguments());

        expectedCodes = new String[] { "yukon.common.prefixedByteValue.mebi" };
        resolvable = BinaryPrefix.getCompactRepresentation(mebiSize);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {1.0}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(mebiSize + 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(mebiSize + 1.0) / mebiSize}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(gibiSize - 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(gibiSize - 1.0) / mebiSize}, resolvable.getArguments());

        expectedCodes = new String[] { "yukon.common.prefixedByteValue.gibi" };
        resolvable = BinaryPrefix.getCompactRepresentation(gibiSize);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {1.0}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(gibiSize + 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(gibiSize + 1.0) / gibiSize}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(tebiSize - 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(tebiSize - 1.0) / gibiSize}, resolvable.getArguments());

        expectedCodes = new String[] { "yukon.common.prefixedByteValue.tebi" };
        resolvable = BinaryPrefix.getCompactRepresentation(tebiSize);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {1.0}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(tebiSize + 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(tebiSize + 1.0) / tebiSize}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(pebiSize - 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(pebiSize - 1.0) / tebiSize}, resolvable.getArguments());

        expectedCodes = new String[] { "yukon.common.prefixedByteValue.pebi" };
        resolvable = BinaryPrefix.getCompactRepresentation(pebiSize);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {1.0}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(pebiSize + 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(pebiSize + 1.0) / pebiSize}, resolvable.getArguments());

        expectedCodes = new String[] { "yukon.common.prefixedByteValue.exbi" };
        resolvable = BinaryPrefix.getCompactRepresentation(exbiSize);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {1.0}, resolvable.getArguments());

        resolvable = BinaryPrefix.getCompactRepresentation(exbiSize + 1);
        assertArrayEquals(expectedCodes, resolvable.getCodes());
        assertArrayEquals(new Object[] {(exbiSize + 1.0) / exbiSize}, resolvable.getArguments());
    }
}

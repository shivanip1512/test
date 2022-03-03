package com.cannontech.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReverseListTest {
    Integer[] arrayA = {1,2,3,4,5};
    List<Integer> listA = Arrays.asList(arrayA);
    Integer[] arrayB = {5,4,3,2,1};
    List<Integer> listB = Arrays.asList(arrayB);

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    public void testSize() {
        ReverseList<Integer> r = new ReverseList<Integer>(listA);
        assertEquals(listA.size(), r.size());
    }

    @Test
    public void testGetInt() {
        ReverseList<Integer> r = new ReverseList<Integer>(listA);
        assertEquals(5, (int)r.get(0));
        assertEquals(1, (int)r.get(4));
        assertEquals(listB, r);
    }

}

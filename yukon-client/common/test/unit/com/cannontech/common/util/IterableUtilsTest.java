package com.cannontech.common.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class IterableUtilsTest {
    @Test
    public void testCollate() {
        List<String> list1 = Lists.newArrayList(new String[] {"a", "b", "c"});
        List<String> list2 = Lists.newArrayList(new String[] {"1", "2", "3"});
        List<String> list3 = Lists.newArrayList(new String[] {"aa", "bb", "cc", "dd", "ee"});
        List<String> list4 = Lists.newArrayList(new String[] {"100", "200", "300"});

        List<Iterable<String>> collated1Lists = Lists.newArrayList();
        collated1Lists.add(list1);
        collated1Lists.add(list2);
        Iterable<String> actual1 = IterableUtils.collate(collated1Lists);
        List<String> expected1 = Lists.newArrayList(new String[] {"a", "1", "b", "2", "c", "3"});
        assertTrue(Iterables.elementsEqual(actual1, expected1));

        List<Iterable<String>> collated2Lists = Lists.newArrayList();
        collated2Lists.add(list1);
        collated2Lists.add(list2);
        collated2Lists.add(list3);
        collated2Lists.add(list4);
        Iterable<String> actual2 = IterableUtils.collate(collated2Lists);
        List<String> expected2 =
            Lists.newArrayList(new String[] {"a", "1", "aa", "100", "b", "2", "bb", "200",
                    "c", "3", "cc", "300", "dd", "ee"});
        assertTrue(Iterables.elementsEqual(actual2, expected2));
    }

    @Test
    public void testClipped() {
        List<Integer> list = Lists.newArrayList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        Iterable<Integer> noMoreThan11 = IterableUtils.clipped(list, 11);
        assertTrue(Iterables.elementsEqual(list, noMoreThan11));

        Iterable<Integer> noMoreThan5 = IterableUtils.clipped(list, 5);
        List<Integer> listOf5 = Lists.newArrayList(new Integer[] {1, 2, 3, 4, 5});
        assertTrue(Iterables.elementsEqual(listOf5, noMoreThan5));
    }
}

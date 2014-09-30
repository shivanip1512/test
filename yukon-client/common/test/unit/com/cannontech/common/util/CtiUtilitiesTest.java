package com.cannontech.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

public class CtiUtilitiesTest {
    private final static Function<String, String> reverseStringFunction = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return new StringBuilder(input).reverse().toString();
        };
    };
    private final static Function<Person, Integer> personSortTranslator = new Function<Person, Integer>() {
        @Override
        public Integer apply(Person person) {
            return person.getSortField();
        }
    };
    private static class Person {
        int randomThingToSortOn;

        boolean called = false;

        Person(int randomThingToSortOn) {
            this.randomThingToSortOn = randomThingToSortOn;
        }

        int getSortField() {
            assertFalse(called);
            called = true;
            return randomThingToSortOn;
        }
    }

    // Some people who will be order in a decidedly un-alphabetical manner.
    private Person sam = new Person(1);
    private Person dan = new Person(2);
    private Person aaron = new Person(3);
    private Person ross = new Person(4);
    private Person jess = new Person(5);
    private List<Person> everyone = ImmutableList.of(sam, dan, aaron, ross, jess);
    private Person[] peopleInOrder = new Person[] {sam, dan, aaron, ross, jess};

    private void resetCalledStatus() {
        for (Person person : everyone) {
            person.called = false;
        }
    }

    private void validateSortedPeople(List<Person> hopefullySorted) {
        for (int index = 0; index < peopleInOrder.length; index++) {
            Person correctPerson = peopleInOrder[index];
            Person fromSortedList = hopefullySorted.get(index);
            assertSame(fromSortedList, correctPerson);
        }
    }
    @Test
    public void test_addNoOverflow() {
        assertEquals(3, CtiUtilities.addNoOverflow(1, 2));

        // Test overflow
        assertEquals(Integer.MAX_VALUE, CtiUtilities.addNoOverflow(Integer.MAX_VALUE, Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, CtiUtilities.addNoOverflow(Integer.MAX_VALUE, 1234));
        assertEquals(Integer.MAX_VALUE, CtiUtilities.addNoOverflow(Integer.MAX_VALUE, 1));
        assertEquals(Integer.MAX_VALUE, CtiUtilities.addNoOverflow(Integer.MAX_VALUE, 0));
        assertEquals(Integer.MAX_VALUE - 1, CtiUtilities.addNoOverflow(Integer.MAX_VALUE, -1));
        assertEquals(Integer.MAX_VALUE - 1234, CtiUtilities.addNoOverflow(Integer.MAX_VALUE, -1234));
        
        // Test under flow
        assertEquals(Integer.MIN_VALUE + 1234, CtiUtilities.addNoOverflow(Integer.MIN_VALUE, 1234));
        assertEquals(Integer.MIN_VALUE + 1, CtiUtilities.addNoOverflow(Integer.MIN_VALUE, 1));
        assertEquals(Integer.MIN_VALUE, CtiUtilities.addNoOverflow(Integer.MIN_VALUE, 0));
        assertEquals(Integer.MIN_VALUE, CtiUtilities.addNoOverflow(Integer.MIN_VALUE, -1));
        assertEquals(Integer.MIN_VALUE, CtiUtilities.addNoOverflow(Integer.MIN_VALUE, -1234));
        assertEquals(Integer.MIN_VALUE, CtiUtilities.addNoOverflow(Integer.MIN_VALUE, Integer.MIN_VALUE));
        
        assertEquals(-1, CtiUtilities.addNoOverflow(Integer.MIN_VALUE, Integer.MAX_VALUE));
    }
    @Test
    public void testSmartTranslatedSort() {
        List<String> toSort = ImmutableList.of("az", "za");
        List<String> sorted = CtiUtilities.smartTranslatedSort(toSort, reverseStringFunction);
        assertEquals(sorted.get(0), "za");
        assertEquals(sorted.get(1), "az");

        toSort = ImmutableList.of("az", "blah blah blah", "ducks and geese", "za");
        sorted = CtiUtilities.smartTranslatedSort(toSort, reverseStringFunction);
        assertEquals(sorted.get(0), "za");
        assertEquals(sorted.get(1), "ducks and geese");
        assertEquals(sorted.get(2), "blah blah blah");
        assertEquals(sorted.get(3), "az");

        resetCalledStatus();
        List<Person> peopleToSort = ImmutableList.of(ross, dan, sam, aaron, jess);
        List<Person> sortedPeople = CtiUtilities.smartTranslatedSort(peopleToSort, personSortTranslator);
        validateSortedPeople(sortedPeople);

        resetCalledStatus();
        peopleToSort = ImmutableList.of(sam, ross, dan, aaron, jess);
        sortedPeople = CtiUtilities.smartTranslatedSort(peopleToSort, personSortTranslator);
        validateSortedPeople(sortedPeople);
        
        resetCalledStatus();
        peopleToSort = ImmutableList.of(aaron, jess, sam, ross, dan);
        sortedPeople = CtiUtilities.smartTranslatedSort(peopleToSort, personSortTranslator);
        validateSortedPeople(sortedPeople);
    }
}

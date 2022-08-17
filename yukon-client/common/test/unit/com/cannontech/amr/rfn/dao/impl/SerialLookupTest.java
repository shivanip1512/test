package com.cannontech.amr.rfn.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;

public class SerialLookupTest {
    @Test
    public void test_empty() {
        var lookup = new SerialLookup();
        assertNull("Nonexistent lookup", lookup.get("1234567890123"));
        assertNull("Nonexistent lookup", lookup.get("987654321"));
        assertNull("Nonexistent lookup", lookup.get("3.14159 is pi"));
        assertNull("Nonexistent lookup", lookup.get("apple"));
        assertNull("Nonexistent lookup", lookup.get("banana"));
    }

    @Test
    public void test_singleNumeric() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("1234567890123", 108));
        
        assertEquals("Numeric lookup", Integer.valueOf(108), lookup.get("1234567890123"));
        assertNull("Nonexistent lookup", lookup.get("987654321"));
        assertNull("Nonexistent lookup", lookup.get("3.14159 is pi"));
        assertNull("Nonexistent lookup", lookup.get("apple"));
        assertNull("Nonexistent lookup", lookup.get("banana"));
    }

    @Test
    public void test_singleAlpha() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("banana", 42));
        
        assertNull("Nonexistent lookup", lookup.get("1234567890123"));
        assertNull("Nonexistent lookup", lookup.get("987654321"));
        assertNull("Nonexistent lookup", lookup.get("3.14159 is pi"));
        assertNull("Nonexistent lookup", lookup.get("apple"));
        assertEquals("Alpha lookup", Integer.valueOf(42), lookup.get("banana"));
    }

    @Test
    public void test_singleAlphanumeric() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("3.14159 is pi", 314159));
        
        assertNull("Nonexistent lookup", lookup.get("1234567890123"));
        assertNull("Nonexistent lookup", lookup.get("987654321"));
        assertEquals("Alphanumeric lookup", Integer.valueOf(314159), lookup.get("3.14159 is pi"));
        assertNull("Nonexistent lookup", lookup.get("apple"));
        assertNull("Nonexistent lookup", lookup.get("banana"));
    }
    
    @Test
    public void test_multiple() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("banana", 42,
                             "1234567890123", 108,
                             "3.14159 is pi", 314159));
        
        assertEquals("Numeric lookup", Integer.valueOf(108), lookup.get("1234567890123"));
        assertNull("Nonexistent lookup", lookup.get("987654321"));
        assertEquals("Alphanumeric lookup", Integer.valueOf(314159), lookup.get("3.14159 is pi"));
        assertNull("Nonexistent lookup", lookup.get("apple"));
        assertEquals("Alpha lookup", Integer.valueOf(42), lookup.get("banana"));

        //  Convert the stream into a list for comparison, retaining its order
        List<Integer> multiple = lookup.getAll(List.of("1234567890123", "987654321", "3.14159 is pi", "apple", "banana"))
                                       .collect(Collectors.toList());
        
        assertEquals("Multiple lookup", List.of(108, 314159, 42), multiple);
    }

    @Test
    public void test_removal() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("banana", 42,
                             "1234567890123", 108,
                             "3.14159 is pi", 314159));
        
        lookup.removeAll(Set.of(314159));

        assertEquals("Numeric lookup", Integer.valueOf(108), lookup.get("1234567890123"));
        assertNull("Nonexistent lookup", lookup.get("987654321"));
        assertNull("Removed element", lookup.get("3.14159 is pi"));
        assertNull("Nonexistent lookup", lookup.get("apple"));
        assertEquals("Alpha lookup", Integer.valueOf(42), lookup.get("banana"));
        
    }
}

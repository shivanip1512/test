package com.cannontech.amr.rfn.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class SerialLookupTest {
    @Test
    public void test_empty() {
        var lookup = new SerialLookup();
        assertNull(lookup.get("1234567890123"), "Nonexistent lookup");
        assertNull(lookup.get("987654321"), "Nonexistent lookup");
        assertNull(lookup.get("3.14159 is pi"), "Nonexistent lookup");
        assertNull(lookup.get("apple"), "Nonexistent lookup");
        assertNull(lookup.get("banana"), "Nonexistent lookup");
    }

    @Test
    public void test_singleNumeric() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("1234567890123", 108));
        
        assertEquals(Integer.valueOf(108), lookup.get("1234567890123"), "Numeric lookup");
        assertNull(lookup.get("987654321"), "Nonexistent lookup");
        assertNull(lookup.get("3.14159 is pi"), "Nonexistent lookup");
        assertNull(lookup.get("apple"), "Nonexistent lookup");
        assertNull(lookup.get("banana"), "Nonexistent lookup");
    }

    @Test
    public void test_singleAlpha() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("banana", 42));
        
        assertNull(lookup.get("1234567890123"), "Nonexistent lookup");
        assertNull(lookup.get("987654321"), "Nonexistent lookup");
        assertNull(lookup.get("3.14159 is pi"), "Nonexistent lookup");
        assertNull(lookup.get("apple"), "Nonexistent lookup");
        assertEquals(Integer.valueOf(42), lookup.get("banana"), "Alpha lookup");
    }

    @Test
    public void test_singleAlphanumeric() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("3.14159 is pi", 314159));
        
        assertNull(lookup.get("1234567890123"), "Nonexistent lookup");
        assertNull(lookup.get("987654321"), "Nonexistent lookup");
        assertEquals(Integer.valueOf(314159), lookup.get("3.14159 is pi"), "Alphanumeric lookup");
        assertNull(lookup.get("apple"), "Nonexistent lookup");
        assertNull(lookup.get("banana"), "Nonexistent lookup");
    }
    
    @Test
    public void test_multiple() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("banana", 42,
                             "1234567890123", 108,
                             "3.14159 is pi", 314159));
        
        assertEquals(Integer.valueOf(108), lookup.get("1234567890123"), "Numeric lookup");
        assertNull(lookup.get("987654321"), "Nonexistent lookup");
        assertEquals(Integer.valueOf(314159), lookup.get("3.14159 is pi"), "Alphanumeric lookup");
        assertNull(lookup.get("apple"), "Nonexistent lookup");
        assertEquals(Integer.valueOf(42), lookup.get("banana"), "Alpha lookup");

        //  Convert the stream into a list for comparison, retaining its order
        List<Integer> multiple = lookup.getAll(List.of("1234567890123", "987654321", "3.14159 is pi", "apple", "banana"))
                                       .collect(Collectors.toList());
        
        assertEquals(List.of(108, 314159, 42), multiple, "Multiple lookup");
    }

    @Test
    public void test_removal() {
        var lookup = new SerialLookup();
        lookup.putAll(Map.of("banana", 42,
                             "1234567890123", 108,
                             "3.14159 is pi", 314159));
        
        lookup.removeAll(Set.of(314159));

        assertEquals(Integer.valueOf(108), lookup.get("1234567890123"), "Numeric lookup");
        assertNull(lookup.get("987654321"), "Nonexistent lookup");
        assertNull(lookup.get("3.14159 is pi"), "Removed element");
        assertNull(lookup.get("apple"), "Nonexistent lookup");
        assertEquals(Integer.valueOf(42), lookup.get("banana"), "Alpha lookup");
        
    }
}

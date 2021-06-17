package com.cannontech.amr.rfn.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.database.db.device.RfnAddress;

public class RfnAddressIdLookupTest {
    
    private static RfnIdentifier id1 = new RfnIdentifier("5551212", "ITRN", "C2SX");
    private static RfnIdentifier id2 = new RfnIdentifier("THX1138", "WTR2", "Pulse-201");
    private static RfnIdentifier id3 = new RfnIdentifier("2718281828459", "ITRN", "C2SX");
    private static RfnIdentifier id4 = new RfnIdentifier("2718281828459", "WTR2", "Pulse-201");
    private static RfnIdentifier nonexistent = new RfnIdentifier("2718281828460", "WTR2", "Pulse-201");
    
    @Test
    public void test_emptyLookup() {
        var lookup = new RfnAddressIdLookup();
        
        assertNull(lookup.get(id1), "Nonexistent lookup");
        assertNull(lookup.get(id2), "Nonexistent lookup");
        assertNull(lookup.get(id3), "Nonexistent lookup");
        assertNull(lookup.get(id4), "Nonexistent lookup");
        assertNull(lookup.get(nonexistent), "Nonexistent lookup");
    }

    @Test
    public void test_singleNumericLookup() {
        var lookup = new RfnAddressIdLookup();

        lookup.putAll(List.of(makeRfnAddress(17, id1)));
        
        assertEquals(Integer.valueOf(17), lookup.get(id1), "Numeric serial lookup");
        assertNull(lookup.get(id2), "Nonexistent lookup");
        assertNull(lookup.get(id3), "Nonexistent lookup");
        assertNull(lookup.get(id4), "Nonexistent lookup");
        assertNull(lookup.get(nonexistent), "Nonexistent lookup");
    }
    
    @Test
    public void test_singleAlphanumericLookup() {
        var lookup = new RfnAddressIdLookup();

        lookup.putAll(List.of(makeRfnAddress(1776, id2)));
        
        assertNull(lookup.get(id1), "Nonexistent lookup");
        assertEquals(Integer.valueOf(1776), lookup.get(id2), "Alphanumeric serial lookup");
        assertNull(lookup.get(id3), "Nonexistent lookup");
        assertNull(lookup.get(id4), "Nonexistent lookup");
        assertNull(lookup.get(nonexistent), "Nonexistent lookup");
    }
    
    @Test
    public void test_multipleLookups() {
        var lookup = new RfnAddressIdLookup();

        lookup.putAll(List.of(makeRfnAddress(17, id1),
                              makeRfnAddress(1776, id2),
                              makeRfnAddress(31, id3),
                              makeRfnAddress(3141, id4)));
        
        assertEquals(Integer.valueOf(17), lookup.get(id1), "Integer serial lookup");
        assertEquals(Integer.valueOf(1776), lookup.get(id2), "Alphanumeric serial lookup");
        assertEquals(Integer.valueOf(31), lookup.get(id3), "Long serial lookup");
        assertEquals(Integer.valueOf(3141), lookup.get(id4), "Long serial lookup");
        assertNull(lookup.get(nonexistent), "Nonexistent lookup");
        
        Set<Integer> multiple = lookup.getAll(List.of(id1, id2, id3, id4, nonexistent));
        
        assertEquals(multiple, Set.of(17, 1776, 31, 3141), "Multiple lookup");
    }
    
    @Test
    public void test_removal() {
        var lookup = new RfnAddressIdLookup();

        lookup.putAll(List.of(makeRfnAddress(17, id1),
                              makeRfnAddress(1776, id2),
                              makeRfnAddress(31, id3),
                              makeRfnAddress(3141, id4)));
        
        lookup.remove(Set.of(1776, 3141));
        
        Set<Integer> multiple = lookup.getAll(List.of(id1, id2, id3, id4, nonexistent));
        
        assertEquals(Set.of(17, 31), multiple, "Post-removal lookup");
    }
    
    private static RfnAddress makeRfnAddress(int paoId, RfnIdentifier rfnIdentifier) {
        var rfnAddress = new RfnAddress();
        rfnAddress.setManufacturer(rfnIdentifier.getSensorManufacturer());
        rfnAddress.setModel(rfnIdentifier.getSensorModel());
        rfnAddress.setSerialNumber(rfnIdentifier.getSensorSerialNumber());
        rfnAddress.setDeviceID(paoId);
        return rfnAddress;
    }
}

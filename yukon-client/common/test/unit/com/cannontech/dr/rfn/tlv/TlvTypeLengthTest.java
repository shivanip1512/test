package com.cannontech.dr.rfn.tlv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TlvTypeLengthTest {
    
    @Test
    public void testAllValidValues() {
        for (int i = 0; i < 4096; i++) {
            testValues(i, i);
        }
    }
    
    @Test
    public void test_length_negative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testValues(1, -1);
        });
    }
    
    @Test
    public void test_length_tooLong() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testValues(1, 4096);
        });
    }
    
    @Test
    public void test_type_negative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testValues(-1, 1);
        });
    }
    
    @Test
    public void test_type_tooLong() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testValues(4096, 1);
        });
    }
    
    private void testValues(int type, int length) {
        //Convert type and length to bytes via constructor
        TlvTypeLength ttl = new TlvTypeLength(type, length);
        
        byte byte1 = (byte) ttl.getByte1UnsignedValue();
        byte byte2 = (byte) ttl.getByte2UnsignedValue();
        byte byte3 = (byte) ttl.getByte3UnsignedValue();
        
        //Convert back to type and length and compare
        int typeResult = TlvTypeLength.getType(byte1, byte2);
        assertEquals(type, typeResult, "Type mismatch");
        
        int lengthResult = TlvTypeLength.getLength(byte2, byte3);
        assertEquals(length, lengthResult, "Length mismatch");
    }
    
    
}

package com.cannontech.dr.rfn.tlv;

import org.junit.Assert;
import org.junit.Test;

public class TlvTypeLengthTest {
    
    @Test
    public void testAllValidValues() {
        for (int i = 0; i < 4096; i++) {
            testValues(i, i);
        }
    }
    
    private void testValues(int type, int length) {
        //Convert type and length to bytes via constructor
        TlvTypeLength ttl = new TlvTypeLength(type, length);
        
        byte byte1 = (byte) ttl.getByte1UnsignedValue();
        byte byte2 = (byte) ttl.getByte2UnsignedValue();
        byte byte3 = (byte) ttl.getByte3UnsignedValue();
        
        //Convert back to type and length and compare
        int typeResult = TlvTypeLength.getType(byte1, byte2);
        Assert.assertEquals("Type mismatch", type, typeResult);
        
        int lengthResult = TlvTypeLength.getLength(byte2, byte3);
        Assert.assertEquals("Length mismatch", length, lengthResult);
    }
}

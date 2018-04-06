package com.cannontech.dr.rfn.tlv;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * An object representing the type and length of a TLV field. For each TLV field, 3 bytes describe the type and 
 * length. The type is 12 bits split across byte 1 and the upper nibble of byte 2. The length is 12 bits split across
 * the second nibble of byte 2 and byte 3.
 * 
 * <pre>
 * [_byte1_] [_byte2_] [_byte3_]
 * 0000 0000 0000 0000 0000 0000
 * [----Type----] [---Length---]
 * <pre>
 */
public final class TlvTypeLength {
    final private int type;
    final private int length;
    final private byte byte1;
    final private byte byte2;
    final private byte byte3;
    
    public TlvTypeLength(int type, int length) {
        // Int can hold 32 bytes. Validate that actual values don't exceed 12 bytes.
        if (type > 2e12 || type < 0) {
            throw new IllegalArgumentException("Invalid TLV type: " + type);
        }
        if (length > 2e12 || length < 0) {
            throw new IllegalArgumentException("Invalid TLV length: " + length);
        }
        
        this.type = type;
        this.length = length;
        
        //Insert the top byte of type into byte 1
        byte1 = (byte) ((type >> 4));
        
        //Insert the bottom nibble of type and the top nibble of length into byte 2
        int topNibble = type << 4;
        int bottomNibble = (length & 0b111100000000) >> 8;
        byte2 = (byte) (topNibble | bottomNibble);
        
        //Insert the bottom byte of length into byte 3
        byte3 = (byte) length;
    }
    
    private TlvTypeLength(byte byte1, byte byte2, byte byte3, int type, int length) {
        this.byte1 = byte1;
        this.byte2 = byte2;
        this.byte3 = byte3;
        this.type = type;
        this.length = length;
    }
    
    public List<Byte> bytesList() {
        return ImmutableList.of(byte1, byte2, byte3);
    }
    
    public int getType() {
        return type;
    }
    
    public int getLength() {
        return length;
    }
    
    public int getByte1UnsignedValue() {
        return doNegativeConversion(byte1);
    }
    
    public int getByte2UnsignedValue() {
        return doNegativeConversion(byte2);
    }
    
    public int getByte3UnsignedValue() {
        return doNegativeConversion(byte3);
    }
    
    private static int doNegativeConversion(byte b) {
        //Since Java has no unsigned values, a byte value above 127 is treated as negative.
        //We have to do this goofy conversion.
        if (b < 0) {
            return b & 0b11111111;
        }
        return b;
    }
    
    public static TlvTypeLength fromBytes(byte byte1, byte byte2, byte byte3) {
        int type = getType(byte1, byte2);
        int length = getLength(byte2, byte3);
        return new TlvTypeLength(byte1, byte2, byte3, type, length);
    }
    
    public static int getType(byte byte1, byte byte2) {
        int type = doNegativeConversion(byte1); //put the first 8 bits of "type" into the int
        type = type << 4; //shift those 8 bits 4 slots to the left
        
        int typeNibble = doNegativeConversion(byte2) & 0b11110000; //get last 4 bits of "type", the top half of byte 2
        typeNibble = typeNibble >> 4; //shift those bits into the bottom 4 bits of the byte
        
        type = type | typeNibble; //combine the first byte and the last nibble
        
        return type;
    }
    
    public static int getLength(byte byte2, byte byte3) {
        int length = doNegativeConversion(byte2) & 0b00001111; //get first 4 bits of "length", the bottom half of byte 2
        length = length << 8; //shift those bits 8 slots to the left, leaving room for a full byte below
        length = length | doNegativeConversion(byte3); //combine the first nibble and the last byte
        
        return length;
    }
}

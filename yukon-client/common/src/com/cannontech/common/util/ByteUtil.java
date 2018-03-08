package com.cannontech.common.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.Instant;

public class ByteUtil {

    /**
     * Returns a list of bytes from large array of bytes based on chunk size
     * (Example divide 128 bytes into chunk size of 8 byte)
     * 
     */
    public static List<byte[]> divideByteArray(byte[] source, int chunksize) {

        List<byte[]> result = new ArrayList<>();
        for (int i = 0; i < source.length; i += chunksize) {
            result.add(Arrays.copyOfRange(source, i, Math.min(source.length, i + chunksize)));
        }
        return result;
    }

    /**
     * Returns Integer value corresponds to byte array
     * 
     */
    public static Integer getInteger(byte[] source) {
        return new BigInteger(source).intValue();
    }

    /**
     * Returns Integer value corresponds to byte
     * 
     */
    public static Integer getInteger(byte source) {
        return source & 0xff;
    }

    /**
     * Returns Long value corresponds to byte array
     * 
     */
    public static Long getLong(byte[] source) {
        return new BigInteger(source).longValue();
    }
    
    public static byte[] getTimestampBytes(Instant timestamp) {
        int dateInSec = (int) (timestamp.getMillis() / 1000);
        byte[] bytes = ByteBuffer.allocate(4).putInt(dateInSec).array();
        return bytes;
    }
}

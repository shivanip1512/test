package com.cannontech.common.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteUtil {

    public static List<byte[]> divideByteArray(byte[] source, int chunksize) {

        List<byte[]> result = new ArrayList<>();
        for (int i = 0; i < source.length; i += chunksize) {
            result.add(Arrays.copyOfRange(source, i, Math.min(source.length, i + chunksize)));
        }
        return result;
    }

    public static Integer getInteger(byte[] source) {
        return new BigInteger(source).intValue();
    }

    public static Integer getInteger(byte source) {
        return source & 0xff;
    }

    public static Long getLong(byte[] source) {
        return new BigInteger(source).longValue();
    }
}

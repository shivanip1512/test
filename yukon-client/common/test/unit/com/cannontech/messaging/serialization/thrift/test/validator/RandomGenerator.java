package com.cannontech.messaging.serialization.thrift.test.validator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class RandomGenerator {
    private static final int MIN_DEFAULT_LIST_SIZE = 10;
    private static final int MAX_DEFAULT_LIST_SIZE = 1000;
    private long u;
    private long v;
    private final long RAND_MAX = 0xFFFFFFFFL;

    public RandomGenerator() {
        this(0);
    }

    public RandomGenerator(long seed) {
        reset(seed);
    }

    public void reset(long seed) {
        seed &= 0X00000000FFFFFFFFl;

        u = seed;
        v = seed;
    }

    public long rand(int seed) {
        reset(seed);
        return rand();
    }

    public long rand() {
        v = 36969L * (v & 65535L) + (v >> 16) + 1;
        u = 18000L * (u & 65535L) + (u >> 16) + 1;

        v &= 0xFFFFFFFFL;
        u &= 0xFFFFFFFFL;

        long val = (v << 16) + u;

        val &= 0xFFFFFFFFL;

        return val;
    }

    /**
     * Generate a 'random' in the range [min, max] (where min AND max are included in the range)
     * @param min value of the lower range bound (included)
     * @param max value of the higher range bound (included)
     * @return a 'random' in the range [min, max] (where min AND max are included in the range)
     */
    public long rand(long min, long max) {
        // Because this is an 32 bits generator the range is restricted to these values
        if (min < Integer.MIN_VALUE || max > 0xFFFFFFFFL || min > max) {
            throw new InvalidParameterException("Invalid parameter value (min max)");
        }

        return min + (rand() % (max - min + 1));
    }

    public Integer generateInt() {
        return generateInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public Integer generateUInt() {
        return generateUIntAsLong().intValue();
    }

    public Integer generateInt(long min, long max) {
        return (int) rand(min, max);
    }

    public Long generateUIntAsLong() {
        return generateUIntAsLong(0, 0xFFFFFFFFL);
    }

    public Long generateUIntAsLong(long min, long max) {

        if (min < 0 || max > 0xFFFFFFFFL || min > max) {
            throw new InvalidParameterException("Invalid parameter value (min max)");
        }

        return rand(min, max);
    }

    public Long generateLong() {
        return rand();
    }

    public Long generateLong(long min, long max) {
        return rand(min, max);
    }

    public Float generateFloat() {
        return generateFloat(-(Float.MAX_VALUE / 3.0f), Float.MAX_VALUE / 3.0f);
    }

    public Float generateFloat(float min, float max) {
        return generateDouble(min, max).floatValue();
    }

    public Double generateDouble() {
        return generateDouble(-(Double.MAX_VALUE / 3.0), Double.MAX_VALUE / 3.0);
    }

    public Double generateDouble(double min, double max) {
        double val = ((double) rand()) / RAND_MAX;
        return min + (val * (max - min));
    }

    public Boolean generateBoolean() {
        return (rand() % 2) != 0;
    }

    public <T extends Enum<T>> T generateEnum(Class<T> clazz) {
        T[] values = clazz.getEnumConstants();

        return generateChoice(values);
    }

    public <T extends Enum<?>> Integer generateEnumInt(Class<T> clazz) {
        T[] values = clazz.getEnumConstants();

        return generateChoice(values).ordinal();
    }

    public <T> T generateChoice(T[] choices) {
        int index = (int) (rand() % choices.length);
        return choices[index];
    }

    public Date generateDate() {
        // 0x259EF1E0 is the unix time stamp for 01/01/1990 00:00:00
        // 0x967ACA60 is the unix time stamp for 01/01/2050 00:00:00
        long time = rand(0x259EF1E0l, 0x967ACA60l) * 1000;
        return new Date(time);
    }

    public GregorianCalendar generateCalendar() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(generateDate().getTime());
        return cal;
    }

    public String generateString() {
        int len = generateInt(MIN_DEFAULT_LIST_SIZE, MAX_DEFAULT_LIST_SIZE);

        return generateString(len);
    }

    public String generateString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; ++i) {
            sb.append((char) generateInt(32, 126).intValue());
        }

        return sb.toString();
    }

    public <T> List<T> generateList(Class<T> clazz) {
        return generateList(clazz, MIN_DEFAULT_LIST_SIZE, MAX_DEFAULT_LIST_SIZE);
    }

    public <T> List<T> generateList(Class<T> clazz, int size) {
        List<T> result = new ArrayList<T>(size);

        while (size-- > 0) {
            result.add(generate(clazz));
        }

        return result;
    }

    public <T> List<T> generateList(Class<T> clazz, int sizeMin, int sizeMax) {
        int size = generateInt(sizeMin, sizeMax);
        return generateList(clazz, size);
    }

    @SuppressWarnings("unchecked")
    public <T> T generate(Class<T> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return (T) generateInt();
        }

        if (clazz == Long.class || clazz == long.class) {
            return (T) generateLong();
        }

        if (clazz == Float.class || clazz == float.class) {
            return (T) generateFloat();
        }

        if (clazz == Double.class || clazz == double.class) {
            return (T) generateDouble();
        }

        if (clazz == Short.class || clazz == short.class) {
            return (T) generateInt(Short.MAX_VALUE, Short.MAX_VALUE);
        }

        if (clazz == Character.class || clazz == char.class) {
            return (T) generateInt(Character.MAX_VALUE, Character.MAX_VALUE);
        }

        if (clazz == String.class) {
            return (T) generateString();
        }

        if (Enum.class.isAssignableFrom(clazz)) {
            return (T) generateEnum((Class<? extends Enum>) clazz);
        }

        if (clazz == Date.class) {
            return (T) generateDate();
        }

        throw new RuntimeException("Unable to generage instance of '" + clazz.getSimpleName() + "'");
    }
}

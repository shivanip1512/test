package com.cannontech.common.util;

public class PositiveInteger implements Comparable<PositiveInteger> {
    private Integer integer;
    
    public PositiveInteger(int integer) {
        if(integer < 0) throw new IllegalArgumentException("Cannot create a PositiveInteger from a negative integer.");
        this.integer = integer;
    }
    
    public PositiveInteger(String s) {
        this(Integer.parseInt(s));
    }
    
    public double doubleValue() {
        return integer.doubleValue();
    }
    
    public float floatValue() {
        return integer.floatValue();
    }
    
    public int intValue() {
        return integer.intValue();
    }
    
    public long longValue() {
        return integer.longValue();
    }
    
    public short shortValue() {
        return integer.shortValue();
    }
    
    @Override
    public int compareTo(PositiveInteger anotherPositiveInteger) {
        return integer.compareTo(anotherPositiveInteger.integer);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PositiveInteger) {
            PositiveInteger otherPositiveInteger = (PositiveInteger) obj;
            return integer.equals(otherPositiveInteger.integer);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return integer.hashCode();
    }
    
    public static PositiveInteger parsePositiveInt(String s) {
        return new PositiveInteger(Integer.parseInt(s));
    }
    
    public static String toString(PositiveInteger pint) {
        return Integer.toString(pint.integer);
    }
    
    public static PositiveInteger valueOf(String s) {
        return new PositiveInteger(Integer.valueOf(s));
    }
}

package com.cannontech.common.util;

public class PositiveDouble implements Comparable<PositiveDouble>{
    private Double value;
    
    public PositiveDouble(double value) {
        if(value < 0) throw new IllegalArgumentException("Cannot create a PositiveDouble from a negative double");
        this.value = value;
    }
    
    public PositiveDouble(String s) {
        this(Double.parseDouble(s));
    }
    
    public double doubleValue() {
        return value.doubleValue();
    }
    
    public float floatValue() {
        return value.floatValue();
    }
    
    public int intValue() {
        return value.intValue();
    }
    
    public long longValue() {
        return value.longValue();
    }
    
    public short shortValue() {
        return value.shortValue();
    }
    
    @Override
    public int compareTo(PositiveDouble anotherPositiveDouble) {
        return value.compareTo(anotherPositiveDouble.value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PositiveDouble) {
            PositiveDouble otherPositiveDouble = (PositiveDouble) obj;
            return value.equals(otherPositiveDouble.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    public static PositiveDouble parsePositiveDouble(String s) {
        return new PositiveDouble(Double.parseDouble(s));
    }
    
    public static String toString(PositiveDouble pdub) {
        return Double.toString(pdub.value);
    }
    
    public static PositiveDouble valueOf(String s) {
        return new PositiveDouble(Double.valueOf(s));
    }
}
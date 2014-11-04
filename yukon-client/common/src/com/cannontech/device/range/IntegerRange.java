package com.cannontech.device.range;


public class IntegerRange extends Range<Integer> {
    
    private int lower;
    private int upper;
    
    public IntegerRange(int lower, int upper) {
        super(lower, upper);
        this.lower = lower;
        this.upper = upper;
    }
    
    public IntegerRange(String range) {
        super(range);
    }
    
    public int getLower() {
        return lower;
    }
    
    public int getUpper() {
        return upper;
    }
    
    @Override
    protected Integer parseToken(String token) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new InvalidRangeException();
        }
    }
    
}
package com.cannontech.device.range;


public class IntegerRange extends Range<Integer> {
   
    public IntegerRange(Integer lower, Integer upper) {
        super(lower, upper);
    }
    
    public IntegerRange(String range) {
        super(range);
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

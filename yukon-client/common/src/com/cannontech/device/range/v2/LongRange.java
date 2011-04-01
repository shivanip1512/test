package com.cannontech.device.range.v2;


public class LongRange extends Range<Long> {
    
    long minimum = Long.MAX_VALUE;
    long maximum = Long.MIN_VALUE;
    

    public LongRange(Long lower, Long upper) {
        super(lower, upper);
        minimum = lower;
        maximum = upper;
    }
    
    public LongRange(String range) {
        super();
        
        String[] intervalStrings = range.split(",");

        for (String intervalString : intervalStrings) {
            parseAndAddInterval(intervalString);
        }
    }

    private void parseAndAddInterval(String subString) {
        String[] bounds = subString.split("-");
        if (bounds.length != 2) {
            throw new IllegalArgumentException("Upper and lower bounds must be separated by -");
        }

        long lowerBound = Long.parseLong(bounds[0].trim());
        long upperBound = Long.parseLong(bounds[1].trim());
        
        if (lowerBound < minimum) {
            minimum = lowerBound;
        }
        
        if (upperBound > maximum) {
            maximum = upperBound;
        }

        addInterval(lowerBound, upperBound);

    }

    public long getMinimum() {
        return minimum;
    }

    public long getMaximum() {
        return maximum;
    }
    
    
}

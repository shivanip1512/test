package com.cannontech.common.util;

import java.math.BigDecimal;
import java.math.MathContext;

public class GraphIntervalRounding {
    /**
     * Rounds up a given number based on a passed in precision and returns that number in
     * BigDecimal format.
     * Precision should be greater than 1.<br>
     * <br>
     * <b>Examples:</b><br>
     * <br>
     * roundUp(0.0, 2.0); --> 0.0 (This is the case for any precision)<br><br>
     * roundUp(0.00534, 2.0); --> 0.006<br>
     * roundUp(0.00534, 2.5); --> 0.0075<br>
     * roundUp(0.00534, 5.0); --> 0.01<br>
     * roundUp(0.00534, 10.0); --> 0.010<br>
     * roundUp(0.00534, 20.0); --> 0.020<br>
     * roundUp(334.0, 2.0); --> 400<br>
     * roundUp(334.0, 2.5); --> 500<br>
     * roundUp(334.0, 5.0); --> 500<br>
     * roundUp(334.0, 10.0); --> 1000<br>
     * roundUp(334.0, 20.0); --> 2000<br>
     * @param number
     * @param precision
     */
    public static BigDecimal roundUp(double number, double precision) {
        if (number == 0) {
            BigDecimal zeroResult = new BigDecimal(0);
            return zeroResult;
        }

        BigDecimal fractionFactor = new BigDecimal(precision);
        double log10 = Math.log10(number);
        double floor = Math.floor(log10);
        double baseSigDigit = Math.pow(10, floor);
        double scaledValue = number / baseSigDigit;
        double scaledBucket = scaledValue - (scaledValue % fractionFactor.doubleValue()) + fractionFactor.doubleValue();
        double bucketSize = baseSigDigit * scaledBucket;
        
        BigDecimal bd = new BigDecimal(bucketSize, new MathContext(fractionFactor.precision()));
        return bd;
    }
}

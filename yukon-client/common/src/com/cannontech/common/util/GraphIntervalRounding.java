package com.cannontech.common.util;

import java.math.BigDecimal;
import java.math.MathContext;

public class GraphIntervalRounding {
    public static BigDecimal roundUp(double number, double precision) {
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

package com.cannontech.billing.format;

import java.text.DecimalFormat;

/**
 * Class used to create a billing file with row format as follows:
 * <pre>
 * 
 *   i,mmmmmmmmmm,ccccc,HH:MM,yy/MM/dd,ddd.dd,HH:MM,yy/MM/dd,s,g,f,p
 *    
 *   i - record indicator
 *   m - meter number
 *   c - total consumption
 *   HH:MM -total consumption time
 *   yy/MM/dd - total consumption date
 *   d - total peak demand
 *   HH:MM - total peak demand time
 *   yy/MM/dd -total peak demand date
 *   s - stat
 *   g - signal
 *   f - frequency
 *   p - phase
 * </pre>
 */
public class NISCRecordFormatter extends TurtleRecordFormatter {

    private static DecimalFormat KW_FORMAT = new DecimalFormat("##0.00");
    private static int KW_FIELD_SIZE = 6;

    @Override
    public int getKwFieldSize() {
        return KW_FIELD_SIZE;
    }

    @Override
    public DecimalFormat getKwFormat() {
        return KW_FORMAT;
    }

}

package com.cannontech.billing.format;

import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Class used to create a billing file with row format as follows:
 * <pre>
 * 
 *   i,mmmmmmmmmm,ccccc,HH:MM,yyyy/MM/dd,ddd.ddd,HH:MM,yyyy/MM/dd,s,g,f,p
 *    
 *   i - record indicator
 *   m - meter number
 *   c - total consumption
 *   HH:MM -total consumption time
 *   yyyy/MM/dd - total consumption date
 *   d - total peak demand
 *   HH:MM - total peak demand time
 *   yyyy/MM/dd -total peak demand date
 *   s - stat
 *   g - signal
 *   f - frequency
 *   p - phase
 * </pre>
 */
public class SEDC_yyyyMMddRecordFormatter extends TurtleRecordFormatter {
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected Format getDateFormat() {
        return DATE_FORMAT;
    }
}

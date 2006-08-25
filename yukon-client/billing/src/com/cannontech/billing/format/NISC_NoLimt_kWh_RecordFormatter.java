package com.cannontech.billing.format;

/**
 * Class used to create a billing file with row format as follows:
 * <pre>
 * 
 *   i,mmmmmmmmmm,ccccc,HH:MM,yyyy/MM/dd,ddd.dd,HH:MM,yyyy/MM/dd,s,g,f,p
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
public class NISC_NoLimt_kWh_RecordFormatter extends NISCRecordFormatter {

    public NISC_NoLimt_kWh_RecordFormatter() {
        // some large number to override BillingFormatterBase
        FORMAT_NODECIMAL.setMaximumIntegerDigits(20);
    }

}

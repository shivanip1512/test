package com.cannontech.billing.format;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *     
 *       i,mmmmmmmmmm,ccccc,HH:MM,yy/MM/dd,ddd.ddd,HH:MM,yy/MM/dd,s,g,f,p
 *        
 *       i - record indicator
 *       m - meter number
 *       c - total consumption
 *       HH:MM -total consumption time
 *       yy/MM/dd - total consumption date
 *       d - total peak demand
 *       HH:MM - total peak demand time
 *       yy/MM/dd -total peak demand date
 *       s - stat
 *       g - signal
 *       f - frequency
 *       p - phase
 * </pre>
 */
public class DAFFRONRecordFormatter extends TurtleRecordFormatter {

    private static final String HEADER = "H    Meter    kWh   Time   Date   Peak   PeakT  PeakD  Stat Sig Freq Phase\r\n";

    private static String STAT = "  6";

    @Override
    public String getStat() {
        return STAT + ",";
    }

    @Override
    public String getBillingFileHeader() {
        return HEADER;
    }

}

package com.cannontech.billing.format;

import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *    
 *      i,mmmmmmmmmm,ccccc,HH:MM,MM/dd/yyyy,ddd.dd,HH:MM,MM/dd/yyyy,s,g,f,p
 *       
 *      i - record indicator
 *      m - meter number
 *      c - total consumption
 *      HH:MM -total consumption time
 *      MM/dd/yyyy - total consumption date
 *      d - total peak demand
 *      HH:MM - total peak demand time
 *      MM/dd/yyyy -total peak demand date
 *      s - stat
 *      g - signal
 *      f - frequency
 *      p - phase
 * </pre>
 */
public class NISC_NCDCRecordFormatter extends NISCRecordFormatter {

    private static final String HEADER = "H    Meter    kWh   Time   Date    Peak   PeakT   PeakD\r\n";

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected Format getDateFormat() {
        return DATE_FORMAT;
    }

    @Override
    public String getBillingFileHeader() {
        return HEADER;
    }
}

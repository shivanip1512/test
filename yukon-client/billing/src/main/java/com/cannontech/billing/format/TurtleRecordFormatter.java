package com.cannontech.billing.format;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *                      
 *                        i,mmmmmmmmmm,ccccc,HH:MM,yy/MM/dd,ddd.ddd,HH:MM,yy/MM/dd,s,g,f,p
 *                         
 *                        i - record indicator
 *                        m - meter number
 *                        c - total consumption
 *                        HH:MM -total consumption time
 *                        yy/MM/dd - total consumption date
 *                        d - total peak demand
 *                        HH:MM - total peak demand time
 *                        yy/MM/dd -total peak demand date
 *                        s - stat
 *                        g - signal
 *                        f - frequency
 *                        p - phase
 * </pre>
 */
public class TurtleRecordFormatter extends BillingFormatterBase {

    private static final String HEADER = "H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase\r\n";

    private static final String RECORD_INDICATOR = "M";
    private static final String STAT = "";
    private static final String SIG = "";
    private static final String FREQ = ";";
    private static final String PHASE = "";
    private static int KW_FIELD_SIZE = 7;

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy/MM/dd");
    
    public TurtleRecordFormatter(){
        // ***THIS FORMAT TRUNCATES THE VALUE (FROM THE LEFT) TO MAX 5 NUMBERS
        // SO MOST SIGNIFICANT DIGITS MAY BE LOST!!!
        FORMAT_NODECIMAL.setMaximumIntegerDigits(5);
    }

    @Override
    public String getBillingFileHeader() {
        return HEADER;
    }

    /**
     * Method to create a String representing one record in the Turtle format:
     * r,mmmmmmmmmm,rrrrr,tt:tt,dd/dd/dd r - 1 meterReading/header record m - 10
     * meterNumber Left Justified r - 5 reading t - 5 time (includes :) d - 8
     * date (includes /s)
     * @param device - Device to create the record for
     * @return String representing the data for the device in the Turtle format
     */
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();

        addToStringBuffer(writeToFile, RECORD_INDICATOR, true);

        addToStringBufferWithTrailingFiller(writeToFile,
                                            device.getData(ReadingType.DEVICE_DATA, BillableField.meterNumber),
                                            10,
                                            " ",
                                            true);

        if (device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption) == null) {
            return "";
        }

        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                                    FORMAT_NODECIMAL),
                                             5,
                                             " ",
                                             true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                              getDateFormat()), true);

        if (this instanceof SEDC54RecordFormatter) {
            writeToFile.append("\r\n");
            return writeToFile.toString();
        }

        // Peak
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getValue(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                                    getKwFormat()),
                                             getKwFieldSize(),
                                             " ",
                                             true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                              getDateFormat()), false);

        if (this instanceof NCDCRecordFormatter || this instanceof NISC_NCDCRecordFormatter) {
            writeToFile.append("\r\n");
            return writeToFile.toString();
        } else {
            writeToFile.append(",");
        }

        // Stat
        writeToFile.append(getStat());

        // Sig
        writeToFile.append(getSig());

        // Freq
        writeToFile.append(getFreq());

        // Phase
        writeToFile.append(getPhase());

        writeToFile.append("\r\n");

        return writeToFile.toString();
    }

    protected Format getDateFormat() {
        return DATE_FORMAT;
    }

    protected String getStat() {
        return STAT + ",";
    }

    protected String getSig() {
        return SIG + ",";
    }

    protected String getFreq() {
        return FREQ;
    }

    protected String getPhase() {
        return PHASE;
    }

    public int getKwFieldSize() {
        return KW_FIELD_SIZE;
    }

    public DecimalFormat getKwFormat() {
        return KW_FORMAT;
    }

}

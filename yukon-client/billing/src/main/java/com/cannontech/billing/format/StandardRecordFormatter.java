package com.cannontech.billing.format;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *                      
 *                        i,mmmmmmmmmm,ccccc,HH:mm,MM/dd/yyyy,ddd.ddd,HH:mm,MM/dd/yyyy
 *                         
 *                        i - record indicator
 *                        m - meter number
 *                        c - total consumption
 *                        HH:mm -total consumption time
 *                        MM/dd/yyyy - total consumption date
 *                        d - total peak demand
 *                        HH:mm - total peak demand time
 *                        MM/dd/yyyy -total peak demand date
 * </pre>
 */
public class StandardRecordFormatter extends BillingFormatterBase {

    private static final String HEADER = "H    Meter    kWh   Time   Date    Peak    PeakT   PeakD\r\n";

    private static final String RECORD_INDICATOR = "M";

    @Override
    public String getBillingFileHeader() {
        return HEADER;
    }

    /**
     * Method to create a String representing one billing record.
     * i,mmmmmmmmmm,ccccc,HH:mm,MM/dd/yyyy,ddd.ddd,HH:mm,MM/dd/yyyy
     *  i - record indicator
     *  m - meter number
     *  c - total consumption
     *  HH:mm -total consumption time
     *  MM/dd/yyyy - total consumption date
     *  d - total peak demand
     *  HH:mm - total peak demand time
     *  MM/dd/yyyy -total peak demand date
     * @param device - Device to create the record for
     * @return String representing the data for the device in the Turtle format
     */
    public String dataToString(BillableDevice device) {

    	if (!isValid(device))
    		return "";
    	
        StringBuffer writeToFile = new StringBuffer();

        addToStringBuffer(writeToFile, RECORD_INDICATOR, true);

        addToStringBuffer(writeToFile,
        				  device.getData(ReadingType.DEVICE_DATA, BillableField.meterNumber),
        				  true);

        addToStringBuffer(writeToFile,
        				  format(device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption), FORMAT_NODECIMAL),
        				  true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                              DATE_FORMAT), true);

        // Peak
        addToStringBuffer(writeToFile,
        				  format(device.getValue(ReadingType.ELECTRIC, BillableField.totalPeakDemand), KW_FORMAT),
        				  true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                              DATE_FORMAT), false);

        writeToFile.append("\r\n");

        return writeToFile.toString();
    }
    
    private boolean isValid(BillableDevice device) {
    	if (device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption) == null &&
    			device.getValue(ReadingType.ELECTRIC, BillableField.totalPeakDemand) == null )
    		return false;

    	return true;
    }
}

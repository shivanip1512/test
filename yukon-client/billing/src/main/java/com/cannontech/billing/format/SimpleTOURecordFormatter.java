package com.cannontech.billing.format;

import java.text.DecimalFormat;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *         
 *         mmmmm,kkkkk,HH:MM,MM/dd/yyyy,wwwww,HH:MM,MM/dd/yyyy,AAAAA,aaaaa,HH:MM,MM/dd/yyyy,BBBBB,bbbbb,HH:MM,MM/dd/yyyy
 *            
 *         m - meter number
 *         k - total consumption
 *         w - total demand
 *         A - rate a consumption
 *         a - rate a demand
 *         B - rate b consumption
 *         b - rate b demand
 *           
 * </pre>
 * 
 * Note: all of the times and dates follow the reading they represent
 */
public class SimpleTOURecordFormatter extends BillingFormatterBase {
    
    public SimpleTOURecordFormatter(){
        FORMAT_NODECIMAL.setMaximumIntegerDigits(20);
    }

    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();

        addToStringBuffer(writeToFile, device.getData(ReadingType.DEVICE_DATA, BillableField.meterNumber), true);

        if (device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption) != null ||
        		device.getValue(ReadingType.ELECTRIC, BillableField.rateAConsumption) != null ||
        		device.getValue(ReadingType.ELECTRIC, BillableField.rateBConsumption) != null ) {
            // KWH
            addToStringBuffer(writeToFile,
                              format(device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption),
                            		  getKWHFormat()),
                              true);

            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                     TIME_FORMAT),
                              true);

            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                     DATE_FORMAT),
                              true);

            // KW
            addToStringBuffer(writeToFile,
                              format(device.getValue(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                     getKWFormat()),
                              true);

            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                     TIME_FORMAT),
                              true);

            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                     DATE_FORMAT),
                              true);

            // On Peak
            addToStringBuffer(writeToFile, format(device.getValue(ReadingType.ELECTRIC, BillableField.rateAConsumption),
                                                  getKWHFormat()), true);

            addToStringBuffer(writeToFile, format(device.getValue(ReadingType.ELECTRIC, BillableField.rateADemand),
                                                  getKWFormat()), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.rateADemand),
                                                  TIME_FORMAT), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.rateADemand),
                                                  DATE_FORMAT), true);

            // Off Peak
            addToStringBuffer(writeToFile, format(device.getValue(ReadingType.ELECTRIC, BillableField.rateBConsumption),
                                                  getKWHFormat()), true);

            addToStringBuffer(writeToFile, format(device.getValue(ReadingType.ELECTRIC, BillableField.rateBDemand),
                                                  getKWFormat()), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.rateBDemand),
                                                  TIME_FORMAT), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.rateBDemand),
                                                  DATE_FORMAT), false);

            writeToFile.append("\r\n");

            return writeToFile.toString();
        } else {
            return "";
        }
    }
    
    public DecimalFormat getKWHFormat() {
    	return FORMAT_NODECIMAL;
    }
    
    public DecimalFormat getKWFormat() {
    	return KW_FORMAT;
    }
}

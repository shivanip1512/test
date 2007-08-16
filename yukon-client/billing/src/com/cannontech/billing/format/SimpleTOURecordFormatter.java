package com.cannontech.billing.format;

import java.text.DecimalFormat;

import com.cannontech.billing.device.base.BillableDevice;
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

        addToStringBuffer(writeToFile, device.getData(BillableField.meterNumber), true);

        if (device.getCalculatedValue(BillableField.totalConsumption) != null ||
        		device.getCalculatedValue(BillableField.rateAConsumption) != null ||
        		device.getCalculatedValue(BillableField.rateBConsumption) != null ) {
            // KWH
            addToStringBuffer(writeToFile,
                              format(device.getCalculatedValue(BillableField.totalConsumption),
                            		  getKWHFormat()),
                              true);

            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(BillableField.totalConsumption),
                                     TIME_FORMAT),
                              true);

            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(BillableField.totalConsumption),
                                     DATE_FORMAT),
                              true);

            // KW
            addToStringBuffer(writeToFile,
                              format(device.getCalculatedValue(BillableField.totalPeakDemand),
                                     getKWFormat()),
                              true);

            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(BillableField.totalPeakDemand),
                                     TIME_FORMAT),
                              true);

            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(BillableField.totalPeakDemand),
                                     DATE_FORMAT),
                              true);

            // On Peak
            addToStringBuffer(writeToFile, format(device.getValue(BillableField.rateAConsumption),
                                                  getKWHFormat()), true);

            addToStringBuffer(writeToFile, format(device.getValue(BillableField.rateADemand),
                                                  getKWFormat()), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(BillableField.rateADemand),
                                                  TIME_FORMAT), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(BillableField.rateADemand),
                                                  DATE_FORMAT), true);

            // Off Peak
            addToStringBuffer(writeToFile, format(device.getValue(BillableField.rateBConsumption),
                                                  getKWHFormat()), true);

            addToStringBuffer(writeToFile, format(device.getValue(BillableField.rateBDemand),
                                                  getKWFormat()), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(BillableField.rateBDemand),
                                                  TIME_FORMAT), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(BillableField.rateBDemand),
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

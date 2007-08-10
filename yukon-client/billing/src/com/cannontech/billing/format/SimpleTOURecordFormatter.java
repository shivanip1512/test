package com.cannontech.billing.format;

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

        if (device.getCalculatedValue(BillableField.totalConsumption) != null) {
            // KWH
            addToStringBuffer(writeToFile,
                              format(device.getCalculatedValue(BillableField.totalConsumption),
                                     FORMAT_NODECIMAL),
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
                                     KW_FORMAT),
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
                                                  FORMAT_NODECIMAL), true);

            addToStringBuffer(writeToFile, format(device.getValue(BillableField.rateADemand),
                                                  KW_FORMAT), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(BillableField.rateADemand),
                                                  TIME_FORMAT), true);

            addToStringBuffer(writeToFile, format(device.getTimestamp(BillableField.rateADemand),
                                                  DATE_FORMAT), true);

            // Off Peak
            addToStringBuffer(writeToFile, format(device.getValue(BillableField.rateBConsumption),
                                                  FORMAT_NODECIMAL), true);

            addToStringBuffer(writeToFile, format(device.getValue(BillableField.rateBDemand),
                                                  KW_FORMAT), true);

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
}

package com.cannontech.billing.format;

import java.text.SimpleDateFormat;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *                      
 *		MeterNumber 
 *		DeviceName 
 *		kWh Reading (9999.99) 
 *		kWh Time (HH:mm) 
 *		kWh Date (MM/dd/yyyy) 
 *		Peak Demand Reading (99.9999) 
 *		Peak Demand Time (HH:mm) 
 *		Peak Demand Date (MM/dd/yyyy) 
 * </pre>
 */
public class Basic_DeviceNameRecordFormatter extends BillingFormatterBase {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    
    public Basic_DeviceNameRecordFormatter(){

    }

    @Override
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();

        addToStringBuffer(writeToFile,
                         device.getData(BillableField.meterNumber),
                         true);

        addToStringBuffer(writeToFile,
                device.getData(BillableField.paoName),
                true);

        
        if (device.getCalculatedValue(BillableField.totalConsumption) == null) {
            return "";
        }

        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getCalculatedValue(BillableField.totalConsumption),
                                            		 DECIMAL_FORMAT_4v2),
                                             7,
                                             " ",
                                             true);

        addToStringBufferWithPrecedingFiller(writeToFile, 
        									 format(device.getTimestamp(BillableField.totalConsumption),
        											 TIME_FORMAT),
        									 TIME_FORMAT.toPattern().length(),
        									 " ",
        									 true);

        addToStringBufferWithPrecedingFiller(writeToFile, 
        									 format(device.getTimestamp(BillableField.totalConsumption),
        											 getDateFormat()),
        									 getDateFormat().toPattern().length(),
        									 " ",
        									 true);
        // Peak
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getCalculatedValue(BillableField.totalPeakDemand),
                                                    DECIMAL_FORMAT_2v4),
                                             7,
                                             " ",
                                             true);

        addToStringBufferWithPrecedingFiller(writeToFile, 
        									 format(device.getTimestamp(BillableField.totalPeakDemand),
        											 TIME_FORMAT),
                                             TIME_FORMAT.toPattern().length(),
                                             " ",
                                             true);

        addToStringBufferWithPrecedingFiller(writeToFile, 
        									 format(device.getTimestamp(BillableField.totalPeakDemand),
        											 getDateFormat()), 
                                             getDateFormat().toPattern().length(),
                                             " ",
                                             false);

        writeToFile.append("\r\n");

        return writeToFile.toString();
    }

    protected SimpleDateFormat getDateFormat() {
        return DATE_FORMAT;
    }
}

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
 *         mmmmm,kkkkk,HH:MM,MM/dd/yyyy,wwwww,HH:MM,MM/dd/yyyy,AAAAA,aaaaa,HH:MM,MM/dd/yyyy,BBBBB,bbbbb,HH:MM,MM/dd/yyyy,ddddd
 *            
 *         m - meter number
 *         k - total consumption
 *         w - total demand
 *         A - rate a consumption
 *         a - rate a demand
 *         B - rate b consumption
 *         b - rate b demand
 *         d - device name
 *           
 * </pre>
 * 
 * This format extends the SimpleTOURecordFormatter and appends the Device Name (PaoName) as the last field in the record
 * Note: all of the times and dates follow the reading they represent
 */
public class SimpleTOU_DeviceNameRecordFormatter extends SimpleTOURecordFormatter {
    
    public SimpleTOU_DeviceNameRecordFormatter(){
    }

    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();
        String superData = super.dataToString(device);
        
        if( superData.length() > 0) {
        	superData = superData.replaceAll("\r\n", "");	//remove the end of line
        	addToStringBuffer(writeToFile, superData, true);

        	addToStringBuffer(writeToFile, device.getData(ReadingType.DEVICE_DATA, BillableField.paoName), false);

        	writeToFile.append("\r\n");
        	
        	return writeToFile.toString();
        } else 
        	return "";
    }
    
    @Override
    public DecimalFormat getKWFormat() {
    	return DECIMAL_FORMAT_4v2;
    }
    
    @Override
    public DecimalFormat getKWHFormat() {
    	return DECIMAL_FORMAT_2v4;
    }
}

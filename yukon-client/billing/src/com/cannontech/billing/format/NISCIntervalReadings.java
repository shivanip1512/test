package com.cannontech.billing.format;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * SN 20090715 - NISC requested that we don't send true interval readings (aka, all readings we have for a date range).
 * Instead, they requested that we only send the most recent one we have for the date range selected, 
 *  which is the same as our standard billing formats. 
 * <pre>
 * 
 * Offset   Length  Type    Description     Value
 * 1        1       C       Record Type     'H' Header 'M' Meter 'L' End of Record
 * 2        1       C       Comma           ','
 * 3        15      C       Meter Number    15 char     pad right (spaces)
 * 18       1       C       Comma           ','
 * 19       15      C       Transponder id  15 char     pad right (spaces)
 * 34       1       C       Comma           ','
 * 35       15      C       Port Number     #########   pad right (spaces)
 * 50       1       C       Comma           ','
 * 51       2       N       Meter Register  ##          pad left (spaces) (A=1, B=2, C=3, D=4)  Total??? use 1 if not know?
 * 53       1       C       Comma           ','
 * 54       8       C       Read date       MMDDYYYY
 * 62       1       C       Comma           ','
 * 63       5       C       Read time       HH:MM
 * 68       1       C       Comma           ','
 * 69       9       N       KWH Reading     #########
 * 78       1       C       Comma           ','
 * 79       10      N       KW Reading      ######.000
 * 89       1       C       Comma           ','
 * 90       8       C       KW Peak date    MMDDYYYY
 * 98       1       C       Comma           ','
 * 99       5       C       KW Peak time    HH:MM
 * 104      1       C       Comma           ','
 * 105      10      N       KVAR Reading    #######.00
 * 115      1       C       Comma           ','
 * 116      5       C       AMR Read Code   5 char
 * 121      1       C       Comma           ','
 * 122      10      N       Blink Count     ##########
 * 131         Total Length    
 * </pre>
 */
public class NISCIntervalReadings extends BillingFormatterBase {

    private final String HEADER = "H\r\n";
    private final String FOOTER = "L\r\n";

    private final String RECORD_INDICATOR = "M";
    private SimpleDateFormat DATE_FORMAT_MMDDYYYY = new SimpleDateFormat("MMddyyyy");
    
    public NISCIntervalReadings() {
    }

    @Override
    public String getBillingFileHeader() {
        return HEADER;
    }

    @Override
    public String getBillingFileFooter() {
        return FOOTER;
    }
    
    /**
     * Method to create a String representing one record in the Interval Readings format:
     * @param device - Device to create the record for
     * @return String representing the data for the device in the Turtle format
     */
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();

        addFormattedLine(writeToFile, device, BillableField.rateAConsumption, BillableField.rateADemand);
        addFormattedLine(writeToFile, device, BillableField.rateBConsumption, BillableField.rateBDemand);
        addFormattedLine(writeToFile, device, BillableField.rateCConsumption, BillableField.rateCDemand);
        addFormattedLine(writeToFile, device, BillableField.rateDConsumption, BillableField.rateDDemand);
        if (StringUtils.isBlank(writeToFile.toString())) {
        	//Only add the "total" if we don't have any of the other rates.
        	addFormattedLine(writeToFile, device, BillableField.totalConsumption, BillableField.totalPeakDemand);
        }

        return writeToFile.toString();
    }

	/**
	 * @param device
	 * @param writeToFile
	 */
	private void addFormattedLine(StringBuffer writeToFile, BillableDevice device, BillableField kWhBillableField, BillableField peakDemandBillableField) {
		
		Timestamp kWhTimestamp = device.getTimestamp(Channel.ONE, ReadingType.ELECTRIC, kWhBillableField);
        Timestamp kWTimestamp = device.getTimestamp(Channel.ONE, ReadingType.ELECTRIC, peakDemandBillableField);
        
        //nothing to write
        if ( kWhTimestamp == null && kWTimestamp == null) {
        	return;
        }
        BillableField rateBillableField = kWhBillableField;
        if (kWhTimestamp == null) {
        	//Try to use the kWTimestamp if the kWh timestamp does not exist.
        	//This change is per NISC's request to always put the timestamp in the kWh timestamp field.
        	kWhTimestamp = kWTimestamp;
        	rateBillableField = peakDemandBillableField;
        }

        addToStringBuffer(writeToFile, RECORD_INDICATOR, true);

        addToStringBufferWithTrailingFiller(writeToFile,
                                            device.getData(Channel.ONE, ReadingType.DEVICE_DATA, BillableField.meterNumber),
                                            15,
                                            " ",
                                            true);

        addToStringBufferWithTrailingFiller(writeToFile,
                                            device.getData(Channel.ONE, ReadingType.DEVICE_DATA, BillableField.address),
                                            15,
                                            " ",
                                            true);

        addToStringBufferWithPrecedingFiller(writeToFile, null, 15, " ", true); //PortNumber - not supported
        
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             convertToNISCRate(device.getRate(ReadingType.ELECTRIC, rateBillableField)),
                                             2,
                                             " ",
                                             true);

        
        addToStringBufferWithPrecedingFiller(writeToFile, 
                                             format(kWhTimestamp,
                                                    DATE_FORMAT_MMDDYYYY), 
                                             8,
                                             " ",
                                             true);

        addToStringBufferWithPrecedingFiller(writeToFile, 
                                             format(kWhTimestamp,
                                                    TIME_FORMAT), 
                                             5,
                                             " ",
                                             true);
        
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getValue(Channel.ONE, ReadingType.ELECTRIC, kWhBillableField),
                                                    FORMAT_NODECIMAL),
                                             9,
                                             " ",
                                             true);

        // Add the peak demand data
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getValue(Channel.ONE, ReadingType.ELECTRIC, peakDemandBillableField),
                                                    DECIMAL_FORMAT6V3),
                                             10,
                                             " ",
                                             true);

        addToStringBufferWithPrecedingFiller(writeToFile, 
                                             format(kWTimestamp,
                                                    DATE_FORMAT_MMDDYYYY), 
                                             8,
                                             " ",
                                             true);

        addToStringBufferWithPrecedingFiller(writeToFile, 
                                             format(kWTimestamp,
                                                    TIME_FORMAT), 
                                             5,
                                             " ",
                                             true);

        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getValue(Channel.ONE, ReadingType.KVAR, BillableField.totalConsumption),
                                                    DECIMAL_FORMAT_7V2),
                                             10,
                                             " ",
                                             true); //Does not have a timestamp associated with it...but that's a limitation of the format, not Yukon

        addToStringBufferWithPrecedingFiller(writeToFile, new Integer(0), 5, " ", true);  //AMR Read Code - use some default value of 0 (CHeinrich@NISC 20090626)
        addToStringBufferWithPrecedingFiller(writeToFile, null, 10, " ", false); //Blink Count - not supported because there is no respective date/time in this format
        writeToFile.append("\r\n");
	}

    /**
     * Helper method to return the NISC numeric value for the TOU Rate.  
     * There is one problem here, NISC does not know about the total register
     * when there is more than one rate.  So we can have potential problems here
     * becuase we will return 1 for A, but also 1 for the total register.  In the 
     * case that the total AND rate A (for example) are being read by the customer,
     * the billing system will get "weird" data.  We (NISC and I) could not come up with 
     * a good solution to this problem since Yukon does not know what rates are being
     * billed off of.
     * @param rate
     * @return
     */
    private Integer convertToNISCRate(String rate) {
        
    	if (rate == null) {
    		//Rate can be null when we have aggregated data.
    		return 1;
    	}
    	
        if (rate.equalsIgnoreCase("A")) {
            return 1;
        } else if (rate.equalsIgnoreCase("B")) {
            return 2;
        } else if (rate.equalsIgnoreCase("C")) {
            return 3;
        } else if (rate.equalsIgnoreCase("D")) {
            return 4;
        }
        return 1;
    }
}
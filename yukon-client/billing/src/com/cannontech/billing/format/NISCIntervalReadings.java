package com.cannontech.billing.format;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * <pre>
 * 
 * Offset   Length  Type    Description     Value
 * 1        1       C       Record Type     ‘H’ Header ‘M’ Meter ‘L’ End of Record
 * 2        1       C       Comma           ‘,’
 * 3        15      C       Meter Number    15 char     pad right (spaces)
 * 18       1       C       Comma           ‘,’
 * 19       15      C       Transponder id  15 char     pad right (spaces)
 * 34       1       C       Comma           ‘,’
 * 35       15      C       Port Number     #########   pad right (spaces)
 * 50       1       C       Comma           ‘,’
 * 51       2       N       Meter Register  ##          pad left (spaces) (A=1, B=2, C=3, D=4)  Total??? use 1 if not know?
 * 53       1       C       Comma           ‘,’
 * 54       8       C       Read date       MMDDYYYY
 * 62       1       C       Comma           ‘,’
 * 63       5       C       Read time       HH:MM
 * 68       1       C       Comma           ‘,’
 * 69       9       N       KWH Reading     #########
 * 78       1       C       Comma           ‘,’
 * 79       10      N       KW Reading      ######.000
 * 89       1       C       Comma           ‘,’
 * 90       8       C       KW Peak date    MMDDYYYY
 * 98       1       C       Comma           ‘,’
 * 99       5       C       KW Peak time    HH:MM
 * 104      1       C       Comma           ‘,’
 * 105      10      N       KVAR Reading    #######.00
 * 115      1       C       Comma           ‘,’
 * 116      5       C       AMR Read Code   5 char
 * 121      1       C       Comma           ‘,’
 * 122      10      N       Blink Count     ##########
 * 131         Total Length    
 * </pre>
 */
public class NISCIntervalReadings extends BillingFormatterBase {

    private final String HEADER = "H\r\n";
    private final String FOOTER = "L\r\n";

    private final String RECORD_INDICATOR = "M";
    private SimpleDateFormat DATE_FORMAT_MMDDYYYY = new SimpleDateFormat("MMddyyyy");
    public final DecimalFormat DECIMAL_FORMAT_2 = new DecimalFormat("##");
    public final DecimalFormat DECIMAL_FORMAT_9 = new DecimalFormat("#########");
    public final DecimalFormat DECIMAL_FORMAT_10 = new DecimalFormat("##########");
    
    private BillableField[] possibleKwhFields = 
        new BillableField[]{BillableField.rateAConsumption,
                            BillableField.rateBConsumption,
                            BillableField.rateCConsumption,
                            BillableField.rateDConsumption,
                            BillableField.totalConsumption};
    
    private BillableField[] possiblePeakDemandFields = 
        new BillableField[]{BillableField.rateADemand,
                            BillableField.rateBDemand,
                            BillableField.rateCDemand,
                            BillableField.rateDDemand,
                            BillableField.totalPeakDemand};
    
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

        BillableField kWhBillableField = getkWhBillableField(device);
        BillableField peakDemandBillableField = getPeakDemandBillableField(device);

        if (kWhBillableField != null || peakDemandBillableField != null) {

            // The billable field is determined by the rate specified in the billableField for the kWh else kW field
            BillableField rateBillableField = (kWhBillableField != null ? kWhBillableField : (peakDemandBillableField != null ? peakDemandBillableField : null));
            
            //Need to adjust the billable fields to "total" if they are null
            kWhBillableField = (kWhBillableField == null ? BillableField.totalConsumption : kWhBillableField);
            peakDemandBillableField = (peakDemandBillableField == null ? BillableField.totalPeakDemand : peakDemandBillableField); 

            
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
                                                 format(device.getTimestamp(Channel.ONE, ReadingType.ELECTRIC, kWhBillableField),
                                                        DATE_FORMAT_MMDDYYYY), 
                                                 8,
                                                 " ",
                                                 true);
    
            addToStringBufferWithPrecedingFiller(writeToFile, 
                                                 format(device.getTimestamp(Channel.ONE, ReadingType.ELECTRIC, kWhBillableField),
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
                                                 format(device.getTimestamp(Channel.ONE, ReadingType.ELECTRIC, peakDemandBillableField),
                                                        DATE_FORMAT_MMDDYYYY), 
                                                 8,
                                                 " ",
                                                 true);
    
            addToStringBufferWithPrecedingFiller(writeToFile, 
                                                 format(device.getTimestamp(Channel.ONE, ReadingType.ELECTRIC, peakDemandBillableField),
                                                        TIME_FORMAT), 
                                                 5,
                                                 " ",
                                                 true);

            addToStringBufferWithPrecedingFiller(writeToFile, null, 10, " ", true); //kVar Reading - not supported because there is no respective date/time in this format
            addToStringBufferWithPrecedingFiller(writeToFile, null, 5, " ", true);  //AMR Read Code - not supported
            addToStringBufferWithPrecedingFiller(writeToFile, null, 10, " ", false); //Blink Count - not supported because there is no respective date/time in this format
            writeToFile.append("\r\n");
        }
        
        return writeToFile.toString();
    }

    /**
     * Helper method to return the type (rate) of consumption that device contains.
     * Returns null if no data is returned.
     * totalConsumption must be checked after all the other rates because it is "special"
     * and can actually return data from any of the rates.  But this isn't good enough, we
     * need to know exactly which billableField we are using.
     * @param device
     * @return
     */
    private BillableField getkWhBillableField(BillableDevice device) {
        
        for (BillableField field : possibleKwhFields) {
            Double kWh = device.getValue(Channel.ONE, ReadingType.ELECTRIC, field);
            if (kWh != null) {
                return field;
            }
        }
        return null;
    }
   
    /**
     * Helper method to return the type (rate) of peak demand that device contains.
     * Returns null if no data is returned.
     * totalPeakDemand must be checked after all the other rates because it is "special"
     * and can actually return data from any of the rates.  But this isn't good enough, we
     * need to know exactly which billableField we are using.
     * @param device
     * @return
     */
    private BillableField getPeakDemandBillableField(BillableDevice device) {
            
        for (BillableField field : possiblePeakDemandFields) {
            Double peakDemand = device.getValue(Channel.ONE, ReadingType.ELECTRIC, field);
            if (peakDemand != null) {
                return field;
            }
        }
        return null;
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

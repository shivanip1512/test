package com.cannontech.billing.format;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *              
 *              mmmmm,kkkkk,HH:MM,MM/dd/yyyy,wwwww,HH:MM,MM/dd/yyyy,AAAAA,aaaaa,HH:MM,MM/dd/yyyy,BBBBB,bbbbb,HH:MM,MM/dd/yyyy,CCCCC,ccccc,HH:MM,MM/dd/yyyy,DDDDD,ddddd,HH:MM,MM/dd/yyyy
 *                 
 *              m - meter number
 *              k - total consumption
 *              w - total demand
 *              A - rate a consumption
 *              a - rate a demand
 *              B - rate b consumption
 *              b - rate b demand
 *              C - rate c consumption
 *              c - rate c demand
 *              D - rate d consumption
 *              d - rate d demand
 *                
 * </pre>
 * 
 * Note: all of the times and dates follow the reading they represent
 */
public class ExtendedTOURecordFormatter extends BillingFormatterBase {

    public static final String ELECTRIC_CODE = "E";
    public static final String KVAR_CODE = "R";
    public static final String KVA_CODE = "V";
    public static final String WATER_CODE = "W";
    public static final String GAS_CODE = "G";

    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();

        // Electric
        if (device.getData(Channel.ONE, ReadingType.ELECTRIC, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, ELECTRIC_CODE, Channel.ONE, ReadingType.ELECTRIC);

        }
        if (device.getData(Channel.TWO, ReadingType.ELECTRIC, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile,
            				 device, 
            				 ELECTRIC_CODE + String.valueOf(Channel.TWO.getNumeric()), 
            				 Channel.TWO, 
            				 ReadingType.ELECTRIC);

        }
        if (device.getData(Channel.THREE, ReadingType.ELECTRIC, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile,
                             device,
                             ELECTRIC_CODE+ String.valueOf(Channel.THREE.getNumeric()),
                             Channel.THREE,
                             ReadingType.ELECTRIC);

        }

        // KVA
        if (device.getData(Channel.ONE, ReadingType.KVA, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, KVA_CODE, Channel.ONE, ReadingType.KVA);

        }
        if (device.getData(Channel.TWO, ReadingType.KVA, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, KVA_CODE, Channel.TWO, ReadingType.KVA);

        }
        if (device.getData(Channel.THREE, ReadingType.KVA, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, KVA_CODE, Channel.THREE, ReadingType.KVA);

        }

        // KVAR
        if (device.getData(Channel.ONE, ReadingType.KVAR, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, KVAR_CODE, Channel.ONE, ReadingType.KVAR);

        }
        if (device.getData(Channel.TWO, ReadingType.KVAR, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, KVAR_CODE, Channel.TWO, ReadingType.KVAR);

        }
        if (device.getData(Channel.THREE, ReadingType.KVAR, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, KVAR_CODE, Channel.THREE, ReadingType.KVAR);

        }

        // Water
        String suffix = "";
        if (device.getData(Channel.ONE, ReadingType.WATER, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, WATER_CODE, Channel.ONE, ReadingType.WATER);
            suffix = "2";

        }
        if (device.getData(Channel.TWO, ReadingType.WATER, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile,
                             device,
                             WATER_CODE + suffix,
                             Channel.TWO,
                             ReadingType.WATER);
            if ("".equals(suffix)) {
                suffix = "2";
            } else {
                suffix = "3";
            }

        }
        if (device.getData(Channel.THREE, ReadingType.WATER, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile,
                             device,
                             WATER_CODE + suffix,
                             Channel.THREE,
                             ReadingType.WATER);

        }

        // Gas
        suffix = "";
        if (device.getData(Channel.ONE, ReadingType.GAS, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, GAS_CODE, Channel.ONE, ReadingType.GAS);
            suffix = "2";

        }
        if (device.getData(Channel.TWO, ReadingType.GAS, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, GAS_CODE + suffix, Channel.TWO, ReadingType.GAS);
            if ("".equals(suffix)) {
                suffix = "2";
            } else {
                suffix = "3";
            }

        }
        if (device.getData(Channel.THREE, ReadingType.GAS, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, GAS_CODE + suffix, Channel.THREE, ReadingType.GAS);

        }

        return writeToFile.toString();
    }

    /**
     * Helper method to create and add a string billing record to the output
     * buffer for a given channel and reading type
     * @param writeToFile - StringBuffer which holds all of the output
     * @param device - Device to get billing record data from
     * @param code - Type code for this reading type
     * @param channel - Channel for this billing record
     * @param type - ReadingType for this billing record
     */
    protected void addFormattedLine(StringBuffer writeToFile, BillableDevice device, String code,
            Channel channel, ReadingType type) {

        addToStringBuffer(writeToFile, getMeterNumber(device, code), true);

        addToStringBuffer(writeToFile, code, true);

        // TotalConsumption
        addToStringBuffer(writeToFile, format(device.getValue(channel,
                                                              type,
                                                              BillableField.totalConsumption),
                                              FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.totalConsumption),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.totalConsumption),
                                              DATE_FORMAT), true);

        // Total Peak Demand
        Double value = device.getValue(channel, type, BillableField.totalPeakDemand);
        addToStringBuffer(writeToFile, format(value, KW_FORMAT), true);

        addToStringBuffer(writeToFile, (value == null) ? ""
                : format(device.getTimestamp(channel, type, BillableField.totalPeakDemand),
                         TIME_FORMAT), true);

        addToStringBuffer(writeToFile, (value == null) ? ""
                : format(device.getTimestamp(channel, type, BillableField.totalPeakDemand),
                         DATE_FORMAT), true);

        // Rate A
        addToStringBuffer(writeToFile, format(device.getValue(channel,
                                                              type,
                                                              BillableField.rateAConsumption),
                                              FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile,
                          format(device.getValue(channel, type, BillableField.rateADemand),
                                 KW_FORMAT),
                          true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.rateADemand),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.rateADemand),
                                              DATE_FORMAT), true);

        // Rate B
        addToStringBuffer(writeToFile, format(device.getValue(channel,
                                                              type,
                                                              BillableField.rateBConsumption),
                                              FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile,
                          format(device.getValue(channel, type, BillableField.rateBDemand),
                                 KW_FORMAT),
                          true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.rateBDemand),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.rateBDemand),
                                              DATE_FORMAT), true);

        // Rate C
        addToStringBuffer(writeToFile, format(device.getValue(channel,
                                                              type,
                                                              BillableField.rateCConsumption),
                                              FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile,
                          format(device.getValue(channel, type, BillableField.rateCDemand),
                                 KW_FORMAT),
                          true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.rateCDemand),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.rateCDemand),
                                              DATE_FORMAT), true);

        // Rate D
        addToStringBuffer(writeToFile, format(device.getValue(channel,
                                                              type,
                                                              BillableField.rateDConsumption),
                                              FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile,
                          format(device.getValue(channel, type, BillableField.rateDDemand),
                                 KW_FORMAT),
                          true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.rateDDemand),
                                              TIME_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(channel,
                                                                  type,
                                                                  BillableField.rateDDemand),
                                              DATE_FORMAT), false);

        writeToFile.append("\r\n");
    }

    /**
     * Method to return the MeterNumber
     * @param device - Device to get billing record data from
     * @param code - Type code for this reading type
     */
    protected String getMeterNumber(BillableDevice device, String code ) {
        return device.getData(ReadingType.DEVICE_DATA, BillableField.meterNumber);
    }
}

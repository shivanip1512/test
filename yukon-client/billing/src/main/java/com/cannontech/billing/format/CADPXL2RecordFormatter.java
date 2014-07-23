package com.cannontech.billing.format;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *               
 *                 aaaaaaaaaaiisssssptttttttttyyyyMMddbbbyyyyMMddwwwmmmmmmmmmmmmmmmnnccoooooooooqqqqqqqqqvvvvvvvvv
 *                  
 *                 a - account number
 *                 i - import type
 *                 s - service group
 *                 p - payment sign
 *                 t - payment
 *                 yyyyMMdd - batch date
 *                 b - batch number
 *                 yyyyMMdd - total consumption date
 *                 w - who read meter
 *                 m - meter number
 *                 n - meter position number
 *                 c - channel number (register number)
 *                 o - total consumption
 *                 q - total peak demand (kW)
 *                 v - total peak demand (kVar)
 * </pre>
 * 
 * Note: sections c, o, q and v will be repeated up to 4 times per row - there
 * can be reading data for up to 4 channels for each device per billing row
 */
public class CADPXL2RecordFormatter extends BillingFormatterBase {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    private static final String IMPORT_TYPE = "HH";
    private static final String SERVICE_GROUP = "ELEC";
    private static final String PAYMENT_SIGN = " ";
    private static final Double PAYMENT = new Double(0.0);
    private static final String BATCH_DATE = DATE_FORMAT.format(new Date());
    private static final String BATCH_NUMBER = "800";
    private static final String WHO_READ_METER = "   ";

    @Override
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();
        
        String accountNumber = device.getData(ReadingType.DEVICE_DATA, BillableField.accountNumber);
        if (accountNumber == null) {
            accountNumber = device.getData(ReadingType.DEVICE_DATA, BillableField.paoName);
        }

        // only keep 10 characters
        if (accountNumber != null && accountNumber.length() > 10) {
            accountNumber = accountNumber.substring(0, 10);
        }
        addToStringBufferWithPrecedingFiller(writeToFile, accountNumber, 10, "0", false);

        writeToFile.append(IMPORT_TYPE);

        addToStringBufferWithTrailingFiller(writeToFile, SERVICE_GROUP, 5, " ", false);

        writeToFile.append(PAYMENT_SIGN);

        String paymentString = format(PAYMENT, DECIMAL_FORMAT_7V2);
        paymentString = paymentString.replaceAll(".", "");
        addToStringBufferWithPrecedingFiller(writeToFile, paymentString, 9, "0", false);

        writeToFile.append(BATCH_DATE);

        writeToFile.append(BATCH_NUMBER);

        Timestamp totalConsumptionTimestamp = device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption);
        if (totalConsumptionTimestamp == null) {
            return "";
        }
        writeToFile.append(format(totalConsumptionTimestamp, DATE_FORMAT));

        writeToFile.append(WHO_READ_METER);

        addToStringBufferWithTrailingFiller(writeToFile,
                                            device.getData(ReadingType.DEVICE_DATA, BillableField.meterNumber),
                                            15,
                                            " ",
                                            false);

        String meterPostionNumber = device.getData(ReadingType.DEVICE_DATA, BillableField.meterPositionNumber);
        if (meterPostionNumber == null) {
            meterPostionNumber = "01";
        }
        addToStringBufferWithPrecedingFiller(writeToFile, meterPostionNumber, 2, "0", false);

        // Loop over all channels, adding the data from each to the billing file
        // string
        for (Channel channel : Channel.values()) {
            writeToFile.append(getChannelDataString(channel, device));
        }

        writeToFile.append("\r\n");

        return writeToFile.toString();
    }

    /**
     * Helper method to generate a data string for a given channel of data
     * @param channel - Channel to generate data for
     * @param device - Device to get the data from
     * @return String representing the channel's billing data
     */
    private String getChannelDataString(Channel channel, BillableDevice device) {

        boolean noRecord = true;

        StringBuffer channelDataString = new StringBuffer();

        // Register number = channel number
        addToStringBufferWithPrecedingFiller(channelDataString,
                                             String.valueOf(channel.getNumeric()),
                                             2,
                                             "0",
                                             false);

        // kWh Total consumption
        String totalConsumption = format(device.getValue(channel,
                                                                   ReadingType.ELECTRIC,
                                                                   BillableField.totalConsumption),
                                         DECIMAL_FORMAT9V0);
        if (totalConsumption == null) {
            totalConsumption = "000000000";
        } else {
            noRecord = false;
        }
        totalConsumption = totalConsumption.replaceAll("\\.", "");

        channelDataString.append(totalConsumption);

        // kW Total demand
        String totalDemand = format(device.getValue(channel,
                                                              ReadingType.ELECTRIC,
                                                              BillableField.totalPeakDemand),
                                    DECIMAL_FORMAT6V3);
        if (totalDemand == null) {
            totalDemand = "000000000";
        } else {
            noRecord = false;
        }
        totalDemand = totalDemand.replaceAll("\\.", "");

        channelDataString.append(totalDemand);

        // kVar Total demand
        totalDemand = format(device.getValue(channel,
                                                       ReadingType.KVAR,
                                                       BillableField.totalPeakDemand),
                             DECIMAL_FORMAT_7V2);
        if (totalDemand == null) {
            totalDemand = "000000000";
        } else {
            noRecord = false;
        }
        totalDemand = totalDemand.replaceAll("\\.", "");

        channelDataString.append(totalDemand);

        return (noRecord) ? "" : channelDataString.toString();
    }

}

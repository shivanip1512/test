package com.cannontech.billing.format;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

public class BigRiversElecCoopFormatter extends BillingFormatterBase {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyMMdd");
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmm");

    public BigRiversElecCoopFormatter() {
        DECIMAL_FORMAT4V3.setMaximumIntegerDigits(4);
        DECIMAL_FORMAT7V0.setMaximumIntegerDigits(7);
    }

    @Override
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer("");

        // Meter number
        String meterNumber = device.getData(ReadingType.DEVICE_DATA, BillableField.meterNumber);
        if (meterNumber != null && meterNumber.length() > 8) {
            // cut the account down to field size
            meterNumber = meterNumber.substring(meterNumber.length() - 8);
        }
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             meterNumber,
                                             8,
                                             " ",
                                             false);

        // kWh time, date, reading
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                                    TIME_FORMAT),
                                             4,
                                             "0",
                                             false);
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                                    DATE_FORMAT),
                                             6,
                                             "0",
                                             false);

        //Need to truncate the decimals from the reading, instead of round.
        Double totalConsumption = device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption);
        if ( totalConsumption == null ){
            return "";
        } else {
            Double r = Math.floor(totalConsumption);
            String value = format(r, DECIMAL_FORMAT7V0);
            addToStringBufferWithPrecedingFiller(writeToFile, value, 7, "0", false);
        }

        // kW time, date, reading
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                                    TIME_FORMAT),
                                             4,
                                             "0",
                                             false);
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalPeakDemand),
                                                    DATE_FORMAT),
                                             6,
                                             "0",
                                             false);
        String value = format(device.getValue(ReadingType.ELECTRIC, BillableField.totalPeakDemand), DECIMAL_FORMAT4V3);
        if (value != null) {
            value = value.replaceAll("\\.", "");
        }
        addToStringBufferWithPrecedingFiller(writeToFile, value, 7, "0", false);

        // kVar time, date, reading (Use kVa time, date, reading if kVar is null)
        //  - First... attempt to find the KVAR reading/timestamp, if not found attempt to find the KVA reading.
        //  - The secondary lookup of kVa data is for Big Rivers (and Jackson Purchase) specfically.
        Timestamp totalPeakDemandTS = device.getTimestamp(Channel.ONE,ReadingType.KVAR, BillableField.totalPeakDemand);
        Double totalPeakDemand = device.getValue(Channel.ONE, ReadingType.KVAR, BillableField.totalPeakDemand);

        //If Kvar returned us nothing, then look for kVa
        if (totalPeakDemandTS == null && totalPeakDemand == null) {
            totalPeakDemandTS = device.getTimestamp(Channel.ONE,ReadingType.KVA, BillableField.totalPeakDemand);
            totalPeakDemand = device.getValue(Channel.ONE, ReadingType.KVA, BillableField.totalPeakDemand);
        }
        
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(totalPeakDemandTS, TIME_FORMAT),
                                             4,
                                             "0",
                                             false);
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(totalPeakDemandTS, DATE_FORMAT),
                                             6,
                                             "0",
                                             false);
        value = format(totalPeakDemand, DECIMAL_FORMAT4V3);
        if (value != null) {
            value = value.replaceAll("\\.", "");
        }
        addToStringBufferWithPrecedingFiller(writeToFile, value, 7, "0", false);

        // Account
        String account = device.getData(ReadingType.DEVICE_DATA, BillableField.paoName);
        if (account != null && account.length() > 11) {
            // cut the account down to field size
            account = account.substring(account.length() - 11);
        }
        addToStringBufferWithPrecedingFiller(writeToFile, account, 11, " ", false);

        writeToFile.append("\r\n");

        return writeToFile.toString();
    }
}

package com.cannontech.billing.format;

import java.text.SimpleDateFormat;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.device.base.BillableField;
import com.cannontech.billing.device.base.Channel;
import com.cannontech.billing.device.base.ReadingType;

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
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             device.getData(BillableField.meterNumber),
                                             8,
                                             " ",
                                             false);

        // kWh time, date, reading
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(BillableField.totalConsumption),
                                                    TIME_FORMAT),
                                             4,
                                             "0",
                                             false);
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(BillableField.totalConsumption),
                                                    DATE_FORMAT),
                                             6,
                                             "0",
                                             false);
        String value = format(device.getCalculatedValue(BillableField.totalConsumption),
                              DECIMAL_FORMAT7V0);
        if (value == null) {
            return "";
        }
        addToStringBufferWithPrecedingFiller(writeToFile, value, 7, "0", false);

        // kW time, date, reading
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(BillableField.totalPeakDemand),
                                                    TIME_FORMAT),
                                             4,
                                             "0",
                                             false);
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(BillableField.totalPeakDemand),
                                                    DATE_FORMAT),
                                             6,
                                             "0",
                                             false);
        value = format(device.getCalculatedValue(BillableField.totalPeakDemand), DECIMAL_FORMAT4V3);
        if (value != null) {
            value = value.replaceAll("\\.", "");
        }
        addToStringBufferWithPrecedingFiller(writeToFile, value, 7, "0", false);

        // kVar time, date, reading
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(Channel.ONE,
                                                                        ReadingType.KVAR,
                                                                        BillableField.totalPeakDemand),
                                                    TIME_FORMAT),
                                             4,
                                             "0",
                                             false);
        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getTimestamp(Channel.ONE,
                                                                        ReadingType.KVAR,
                                                                        BillableField.totalPeakDemand),
                                                    DATE_FORMAT),
                                             6,
                                             "0",
                                             false);
        value = format(device.getCalculatedValue(Channel.ONE,
                                                 ReadingType.KVAR,
                                                 BillableField.totalPeakDemand), DECIMAL_FORMAT4V3);
        if (value != null) {
            value = value.replaceAll("\\.", "");
        }
        addToStringBufferWithPrecedingFiller(writeToFile, value, 7, "0", false);

        // Account
        String account = device.getData(BillableField.paoName);
        if (account != null && account.length() > 11) {
            // cut the account down to field size
            account = account.substring(account.length() - 11);
        }
        addToStringBufferWithPrecedingFiller(writeToFile, account, 11, " ", false);

        writeToFile.append("\r\n");

        return writeToFile.toString();
    }
}

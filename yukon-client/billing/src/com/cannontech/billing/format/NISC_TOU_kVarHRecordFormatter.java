package com.cannontech.billing.format;

import java.text.DecimalFormat;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *                     
 *                       i,mmmmmmmmmm,t,kkkkk,HH:MM,MM/dd/yyyy,wwww.ww,HH:MM,MM/dd/yyyy,vvvvvv 
 *                         
 *                       i - record indicator 
 *                       m - meter number
 *                       k - total kWh reading (followed by total kWh time and date)
 *                       w - peak kW reading (followed by peak kW time and date)
 *                       v - total kVarh reading
 * </pre>
 */
public class NISC_TOU_kVarHRecordFormatter extends BillingFormatterBase {

    private static final String HEADER = "H    Meter   TOU kWh  Time   Date      Peak   PeakT   PeakD\r\n";

    private static final DecimalFormat KVARH_FORMAT_NODECIMAL = new DecimalFormat("######");

    private static final String RECORD_INDICATOR = "M";

    public NISC_TOU_kVarHRecordFormatter() {
        FORMAT_NODECIMAL.setMaximumIntegerDigits(20);
    }

    @Override
    public String getBillingFileHeader() {
        return HEADER;
    }

    @Override
    public String dataToString(BillableDevice device) {
        StringBuffer writeToFile = new StringBuffer();

        String rowData = null;
        boolean addKwh = true;

        // Total kWh, Rate A kw and kvarh
        rowData = createRow(device,
                            1,
                            BillableField.rateADemand,
                            BillableField.rateAConsumption,
                            addKwh);
        if (rowData != "") {
            writeToFile.append(rowData);
            addKwh = false;
        }

        // Rate B kw and kvarh
        rowData = createRow(device,
                            2,
                            BillableField.rateBDemand,
                            BillableField.rateBConsumption,
                            addKwh);
        if (rowData != "") {
            writeToFile.append(rowData);
            addKwh = false;
        }
        // Rate C kw and kvarh
        rowData = createRow(device,
                            3,
                            BillableField.rateCDemand,
                            BillableField.rateCConsumption,
                            addKwh);
        if (rowData != "") {
            writeToFile.append(rowData);
            addKwh = false;
        }

        // Rate D kw and kvarh
        rowData = createRow(device,
                            4,
                            BillableField.rateDDemand,
                            BillableField.rateDConsumption,
                            addKwh);
        writeToFile.append(rowData);

        return writeToFile.toString();
    }

    /**
     * Helper method to generate a billing file row for a rate
     * @param device - device to get the data from
     * @param touIndicator - Rate indicator for the row
     * @param kwField - kw field for the tou rate
     * @param kvarhField - kvarh field for the tou rate
     * @return String representing a billing file row
     */
    private String createRow(BillableDevice device, int touIndicator, BillableField kwField,
            BillableField kvarhField, boolean includeKwh) {

        // Only write the row if kwh is included OR there is a value for kw or
        // kvarh
        if ((includeKwh && device.getCalculatedValue(BillableField.totalConsumption) != null)
                || device.getValue(kwField) != null
                || device.getValue(Channel.ONE, ReadingType.KVAR, kvarhField) != null) {

            StringBuffer writeToFile = new StringBuffer();

            // Total kWh, Rate A kw and kvarh
            addToStringBuffer(writeToFile, RECORD_INDICATOR, true);

            addToStringBufferWithTrailingFiller(writeToFile,
                                                device.getData(BillableField.meterNumber),
                                                10,
                                                " ",
                                                true);

            // TOU indicator
            writeToFile.append(touIndicator + ",");

            // Total kWh
            if (includeKwh) {
                addToStringBufferWithPrecedingFiller(writeToFile,
                                                     format(device.getCalculatedValue(BillableField.totalConsumption),
                                                            FORMAT_NODECIMAL),
                                                     5,
                                                     " ",
                                                     true);
            } else {
                writeToFile.append("     ,");
            }
            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(BillableField.totalConsumption),
                                     TIME_FORMAT),
                              true);
            addToStringBuffer(writeToFile,
                              format(device.getTimestamp(BillableField.totalConsumption),
                                     DATE_FORMAT),
                              true);

            // Peak kW
            addToStringBufferWithPrecedingFiller(writeToFile,
                                                 format(device.getValue(kwField),
                                                        DECIMAL_FORMAT_3v2),
                                                 7,
                                                 " ",
                                                 true);
            addToStringBuffer(writeToFile, format(device.getTimestamp(kwField), TIME_FORMAT), true);
            addToStringBuffer(writeToFile, format(device.getTimestamp(kwField), DATE_FORMAT), true);

            // Total kVarh
            addToStringBufferWithPrecedingFiller(writeToFile,
                                                 format(device.getValue(Channel.ONE,
                                                                        ReadingType.KVAR,
                                                                        kvarhField),
                                                        KVARH_FORMAT_NODECIMAL),
                                                 6,
                                                 " ",
                                                 false);

            writeToFile.append("\r\n");

            return writeToFile.toString();

        } else {
            return "";
        }

    }
}

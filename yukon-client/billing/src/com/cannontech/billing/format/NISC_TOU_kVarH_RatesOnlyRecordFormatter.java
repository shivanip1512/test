package com.cannontech.billing.format;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;

/**
 * This format is the same as the NISC_TOU_kVarH format with the exception that the TotalConsumption 
 *  point is NOT used (unless no rates exist).  
 * Instead, this format looks FIRST for the Rates (A,B,C,D) to have data on them. 
 * If None of Rates A,B,C,D return data, THEN the Total Consumption, TotalDemand will be returned (if they exist)
 * 
 * Class used to create a billing file with row format as follows:
 * <pre>
 *                       i,mmmmmmmmmm,t,kkkkk,HH:MM,MM/dd/yyyy,wwww.ww,HH:MM,MM/dd/yyyy,vvvvvv 
 *                       i - record indicator 
 *                       m - meter number
 *                       t - register (A=2, B=2, C=3, D=4)  ***Total=1 when no rates present
 *                       k - rate kWh reading for register (followed by rate kWh time and date)
 *                       w - rate peak kW reading for register (followed by peak kW time and date)
 *                       v - rate kVarh reading
 * </pre>
 */
public class NISC_TOU_kVarH_RatesOnlyRecordFormatter extends BillingFormatterBase {

    private static final String HEADER = "H    Meter   TOU kWh  Time   Date      Peak   PeakT   PeakD\r\n";

    private static final DecimalFormat KVARH_FORMAT_NODECIMAL = new DecimalFormat("######");

    private static final String RECORD_INDICATOR = "M";

    public NISC_TOU_kVarH_RatesOnlyRecordFormatter() {
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
        boolean useTotal = true;
        // Rate A kWh, Rate A kw and kvarh
        rowData = createRow(device,
                            1,
                            BillableField.rateADemand,
                            BillableField.rateAConsumption);
        
        if (StringUtils.isNotBlank(rowData)) {
            writeToFile.append(rowData);
            useTotal = false;
        }

        // Rate B kWh, Rate B kw and kvarh
        rowData = createRow(device,
                            2,
                            BillableField.rateBDemand,
                            BillableField.rateBConsumption);

        if (StringUtils.isNotBlank(rowData)) {
            writeToFile.append(rowData);
            useTotal = false;
        }


        // Rate C kWh, Rate C kw and kvarh
        rowData = createRow(device,
                            3,
                            BillableField.rateCDemand,
                            BillableField.rateCConsumption);

        if (StringUtils.isNotBlank(rowData)) {
            writeToFile.append(rowData);
            useTotal = false;
        }

        // Rate D kWh, Rate D kw and kvarh
        rowData = createRow(device,
                            4,
                            BillableField.rateDDemand,
                            BillableField.rateDConsumption);
        
        if (StringUtils.isNotBlank(rowData)) {
            writeToFile.append(rowData);
            useTotal = false;
        }

        //If no data comes back, try to get the totalConsumption, totalDemand as register 1 instead
        if( useTotal) {
         // Total kWh, total Peak kw and kvarh
            rowData = createRow(device,
                                1,
                                BillableField.totalPeakDemand,
                                BillableField.totalConsumption);
            writeToFile.append(rowData);
        }
        
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
    private String createRow(BillableDevice device, int touIndicator, BillableField demand,
            BillableField consumption) {

        Double kWh = device.getValue(Channel.ONE, ReadingType.ELECTRIC, consumption);
        Double kW = device.getValue(Channel.ONE, ReadingType.ELECTRIC, demand);
        Double kVarh = device.getValue(Channel.ONE, ReadingType.KVAR, consumption);

        // Only write the row there is a value for at least one of the fields: kwh or kw or kvarh
        if ( kWh != null || kW != null || kVarh != null) {
            StringBuffer writeToFile = new StringBuffer();

            // M - recordIndicator
            addToStringBuffer(writeToFile, RECORD_INDICATOR, true);

            //MeterNumber
            addToStringBufferWithTrailingFiller(writeToFile,
                                                device.getData(Channel.ONE, ReadingType.DEVICE_DATA, BillableField.meterNumber),
                                                10,
                                                " ",
                                                true);

            // TOU indicator
            writeToFile.append(touIndicator + ",");

            // kWh
            addToStringBufferWithPrecedingFiller(writeToFile,
                                                     format(kWh, FORMAT_NODECIMAL),
                                                     5,
                                                     " ",
                                                     true);
            Timestamp consumptionTimestamp = device.getTimestamp(Channel.ONE, ReadingType.ELECTRIC, consumption);
            addToStringBuffer(writeToFile,
                              format(consumptionTimestamp, TIME_FORMAT),
                              true);
            addToStringBuffer(writeToFile,
                              format(consumptionTimestamp, DATE_FORMAT),
                              true);

            // Peak kW
            addToStringBufferWithPrecedingFiller(writeToFile,
                                                 format(kW, DECIMAL_FORMAT_3v2),
                                                 7,
                                                 " ",
                                                 true);
            Timestamp demandTimestamp = device.getTimestamp(Channel.ONE, ReadingType.ELECTRIC, demand);
            addToStringBuffer(writeToFile, format(demandTimestamp, TIME_FORMAT), true);
            addToStringBuffer(writeToFile, format(demandTimestamp, DATE_FORMAT), true);

            // Total kVarh
            addToStringBufferWithPrecedingFiller(writeToFile,
                                                 format(kVarh, KVARH_FORMAT_NODECIMAL),
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

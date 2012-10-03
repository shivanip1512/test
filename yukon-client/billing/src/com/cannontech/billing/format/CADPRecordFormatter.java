package com.cannontech.billing.format;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.impl.YukonSettingsDaoImpl;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *                                          
 *   iiiiippttMMddyybbbaaaaaaaaaacccccpppp.pppvvvv.vvvnnnnffff
 *                                             
 *   i - coopid
 *   p - page center
 *   t - tran number
 *   MMddyy - batch date
 *   b - batch number
 *   a - acount number (or paoName if no account number) 
 *   c - total consumption
 *   p - total peak demand
 *   v - total kVar demand
 *   n - page number
 *   f - filler spaces
 * </pre>
 * 
 * Note: sections a, c, p and v will be repeated up to 8 times per row - there
 * can be reading data for up to 8 devices on each billing row
 */
public class CADPRecordFormatter extends BillingFormatterBase {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddyy");

    private static final String PAGE_CENTER = "99";
    private static final String TRAN_NUMBER = "062";

    private static final String BATCH_DATE = DATE_FORMAT.format(new Date());
    private static final String BATCH_NUMBER = "999";
    private static final String FILLER = "    ";

    private String coopId = null;

    @Override
    public String dataToString(BillableDevice device) {

        boolean noRecord = true;

        StringBuffer writeToFile = new StringBuffer();

        // Account number
        String accountNumber = device.getData(ReadingType.DEVICE_DATA, BillableField.accountNumber);
        if (accountNumber == null) {
            accountNumber = device.getData(ReadingType.DEVICE_DATA, BillableField.paoName);
        }
        if (accountNumber != null && accountNumber.length() > 10) {
            accountNumber = accountNumber.substring(0, 10);
        }
        addToStringBufferWithPrecedingFiller(writeToFile, accountNumber, 10, "0", false);

        // Total Consumption
        Double totalConsumption = device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption);
        if (totalConsumption == null) {
            totalConsumption = new Double(0);
        } else {
            noRecord = false;
        }
        addToStringBuffer(writeToFile, format(totalConsumption, DECIMAL_FORMAT5V0), false);

        // kW Total Demand
        Double totalDemand = device.getValue(ReadingType.ELECTRIC, BillableField.totalPeakDemand);
        String totalDemandString = "       ";
        if (totalDemand != null && totalDemand.doubleValue() != 0) {
            noRecord = false;

            totalDemandString = format(totalDemand, DECIMAL_FORMAT4V3);
            totalDemandString = totalDemandString.replaceAll("\\.", "");
        }
        addToStringBuffer(writeToFile, totalDemandString, false);

        // kVar Total Demand
        totalDemand = device.getValue(Channel.ONE, ReadingType.KVAR, BillableField.totalPeakDemand);
        totalDemandString = "     ";
        if (totalDemand != null && totalDemand.doubleValue() != 0.0) {
            noRecord = false;

            totalDemandString = format(totalDemand, DECIMAL_FORMAT5V0);
            totalDemandString = totalDemandString.replaceAll("\\.", "");
        }
        addToStringBuffer(writeToFile, totalDemandString, false);

        return (noRecord) ? "" : writeToFile.toString();

    }

    @Override
    public String getBillingFileDetailsString(List<BillableDevice> deviceList) {

        StringBuffer billingFileString = new StringBuffer();
        int count = 0;
        int pageNumber = 1;

        if (coopId == null) {
            coopId = getCoopId();
        }

        Iterator<BillableDevice> deviceListIter = deviceList.iterator();
        while (deviceListIter.hasNext()) {

            BillableDevice device = deviceListIter.next();
            String deviceData = dataToString(device);
            if (deviceData != "") {

                // This format groups meter readings into 8 readings per billing
                // file line. There is data before and after each grouping of 8
                // readings and that is what the following 2 'if' statments are
                // for
                if (count % 8 == 0) {

                    billingFileString.append(coopId);

                    billingFileString.append(PAGE_CENTER);

                    billingFileString.append(TRAN_NUMBER);

                    billingFileString.append(BATCH_DATE);

                    billingFileString.append(BATCH_NUMBER);

                }

                billingFileString.append(deviceData);
                count++;

                if (count % 8 == 0) {

                    String pageNumberString = String.valueOf(pageNumber);
                    addToStringBufferWithPrecedingFiller(billingFileString,
                                                         pageNumberString,
                                                         4,
                                                         "0",
                                                         false);
                    billingFileString.append(FILLER);

                    billingFileString.append("\r\n");
                }
            }
        }

        // Complete the last row
        if (count % 8 != 0) {
            for (int i = count; i % 8 != 0; i++) {
                billingFileString.append("000000000000000            ");
            }
            String pageNumberString = String.valueOf(pageNumber);
            addToStringBufferWithPrecedingFiller(billingFileString, pageNumberString, 4, "0", false);
            billingFileString.append(FILLER);
            billingFileString.append("\r\n");
        }

        return billingFileString.toString();
    }

    /**
     * Method to get the CoopId from the client session or user if not in the
     * session
     * @return String representing the Coopid
     */
    public String getCoopId() {
        String coopId = YukonSpringHook.getBean("yukonSettingsDao",YukonSettingsDaoImpl.class).getSettingStringValue(YukonSetting.COOP_ID_CADP_ONLY);

        if (StringUtils.isBlank(coopId)) {
            CTILogger.info("No value set for Billing Configuration > Coop ID - CADP Only. A default value of '00000' will be used.");
            coopId = "00000";
        }
    
        if (coopId.length() > 5) {
            coopId = coopId.substring(0, 5);
            CTILogger.info("Coop Id value's length greater than 5 characters. Value will be trimmed to first 5 characters.");
        } else if (coopId.length() < 5) {
            for (int i = coopId.length(); i < 5; i++) {
                coopId += " ";
            }
            CTILogger.info("Coop Id value's length less than 5 characters. Spaces appended to end of value.");
        }
        return coopId;
    }

}

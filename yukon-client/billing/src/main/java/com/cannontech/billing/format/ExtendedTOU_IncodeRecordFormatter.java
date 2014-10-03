package com.cannontech.billing.format;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.clientutils.CTILogger;
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
public class ExtendedTOU_IncodeRecordFormatter extends ExtendedTOURecordFormatter {

    // Key=ReadingCode+Address String, Value=MeterNumber String
    private Map<String, String> cisMeterNumberMap;

    @Override
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();

        // Electric
        if (device.getData(Channel.ONE, ReadingType.ELECTRIC, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, ELECTRIC_CODE, Channel.ONE, ReadingType.ELECTRIC);

        }
        if (device.getData(Channel.TWO, ReadingType.ELECTRIC, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, ELECTRIC_CODE, Channel.TWO, ReadingType.ELECTRIC);

        }
        if (device.getData(Channel.THREE, ReadingType.ELECTRIC, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, ELECTRIC_CODE, Channel.THREE, ReadingType.ELECTRIC);

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
        if (device.getData(Channel.ONE, ReadingType.WATER, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, WATER_CODE, Channel.ONE, ReadingType.WATER);
        }
        if (device.getData(Channel.TWO, ReadingType.WATER, BillableField.totalConsumption) != null) {
            addFormattedLine(writeToFile, device, WATER_CODE + String.valueOf(Channel.TWO.getNumeric()), Channel.TWO,
                ReadingType.WATER);
        }
        if (device.getData(Channel.THREE, ReadingType.WATER, BillableField.totalConsumption) != null) {
            addFormattedLine(writeToFile, device, WATER_CODE + String.valueOf(Channel.THREE.getNumeric()),
                Channel.THREE, ReadingType.WATER);
        }

        // Gas
        if (device.getData(Channel.ONE, ReadingType.GAS, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, GAS_CODE, Channel.ONE, ReadingType.GAS);
        }
        if (device.getData(Channel.TWO, ReadingType.GAS, BillableField.totalConsumption) != null) {

            addFormattedLine(writeToFile, device, GAS_CODE + String.valueOf(Channel.TWO.getNumeric()), Channel.TWO,
                ReadingType.GAS);
        }
        if (device.getData(Channel.THREE, ReadingType.GAS, BillableField.totalConsumption) != null) {
            addFormattedLine(writeToFile, device, GAS_CODE + String.valueOf(Channel.THREE.getNumeric()), Channel.THREE,
                ReadingType.GAS);
        }

        return writeToFile.toString();
    }

    @Override
    protected String getMeterNumber(BillableDevice device, String code) {
        String key = buildKey(code, device.getData(ReadingType.DEVICE_DATA, BillableField.address));
        String customMeterNumber = getCISMeterNumbers().get(key);
        return (customMeterNumber != null ? customMeterNumber : super.getMeterNumber(device, code));
    }

    /**
     * Returns a HashMap of buildKey(readingCode, address) as key and meterNumber as value.
     * 
     * @return java.util.Hashtable
     */
    private void retrieveCISMeterNumbers() {
        cisMeterNumberMap = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(getBillingFileDefaults().getInputFileDir());
            BufferedReader readBuffer = new BufferedReader(fileReader);

            try {
                String line = readBuffer.readLine();
                while (line != null) {
                    StringTokenizer tokenizer = new StringTokenizer(line.trim(), ",");
                    if (tokenizer.countTokens() >= 3) {
                        String readingCode = tokenizer.nextToken().trim();
                        String address = tokenizer.nextToken().trim();
                        String key = buildKey(readingCode, address);
                        String meterNumber = tokenizer.nextToken().trim();
                        cisMeterNumberMap.put(key, meterNumber);
                    }
                    line = readBuffer.readLine();
                }
            } catch (IOException ioe) {
                CTILogger.error(ioe);
            }
        } catch (FileNotFoundException fnfe) {
            CTILogger.info("Cannot find " + getBillingFileDefaults().getInputFileDir()
                + ".  Please create file (if applicable) and regenerate file.");
            CTILogger.info("Default MeterNumbers stored in database will be used.");
        }
    }

    public Map<String, String> getCISMeterNumbers() {
        if (cisMeterNumberMap == null) {
            retrieveCISMeterNumbers();
        }
        return cisMeterNumberMap;
    }

    private String buildKey(String readingCode, String address) {
        return readingCode + address;
    }
}

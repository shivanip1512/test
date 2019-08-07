package com.cannontech.web.support.waterNode.csvDao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.assetavailability.service.impl.AssetAvailabilityServiceImpl;
import com.cannontech.web.support.waterNode.csvDao.WaterNodeCSVDao;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.voltageDetails.VoltageDetails;
import com.opencsv.CSVReader;

public class WaterNodeCSVDaoImpl implements WaterNodeCSVDao {
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityServiceImpl.class);

    @Override
    public List<WaterNodeDetails> getWaterNodeDetails(Instant startTime, Instant stopTime, File file) {
        CSVReader reader;
        ArrayList<WaterNodeDetails> resultsList = new ArrayList<WaterNodeDetails>();
        WaterNodeDetails waterNodeDetails = new WaterNodeDetails();
        
        //Java millisecond time and second time are both stored as type long. Current millisecond time
        //is 1000 as large as current second time. To determine if the input is second or millisecond
        //time, a sentinel value of 10* the current epoch second time is used. This date is September 19,
        //2462
        long MILLISBOUNDARY =  155486880000L;
        try {
            reader = new CSVReader(new BufferedReader(new FileReader(file)));
            String[] row;
            // headers
            row = reader.readNext();
            String oldSN = StringUtils.EMPTY;
            int rowLengthErrors = 0;

            while ((row = reader.readNext()) != null) {
                if (row.length == 6) {
                    Long newTimestamp = Long.valueOf(row[4]);
                    if (newTimestamp<MILLISBOUNDARY)
                    {
                        newTimestamp = newTimestamp*1000;//convert from seconds to milliseconds
                    }
                    Instant currentTimestamp = new Instant(newTimestamp);
                    if (currentTimestamp.isAfter(startTime) && currentTimestamp.isBefore(stopTime)) {
                        // Set a meter's serial number and details once for each set of voltage readings.
                        if (!row[0].equals(oldSN)) {

                            // Serial number is only null for the empty placeholder needed to initialize
                            // the loop. This prevents an empty item at the beginning of resultsList
                            if (waterNodeDetails.getSerialNumber() != null) {
                                resultsList.add(waterNodeDetails);
                            }
                            waterNodeDetails = new WaterNodeDetails();
                            waterNodeDetails.setSerialNumber(row[0]);
                            waterNodeDetails.setMeterNumber(String.valueOf(row[1]));
                            waterNodeDetails.setName(row[2]);
                            waterNodeDetails.setType(row[3]);
                            waterNodeDetails.setHighSleepingCurrentIndicator(false);
                            waterNodeDetails.setBatteryLevel(null);
                        }
                        
                        
                        Double newVoltage = Double.valueOf(row[5]);
                        if (newVoltage>1000)
                        {
                            newVoltage = newVoltage/1000.0;//convert from millivolts to volts
                        }
                        
                        waterNodeDetails.addTimestamp(new Instant(newTimestamp));
                        waterNodeDetails.addVoltage(newVoltage);
                        oldSN = row[0];
                    }
                } else {
                    rowLengthErrors++;
                }
            }
            if (rowLengthErrors > 0) {
                log.error("Error in file parsing: " + rowLengthErrors + " unexpected added or missing columns present");
            }
            resultsList.add(waterNodeDetails);// add final meter to resultsList
            reader.close();
        } catch (IOException e) {
            log.error("Unable to read CSV file input", e);
        }
        return resultsList;
    }

    @Override
    public List<VoltageDetails> getVoltageData(Instant startTime, Instant stopTime) {
        return null;
    }

}

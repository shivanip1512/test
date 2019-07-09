package com.cannontech.web.support.waterNode.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import java.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.assetavailability.service.impl.AssetAvailabilityServiceImpl;
import com.cannontech.web.support.waterNode.dao.WaterNodeDao;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.opencsv.CSVReader;

public class WaterNodeCSVDaoImpl implements WaterNodeDao {
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityServiceImpl.class);

    @Override
    public List<WaterNodeDetails> getWaterNodeDetails(Instant startTime, Instant stopTime) {

        File file = new File("C:\\Users\\e0535626\\Desktop", "data.csv");
        CSVReader reader;
        ArrayList<WaterNodeDetails> resultsList = new ArrayList<WaterNodeDetails>();
        WaterNodeDetails waterNodeDetails = new WaterNodeDetails();
        try {
            reader = new CSVReader(new BufferedReader(new FileReader(file)));
            String[] row;
            // headers
            row = reader.readNext();
            String oldSN = "";

            while ((row = reader.readNext()) != null) {
                Instant currentTimestamp = Instant.ofEpochMilli(Long.valueOf(row[4]));
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
                        waterNodeDetails.setMeterNumber(Integer.valueOf(row[1]));
                        waterNodeDetails.setName(row[2]);
                        waterNodeDetails.setType(row[3]);
                        waterNodeDetails.setHighSleepingCurrentIndicator(false);
                        waterNodeDetails.setBatteryLevel(null);
                    }
                    waterNodeDetails.addTimestamp(Instant.ofEpochMilli(Long.valueOf(row[4])));
                    waterNodeDetails.addVoltage(Integer.valueOf(row[5]));
                    oldSN = row[0];
                }
            }
            resultsList.add(waterNodeDetails);// add final meter to resultsList
            reader.close();

        } catch (IOException e) {
            log.error("Unable to read CSV file input", e);
        }
        return resultsList;
    }

}

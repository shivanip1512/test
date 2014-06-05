package com.cannontech.web.dr.ecobee.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.web.dr.ecobee.service.DataDownloadService;
import com.google.common.collect.Lists;

public class DataDownloadServiceImpl implements DataDownloadService {
    
    Logger log = YukonLogManager.getLogger(DataDownloadServiceImpl.class);
    
    @Autowired private EcobeeCommunicationService commService;
    @Autowired @Qualifier("longRunning") private Executor executor;
    @Autowired @Qualifier("ecobeeReads") private RecentResultsCache<EcobeeReadResult> readResultsCache;

    @Override
    public String start(final List<String> serialNumbers, final Range<Instant> dateRange) throws IOException {
        
        final File file = File.createTempFile("ecobee_data_" + Instant.now().getMillis(), ".csv");
        file.deleteOnExit();
        
        final EcobeeReadResult result = new EcobeeReadResult(serialNumbers.size(), file);
        String resultKey = readResultsCache.addResult(result);
        result.setKey(resultKey);
        
        Runnable task = new Runnable() {
            
            @Override
            public void run() {
                
                try (FileWriter output = new FileWriter(file)) {
                    
                    DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                    String headerFormat = "%s,%s,%s,%s,%s,%s,%s,%s\n";
                    String dataFormat = "%s,%s,%s,%s,%s,%s,%d,%s\n";
                    output.write(String.format(headerFormat, "Serial Number", 
                            "Date", 
                            "Outdoor Temp",
                            "Indoor Temp", 
                            "Set Cool Temp", 
                            "Set Heat Temp", 
                            "Runtime Seconds", 
                            "Event Activity"));
                    
                    // readDeviceData should only be sent 25 serial numbers at a time
                    for (List<String> batch : Lists.partition(serialNumbers, 25)) {
                        
                        List<EcobeeDeviceReadings> batchedReads = new ArrayList<>();
                        try {
                            batchedReads = commService.readDeviceData(batch, dateRange);
                        } catch (EcobeeCommunicationException e) {
                            // TODO Add retry mechanism
                            log.error("Unable to retreive data from ecobee service.", e);
                            result.setComplete(true);
                            result.setSuccessful(false);
                            break;
                        }
                        
                        for (EcobeeDeviceReadings deviceReadings : batchedReads) {
                            
                            for (EcobeeDeviceReading deviceReading : deviceReadings.getReadings()) {
                                
                                String dateStr = timeFormatter.print(deviceReading.getDate());
                                int runtimeSeconds = deviceReading.getRuntimeSeconds();
                                if (0 > runtimeSeconds) {
                                    log.debug("runtimeSeconds=" + runtimeSeconds + ", converting to absolute value");
                                    runtimeSeconds = Math.abs(runtimeSeconds);
                                }
                                
                                String dataRow = String.format(dataFormat,
                                    deviceReadings.getSerialNumber(),
                                    dateStr,
                                    formatNullable(deviceReading.getOutdoorTempInF()),
                                    formatNullable(deviceReading.getIndoorTempInF()),
                                    formatNullable(deviceReading.getSetCoolTempInF()),
                                    formatNullable(deviceReading.getSetHeatTempInF()),
                                    runtimeSeconds,
                                    deviceReading.getEventActivity());
                                
                                output.write(dataRow);
                            }
                            result.addCompleted(batch.size());
                        }
                    }
                    
                    result.setSuccessful(true);
                    
                } catch (IOException e) {
                    log.error("Unable to write ecobee data file.", e);
                    result.setComplete(true);
                    result.setSuccessful(false);
                }
            }
        };
        
        executor.execute(task);
        
        return resultKey;
    }
    
    private static String formatNullable(Float num) {
        return num == null ? "" : new DecimalFormat("#.#").format(num);
    }
    
}

package com.cannontech.web.dr.ecobee.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.ecobee.service.DataDownloadService;

public class DataDownloadServiceImpl implements DataDownloadService {
    
    Logger log = YukonLogManager.getLogger(DataDownloadServiceImpl.class);
    
    @Autowired private EcobeeCommunicationService commService;
    @Autowired @Qualifier("longRunning") private Executor executor;
    @Autowired @Qualifier("ecobeeReads") private RecentResultsCache<EcobeeReadResult> readResultsCache;
    @Autowired private DateFormattingService dateFormattingService;

    @Override
    public String start(final List<String> serialNumbers, final Range<LocalDate> dateRange, final YukonUserContext userContext) throws IOException {
        
        final File file = File.createTempFile("ecobee_data_" + Instant.now().getMillis(), ".csv");
        file.deleteOnExit();
        
        final EcobeeReadResult result = new EcobeeReadResult(serialNumbers.size(), file, dateRange);
        String resultKey = readResultsCache.addResult(result);
        result.setKey(resultKey);
        
        Runnable task = new Runnable() {
            
            @Override
            public void run() {
                runTask(serialNumbers, dateRange, userContext, file, result);
            }
        };
        
        executor.execute(task);
        
        return resultKey;
    }
    
    private void runTask(List<String> serialNumbers, Range<LocalDate> dateRange, YukonUserContext userContext, File file, 
                         EcobeeReadResult result) {
        try (FileWriter output = new FileWriter(file)) {
            
            String format = "%s,%s,%s,%s,%s,%s,%s,%s\n";
            output.write(String.format(format, "Serial Number", 
                    "Date", 
                    "Outdoor Temp",
                    "Indoor Temp", 
                    "Set Cool Temp", 
                    "Set Heat Temp", 
                    "Runtime Seconds", 
                    "Event Activity"));

            List<EcobeeDeviceReadings> allDeviceReadings = new ArrayList<>();
            try {
                allDeviceReadings = commService.readDeviceData(SelectionType.THERMOSTATS, serialNumbers, dateRange);
            } catch (EcobeeCommunicationException e) {
                // TODO Add retry mechanism
                log.error("Unable to retreive data from ecobee service.", e);
                result.setComplete();
                result.setSuccessful(false);
            }

            for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {

                for (EcobeeDeviceReading deviceReading : deviceReadings.getReadings()) {
                    String deviceReadingDate;
                    if (dateFormattingService != null) {
                        deviceReadingDate = dateFormattingService.format(deviceReading.getDate(),
                                DateFormatEnum.FULL, userContext);
                    } else {
                        deviceReadingDate = deviceReading.getDate().toString();
                    }

                    Integer runtimeSeconds = deviceReading.getRuntimeSeconds();
                    if (runtimeSeconds != null && 0 > runtimeSeconds) {
                        log.debug("runtimeSeconds=" + runtimeSeconds + ", converting to absolute value");
                        runtimeSeconds = Math.abs(runtimeSeconds);
                    }

                    String dataRow = String.format(format, deviceReadings.getSerialNumber(), deviceReadingDate,
                        formatNullable(deviceReading.getOutdoorTempInF()),
                        formatNullable(deviceReading.getIndoorTempInF()),
                        formatNullable(deviceReading.getSetCoolTempInF()),
                        formatNullable(deviceReading.getSetHeatTempInF()), formatNullable(runtimeSeconds),
                        deviceReading.getEventActivity());

                    output.write(dataRow);
                }
                result.incrementCompleted();
            }

            result.setSuccessful(true);
            result.setComplete();

        } catch (IOException e) {
            log.error("Unable to write ecobee data file.", e);
            result.setComplete();
            result.setSuccessful(false);
        }
    }
    
    private static String formatNullable(Float num) {
        return num == null ? "" : new DecimalFormat("#.#").format(num);
    }
    
    private static String formatNullable(Integer num) {
        return num == null ? "" : num.toString();
    }
}

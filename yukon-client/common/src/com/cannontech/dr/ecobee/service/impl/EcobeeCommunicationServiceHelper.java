package com.cannontech.dr.ecobee.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.google.common.collect.Iterables;
import com.opencsv.CSVReader;

public class EcobeeCommunicationServiceHelper {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationServiceHelper.class);

    List<EcobeeDeviceReadings> getEcobeeDeviceReadings(List<File> csvFiles) {
        List<EcobeeDeviceReadings> ecobeeDeviceReadings = new ArrayList<>();
        EcobeeDeviceReadings deviceReadings = null;

        for (File file : csvFiles) {
            try (FileInputStream inputStream = new FileInputStream(file);
                 BOMInputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                         ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
                 InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream)) {
                CSVReader csvReader = new CSVReader(inputStreamReader);
                Iterator<String[]> iterator = csvReader.readAll().iterator();
                List<EcobeeDeviceReading> readings = getEcobeeDeviceReading(iterator);
                String serialNumber = getSerialNumber(file.getName());
                deviceReadings = new EcobeeDeviceReadings(serialNumber, readings);
                CtiUtilities.close(iterator, csvReader);
            } catch (Exception e) {
                log.error("Error while processing runtimereport data.", e);
                throw new EcobeeCommunicationException("Error occured while processing runtimereport data.");
            }
            file.delete();
            ecobeeDeviceReadings.add(deviceReadings);
        }
        return ecobeeDeviceReadings;
    }

    private String getSerialNumber(String fileName) {
        String serialNumber = fileName.substring(0, fileName.indexOf('-'));
        log.debug("Received runtime reports for Thermostat : " + serialNumber + "in file name"
            + fileName.substring(0, fileName.indexOf(".csv") + 4));
        return serialNumber;
    }

    private List<EcobeeDeviceReading> getEcobeeDeviceReading(Iterator<String[]> iterator) throws IOException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        List<EcobeeDeviceReading> readings = new ArrayList<EcobeeDeviceReading>();

        Map<String, Integer> headerMap = buildHeaderIndex(iterator.next());
        while (iterator.hasNext()) {
            String[] thermostatData = iterator.next();
            Instant date = new DateTime(formatter.parseDateTime(thermostatData[0] + StringUtils.SPACE + thermostatData[1])).toInstant();

            String eventActivity = thermostatData[headerMap.get("zoneCalendarEvent")];
            Float indoorTempInF = parseFloatData(thermostatData[headerMap.get("zoneAveTemp")]);
            Float outdoorTempInF = parseFloatData(thermostatData[headerMap.get("outdoorTemp")]);
            Float setCoolTempInF = parseFloatData(thermostatData[headerMap.get("zoneCoolTemp")]);
            Float setHeatTempInF = parseFloatData(thermostatData[headerMap.get("zoneHeatTemp")]);
            Integer compCool1 = parseIntegerData(thermostatData[headerMap.get("compCool1")]);
            Integer compHeat1 = parseIntegerData(thermostatData[headerMap.get("compHeat1")]);

            Integer runtime = getRuntime(compCool1, compHeat1);

            EcobeeDeviceReading deviceReading = new EcobeeDeviceReading(outdoorTempInF, indoorTempInF, setCoolTempInF,
                setHeatTempInF, runtime, eventActivity, date);
            readings.add(deviceReading);
        }
        return readings;

    }
    
    private Integer parseIntegerData(String value) {
        return StringUtils.isEmpty(value) ? null : Integer.parseInt(value);
    }

    private Float parseFloatData(String value) {
        return StringUtils.isEmpty(value) ? null : Float.parseFloat(value);
    }

    private Integer getRuntime(Integer setCompCool, Integer setCompHeat) {
        Integer runtime;
        // Add the values if they're both non-null
        if (setCompCool != null && setCompHeat != null) {
            runtime = setCompCool + setCompHeat;
            // If only one is non-null, use that value. Otherwise return null.
        } else if (setCompCool == null) {
            runtime = setCompHeat;
        } else {
            runtime = setCompCool;
        }

        return runtime;

    }
    
    private Map<String, Integer> buildHeaderIndex(String[] headers) {
        Map<String, Integer> headerMap = new HashMap<String, Integer>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            if (EcobeeCommunicationService.deviceReadColumns.contains(header)) {
                headerMap.put(header, i);
            }
        }
        return headerMap;
    }

    public void logMissingSerialNumber(List<EcobeeDeviceReadings> deviceReadings, Collection<String> selectionMatch) {

        if (CollectionUtils.isNotEmpty(deviceReadings)) {
            Set<String> missingSerialNumbers = new HashSet<>(selectionMatch);
            List<String> serialNumbers =
                deviceReadings.stream().map(reading -> reading.getSerialNumber()).collect(Collectors.toList());
            Iterables.removeAll(missingSerialNumbers, serialNumbers);
            for (String missingSerialNumber : missingSerialNumbers) {
                log.error("Unable to read Serial number: " + missingSerialNumber
                    + ". It may have been removed from ecobee's system.");
            }
        }

    }
    
    public String getDecryptedFileName(String url) {
        
        String decryptedFileName = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
        log.info("Encrypted file : " + url.substring(url.lastIndexOf('/') + 1, url.length())
            + "received for Job ID : " + decryptedFileName.substring(0, decryptedFileName.indexOf("-")));
        return decryptedFileName;
    }

}

package com.cannontech.web.dr.ecobee.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.EcobeeDeviceDoesNotExistException;
import com.cannontech.dr.ecobee.EcobeeSetDoesNotExistException;
import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;

public class DataDownloadServiceImplTest {
    private DataDownloadServiceImpl service;
    
    private final DateTimeZone timeZone = DateTimeZone.forID("America/Chicago");
    
    @Before
    public void setUp() {
        service = new DataDownloadServiceImpl();
        EcobeeCommunicationService ecobeeCommunicationService = new MockEcobeeCommunicationService(timeZone);
        ReflectionTestUtils.setField(service, "commService", ecobeeCommunicationService);
    }
    
    @Test
    public void test_runTask_withDevicesForOneMessage() throws Exception {
        test_runTask(getSerialNumbers(25));
    }
    
    @Test
    public void test_runTask_withDevicesForTwoMessages() throws Exception {
        test_runTask(getSerialNumbers(26));
    }
    
    @Test
    public void test_runTask_withDevicesForTenMessages() throws Exception {
        test_runTask(getSerialNumbers(247));
    }
    
    private void test_runTask(List<String> serialNumbers) throws Exception {
        Instant start = new Instant(1339529000); //about 3/8/2015
        Instant end = new Instant(1425929000); 
        Range<Instant> dateRange = new Range<>(start, true, end, true);
        
        File file = File.createTempFile("data_download_test" + Instant.now().getMillis(), ".csv");
        
        EcobeeReadResult result = new EcobeeReadResult(serialNumbers.size(), file, dateRange);
        
        //call DataDownloadService.runTask(serialNumbers, dateRange, timeZone, file, result)
        ReflectionTestUtils.invokeMethod(service, "runTask", serialNumbers, dateRange, timeZone, file, result);
        
        Assert.assertEquals("Incorrect device total.", serialNumbers.size(), result.getTotal());
        Assert.assertTrue("Incorrect success value.", result.isSuccessful());
        Assert.assertEquals("Incorrect number of complete devices.", serialNumbers.size(), result.getCompleted());
        Assert.assertEquals("Incorrect completion value.", true, result.isComplete());
        Assert.assertEquals("Incorrect percentage.", "100%", result.getPercentDone());
    }
    
    private List<String> getSerialNumbers(int amount) {
        List<String> serialNumbers = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            serialNumbers.add(Integer.toString(i));
        }
        return serialNumbers;
    }
    
    private class MockEcobeeCommunicationService implements EcobeeCommunicationService {
        private DateTimeZone timeZone;
        
        public MockEcobeeCommunicationService(DateTimeZone timeZone) {
            this.timeZone = timeZone;
        }
        
        @Override
        public List<EcobeeDeviceReadings> readDeviceData(Collection<String> serialNumbers, Range<Instant> dateRange) {
            
            List<RuntimeReport> reports = new ArrayList<>();
            List<EcobeeDeviceReadings> deviceReadings = new ArrayList<>();

            for (String serialNumber : serialNumbers) {
                List<RuntimeReportRow> reportRows = new ArrayList<>();
                List<EcobeeDeviceReading> expectedReadings = new ArrayList<>();

                LocalDateTime thermostatStartTime = dateRange.getMin().toDateTime().withZone(timeZone).toLocalDateTime();
                LocalDateTime thermostatEndTime = dateRange.getMax().toDateTime().withZone(timeZone).toLocalDateTime();

                for (LocalDateTime reportTime = thermostatStartTime; reportTime.isBefore(thermostatEndTime);
                        reportTime = reportTime.plusMinutes(5)) {
                    RuntimeReportRow row = new RuntimeReportRow(reportTime, "", 75f, 95f, 75f, 75f, 0);
                    reportRows.add(row);
                    EcobeeDeviceReading reading = new EcobeeDeviceReading(row.getOutdoorTemp(), row.getIndoorTemp(),
                           row.getCoolSetPoint(), row.getHeatSetPoint(), row.getRuntime(), row.getEventName(),
                           reportTime.toDateTime(timeZone).toInstant());
                    expectedReadings.add(reading);
                }
                reports.add(new RuntimeReport(serialNumber, reportRows.size(), reportRows));
                deviceReadings.add(new EcobeeDeviceReadings(serialNumber, dateRange, expectedReadings));
            }
            
            return deviceReadings;
        }
        
        /* All other methods unimplemented */
        
        @Override
        public void registerDevice(String serialNumber) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public boolean moveDeviceToSet(String serialNumber, String setPath) throws EcobeeDeviceDoesNotExistException,
                EcobeeSetDoesNotExistException {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public boolean createManagementSet(String managementSetName) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public boolean deleteManagementSet(String managementSetName) throws EcobeeSetDoesNotExistException {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public boolean moveManagementSet(String currentPath, String newParentPath) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public boolean sendRestore(String drIdentifier) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public List<SetNode> getHierarchy() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public void deleteDevice(String serialNumber) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        @Override
        public void sendOverrideControl(String serialNumber) {
            throw new UnsupportedOperationException("Method not implemented.");
        }
    }
}

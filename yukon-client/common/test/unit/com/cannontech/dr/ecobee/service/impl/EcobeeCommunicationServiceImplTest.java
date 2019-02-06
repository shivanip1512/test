package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.message.DeviceDataResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportRequest;
import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class EcobeeCommunicationServiceImplTest {

    private final static DateTimeFormatter dateTimeFormatter =
            DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZoneUTC();

    private static final List<String> deviceReadColumns = ImmutableList.of("zoneCalendarEvent", "zoneAveTemp",
            "outdoorTemp", "zoneCoolTemp", "zoneHeatTemp", "compCool1", "compHeat1");

    private EcobeeCommunicationServiceImpl impl;

    @Before
    public void setup() throws Exception {
        GlobalSettingDao mockGlobalSettingDao = EasyMock.createMock(GlobalSettingDao.class);
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL)).andReturn("");

        EcobeeQueryCountDao mockEcobeeQueryCountDao = EasyMock.createMock(EcobeeQueryCountDao.class);
        mockEcobeeQueryCountDao.incrementQueryCount(EasyMock.anyObject(EcobeeQueryType.class));
        EasyMock.expectLastCall();

        impl = new EcobeeCommunicationServiceImpl();
        ReflectionTestUtils.setField(impl, "settingDao", mockGlobalSettingDao);
        ReflectionTestUtils.setField(impl, "ecobeeQueryCountDao", mockEcobeeQueryCountDao);
    }

    @Test
    public void test_readDeviceData_someSerialNumbersMissing() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(7, 15));
        testCase.setSerialNumbers("2", "3", "4", "5", "6");
        testCase.setSerialNumbersNotRegistered("5", "6");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("5/21/2014 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("5/28/2014 11:00:00"));

        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);

        List<EcobeeDeviceReadings> deviceReadings = 
                impl.readDeviceData(testCase.getSerialNumbers(), testCase.getDateRange());
        AssertEqual(deviceReadings, expectedResponse);
    }

    @Test
    public void test_readDeviceData_oldDate() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.UTC);
        testCase.setSerialNumbers("1");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("1/21/1990 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("5/28/1990 11:00:00"));

        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);

        List<EcobeeDeviceReadings> deviceReadings = 
                impl.readDeviceData(testCase.getSerialNumbers(), testCase.getDateRange());
        AssertEqual(deviceReadings, expectedResponse);
    }

    @Test
    public void test_readDeviceData_noDataReturned() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(2, 30));
        testCase.setSerialNumbers("1");
        testCase.setSerialNumbersNotRegistered("1");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("5/21/2014 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("5/28/2014 11:00:00"));

        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);

        List<EcobeeDeviceReadings> deviceReadings = 
                impl.readDeviceData(testCase.getSerialNumbers(), testCase.getDateRange());
        AssertEqual(deviceReadings, expectedResponse);
    }

    @Test
    public void test_readDeviceData_dateWithMinutesAndSeconds() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(2, 30));
        testCase.setSerialNumbers("1");
        testCase.setSerialNumbersNotRegistered("1");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("1/21/2014 18:37:12"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("1/28/2014 12:19:13"));

        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);

        List<EcobeeDeviceReadings> deviceReadings = 
                impl.readDeviceData(testCase.getSerialNumbers(), testCase.getDateRange());
        AssertEqual(deviceReadings, expectedResponse);
    }
    
    @Test
    public void test_readDeviceData_enoughDevicesForTwoMessages() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(2, 30));
        String[] serialNumbers = getEnoughSerialNumbersForTwoMessages();
        testCase.setSerialNumbers(serialNumbers);
        testCase.setStartDate(dateTimeFormatter.parseDateTime("5/1/2014 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("5/2/2014 18:00:00"));
        
        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);
        List<EcobeeDeviceReadings> deviceReadings = 
                impl.readDeviceData(testCase.getSerialNumbers(), testCase.getDateRange());
        AssertEqual(deviceReadings, expectedResponse);
    }
    
    private String[] getEnoughSerialNumbersForTwoMessages() {
        String[] serialNumbers = new String[26];
        for (int i = 1; i <= 26; i++) {
            serialNumbers[i-1] = Integer.toString(i);
        }
        return serialNumbers;
    }
    
    private List<EcobeeDeviceReadings> setupMockForTestCase(TestCase testCase) throws Exception {
        RestTemplate restTemplateMock = EasyMock.createMock(RestTemplate.class);

        RuntimeReportRequest request = new RuntimeReportRequest(testCase.getDateRange().getMin(), 
                testCase.getDateRange().getMax(), testCase.getSerialNumbers(), deviceReadColumns);
        List<RuntimeReport> reports = new ArrayList<>();
        List<EcobeeDeviceReadings> expectedDeviceReadings = new ArrayList<>();

        Set<String> serialNumbers = new HashSet<>(testCase.getSerialNumbers());
        serialNumbers.removeAll(testCase.getSerialNumbersNotRegistered());
        for (String serialNumber : serialNumbers) {
            List<RuntimeReportRow> reportRows = new ArrayList<>();
            List<EcobeeDeviceReading> expectedReadings = new ArrayList<>();

            LocalDateTime thermostatStartTime = testCase.getStartDate().withZone(testCase.getTimeZone()).toLocalDateTime();
            LocalDateTime thermostatEndTime = testCase.getEndDate().withZone(testCase.getTimeZone()).toLocalDateTime();

            for (LocalDateTime reportTime = thermostatStartTime; reportTime.isBefore(thermostatEndTime);
                    reportTime = reportTime.plusMinutes(5)) {
                RuntimeReportRow row = new RuntimeReportRow(reportTime, "", 75f, 95f, 75f, 75f, 0);
                reportRows.add(row);
                EcobeeDeviceReading reading = new EcobeeDeviceReading(row.getOutdoorTemp(), row.getIndoorTemp(),
                       row.getCoolSetPoint(), row.getHeatSetPoint(), row.getRuntime(), row.getEventName(),
                    reportTime.toDateTime(testCase.getTimeZone()).toInstant(), null, 0f);
                expectedReadings.add(reading);
            }
            reports.add(new RuntimeReport(serialNumber, reportRows.size(), reportRows));
            expectedDeviceReadings.add(new EcobeeDeviceReadings(serialNumber, testCase.getDateRange(), expectedReadings));
        }

        DeviceDataResponse response = new DeviceDataResponse(new Status(0, ""), reports);
        setRequestResponse(restTemplateMock, request, response);

        EasyMock.replay(restTemplateMock);
        ReflectionTestUtils.setField(impl, "restTemplate", restTemplateMock);

        return expectedDeviceReadings;
    }

    private class TestCase {
        DateTimeZone timeZone;
        Set<String> serialNumbers;
        Set<String> serialNumbersNotRegistered = new HashSet<>();
        DateTime startDate;
        DateTime endDate;

        public void setTimeZone(DateTimeZone timeZone) {
            this.timeZone = timeZone;
        }

        public void setSerialNumbers(String... serialNumbers) {
            this.serialNumbers = Sets.newHashSet(serialNumbers);
        }

        public void setSerialNumbersNotRegistered(String... serialNumbersNotRegistered) {
            this.serialNumbersNotRegistered = Sets.newHashSet(serialNumbersNotRegistered);
        }

        public DateTimeZone getTimeZone() {
            return timeZone;
        }

        public Set<String> getSerialNumbers() {
            return serialNumbers;
        }

        public Set<String> getSerialNumbersNotRegistered() {
            return serialNumbersNotRegistered;
        }

        public DateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(DateTime startDate) {
            this.startDate = startDate;
        }

        public DateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(DateTime endDate) {
            this.endDate = endDate;
        }
        
        public Range<Instant> getDateRange() {
            return Range.inclusive(startDate.toInstant(), endDate.toInstant());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void setRequestResponse(RestTemplate mock, Object request, T response) throws Exception {
        EasyMock.expect(mock.exchange(EasyMock.anyObject(String.class), 
                  EasyMock.eq(HttpMethod.GET), EasyMock.anyObject(HttpEntity.class), 
                  (Class<T>)EasyMock.anyObject(Class.class), 
                  EasyMock.eq(Collections.singletonMap("bodyJson", JsonUtils.toJson(request)))))
              .andReturn(new ResponseEntity<>(response, HttpStatus.I_AM_A_TEAPOT));
    }

    private void AssertEqual(List<EcobeeDeviceReadings> allDeviceReadings1, List<EcobeeDeviceReadings> allDeviceReadings2) {
        Assert.assertNotNull(allDeviceReadings1);
        Assert.assertNotNull(allDeviceReadings2);

        Assert.assertTrue("Expected number of device readings is different from amount returned",
                          allDeviceReadings1.size() == allDeviceReadings2.size());
        for (int i=0; i < allDeviceReadings1.size(); i++) {
            EcobeeDeviceReadings deviceReadings1 = allDeviceReadings1.get(i);
            EcobeeDeviceReadings deviceReadings2 = allDeviceReadings2.get(i);

            Assert.assertEquals("Expected serial number doesn't match returned value",
                                deviceReadings1.getSerialNumber(), deviceReadings2.getSerialNumber());
            Assert.assertEquals("Expected date range doesn't match returned value",
                                deviceReadings1.getDateRange(), deviceReadings2.getDateRange());

            List<EcobeeDeviceReading> readings1 = deviceReadings1.getReadings();
            List<EcobeeDeviceReading> readings2 = deviceReadings2.getReadings();
            Assert.assertTrue("Different number of readings returned than was expected",
                              readings1.size() == readings2.size());
            for (int j=0; j < readings1.size(); j++) {
                EcobeeDeviceReading ecobeeDeviceReading1 = readings1.get(j);
                EcobeeDeviceReading ecobeeDeviceReading2 = readings2.get(j);
                Assert.assertEquals("Expected date doesn't match returned date",
                                    ecobeeDeviceReading1.getDate(), ecobeeDeviceReading2.getDate());
                Assert.assertEquals("Expected event activity string doesn't match returned string",
                                    ecobeeDeviceReading1.getEventActivity(), ecobeeDeviceReading2.getEventActivity());
                Assert.assertEquals("Expected indoor temp doesn't match returned indoor temp",
                                    ecobeeDeviceReading1.getIndoorTempInF(), ecobeeDeviceReading2.getIndoorTempInF());
                Assert.assertEquals("Expected outdoor temp doesn't match returned outdoor temp",
                                    ecobeeDeviceReading1.getOutdoorTempInF(), ecobeeDeviceReading2.getOutdoorTempInF());
                Assert.assertEquals("Expected runtime seconds doens't match returned runtime seconds",
                                    ecobeeDeviceReading1.getRuntimeSeconds(), ecobeeDeviceReading2.getRuntimeSeconds());
                Assert.assertEquals("Expected set cool temp doesn't match returned value",
                                    ecobeeDeviceReading1.getSetCoolTempInF(), ecobeeDeviceReading2.getSetCoolTempInF());
                Assert.assertEquals("Expected set heat temp doesn't match returned value",
                                    ecobeeDeviceReading1.getSetHeatTempInF(), ecobeeDeviceReading2.getSetHeatTempInF());
            }
        }
    }
}

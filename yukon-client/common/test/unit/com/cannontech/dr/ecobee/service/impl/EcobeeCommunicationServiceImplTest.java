package com.cannontech.dr.ecobee.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.message.EcobeeJobStatus;
import com.cannontech.dr.ecobee.message.EcobeeReportJob;
import com.cannontech.dr.ecobee.message.RuntimeReportJobResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportJobStatusRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobStatusResponse;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.encryption.EcobeeSecurityService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.csv.CSVWriter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class EcobeeCommunicationServiceImplTest {

    private final static DateTimeFormatter dateTimeFormatter =
        DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZoneUTC();

    private static final List<String> deviceReadColumns = ImmutableList.of("date", "time", "zoneCalendarEvent",
        "zoneAveTemp", "outdoorTemp", "zoneCoolTemp", "zoneHeatTemp", "compCool1", "compHeat1");

    private EcobeeCommunicationServiceImpl impl;
    private EcobeeCommunicationServiceHelper helper;
    private File gzFile;
    private String jobId = "yUo111RE9wtoMmTS1pXXCxhBkOooaf2N";

    @Before
    public void setup() throws Exception {
        GlobalSettingDao mockGlobalSettingDao = EasyMock.createMock(GlobalSettingDao.class);
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL)).andReturn(
            StringUtils.EMPTY);
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.HTTP_PROXY)).andReturn(StringUtils.EMPTY);
        EcobeeQueryCountDao mockEcobeeQueryCountDao = EasyMock.createMock(EcobeeQueryCountDao.class);
        mockEcobeeQueryCountDao.incrementQueryCount(EasyMock.anyObject(EcobeeQueryType.class));

        EasyMock.expectLastCall();

        impl = new EcobeeCommunicationServiceImpl();
        helper = new EcobeeCommunicationServiceHelper();
        EasyMock.replay(mockGlobalSettingDao);
        ReflectionTestUtils.setField(impl, "settingDao", mockGlobalSettingDao);
        ReflectionTestUtils.setField(impl, "ecobeeQueryCountDao", mockEcobeeQueryCountDao);
    }

    @Test
    public void test_createRuntimeReportJob_completed() throws Exception {
        RestTemplate restTemplateMock = EasyMock.createMock(RestTemplate.class);
        TestCase testCase = getTestCase();
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method =
            implClass.getDeclaredMethod("createRuntimeReportJob", SelectionType.class, Collection.class, Range.class);
        method.setAccessible(true);
        RuntimeReportJobResponse response =
            new RuntimeReportJobResponse(jobId, EcobeeJobStatus.COMPLETED, new Status(0, StringUtils.EMPTY));
        setPostForObjectMethod(restTemplateMock, response);
        EasyMock.replay(restTemplateMock);
        ReflectionTestUtils.setField(impl, "restTemplate", restTemplateMock);
        RuntimeReportJobResponse res = (RuntimeReportJobResponse) method.invoke(impl, SelectionType.THERMOSTATS,
            testCase.serialNumbers, testCase.getDateRange());
        Assert.assertEquals("Expected jobID doesn't match returned jobID", jobId, res.getJobId());
        Assert.assertEquals("Expected job status doesn't match returned job status", EcobeeJobStatus.COMPLETED,
            res.getJobStatus());
    }

    @Test
    public void test_createRuntimeReportJob_error() throws Exception {
        RestTemplate restTemplateMock = EasyMock.createMock(RestTemplate.class);
        TestCase testCase = getTestCase();
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method =
            implClass.getDeclaredMethod("createRuntimeReportJob", SelectionType.class, Collection.class, Range.class);
        method.setAccessible(true);
        RuntimeReportJobResponse response =
            new RuntimeReportJobResponse(jobId, EcobeeJobStatus.ERROR, new Status(0, StringUtils.EMPTY));
        setPostForObjectMethod(restTemplateMock, response);
        EasyMock.replay(restTemplateMock);
        ReflectionTestUtils.setField(impl, "restTemplate", restTemplateMock);
        RuntimeReportJobResponse res = (RuntimeReportJobResponse) method.invoke(impl, SelectionType.THERMOSTATS,
            testCase.serialNumbers, testCase.getDateRange());
        Assert.assertEquals("Expected jobID doesn't match returned jobID", jobId, res.getJobId());
        Assert.assertEquals("Expected EcobeeJobStatus job status doesn't match returned job status",
            EcobeeJobStatus.ERROR, res.getJobStatus());
        Assert.assertEquals("Expected status code doesn't match returned status code", 0, res.getStatus().getCode());
        Assert.assertEquals("Expected status message doesn't match returned status message", StringUtils.EMPTY,
            res.getStatus().getMessage());
    }

    @Test
    public void test_getRuntimeReportJobStatus_completed() throws Exception {
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("getRuntimeReportJobStatus", String.class);
        method.setAccessible(true);
        RuntimeReportJobStatusResponse expectedResponse =
            setupMockRuntimeReportJobStatusResponse(EcobeeJobStatus.COMPLETED);
        RuntimeReportJobStatusResponse actualResponse = (RuntimeReportJobStatusResponse) method.invoke(impl, jobId);
        assertRuntimeReportJobStatus(expectedResponse, actualResponse);
    }

    @Test
    public void test_getRuntimeReportJobStatus_canclled() throws Exception {
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("getRuntimeReportJobStatus", String.class);
        method.setAccessible(true);
        RuntimeReportJobStatusResponse expectedResponse =
            setupMockRuntimeReportJobStatusResponse(EcobeeJobStatus.CANCELLED);
        RuntimeReportJobStatusResponse actualResponse = (RuntimeReportJobStatusResponse) method.invoke(impl, jobId);
        assertRuntimeReportJobStatus(expectedResponse, actualResponse);
    }

    @Test
    public void test_getRuntimeReportJobStatus_error() throws Exception {
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("getRuntimeReportJobStatus", String.class);
        method.setAccessible(true);
        RuntimeReportJobStatusResponse expectedResponse =
            setupMockRuntimeReportJobStatusResponse(EcobeeJobStatus.ERROR);
        RuntimeReportJobStatusResponse actualResponse = (RuntimeReportJobStatusResponse) method.invoke(impl, jobId);
        assertRuntimeReportJobStatus(expectedResponse, actualResponse);
    }

    @Test
    public void test_getRuntimeReportJobStatus_processing() throws Exception {
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("getRuntimeReportJobStatus", String.class);
        method.setAccessible(true);
        RuntimeReportJobStatusResponse expectedResponse =
            setupMockRuntimeReportJobStatusResponse(EcobeeJobStatus.PROCESSING);
        RuntimeReportJobStatusResponse actualResponse = (RuntimeReportJobStatusResponse) method.invoke(impl, jobId);
        assertRuntimeReportJobStatus(expectedResponse, actualResponse);
    }

    @Test
    public void test_getRuntimeReportJobStatus_queued() throws Exception {
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("getRuntimeReportJobStatus", String.class);
        method.setAccessible(true);
        RuntimeReportJobStatusResponse expectedResponse =
            setupMockRuntimeReportJobStatusResponse(EcobeeJobStatus.QUEUED);
        RuntimeReportJobStatusResponse actualResponse = (RuntimeReportJobStatusResponse) method.invoke(impl, jobId);
        assertRuntimeReportJobStatus(expectedResponse, actualResponse);
    }

    @Test
    public void test_readDeviceData_someSerialNumbersMissing() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(7, 15));
        testCase.setSerialNumbers("2", "3", "4", "5", "6");
        testCase.setSerialNumbersNotRegistered("5", "6");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("03/03/2019 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("03/04/2019 11:00:00"));
        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);
        mockGzFile(testCase.getSerialNumbers(), testCase.getStartDate(), testCase.getEndDate());
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("downloadRuntimeReport", List.class);
        method.setAccessible(true);
        mockDependentObjects(expectedResponse);
        URL url = gzFile.toURI().toURL();
        List<String> dataUrls = new ArrayList<>();
        dataUrls.add(url.toString());
        @SuppressWarnings("unchecked")
        List<EcobeeDeviceReadings> actualResponse = (List<EcobeeDeviceReadings>) method.invoke(impl, dataUrls);
        AssertEqual(actualResponse, expectedResponse);
    }

    @Test(expected = Exception.class)
    public void test_readDeviceData_withException() throws Exception {
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("downloadRuntimeReport", List.class);
        method.setAccessible(true);
        List<String> dataUrls = new ArrayList<>();
        dataUrls.add("test");
        method.invoke(impl, dataUrls);
    }

    @Test
    public void test_readDeviceData_noDataReturned() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(2, 30));
        testCase.setSerialNumbers("1");
        testCase.setSerialNumbersNotRegistered("1");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("03/03/2019 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("03/04/2019 11:00:00"));

        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);
        mockGzFile(testCase.getSerialNumbers(), testCase.getStartDate(), testCase.getEndDate());
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("downloadRuntimeReport", List.class);
        method.setAccessible(true);
        mockDependentObjects(expectedResponse);
        URL url = gzFile.toURI().toURL();
        List<String> dataUrls = new ArrayList<>();
        dataUrls.add(url.toString());
        @SuppressWarnings("unchecked")
        List<EcobeeDeviceReadings> actualResponse = (List<EcobeeDeviceReadings>) method.invoke(impl, dataUrls);
        AssertEqual(actualResponse, expectedResponse);
    }

    @Test
    public void test_readDeviceData_dateWithMinutesAndSeconds() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(2, 30));
        testCase.setSerialNumbers("1");
        testCase.setSerialNumbersNotRegistered("1", "2");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("03/03/2019 18:37:12"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("03/04/2019 12:19:13"));

        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);
        mockGzFile(testCase.getSerialNumbers(), testCase.getStartDate(), testCase.getEndDate());
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("downloadRuntimeReport", List.class);
        method.setAccessible(true);
        mockDependentObjects(expectedResponse);
        URL url = gzFile.toURI().toURL();
        List<String> dataUrls = new ArrayList<>();
        dataUrls.add(url.toString());
        @SuppressWarnings("unchecked")
        List<EcobeeDeviceReadings> actualResponse = (List<EcobeeDeviceReadings>) method.invoke(impl, dataUrls);
        AssertEqual(actualResponse, expectedResponse);
    }

    @Test
    public void test_readDeviceData_enoughDevicesForTwoMessages() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(2, 30));
        String[] serialNumbers = getEnoughSerialNumbersForTwoMessages();
        testCase.setSerialNumbers(serialNumbers);
        testCase.setStartDate(dateTimeFormatter.parseDateTime("03/03/2019 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("03/04/2019 18:00:00"));

        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);
        mockGzFile(testCase.getSerialNumbers(), testCase.getStartDate(), testCase.getEndDate());
        Class<EcobeeCommunicationServiceImpl> implClass = EcobeeCommunicationServiceImpl.class;
        Method method = implClass.getDeclaredMethod("downloadRuntimeReport", List.class);
        method.setAccessible(true);
        mockDependentObjects(expectedResponse);
        URL url = gzFile.toURI().toURL();
        List<String> dataUrls = new ArrayList<>();
        dataUrls.add(url.toString());
        @SuppressWarnings("unchecked")
        List<EcobeeDeviceReadings> actualResponse = (List<EcobeeDeviceReadings>) method.invoke(impl, dataUrls);
        AssertEqual(actualResponse, expectedResponse);
    }

    @Test
    public void test_getEcobeeDeviceReadings() throws Exception {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.UTC);
        testCase.setSerialNumbers("1");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("03/03/1990 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("03/04/1990 11:00:00"));
        mockGzFile(testCase.getSerialNumbers(), testCase.getStartDate(), testCase.getEndDate());
        List<File> csvFiles = FileUtil.untar(FileUtil.ungzip(gzFile));
        List<EcobeeDeviceReadings> expectedResponse = setupMockForTestCase(testCase);
        List<EcobeeDeviceReadings> actualResponse = helper.getEcobeeDeviceReadings(csvFiles);
        AssertEqual(actualResponse, expectedResponse);
    }

    public void mockGzFile(Set<String> serialNumbers, DateTime thermostatStartTime, DateTime thermostatEndTime)
            throws IOException {
        File tarFile = File.createTempFile("yUo111RE9wtoMmTS1pXXCxhBkOooaf2N", ".tar");
        gzFile = File.createTempFile("yUo111RE9wtoMmTS1pXXCxhBkOooaf2N", ".gz");
        tarFile.deleteOnExit();
        gzFile.deleteOnExit();
        List<File> csvFiles = new ArrayList<>();
        serialNumbers.stream().forEach(serialNumber -> {
            Writer writer = null;
            CSVWriter csvwriter = null;
            try {
                File file = File.createTempFile(serialNumber + "-" + jobId, ".csv");
                file.deleteOnExit();
                writer = new FileWriter(file);
                csvwriter = new CSVWriter(writer);
                csvwriter.writeNext(deviceReadColumns.toArray(new String[deviceReadColumns.size()]));
                for (DateTime reportTime = thermostatStartTime; reportTime.isBefore(thermostatEndTime); reportTime =
                    reportTime.plusMinutes(5)) {
                    String date = reportTime.toLocalDate().toString();
                    String time = reportTime.toLocalTime().toString("HH:mm:ss");
                    csvwriter.writeNext(
                        new String[] { date, time, StringUtils.EMPTY, "75", "95", "75", "75", "0", "15" });
                }
                csvFiles.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            CtiUtilities.close(writer, csvwriter);
        });
        try (TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(tarFile));) {
            csvFiles.stream().forEach(file -> {
                try (InputStream inputStream = new FileInputStream(file);) {
                    TarArchiveEntry tarEntry = new TarArchiveEntry(file);
                    archiveOutputStream.putArchiveEntry(tarEntry);
                    IOUtils.copy(inputStream, archiveOutputStream);
                    archiveOutputStream.closeArchiveEntry();
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        try (InputStream inStream = new FileInputStream(tarFile);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(gzFile));) {
            IOUtils.copy(inStream, gzipOutputStream);
        }
        tarFile.delete();
    }

    private TestCase getTestCase() {
        TestCase testCase = new TestCase();
        testCase.setTimeZone(DateTimeZone.forOffsetHoursMinutes(7, 15));
        testCase.setSerialNumbers("2");
        testCase.setStartDate(dateTimeFormatter.parseDateTime("03/03/2019 18:00:00"));
        testCase.setEndDate(dateTimeFormatter.parseDateTime("03/04/2019 11:00:00"));
        return testCase;
    }

    @SuppressWarnings("unchecked")
    private void mockDependentObjects(List<EcobeeDeviceReadings> expectedResponse) throws Exception {
        EcobeeCommunicationServiceHelper helper = EasyMock.createMock(EcobeeCommunicationServiceHelper.class);
        EcobeeSecurityService ecobeeSecurityService = EasyMock.createMock(EcobeeSecurityService.class);
        EasyMock.expect(helper.getDecryptedFileName(EasyMock.anyObject(String.class))).andReturn(
            "yUo111RE9wtoMmTS1pXXCxhBkOooaf2N.tar.gz");
        EasyMock.expect(helper.getEcobeeDeviceReadings(EasyMock.anyObject(List.class))).andReturn(expectedResponse);
        EasyMock.expect(ecobeeSecurityService.decryptEcobeeFile(EasyMock.anyObject(InputStream.class))).andReturn(
            Files.readAllBytes(gzFile.toPath()));
        EasyMock.replay(helper);
        EasyMock.replay(ecobeeSecurityService);
        ReflectionTestUtils.setField(impl, "ecobeeCommunicationServiceHelper", helper);
        ReflectionTestUtils.setField(impl, "ecobeeSecurityService", ecobeeSecurityService);

    }

    private String[] getEnoughSerialNumbersForTwoMessages() {
        String[] serialNumbers = new String[26];
        for (int i = 1; i <= 26; i++) {
            serialNumbers[i - 1] = Integer.toString(i);
        }
        return serialNumbers;
    }

    public List<EcobeeDeviceReadings> setupMockForTestCase(TestCase testCase) throws Exception {
        List<EcobeeDeviceReadings> expectedDeviceReadings = new ArrayList<>();
        Set<String> serialNumbers = new HashSet<>(testCase.getSerialNumbers());
        serialNumbers.removeAll(testCase.getSerialNumbersNotRegistered());
        for (String serialNumber : serialNumbers) {
            List<EcobeeDeviceReading> expectedReadings = new ArrayList<>();

            LocalDateTime thermostatStartTime =
                testCase.getStartDate().withZone(testCase.getTimeZone()).toLocalDateTime();
            LocalDateTime thermostatEndTime = testCase.getEndDate().withZone(testCase.getTimeZone()).toLocalDateTime();
            for (LocalDateTime reportTime = thermostatStartTime; reportTime.isBefore(thermostatEndTime); reportTime =
                reportTime.plusMinutes(5)) {
                String dateAndTime = reportTime.toLocalDate().toString() + StringUtils.SPACE
                    + reportTime.toLocalTime().toString("HH:mm:ss");
                DateTime dt = DateTime.parse(dateAndTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                Instant instant = dt.toInstant();
                EcobeeDeviceReading reading =
                    new EcobeeDeviceReading(95f, 75f, 75f, 75f, 15, StringUtils.EMPTY, instant);
                expectedReadings.add(reading);
            }
            expectedDeviceReadings.add(new EcobeeDeviceReadings(serialNumber, expectedReadings));
        }

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

    private void AssertEqual(List<EcobeeDeviceReadings> allDeviceReadings1,
            List<EcobeeDeviceReadings> allDeviceReadings2) {
        Assert.assertNotNull(allDeviceReadings1);
        Assert.assertNotNull(allDeviceReadings2);

        Assert.assertTrue("Expected number of device readings is different from amount returned",
            allDeviceReadings1.size() == allDeviceReadings2.size());
        for (int i = 0; i < allDeviceReadings1.size(); i++) {
            EcobeeDeviceReadings deviceReadings1 = allDeviceReadings1.get(i);
            EcobeeDeviceReadings deviceReadings2 = allDeviceReadings2.get(i);

            Assert.assertEquals("Expected serial number doesn't match returned value",
                deviceReadings1.getSerialNumber(), deviceReadings2.getSerialNumber());
            List<EcobeeDeviceReading> readings1 = deviceReadings1.getReadings();
            List<EcobeeDeviceReading> readings2 = deviceReadings2.getReadings();
            Assert.assertTrue("Different number of readings returned than was expected",
                readings1.size() == readings2.size());
            for (int j = 0; j < readings1.size(); j++) {
                EcobeeDeviceReading ecobeeDeviceReading1 = readings1.get(j);
                EcobeeDeviceReading ecobeeDeviceReading2 = readings2.get(j);
                Assert.assertEquals("Expected date doesn't match returned date", ecobeeDeviceReading1.getDate(),
                    ecobeeDeviceReading2.getDate());
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

    private void assertRuntimeReportJobStatus(RuntimeReportJobStatusResponse expectedResponse,
            RuntimeReportJobStatusResponse actualResponse) {
        Assert.assertTrue("Expected number of job readings is different from amount returned",
            expectedResponse.getJobs().size() == actualResponse.getJobs().size());
        EcobeeReportJob expectedJob = expectedResponse.getJobs().get(0);
        EcobeeReportJob actualJob = actualResponse.getJobs().get(0);
        Assert.assertEquals("Expected jobID doesn't match returned jobID", expectedJob.getJobId(),
            actualJob.getJobId());
        Assert.assertEquals("Expected EcobeeJobStatus job status doesn't match returned job status",
            expectedJob.getStatus(), actualJob.getStatus());
        Assert.assertEquals("Expected file size doesn't match returned file size", expectedJob.getFiles().length,
            actualJob.getFiles().length);
        if (EcobeeJobStatus.COMPLETED == actualJob.getStatus()) {
            Assert.assertEquals("Expected file name doesn't match returned file name", expectedJob.getFiles()[0],
                actualJob.getFiles()[0]);
        }
        Assert.assertEquals("Expected status message doesn't match returned status message", expectedJob.getMessage(),
            actualJob.getMessage());
    }

    private RuntimeReportJobStatusResponse setupMockRuntimeReportJobStatusResponse(EcobeeJobStatus ecobeeJobStatus)
            throws Exception {
        RestTemplate restTemplateMock = EasyMock.createMock(RestTemplate.class);
        EcobeeReportJob job =
            new EcobeeReportJob(jobId, ecobeeJobStatus, StringUtils.EMPTY, EcobeeJobStatus.COMPLETED == ecobeeJobStatus
                ? new String[] { "testFile" } : new String[] { StringUtils.EMPTY });
        RuntimeReportJobStatusRequest request = new RuntimeReportJobStatusRequest(jobId);
        ArrayList<EcobeeReportJob> ecobeeReportJobs = new ArrayList<>();
        ecobeeReportJobs.add(job);
        RuntimeReportJobStatusResponse response =
            new RuntimeReportJobStatusResponse(ecobeeReportJobs, new Status(0, StringUtils.EMPTY));
        setExchangeMethod(restTemplateMock, request, response);
        EasyMock.replay(restTemplateMock);
        ReflectionTestUtils.setField(impl, "restTemplate", restTemplateMock);
        return response;
    }

    @SuppressWarnings("unchecked")
    private <T> void setExchangeMethod(RestTemplate mock, Object request, T response) throws Exception {
        EasyMock.expect(mock.exchange(EasyMock.anyObject(String.class), EasyMock.eq(HttpMethod.GET),
            EasyMock.anyObject(HttpEntity.class), (Class<T>) EasyMock.anyObject(Class.class),
            EasyMock.eq(Collections.singletonMap("bodyJson", JsonUtils.toJson(request))))).andReturn(
                new ResponseEntity<>(response, HttpStatus.I_AM_A_TEAPOT));
    }

    @SuppressWarnings("unchecked")
    private <T> void setPostForObjectMethod(RestTemplate mock, T response) throws Exception {
        EasyMock.expect(mock.postForObject(EasyMock.anyObject(String.class), EasyMock.anyObject(HttpEntity.class),
            (Class<T>) EasyMock.anyObject(Class.class))).andReturn(response);
    }
}

package com.cannontech.dr.ecobee.message.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.ecobee.message.CreateSetRequest;
import com.cannontech.dr.ecobee.message.DeleteSetRequest;
import com.cannontech.dr.ecobee.message.DeviceDataResponse;
import com.cannontech.dr.ecobee.message.DrRestoreRequest;
import com.cannontech.dr.ecobee.message.DutyCycleDrRequest;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.ListHierarchyRequest;
import com.cannontech.dr.ecobee.message.MoveDeviceRequest;
import com.cannontech.dr.ecobee.message.MoveSetRequest;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.message.partial.DutyCycleDr;
import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;

public class JsonSerializationTest {

    @Test
    public void test_Selection() throws IOException {
        Selection request = new Selection(SelectionType.REGISTERED, ImmutableList.of("abcd", "1234", "efgh"));
        Selection testRequest = testSerialization(request);

        Assert.assertEquals(request.getSelectionType(), testRequest.getSelectionType());
        Assert.assertEquals(request.getSerialNumbers(), testRequest.getSerialNumbers());
    }

    @Test
    public void test_RegisterDeviceRequest() throws IOException {
        RegisterDeviceRequest request = new RegisterDeviceRequest("123abc321");
        RegisterDeviceRequest testRequest = testSerialization(request);

        Assert.assertEquals(request.getOperation(), testRequest.getOperation());
        Assert.assertEquals(request.getThermostats(), testRequest.getThermostats());
    }

    @Test
    public void test_StandardResponse() throws IOException {
        StandardResponse resposne = new StandardResponse(true, new Status(2, "test"));
        StandardResponse testResponse = testSerialization(resposne);

        Assert.assertEquals(resposne.getSuccess(), testResponse.getSuccess());
        Assert.assertEquals(resposne.getStatus().getMessage(), testResponse.getStatus().getMessage());
        Assert.assertEquals(resposne.getStatus().getCode(), testResponse.getStatus().getCode());
    }

    @Test
    public void test_MoveDeviceRequest() throws IOException {
        MoveDeviceRequest request = new MoveDeviceRequest("123abc321", "bogusPath");
        MoveDeviceRequest testRequest = testSerialization(request);

        Assert.assertEquals(request.getOperation(), testRequest.getOperation());
        Assert.assertEquals(request.getSetPath(), testRequest.getSetPath());
        Assert.assertEquals(request.getThermostats(), testRequest.getThermostats());
    }

    @Test
    public void test_RuntimeReportRequest() throws IOException {
        DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZoneUTC();
        Instant startDate = timeFormater.parseDateTime("2014-05-28 10:10").toInstant();
        Instant endDate = timeFormater.parseDateTime("2012-01-01 20:25").toInstant();

        RuntimeReportRequest request =
            new RuntimeReportRequest(startDate, endDate, ImmutableList.of("123abc321", "321abc123"), 
                ImmutableList.of("bogusColumn1", "anotherBogusColumn"));
        RuntimeReportRequest testRequest = testSerialization(request);

        Assert.assertEquals(request.getStartDate(), testRequest.getStartDate());
        Assert.assertEquals(request.getEndDate(), testRequest.getEndDate());
        Assert.assertEquals(request.getColumns(), testRequest.getColumns());
        Assert.assertEquals(request.getSelection().getSelectionType(), testRequest.getSelection().getSelectionType());
        Assert.assertEquals(request.getSelection().getSerialNumbers(), testRequest.getSelection().getSerialNumbers());
        Assert.assertEquals(request.getStartInterval(), testRequest.getStartInterval());
        Assert.assertEquals(request.getEndInterval(), testRequest.getEndInterval());
    }

    @Test
    public void test_DeviceDataResponse() throws IOException {
        int numRows = 15;
        int numThermostats = 20;

        List<RuntimeReport> reportList = new ArrayList<>();
        for (int i = 0; i < numThermostats; i++) {
            List<RuntimeReportRow> runtimeReports = new ArrayList<>();
            for (int j = 0; j < numRows; j++) {
                LocalDateTime reportTime = new LocalDateTime(2014, 5, 25, 10, 35);
                RuntimeReportRow reportRow = new RuntimeReportRow(reportTime, "", 75f, 95f, 75.4f, .56456f, 123);
                runtimeReports.add(reportRow);
            }
            reportList.add(new RuntimeReport(Integer.toString(i), numRows, runtimeReports));
        }
        Status status = new Status(3, "code 3");
        DeviceDataResponse response = new DeviceDataResponse(status, reportList);
        DeviceDataResponse testResponse = testSerialization(response);

        Assert.assertEquals(response.getStatus().getMessage(), testResponse.getStatus().getMessage());
        Assert.assertEquals(response.getStatus().getCode(), testResponse.getStatus().getCode());
        Assert.assertEquals(response.getReportList().size(), testResponse.getReportList().size());
        for (int i = 0; i < numThermostats; i++) {
            RuntimeReport runtimeReport = response.getReportList().get(i);
            RuntimeReport testRuntimeReport = testResponse.getReportList().get(i);
            Assert.assertEquals(runtimeReport.getThermostatIdentifier(), testRuntimeReport.getThermostatIdentifier());
            Assert.assertEquals(runtimeReport.getRowCount(), testRuntimeReport.getRowCount());
            Assert.assertEquals(runtimeReport.getRuntimeReports().size(), testRuntimeReport.getRuntimeReports().size());
            for (int j = 0; j < numRows; j++) {
                RuntimeReportRow runtimeReportRow = runtimeReport.getRuntimeReports().get(j);
                RuntimeReportRow testRuntimeReportRow = testRuntimeReport.getRuntimeReports().get(j);
                Assert.assertEquals(runtimeReportRow.getEventName(), testRuntimeReportRow.getEventName());
                Assert.assertEquals(runtimeReportRow.getCoolSetPoint(), testRuntimeReportRow.getCoolSetPoint());
                Assert.assertEquals(runtimeReportRow.getHeatSetPoint(), testRuntimeReportRow.getHeatSetPoint());
                Assert.assertEquals(runtimeReportRow.getIndoorTemp(), testRuntimeReportRow.getIndoorTemp());
                Assert.assertEquals(runtimeReportRow.getOutdoorTemp(), testRuntimeReportRow.getOutdoorTemp());
                Assert.assertEquals(runtimeReportRow.getRuntime(), testRuntimeReportRow.getRuntime());
                Assert.assertEquals(runtimeReportRow.getThermostatTime(), testRuntimeReportRow.getThermostatTime());
            }
        }
    }

    @Test
    public void test_CreateSetRequest() throws IOException {
        CreateSetRequest request = new CreateSetRequest("bogusSet");
        CreateSetRequest testRequest = testSerialization(request);

        Assert.assertEquals(request.getOperation(), testRequest.getOperation());
        Assert.assertEquals(request.getParentPath(), testRequest.getParentPath());
        Assert.assertEquals(request.getSetName(), testRequest.getSetName());
    }

    // @Test
    public void test_DeleteSetRequest() throws IOException {
        DeleteSetRequest request = new DeleteSetRequest("bogusSet");
        DeleteSetRequest testRequest = testSerialization(request);

        Assert.assertEquals(request.getOperation(), testRequest.getOperation());
        Assert.assertEquals(request.getSetPath(), testRequest.getSetPath());
    }

    @Test
    public void test_MoveSetRequest() throws IOException {
        MoveSetRequest request = new MoveSetRequest("bogusCurrentPath", "bogusToPath");
        MoveSetRequest testRequest = testSerialization(request);

        Assert.assertEquals(request.getOperation(), testRequest.getOperation());
        Assert.assertEquals(request.getSetPath(), testRequest.getSetPath());
    }

    @Test
    public void test_DutyCycleDrRequest() throws IOException {

        DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
        Instant startDate = timeFormater.parseDateTime("2010-01-01 10:10:00").toInstant();
        Instant endDate = timeFormater.parseDateTime("2012-01-01 20:25:00").toInstant();

        DutyCycleDrRequest request =
            new DutyCycleDrRequest("bogusSetName", "bogusDrName", 99, startDate, false, endDate, false);
        DutyCycleDrRequest testRrequest = testSerialization(request);

        Assert.assertEquals(request.getOperation(), testRrequest.getOperation());
        DutyCycleDr demandResponse = request.getDemandResponse();
        DutyCycleDr testDemandResponse = testRrequest.getDemandResponse();
        Assert.assertEquals(demandResponse.getMessage(), testDemandResponse.getMessage());
        Assert.assertEquals(demandResponse.getRandomizeEndTime(), testDemandResponse.getRandomizeEndTime());
        Assert.assertEquals(demandResponse.getRandomizeStartTime(), testDemandResponse.getRandomizeStartTime());
        Assert.assertEquals(demandResponse.getName(), testDemandResponse.getName());
        Selection selection = request.getSelection();
        Selection testSelection = testRrequest.getSelection();
        Assert.assertEquals(selection.getSelectionType(), testSelection.getSelectionType());
        Assert.assertTrue(selection.getSerialNumbers().containsAll(testSelection.getSerialNumbers()));
        Assert.assertTrue(testSelection.getSerialNumbers().containsAll(selection.getSerialNumbers()));
    }

    @Test
    public void test_DrRestoreRequest() throws IOException {
        DrRestoreRequest request = new DrRestoreRequest("demandResponseRef");
        DrRestoreRequest testRequest = testSerialization(request);

        Assert.assertEquals(request.getOperation(), testRequest.getOperation());
        Assert.assertEquals(request.getDemandResponse().getDemandResponseRef(),
            testRequest.getDemandResponse().getDemandResponseRef());
    }

    @Test
    public void test_ListHierarchyRequest() throws IOException {
        ListHierarchyRequest request = new ListHierarchyRequest();
        ListHierarchyRequest testRequest = testSerialization(request);

        Assert.assertEquals(request.getOperation(), testRequest.getOperation());
        Assert.assertEquals(request.getSetPath(), testRequest.getSetPath());
        Assert.assertEquals(request.isIncludeThermostats(), testRequest.isIncludeThermostats());
        Assert.assertEquals(request.isRecursive(), testRequest.isRecursive());
    }

    @Test
    public void test_HierarchyResponse() throws IOException {
        int numSets = 10;
        int numChildren = 10;

        List<SetNode> sets = new ArrayList<>();
        Status status = new Status(3, "code 3");

        for (int i = 0; i < numSets; i++) {
            List<SetNode> children = new ArrayList<>();
            List<String> thermostats = new ArrayList<>();
            for (int j = 0; j < numChildren; j++) {
                children.add(new SetNode("someChildSet", "someOtherPath", new ArrayList<SetNode>(), 
                    ImmutableList.of("123abc321", "321abc123", "another")));
                thermostats.add("aThermostat" + j);
            }
            sets.add(new SetNode("someSet", "somePath", children, thermostats));
        }

        HierarchyResponse response = new HierarchyResponse(sets, status);
        HierarchyResponse testResponse = testSerialization(response);

        Assert.assertEquals(response.getStatus().getMessage(), testResponse.getStatus().getMessage());
        Assert.assertEquals(response.getStatus().getCode(), testResponse.getStatus().getCode());
        Assert.assertEquals(response.getSets().size(), testResponse.getSets().size());
        for (int i = 0; i < numSets; i++) {
            SetNode set = response.getSets().get(i);
            SetNode testSet = testResponse.getSets().get(i);
            assertEquals(set, testSet);
        }
    }

    public void assertEquals(SetNode nodeA, SetNode nodeB) {
        Assert.assertEquals(nodeA.getChildren().size(), nodeB.getChildren().size());
        Assert.assertEquals(nodeA.getSetName(), nodeB.getSetName());
        Assert.assertEquals(nodeA.getSetPath(), nodeB.getSetPath());
        int numChildren = nodeA.getChildren().size();
        for (int i = 0; i < numChildren; i++) {
            SetNode nodeAChild = nodeA.getChildren().get(i);
            SetNode nodeBChild = nodeB.getChildren().get(i);
            assertEquals(nodeAChild, nodeBChild);
        }
        Assert.assertEquals(nodeA.getThermostats().size(), nodeB.getThermostats().size());
        int numThermostats = nodeA.getThermostats().size();
        for (int i = 0; i < numThermostats; i++) {
            Assert.assertEquals(nodeA.getThermostats().get(i), nodeB.getThermostats().get(i));
        }
    }

    /**
     * Serializes object into JSON string and deserializes it back into its original java type.
     */
    @SuppressWarnings("unchecked")
    private <T> T testSerialization(T obj) throws IOException {
        String json;
        try {
            json = JsonUtils.toJson(obj);
        } catch (JsonProcessingException e) {
            Assert.fail("Serialization failed for " + obj.getClass().getSimpleName() + ". " + e.getMessage());
            return null;
        }
        try {
            return (T) JsonUtils.fromJson(json, obj.getClass());
        } catch (JsonProcessingException e) {
            Assert.fail("Deserialization failed for " + obj.getClass().getSimpleName() + ". " + e.getMessage());
            return null;
        }
    }
}

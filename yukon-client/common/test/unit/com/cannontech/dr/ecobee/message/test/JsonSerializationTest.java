package com.cannontech.dr.ecobee.message.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;

@SuppressWarnings(value={"unchecked", "rawtypes"})
public class JsonSerializationTest {

    @Test
    public void test_RegisterDeviceRequest() throws IOException {
        String serialNumber = "123abc321";
        RegisterDeviceRequest request = new RegisterDeviceRequest(serialNumber);
        String json = JsonUtils.toJson(request);
        Map<String, String> jsonResult = JsonUtils.fromJson(json, new TypeReference<Map<String, String>>(){/* required */});

        Assert.assertEquals(jsonResult.get("operation"), "register");
        Assert.assertEquals(jsonResult.get("thermostats"), serialNumber);
    }

    @Test
    public void test_StandardResponse() throws IOException {
        Map<String, Object> statusJsonSource = new HashMap<>();
        statusJsonSource.put("code", 3);
        statusJsonSource.put("message", "hello world");
        Map<String, Object> jsonSource = new HashMap<>();
        jsonSource.put("success", true);
        jsonSource.put("status", statusJsonSource);
        String json = JsonUtils.toJson(jsonSource);
        StandardResponse response = JsonUtils.fromJson(json, StandardResponse.class);

        Assert.assertEquals(response.getStatus().getMessage(), ((Map)jsonSource.get("status")).get("message"));
        Assert.assertEquals(response.getStatus().getCode(), ((Map)jsonSource.get("status")).get("code"));
        Assert.assertEquals(response.getSuccess(), jsonSource.get("success"));
    }
    
    @Test
    public void test_MoveDeviceRequest() throws IOException {
        String serialNumber = "123abc321";
        String setPath = "bogusPath";

        MoveDeviceRequest request = new MoveDeviceRequest(serialNumber, setPath);
        String json = JsonUtils.toJson(request);
        Map<String, String> jsonResult = JsonUtils.fromJson(json, new TypeReference<Map<String, String>>(){/* required */});

        Assert.assertEquals(jsonResult.get("operation"), "assign");
        Assert.assertEquals(jsonResult.get("setPath"), setPath);
        Assert.assertEquals(jsonResult.get("thermostats"), serialNumber);
    }

    @Test
    public void test_RuntimeReportRequest() throws IOException {
        String startDateStr = "2014-05-28";
        String endDateStr = "2012-01-01";
        List<String> columns = ImmutableList.of("bogusColumn1", "anotherBogusColumn");
        List<String> serialNumbers = ImmutableList.of("123abc321", "321abc123");

        DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZoneUTC();
        Instant startDate = timeFormater.parseDateTime(startDateStr + " 10:10").toInstant();
        Instant endDate = timeFormater.parseDateTime(endDateStr + " 20:25").toInstant();

        RuntimeReportRequest request = new RuntimeReportRequest(startDate, endDate, serialNumbers, columns);
        String json = JsonUtils.toJson(request);
        Map<String, Object> jsonResult = JsonUtils.fromJson(json, Map.class);
        Map selectionJsonResult = (Map) jsonResult.get("selection");

        Assert.assertEquals(jsonResult.get("startDate"), startDateStr);
        Assert.assertEquals(jsonResult.get("startInterval"), "122"); // 10:10 is 122*5 minutes into the day
        Assert.assertEquals(jsonResult.get("endDate"), endDateStr); // 20:25 is 125*5 minutes into the day
        Assert.assertEquals(jsonResult.get("endInterval"), "245");
        Assert.assertEquals(jsonResult.get("columns"), StringUtils.join(columns, ","));
        Assert.assertEquals(selectionJsonResult.get("selectionType"), SelectionType.THERMOSTATS.getEcobeeString());
        Assert.assertEquals(selectionJsonResult.get("selectionMatch"), StringUtils.join(serialNumbers, ","));
    }

    @Test
    public void test_DeviceDataResponse() throws IOException {
        int numRows = 15;
        int numThermostats = 20;

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        Map<String, Object> statusJsonSource = new HashMap<>();
        statusJsonSource.put("code", 3);
        statusJsonSource.put("message", "hello world");
        List<Map> reportListJsonSource = new ArrayList<>();
        for (int i=0; i<numThermostats; i++) {
            List<String> rowListJsonSource = new ArrayList<>();
            for (int j=0; j<numRows; j++) {
                String dateStr = "200" + i % 10 + "-01-01";
                String timeStr = "00:00:0"+ i % 10;
                rowListJsonSource.add(dateStr + "," + timeStr + ",event_" + i + "," + i + "," + i + "," + i + "," +
                        i + "," + i + ","+ i);
            }
            Map<String, Object> runtimeReportJsonSource = new HashMap<>();
            runtimeReportJsonSource.put("thermostatIdentifier", Integer.toString(i));
            runtimeReportJsonSource.put("rowCount", numRows);
            runtimeReportJsonSource.put("rowList", rowListJsonSource);
            reportListJsonSource.add(runtimeReportJsonSource);
        }
        // add stuff here
        Map<String, Object> jsonSource = new HashMap<>();
        jsonSource.put("status", statusJsonSource);
        jsonSource.put("reportList", reportListJsonSource);

        String json = JsonUtils.toJson(jsonSource);
        DeviceDataResponse response = JsonUtils.fromJson(json, DeviceDataResponse.class);

        Assert.assertEquals(response.getStatus().getMessage(), ((Map)jsonSource.get("status")).get("message"));
        Assert.assertEquals(response.getStatus().getCode(), ((Map)jsonSource.get("status")).get("code"));
        Assert.assertEquals(response.getReportList().size(), numThermostats);

        List<RuntimeReport> reportList = response.getReportList();
        for (int i=0; i<numThermostats; i++) {
            RuntimeReport runtimeReport = reportList.get(i);
            Assert.assertEquals(runtimeReport.getRowCount(), 15);
            Assert.assertEquals(runtimeReport.getThermostatIdentifier(), Integer.toString(i));
            for (RuntimeReportRow runtimeReportRow : runtimeReport.getRuntimeReports()) {
                Assert.assertEquals(runtimeReportRow.getCoolSetPoint(), Float.valueOf(i));
                Assert.assertEquals(runtimeReportRow.getHeatSetPoint(), Float.valueOf(i));
                Assert.assertEquals(runtimeReportRow.getIndoorTemp(), Float.valueOf(i));
                Assert.assertEquals(runtimeReportRow.getOutdoorTemp(), Float.valueOf(i));
                Assert.assertEquals(runtimeReportRow.getEventName(), "event_" + i);
                Assert.assertEquals(runtimeReportRow.getRuntime(), i + i); // parser combines cool and heat
                String dateStr = "200" + i % 10 + "-01-01";
                String timeStr = "00:00:0"+ i % 10;
                LocalDateTime thermostatTime = dateTimeFormatter.parseLocalDateTime(dateStr + " " + timeStr);
                Assert.assertEquals(runtimeReportRow.getThermostatTime(), thermostatTime);
            }
        }
    }

    @Test
    public void test_CreateSetRequest() throws IOException {
        String setName = "bogusSet";
        CreateSetRequest request = new CreateSetRequest(setName);
        String json = JsonUtils.toJson(request);
        Map<String, String> jsonResult = JsonUtils.fromJson(json, new TypeReference<Map<String, String>>(){/* required */});

        Assert.assertEquals(jsonResult.get("operation"), "add");
        Assert.assertEquals(jsonResult.get("setName"), setName);
        Assert.assertEquals(jsonResult.get("parentPath"), "/");
    }

    @Test
    public void test_DeleteSetRequest() throws IOException {
        String setName = "bogusSet";
        DeleteSetRequest request = new DeleteSetRequest(setName);
        String json = JsonUtils.toJson(request);
        Map<String, String> jsonResult = JsonUtils.fromJson(json, new TypeReference<Map<String, String>>(){/* required */});

        Assert.assertEquals(jsonResult.get("operation"), "remove");
        Assert.assertEquals(jsonResult.get("setPath"), "/" + setName);
    }

    @Test
    public void test_MoveSetRequest() throws IOException {
        String currentPath = "bogusCurrentPath";
        String newPath = "bogusToPath";
        MoveSetRequest request = new MoveSetRequest(currentPath, newPath);
        String json = JsonUtils.toJson(request);
        Map<String, String> jsonResult = JsonUtils.fromJson(json, new TypeReference<Map<String, String>>(){/* required */});

        Assert.assertEquals(jsonResult.get("operation"), "move");
        Assert.assertEquals(jsonResult.get("setPath"), currentPath);
        Assert.assertEquals(jsonResult.get("toPath"), newPath);
    }
    
    @Test
    public void test_DutyCycleDrRequest() throws IOException {
        String setName = "bogusSetName";
        String drName = "bogusDrName";
        String startDateStr = "2010-01-01";
        String endDateStr = "2012-01-01";
        String startTimeStr = "10:10:00";
        String endTimeStr = "20:25:00";
        
        DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
        Instant startDate = timeFormater.parseDateTime(startDateStr + " " + startTimeStr).toInstant();
        Instant endDate = timeFormater.parseDateTime(endDateStr + " " + endTimeStr).toInstant();

        DutyCycleDrRequest request = new DutyCycleDrRequest(setName, drName, 99, startDate, false, endDate, false);
        String json = JsonUtils.toJson(request);
        Map<String, Object> jsonResult = JsonUtils.fromJson(json, Map.class);
        Map selectionJsonResult = (Map) jsonResult.get("selection");
        Map demandResponseResult = (Map) jsonResult.get("demandResponse");
        Map demandEventResult = (Map) demandResponseResult.get("event");

        Assert.assertEquals(jsonResult.get("operation"), "create");
        Assert.assertEquals(selectionJsonResult.get("selectionType"), SelectionType.MANAGEMENT_SET.getEcobeeString());
        Assert.assertEquals(selectionJsonResult.get("selectionMatch"), "/" + setName);
        Assert.assertEquals(demandResponseResult.get("name"), drName);
        Assert.assertEquals(demandResponseResult.get("message"), "");
        Assert.assertEquals(demandResponseResult.get("randomizeStartTime"), false);
        Assert.assertEquals(demandResponseResult.get("randomizeEndTime"), false);
        Assert.assertEquals(demandEventResult.get("name"), drName);
        Assert.assertEquals(demandEventResult.get("type"), "demandResponse");
        Assert.assertEquals(demandEventResult.get("startDate"), startDateStr);
        Assert.assertEquals(demandEventResult.get("startTime"), startTimeStr);
        Assert.assertEquals(demandEventResult.get("endDate"), endDateStr);
        Assert.assertEquals(demandEventResult.get("endTime"), endTimeStr);
        Assert.assertEquals(demandEventResult.get("isTemperatureRelative"), true);
        Assert.assertEquals(demandEventResult.get("heatHoldTemp"), 0);
        Assert.assertEquals(demandEventResult.get("coolHoldTemp"), 0);
        Assert.assertEquals(demandEventResult.get("dutyCyclePercentage"), 99);
        Assert.assertEquals(demandEventResult.get("isOptional"), false);
    }

    @Test
    public void test_DrRestoreRequest() throws IOException {
        String demandResponseRef = "demandResponseRef";
        DrRestoreRequest request = new DrRestoreRequest(demandResponseRef);
        String json = JsonUtils.toJson(request);
        Map<String, Object> jsonResult = JsonUtils.fromJson(json, Map.class);
        Map demandResponseRefResult = (Map) jsonResult.get("demandResponse");
        
        Assert.assertEquals(jsonResult.get("operation"), "cancel");
        Assert.assertEquals(demandResponseRefResult.get("demandResponseRef"), demandResponseRef);
    }

    @Test
    public void test_ListHierarchyRequest() throws IOException {
        ListHierarchyRequest request = new ListHierarchyRequest();
        String json = JsonUtils.toJson(request);
        Map<String, Object> jsonResult = JsonUtils.fromJson(json, Map.class);
        
        Assert.assertEquals(jsonResult.get("operation"), "list");
        Assert.assertEquals(jsonResult.get("setPath"), "/");
        Assert.assertEquals(jsonResult.get("recursive"), true);
        Assert.assertEquals(jsonResult.get("includeThermostats"), true);
    }

    @Test
    public void test_HierarchyResponse() throws IOException {
        int numSets = 10;
        int numChildren = 10;
        ImmutableList<String> thermostatList = ImmutableList.of("abcd","1234","efgh");
        ImmutableList<String> childThermostatList = ImmutableList.of("abc","123","def");
        
        Map<String, Object> statusJsonSource = new HashMap<>();
        statusJsonSource.put("code", 3);
        statusJsonSource.put("message", "hello world");
        List<Map<String, Object>> setsJsonSource = new ArrayList<>();
        for (int i=0; i<numSets; i++) {
            List<Map<String, Object>> childSetsJsonSource = new ArrayList<>();
            for (int j=0; j<numChildren; j++) {
                Map<String, Object> childSetNodeJsonSource = new HashMap<>();
                childSetNodeJsonSource.put("setName", "setName_" + i + "_" + j);
                childSetNodeJsonSource.put("setPath", "setPath_" + i + "_" + j);
                childSetNodeJsonSource.put("children", Collections.EMPTY_LIST);
                childSetNodeJsonSource.put("thermostats", childThermostatList);
                childSetsJsonSource.add(childSetNodeJsonSource);
            }
            Map<String, Object> setNodeJsonSource = new HashMap<>();
            setNodeJsonSource.put("setName", "setName_" + i);
            setNodeJsonSource.put("setPath", "setPath_" + i);
            setNodeJsonSource.put("children", childSetsJsonSource);
            setNodeJsonSource.put("thermostats", thermostatList);
            setsJsonSource.add(setNodeJsonSource);
        }
        Map<String, Object> jsonSource = new HashMap<>();
        jsonSource.put("status", statusJsonSource);
        jsonSource.put("sets", setsJsonSource);
        String json = JsonUtils.toJson(jsonSource);
        HierarchyResponse response = JsonUtils.fromJson(json, HierarchyResponse.class);

        Assert.assertEquals(response.getStatus().getMessage(), ((Map)jsonSource.get("status")).get("message"));
        Assert.assertEquals(response.getStatus().getCode(), ((Map)jsonSource.get("status")).get("code"));
        Assert.assertEquals(response.getSets().size(), numSets);
        for (int i=0; i<numSets; i++) {
            SetNode set = response.getSets().get(i);
            Assert.assertEquals(set.getChildren().size(), numChildren);
            Assert.assertEquals(set.getSetName(), "setName_" + i);
            Assert.assertEquals(set.getSetPath(), "setPath_" + i);
            Assert.assertEquals(set.getThermostats().size(), thermostatList.size());
            Assert.assertTrue(set.getThermostats().containsAll(thermostatList));
            for (int j=0; j<numChildren; j++) {
                SetNode childSet = set.getChildren().get(j);
                Assert.assertEquals(childSet.getChildren().size(), 0);
                Assert.assertEquals(childSet.getSetName(), "setName_" + i + "_" + j);
                Assert.assertEquals(childSet.getSetPath(), "setPath_" + i + "_" + j);
                Assert.assertTrue(childSet.getChildren().isEmpty());
                Assert.assertEquals(childSet.getThermostats().size(), childThermostatList.size());
                Assert.assertTrue(childSet.getThermostats().containsAll(childThermostatList));
            }
        }
    }
}

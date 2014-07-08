package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.ecobee.message.DeviceDataResponse;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.message.partial.Status;

public class EcobeeMockApiService {
    private final static DateTimeFormatter dateTimeFormatter =
        DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZoneUTC();
    @Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;
    public DeviceDataResponse getRuntimeReport() {
        DeviceDataResponse deviceDataResponse = null;
        List<RuntimeReport> runtimeReports = new ArrayList<RuntimeReport>();
        float indoortemp = 2.4f, outdoortemp = 4.2f, coolTemp = 2.0f;
        for (int i = 0, day = 1, month = 1, year = 2000; i < 1000; i++) {
            LocalDateTime thermostatStartTime =
                dateTimeFormatter.parseDateTime(new Integer(month).toString() + "/" + day + "/" + year + " 18:37:12").withZone(
                    DateTimeZone.forOffsetHoursMinutes(2, 30)).toLocalDateTime();
            RuntimeReportRow runtimeReportRow =
                new RuntimeReportRow(thermostatStartTime, "Testing", indoortemp, outdoortemp, coolTemp, 34.0f, 8);
            if (month == 2 && day == 28 && (year % 4) != 0) {
                day = 1;
            }
            if (day == 31) {
                day = 1;
            }
            day++;
            if (month == 12) {
                month = 1;
            }
            indoortemp = indoortemp + 0.1f;
            outdoortemp = outdoortemp + 0.1f;
            coolTemp = coolTemp + 0.1f;
            List<RuntimeReportRow> runtimeReportRows = new ArrayList<RuntimeReportRow>();
            runtimeReportRows.add(runtimeReportRow);
            RuntimeReport runtimeReport = new RuntimeReport("T" + i, 2, runtimeReportRows);
            runtimeReports.add(runtimeReport);
        }
        deviceDataResponse = new DeviceDataResponse(new Status(ecobeeDataConfiguration.getRuntimeReport(), "Tested"), runtimeReports);
        return deviceDataResponse;
    }

    public HierarchyResponse getHierarchyList() {
        List<SetNode> setNodes = new ArrayList<SetNode>();
        long thermostat = 222222;
        String node = "Node";
        for (int i = 0; i < 300; i++, thermostat++) {
            List<String> thermostats = new ArrayList<String>();
            thermostats.add(new Long(thermostat).toString());
            SetNode setNode = new SetNode(node + i, "\\data", null, thermostats);
            setNodes.add(setNode);
        }

        HierarchyResponse hierarchyResponse = new HierarchyResponse(setNodes, new Status(ecobeeDataConfiguration.getHierarchy(), "Hierarchy Tested"));
        return hierarchyResponse;
    }

}

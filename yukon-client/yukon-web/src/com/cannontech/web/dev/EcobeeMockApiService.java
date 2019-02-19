package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.ecobee.message.DeviceDataResponse;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportRequest;
import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.message.partial.Status;

public class EcobeeMockApiService {
    private static final Duration fiveMinutes = Duration.standardMinutes(5);
    @Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;
    
    public DeviceDataResponse getRuntimeReport(RuntimeReportRequest request) {
        
        // Add 5-minute intervals to start and stop dates to get the actual start and stop time
        Instant startInstant = request.getStartDate().plus(Duration.standardMinutes(request.getStartInterval() * 5));
        Instant endInstant = request.getEndDate().plus(Duration.standardMinutes(request.getEndInterval() * 5));
        
        List<RuntimeReport> runtimeReports = new ArrayList<>();
        for (String serialNumber : request.getSelection().getSelectionMatch()) {
            
            List<RuntimeReportRow> rows = new ArrayList<RuntimeReportRow>();
            for (Instant intervalStart = startInstant; intervalStart.isBefore(endInstant); intervalStart = intervalStart.plus(fiveMinutes)) {
                // Use the minute of hour as a seed for response values
                int minute = intervalStart.toDateTime().getMinuteOfHour();
                
                // Show event in last 30 minutes of every hour
                String event = minute > 30 ? "Yukon Cycle" : "";
                
                // Indoor and outdoor temps rise through the hour
                Float inTemp = 70.0f + (0.1f * minute);
                Float outTemp = 80.0f + (0.1f * minute);
                
                // Static setpoint values
                Float coolSetPoint = 72f;
                Float heatSetPoint = 68f;
                
                // Runtime for a given five-minute interval is the hour of the day * 5 seconds.
                // This causes runtime to scale from 0 to 23 minutes per hour when the intervals are added up.
                int hour = intervalStart.toDateTime().getHourOfDay();
                int runtime = hour * 5;
                
                LocalDateTime localIntervalStart = new LocalDateTime(intervalStart);
                RuntimeReportRow row = new RuntimeReportRow(localIntervalStart, event, inTemp, outTemp, coolSetPoint, heatSetPoint, runtime);
                rows.add(row);
            }
            
            RuntimeReport report = new RuntimeReport(serialNumber, rows.size(), rows);
            runtimeReports.add(report);
            
        }
        
        Status status = new Status(ecobeeDataConfiguration.getRuntimeReport(), "Success");
        DeviceDataResponse deviceDataResponse = new DeviceDataResponse(status, runtimeReports);
        
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

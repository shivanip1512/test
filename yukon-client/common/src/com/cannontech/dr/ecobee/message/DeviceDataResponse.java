package com.cannontech.dr.ecobee.message;

import java.util.List;

import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DeviceDataResponse extends BaseResponse {
    private final String startDate;
    private final int startInterval;
    private final String endDate;
    private final int endInterval;
    private final String columns;
    private final List<RuntimeReport> reportList;
    private final List<String> sensorList;
    
    @JsonCreator
    public DeviceDataResponse(@JsonProperty("status") Status status,
                              @JsonProperty("startDate") String startDate,
                              @JsonProperty("startInterval") int startInterval,
                              @JsonProperty("endDate") String endDate,
                              @JsonProperty("endInterval") int endInterval,
                              @JsonProperty("columns") String columns,
                              @JsonProperty("reportList") List<RuntimeReport> reportList,
                              @JsonProperty("sensorList") List<String> sensorList) {
        super(status);
        this.startDate = startDate;
        this.startInterval = startInterval;
        this.endDate = endDate;
        this.endInterval = endInterval;
        this.columns = columns;
        this.reportList = reportList;
        this.sensorList = sensorList;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getStartInterval() {
        return startInterval;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getEndInterval() {
        return endInterval;
    }

    public String getColumns() {
        return columns;
    }

    public List<RuntimeReport> getReportList() {
        return reportList;
    }

    public List<String> getSensorList() {
        return sensorList;
    }
}

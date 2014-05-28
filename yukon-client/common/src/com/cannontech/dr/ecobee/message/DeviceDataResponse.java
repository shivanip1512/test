package com.cannontech.dr.ecobee.message;

import java.util.List;

import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class DeviceDataResponse extends BaseResponse {
    private final List<RuntimeReport> reportList;

    @JsonCreator
    public DeviceDataResponse(@JsonProperty("status") Status status,
                              @JsonProperty("reportList") List<RuntimeReport> reportList) {
        super(status);
        this.reportList = reportList != null ? ImmutableList.copyOf(reportList) : ImmutableList.<RuntimeReport>of();
    }

    public List<RuntimeReport> getReportList() {
        return reportList;
    }
}

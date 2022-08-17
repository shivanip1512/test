package com.cannontech.dr.ecobee.message;

import java.util.List;

import com.cannontech.dr.ecobee.message.partial.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimeReportJobStatusResponse extends BaseResponse {

    private final List<EcobeeReportJob> jobs;

    @JsonCreator
    public RuntimeReportJobStatusResponse(@JsonProperty("jobs") List<EcobeeReportJob> jobs,
            @JsonProperty("status") Status status) {
        super(status);
        this.jobs = jobs != null ? ImmutableList.copyOf(jobs) : ImmutableList.<EcobeeReportJob> of();
    }

    public List<EcobeeReportJob> getJobs() {
        return jobs;
    }



}

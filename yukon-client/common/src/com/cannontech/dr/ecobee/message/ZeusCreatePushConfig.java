package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusCreatePushConfig {
    @JsonProperty("reporting_url") private String reportingUrl;
    @JsonProperty("utility_rsa_key_base64") private String privateKey;

    @JsonCreator
    public ZeusCreatePushConfig(@JsonProperty("reporting_url") String reportingUrl,
            @JsonProperty("utility_rsa_key_base64") String privateKey) {
        this.reportingUrl = reportingUrl;
        this.privateKey = privateKey;
    }

    public ZeusCreatePushConfig() {
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getReportingUrl() {
        return reportingUrl;
    }
}

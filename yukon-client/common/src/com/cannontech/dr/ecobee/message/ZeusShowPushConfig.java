package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusShowPushConfig {
    @JsonProperty("reporting_url") private String reportingUrl;
    @JsonProperty("utility_rsa_key_base64_sha1") private String privateKey;

    public String getReportingUrl() {
        return reportingUrl;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setReportingUrl(String reportingUrl) {
        this.reportingUrl = reportingUrl;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}

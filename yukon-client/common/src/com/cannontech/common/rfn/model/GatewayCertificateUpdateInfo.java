package com.cannontech.common.rfn.model;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;

public class GatewayCertificateUpdateInfo {
    private final int updateId;
    private final String certificateId;
    private final Instant sendDate;
    private final String fileName;
    private Map<Integer, GatewayCertificateUpdateStatus> gatewayStatuses;
    
    public GatewayCertificateUpdateInfo(int updateId, String certificateId, Instant sendDate, String fileName) {
        this.updateId = updateId;
        this.certificateId = certificateId;
        this.sendDate = sendDate;
        this.fileName = fileName;
        gatewayStatuses = new HashMap<>();
    }
    
    public void addGatewayStatus(Integer gatewayId, GatewayCertificateUpdateStatus status) {
        gatewayStatuses.put(gatewayId, status);
    }
    
    public int getUpdateId() {
        return updateId;
    }

    public String getCertificateId() {
        return certificateId;
    }
    
    public Instant getSendDate() {
        return sendDate;
    }
    
    public String getFileName() {
        return fileName;
    }

    public Map<Integer, GatewayCertificateUpdateStatus> getGatewayStatuses() {
        return gatewayStatuses;
    }
    
}

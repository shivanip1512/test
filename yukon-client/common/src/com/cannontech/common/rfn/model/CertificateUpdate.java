package com.cannontech.common.rfn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.util.StringUtils;

public class CertificateUpdate {
    
    private Instant timestamp;
    private String fileName;
    private String updateId;
    private int yukonUpdateId;
    private List<RfnGateway> pending = new ArrayList<>();
    private Map<RfnGateway, GatewayCertificateUpdateStatus> failed = new HashMap<>();
    private List<RfnGateway> successful = new ArrayList<>();
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getUpdateId() {
        return updateId;
    }
    
    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }
    
    public List<RfnGateway> getPending() {
        return pending;
    }
    
    public void setPending(List<RfnGateway> pending) {
        this.pending = pending;
    }
    
    public Map<RfnGateway, GatewayCertificateUpdateStatus> getFailed() {
        return failed;
    }
    
    public void setFailed(Map<RfnGateway, GatewayCertificateUpdateStatus> failed) {
        this.failed = failed;
    }
    
    public List<RfnGateway> getSuccessful() {
        return successful;
    }
    
    public void setSuccessful(List<RfnGateway> successful) {
        this.successful = successful;
    }
    
    public void setYukonUpdateId(int yukonUpdateId) {
        this.yukonUpdateId = yukonUpdateId;
    }
    
    public int getYukonUpdateId() {
        return yukonUpdateId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((failed == null) ? 0 : failed.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((pending == null) ? 0 : pending.hashCode());
        result = prime * result + ((successful == null) ? 0 : successful.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((updateId == null) ? 0 : updateId.hashCode());
        result = prime * result + yukonUpdateId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CertificateUpdate other = (CertificateUpdate) obj;
        if (failed == null) {
            if (other.failed != null)
                return false;
        } else if (!failed.equals(other.failed))
            return false;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (pending == null) {
            if (other.pending != null)
                return false;
        } else if (!pending.equals(other.pending))
            return false;
        if (successful == null) {
            if (other.successful != null)
                return false;
        } else if (!successful.equals(other.successful))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (updateId == null) {
            if (other.updateId != null)
                return false;
        } else if (!updateId.equals(other.updateId))
            return false;
        if (yukonUpdateId != other.yukonUpdateId)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("CertificateUpdate [timestamp=%s, fileName=%s, updateId=%s, yukonUpdateId=%s, pending=%s, failed=%s, successful=%s]",
                timestamp, fileName, updateId, yukonUpdateId, pending, failed, successful);
    }

    public List<RfnDevice> getGateways() {
        List<RfnDevice> all = new ArrayList<>();
        all.addAll(successful);
        all.addAll(failed.keySet());
        all.addAll(pending);
        
        Collections.sort(all, new Comparator<RfnDevice>() {
            @Override
            public int compare(RfnDevice o1, RfnDevice o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        
        return all;
    }
    
    public String getSuccessPercent() {
        int total = successful.size() + failed.size() + pending.size();
        return StringUtils.percent(total, successful.size(), 2);
    }
    
    public String getFailedPercent() {
        int total = successful.size() + failed.size() + pending.size();
        return StringUtils.percent(total, failed.size(), 2);
    }
    
    public String getTotalPercent() {
        int total = successful.size() + failed.size() + pending.size();
        return StringUtils.percent(total, failed.size() + successful.size(), 2);
    }
    
}
package com.cannontech.common.rfn.model;

import org.joda.time.Instant;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.StringUtils;

/**
 * Summary of an entire firmware upgrade operation.
 */
public class RfnGatewayFirmwareUpdateSummary {
    private int updateId;
    private Instant sendDate;
    private int totalGateways;
    private int totalUpdateServers;
    private int pending;
    private int failed;
    private int successful;
    
    public enum GatewayFirmwareUpdateSummaryStatus implements DisplayableEnum {
        IN_PROGRESS,
        COMPLETE;
        
        private String keyBase = "yukon.common.rfn.gatewayFirmwareUpdateSummary.";
        
        @Override
        public String getFormatKey() {
            return keyBase + name();
        }
    }

    public int getUpdateId() {
        return updateId;
    }

    public void setUpdateId(int updateId) {
        this.updateId = updateId;
    }

    public Instant getSendDate() {
        return sendDate;
    }

    public void setSendDate(Instant sendDate) {
        this.sendDate = sendDate;
    }

    public int getTotalGateways() {
        return totalGateways;
    }

    public void setTotalGateways(int totalGateways) {
        this.totalGateways = totalGateways;
    }

    public int getTotalUpdateServers() {
        return totalUpdateServers;
    }

    public void setTotalUpdateServers(int totalUpdateServers) {
        this.totalUpdateServers = totalUpdateServers;
    }

    public GatewayFirmwareUpdateSummaryStatus getStatus() {
        if (pending > 0) {
            return GatewayFirmwareUpdateSummaryStatus.IN_PROGRESS;
        }
        return GatewayFirmwareUpdateSummaryStatus.COMPLETE;
    }
    
    public boolean isComplete() {
        return pending == 0;
    }
    
    public int getGatewayUpdatesPending() {
        return pending;
    }

    public void setGatewayUpdatesPending(int gatewayUpdatesPending) {
        pending = gatewayUpdatesPending;
    }

    public int getGatewayUpdatesFailed() {
        return failed;
    }

    public void setGatewayUpdatesFailed(int gatewayUpdatesFailed) {
        failed = gatewayUpdatesFailed;
    }

    public int getGatewayUpdatesSuccessful() {
        return successful;
    }

    public void setGatewayUpdatesSuccessful(int gatewayUpdatesSuccessful) {
        successful = gatewayUpdatesSuccessful;
    }
    
    public String getSuccessPercent() {
        int total = successful + failed + pending;
        return StringUtils.percent(total, successful, 2);
    }
    
    public String getFailedPercent() {
        int total = successful + failed + pending;
        return StringUtils.percent(total, failed, 2);
    }
    
    public String getTotalPercent() {
        int total = successful + failed + pending;
        return StringUtils.percent(total, failed + successful, 2);
    }
    
}

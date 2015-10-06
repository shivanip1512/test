package com.cannontech.common.rfn.model;

import org.joda.time.Instant;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Summary of an entire firmware upgrade operation.
 */
public class RfnGatewayFirmwareUpdateSummary {
    private int updateId;
    private Instant sendDate;
    private int totalGateways;
    private int totalUpdateServers;
    private int gatewayUpdatesPending;
    private int gatewayUpdatesFailed;
    private int gatewayUpdatesSuccessful;
    
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
        if (gatewayUpdatesPending > 0) {
            return GatewayFirmwareUpdateSummaryStatus.IN_PROGRESS;
        }
        return GatewayFirmwareUpdateSummaryStatus.COMPLETE;
    }

    public int getGatewayUpdatesPending() {
        return gatewayUpdatesPending;
    }

    public void setGatewayUpdatesPending(int gatewayUpdatesPending) {
        this.gatewayUpdatesPending = gatewayUpdatesPending;
    }

    public int getGatewayUpdatesFailed() {
        return gatewayUpdatesFailed;
    }

    public void setGatewayUpdatesFailed(int gatewayUpdatesFailed) {
        this.gatewayUpdatesFailed = gatewayUpdatesFailed;
    }

    public int getGatewayUpdatesSuccessful() {
        return gatewayUpdatesSuccessful;
    }

    public void setGatewayUpdatesSuccessful(int gatewayUpdatesSuccessful) {
        this.gatewayUpdatesSuccessful = gatewayUpdatesSuccessful;
    }
    
}

package com.cannontech.web.support.development.database.objects;

import com.cannontech.common.events.model.EventSource;
import com.cannontech.web.support.development.database.service.impl.DevEventLogCreationService;

public class DevEventLog {
    
    private boolean accountEventLog = true;
    private boolean commandRequestExecutorEventLog = true;
    private boolean commandScheduleEventLog = true;
    private boolean databaseMigrationEventLog = true;
    private boolean demandResponseEventLog = true;
    private boolean hardwareEventLog = true;
    private boolean inventoryConfigEventLog = true;
    private boolean meteringEventLog = true;
    private boolean outageEventLog = true;
    private boolean rfnDeviceEventLog = true;
    private boolean starsEventLog = true;
    private boolean systemEventLog = true;
    private boolean validationEventLog = true;
    private boolean veeReviewEventLog = true;
    private boolean zigbeeEventLog = true;
    
    private String username = "yukon";
    private String indicatorString = "fake_";
    
    private int iterations = 1;
    
    private EventSource eventSource = EventSource.OPERATOR;
    
    public boolean isAccountEventLog() {
        return accountEventLog;
    }

    public void setAccountEventLog(boolean accountEventLog) {
        this.accountEventLog = accountEventLog;
    }

    public boolean isCommandRequestExecutorEventLog() {
        return commandRequestExecutorEventLog;
    }

    public void setCommandRequestExecutorEventLog(boolean commandRequestExecutorEventLog) {
        this.commandRequestExecutorEventLog = commandRequestExecutorEventLog;
    }

    public boolean isCommandScheduleEventLog() {
        return commandScheduleEventLog;
    }

    public void setCommandScheduleEventLog(boolean commandScheduleEventLog) {
        this.commandScheduleEventLog = commandScheduleEventLog;
    }

    public boolean isDatabaseMigrationEventLog() {
        return databaseMigrationEventLog;
    }

    public void setDatabaseMigrationEventLog(boolean databaseMigrationEventLog) {
        this.databaseMigrationEventLog = databaseMigrationEventLog;
    }

    public boolean isDemandResponseEventLog() {
        return demandResponseEventLog;
    }

    public void setDemandResponseEventLog(boolean demandResponseEventLog) {
        this.demandResponseEventLog = demandResponseEventLog;
    }

    public boolean isHardwareEventLog() {
        return hardwareEventLog;
    }

    public void setHardwareEventLog(boolean hardwareEventLog) {
        this.hardwareEventLog = hardwareEventLog;
    }

    public boolean isInventoryConfigEventLog() {
        return inventoryConfigEventLog;
    }

    public void setInventoryConfigEventLog(boolean inventoryConfigEventLog) {
        this.inventoryConfigEventLog = inventoryConfigEventLog;
    }

    public boolean isMeteringEventLog() {
        return meteringEventLog;
    }

    public void setMeteringEventLog(boolean meteringEventLog) {
        this.meteringEventLog = meteringEventLog;
    }

    public boolean isOutageEventLog() {
        return outageEventLog;
    }

    public void setOutageEventLog(boolean outageEventLog) {
        this.outageEventLog = outageEventLog;
    }

    public boolean isRfnDeviceEventLog() {
        return rfnDeviceEventLog;
    }

    public void setRfnDeviceEventLog(boolean rfnDeviceEventLog) {
        this.rfnDeviceEventLog = rfnDeviceEventLog;
    }

    public boolean isStarsEventLog() {
        return starsEventLog;
    }

    public void setStarsEventLog(boolean starsEventLog) {
        this.starsEventLog = starsEventLog;
    }

    public boolean isSystemEventLog() {
        return systemEventLog;
    }

    public void setSystemEventLog(boolean systemEventLog) {
        this.systemEventLog = systemEventLog;
    }

    public boolean isValidationEventLog() {
        return validationEventLog;
    }

    public void setValidationEventLog(boolean validationEventLog) {
        this.validationEventLog = validationEventLog;
    }

    public boolean isVeeReviewEventLog() {
        return veeReviewEventLog;
    }

    public void setVeeReviewEventLog(boolean veeReviewEventLog) {
        this.veeReviewEventLog = veeReviewEventLog;
    }

    public boolean isZigbeeEventLog() {
        return zigbeeEventLog;
    }

    public void setZigbeeEventLog(boolean zigbeeEventLog) {
        this.zigbeeEventLog = zigbeeEventLog;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIndicatorString() {
        return indicatorString;
    }

    public void setIndicatorString(String indicatorString) {
        this.indicatorString = indicatorString;
    }

    public EventSource getEventSource() {
        return eventSource;
    }

    public void setEventSource(EventSource eventSource) {
        this.eventSource = eventSource;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getTotal() {
        int total = 0;

        // Numbers represent number of entries per service
        // these can be found in DevEventLogCreationService
        
        total += accountEventLog ? DevEventLogCreationService.ACCOUNT_EVENT_LOG : 0;
        total += commandRequestExecutorEventLog ? DevEventLogCreationService.COMMAND_REQUEST_EXECUTOR__EVENT_LOG : 0;
        total += commandScheduleEventLog ? DevEventLogCreationService.COMMAND_SCHEDULE__EVENT_LOG : 0;
        total += databaseMigrationEventLog ? DevEventLogCreationService.DATABASE_MIGRATION_EVENT_LOG : 0;
        total += demandResponseEventLog ? DevEventLogCreationService.DEMAND_RESPONSE_EVENT_LOG : 0;
        total += hardwareEventLog ? DevEventLogCreationService.HARDWARE_EVENT_LOG : 0;
        total += inventoryConfigEventLog ? DevEventLogCreationService.INVENTORY_CONFIG_EVENT_LOG : 0;
        total += meteringEventLog ? DevEventLogCreationService.METERING_EVENT_LOG : 0;
        total += outageEventLog ? DevEventLogCreationService.OUTAGE_EVENT_LOG : 0;
        total += rfnDeviceEventLog ? DevEventLogCreationService.RFN_DEVICE_EVENT_LOG: 0;
        total += starsEventLog ? DevEventLogCreationService.STARS_EVENT_LOG : 0;
        total += systemEventLog ? DevEventLogCreationService.SYSTEM_EVENT_LOG : 0;
        total += validationEventLog ? DevEventLogCreationService.VALIDATION_EVENT_LOG : 0;
        total += veeReviewEventLog ? DevEventLogCreationService.VEE_REVIEW_EVENT_LOG: 0;
        total += zigbeeEventLog ? DevEventLogCreationService.ZIGBEE_EVENT_LOG : 0;

        return total * iterations;
    }
}

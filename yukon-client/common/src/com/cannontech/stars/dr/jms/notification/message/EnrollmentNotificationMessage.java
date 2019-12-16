package com.cannontech.stars.dr.jms.notification.message;

import java.io.Serializable;

import org.joda.time.Instant;

public class EnrollmentNotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer inventoryId;
    private Integer accountId;
    private Integer loadGroupId;
    private Integer relayNumber;
    private Integer programId;
    private Instant enrollmentStartTime;
    private Instant enrollmentStopTime;
    private DRNotificationMessageType messageType;

    public Instant getEnrollmentStartTime() {
        return enrollmentStartTime;
    }

    public void setEnrollmentStartTime(Instant enrollmentStartTime) {
        this.enrollmentStartTime = enrollmentStartTime;
    }

    public Instant getEnrollmentStopTime() {
        return enrollmentStopTime;
    }

    public void setEnrollmentStopTime(Instant enrollmentStopTime) {
        this.enrollmentStopTime = enrollmentStopTime;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setLoadGroupId(Integer loadGroupId) {
        this.loadGroupId = loadGroupId;
    }

    public void setRelayNumber(Integer relayNumber) {
        this.relayNumber = relayNumber;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getLoadGroupId() {
        return loadGroupId;
    }

    public int getRelayNumber() {
        return relayNumber;
    }

    public int getProgramId() {
        return programId;
    }

    public DRNotificationMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(DRNotificationMessageType messageType) {
        this.messageType = messageType;
    }

}

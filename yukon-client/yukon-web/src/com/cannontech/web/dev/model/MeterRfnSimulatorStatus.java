/**
 * 
 */
package com.cannontech.web.dev.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.Instant;

public class MeterRfnSimulatorStatus {
    private final AtomicBoolean isRunningRfnBillingType;
    private final AtomicBoolean isRunningRfnCurrentType;
    private final AtomicBoolean isRunningRfnStatusType;
    private final AtomicBoolean isRunningRfnProfileType;
    private final AtomicBoolean isRunningRfnIntervalType;

    private final AtomicBoolean isCancelledRfnBillingType;
    private final AtomicBoolean isCancelledRfnCurrentType;
    private final AtomicBoolean isCancelledRfnStatusType;
    private final AtomicBoolean isCancelledRfnProfileType;
    private final AtomicBoolean isCancelledRfnIntervalType;

    private final AtomicLong numCompleteRfnBillingType;
    private final AtomicLong numCompleteRfnCurrentType;
    private final AtomicLong numCompleteRfnStatusType;
    private final AtomicLong numCompleteRfnProfileType;
    private final AtomicLong numCompleteRfnIntervalType;

    private volatile Instant lastFinishedInjectionRfnBillingType;
    private volatile Instant lastFinishedInjectionRfnCurrentType;
    private volatile Instant lastFinishedInjectionRfnStatusType;
    private volatile Instant lastFinishedInjectionRfnProfileType;
    private volatile Instant lastFinishedInjectionRfnIntervalType;

    private volatile long numTotalRfnMeters;
    private volatile long numTotalRfnCurrentType;
    private volatile long numTotalRfnStatusType;
    private volatile long numTotalRfnProfileType;
    private volatile long numTotalRfnIntervalType;

    private volatile String errorMessage;

    public MeterRfnSimulatorStatus() {
        this.numCompleteRfnBillingType = new AtomicLong();
        this.numCompleteRfnCurrentType = new AtomicLong();
        this.numCompleteRfnStatusType = new AtomicLong();
        this.numCompleteRfnProfileType = new AtomicLong();
        this.numCompleteRfnIntervalType = new AtomicLong();

        this.isRunningRfnBillingType = new AtomicBoolean();
        this.isRunningRfnCurrentType = new AtomicBoolean();
        this.isRunningRfnStatusType = new AtomicBoolean();
        this.isRunningRfnProfileType = new AtomicBoolean();
        this.isRunningRfnIntervalType = new AtomicBoolean();

        this.isCancelledRfnBillingType = new AtomicBoolean();
        this.isCancelledRfnCurrentType = new AtomicBoolean();
        this.isCancelledRfnStatusType = new AtomicBoolean();
        this.isCancelledRfnProfileType = new AtomicBoolean();
        this.isCancelledRfnIntervalType = new AtomicBoolean();

        this.numTotalRfnMeters = 0;
        this.numTotalRfnCurrentType = 0;
        this.numTotalRfnStatusType = 0;
        this.numTotalRfnProfileType = 0;
        this.numTotalRfnIntervalType = 0;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getLastFinishedInjectionRfnBillingType() {
        return lastFinishedInjectionRfnBillingType;
    }

    public void setLastFinishedInjectionRfnBillingType(Instant lastFinishedInjectionRfnBillingType) {
        this.lastFinishedInjectionRfnBillingType = lastFinishedInjectionRfnBillingType;
    }

    public Instant getLastFinishedInjectionRfnCurrentType() {
        return lastFinishedInjectionRfnCurrentType;
    }

    public void setLastFinishedInjectionRfnCurrentType(Instant lastFinishedInjectionRfnCurrentType) {
        this.lastFinishedInjectionRfnCurrentType = lastFinishedInjectionRfnCurrentType;
    }

    public Instant getLastFinishedInjectionRfnStatusType() {
        return lastFinishedInjectionRfnStatusType;
    }

    public void setLastFinishedInjectionRfnStatusType(Instant lastFinishedInjectionRfnStatusType) {
        this.lastFinishedInjectionRfnStatusType = lastFinishedInjectionRfnStatusType;
    }

    public Instant getLastFinishedInjectionRfnProfileType() {
        return lastFinishedInjectionRfnProfileType;
    }

    public void setLastFinishedInjectionRfnProfileType(Instant lastFinishedInjectionRfnProfileType) {
        this.lastFinishedInjectionRfnProfileType = lastFinishedInjectionRfnProfileType;
    }

    public Instant getLastFinishedInjectionRfnIntervalType() {
        return lastFinishedInjectionRfnIntervalType;
    }

    public void setLastFinishedInjectionRfnIntervalType(Instant lastFinishedInjectionRfnIntervalType) {
        this.lastFinishedInjectionRfnIntervalType = lastFinishedInjectionRfnIntervalType;
    }

    public long getNumTotalRfnMeters() {
        return numTotalRfnMeters;
    }

    public void setNumTotalRfnMeters(long numTotalRfnMeters) {
        this.numTotalRfnMeters = numTotalRfnMeters;
    }

    public long getNumTotalRfnCurrentType() {
        return numTotalRfnCurrentType;
    }

    public void setNumTotalRfnCurrentType(long numTotalRfnCurrentType) {
        this.numTotalRfnCurrentType = numTotalRfnCurrentType;
    }

    public long getNumTotalRfnStatusType() {
        return numTotalRfnStatusType;
    }

    public void setNumTotalRfnStatusType(long numTotalRfnStatusType) {
        this.numTotalRfnStatusType = numTotalRfnStatusType;
    }

    public long getNumTotalRfnProfileType() {
        return numTotalRfnProfileType;
    }

    public void setNumTotalRfnProfileType(long numTotalRfnProfileType) {
        this.numTotalRfnProfileType = numTotalRfnProfileType;
    }

    public long getNumTotalRfnIntervalType() {
        return numTotalRfnIntervalType;
    }

    public void setNumTotalRfnIntervalType(long numTotalRfnIntervalType) {
        this.numTotalRfnIntervalType = numTotalRfnIntervalType;
    }

    public AtomicBoolean getIsRunningRfnBillingType() {
        return isRunningRfnBillingType;
    }

    public AtomicBoolean getIsRunningRfnCurrentType() {
        return isRunningRfnCurrentType;
    }

    public AtomicBoolean getIsRunningRfnStatusType() {
        return isRunningRfnStatusType;
    }

    public AtomicBoolean getIsRunningRfnProfileType() {
        return isRunningRfnProfileType;
    }

    public AtomicBoolean getIsRunningRfnIntervalType() {
        return isRunningRfnIntervalType;
    }

    public void setIsRunningRfnBillingType(boolean isRunning) {
        this.isRunningRfnBillingType.set(isRunning);
    }

    public void setIsRunningRfnCurrentType(boolean isRunning) {
        this.isRunningRfnCurrentType.set(isRunning);
    }

    public void setIsRunningRfnStatusType(boolean isRunning) {
        this.isRunningRfnStatusType.set(isRunning);
    }

    public void setIsRunningRfnProfileType(boolean isRunning) {
        this.isRunningRfnProfileType.set(isRunning);
    }

    public void setIsRunningRfnIntervalTType(boolean isRunning) {
        this.isRunningRfnIntervalType.set(isRunning);
    }

    public AtomicBoolean getIsCancelledRfnBillingType() {
        return isCancelledRfnBillingType;
    }

    public AtomicBoolean getIsCancelledRfnCurrentType() {
        return isCancelledRfnCurrentType;
    }

    public AtomicBoolean getIsCancelledRfnStatusType() {
        return isCancelledRfnStatusType;
    }

    public AtomicBoolean getIsCancelledRfnProfileType() {
        return isCancelledRfnProfileType;
    }

    public AtomicBoolean getIsCancelledRfnIntervalType() {
        return isCancelledRfnIntervalType;
    }

    public AtomicLong getNumCompleteRfnBillingType() {
        return numCompleteRfnBillingType;
    }

    public AtomicLong getNumCompleteRfnCurrentType() {
        return numCompleteRfnCurrentType;
    }

    public AtomicLong getNumCompleteRfnStatusType() {
        return numCompleteRfnStatusType;
    }

    public AtomicLong getNumCompleteRfnProfileType() {
        return numCompleteRfnProfileType;
    }

    public AtomicLong getNumCompleteRfnIntervalType() {
        return numCompleteRfnIntervalType;
    }

}

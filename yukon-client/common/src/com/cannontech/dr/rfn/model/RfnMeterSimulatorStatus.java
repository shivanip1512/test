/**
 * 
 */
package com.cannontech.dr.rfn.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.Instant;

public class RfnMeterSimulatorStatus {
    private volatile Instant lastFinishedInjectionRfnBillingType;
    private volatile Instant lastFinishedInjectionRfnCurrentType;
    private volatile Instant lastFinishedInjectionRfnStatusType;
    private volatile Instant lastFinishedInjectionRfnProfileType;
    private volatile Instant lastFinishedInjectionRfnIntervalType;

    private AtomicLong numCompleteRfnBillingType;
    private AtomicLong numCompleteRfnCurrentType;
    private AtomicLong numCompleteRfnStatusType;
    private AtomicLong numCompleteRfnProfileType;
    private AtomicLong numCompleteRfnIntervalType;

    private volatile String errorMessage;

    private AtomicBoolean isRunningRfnBillingType;
    private AtomicBoolean isRunningRfnCurrentType;
    private AtomicBoolean isRunningRfnStatusType;
    private AtomicBoolean isRunningRfnProfileType;
    private AtomicBoolean isRunningRfnIntervalType;

    private String paoType;

    public RfnMeterSimulatorStatus() {
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
    }

    public static void reInitializeStatus(RfnMeterSimulatorStatus status) {
        status.numCompleteRfnBillingType = new AtomicLong();
        status.numCompleteRfnCurrentType = new AtomicLong();
        status.numCompleteRfnStatusType = new AtomicLong();
        status.numCompleteRfnProfileType = new AtomicLong();
        status.numCompleteRfnIntervalType = new AtomicLong();

        status.isRunningRfnBillingType = new AtomicBoolean();
        status.isRunningRfnCurrentType = new AtomicBoolean();
        status.isRunningRfnStatusType = new AtomicBoolean();
        status.isRunningRfnProfileType = new AtomicBoolean();
        status.isRunningRfnIntervalType = new AtomicBoolean();

        status.errorMessage = null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void resetCompletionState() {
        this.numCompleteRfnBillingType = new AtomicLong();
        this.numCompleteRfnCurrentType = new AtomicLong();
        this.numCompleteRfnStatusType = new AtomicLong();
        this.numCompleteRfnProfileType = new AtomicLong();
        this.numCompleteRfnIntervalType = new AtomicLong();
    }

    /**
     * @return the lastFinishedInjectionRfnBillingType
     */
    public Instant getLastFinishedInjectionRfnBillingType() {
        return lastFinishedInjectionRfnBillingType;
    }

    /**
     * @param lastFinishedInjectionRfnBillingType the lastFinishedInjectionRfnBillingType to set
     */
    public void setLastFinishedInjectionRfnBillingType(Instant lastFinishedInjectionRfnBillingType) {
        this.lastFinishedInjectionRfnBillingType = lastFinishedInjectionRfnBillingType;
    }

    /**
     * @return the lastFinishedInjectionRfnCurrentType
     */
    public Instant getLastFinishedInjectionRfnCurrentType() {
        return lastFinishedInjectionRfnCurrentType;
    }

    /**
     * @param lastFinishedInjectionRfnCurrentType the lastFinishedInjectionRfnCurrentType to set
     */
    public void setLastFinishedInjectionRfnCurrentType(Instant lastFinishedInjectionRfnCurrentType) {
        this.lastFinishedInjectionRfnCurrentType = lastFinishedInjectionRfnCurrentType;
    }

    /**
     * @return the lastFinishedInjectionRfnStatusType
     */
    public Instant getLastFinishedInjectionRfnStatusType() {
        return lastFinishedInjectionRfnStatusType;
    }

    /**
     * @param lastFinishedInjectionRfnStatusType the lastFinishedInjectionRfnStatusType to set
     */
    public void setLastFinishedInjectionRfnStatusType(Instant lastFinishedInjectionRfnStatusType) {
        this.lastFinishedInjectionRfnStatusType = lastFinishedInjectionRfnStatusType;
    }

    /**
     * @return the lastFinishedInjectionRfnProfileType
     */
    public Instant getLastFinishedInjectionRfnProfileType() {
        return lastFinishedInjectionRfnProfileType;
    }

    /**
     * @param lastFinishedInjectionRfnProfileType the lastFinishedInjectionRfnProfileType to set
     */
    public void setLastFinishedInjectionRfnProfileType(Instant lastFinishedInjectionRfnProfileType) {
        this.lastFinishedInjectionRfnProfileType = lastFinishedInjectionRfnProfileType;
    }

    /**
     * @return the lastFinishedInjectionRfnIntervalType
     */
    public Instant getLastFinishedInjectionRfnIntervalType() {
        return lastFinishedInjectionRfnIntervalType;
    }

    /**
     * @param lastFinishedInjectionRfnIntervalType the lastFinishedInjectionRfnIntervalType to set
     */
    public void setLastFinishedInjectionRfnIntervalType(Instant lastFinishedInjectionRfnIntervalType) {
        this.lastFinishedInjectionRfnIntervalType = lastFinishedInjectionRfnIntervalType;
    }

    /**
     * @return the numCompleteRfnBillingType
     */
    public AtomicLong getNumCompleteRfnBillingType() {
        return numCompleteRfnBillingType;
    }

    public void setNumCompleteRfnBillingType(AtomicLong numCompleteRfnBillingType) {
        this.numCompleteRfnBillingType = numCompleteRfnBillingType;
    }

    /**
     * @return the numCompleteRfnCurrentType
     */
    public AtomicLong getNumCompleteRfnCurrentType() {
        return numCompleteRfnCurrentType;
    }

    /**
     * @return the numCompleteRfnStatusType
     */
    public AtomicLong getNumCompleteRfnStatusType() {
        return numCompleteRfnStatusType;
    }

    /**
     * @return the numCompleteRfnProfileType
     */
    public AtomicLong getNumCompleteRfnProfileType() {
        return numCompleteRfnProfileType;
    }

    /**
     * @return the numCompleteRfnIntervalType
     */
    public AtomicLong getNumCompleteRfnIntervalType() {
        return numCompleteRfnIntervalType;
    }

    /**
     * @return the isRunningRfnBillingType
     */
    public AtomicBoolean getIsRunningRfnBillingType() {
        return isRunningRfnBillingType;
    }

    /**
     * @return the isRunningRfnCurrentType
     */
    public AtomicBoolean getIsRunningRfnCurrentType() {
        return isRunningRfnCurrentType;
    }

    /**
     * @return the isRunningRfnStatusType
     */
    public AtomicBoolean getIsRunningRfnStatusType() {
        return isRunningRfnStatusType;
    }

    /**
     * @return the isRunningRfnProfileType
     */
    public AtomicBoolean getIsRunningRfnProfileType() {
        return isRunningRfnProfileType;
    }

    /**
     * @return the isRunningRfnIntervalType
     */
    public AtomicBoolean getIsRunningRfnIntervalType() {
        return isRunningRfnIntervalType;
    }

    /**
     * @return the paoType
     */
    public String getPaoType() {
        return paoType;
    }

    /**
     * @param paoType the paoType to set
     */
    public void setPaoType(String paoType) {
        this.paoType = paoType;
    }

}

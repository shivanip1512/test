package com.cannontech.common.device.config.model;

import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.model.tou.Tou;

public class MCT440Configuration extends ConfigurationBase {

    @Override
    public ConfigurationType getType() {
        return ConfigurationType.MCT440;
    }

    // DST
    private Integer timeZoneOffset = 0;

    // Addressing
    private Integer bronzeAddress = 0;
    private Integer leadAddress = 0;
    private Integer collectionAddress = 0;
    private Integer serviceProviderId = 0;

    // Configuration
    private Boolean enableDst = true;
    private Integer timeAdjustTolerance = 15;
    
    // Phase Loss
    private Integer phaseLossThreshold = 95;
    private Integer phaseLossDuration = 5;

    // Demand and LP
    private Integer demandInterval = 5;
    private Integer kwProfileInterval = 60;
    private Integer voltageProfileInterval = 60;

    // TOU
    private Tou tou = new Tou(10);

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public Integer getBronzeAddress() {
        return bronzeAddress;
    }

    public void setBronzeAddress(Integer bronzeAddress) {
        this.bronzeAddress = bronzeAddress;
    }

    public Integer getLeadAddress() {
        return leadAddress;
    }

    public void setLeadAddress(Integer leadAddress) {
        this.leadAddress = leadAddress;
    }

    public Integer getCollectionAddress() {
        return collectionAddress;
    }

    public void setCollectionAddress(Integer collectionAddress) {
        this.collectionAddress = collectionAddress;
    }

    public Integer getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Integer serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public Boolean getEnableDst() {
        return enableDst;
    }

    public void setEnableDst(Boolean enableDst) {
        this.enableDst = enableDst;
    }

    public Integer getTimeAdjustTolerance() {
        return timeAdjustTolerance;
    }

    public void setTimeAdjustTolerance(Integer timeAdjustTolerance) {
        this.timeAdjustTolerance = timeAdjustTolerance;
    }

    public Integer getDemandInterval() {
        return demandInterval;
    }

    public void setDemandInterval(Integer demandInterval) {
        this.demandInterval = demandInterval;
    }

    public Integer getKwProfileInterval() {
        return kwProfileInterval;
    }

    public void setKwProfileInterval(Integer kwProfileInterval) {
        this.kwProfileInterval = kwProfileInterval;
    }

    public Integer getVoltageProfileInterval() {
        return voltageProfileInterval;
    }

    public void setVoltageProfileInterval(Integer voltageProfileInterval) {
        this.voltageProfileInterval = voltageProfileInterval;
    }

    public Tou getTou() {
        return tou;
    }

    public void setTou(Tou tou) {
        this.tou = tou;
    }

    public Integer getPhaseLossThreshold() {
        return phaseLossThreshold;
    }

    public void setPhaseLossThreshold(Integer phaseLossThreshold) {
        this.phaseLossThreshold = phaseLossThreshold;
    }

    public Integer getPhaseLossDuration() {
        return phaseLossDuration;
    }

    public void setPhaseLossDuration(Integer phaseLossDuration) {
        this.phaseLossDuration = phaseLossDuration;
    }
}

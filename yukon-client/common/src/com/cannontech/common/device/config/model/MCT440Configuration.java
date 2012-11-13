package com.cannontech.common.device.config.model;

import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.model.tou.Tou;

public class MCT440Configuration extends ConfigurationBase {

    @Override
    public ConfigurationType getType() {
        return ConfigurationType.MCT440;
    }

    // DST
    private Integer timeZoneOffset = null;

    // Addressing
    private Integer serviceProviderId = 0;

    // Configuration
    private Boolean enableDst = true;
    private Integer timeAdjustTolerance = 15;

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

    public int getConfiguration() {
        // Get the bits and mask them to throw out erroneous values, also shift
        // them to their correct position

        // bit 0
        int enableDst = (getEnableDst()) ? 1 : 0;
        enableDst = (0x01 & enableDst);

        // Combine the bits to make the full value
        int configurationValue = enableDst;

        return configurationValue;

    }

    public void setConfiguration(int configuration) {
        // Unmask and shift the bits and set them to the correct param

        // bit 0
        int enableDst = (0x01 & configuration);
        this.setEnableDst(enableDst == 1);
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
}

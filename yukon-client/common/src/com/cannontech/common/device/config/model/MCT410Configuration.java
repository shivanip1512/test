package com.cannontech.common.device.config.model;

import java.util.Date;

import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.model.tou.Tou;

/**
 * Backing bean for mct 410 device configurations
 */
public class MCT410Configuration extends ConfigurationBase {

    // Centron
    private Integer parameters = null;
    private Integer transformerRatio = null;

    public Integer getParameters() {
        return parameters;
    }

    public void setParameters(Integer parameters) {
        this.parameters = parameters;
    }

    public Integer getTransformerRatio() {
        return transformerRatio;
    }

    public void setTransformerRatio(Integer transformerRatio) {
        this.transformerRatio = transformerRatio;
    }

    // Addressing
    private Integer bronzeAddress = null;
    private Integer leadAddress = null;
    private Integer serviceProviderId = null;
    private Integer collectionAddress = null;

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

    public Integer getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Integer serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public Integer getCollectionAddress() {
        return collectionAddress;
    }

    public void setCollectionAddress(Integer collectionAddress) {
        this.collectionAddress = collectionAddress;
    }

    // DST
    private Date dstBegin = null;
    private Date dstEnd = null;
    private Integer timeZoneOffset = null;

    @Override
    public ConfigurationType getType() {
        return ConfigurationType.MCT410;
    }

    public Date getDstBegin() {
        return dstBegin;
    }

    public void setDstBegin(Date dstBegin) {
        this.dstBegin = dstBegin;
    }

    public Date getDstEnd() {
        return dstEnd;
    }

    public void setDstEnd(Date dstEnd) {
        this.dstEnd = dstEnd;
    }

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    // Options
    private Boolean alarmMaskMeter = null;
    private Boolean alarmMaskEvent1 = null;
    private Boolean alarmMaskEvent2 = null;
    private Integer timeAdjustTolerance = null;
    private Boolean configuration = null;
    private Boolean options = null;
    private Integer outageCycles = null;

    public Boolean getAlarmMaskMeter() {
        return alarmMaskMeter;
    }

    public void setAlarmMaskMeter(Boolean alarmMaskMeter) {
        this.alarmMaskMeter = alarmMaskMeter;
    }

    public Boolean getAlarmMaskEvent1() {
        return alarmMaskEvent1;
    }

    public void setAlarmMaskEvent1(Boolean alarmMaskEvent1) {
        this.alarmMaskEvent1 = alarmMaskEvent1;
    }

    public Boolean getAlarmMaskEvent2() {
        return alarmMaskEvent2;
    }

    public void setAlarmMaskEvent2(Boolean alarmMaskEvent2) {
        this.alarmMaskEvent2 = alarmMaskEvent2;
    }

    public Integer getTimeAdjustTolerance() {
        return timeAdjustTolerance;
    }

    public void setTimeAdjustTolerance(Integer timeAdjustTolerance) {
        this.timeAdjustTolerance = timeAdjustTolerance;
    }

    public Boolean getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Boolean configuration) {
        this.configuration = configuration;
    }

    public Boolean getOptions() {
        return options;
    }

    public void setOptions(Boolean options) {
        this.options = options;
    }

    public Integer getOutageCycles() {
        return outageCycles;
    }

    public void setOutageCycles(Integer outageCycles) {
        this.outageCycles = outageCycles;
    }

    // Demand and LP
    private Integer demandInterval = null;
    private Integer loadProfileInterval = null;
    private Integer voltageLpInterval = null;
    private Integer demandVInterval = null;
    private Integer loadProfileInterval2 = null;

    public Integer getDemandInterval() {
        return demandInterval;
    }

    public void setDemandInterval(Integer demandInterval) {
        this.demandInterval = demandInterval;
    }

    public Integer getLoadProfileInterval() {
        return loadProfileInterval;
    }

    public void setLoadProfileInterval(Integer loadProfileInterval) {
        this.loadProfileInterval = loadProfileInterval;
    }

    public Integer getVoltageLpInterval() {
        return voltageLpInterval;
    }

    public void setVoltageLpInterval(Integer voltageLpInterval) {
        this.voltageLpInterval = voltageLpInterval;
    }

    public Integer getDemandVInterval() {
        return demandVInterval;
    }

    public void setDemandVInterval(Integer demandVInterval) {
        this.demandVInterval = demandVInterval;
    }

    public Integer getLoadProfileInterval2() {
        return loadProfileInterval2;
    }

    public void setLoadProfileInterval2(Integer loadProfileInterval2) {
        this.loadProfileInterval2 = loadProfileInterval2;
    }

    // Voltage Threshold
    private Integer underVThreshold = null;
    private Integer overVThreshold = null;

    public Integer getUnderVThreshold() {
        return underVThreshold;
    }

    public void setUnderVThreshold(Integer underVThreshold) {
        this.underVThreshold = underVThreshold;
    }

    public Integer getOverVThreshold() {
        return overVThreshold;
    }

    public void setOverVThreshold(Integer overVThreshold) {
        this.overVThreshold = overVThreshold;
    }

    // Disconnect
    private Integer demandThreshold = null;
    private Integer connectDelay = null;
    private Integer cyclingConnectMinutes = null;
    private Integer cyclingDisconnectMinutes = null;

    public Integer getDemandThreshold() {
        return demandThreshold;
    }

    public void setDemandThreshold(Integer demandThreshold) {
        this.demandThreshold = demandThreshold;
    }

    public Integer getConnectDelay() {
        return connectDelay;
    }

    public void setConnectDelay(Integer connectDelay) {
        this.connectDelay = connectDelay;
    }

    public Integer getCyclingConnectMinutes() {
        return cyclingConnectMinutes;
    }

    public void setCyclingConnectMinutes(Integer cyclingConnectMinutes) {
        this.cyclingConnectMinutes = cyclingConnectMinutes;
    }

    public Integer getCyclingDisconnectMinutes() {
        return cyclingDisconnectMinutes;
    }

    public void setCyclingDisconnectMinutes(Integer cyclingDisconnectMinutes) {
        this.cyclingDisconnectMinutes = cyclingDisconnectMinutes;
    }

    // Long LP
    private Integer channel1Length = null;
    private Integer channel2Length = null;
    private Integer channel3Length = null;
    private Integer channel4Length = null;

    public Integer getChannel1Length() {
        return channel1Length;
    }

    public void setChannel1Length(Integer channel1Length) {
        this.channel1Length = channel1Length;
    }

    public Integer getChannel2Length() {
        return channel2Length;
    }

    public void setChannel2Length(Integer channel2Length) {
        this.channel2Length = channel2Length;
    }

    public Integer getChannel3Length() {
        return channel3Length;
    }

    public void setChannel3Length(Integer channel3Length) {
        this.channel3Length = channel3Length;
    }

    public Integer getChannel4Length() {
        return channel4Length;
    }

    public void setChannel4Length(Integer channel4Length) {
        this.channel4Length = channel4Length;
    }

    // Holidays
    private Date holidayDate1 = null;
    private Date holidayDate2 = null;
    private Date holidayDate3 = null;

    public Date getHolidayDate1() {
        return holidayDate1;
    }

    public void setHolidayDate1(Date holidayDate1) {
        this.holidayDate1 = holidayDate1;
    }

    public Date getHolidayDate2() {
        return holidayDate2;
    }

    public void setHolidayDate2(Date holidayDate2) {
        this.holidayDate2 = holidayDate2;
    }

    public Date getHolidayDate3() {
        return holidayDate3;
    }

    public void setHolidayDate3(Date holidayDate3) {
        this.holidayDate3 = holidayDate3;
    }

    // TOU
    private Tou tou = new Tou();

    public Tou getTou() {
        return tou;
    }

    public void setTou(Tou tou) {
        this.tou = tou;
    }

}

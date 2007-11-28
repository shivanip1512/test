package com.cannontech.common.device.config.model;

import java.util.Date;

import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.model.tou.Tou;

/**
 * Backing bean for mct 410 device configurations
 */
public class MCT410Configuration extends ConfigurationBase {
    
    @Override
    public ConfigurationType getType() {
        return ConfigurationType.MCT410;
    }

    // Centron
    private int displayBits = 0;
    private Boolean lcdSegmentTest = false;
    private Boolean lcdSegmentTestTime = false;
    private Boolean lcdErrorDisplay = false;
    private Integer transformerRatio = 1;

    public int getParameters() {

        // Get the bits and mask them to throw out erroneous values, also shift
        // them to their correct position

        // bits 0,1
        int displayBits = getDisplayBits();
        displayBits = (0x03 & displayBits);

        // bit 2
        int lcdSegmentTest = (getLcdSegmentTest()) ? 1 : 0;
        lcdSegmentTest = 0x04 & (lcdSegmentTest << 2);

        // bit 3
        int lcdSegmentTestTime = (getLcdSegmentTestTime()) ? 1 : 0;
        lcdSegmentTestTime = 0x08 & (lcdSegmentTestTime << 3);

        // bit 4
        int lcdErrorDisplay = (getLcdErrorDisplay()) ? 1 : 0;
        lcdErrorDisplay = 0x10 & (lcdErrorDisplay << 4);

        // bits 5,6,7 - not used

        // Combine the bits to make the full value
        int parametersValue = displayBits | lcdSegmentTest | lcdSegmentTestTime | lcdErrorDisplay;

        return parametersValue;
    }

    public void setParameters(int parameters) {

        // Unmask and shift the bits and set them to the correct param

        // bits 0,1
        int displayBits = (0x03 & parameters);
        this.setDisplayBits(displayBits);

        // bit 2
        int lcdSegmentTest = (0x04 & parameters) >> 2;
        this.setLcdSegmentTest(lcdSegmentTest == 1);

        // bit 3
        int lcdSegmentTestTime = (0x08 & parameters) >> 3;
        this.setLcdSegmentTestTime(lcdSegmentTestTime == 1);

        // bit 4
        int lcdErrorDisplay = (0x10 & parameters) >> 4;
        this.setLcdErrorDisplay(lcdErrorDisplay == 1);

        // bits 5,6,7 - not used

    }

    public int getDisplayBits() {
        return displayBits;
    }

    public void setDisplayBits(int displayBits) {
        this.displayBits = displayBits;
    }

    public Boolean getLcdSegmentTest() {
        return lcdSegmentTest;
    }

    public void setLcdSegmentTest(Boolean lcdSegmentTest) {
        this.lcdSegmentTest = lcdSegmentTest;
    }

    public Boolean getLcdSegmentTestTime() {
        return lcdSegmentTestTime;
    }

    public void setLcdSegmentTestTime(Boolean lcdSegmentTestTime) {
        this.lcdSegmentTestTime = lcdSegmentTestTime;
    }

    public Boolean getLcdErrorDisplay() {
        return lcdErrorDisplay;
    }

    public void setLcdErrorDisplay(Boolean lcdErrorDisplay) {
        this.lcdErrorDisplay = lcdErrorDisplay;
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

    private Boolean powerFailEvent = null;
    private Boolean underVoltageEvent = null;
    private Boolean overVoltageEvent = null;
    private Boolean powerFailCarryover = null;
    private Boolean rtcAdjusted = null;
    private Boolean holidayFlag = null;
    private Boolean dstChange = null;
    private Boolean tamperFlag = null;

    private Boolean zeroUsage = null;
    private Boolean disconnectError = null;
    private Boolean meterReadingCorrupted = null;
    private Boolean transmitterOverheat = null;

    private Integer timeAdjustTolerance = 15;

    private Boolean dailyReporting = null;
    private Boolean mct410dRevE = null;
    private Boolean roleEnabled = null;
    private Boolean disconnectCyclingMode = null;
    private Boolean demandLimitMode = null;
    private Boolean disableReconnectButton = null;
    private Boolean ledTestEnabled = null;
    private Boolean enableDst = null;

    private int channel3MeterConfig = 0;
    private int channel2MeterConfig = 0;

    private Integer outageCycles = 30;

    public Boolean getAlarmMaskMeter() {
        return alarmMaskMeter;
    }

    public void setAlarmMaskMeter(Boolean alarmMaskMeter) {
        this.alarmMaskMeter = alarmMaskMeter;
    }

    public int getAlarmMaskEvent1() {

        // Get the bits and mask them to throw out erroneous values, also shift
        // them to their correct position

        // bit 0
        int powerFailEvent = (getPowerFailEvent()) ? 1 : 0;
        powerFailEvent = (0x01 & powerFailEvent);

        // bit 1
        int underVoltageEvent = (getUnderVoltageEvent()) ? 1 : 0;
        underVoltageEvent = 0x02 & (underVoltageEvent << 1);

        // bit 2
        int overVoltageEvent = (getOverVoltageEvent()) ? 1 : 0;
        overVoltageEvent = 0x04 & (overVoltageEvent << 2);

        // bit 3
        int powerFailCarryover = (getPowerFailCarryover()) ? 1 : 0;
        powerFailCarryover = 0x08 & (powerFailCarryover << 3);

        // bit 4
        int rtcAdjusted = (getRtcAdjusted()) ? 1 : 0;
        rtcAdjusted = 0x10 & (rtcAdjusted << 4);

        // bit 5
        int holidayFlag = (getHolidayFlag()) ? 1 : 0;
        holidayFlag = 0x20 & (holidayFlag << 5);

        // bit 6
        int dstChange = (getDstChange()) ? 1 : 0;
        dstChange = 0x40 & (dstChange << 6);

        // bit 7
        int tamperFlag = (getTamperFlag()) ? 1 : 0;
        tamperFlag = 0x80 & (tamperFlag << 7);

        // Combine the bits to make the full value
        int alarmMaskEvent1Value = powerFailEvent | underVoltageEvent | overVoltageEvent | powerFailCarryover | rtcAdjusted | holidayFlag | dstChange | tamperFlag;

        return alarmMaskEvent1Value;

    }

    public void setAlarmMaskEvent1(int alarmMaskEvent1) {

        // Unmask and shift the bits and set them to the correct param

        // bit 0
        int powerFailEvent = (0x01 & alarmMaskEvent1);
        this.setPowerFailEvent(powerFailEvent == 1);

        // bit 1
        int underVoltageEvent = (0x02 & alarmMaskEvent1) >> 1;
        this.setUnderVoltageEvent(underVoltageEvent == 1);

        // bit 2
        int overVoltageEvent = (0x04 & alarmMaskEvent1) >> 2;
        this.setOverVoltageEvent(overVoltageEvent == 1);

        // bit 3
        int powerFailCarryover = (0x08 & alarmMaskEvent1) >> 3;
        this.setPowerFailCarryover(powerFailCarryover == 1);

        // bit 4
        int rtcAdjusted = (0x10 & alarmMaskEvent1) >> 4;
        this.setRtcAdjusted(rtcAdjusted == 1);

        // bit 5
        int holidayFlag = (0x20 & alarmMaskEvent1) >> 5;
        this.setHolidayFlag(holidayFlag == 1);

        // bit 6
        int dstChange = (0x40 & alarmMaskEvent1) >> 6;
        this.setDstChange(dstChange == 1);

        // bit 7
        int tamperFlag = (0x80 & alarmMaskEvent1) >> 7;
        this.setTamperFlag(tamperFlag == 1);

    }

    public Boolean getPowerFailEvent() {
        return powerFailEvent;
    }

    public void setPowerFailEvent(Boolean powerFailEvent) {
        this.powerFailEvent = powerFailEvent;
    }

    public Boolean getUnderVoltageEvent() {
        return underVoltageEvent;
    }

    public void setUnderVoltageEvent(Boolean underVoltageEvent) {
        this.underVoltageEvent = underVoltageEvent;
    }

    public Boolean getOverVoltageEvent() {
        return overVoltageEvent;
    }

    public void setOverVoltageEvent(Boolean overVoltageEvent) {
        this.overVoltageEvent = overVoltageEvent;
    }

    public Boolean getPowerFailCarryover() {
        return powerFailCarryover;
    }

    public void setPowerFailCarryover(Boolean powerFailCarryover) {
        this.powerFailCarryover = powerFailCarryover;
    }

    public Boolean getRtcAdjusted() {
        return rtcAdjusted;
    }

    public void setRtcAdjusted(Boolean rtcAdjusted) {
        this.rtcAdjusted = rtcAdjusted;
    }

    public Boolean getHolidayFlag() {
        return holidayFlag;
    }

    public void setHolidayFlag(Boolean holidayFlag) {
        this.holidayFlag = holidayFlag;
    }

    public Boolean getDstChange() {
        return dstChange;
    }

    public void setDstChange(Boolean dstChange) {
        this.dstChange = dstChange;
    }

    public Boolean getTamperFlag() {
        return tamperFlag;
    }

    public void setTamperFlag(Boolean tamperFlag) {
        this.tamperFlag = tamperFlag;
    }

    public int getAlarmMaskEvent2() {

        // Get the bits and mask them to throw out erroneous values, also shift
        // them to their correct position

        // bit 0
        int zeroUsage = (getZeroUsage()) ? 1 : 0;
        zeroUsage = (0x01 & zeroUsage);

        // bit 1
        int disconnectError = (getDisconnectError()) ? 1 : 0;
        disconnectError = 0x02 & (disconnectError << 1);

        // bit 2
        int meterReadingCorrupted = (getMeterReadingCorrupted()) ? 1 : 0;
        meterReadingCorrupted = 0x04 & (meterReadingCorrupted << 2);

        // bit 3
        int transmitterOverheat = (getTransmitterOverheat()) ? 1 : 0;
        transmitterOverheat = 0x08 & (transmitterOverheat << 3);

        // Combine the bits to make the full value
        int alarmMaskEvent2Value = zeroUsage | disconnectError | meterReadingCorrupted | transmitterOverheat;

        return alarmMaskEvent2Value;
    }

    public void setAlarmMaskEvent2(int alarmMaskEvent2) {

        // Unmask and shift the bits and set them to the correct param

        // bit 0
        int zeroUsage = (0x01 & alarmMaskEvent2);
        this.setZeroUsage(zeroUsage == 1);

        // bit 1
        int disconnectError = (0x02 & alarmMaskEvent2) >> 1;
        this.setDisconnectError(disconnectError == 1);

        // bit 2
        int meterReadingCorrupted = (0x04 & alarmMaskEvent2) >> 2;
        this.setMeterReadingCorrupted(meterReadingCorrupted == 1);

        // bit 3
        int transmitterOverheat = (0x08 & alarmMaskEvent2) >> 3;
        this.setTransmitterOverheat(transmitterOverheat == 1);

    }

    public Boolean getDisconnectError() {
        return disconnectError;
    }

    public void setDisconnectError(Boolean disconnectError) {
        this.disconnectError = disconnectError;
    }

    public Boolean getMeterReadingCorrupted() {
        return meterReadingCorrupted;
    }

    public void setMeterReadingCorrupted(Boolean meterReadingCorrupted) {
        this.meterReadingCorrupted = meterReadingCorrupted;
    }

    public Boolean getTransmitterOverheat() {
        return transmitterOverheat;
    }

    public void setTransmitterOverheat(Boolean transmitterOverheat) {
        this.transmitterOverheat = transmitterOverheat;
    }

    public Boolean getZeroUsage() {
        return zeroUsage;
    }

    public void setZeroUsage(Boolean zeroUsage) {
        this.zeroUsage = zeroUsage;
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

        // bit 1
        int ledTestEnabled = (getLedTestEnabled()) ? 1 : 0;
        ledTestEnabled = 0x02 & (ledTestEnabled << 1);

        // bit 2
        int disableReconnectButton = (getDisableReconnectButton()) ? 1 : 0;
        disableReconnectButton = 0x04 & (disableReconnectButton << 2);

        // bit 3
        int demandLimitMode = (getDemandLimitMode()) ? 1 : 0;
        demandLimitMode = 0x08 & (demandLimitMode << 3);

        // bit 4
        int disconnectCyclingMode = (getDisconnectCyclingMode()) ? 1 : 0;
        disconnectCyclingMode = 0x10 & (disconnectCyclingMode << 4);

        // bit 5
        int roleEnabled = (getRoleEnabled()) ? 1 : 0;
        roleEnabled = 0x20 & (roleEnabled << 5);

        // bit 6
        int mct410dRevE = (getMct410dRevE()) ? 1 : 0;
        mct410dRevE = 0x40 & (mct410dRevE << 6);

        // bit 7
        int dailyReporting = (getDailyReporting()) ? 1 : 0;
        dailyReporting = 0x80 & (dailyReporting << 7);

        // Combine the bits to make the full value
        int configurationValue = enableDst | ledTestEnabled | disableReconnectButton | demandLimitMode | disconnectCyclingMode | roleEnabled | mct410dRevE | dailyReporting;

        return configurationValue;

    }

    public void setConfiguration(int configuration) {

        // Unmask and shift the bits and set them to the correct param

        // bit 0
        int enableDst = (0x01 & configuration);
        this.setEnableDst(enableDst == 1);

        // bit 1
        int ledTestEnabled = (0x02 & configuration) >> 1;
        this.setLedTestEnabled(ledTestEnabled == 1);

        // bit 2
        int disbaleReconnectButton = (0x04 & configuration) >> 2;
        this.setDisableReconnectButton(disbaleReconnectButton == 1);

        // bit 3
        int demandLimitMode = (0x08 & configuration) >> 3;
        this.setDemandLimitMode(demandLimitMode == 1);

        // bit 4
        int disconnectCyclingMode = (0x10 & configuration) >> 4;
        this.setDisconnectCyclingMode(disconnectCyclingMode == 1);

        // bit 5
        int roleEnabled = (0x20 & configuration) >> 5;
        this.setRoleEnabled(roleEnabled == 1);

        // bit 6
        int mct410dRevE = (0x40 & configuration) >> 6;
        this.setMct410dRevE(mct410dRevE == 1);

        // bit 7
        int dailyReporting = (0x80 & configuration) >> 7;
        this.setDailyReporting(dailyReporting == 1);

    }

    public Boolean getDailyReporting() {
        return dailyReporting;
    }

    public void setDailyReporting(Boolean dailyReporting) {
        this.dailyReporting = dailyReporting;
    }

    public Boolean getDemandLimitMode() {
        return demandLimitMode;
    }

    public void setDemandLimitMode(Boolean demandLimitMode) {
        this.demandLimitMode = demandLimitMode;
    }

    public Boolean getDisableReconnectButton() {
        return disableReconnectButton;
    }

    public void setDisableReconnectButton(Boolean disableReconnectButton) {
        this.disableReconnectButton = disableReconnectButton;
    }

    public Boolean getDisconnectCyclingMode() {
        return disconnectCyclingMode;
    }

    public void setDisconnectCyclingMode(Boolean disconnectCyclingMode) {
        this.disconnectCyclingMode = disconnectCyclingMode;
    }

    public Boolean getEnableDst() {
        return enableDst;
    }

    public void setEnableDst(Boolean enableDst) {
        this.enableDst = enableDst;
    }

    public Boolean getLedTestEnabled() {
        return ledTestEnabled;
    }

    public void setLedTestEnabled(Boolean ledTestEnabled) {
        this.ledTestEnabled = ledTestEnabled;
    }

    public Boolean getMct410dRevE() {
        return mct410dRevE;
    }

    public void setMct410dRevE(Boolean mct410dRevE) {
        this.mct410dRevE = mct410dRevE;
    }

    public Boolean getRoleEnabled() {
        return roleEnabled;
    }

    public void setRoleEnabled(Boolean roleEnabled) {
        this.roleEnabled = roleEnabled;
    }

    public int getOptions() {

        // Get the bits and mask them to throw out erroneous values, also shift
        // them to their correct position

        // bits 0,1 - cannot change: determined by the software version
        // programmed into the meter

        // bits 2,3,4
        int channel2MeterConfig = getChannel2MeterConfig();
        channel2MeterConfig = 0x1C & (channel2MeterConfig << 2);

        // bits 5,6,7
        int channel3MeterConfig = getChannel3MeterConfig();
        channel3MeterConfig = 0xE0 & (channel3MeterConfig << 5);

        // Combine the bits to make the full value
        int optionsValue = channel2MeterConfig | channel3MeterConfig;

        return optionsValue;
    }

    public void setOptions(int options) {

        // Unmask and shift the bits and set them to the correct param

        // bits 0,1 - cannot change: determined by the software version
        // programmed into the meter

        // bits 2,3,4
        int channel2MeterConfig = (0x1C & options) >> 2;
        this.setChannel2MeterConfig(channel2MeterConfig);

        // bits 5,6,7
        int channel3MeterConfig = (0xE0 & options) >> 5;
        this.setChannel3MeterConfig(channel3MeterConfig);

    }

    public int getChannel3MeterConfig() {
        return channel3MeterConfig;
    }

    public void setChannel3MeterConfig(int channel3MeterConfig) {
        this.channel3MeterConfig = channel3MeterConfig;
    }

    public int getChannel2MeterConfig() {
        return channel2MeterConfig;
    }

    public void setChannel2MeterConfig(int channel2MeterConfig) {
        this.channel2MeterConfig = channel2MeterConfig;
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

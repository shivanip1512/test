package com.cannontech.common.device.config.model;

import java.util.Date;

import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.model.tou.Tou;

/**
 * Backing bean for mct 470 device configurations
 */
public class MCT470Configuration extends ConfigurationBase {

    // Addressing
    private Integer bronzeAddress = null;
    private Integer leadAddress = null;
    private Integer serviceProviderId = null;
    private Integer collectionAddress = null;

    // DST
    private Date dstBegin = null;
    private Date dstEnd = null;
    private Integer timeZoneOffset = null;

    // Options
    private Boolean alarmMaskMeter = null;
    private Boolean alarmMaskEvent1 = null;
    private Boolean alarmMaskEvent2 = null;
    private Integer timeAdjustTolerance = null;
    private Boolean configuration = null;
    private Boolean options = null;
    private Integer outageCycles = null;

    // Demand and LP
    private Integer demandInterval = null;
    private Integer loadProfileInterval = null;
    private Integer voltageLpInterval = null;
    private Integer demandVInterval = null;
    private Integer loadProfileInterval2 = null;

    // Voltage Threshold
    private Integer underVThreshold = null;
    private Integer overVThreshold = null;

    // Long LP
    private Integer channel1Length = null;
    private Integer channel2Length = null;
    private Integer channel3Length = null;
    private Integer channel4Length = null;

    // Holidays
    private Date holidayDate1 = null;
    private Date holidayDate2 = null;
    private Date holidayDate3 = null;

    // LP channels
    private Integer channelConfig1 = null;
    private Integer channelConfig2 = null;
    private Integer channelConfig3 = null;
    private Integer channelConfig4 = null;

    // Relays
    private Integer relayATimer = null;
    private Integer relayBTimer = null;

    // Precanned Table
    private Integer tableReadInterval = null;
    private Integer meterNumber = null;
    private Integer tableType = null;

    // System Options
    private String demandMetersToScan = null;

    // TOU
    private Tou tou = new Tou();

    // DNP
    private Integer collection1BinaryA = null;
    private Integer collection1BinaryB = null;
    private Integer collection2BinaryA = null;
    private Integer collection2BinaryB = null;
    private Integer collection1Analog = null;
    private Integer collection2Analog = null;
    private Integer collection1Accumulator = null;
    private Integer collection2Accumulator = null;
    private Integer analog1 = null;
    private Integer analog2 = null;
    private Integer analog3 = null;
    private Integer analog4 = null;
    private Integer analog5 = null;
    private Integer analog6 = null;
    private Integer analog7 = null;
    private Integer analog8 = null;
    private Integer analog9 = null;
    private Integer analog10 = null;
    private Integer accumulator1 = null;
    private Integer accumulator2 = null;
    private Integer accumulator3 = null;
    private Integer accumulator4 = null;
    private Integer accumulator5 = null;
    private Integer accumulator6 = null;
    private Integer accumulator7 = null;
    private Integer accumulator8 = null;
    private Integer binaryByte1a = null;
    private Integer binaryByte1b = null;

    @Override
    public ConfigurationType getType() {
        return ConfigurationType.MCT470;
    }

    public Integer getBronzeAddress() {
        return bronzeAddress;
    }

    public void setBronzeAddress(Integer bronzeAddress) {
        this.bronzeAddress = bronzeAddress;
    }

    public Integer getCollectionAddress() {
        return collectionAddress;
    }

    public void setCollectionAddress(Integer collectionAddress) {
        this.collectionAddress = collectionAddress;
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

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public Integer getDemandInterval() {
        return demandInterval;
    }

    public void setDemandInterval(Integer demandInterval) {
        this.demandInterval = demandInterval;
    }

    public Integer getDemandVInterval() {
        return demandVInterval;
    }

    public void setDemandVInterval(Integer demandVInterval) {
        this.demandVInterval = demandVInterval;
    }

    public Integer getLoadProfileInterval() {
        return loadProfileInterval;
    }

    public void setLoadProfileInterval(Integer loadProfileInterval) {
        this.loadProfileInterval = loadProfileInterval;
    }

    public Integer getLoadProfileInterval2() {
        return loadProfileInterval2;
    }

    public void setLoadProfileInterval2(Integer loadProfileInterval2) {
        this.loadProfileInterval2 = loadProfileInterval2;
    }

    public Integer getVoltageLpInterval() {
        return voltageLpInterval;
    }

    public void setVoltageLpInterval(Integer voltageLpInterval) {
        this.voltageLpInterval = voltageLpInterval;
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

    public Boolean getAlarmMaskMeter() {
        return alarmMaskMeter;
    }

    public void setAlarmMaskMeter(Boolean alarmMaskMeter) {
        this.alarmMaskMeter = alarmMaskMeter;
    }

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

    public Integer getChannelConfig1() {
        return channelConfig1;
    }

    public void setChannelConfig1(Integer channelConfig1) {
        this.channelConfig1 = channelConfig1;
    }

    public Integer getChannelConfig2() {
        return channelConfig2;
    }

    public void setChannelConfig2(Integer channelConfig2) {
        this.channelConfig2 = channelConfig2;
    }

    public Integer getChannelConfig3() {
        return channelConfig3;
    }

    public void setChannelConfig3(Integer channelConfig3) {
        this.channelConfig3 = channelConfig3;
    }

    public Integer getChannelConfig4() {
        return channelConfig4;
    }

    public void setChannelConfig4(Integer channelConfig4) {
        this.channelConfig4 = channelConfig4;
    }

    public Boolean getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Boolean configuration) {
        this.configuration = configuration;
    }

    public String getDemandMetersToScan() {
        return demandMetersToScan;
    }

    public void setDemandMetersToScan(String demandMetersToScan) {
        this.demandMetersToScan = demandMetersToScan;
    }

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

    public Integer getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(Integer meterNumber) {
        this.meterNumber = meterNumber;
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

    public Integer getOverVThreshold() {
        return overVThreshold;
    }

    public void setOverVThreshold(Integer overVThreshold) {
        this.overVThreshold = overVThreshold;
    }

    public Integer getRelayATimer() {
        return relayATimer;
    }

    public void setRelayATimer(Integer relayATimer) {
        this.relayATimer = relayATimer;
    }

    public Integer getRelayBTimer() {
        return relayBTimer;
    }

    public void setRelayBTimer(Integer relayBTimer) {
        this.relayBTimer = relayBTimer;
    }

    public Integer getTableReadInterval() {
        return tableReadInterval;
    }

    public void setTableReadInterval(Integer tableReadInterval) {
        this.tableReadInterval = tableReadInterval;
    }

    public Integer getTableType() {
        return tableType;
    }

    public void setTableType(Integer tableType) {
        this.tableType = tableType;
    }

    public Integer getTimeAdjustTolerance() {
        return timeAdjustTolerance;
    }

    public void setTimeAdjustTolerance(Integer timeAdjustTolerance) {
        this.timeAdjustTolerance = timeAdjustTolerance;
    }

    public Integer getUnderVThreshold() {
        return underVThreshold;
    }

    public void setUnderVThreshold(Integer underVThreshold) {
        this.underVThreshold = underVThreshold;
    }

    public Tou getTou() {
        return tou;
    }

    public void setTou(Tou tou) {
        this.tou = tou;
    }

    public Integer getAccumulator1() {
        return accumulator1;
    }

    public void setAccumulator1(Integer accumulator1) {
        this.accumulator1 = accumulator1;
    }

    public Integer getAccumulator2() {
        return accumulator2;
    }

    public void setAccumulator2(Integer accumulator2) {
        this.accumulator2 = accumulator2;
    }

    public Integer getAccumulator3() {
        return accumulator3;
    }

    public void setAccumulator3(Integer accumulator3) {
        this.accumulator3 = accumulator3;
    }

    public Integer getAccumulator4() {
        return accumulator4;
    }

    public void setAccumulator4(Integer accumulator4) {
        this.accumulator4 = accumulator4;
    }

    public Integer getAccumulator5() {
        return accumulator5;
    }

    public void setAccumulator5(Integer accumulator5) {
        this.accumulator5 = accumulator5;
    }

    public Integer getAccumulator6() {
        return accumulator6;
    }

    public void setAccumulator6(Integer accumulator6) {
        this.accumulator6 = accumulator6;
    }

    public Integer getAccumulator7() {
        return accumulator7;
    }

    public void setAccumulator7(Integer accumulator7) {
        this.accumulator7 = accumulator7;
    }

    public Integer getAccumulator8() {
        return accumulator8;
    }

    public void setAccumulator8(Integer accumulator8) {
        this.accumulator8 = accumulator8;
    }

    public Integer getAnalog1() {
        return analog1;
    }

    public void setAnalog1(Integer analog1) {
        this.analog1 = analog1;
    }

    public Integer getAnalog10() {
        return analog10;
    }

    public void setAnalog10(Integer analog10) {
        this.analog10 = analog10;
    }

    public Integer getAnalog2() {
        return analog2;
    }

    public void setAnalog2(Integer analog2) {
        this.analog2 = analog2;
    }

    public Integer getAnalog3() {
        return analog3;
    }

    public void setAnalog3(Integer analog3) {
        this.analog3 = analog3;
    }

    public Integer getAnalog4() {
        return analog4;
    }

    public void setAnalog4(Integer analog4) {
        this.analog4 = analog4;
    }

    public Integer getAnalog5() {
        return analog5;
    }

    public void setAnalog5(Integer analog5) {
        this.analog5 = analog5;
    }

    public Integer getAnalog6() {
        return analog6;
    }

    public void setAnalog6(Integer analog6) {
        this.analog6 = analog6;
    }

    public Integer getAnalog7() {
        return analog7;
    }

    public void setAnalog7(Integer analog7) {
        this.analog7 = analog7;
    }

    public Integer getAnalog8() {
        return analog8;
    }

    public void setAnalog8(Integer analog8) {
        this.analog8 = analog8;
    }

    public Integer getAnalog9() {
        return analog9;
    }

    public void setAnalog9(Integer analog9) {
        this.analog9 = analog9;
    }

    public Integer getBinaryByte1a() {
        return binaryByte1a;
    }

    public void setBinaryByte1a(Integer binaryByte1a) {
        this.binaryByte1a = binaryByte1a;
    }

    public Integer getBinaryByte1b() {
        return binaryByte1b;
    }

    public void setBinaryByte1b(Integer binaryByte1b) {
        this.binaryByte1b = binaryByte1b;
    }

    public Integer getCollection1Accumulator() {
        return collection1Accumulator;
    }

    public void setCollection1Accumulator(Integer collection1Accumulator) {
        this.collection1Accumulator = collection1Accumulator;
    }

    public Integer getCollection1Analog() {
        return collection1Analog;
    }

    public void setCollection1Analog(Integer collection1Analog) {
        this.collection1Analog = collection1Analog;
    }

    public Integer getCollection1BinaryA() {
        return collection1BinaryA;
    }

    public void setCollection1BinaryA(Integer collection1BinaryA) {
        this.collection1BinaryA = collection1BinaryA;
    }

    public Integer getCollection1BinaryB() {
        return collection1BinaryB;
    }

    public void setCollection1BinaryB(Integer collection1BinaryB) {
        this.collection1BinaryB = collection1BinaryB;
    }

    public Integer getCollection2Accumulator() {
        return collection2Accumulator;
    }

    public void setCollection2Accumulator(Integer collection2Accumulator) {
        this.collection2Accumulator = collection2Accumulator;
    }

    public Integer getCollection2Analog() {
        return collection2Analog;
    }

    public void setCollection2Analog(Integer collection2Analog) {
        this.collection2Analog = collection2Analog;
    }

    public Integer getCollection2BinaryA() {
        return collection2BinaryA;
    }

    public void setCollection2BinaryA(Integer collection2BinaryA) {
        this.collection2BinaryA = collection2BinaryA;
    }

    public Integer getCollection2BinaryB() {
        return collection2BinaryB;
    }

    public void setCollection2BinaryB(Integer collection2BinaryB) {
        this.collection2BinaryB = collection2BinaryB;
    }

}

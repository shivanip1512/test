#include "precompiled.h"

#include "PointAttribute.h"

PointAttribute::AttributeMap PointAttribute::nameToAttributeMap = PointAttribute::AttributeMap();

const PointAttribute PointAttribute::Unknown = PointAttribute(PointAttribute::UnknownAttribute,"UNKNOWN");
const PointAttribute PointAttribute::TapDown = PointAttribute(PointAttribute::TapDownAttribute,"TAP_DOWN");
const PointAttribute PointAttribute::TapUp = PointAttribute(PointAttribute::TapUpAttribute,"TAP_UP");
const PointAttribute PointAttribute::Voltage = PointAttribute(PointAttribute::VoltageAttribute,"VOLTAGE");
const PointAttribute PointAttribute::AutoRemoteControl = PointAttribute(PointAttribute::AutoRemoteControlAttribute,"AUTO_REMOTE_CONTROL");
const PointAttribute PointAttribute::TapPosition = PointAttribute(PointAttribute::TapPositionAttribute,"TAP_POSITION");
const PointAttribute PointAttribute::KeepAlive = PointAttribute(PointAttribute::KeepAliveAttribute,"KEEP_ALIVE");

const PointAttribute PointAttribute::VoltageX = PointAttribute(PointAttribute::VoltageXAttribute,"VOLTAGE_X");
const PointAttribute PointAttribute::VoltageY = PointAttribute(PointAttribute::VoltageYAttribute,"VOLTAGE_Y");
const PointAttribute PointAttribute::Terminate = PointAttribute(PointAttribute::TerminateAttribute,"TERMINATE");
const PointAttribute PointAttribute::AutoBlockEnable = PointAttribute(PointAttribute::AutoBlockEnableAttribute,"AUTO_BLOCK_ENABLE");
const PointAttribute PointAttribute::HeartbeatTimerConfig = PointAttribute(PointAttribute::HeartbeatTimerConfigAttribute,"KEEP_ALIVE_TIMER");

const PointAttribute PointAttribute::CbcVoltage = PointAttribute(PointAttribute::CbcVoltageAttribute, "Voltage");
const PointAttribute PointAttribute::HighVoltage = PointAttribute(PointAttribute::HighVoltageAttribute, "High Voltage");
const PointAttribute PointAttribute::LowVoltage = PointAttribute(PointAttribute::LowVoltageAttribute, "Low Voltage");
const PointAttribute PointAttribute::DeltaVoltage = PointAttribute(PointAttribute::DeltaVoltageAttribute, "Delta Voltage");
const PointAttribute PointAttribute::AnalogInput1 = PointAttribute(PointAttribute::AnalogInput1Attribute, "Analog Input 1");
const PointAttribute PointAttribute::Temperature = PointAttribute(PointAttribute::TemperatureAttribute, "Temperature");
const PointAttribute PointAttribute::RSSI = PointAttribute(PointAttribute::RSSIAttribute, "RSSI");
const PointAttribute PointAttribute::IgnoredReason = PointAttribute(PointAttribute::IgnoredReasonAttribute, "Ignored Reason");

const PointAttribute PointAttribute::VoltageControl = PointAttribute(PointAttribute::VoltageControlAttribute, "Voltage Control");
const PointAttribute PointAttribute::UvThreshold = PointAttribute(PointAttribute::UvThresholdAttribute, "Uv Threshold");
const PointAttribute PointAttribute::OvThreshold = PointAttribute(PointAttribute::OvThresholdAttribute, "Ov Threshold");
const PointAttribute PointAttribute::OVUVTrackTime = PointAttribute(PointAttribute::OVUVTrackTimeAttribute, "OVUV Track Time");
const PointAttribute PointAttribute::NeutralCurrentSensor = PointAttribute(PointAttribute::NeutralCurrentSensorAttribute, "Neutral Current Sensor");
const PointAttribute PointAttribute::NeutralCurrentAlarmThreshold = PointAttribute(PointAttribute::NeutralCurrentAlarmThresholdAttribute, "Neutral Current Alarm Threshold");
const PointAttribute PointAttribute::TimeTempSeasonOne = PointAttribute(PointAttribute::TimeTempSeasonOneAttribute, "Time Temp Season One");
const PointAttribute PointAttribute::TimeTempSeasonTwo = PointAttribute(PointAttribute::TimeTempSeasonTwoAttribute, "Time Temp Season Two");
const PointAttribute PointAttribute::VarControl = PointAttribute(PointAttribute::VarControlAttribute, "Var Control");
const PointAttribute PointAttribute::UDPIpAddress = PointAttribute(PointAttribute::UDPIpAddressAttribute, "UDP Ip Address");
const PointAttribute PointAttribute::UDPPortNumber = PointAttribute(PointAttribute::UDPPortNumberAttribute, "UDP Port Number");

const PointAttribute PointAttribute::CapacitorBankState = PointAttribute(PointAttribute::CapacitorBankStateAttribute, "Capacitor Bank State");
const PointAttribute PointAttribute::ReCloseBlocked = PointAttribute(PointAttribute::ReCloseBlockedAttribute, "Re-Close Blocked");
const PointAttribute PointAttribute::ControlMode = PointAttribute(PointAttribute::ControlModeAttribute, "Control Mode");
const PointAttribute PointAttribute::AutoVoltControl = PointAttribute(PointAttribute::AutoVoltControlAttribute, "Auto Volt Control");
const PointAttribute PointAttribute::LastControlLocal = PointAttribute(PointAttribute::LastControlLocalAttribute, "Last Control Local");
const PointAttribute PointAttribute::LastControlRemote = PointAttribute(PointAttribute::LastControlRemoteAttribute, "Last Control Remote");
const PointAttribute PointAttribute::LastControlOvUv = PointAttribute(PointAttribute::LastControlOvUvAttribute, "Last Control OvUv");
const PointAttribute PointAttribute::LastControlNeutralFault = PointAttribute(PointAttribute::LastControlNeutralFaultAttribute, "Last Control Neutral Fault");
const PointAttribute PointAttribute::LastControlScheduled  = PointAttribute(PointAttribute::LastControlScheduledAttribute, "Last Control Scheduled");
const PointAttribute PointAttribute::LastControlDigital = PointAttribute(PointAttribute::LastControlDigitalAttribute, "Last Control Digital");
const PointAttribute PointAttribute::LastControlAnalog = PointAttribute(PointAttribute::LastControlAnalogAttribute, "Last Control Analog");
const PointAttribute PointAttribute::LastControlTemperature = PointAttribute(PointAttribute::LastControlTemperatureAttribute, "Last Control Temperature");
const PointAttribute PointAttribute::OvCondition = PointAttribute(PointAttribute::OvConditionAttribute, "Ov Condition");
const PointAttribute PointAttribute::UvCondition = PointAttribute(PointAttribute::UvConditionAttribute, "Uv Condition");
const PointAttribute PointAttribute::OpFailedNeutralCurrent = PointAttribute(PointAttribute::OpFailedNeutralCurrentAttribute, "Op Failed Neutral Current");
const PointAttribute PointAttribute::NeutralCurrentFault = PointAttribute(PointAttribute::NeutralCurrentFaultAttribute, "Neutral Current Fault");
const PointAttribute PointAttribute::BadRelay = PointAttribute(PointAttribute::BadRelayAttribute, "Bad Relay");
const PointAttribute PointAttribute::DailyMaxOps = PointAttribute(PointAttribute::DailyMaxOpsAttribute, "Daily Max Ops");
const PointAttribute PointAttribute::VoltageDeltaAbnormal = PointAttribute(PointAttribute::VoltageDeltaAbnormalAttribute, "Voltage Delta Abnormal");
const PointAttribute PointAttribute::TempAlarm = PointAttribute(PointAttribute::TempAlarmAttribute, "Temp Alarm");
const PointAttribute PointAttribute::DSTActive = PointAttribute(PointAttribute::DSTActiveAttribute, "DST Active");
const PointAttribute PointAttribute::NeutralLockout = PointAttribute(PointAttribute::NeutralLockoutAttribute, "Neutral Lockout");
const PointAttribute PointAttribute::IgnoredIndicator = PointAttribute(PointAttribute::IgnoredIndicatorAttribute, "Ignored Indicator");

const PointAttribute PointAttribute::TotalOpCount = PointAttribute(PointAttribute::TotalOpCountAttribute, "Total Op Count");
const PointAttribute PointAttribute::UvCount = PointAttribute(PointAttribute::UvCountAttribute, "Uv Count");
const PointAttribute PointAttribute::OvCount = PointAttribute(PointAttribute::OvCountAttribute, "Ov Count");

const PointAttribute& PointAttribute::valueOf(const std::string& name)
{
    AttributeMap::iterator itr = nameToAttributeMap.find(name);

    if (itr == nameToAttributeMap.end())
    {
        return Unknown;
    }

    return *(itr->second);
}

std::string PointAttribute::name() const
{
    return _dbName;
}

PointAttribute::Attribute PointAttribute::value() const
{
    return _value;
}

PointAttribute::PointAttribute(Attribute value, const std::string& name)
{
    _dbName = name;
    _value = value;

    nameToAttributeMap[name] = this;
}

const bool PointAttribute::operator == (const PointAttribute& rhs) const
{
    return _value == rhs._value;
}

const bool PointAttribute::operator <  (const PointAttribute& rhs) const
{
    return _value < rhs._value;
}

#include "precompiled.h"

#include "PointAttribute.h"

#include "std_helper.h"

PointAttribute::AttributeMap PointAttribute::nameToAttributeMap = PointAttribute::AttributeMap();

const PointAttribute PointAttribute::Unknown                       = PointAttribute(PointAttribute::UnknownAttribute,                      "UNKNOWN");
const PointAttribute PointAttribute::TapDown                       = PointAttribute(PointAttribute::TapDownAttribute,                      "TAP_DOWN");
const PointAttribute PointAttribute::TapUp                         = PointAttribute(PointAttribute::TapUpAttribute,                        "TAP_UP");
const PointAttribute PointAttribute::Voltage                       = PointAttribute(PointAttribute::VoltageAttribute,                      "VOLTAGE");
const PointAttribute PointAttribute::AutoRemoteControl             = PointAttribute(PointAttribute::AutoRemoteControlAttribute,            "AUTO_REMOTE_CONTROL");
const PointAttribute PointAttribute::TapPosition                   = PointAttribute(PointAttribute::TapPositionAttribute,                  "TAP_POSITION");
const PointAttribute PointAttribute::KeepAlive                     = PointAttribute(PointAttribute::KeepAliveAttribute,                    "KEEP_ALIVE");

const PointAttribute PointAttribute::VoltageX                      = PointAttribute(PointAttribute::VoltageXAttribute,                     "VOLTAGE_X");
const PointAttribute PointAttribute::VoltageY                      = PointAttribute(PointAttribute::VoltageYAttribute,                     "VOLTAGE_Y");
const PointAttribute PointAttribute::Terminate                     = PointAttribute(PointAttribute::TerminateAttribute,                    "TERMINATE");
const PointAttribute PointAttribute::AutoBlockEnable               = PointAttribute(PointAttribute::AutoBlockEnableAttribute,              "AUTO_BLOCK_ENABLE");
const PointAttribute PointAttribute::HeartbeatTimerConfig          = PointAttribute(PointAttribute::HeartbeatTimerConfigAttribute,         "KEEP_ALIVE_TIMER");

const PointAttribute PointAttribute::CbcVoltage                    = PointAttribute(PointAttribute::CbcVoltageAttribute,                   "Voltage");
const PointAttribute PointAttribute::HighVoltage                   = PointAttribute(PointAttribute::HighVoltageAttribute,                  "High Voltage");
const PointAttribute PointAttribute::LowVoltage                    = PointAttribute(PointAttribute::LowVoltageAttribute,                   "Low Voltage");
const PointAttribute PointAttribute::DeltaVoltage                  = PointAttribute(PointAttribute::DeltaVoltageAttribute,                 "Delta Voltage");
const PointAttribute PointAttribute::AnalogInput1                  = PointAttribute(PointAttribute::AnalogInput1Attribute,                 "Analog Input 1");
const PointAttribute PointAttribute::Temperature                   = PointAttribute(PointAttribute::TemperatureAttribute,                  "Temperature");
const PointAttribute PointAttribute::RSSI                          = PointAttribute(PointAttribute::RSSIAttribute,                         "RSSI");
const PointAttribute PointAttribute::IgnoredReason                 = PointAttribute(PointAttribute::IgnoredReasonAttribute,                "Ignored Reason");

const PointAttribute PointAttribute::VoltageControl                = PointAttribute(PointAttribute::VoltageControlAttribute,               "Voltage Control");
const PointAttribute PointAttribute::UvThreshold                   = PointAttribute(PointAttribute::UvThresholdAttribute,                  "Uv Threshold");
const PointAttribute PointAttribute::OvThreshold                   = PointAttribute(PointAttribute::OvThresholdAttribute,                  "Ov Threshold");
const PointAttribute PointAttribute::OVUVTrackTime                 = PointAttribute(PointAttribute::OVUVTrackTimeAttribute,                "OVUV Track Time");
const PointAttribute PointAttribute::NeutralCurrentSensor          = PointAttribute(PointAttribute::NeutralCurrentSensorAttribute,         "Neutral Current Sensor");
const PointAttribute PointAttribute::NeutralCurrentAlarmThreshold  = PointAttribute(PointAttribute::NeutralCurrentAlarmThresholdAttribute, "Neutral Current Alarm Threshold");
const PointAttribute PointAttribute::TimeTempSeasonOne             = PointAttribute(PointAttribute::TimeTempSeasonOneAttribute,            "Time Temp Season One");
const PointAttribute PointAttribute::TimeTempSeasonTwo             = PointAttribute(PointAttribute::TimeTempSeasonTwoAttribute,            "Time Temp Season Two");
const PointAttribute PointAttribute::VarControl                    = PointAttribute(PointAttribute::VarControlAttribute,                   "Var Control");
const PointAttribute PointAttribute::UDPIpAddress                  = PointAttribute(PointAttribute::UDPIpAddressAttribute,                 "UDP Ip Address");
const PointAttribute PointAttribute::UDPPortNumber                 = PointAttribute(PointAttribute::UDPPortNumberAttribute,                "UDP Port Number");

const PointAttribute PointAttribute::CapacitorBankState            = PointAttribute(PointAttribute::CapacitorBankStateAttribute,           "Capacitor Bank State");
const PointAttribute PointAttribute::ReCloseBlocked                = PointAttribute(PointAttribute::ReCloseBlockedAttribute,               "Re-Close Blocked");
const PointAttribute PointAttribute::ControlMode                   = PointAttribute(PointAttribute::ControlModeAttribute,                  "Control Mode");
const PointAttribute PointAttribute::AutoVoltControl               = PointAttribute(PointAttribute::AutoVoltControlAttribute,              "Auto Volt Control");
const PointAttribute PointAttribute::LastControlLocal              = PointAttribute(PointAttribute::LastControlLocalAttribute,             "Last Control Local");
const PointAttribute PointAttribute::LastControlRemote             = PointAttribute(PointAttribute::LastControlRemoteAttribute,            "Last Control Remote");
const PointAttribute PointAttribute::LastControlOvUv               = PointAttribute(PointAttribute::LastControlOvUvAttribute,              "Last Control OvUv");
const PointAttribute PointAttribute::LastControlNeutralFault       = PointAttribute(PointAttribute::LastControlNeutralFaultAttribute,      "Last Control Neutral Fault");
const PointAttribute PointAttribute::LastControlScheduled          = PointAttribute(PointAttribute::LastControlScheduledAttribute,         "Last Control Scheduled");
const PointAttribute PointAttribute::LastControlDigital            = PointAttribute(PointAttribute::LastControlDigitalAttribute,           "Last Control Digital");
const PointAttribute PointAttribute::LastControlAnalog             = PointAttribute(PointAttribute::LastControlAnalogAttribute,            "Last Control Analog");
const PointAttribute PointAttribute::LastControlTemperature        = PointAttribute(PointAttribute::LastControlTemperatureAttribute,       "Last Control Temperature");
const PointAttribute PointAttribute::OvCondition                   = PointAttribute(PointAttribute::OvConditionAttribute,                  "Ov Condition");
const PointAttribute PointAttribute::UvCondition                   = PointAttribute(PointAttribute::UvConditionAttribute,                  "Uv Condition");
const PointAttribute PointAttribute::OpFailedNeutralCurrent        = PointAttribute(PointAttribute::OpFailedNeutralCurrentAttribute,       "Op Failed Neutral Current");
const PointAttribute PointAttribute::NeutralCurrentFault           = PointAttribute(PointAttribute::NeutralCurrentFaultAttribute,          "Neutral Current Fault");
const PointAttribute PointAttribute::BadRelay                      = PointAttribute(PointAttribute::BadRelayAttribute,                     "Bad Relay");
const PointAttribute PointAttribute::DailyMaxOps                   = PointAttribute(PointAttribute::DailyMaxOpsAttribute,                  "Daily Max Ops");
const PointAttribute PointAttribute::VoltageDeltaAbnormal          = PointAttribute(PointAttribute::VoltageDeltaAbnormalAttribute,         "Voltage Delta Abnormal");
const PointAttribute PointAttribute::TempAlarm                     = PointAttribute(PointAttribute::TempAlarmAttribute,                    "Temp Alarm");
const PointAttribute PointAttribute::DSTActive                     = PointAttribute(PointAttribute::DSTActiveAttribute,                    "DST Active");
const PointAttribute PointAttribute::NeutralLockout                = PointAttribute(PointAttribute::NeutralLockoutAttribute,               "Neutral Lockout");
const PointAttribute PointAttribute::IgnoredIndicator              = PointAttribute(PointAttribute::IgnoredIndicatorAttribute,             "Ignored Indicator");

const PointAttribute PointAttribute::TotalOpCount                  = PointAttribute(PointAttribute::TotalOpCountAttribute,                 "Total Op Count");
const PointAttribute PointAttribute::UvCount                       = PointAttribute(PointAttribute::UvCountAttribute,                      "Uv Count");
const PointAttribute PointAttribute::OvCount                       = PointAttribute(PointAttribute::OvCountAttribute,                      "Ov Count");
const PointAttribute PointAttribute::CloseOpCount                  = PointAttribute(PointAttribute::CloseOpCountAttribute,                 "Close Op Count");
const PointAttribute PointAttribute::OpenOpCount                   = PointAttribute(PointAttribute::OpenOpCountAttribute,                  "Open Op Count");

const PointAttribute PointAttribute::LastControlReason             = PointAttribute(PointAttribute::LastControlReasonAttribute,            "Last Control Reason");

const PointAttribute PointAttribute::ForwardSetPoint               = PointAttribute(PointAttribute::ForwardSetPointAttribute,              "FORWARD_SET_POINT");
const PointAttribute PointAttribute::ForwardBandwidth              = PointAttribute(PointAttribute::ForwardBandwidthAttribute,             "FORWARD_BANDWIDTH");



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

///////////////////////////


Attribute::NameToAttributeMap  Attribute::_lookup;

unsigned Attribute::_seed = 0;


Attribute::Attribute( const std::string & name )
    :   _name( name ),
        _value( _seed++ )
{
    _lookup[ _name ] = this;
}


const Attribute & Attribute::Lookup( const std::string & name )
{
    if( auto attribute = Cti::mapFind(_lookup, name) )
    {
        return **attribute;
    }

    throw AttributeNotFound(name);
}


bool Attribute::operator==( const Attribute & rhs ) const
{
    return _value == rhs._value;
}


bool Attribute::operator<( const Attribute & rhs ) const
{
    return _value < rhs._value;
}

//  this ordering should match BuiltInAttribute.java
const Attribute Attribute::Unknown                                          = Attribute( "UNKNOWN" );
const Attribute Attribute::BlinkCount                                       = Attribute( "BLINK_COUNT" );
const Attribute Attribute::CommunicationStatus                              = Attribute( "COMM_STATUS" );
const Attribute Attribute::ControlPoint                                     = Attribute( "CONTROL_POINT" );
const Attribute Attribute::ControlStatus                                    = Attribute( "CONTROL_STATUS" );
const Attribute Attribute::CurrentNeutral                                   = Attribute( "NEUTRAL_CURRENT" );
const Attribute Attribute::Current                                          = Attribute( "CURRENT" );
const Attribute Attribute::CurrentPhaseA                                    = Attribute( "CURRENT_PHASE_A" );
const Attribute Attribute::CurrentPhaseB                                    = Attribute( "CURRENT_PHASE_B" );
const Attribute Attribute::CurrentPhaseC                                    = Attribute( "CURRENT_PHASE_C" );
const Attribute Attribute::CurrentAngle                                     = Attribute( "CURRENT_ANGLE" );
const Attribute Attribute::CurrentAnglePhaseA                               = Attribute( "CURRENT_ANGLE_PHASE_A" );
const Attribute Attribute::CurrentAnglePhaseB                               = Attribute( "CURRENT_ANGLE_PHASE_B" );
const Attribute Attribute::CurrentAnglePhaseC                               = Attribute( "CURRENT_ANGLE_PHASE_C" );
const Attribute Attribute::CurrentWithoutVoltage                            = Attribute( "CURRENT_WITHOUT_VOLTAGE_FLAG" );
const Attribute Attribute::Demand                                           = Attribute( "DEMAND" );
const Attribute Attribute::ReceivedDemand                                   = Attribute( "RECEIVED_DEMAND" );
const Attribute Attribute::DemandPeakkVACoin                                = Attribute( "DEMAND_PEAK_KVA_COIN" );
const Attribute Attribute::DisconnectStatus                                 = Attribute( "DISCONNECT_STATUS" );
const Attribute Attribute::FaultStatus                                      = Attribute( "FAULT_STATUS" );
const Attribute Attribute::ForwardInductivekVArh                            = Attribute( "FORWARD_INDUCTIVE_KVARH" );
const Attribute Attribute::GeneralAlarmFlag                                 = Attribute( "GENERAL_ALARM_FLAG" );
const Attribute Attribute::IEDDemandResetCount                              = Attribute( "IED_DEMAND_RESET_COUNT" );
const Attribute Attribute::kVAh                                             = Attribute( "KVAH" );
const Attribute Attribute::kVAhRateA                                        = Attribute( "KVAH_RATE_A" );
const Attribute Attribute::kVAhRateB                                        = Attribute( "KVAH_RATE_B" );
const Attribute Attribute::kVAhRateC                                        = Attribute( "KVAH_RATE_C" );
const Attribute Attribute::kVAhRateD                                        = Attribute( "KVAH_RATE_D" );
const Attribute Attribute::NetkVAhRateA                                     = Attribute( "NET_KVAH_RATE_A" );
const Attribute Attribute::NetkVAhRateB                                     = Attribute( "NET_KVAH_RATE_B" );
const Attribute Attribute::NetkVAhRateC                                     = Attribute( "NET_KVAH_RATE_C" );
const Attribute Attribute::NetkVAhRateD                                     = Attribute( "NET_KVAH_RATE_D" );
const Attribute Attribute::ReceivedkVAhRateA                                = Attribute( "RECEIVED_KVAH_RATE_A" );
const Attribute Attribute::ReceivedkVAhRateB                                = Attribute( "RECEIVED_KVAH_RATE_B" );
const Attribute Attribute::ReceivedkVAhRateC                                = Attribute( "RECEIVED_KVAH_RATE_C" );
const Attribute Attribute::ReceivedkVAhRateD                                = Attribute( "RECEIVED_KVAH_RATE_D" );
const Attribute Attribute::SumkVAhRateA                                     = Attribute( "SUM_KVAH_RATE_A" );
const Attribute Attribute::SumkVAhRateB                                     = Attribute( "SUM_KVAH_RATE_B" );
const Attribute Attribute::SumkVAhRateC                                     = Attribute( "SUM_KVAH_RATE_C" );
const Attribute Attribute::SumkVAhRateD                                     = Attribute( "SUM_KVAH_RATE_D" );
const Attribute Attribute::kVAr                                             = Attribute( "KVAR" );
const Attribute Attribute::ReceivedkVAr                                     = Attribute( "RECEIVED_KVAR" );
const Attribute Attribute::kVArh                                            = Attribute( "KVARH" );
const Attribute Attribute::kVArhRateA                                       = Attribute( "KVARH_RATE_A" );
const Attribute Attribute::kVArhRateB                                       = Attribute( "KVARH_RATE_B" );
const Attribute Attribute::kVArhRateC                                       = Attribute( "KVARH_RATE_C" );
const Attribute Attribute::kVArhRateD                                       = Attribute( "KVARH_RATE_D" );
const Attribute Attribute::LMGroupStatus                                    = Attribute( "LM_GROUP_STATUS" );
const Attribute Attribute::LoadProfile                                      = Attribute( "LOAD_PROFILE" );
const Attribute Attribute::LoadSideVoltageDetected                          = Attribute( "LOAD_SIDE_VOLTAGE_DETECTED_FLAG" );
const Attribute Attribute::MeterBoxCoverRemoval                             = Attribute( "METER_BOX_COVER_REMOVAL_FLAG" );
const Attribute Attribute::MaximumVoltage                                   = Attribute( "MAXIMUM_VOLTAGE" );
const Attribute Attribute::MaximumVoltageFrozen                             = Attribute( "MAXIMUM_VOLTAGE_FROZEN" );
const Attribute Attribute::MinimumVoltage                                   = Attribute( "MINIMUM_VOLTAGE" );
const Attribute Attribute::MinimumVoltageFrozen                             = Attribute( "MINIMUM_VOLTAGE_FROZEN" );
const Attribute Attribute::OutageLog                                        = Attribute( "OUTAGE_LOG" );
const Attribute Attribute::OutageStatus                                     = Attribute( "OUTAGE_STATUS" );
const Attribute Attribute::OutofVoltage                                     = Attribute( "OUT_OF_VOLTAGE_FLAG" );
const Attribute Attribute::VoltageOutofLimits                               = Attribute( "VOLTAGE_OUT_OF_LIMITS_FLAG" );
const Attribute Attribute::OverVoltage                                      = Attribute( "OVER_VOLTAGE" );
const Attribute Attribute::OverVoltageMeasured                              = Attribute( "OVER_VOLTAGE_MEASURED" );
const Attribute Attribute::OverVoltageThreshold                             = Attribute( "OVER_VOLTAGE_THRESHOLD" );
const Attribute Attribute::PeakDemand                                       = Attribute( "PEAK_DEMAND" );
const Attribute Attribute::NetPeakDemand                                    = Attribute( "NET_PEAK_DEMAND" );
const Attribute Attribute::SumPeakDemand                                    = Attribute( "SUM_PEAK_DEMAND" );
const Attribute Attribute::PeakDemandFrozen                                 = Attribute( "PEAK_DEMAND_FROZEN" );
const Attribute Attribute::PeakDemandRateA                                  = Attribute( "PEAK_DEMAND_RATE_A" );
const Attribute Attribute::PeakDemandFrozenRateA                            = Attribute( "PEAK_DEMAND_FROZEN_RATE_A" );
const Attribute Attribute::PeakDemandRateB                                  = Attribute( "PEAK_DEMAND_RATE_B" );
const Attribute Attribute::PeakDemandFrozenRateB                            = Attribute( "PEAK_DEMAND_FROZEN_RATE_B" );
const Attribute Attribute::PeakDemandRateC                                  = Attribute( "PEAK_DEMAND_RATE_C" );
const Attribute Attribute::PeakDemandFrozenRateC                            = Attribute( "PEAK_DEMAND_FROZEN_RATE_C" );
const Attribute Attribute::PeakDemandRateD                                  = Attribute( "PEAK_DEMAND_RATE_D" );
const Attribute Attribute::PeakDemandFrozenRateD                            = Attribute( "PEAK_DEMAND_FROZEN_RATE_D" );
const Attribute Attribute::PeakDemandRateE                                  = Attribute( "PEAK_DEMAND_RATE_E" );
const Attribute Attribute::NetPeakDemandRateA                               = Attribute( "NET_PEAK_DEMAND_RATE_A" );
const Attribute Attribute::NetPeakDemandRateB                               = Attribute( "NET_PEAK_DEMAND_RATE_B" );
const Attribute Attribute::NetPeakDemandRateC                               = Attribute( "NET_PEAK_DEMAND_RATE_C" );
const Attribute Attribute::NetPeakDemandRateD                               = Attribute( "NET_PEAK_DEMAND_RATE_D" );
const Attribute Attribute::SumPeakDemandRateA                               = Attribute( "SUM_PEAK_DEMAND_RATE_A" );
const Attribute Attribute::SumPeakDemandRateB                               = Attribute( "SUM_PEAK_DEMAND_RATE_B" );
const Attribute Attribute::SumPeakDemandRateC                               = Attribute( "SUM_PEAK_DEMAND_RATE_C" );
const Attribute Attribute::SumPeakDemandRateD                               = Attribute( "SUM_PEAK_DEMAND_RATE_D" );
const Attribute Attribute::ReceivedPeakDemand                               = Attribute( "RECEIVED_PEAK_DEMAND" );
const Attribute Attribute::ReceivedPeakDemandRateA                          = Attribute( "RECEIVED_PEAK_DEMAND_RATE_A" );
const Attribute Attribute::ReceivedPeakDemandRateB                          = Attribute( "RECEIVED_PEAK_DEMAND_RATE_B" );
const Attribute Attribute::ReceivedPeakDemandRateC                          = Attribute( "RECEIVED_PEAK_DEMAND_RATE_C" );
const Attribute Attribute::ReceivedPeakDemandRateD                          = Attribute( "RECEIVED_PEAK_DEMAND_RATE_D" );
const Attribute Attribute::DeliveredkVA                                     = Attribute( "DELIVERED_KVA" );
const Attribute Attribute::ReceivedkVA                                      = Attribute( "RECEIVED_KVA" );
const Attribute Attribute::SumkVA                                           = Attribute( "SUM_KVA" );
const Attribute Attribute::PeakkVA                                          = Attribute( "PEAK_KVA" );
const Attribute Attribute::PeakkVARateA                                     = Attribute( "PEAK_KVA_RATE_A" );
const Attribute Attribute::PeakkVARateB                                     = Attribute( "PEAK_KVA_RATE_B" );
const Attribute Attribute::PeakkVARateC                                     = Attribute( "PEAK_KVA_RATE_C" );
const Attribute Attribute::PeakkVARateD                                     = Attribute( "PEAK_KVA_RATE_D" );
const Attribute Attribute::PeakkVACoincidental                              = Attribute( "PEAK_KVA_COIN" );
const Attribute Attribute::ReceivedPeakkVA                                  = Attribute( "RECEIVED_PEAK_KVA" );
const Attribute Attribute::ReceivedPeakkVARateA                             = Attribute( "RECEIVED_PEAK_KVA_RATE_A" );
const Attribute Attribute::ReceivedPeakkVARateB                             = Attribute( "RECEIVED_PEAK_KVA_RATE_B" );
const Attribute Attribute::ReceivedPeakkVARateC                             = Attribute( "RECEIVED_PEAK_KVA_RATE_C" );
const Attribute Attribute::ReceivedPeakkVARateD                             = Attribute( "RECEIVED_PEAK_KVA_RATE_D" );
const Attribute Attribute::SumPeakkVA                                       = Attribute( "SUM_PEAK_KVA" );
const Attribute Attribute::SumPeakkVARateA                                  = Attribute( "SUM_PEAK_KVA_RATE_A" );
const Attribute Attribute::SumPeakkVARateB                                  = Attribute( "SUM_PEAK_KVA_RATE_B" );
const Attribute Attribute::SumPeakkVARateC                                  = Attribute( "SUM_PEAK_KVA_RATE_C" );
const Attribute Attribute::SumPeakkVARateD                                  = Attribute( "SUM_PEAK_KVA_RATE_D" );
const Attribute Attribute::PeakkVAr                                         = Attribute( "PEAK_KVAR" );
const Attribute Attribute::PeakkVArCoincidental                             = Attribute( "PEAK_KVAR_COIN" );
const Attribute Attribute::PeakkVArRateA                                    = Attribute( "PEAK_KVAR_RATE_A" );
const Attribute Attribute::PeakkVArRateB                                    = Attribute( "PEAK_KVAR_RATE_B" );
const Attribute Attribute::PeakkVArRateC                                    = Attribute( "PEAK_KVAR_RATE_C" );
const Attribute Attribute::PeakkVArRateD                                    = Attribute( "PEAK_KVAR_RATE_D" );
const Attribute Attribute::ReceivedPeakkVAr                                 = Attribute( "RECEIVED_PEAK_KVAR" );
const Attribute Attribute::ReceivedPeakkVArRateA                            = Attribute( "RECEIVED_PEAK_KVAR_RATE_A" );
const Attribute Attribute::ReceivedPeakkVArRateB                            = Attribute( "RECEIVED_PEAK_KVAR_RATE_B" );
const Attribute Attribute::ReceivedPeakkVArRateC                            = Attribute( "RECEIVED_PEAK_KVAR_RATE_C" );
const Attribute Attribute::ReceivedPeakkVArRateD                            = Attribute( "RECEIVED_PEAK_KVAR_RATE_D" );
const Attribute Attribute::SumPeakkVAr                                      = Attribute( "SUM_PEAK_KVAR" );
const Attribute Attribute::SumPeakkVArRateA                                 = Attribute( "SUM_PEAK_KVAR_RATE_A" );
const Attribute Attribute::SumPeakkVArRateB                                 = Attribute( "SUM_PEAK_KVAR_RATE_B" );
const Attribute Attribute::SumPeakkVArRateC                                 = Attribute( "SUM_PEAK_KVAR_RATE_C" );
const Attribute Attribute::SumPeakkVArRateD                                 = Attribute( "SUM_PEAK_KVAR_RATE_D" );
const Attribute Attribute::PeakkVArh                                        = Attribute( "PEAK_KVARH" );
const Attribute Attribute::PeakkVArhCoincidental                            = Attribute( "PEAK_KVARH_COIN" );
const Attribute Attribute::Phase                                            = Attribute( "PHASE" );
const Attribute Attribute::PowerFactor                                      = Attribute( "POWER_FACTOR" );
const Attribute Attribute::PowerFactorCoincidental                          = Attribute( "POWER_FACTOR_COIN" );
const Attribute Attribute::PowerFactorPhaseA                                = Attribute( "POWER_FACTOR_PHASE_A" );
const Attribute Attribute::PowerFactorPhaseB                                = Attribute( "POWER_FACTOR_PHASE_B" );
const Attribute Attribute::PowerFactorPhaseC                                = Attribute( "POWER_FACTOR_PHASE_C" );
const Attribute Attribute::PowerFailFlag                                    = Attribute( "POWER_FAIL_FLAG" );
const Attribute Attribute::ProfileChannel2                                  = Attribute( "PROFILE_CHANNEL_2" );
const Attribute Attribute::ProfileChannel3                                  = Attribute( "PROFILE_CHANNEL_3" );
const Attribute Attribute::RecordingInterval                                = Attribute( "RECORDING_INTERVAL" );
const Attribute Attribute::RelativeHumidity                                 = Attribute( "HUMIDITY" );
const Attribute Attribute::Relay1kWLoadSize                                 = Attribute( "RELAY_1_LOAD_SIZE" );
const Attribute Attribute::Relay1RemainingControlTime                       = Attribute( "RELAY_1_REMAINING_CONTROL" );
const Attribute Attribute::Relay1RunTime                                    = Attribute( "RELAY_1_RUN_TIME_DATA_LOG" );
const Attribute Attribute::Relay1ShedTime                                   = Attribute( "RELAY_1_SHED_TIME_DATA_LOG" );
const Attribute Attribute::Relay2kWLoadSize                                 = Attribute( "RELAY_2_LOAD_SIZE" );
const Attribute Attribute::Relay2RemainingControlTime                       = Attribute( "RELAY_2_REMAINING_CONTROL" );
const Attribute Attribute::Relay2RunTime                                    = Attribute( "RELAY_2_RUN_TIME_DATA_LOG" );
const Attribute Attribute::Relay2ShedTime                                   = Attribute( "RELAY_2_SHED_TIME_DATA_LOG" );
const Attribute Attribute::Relay3kWLoadSize                                 = Attribute( "RELAY_3_LOAD_SIZE" );
const Attribute Attribute::Relay3RemainingControlTime                       = Attribute( "RELAY_3_REMAINING_CONTROL" );
const Attribute Attribute::Relay3RunTime                                    = Attribute( "RELAY_3_RUN_TIME_DATA_LOG" );
const Attribute Attribute::Relay3ShedTime                                   = Attribute( "RELAY_3_SHED_TIME_DATA_LOG" );
const Attribute Attribute::Relay4RemainingControlTime                       = Attribute( "RELAY_4_REMAINING_CONTROL" );
const Attribute Attribute::Relay4RunTime                                    = Attribute( "RELAY_4_RUN_TIME_DATA_LOG" );
const Attribute Attribute::Relay4ShedTime                                   = Attribute( "RELAY_4_SHED_TIME_DATA_LOG" );
const Attribute Attribute::ReportingInterval                                = Attribute( "REPORTING_INTERVAL" );
const Attribute Attribute::ReverseInductivekVArh                            = Attribute( "REVERSE_INDUCTIVE_KVARH" );
const Attribute Attribute::ReversePowerFlag                                 = Attribute( "REVERSE_POWER_FLAG" );
const Attribute Attribute::RFDemandResetStatus                              = Attribute( "RF_DEMAND_RESET_STATUS" );
const Attribute Attribute::ServiceStatus                                    = Attribute( "SERVICE_STATUS" );
const Attribute Attribute::TamperFlag                                       = Attribute( "TAMPER_FLAG" );
const Attribute Attribute::Temperature                                      = Attribute( "TEMPERATURE" );
const Attribute Attribute::TemperatureofDevice                              = Attribute( "TEMPERATURE_DEVICE" );
const Attribute Attribute::TotalLUFEventCount                               = Attribute( "TOTAL_LUF_COUNT" );
const Attribute Attribute::TotalLUVEventCount                               = Attribute( "TOTAL_LUV_COUNT" );
const Attribute Attribute::UnderVoltage                                     = Attribute( "UNDER_VOLTAGE" );
const Attribute Attribute::UnderVoltageMeasured                             = Attribute( "UNDER_VOLTAGE_MEASURED" );
const Attribute Attribute::UnderVoltageThreshold                            = Attribute( "UNDER_VOLTAGE_THRESHOLD" );
const Attribute Attribute::UsageReading                                     = Attribute( "USAGE" );
const Attribute Attribute::UsageFrozen                                      = Attribute( "USAGE_FROZEN" );
const Attribute Attribute::UsageRateA                                       = Attribute( "USAGE_RATE_A" );
const Attribute Attribute::UsageRateB                                       = Attribute( "USAGE_RATE_B" );
const Attribute Attribute::UsageRateC                                       = Attribute( "USAGE_RATE_C" );
const Attribute Attribute::UsageRateD                                       = Attribute( "USAGE_RATE_D" );
const Attribute Attribute::UsageRateE                                       = Attribute( "USAGE_RATE_E" );
const Attribute Attribute::WaterUsageReading                                = Attribute( "USAGE_WATER" );
const Attribute Attribute::Voltage                                          = Attribute( "VOLTAGE" );
const Attribute Attribute::VoltagePhaseA                                    = Attribute( "VOLTAGE_PHASE_A" );
const Attribute Attribute::VoltagePhaseB                                    = Attribute( "VOLTAGE_PHASE_B" );
const Attribute Attribute::VoltagePhaseC                                    = Attribute( "VOLTAGE_PHASE_C" );
const Attribute Attribute::MinimumVoltagePhaseA                             = Attribute( "MINIMUM_VOLTAGE_PHASE_A" );
const Attribute Attribute::MinimumVoltagePhaseB                             = Attribute( "MINIMUM_VOLTAGE_PHASE_B" );
const Attribute Attribute::MinimumVoltagePhaseC                             = Attribute( "MINIMUM_VOLTAGE_PHASE_C" );
const Attribute Attribute::MaximumVoltagePhaseA                             = Attribute( "MAXIMUM_VOLTAGE_PHASE_A" );
const Attribute Attribute::MaximumVoltagePhaseB                             = Attribute( "MAXIMUM_VOLTAGE_PHASE_B" );
const Attribute Attribute::MaximumVoltagePhaseC                             = Attribute( "MAXIMUM_VOLTAGE_PHASE_C" );
const Attribute Attribute::VoltageProfile                                   = Attribute( "VOLTAGE_PROFILE" );
const Attribute Attribute::WattHourPulseFailure                             = Attribute( "WATT_HOUR_PULSE_FAILURE" );
const Attribute Attribute::ZeroUsageFlag                                    = Attribute( "ZERO_USAGE_FLAG" );
const Attribute Attribute::ZigBeeLinkStatus                                 = Attribute( "ZIGBEE_LINK_STATUS" );
const Attribute Attribute::TerminalBlockCoverRemoval                        = Attribute( "TERMINAL_BLOCK_COVER_REMOVAL_FLAG" );
const Attribute Attribute::IndoorTemperature                                = Attribute( "INDOOR_TEMPERATURE" );
const Attribute Attribute::OutdoorTemperature                               = Attribute( "OUTDOOR_TEMPERATURE" );
const Attribute Attribute::CoolSetTemperature                               = Attribute( "COOL_SET_TEMPERATURE" );
const Attribute Attribute::HeatSetTemperature                               = Attribute( "HEAT_SET_TEMPERATURE" );

const Attribute Attribute::DeliveredkWh                                     = Attribute( "DELIVERED_KWH" );
const Attribute Attribute::ReceivedkWh                                      = Attribute( "RECEIVED_KWH" );
const Attribute Attribute::ReceivedkWhFrozen                                = Attribute( "RECEIVED_KWH_FROZEN" );
const Attribute Attribute::ReceivedkWhRateA                                 = Attribute( "RECEIVED_KWH_RATE_A" );
const Attribute Attribute::ReceivedkWhRateB                                 = Attribute( "RECEIVED_KWH_RATE_B" );
const Attribute Attribute::ReceivedkWhRateC                                 = Attribute( "RECEIVED_KWH_RATE_C" );
const Attribute Attribute::ReceivedkWhRateD                                 = Attribute( "RECEIVED_KWH_RATE_D" );
const Attribute Attribute::ReceivedkWhRateE                                 = Attribute( "RECEIVED_KWH_RATE_E" );
const Attribute Attribute::ReceivedkVAh                                     = Attribute( "RECEIVED_KVAH" );

const Attribute Attribute::NetkWh                                           = Attribute( "NET_KWH" );
const Attribute Attribute::NetkWhRateA                                      = Attribute( "NET_KWH_RATE_A" );
const Attribute Attribute::NetkWhRateB                                      = Attribute( "NET_KWH_RATE_B" );
const Attribute Attribute::NetkWhRateC                                      = Attribute( "NET_KWH_RATE_C" );
const Attribute Attribute::NetkWhRateD                                      = Attribute( "NET_KWH_RATE_D" );
const Attribute Attribute::NetkWhRateE                                      = Attribute( "NET_KWH_RATE_E" );

const Attribute Attribute::SumkWh                                           = Attribute( "SUM_KWH" );
const Attribute Attribute::SumkWhRateA                                      = Attribute( "SUM_KWH_RATE_A" );
const Attribute Attribute::SumkWhRateB                                      = Attribute( "SUM_KWH_RATE_B" );
const Attribute Attribute::SumkWhRateC                                      = Attribute( "SUM_KWH_RATE_C" );
const Attribute Attribute::SumkWhRateD                                      = Attribute( "SUM_KWH_RATE_D" );
const Attribute Attribute::SumkVAh                                          = Attribute( "SUM_KVAH" );
const Attribute Attribute::SumkVArh                                         = Attribute( "SUM_KVARH" );
const Attribute Attribute::SumkVArhRateA                                    = Attribute( "SUM_KVARH_RATE_A" );
const Attribute Attribute::SumkVArhRateB                                    = Attribute( "SUM_KVARH_RATE_B" );
const Attribute Attribute::SumkVArhRateC                                    = Attribute( "SUM_KVARH_RATE_C" );
const Attribute Attribute::SumkVArhRateD                                    = Attribute( "SUM_KVARH_RATE_D" );

const Attribute Attribute::UsageperInterval                                 = Attribute( "USAGE_PER_INTERVAL" );
const Attribute Attribute::DeliveredkWhperInterval                          = Attribute( "DELIVERED_KWH_PER_INTERVAL" );
const Attribute Attribute::ReceivedkWhperInterval                           = Attribute( "RECEIVED_KWH_PER_INTERVAL" );
const Attribute Attribute::SumkWhperInterval                                = Attribute( "SUM_KWH_PER_INTERVAL" );
const Attribute Attribute::NetkWhperInterval                                = Attribute( "NET_KWH_PER_INTERVAL" );
const Attribute Attribute::SumkVAhperInterval                               = Attribute( "SUM_KVAH_PER_INTERVAL" );
const Attribute Attribute::SumkVArhperInterval                              = Attribute( "SUM_KVARH_PER_INTERVAL" );
const Attribute Attribute::WaterUsageperInterval                            = Attribute( "WATER_USAGE_PER_INTERVAL" );
const Attribute Attribute::ForwardInductivekVArhperInterval                 = Attribute( "FORWARD_INDUCTIVE_KVARH_PER_INTERVAL" );
const Attribute Attribute::ForwardCapacitivekVArhperInterval                = Attribute( "FORWARD_CAPACITIVE_KVARH_PER_INTERVAL" );
const Attribute Attribute::ReverseInductivekVArhperInterval                 = Attribute( "REVERSE_INDUCTIVE_KVARH_PER_INTERVAL" );
const Attribute Attribute::ReverseCapacitivekVArhperInterval                = Attribute( "REVERSE_CAPACITIVE_KVARH_PER_INTERVAL" );

const Attribute Attribute::DeliveredkWLoadProfile                           = Attribute( "DELIVERED_KW_LOAD_PROFILE" );
const Attribute Attribute::DeliveredkVArLoadProfile                         = Attribute( "DELIVERED_KVAR_LOAD_PROFILE" );
const Attribute Attribute::ReceivedkWLoadProfile                            = Attribute( "RECEIVED_KW_LOAD_PROFILE" );
const Attribute Attribute::SumkWLoadProfile                                 = Attribute( "SUM_KW_LOAD_PROFILE" );
const Attribute Attribute::NetkWLoadProfile                                 = Attribute( "NET_KW_LOAD_PROFILE" );
const Attribute Attribute::SumkVALoadProfile                                = Attribute( "SUM_KVA_LOAD_PROFILE" );
const Attribute Attribute::SumkVArLoadProfile                               = Attribute( "SUM_KVAR_LOAD_PROFILE" );

const Attribute Attribute::NetDeliveredkVArh                                = Attribute( "NET_DELIVERED_KVARH" );
const Attribute Attribute::NetDeliveredkVArhRateA                           = Attribute( "NET_DELIVERED_KVARH_RATE_A" );
const Attribute Attribute::NetDeliveredkVArhRateB                           = Attribute( "NET_DELIVERED_KVARH_RATE_B" );
const Attribute Attribute::NetDeliveredkVArhRateC                           = Attribute( "NET_DELIVERED_KVARH_RATE_C" );
const Attribute Attribute::NetDeliveredkVArhRateD                           = Attribute( "NET_DELIVERED_KVARH_RATE_D" );

const Attribute Attribute::ReceivedkVArh                                    = Attribute( "RECEIVED_KVARH" );
const Attribute Attribute::ReceivedkVArhRateA                               = Attribute( "RECEIVED_KVARH_RATE_A" );
const Attribute Attribute::ReceivedkVArhRateB                               = Attribute( "RECEIVED_KVARH_RATE_B" );
const Attribute Attribute::ReceivedkVArhRateC                               = Attribute( "RECEIVED_KVARH_RATE_C" );
const Attribute Attribute::ReceivedkVArhRateD                               = Attribute( "RECEIVED_KVARH_RATE_D" );
const Attribute Attribute::NetReceivedkVArh                                 = Attribute( "NET_RECEIVED_KVARH" );
const Attribute Attribute::NetReceivedkVArhRateA                            = Attribute( "NET_RECEIVED_KVARH_RATE_A" );
const Attribute Attribute::NetReceivedkVArhRateB                            = Attribute( "NET_RECEIVED_KVARH_RATE_B" );
const Attribute Attribute::NetReceivedkVArhRateC                            = Attribute( "NET_RECEIVED_KVARH_RATE_C" );
const Attribute Attribute::NetReceivedkVArhRateD                            = Attribute( "NET_RECEIVED_KVARH_RATE_D" );

const Attribute Attribute::AlternateModeEntry                               = Attribute( "ALTERNATE_MODE_ENTRY" );
const Attribute Attribute::ANSISecurityFailed                               = Attribute( "ANSI_SECURITY_FAILED" );
const Attribute Attribute::BadUpgradeSecurityParameter                      = Attribute( "BAD_UPGRADE_SECURITY_PARAM" );
const Attribute Attribute::ConfigurationError                               = Attribute( "CONFIGURATION_ERROR" );
const Attribute Attribute::ClockError                                       = Attribute( "CLOCK_ERROR" );
const Attribute Attribute::CrystalOscillatorError                           = Attribute( "CRYSTAL_OSCILLATOR_ERROR" );
const Attribute Attribute::CurrentLoss                                      = Attribute( "CURRENT_LOSS" );
const Attribute Attribute::CurrentWaveformDistortion                        = Attribute( "CURRENT_WAVEFORM_DISTORTION" );
const Attribute Attribute::DemandOverload                                   = Attribute( "DEMAND_OVERLOAD" );
const Attribute Attribute::DemandReadsAndReset                              = Attribute( "DEMAND_READS_AND_RESET" );
const Attribute Attribute::DemandThresholdExceededWarning                   = Attribute( "DEMAND_THRESHOLD_EXCEEDED_WARNING" );
const Attribute Attribute::DNP3AddressChanged                               = Attribute( "DNP3_ADDRESS_CHANGED" );
const Attribute Attribute::DisplayLockedByWarning                           = Attribute( "DISPLAY_LOCKED_BY_WARNING" );
const Attribute Attribute::EepromAccessError                                = Attribute( "EEPROM_ACCESS_ERROR" );
const Attribute Attribute::EncryptionKeyTableCrcError                       = Attribute( "ENCRYPTION_KEY_TABLE_CRC_ERROR" );
const Attribute Attribute::EndOfCalendarWarning                             = Attribute( "END_OF_CALENDAR_WARNING" );
const Attribute Attribute::EnergyAccumulatedWhileInStandbyMode              = Attribute( "ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE" );
const Attribute Attribute::FailedUpgradeSignatureVerification               = Attribute( "FAILED_UPGRADE_SIGNATURE_VERIF" );
const Attribute Attribute::ImproperMeterEngineOperationWarning              = Attribute( "IMPROPER_METER_ENGINE_OPERATION_WARNING" );
const Attribute Attribute::InactivePhaseCurrentDiagnosticError              = Attribute( "INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR" );
const Attribute Attribute::InternalCommunicationError                       = Attribute( "INTERNAL_COMMUNICATION_ERROR" );
const Attribute Attribute::InternalErrorFlag                                = Attribute( "INTERNAL_ERROR_FLAG" );
const Attribute Attribute::InvalidService                                   = Attribute( "INVALID_SERVICE" );
const Attribute Attribute::LineFrequencyWarning                             = Attribute( "LINE_FREQUENCY_WARNING" );
const Attribute Attribute::LoadSideVoltageIsMissing                         = Attribute( "LOAD_SIDE_VOLTAGE_IS_MISSING" );
const Attribute Attribute::LossOfAllCurrent                                 = Attribute( "LOSS_OF_ALL_CURRENT" );
const Attribute Attribute::LossOfPhaseACurrent                              = Attribute( "LOSS_OF_PHASE_A_CURRENT" );
const Attribute Attribute::LossOfPhaseCCurrent                              = Attribute( "LOSS_OF_PHASE_C_CURRENT" );
const Attribute Attribute::LowBatteryWarning                                = Attribute( "LOW_BATTERY_WARNING" );
const Attribute Attribute::LowLossPotential                                 = Attribute( "LOW_LOSS_POTENTIAL" );
const Attribute Attribute::MassMemoryError                                  = Attribute( "MASS_MEMORY_ERROR" );
const Attribute Attribute::MeasurementError                                 = Attribute( "MEASUREMENT_ERROR" );
const Attribute Attribute::MeterReconfigure                                 = Attribute( "METER_RECONFIGURE" );
const Attribute Attribute::MetrologyCommunicationFailure                    = Attribute( "METROLOGY_COMM_FAILURE" );
const Attribute Attribute::NonVolatileMemFailure                            = Attribute( "NON_VOLATILE_MEM_FAILURE" );
const Attribute Attribute::OutstationDNP3SerCommLocked                      = Attribute( "OUTSTATION_DNP3_SERCOMM_LOCKED" );
const Attribute Attribute::PasswordTableCrcError                            = Attribute( "PASSWORD_TABLE_CRC_ERROR" );
const Attribute Attribute::PhaseAngleDisplacement                           = Attribute( "PHASE_ANGLE_DISPLACEMENT" );
const Attribute Attribute::PhaseLoss                                        = Attribute( "PHASE_LOSS" );
const Attribute Attribute::PolarityCrossPhaseAndEnergyFlowDiagnostic        = Attribute( "POLARITY_CROSS_PHASE_ENERGY_FLOW_DIAGNOSTIC" );
const Attribute Attribute::PotentialIndicatorWarning                        = Attribute( "POTENTIAL_INDICATOR_WARNING" );
const Attribute Attribute::PowerFailDataSaveError                           = Attribute( "POWER_FAIL_DATA_SAVE_ERROR" );
const Attribute Attribute::PqmTestFailureWarning                            = Attribute( "PQM_TEST_FAILURE_WARNING" );
const Attribute Attribute::RamError                                         = Attribute( "RAM_ERROR" );
const Attribute Attribute::RegisterFullScaleExceeded                        = Attribute( "REGISTER_FULL_SCALE_EXCEEDED" );
const Attribute Attribute::ReversedAggregate                                = Attribute( "REVERSED_AGGREGATE" );
const Attribute Attribute::ReversedPhaseA                                   = Attribute( "REVERSED_PHASE_A" );
const Attribute Attribute::ReversedPhaseC                                   = Attribute( "REVERSED_PHASE_C" );
const Attribute Attribute::RfnBlinkCount                                    = Attribute( "RFN_BLINK_COUNT" );
const Attribute Attribute::RfnBlinkRestoreCount                             = Attribute( "RFN_BLINK_RESTORE_COUNT" );
const Attribute Attribute::RFNHighTemperatureAlarm                          = Attribute( "RFN_TEMPERATURE_ALARM" );
const Attribute Attribute::RfnOutageCount                                   = Attribute( "RFN_OUTAGE_COUNT" );
const Attribute Attribute::RfnOutageRestoreCount                            = Attribute( "RFN_OUTAGE_RESTORE_COUNT" );
const Attribute Attribute::RomError                                         = Attribute( "ROM_ERROR" );
const Attribute Attribute::SeasonChange                                     = Attribute( "SEASON_CHANGE" );
const Attribute Attribute::SecurityConfigurationError                       = Attribute( "SECURITY_CONFIGURATION_ERROR" );
const Attribute Attribute::SelfCheckError                                   = Attribute( "SELF_CHECK_ERROR" );
const Attribute Attribute::ServiceCurrentTestFailureWarning                 = Attribute( "SERVICE_CURRENT_TEST_FAILURE_WARNING" );
const Attribute Attribute::ServiceDisconnectSwitchError                     = Attribute( "SERVICE_DISCONNECT_SWITCH_ERROR" );
const Attribute Attribute::ServiceDisconnectSwitchOpen                      = Attribute( "SERVICE_DISCONNECT_SWITCH_OPEN" );
const Attribute Attribute::ServiceDisconnectSwitchSensorError               = Attribute( "SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR" );
const Attribute Attribute::SiteScanError                                    = Attribute( "SITESCAN_ERROR" );
const Attribute Attribute::StuckSwitch                                      = Attribute( "STUCK_SWITCH" );
const Attribute Attribute::TableCrcError                                    = Attribute( "TABLE_CRC_ERROR" );
const Attribute Attribute::THDVorTDDIError                                  = Attribute( "THD_V_OR_TDD_I_ERROR" );
const Attribute Attribute::TimeAdjustment                                   = Attribute( "TIME_ADJUSTMENT" );
const Attribute Attribute::TimeSyncFailed                                   = Attribute( "TIME_SYNC_FAILED" );
const Attribute Attribute::TOUScheduleError                                 = Attribute( "TOU_SCHEDULE_ERROR" );
const Attribute Attribute::Unconfigured                                     = Attribute( "UNCONFIGURED" );
const Attribute Attribute::Unprogrammed                                     = Attribute( "UNPROGRAMMED" );
const Attribute Attribute::UserProgrammableTemperatureThresholdExceeded     = Attribute( "USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED" );
const Attribute Attribute::VoltageAlerts                                    = Attribute( "VOLTAGE_ALERTS" );
const Attribute Attribute::VoltageLoss                                      = Attribute( "VOLTAGE_LOSS" );
const Attribute Attribute::VoltagePhaseAOut                                 = Attribute( "VOLTAGE_PHASE_A_OUT" );
const Attribute Attribute::VoltagePhaseBOut                                 = Attribute( "VOLTAGE_PHASE_B_OUT" );
const Attribute Attribute::VoltagePhaseCOut                                 = Attribute( "VOLTAGE_PHASE_C_OUT" );
const Attribute Attribute::VoltagePhaseError                                = Attribute( "VOLTAGE_PHASE_ERROR" );

const Attribute Attribute::ConnectedDeviceCount                             = Attribute( "CONNECTED_DEVICE_COUNT" );
const Attribute Attribute::StreamingDeviceCount                             = Attribute( "STREAMING_DEVICE_COUNT" );
const Attribute Attribute::DataStreamingLoad                                = Attribute( "DATA_STREAMING_LOAD" );

const Attribute Attribute::FirmwareVersion                                  = Attribute( "FIRMWARE_VERSION" );
const Attribute Attribute::IgnoredControlReason                             = Attribute( "IGNORED_CONTROL_REASON" );
const Attribute Attribute::IpAddress                                        = Attribute( "IP_ADDRESS" );
const Attribute Attribute::LastControlReason                                = Attribute( "LAST_CONTROL_REASON" );
const Attribute Attribute::NeutralCurrentSensor                             = Attribute( "NEUTRAL_CURRENT_SENSOR" );
const Attribute Attribute::SerialNumber                                     = Attribute( "SERIAL_NUMBER" );
const Attribute Attribute::UdpPort                                          = Attribute( "UDP_PORT" );
const Attribute Attribute::ConnectedLoad                                    = Attribute( "CONNECTED_LOAD" );
const Attribute Attribute::DiversifiedLoad                                  = Attribute( "DIVERSIFIED_LOAD" );
const Attribute Attribute::MaxLoadReduction                                 = Attribute( "MAX_LOAD_REDUCTION" );
const Attribute Attribute::AvailableLoadReduction                           = Attribute( "AVAILABLE_LOAD_REDUCTION" );

const Attribute Attribute::PorterCpuUtilization                             = Attribute( "PORTER_CPU_UTILIZATION" );
const Attribute Attribute::DispatchCpuUtilization                           = Attribute( "DISPATCH_CPU_UTILIZATION" );
const Attribute Attribute::ScannerCpuUtilization                            = Attribute( "SCANNER_CPU_UTILIZATION" );
const Attribute Attribute::CalcCpuUtilization                               = Attribute( "CALC_CPU_UTILIZATION" );
const Attribute Attribute::CapcontrolCpuUtilization                         = Attribute( "CAPCONTROL_CPU_UTILIZATION" );
const Attribute Attribute::FdrCpuUtilization                                = Attribute( "FDR_CPU_UTILIZATION" );
const Attribute Attribute::MacsCpuUtilization                               = Attribute( "MACS_CPU_UTILIZATION" );

const Attribute Attribute::NotificationServerCpuUtilization                 = Attribute( "NOTIFICATION_SERVER_CPU_UTILIZATION" );
const Attribute Attribute::ServiceManagerCpuUtilization                     = Attribute( "SERVICE_MANAGER_CPU_UTILIZATION" );
const Attribute Attribute::WebServiceCpuUtilization                         = Attribute( "WEB_SERVICE_CPU_UTILIZATION" );

const Attribute Attribute::PorterMemoryUtilization                          = Attribute( "PORTER_MEMORY_UTILIZATION" );
const Attribute Attribute::DispatchMemoryUtilization                        = Attribute( "DISPATCH_MEMORY_UTILIZATION" );
const Attribute Attribute::ScannerMemoryUtilization                         = Attribute( "SCANNER_MEMORY_UTILIZATION" );
const Attribute Attribute::CalcMemoryUtilization                            = Attribute( "CALC_MEMORY_UTILIZATION" );
const Attribute Attribute::CapcontrolMemoryUtilization                      = Attribute( "CAPCONTROL_MEMORY_UTILIZATION" );
const Attribute Attribute::FdrMemoryUtilization                             = Attribute( "FDR_MEMORY_UTILIZATION" );
const Attribute Attribute::MacsMemoryUtilization                            = Attribute( "MACS_MEMORY_UTILIZATION" );

const Attribute Attribute::NotificationServerMemoryUtilization              = Attribute( "NOTIFICATION_SERVER_MEMORY_UTILIZATION" );
const Attribute Attribute::ServiceManagerMemoryUtilization                  = Attribute( "SERVICE_MANAGER_MEMORY_UTILIZATION" );
const Attribute Attribute::WebServiceMemoryUtilization                      = Attribute( "WEB_SERVICE_MEMORY_UTILIZATION" );

const Attribute Attribute::LoadManagementCpuUtilization                     = Attribute( "LOAD_MANAGEMENT_CPU_UTILIZATION" );
const Attribute Attribute::LoadManagementMemoryUtilization                  = Attribute( "LOAD_MANAGEMENT_MEMORY_UTILIZATION" );

AttributeNotFound::AttributeNotFound(const std::string &name)
{
    desc = "Attribute not found: " + name;
}

const char* AttributeNotFound::what() const
{
    return desc.c_str();
}

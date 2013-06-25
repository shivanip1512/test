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
const PointAttribute PointAttribute::CloseOpCount = PointAttribute(PointAttribute::CloseOpCountAttribute, "Close Op Count");
const PointAttribute PointAttribute::OpenOpCount = PointAttribute(PointAttribute::OpenOpCountAttribute, "Open Op Count");

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
    NameToAttributeMap::const_iterator searchResult = _lookup.find( name );

    return ( searchResult != _lookup.end() )
                ?   *searchResult->second
                :   Unknown;
}


bool Attribute::operator==( const Attribute & rhs ) const
{
    return _value == rhs._value;
}


bool Attribute::operator<( const Attribute & rhs ) const
{
    return _value < rhs._value;
}


const Attribute Attribute::Unknown                                          = Attribute( "UNKNOWN"                                          );
const Attribute Attribute::BlinkCount                                       = Attribute( "BLINK_COUNT"                                      );
const Attribute Attribute::ClockError                                       = Attribute( "CLOCK_ERROR"                                      );
const Attribute Attribute::ConfigurationError                               = Attribute( "CONFIGURATION_ERROR"                              );
const Attribute Attribute::ControlPoint                                     = Attribute( "CONTROL_POINT"                                    );
const Attribute Attribute::ControlStatus                                    = Attribute( "CONTROL_STATUS"                                   );
const Attribute Attribute::CrystalOscillatorError                           = Attribute( "CRYSTAL_OSCILLATOR_ERROR"                         );
const Attribute Attribute::Current                                          = Attribute( "CURRENT"                                          );
const Attribute Attribute::CurrentLoss                                      = Attribute( "CURRENT_LOSS"                                     );
const Attribute Attribute::CurrentPhaseA                                    = Attribute( "CURRENT_PHASE_A"                                  );
const Attribute Attribute::CurrentPhaseB                                    = Attribute( "CURRENT_PHASE_B"                                  );
const Attribute Attribute::CurrentPhaseC                                    = Attribute( "CURRENT_PHASE_C"                                  );
const Attribute Attribute::CurrentWithoutVoltageFlag                        = Attribute( "CURRENT_WITHOUT_VOLTAGE_FLAG"                     );
const Attribute Attribute::DeliveredKwLoadProfile                           = Attribute( "DELIVERED_KW_LOAD_PROFILE"                        );
const Attribute Attribute::DeliveredKwh                                     = Attribute( "DELIVERED_KWH"                                    );
const Attribute Attribute::DeliveredKwhPerInterval                          = Attribute( "DELIVERED_KWH_PER_INTERVAL"                       );
const Attribute Attribute::Demand                                           = Attribute( "DEMAND"                                           );
const Attribute Attribute::DemandOverload                                   = Attribute( "DEMAND_OVERLOAD"                                  );
const Attribute Attribute::DemandReadsAndReset                              = Attribute( "DEMAND_READS_AND_RESET"                           );
const Attribute Attribute::DemandThresholdExceededWarning                   = Attribute( "DEMAND_THRESHOLD_EXCEEDED_WARNING"                );
const Attribute Attribute::DisconnectStatus                                 = Attribute( "DISCONNECT_STATUS"                                );
const Attribute Attribute::DisplayLockedByWarning                           = Attribute( "DISPLAY_LOCKED_BY_WARNING"                        );
const Attribute Attribute::EepromAccessError                                = Attribute( "EEPROM_ACCESS_ERROR"                              );
const Attribute Attribute::EncryptionKeyTableCrcError                       = Attribute( "ENCRYPTION_KEY_TABLE_CRC_ERROR"                   );
const Attribute Attribute::EndOfCalendarWarning                             = Attribute( "END_OF_CALENDAR_WARNING"                          );
const Attribute Attribute::EnergyAccumulatedWhileInStandbyMode              = Attribute( "ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE"         );
const Attribute Attribute::EnergyGenerated                                  = Attribute( "ENERGY_GENERATED"                                 );
const Attribute Attribute::FaultStatus                                      = Attribute( "FAULT_STATUS"                                     );
const Attribute Attribute::ForwardCapacitiveKvarhPerInterval                = Attribute( "FORWARD_CAPACITIVE_KVARH_PER_INTERVAL"            );
const Attribute Attribute::ForwardInductiveKvarh                            = Attribute( "FORWARD_INDUCTIVE_KVARH"                          );
const Attribute Attribute::ForwardInductiveKvarhPerInterval                 = Attribute( "FORWARD_INDUCTIVE_KVARH_PER_INTERVAL"             );
const Attribute Attribute::GeneralAlarmFlag                                 = Attribute( "GENERAL_ALARM_FLAG"                               );
const Attribute Attribute::GeneratedKwhPerInterval                          = Attribute( "GENERATED_KWH_PER_INTERVAL"                       );
const Attribute Attribute::IedDemandResetCount                              = Attribute( "IED_DEMAND_RESET_COUNT"                           );
const Attribute Attribute::ImproperMeterEngineOperationWarning              = Attribute( "IMPROPER_METER_ENGINE_OPERATION_WARNING"          );
const Attribute Attribute::InternalCommunicationError                       = Attribute( "INTERNAL_COMMUNICATION_ERROR"                     );
const Attribute Attribute::InternalErrorFlag                                = Attribute( "INTERNAL_ERROR_FLAG"                              );
const Attribute Attribute::InvalidService                                   = Attribute( "INVALID_SERVICE"                                  );
const Attribute Attribute::Kvar                                             = Attribute( "KVAR"                                             );
const Attribute Attribute::Kvarh                                            = Attribute( "KVARH"                                            );
const Attribute Attribute::LineFrequencyWarning                             = Attribute( "LINE_FREQUENCY_WARNING"                           );
const Attribute Attribute::LmGroupStatus                                    = Attribute( "LM_GROUP_STATUS"                                  );
const Attribute Attribute::LoadProfile                                      = Attribute( "LOAD_PROFILE"                                     );
const Attribute Attribute::LoadSideVoltageDetectedFlag                      = Attribute( "LOAD_SIDE_VOLTAGE_DETECTED_FLAG"                  );
const Attribute Attribute::LoadSideVoltageIsMissing                         = Attribute( "LOAD_SIDE_VOLTAGE_IS_MISSING"                     );
const Attribute Attribute::LossOfAllCurrent                                 = Attribute( "LOSS_OF_ALL_CURRENT"                              );
const Attribute Attribute::LossOfPhaseACurrent                              = Attribute( "LOSS_OF_PHASE_A_CURRENT"                          );
const Attribute Attribute::LossOfPhaseCCurrent                              = Attribute( "LOSS_OF_PHASE_C_CURRENT"                          );
const Attribute Attribute::LowBatteryWarning                                = Attribute( "LOW_BATTERY_WARNING"                              );
const Attribute Attribute::LowLossPotential                                 = Attribute( "LOW_LOSS_POTENTIAL"                               );
const Attribute Attribute::MaximumVoltage                                   = Attribute( "MAXIMUM_VOLTAGE"                                  );
const Attribute Attribute::MeasurementError                                 = Attribute( "MEASUREMENT_ERROR"                                );
const Attribute Attribute::MeterBoxCoverRemovalFlag                         = Attribute( "METER_BOX_COVER_REMOVAL_FLAG"                     );
const Attribute Attribute::MinimumVoltage                                   = Attribute( "MINIMUM_VOLTAGE"                                  );
const Attribute Attribute::NetKwLoadProfile                                 = Attribute( "NET_KW_LOAD_PROFILE"                              );
const Attribute Attribute::NetKwh                                           = Attribute( "NET_KWH"                                          );
const Attribute Attribute::NetKwhPerInterval                                = Attribute( "NET_KWH_PER_INTERVAL"                             );
const Attribute Attribute::NeutralCurrent                                   = Attribute( "NEUTRAL_CURRENT"                                  );
const Attribute Attribute::NonVolatileMemFailure                            = Attribute( "NON_VOLATILE_MEM_FAILURE"                         );
const Attribute Attribute::OutOfVoltageFlag                                 = Attribute( "OUT_OF_VOLTAGE_FLAG"                              );
const Attribute Attribute::OutageLog                                        = Attribute( "OUTAGE_LOG"                                       );
const Attribute Attribute::OutageStatus                                     = Attribute( "OUTAGE_STATUS"                                    );
const Attribute Attribute::OverVoltage                                      = Attribute( "OVER_VOLTAGE"                                     );
const Attribute Attribute::OverVoltageMeasured                              = Attribute( "OVER_VOLTAGE_MEASURED"                            );
const Attribute Attribute::OverVoltageThreshold                             = Attribute( "OVER_VOLTAGE_THRESHOLD"                           );
const Attribute Attribute::PasswordTableCrcError                            = Attribute( "PASSWORD_TABLE_CRC_ERROR"                         );
const Attribute Attribute::PeakDemand                                       = Attribute( "PEAK_DEMAND"                                      );
const Attribute Attribute::PeakKvar                                         = Attribute( "PEAK_KVAR"                                        );
const Attribute Attribute::Phase                                            = Attribute( "PHASE"                                            );
const Attribute Attribute::PotentialIndicatorWarning                        = Attribute( "POTENTIAL_INDICATOR_WARNING"                      );
const Attribute Attribute::PowerFactor                                      = Attribute( "POWER_FACTOR"                                     );
const Attribute Attribute::PowerFactorPhaseA                                = Attribute( "POWER_FACTOR_PHASE_A"                             );
const Attribute Attribute::PowerFactorPhaseB                                = Attribute( "POWER_FACTOR_PHASE_B"                             );
const Attribute Attribute::PowerFactorPhaseC                                = Attribute( "POWER_FACTOR_PHASE_C"                             );
const Attribute Attribute::PowerFailDataSaveError                           = Attribute( "POWER_FAIL_DATA_SAVE_ERROR"                       );
const Attribute Attribute::PowerFailFlag                                    = Attribute( "POWER_FAIL_FLAG"                                  );
const Attribute Attribute::PqmTestFailureWarning                            = Attribute( "PQM_TEST_FAILURE_WARNING"                         );
const Attribute Attribute::ProfileChannel2                                  = Attribute( "PROFILE_CHANNEL_2"                                );
const Attribute Attribute::ProfileChannel3                                  = Attribute( "PROFILE_CHANNEL_3"                                );
const Attribute Attribute::RamError                                         = Attribute( "RAM_ERROR"                                        );
const Attribute Attribute::ReceivedKwLoadProfile                            = Attribute( "RECEIVED_KW_LOAD_PROFILE"                         );
const Attribute Attribute::ReceivedKwh                                      = Attribute( "RECEIVED_KWH"                                     );
const Attribute Attribute::ReceivedKwhPerInterval                           = Attribute( "RECEIVED_KWH_PER_INTERVAL"                        );
const Attribute Attribute::RecordingInterval                                = Attribute( "RECORDING_INTERVAL"                               );
const Attribute Attribute::Relay1LoadSize                                   = Attribute( "RELAY_1_LOAD_SIZE"                                );
const Attribute Attribute::Relay1RemainingControl                           = Attribute( "RELAY_1_REMAINING_CONTROL"                        );
const Attribute Attribute::Relay1RunTimeDataLog                             = Attribute( "RELAY_1_RUN_TIME_DATA_LOG"                        );
const Attribute Attribute::Relay1ShedTimeDataLog                            = Attribute( "RELAY_1_SHED_TIME_DATA_LOG"                       );
const Attribute Attribute::Relay2LoadSize                                   = Attribute( "RELAY_2_LOAD_SIZE"                                );
const Attribute Attribute::Relay2RemainingControl                           = Attribute( "RELAY_2_REMAINING_CONTROL"                        );
const Attribute Attribute::Relay2RunTimeDataLog                             = Attribute( "RELAY_2_RUN_TIME_DATA_LOG"                        );
const Attribute Attribute::Relay2ShedTimeDataLog                            = Attribute( "RELAY_2_SHED_TIME_DATA_LOG"                       );
const Attribute Attribute::Relay3LoadSize                                   = Attribute( "RELAY_3_LOAD_SIZE"                                );
const Attribute Attribute::Relay3RemainingControl                           = Attribute( "RELAY_3_REMAINING_CONTROL"                        );
const Attribute Attribute::Relay3RunTimeDataLog                             = Attribute( "RELAY_3_RUN_TIME_DATA_LOG"                        );
const Attribute Attribute::Relay3ShedTimeDataLog                            = Attribute( "RELAY_3_SHED_TIME_DATA_LOG"                       );
const Attribute Attribute::Relay4RemainingControl                           = Attribute( "RELAY_4_REMAINING_CONTROL"                        );
const Attribute Attribute::Relay4RunTimeDataLog                             = Attribute( "RELAY_4_RUN_TIME_DATA_LOG"                        );
const Attribute Attribute::Relay4ShedTimeDataLog                            = Attribute( "RELAY_4_SHED_TIME_DATA_LOG"                       );
const Attribute Attribute::ReportingInterval                                = Attribute( "REPORTING_INTERVAL"                               );
const Attribute Attribute::ReverseCapacitiveKvarhPerInterval                = Attribute( "REVERSE_CAPACITIVE_KVARH_PER_INTERVAL"            );
const Attribute Attribute::ReverseInductiveKvarh                            = Attribute( "REVERSE_INDUCTIVE_KVARH"                          );
const Attribute Attribute::ReverseInductiveKvarhPerInterval                 = Attribute( "REVERSE_INDUCTIVE_KVARH_PER_INTERVAL"             );
const Attribute Attribute::ReversePowerFlag                                 = Attribute( "REVERSE_POWER_FLAG"                               );
const Attribute Attribute::ReversedAggregate                                = Attribute( "REVERSED_AGGREGATE"                               );
const Attribute Attribute::ReversedPhaseA                                   = Attribute( "REVERSED_PHASE_A"                                 );
const Attribute Attribute::ReversedPhaseC                                   = Attribute( "REVERSED_PHASE_C"                                 );
const Attribute Attribute::RfDemandResetStatus                              = Attribute( "RF_DEMAND_RESET_STATUS"                           );
const Attribute Attribute::RfnBlinkCount                                    = Attribute( "RFN_BLINK_COUNT"                                  );
const Attribute Attribute::RfnBlinkRestoreCount                             = Attribute( "RFN_BLINK_RESTORE_COUNT"                          );
const Attribute Attribute::RfnOutageCount                                   = Attribute( "RFN_OUTAGE_COUNT"                                 );
const Attribute Attribute::RfnOutageRestoreCount                            = Attribute( "RFN_OUTAGE_RESTORE_COUNT"                         );
const Attribute Attribute::RomError                                         = Attribute( "ROM_ERROR"                                        );
const Attribute Attribute::SecurityConfigurationError                       = Attribute( "SECURITY_CONFIGURATION_ERROR"                     );
const Attribute Attribute::SelfCheckError                                   = Attribute( "SELF_CHECK_ERROR"                                 );
const Attribute Attribute::ServiceCurrentTestFailureWarning                 = Attribute( "SERVICE_CURRENT_TEST_FAILURE_WARNING"             );
const Attribute Attribute::ServiceDisconnectSwitchError                     = Attribute( "SERVICE_DISCONNECT_SWITCH_ERROR"                  );
const Attribute Attribute::ServiceDisconnectSwitchOpen                      = Attribute( "SERVICE_DISCONNECT_SWITCH_OPEN"                   );
const Attribute Attribute::ServiceDisconnectSwitchSensorError               = Attribute( "SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR"           );
const Attribute Attribute::ServiceStatus                                    = Attribute( "SERVICE_STATUS"                                   );
const Attribute Attribute::StuckSwitch                                      = Attribute( "STUCK_SWITCH"                                     );
const Attribute Attribute::SumKvaLoadProfile                                = Attribute( "SUM_KVA_LOAD_PROFILE"                             );
const Attribute Attribute::SumKvah                                          = Attribute( "SUM_KVAH"                                         );
const Attribute Attribute::SumKvahPerInterval                               = Attribute( "SUM_KVAH_PER_INTERVAL"                            );
const Attribute Attribute::SumKvarLoadProfile                               = Attribute( "SUM_KVAR_LOAD_PROFILE"                            );
const Attribute Attribute::SumKvarh                                         = Attribute( "SUM_KVARH"                                        );
const Attribute Attribute::SumKvarhPerInterval                              = Attribute( "SUM_KVARH_PER_INTERVAL"                           );
const Attribute Attribute::SumKwLoadProfile                                 = Attribute( "SUM_KW_LOAD_PROFILE"                              );
const Attribute Attribute::SumKwh                                           = Attribute( "SUM_KWH"                                          );
const Attribute Attribute::SumKwhPerInterval                                = Attribute( "SUM_KWH_PER_INTERVAL"                             );
const Attribute Attribute::TableCrcError                                    = Attribute( "TABLE_CRC_ERROR"                                  );
const Attribute Attribute::TamperFlag                                       = Attribute( "TAMPER_FLAG"                                      );
const Attribute Attribute::TemporaryOutOfService                            = Attribute( "TEMPORARY_OUT_OF_SERVICE"                         );
const Attribute Attribute::TimeAdjustment                                   = Attribute( "TIME_ADJUSTMENT"                                  );
const Attribute Attribute::TotalLufCount                                    = Attribute( "TOTAL_LUF_COUNT"                                  );
const Attribute Attribute::TotalLuvCount                                    = Attribute( "TOTAL_LUV_COUNT"                                  );
const Attribute Attribute::TouRateAEnergyGenerated                          = Attribute( "TOU_RATE_A_ENERGY_GENERATED"                      );
const Attribute Attribute::TouRateAPeakDemand                               = Attribute( "TOU_RATE_A_PEAK_DEMAND"                           );
const Attribute Attribute::TouRateAUsage                                    = Attribute( "TOU_RATE_A_USAGE"                                 );
const Attribute Attribute::TouRateBEnergyGenerated                          = Attribute( "TOU_RATE_B_ENERGY_GENERATED"                      );
const Attribute Attribute::TouRateBPeakDemand                               = Attribute( "TOU_RATE_B_PEAK_DEMAND"                           );
const Attribute Attribute::TouRateBUsage                                    = Attribute( "TOU_RATE_B_USAGE"                                 );
const Attribute Attribute::TouRateCEnergyGenerated                          = Attribute( "TOU_RATE_C_ENERGY_GENERATED"                      );
const Attribute Attribute::TouRateCPeakDemand                               = Attribute( "TOU_RATE_C_PEAK_DEMAND"                           );
const Attribute Attribute::TouRateCUsage                                    = Attribute( "TOU_RATE_C_USAGE"                                 );
const Attribute Attribute::TouRateDEnergyGenerated                          = Attribute( "TOU_RATE_D_ENERGY_GENERATED"                      );
const Attribute Attribute::TouRateDPeakDemand                               = Attribute( "TOU_RATE_D_PEAK_DEMAND"                           );
const Attribute Attribute::TouRateDUsage                                    = Attribute( "TOU_RATE_D_USAGE"                                 );
const Attribute Attribute::Unconfigured                                     = Attribute( "UNCONFIGURED"                                     );
const Attribute Attribute::UnderVoltage                                     = Attribute( "UNDER_VOLTAGE"                                    );
const Attribute Attribute::UnderVoltageMeasured                             = Attribute( "UNDER_VOLTAGE_MEASURED"                           );
const Attribute Attribute::UnderVoltageThreshold                            = Attribute( "UNDER_VOLTAGE_THRESHOLD"                          );
const Attribute Attribute::Unprogrammed                                     = Attribute( "UNPROGRAMMED"                                     );
const Attribute Attribute::Usage                                            = Attribute( "USAGE"                                            );
const Attribute Attribute::UsagePerInterval                                 = Attribute( "USAGE_PER_INTERVAL"                               );
const Attribute Attribute::UsageWater                                       = Attribute( "USAGE_WATER"                                      );
const Attribute Attribute::UserProgrammableTemperatureThresholdExceeded     = Attribute( "USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED" );
const Attribute Attribute::Voltage                                          = Attribute( "VOLTAGE"                                          );
const Attribute Attribute::VoltageAlerts                                    = Attribute( "VOLTAGE_ALERTS"                                   );
const Attribute Attribute::VoltageLoss                                      = Attribute( "VOLTAGE_LOSS"                                     );
const Attribute Attribute::VoltageOutOfLimitsFlag                           = Attribute( "VOLTAGE_OUT_OF_LIMITS_FLAG"                       );
const Attribute Attribute::VoltagePhaseA                                    = Attribute( "VOLTAGE_PHASE_A"                                  );
const Attribute Attribute::VoltagePhaseAOut                                 = Attribute( "VOLTAGE_PHASE_A_OUT"                              );
const Attribute Attribute::VoltagePhaseB                                    = Attribute( "VOLTAGE_PHASE_B"                                  );
const Attribute Attribute::VoltagePhaseBOut                                 = Attribute( "VOLTAGE_PHASE_B_OUT"                              );
const Attribute Attribute::VoltagePhaseC                                    = Attribute( "VOLTAGE_PHASE_C"                                  );
const Attribute Attribute::VoltagePhaseCOut                                 = Attribute( "VOLTAGE_PHASE_C_OUT"                              );
const Attribute Attribute::VoltagePhaseError                                = Attribute( "VOLTAGE_PHASE_ERROR"                              );
const Attribute Attribute::VoltageProfile                                   = Attribute( "VOLTAGE_PROFILE"                                  );
const Attribute Attribute::WaterUsagePerInterval                            = Attribute( "WATER_USAGE_PER_INTERVAL"                         );
const Attribute Attribute::WattHourPulseFailure                             = Attribute( "WATT_HOUR_PULSE_FAILURE"                          );
const Attribute Attribute::ZeroUsageFlag                                    = Attribute( "ZERO_USAGE_FLAG"                                  );
const Attribute Attribute::ZigbeeLinkStatus                                 = Attribute( "ZIGBEE_LINK_STATUS"                               );


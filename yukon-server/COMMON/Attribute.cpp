#include "precompiled.h"

#include "Attribute.h"

#include "std_helper.h"

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

#define INIT_ATTRIBUTE(x,y) const Attribute Attribute::x { y };

//  this ordering should match BuiltInAttribute.java
INIT_ATTRIBUTE( Unknown,                                            "UNKNOWN" );
INIT_ATTRIBUTE( BlinkCount,                                         "BLINK_COUNT" );
INIT_ATTRIBUTE( CommunicationStatus,                                "COMM_STATUS" );
INIT_ATTRIBUTE( ControlStatus,                                      "CONTROL_STATUS" );
INIT_ATTRIBUTE( CurrentNeutral,                                     "NEUTRAL_CURRENT" );
INIT_ATTRIBUTE( Current,                                            "CURRENT" );
INIT_ATTRIBUTE( CurrentPhaseA,                                      "CURRENT_PHASE_A" );
INIT_ATTRIBUTE( CurrentPhaseB,                                      "CURRENT_PHASE_B" );
INIT_ATTRIBUTE( CurrentPhaseC,                                      "CURRENT_PHASE_C" );
INIT_ATTRIBUTE( CurrentAngle,                                       "CURRENT_ANGLE" );
INIT_ATTRIBUTE( CurrentAnglePhaseA,                                 "CURRENT_ANGLE_PHASE_A" );
INIT_ATTRIBUTE( CurrentAnglePhaseB,                                 "CURRENT_ANGLE_PHASE_B" );
INIT_ATTRIBUTE( CurrentAnglePhaseC,                                 "CURRENT_ANGLE_PHASE_C" );
INIT_ATTRIBUTE( CurrentWithoutVoltage,                              "CURRENT_WITHOUT_VOLTAGE_FLAG" );
INIT_ATTRIBUTE( Demand,                                             "DEMAND" );
INIT_ATTRIBUTE( DeliveredDemand,                                    "DELIVERED_DEMAND" );
INIT_ATTRIBUTE( ReceivedDemand,                                     "RECEIVED_DEMAND" );
INIT_ATTRIBUTE( DemandPeakkVACoin,                                  "DEMAND_PEAK_KVA_COIN" );
INIT_ATTRIBUTE( InstantaneouskW,                                    "INSTANTANEOUS_KW" );
INIT_ATTRIBUTE( SumkW,                                              "SUM_KW" );
INIT_ATTRIBUTE( NetkW,                                              "NET_KW" );
INIT_ATTRIBUTE( DisconnectStatus,                                   "DISCONNECT_STATUS" );
INIT_ATTRIBUTE( FaultStatus,                                        "FAULT_STATUS" );
INIT_ATTRIBUTE( ForwardInductivekVArh,                              "FORWARD_INDUCTIVE_KVARH" );
INIT_ATTRIBUTE( GeneralAlarmFlag,                                   "GENERAL_ALARM_FLAG" );
INIT_ATTRIBUTE( IEDDemandResetCount,                                "IED_DEMAND_RESET_COUNT" );
INIT_ATTRIBUTE( kVAh,                                               "KVAH" );
INIT_ATTRIBUTE( kVAhRateA,                                          "KVAH_RATE_A" );
INIT_ATTRIBUTE( kVAhRateB,                                          "KVAH_RATE_B" );
INIT_ATTRIBUTE( kVAhRateC,                                          "KVAH_RATE_C" );
INIT_ATTRIBUTE( kVAhRateD,                                          "KVAH_RATE_D" );
INIT_ATTRIBUTE( NetkVAh,                                            "NET_KVAH" );
INIT_ATTRIBUTE( NetkVAhRateA,                                       "NET_KVAH_RATE_A" );
INIT_ATTRIBUTE( NetkVAhRateB,                                       "NET_KVAH_RATE_B" );
INIT_ATTRIBUTE( NetkVAhRateC,                                       "NET_KVAH_RATE_C" );
INIT_ATTRIBUTE( NetkVAhRateD,                                       "NET_KVAH_RATE_D" );
INIT_ATTRIBUTE( ReceivedkVAhRateA,                                  "RECEIVED_KVAH_RATE_A" );
INIT_ATTRIBUTE( ReceivedkVAhRateB,                                  "RECEIVED_KVAH_RATE_B" );
INIT_ATTRIBUTE( ReceivedkVAhRateC,                                  "RECEIVED_KVAH_RATE_C" );
INIT_ATTRIBUTE( ReceivedkVAhRateD,                                  "RECEIVED_KVAH_RATE_D" );
INIT_ATTRIBUTE( SumkVAhRateA,                                       "SUM_KVAH_RATE_A" );
INIT_ATTRIBUTE( SumkVAhRateB,                                       "SUM_KVAH_RATE_B" );
INIT_ATTRIBUTE( SumkVAhRateC,                                       "SUM_KVAH_RATE_C" );
INIT_ATTRIBUTE( SumkVAhRateD,                                       "SUM_KVAH_RATE_D" );
INIT_ATTRIBUTE( kVAr,                                               "KVAR" );
INIT_ATTRIBUTE( DeliveredkVAr,                                      "DELIVERED_KVAR" );
INIT_ATTRIBUTE( ReceivedkVAr,                                       "RECEIVED_KVAR" );
INIT_ATTRIBUTE( kVArh,                                              "KVARH" );
INIT_ATTRIBUTE( kVArhRateA,                                         "KVARH_RATE_A" );
INIT_ATTRIBUTE( kVArhRateB,                                         "KVARH_RATE_B" );
INIT_ATTRIBUTE( kVArhRateC,                                         "KVARH_RATE_C" );
INIT_ATTRIBUTE( kVArhRateD,                                         "KVARH_RATE_D" );
INIT_ATTRIBUTE( LMGroupStatus,                                      "LM_GROUP_STATUS" );
INIT_ATTRIBUTE( LoadProfile,                                        "LOAD_PROFILE" );
INIT_ATTRIBUTE( LoadSideVoltageDetected,                            "LOAD_SIDE_VOLTAGE_DETECTED_WHILE_DISCONNECTED" );
INIT_ATTRIBUTE( MeterBoxCoverRemoval,                               "METER_BOX_COVER_REMOVAL_FLAG" );
INIT_ATTRIBUTE( MaximumVoltage,                                     "MAXIMUM_VOLTAGE" );
INIT_ATTRIBUTE( MaximumVoltageFrozen,                               "MAXIMUM_VOLTAGE_FROZEN" );
INIT_ATTRIBUTE( MinimumVoltage,                                     "MINIMUM_VOLTAGE" );
INIT_ATTRIBUTE( MinimumVoltageFrozen,                               "MINIMUM_VOLTAGE_FROZEN" );
INIT_ATTRIBUTE( OutageLog,                                          "OUTAGE_LOG" );
INIT_ATTRIBUTE( OutageStatus,                                       "OUTAGE_STATUS" );
INIT_ATTRIBUTE( OutofVoltage,                                       "OUT_OF_VOLTAGE_FLAG" );
INIT_ATTRIBUTE( VoltageOutofLimits,                                 "VOLTAGE_OUT_OF_LIMITS_FLAG" );
INIT_ATTRIBUTE( OverVoltage,                                        "OVER_VOLTAGE" );
INIT_ATTRIBUTE( OverVoltageThreshold,                               "OVER_VOLTAGE_THRESHOLD" );
INIT_ATTRIBUTE( PeakDemand,                                         "PEAK_DEMAND" );
INIT_ATTRIBUTE( PeakDemandDaily,                                    "PEAK_DEMAND_DAILY" );
INIT_ATTRIBUTE( NetPeakDemand,                                      "NET_PEAK_DEMAND" );
INIT_ATTRIBUTE( SumPeakDemand,                                      "SUM_PEAK_DEMAND" );
INIT_ATTRIBUTE( PeakDemandFrozen,                                   "PEAK_DEMAND_FROZEN" );
INIT_ATTRIBUTE( PeakDemandRateA,                                    "PEAK_DEMAND_RATE_A" );
INIT_ATTRIBUTE( PeakDemandFrozenRateA,                              "PEAK_DEMAND_FROZEN_RATE_A" );
INIT_ATTRIBUTE( PeakDemandRateB,                                    "PEAK_DEMAND_RATE_B" );
INIT_ATTRIBUTE( PeakDemandFrozenRateB,                              "PEAK_DEMAND_FROZEN_RATE_B" );
INIT_ATTRIBUTE( PeakDemandRateC,                                    "PEAK_DEMAND_RATE_C" );
INIT_ATTRIBUTE( PeakDemandFrozenRateC,                              "PEAK_DEMAND_FROZEN_RATE_C" );
INIT_ATTRIBUTE( PeakDemandRateD,                                    "PEAK_DEMAND_RATE_D" );
INIT_ATTRIBUTE( PeakDemandFrozenRateD,                              "PEAK_DEMAND_FROZEN_RATE_D" );
INIT_ATTRIBUTE( PeakDemandRateE,                                    "PEAK_DEMAND_RATE_E" );
INIT_ATTRIBUTE( NetPeakDemandRateA,                                 "NET_PEAK_DEMAND_RATE_A" );
INIT_ATTRIBUTE( NetPeakDemandRateB,                                 "NET_PEAK_DEMAND_RATE_B" );
INIT_ATTRIBUTE( NetPeakDemandRateC,                                 "NET_PEAK_DEMAND_RATE_C" );
INIT_ATTRIBUTE( NetPeakDemandRateD,                                 "NET_PEAK_DEMAND_RATE_D" );
INIT_ATTRIBUTE( SumPeakDemandRateA,                                 "SUM_PEAK_DEMAND_RATE_A" );
INIT_ATTRIBUTE( SumPeakDemandRateB,                                 "SUM_PEAK_DEMAND_RATE_B" );
INIT_ATTRIBUTE( SumPeakDemandRateC,                                 "SUM_PEAK_DEMAND_RATE_C" );
INIT_ATTRIBUTE( SumPeakDemandRateD,                                 "SUM_PEAK_DEMAND_RATE_D" );
INIT_ATTRIBUTE( ReceivedPeakDemand,                                 "RECEIVED_PEAK_DEMAND" );
INIT_ATTRIBUTE( ReceivedPeakDemandRateA,                            "RECEIVED_PEAK_DEMAND_RATE_A" );
INIT_ATTRIBUTE( ReceivedPeakDemandRateB,                            "RECEIVED_PEAK_DEMAND_RATE_B" );
INIT_ATTRIBUTE( ReceivedPeakDemandRateC,                            "RECEIVED_PEAK_DEMAND_RATE_C" );
INIT_ATTRIBUTE( ReceivedPeakDemandRateD,                            "RECEIVED_PEAK_DEMAND_RATE_D" );
INIT_ATTRIBUTE( kVA,                                                "KVA" );
INIT_ATTRIBUTE( DeliveredkVA,                                       "DELIVERED_KVA" );
INIT_ATTRIBUTE( ReceivedkVA,                                        "RECEIVED_KVA" );
INIT_ATTRIBUTE( SumkVA,                                             "SUM_KVA" );
INIT_ATTRIBUTE( NetkVA,                                             "NET_KVA" );
INIT_ATTRIBUTE( KvaPeakDemandCoin,                                  "KVA_PEAK_DEMAND_COIN" );
INIT_ATTRIBUTE( PeakkVA,                                            "PEAK_KVA" );
INIT_ATTRIBUTE( PeakkVARateA,                                       "PEAK_KVA_RATE_A" );
INIT_ATTRIBUTE( PeakkVARateB,                                       "PEAK_KVA_RATE_B" );
INIT_ATTRIBUTE( PeakkVARateC,                                       "PEAK_KVA_RATE_C" );
INIT_ATTRIBUTE( PeakkVARateD,                                       "PEAK_KVA_RATE_D" );
INIT_ATTRIBUTE( PeakkVACoincidental,                                "PEAK_KVA_COIN" );
INIT_ATTRIBUTE( ReceivedPeakkVA,                                    "RECEIVED_PEAK_KVA" );
INIT_ATTRIBUTE( ReceivedPeakkVARateA,                               "RECEIVED_PEAK_KVA_RATE_A" );
INIT_ATTRIBUTE( ReceivedPeakkVARateB,                               "RECEIVED_PEAK_KVA_RATE_B" );
INIT_ATTRIBUTE( ReceivedPeakkVARateC,                               "RECEIVED_PEAK_KVA_RATE_C" );
INIT_ATTRIBUTE( ReceivedPeakkVARateD,                               "RECEIVED_PEAK_KVA_RATE_D" );
INIT_ATTRIBUTE( SumPeakkVA,                                         "SUM_PEAK_KVA" );
INIT_ATTRIBUTE( SumPeakkVARateA,                                    "SUM_PEAK_KVA_RATE_A" );
INIT_ATTRIBUTE( SumPeakkVARateB,                                    "SUM_PEAK_KVA_RATE_B" );
INIT_ATTRIBUTE( SumPeakkVARateC,                                    "SUM_PEAK_KVA_RATE_C" );
INIT_ATTRIBUTE( SumPeakkVARateD,                                    "SUM_PEAK_KVA_RATE_D" );
INIT_ATTRIBUTE( kVAQ12,                                             "KVA_Q12" );
INIT_ATTRIBUTE( kVAQ34,                                             "KVA_Q34" );
INIT_ATTRIBUTE( kVAQ13,                                             "KVA_Q13" );
INIT_ATTRIBUTE( kVAQ24,                                             "KVA_Q24" );
INIT_ATTRIBUTE( SumkVAr,                                            "SUM_KVAR" );
INIT_ATTRIBUTE( NetkVAr,                                            "NET_KVAR" );
INIT_ATTRIBUTE( PeakkVAr,                                           "PEAK_KVAR" );
INIT_ATTRIBUTE( PeakkVArCoincidental,                               "PEAK_KVAR_COIN" );
INIT_ATTRIBUTE( PeakkVArRateA,                                      "PEAK_KVAR_RATE_A" );
INIT_ATTRIBUTE( PeakkVArRateB,                                      "PEAK_KVAR_RATE_B" );
INIT_ATTRIBUTE( PeakkVArRateC,                                      "PEAK_KVAR_RATE_C" );
INIT_ATTRIBUTE( PeakkVArRateD,                                      "PEAK_KVAR_RATE_D" );
INIT_ATTRIBUTE( PeakkVArQ14,                                        "PEAK_KVAR_Q14" );
INIT_ATTRIBUTE( PeakkVArQ23,                                        "PEAK_KVAR_Q23" );
INIT_ATTRIBUTE( CoincidentPeakkVArQ23,                              "COIN_PEAK_KVAR_Q23" );
INIT_ATTRIBUTE( CoincidentCumulativePeakkVArQ14,                    "COIN_CUMU_PEAK_KVAR_Q14" );
INIT_ATTRIBUTE( CoincidentCumulativePeakkVArQ23,                    "COIN_CUMU_PEAK_KVAR_Q23" );
INIT_ATTRIBUTE( ReceivedPeakkVAr,                                   "RECEIVED_PEAK_KVAR" );
INIT_ATTRIBUTE( ReceivedPeakkVArRateA,                              "RECEIVED_PEAK_KVAR_RATE_A" );
INIT_ATTRIBUTE( ReceivedPeakkVArRateB,                              "RECEIVED_PEAK_KVAR_RATE_B" );
INIT_ATTRIBUTE( ReceivedPeakkVArRateC,                              "RECEIVED_PEAK_KVAR_RATE_C" );
INIT_ATTRIBUTE( ReceivedPeakkVArRateD,                              "RECEIVED_PEAK_KVAR_RATE_D" );
INIT_ATTRIBUTE( ReceivedCoincidentPeakkVAr,                         "RECEIVED_COIN_PEAK_KVAR" );
INIT_ATTRIBUTE( SumPeakkVAr,                                        "SUM_PEAK_KVAR" );
INIT_ATTRIBUTE( SumPeakkVArRateA,                                   "SUM_PEAK_KVAR_RATE_A" );
INIT_ATTRIBUTE( SumPeakkVArRateB,                                   "SUM_PEAK_KVAR_RATE_B" );
INIT_ATTRIBUTE( SumPeakkVArRateC,                                   "SUM_PEAK_KVAR_RATE_C" );
INIT_ATTRIBUTE( SumPeakkVArRateD,                                   "SUM_PEAK_KVAR_RATE_D" );
INIT_ATTRIBUTE( kVArQ13,                                            "KVAR_Q13" );
INIT_ATTRIBUTE( kVArQ24,                                            "KVAR_Q24" );
INIT_ATTRIBUTE( kVArQ14,                                            "KVAR_Q14" );
INIT_ATTRIBUTE( kVArQ23,                                            "KVAR_Q23" );
INIT_ATTRIBUTE( PeakkVArh,                                          "PEAK_KVARH" );
INIT_ATTRIBUTE( PeakkVArhCoincidental,                              "PEAK_KVARH_COIN" );
INIT_ATTRIBUTE( Phase,                                              "PHASE" );
INIT_ATTRIBUTE( PowerFactorAvgDelivered,                            "AVERAGE_DELIVERED_POWER_FACTOR" );
INIT_ATTRIBUTE( PowerFactorAvgReceived,                             "AVERAGE_RECEIVED_POWER_FACTOR" );
INIT_ATTRIBUTE( PowerFactor,                                        "POWER_FACTOR" );
INIT_ATTRIBUTE( PowerFactorCoincidental,                            "POWER_FACTOR_COIN" );
INIT_ATTRIBUTE( PowerFactorAvgQ124,                                 "AVERAGE_POWER_FACTOR_Q124" );
INIT_ATTRIBUTE( PowerFactorAvgQ234,                                 "AVERAGE_POWER_FACTOR_Q234" );
INIT_ATTRIBUTE( PowerFactorAvg,                                     "AVERAGE_POWER_FACTOR" );
INIT_ATTRIBUTE( PowerFactorAnglePhaseA,                             "POWER_FACTOR_ANGLE_PHASE_A" );
INIT_ATTRIBUTE( PowerFactorAnglePhaseB,                             "POWER_FACTOR_ANGLE_PHASE_B" );
INIT_ATTRIBUTE( PowerFactorAnglePhaseC,                             "POWER_FACTOR_ANGLE_PHASE_C" );
INIT_ATTRIBUTE( PowerFactorPhaseA,                                  "POWER_FACTOR_PHASE_A" );
INIT_ATTRIBUTE( PowerFactorPhaseB,                                  "POWER_FACTOR_PHASE_B" );
INIT_ATTRIBUTE( PowerFactorPhaseC,                                  "POWER_FACTOR_PHASE_C" );
INIT_ATTRIBUTE( PowerFailFlag,                                      "POWER_FAIL_FLAG" );
INIT_ATTRIBUTE( ProfileChannel2,                                    "PROFILE_CHANNEL_2" );
INIT_ATTRIBUTE( ProfileChannel3,                                    "PROFILE_CHANNEL_3" );
INIT_ATTRIBUTE( RecordingInterval,                                  "RECORDING_INTERVAL" );
INIT_ATTRIBUTE( RelativeHumidity,                                   "HUMIDITY" );
INIT_ATTRIBUTE( Relay1kWLoadSize,                                   "RELAY_1_LOAD_SIZE" );
INIT_ATTRIBUTE( Relay1RemainingControlTime,                         "RELAY_1_REMAINING_CONTROL" );
INIT_ATTRIBUTE( Relay1RunTime,                                      "RELAY_1_RUN_TIME_DATA_LOG" );
INIT_ATTRIBUTE( Relay1RunTime5Min,                                  "RELAY_1_RUN_TIME_DATA_LOG_5_MIN" )
INIT_ATTRIBUTE( Relay1RunTime15Min,                                 "RELAY_1_RUN_TIME_DATA_LOG_15_MIN" )
INIT_ATTRIBUTE( Relay1RunTime30Min,                                 "RELAY_1_RUN_TIME_DATA_LOG_30_MIN" )
INIT_ATTRIBUTE( Relay1ShedStatus,                                   "RELAY_1_SHED_STATUS" );
INIT_ATTRIBUTE( Relay1ShedTime,                                     "RELAY_1_SHED_TIME_DATA_LOG" );
INIT_ATTRIBUTE( Relay1RelayState,                                   "RELAY_1_RELAY_STATE" );
INIT_ATTRIBUTE( Relay2kWLoadSize,                                   "RELAY_2_LOAD_SIZE" );
INIT_ATTRIBUTE( Relay2RemainingControlTime,                         "RELAY_2_REMAINING_CONTROL" );
INIT_ATTRIBUTE( Relay2RunTime,                                      "RELAY_2_RUN_TIME_DATA_LOG" );
INIT_ATTRIBUTE( Relay2RunTime5Min,                                  "RELAY_2_RUN_TIME_DATA_LOG_5_MIN" )
INIT_ATTRIBUTE( Relay2RunTime15Min,                                 "RELAY_2_RUN_TIME_DATA_LOG_15_MIN" )
INIT_ATTRIBUTE( Relay2RunTime30Min,                                 "RELAY_2_RUN_TIME_DATA_LOG_30_MIN" )
INIT_ATTRIBUTE( Relay2ShedStatus,                                   "RELAY_2_SHED_STATUS" );
INIT_ATTRIBUTE( Relay2ShedTime,                                     "RELAY_2_SHED_TIME_DATA_LOG" );
INIT_ATTRIBUTE( Relay2RelayState,                                   "RELAY_2_RELAY_STATE" );
INIT_ATTRIBUTE( Relay3kWLoadSize,                                   "RELAY_3_LOAD_SIZE" );
INIT_ATTRIBUTE( Relay3RemainingControlTime,                         "RELAY_3_REMAINING_CONTROL" );
INIT_ATTRIBUTE( Relay3RunTime,                                      "RELAY_3_RUN_TIME_DATA_LOG" );
INIT_ATTRIBUTE( Relay3RunTime5Min,                                  "RELAY_3_RUN_TIME_DATA_LOG_5_MIN" )
INIT_ATTRIBUTE( Relay3RunTime15Min,                                 "RELAY_3_RUN_TIME_DATA_LOG_15_MIN" )
INIT_ATTRIBUTE( Relay3RunTime30Min,                                 "RELAY_3_RUN_TIME_DATA_LOG_30_MIN" )
INIT_ATTRIBUTE( Relay3ShedStatus,                                   "RELAY_3_SHED_STATUS" );
INIT_ATTRIBUTE( Relay3ShedTime,                                     "RELAY_3_SHED_TIME_DATA_LOG" );
INIT_ATTRIBUTE( Relay3RelayState,                                   "RELAY_3_RELAY_STATE" );
INIT_ATTRIBUTE( Relay4RemainingControlTime,                         "RELAY_4_REMAINING_CONTROL" );
INIT_ATTRIBUTE( Relay4RunTime,                                      "RELAY_4_RUN_TIME_DATA_LOG" );
INIT_ATTRIBUTE( Relay4RunTime5Min,                                  "RELAY_4_RUN_TIME_DATA_LOG_5_MIN" )
INIT_ATTRIBUTE( Relay4RunTime15Min,                                 "RELAY_4_RUN_TIME_DATA_LOG_15_MIN" )
INIT_ATTRIBUTE( Relay4RunTime30Min,                                 "RELAY_4_RUN_TIME_DATA_LOG_30_MIN" )
INIT_ATTRIBUTE( Relay4ShedStatus,                                   "RELAY_4_SHED_STATUS" );
INIT_ATTRIBUTE( Relay4ShedTime,                                     "RELAY_4_SHED_TIME_DATA_LOG" );
INIT_ATTRIBUTE( Relay4RelayState,                                   "RELAY_4_RELAY_STATE" );
INIT_ATTRIBUTE( ReportingInterval,                                  "REPORTING_INTERVAL" );
INIT_ATTRIBUTE( ReverseInductivekVArh,                              "REVERSE_INDUCTIVE_KVARH" );
INIT_ATTRIBUTE( ReversePowerFlag,                                   "REVERSE_POWER_FLAG" );
INIT_ATTRIBUTE( RFDemandResetStatus,                                "RF_DEMAND_RESET_STATUS" );
INIT_ATTRIBUTE( ServiceStatus,                                      "SERVICE_STATUS" );
INIT_ATTRIBUTE( TamperFlag,                                         "TAMPER_FLAG" );
INIT_ATTRIBUTE( Temperature,                                        "TEMPERATURE" );
INIT_ATTRIBUTE( TemperatureofDevice,                                "TEMPERATURE_DEVICE" );
INIT_ATTRIBUTE( TotalLUFEventCount,                                 "TOTAL_LUF_COUNT" );
INIT_ATTRIBUTE( TotalLUVEventCount,                                 "TOTAL_LUV_COUNT" );
INIT_ATTRIBUTE( TotalLOFEventCount,                                 "TOTAL_LOF_COUNT" );
INIT_ATTRIBUTE( TotalLOVEventCount,                                 "TOTAL_LOV_COUNT" );
INIT_ATTRIBUTE( LOFTrigger,                                         "LOF_TRIGGER" );
INIT_ATTRIBUTE( LOFRestore,                                         "LOF_RESTORE" );
INIT_ATTRIBUTE( LOFTriggerTime,                                     "LOF_TRIGGER_TIME" );
INIT_ATTRIBUTE( LOFRestoreTime,                                     "LOF_RESTORE_TIME" );
INIT_ATTRIBUTE( LOFStartRandomTime,                                 "LOF_START_RANDOM_TIME" );
INIT_ATTRIBUTE( LOFEndRandomTime,                                   "LOF_END_RANDOM_TIME" );
INIT_ATTRIBUTE( LOFMinEventDuration,                                "LOF_MIN_EVENT_DURATION" );
INIT_ATTRIBUTE( LOFMaxEventDuration,                                "LOF_MAX_EVENT_DURATION" );
INIT_ATTRIBUTE( LOVTrigger,                                         "LOV_TRIGGER" );
INIT_ATTRIBUTE( LOVRestore,                                         "LOV_RESTORE" );
INIT_ATTRIBUTE( LOVTriggerTime,                                     "LOV_TRIGGER_TIME" );
INIT_ATTRIBUTE( LOVRestoreTime,                                     "LOV_RESTORE_TIME" );
INIT_ATTRIBUTE( LOVStartRandomTime,                                 "LOV_START_RANDOM_TIME" );
INIT_ATTRIBUTE( LOVEndRandomTime,                                   "LOV_END_RANDOM_TIME" );
INIT_ATTRIBUTE( LOVMinEventDuration,                                "LOV_MIN_EVENT_DURATION" );
INIT_ATTRIBUTE( LOVMaxEventDuration,                                "LOV_MAX_EVENT_DURATION" );
INIT_ATTRIBUTE( MinimumEventSeparation,                             "MINIMUM_EVENT_SEPARATION" );
INIT_ATTRIBUTE( PowerQualityResponseEnabled,                        "POWER_QUALITY_RESPONSE_ENABLED" );
INIT_ATTRIBUTE( UnderVoltage,                                       "UNDER_VOLTAGE" );
INIT_ATTRIBUTE( UnderVoltageThreshold,                              "UNDER_VOLTAGE_THRESHOLD" );
INIT_ATTRIBUTE( UsageReading,                                       "USAGE" );
INIT_ATTRIBUTE( UsageFrozen,                                        "USAGE_FROZEN" );
INIT_ATTRIBUTE( UsageRateA,                                         "USAGE_RATE_A" );
INIT_ATTRIBUTE( UsageRateB,                                         "USAGE_RATE_B" );
INIT_ATTRIBUTE( UsageRateC,                                         "USAGE_RATE_C" );
INIT_ATTRIBUTE( UsageRateD,                                         "USAGE_RATE_D" );
INIT_ATTRIBUTE( UsageRateE,                                         "USAGE_RATE_E" );
INIT_ATTRIBUTE( WaterUsageReading,                                  "USAGE_WATER" );
INIT_ATTRIBUTE( GasUsageReading,                                    "USAGE_GAS" );
INIT_ATTRIBUTE( Voltage,                                            "VOLTAGE" );
INIT_ATTRIBUTE( VoltagePhaseA,                                      "VOLTAGE_PHASE_A" );
INIT_ATTRIBUTE( VoltagePhaseB,                                      "VOLTAGE_PHASE_B" );
INIT_ATTRIBUTE( VoltagePhaseC,                                      "VOLTAGE_PHASE_C" );
INIT_ATTRIBUTE( MinimumVoltagePhaseA,                               "MINIMUM_VOLTAGE_PHASE_A" );
INIT_ATTRIBUTE( MinimumVoltagePhaseB,                               "MINIMUM_VOLTAGE_PHASE_B" );
INIT_ATTRIBUTE( MinimumVoltagePhaseC,                               "MINIMUM_VOLTAGE_PHASE_C" );
INIT_ATTRIBUTE( AverageVoltage,                                     "AVERAGE_VOLTAGE" );
INIT_ATTRIBUTE( AverageVoltagePhaseA,                               "AVERAGE_VOLTAGE_PHASE_A" );
INIT_ATTRIBUTE( AverageVoltagePhaseB,                               "AVERAGE_VOLTAGE_PHASE_B" );
INIT_ATTRIBUTE( AverageVoltagePhaseC,                               "AVERAGE_VOLTAGE_PHASE_C" );
INIT_ATTRIBUTE( MaximumVoltagePhaseA,                               "MAXIMUM_VOLTAGE_PHASE_A" );
INIT_ATTRIBUTE( MaximumVoltagePhaseB,                               "MAXIMUM_VOLTAGE_PHASE_B" );
INIT_ATTRIBUTE( MaximumVoltagePhaseC,                               "MAXIMUM_VOLTAGE_PHASE_C" );
INIT_ATTRIBUTE( VoltageProfile,                                     "VOLTAGE_PROFILE" );
INIT_ATTRIBUTE( WattHourPulseFailure,                               "WATT_HOUR_PULSE_FAILURE" );
INIT_ATTRIBUTE( ZeroUsageFlag,                                      "ZERO_USAGE_FLAG" );
INIT_ATTRIBUTE( ZigBeeLinkStatus,                                   "ZIGBEE_LINK_STATUS" );
INIT_ATTRIBUTE( TerminalBlockCoverRemoval,                          "TERMINAL_BLOCK_COVER_REMOVAL_FLAG" );
INIT_ATTRIBUTE( IndoorTemperature,                                  "INDOOR_TEMPERATURE" );
INIT_ATTRIBUTE( OutdoorTemperature,                                 "OUTDOOR_TEMPERATURE" );
INIT_ATTRIBUTE( CoolSetTemperature,                                 "COOL_SET_TEMPERATURE" );
INIT_ATTRIBUTE( HeatSetTemperature,                                 "HEAT_SET_TEMPERATURE" );

INIT_ATTRIBUTE( DeliveredkWh,                                       "DELIVERED_KWH" );
INIT_ATTRIBUTE( ReceivedkWh,                                        "RECEIVED_KWH" );
INIT_ATTRIBUTE( ReceivedkWhFrozen,                                  "RECEIVED_KWH_FROZEN" );
INIT_ATTRIBUTE( ReceivedkWhRateA,                                   "RECEIVED_KWH_RATE_A" );
INIT_ATTRIBUTE( ReceivedkWhRateB,                                   "RECEIVED_KWH_RATE_B" );
INIT_ATTRIBUTE( ReceivedkWhRateC,                                   "RECEIVED_KWH_RATE_C" );
INIT_ATTRIBUTE( ReceivedkWhRateD,                                   "RECEIVED_KWH_RATE_D" );
INIT_ATTRIBUTE( ReceivedkWhRateE,                                   "RECEIVED_KWH_RATE_E" );
INIT_ATTRIBUTE( ReceivedkVAh,                                       "RECEIVED_KVAH" );

INIT_ATTRIBUTE( NetkWh,                                             "NET_KWH" );
INIT_ATTRIBUTE( NetkWhRateA,                                        "NET_KWH_RATE_A" );
INIT_ATTRIBUTE( NetkWhRateB,                                        "NET_KWH_RATE_B" );
INIT_ATTRIBUTE( NetkWhRateC,                                        "NET_KWH_RATE_C" );
INIT_ATTRIBUTE( NetkWhRateD,                                        "NET_KWH_RATE_D" );
INIT_ATTRIBUTE( NetkWhRateE,                                        "NET_KWH_RATE_E" );

INIT_ATTRIBUTE( SumkWh,                                             "SUM_KWH" );
INIT_ATTRIBUTE( SumkWhRateA,                                        "SUM_KWH_RATE_A" );
INIT_ATTRIBUTE( SumkWhRateB,                                        "SUM_KWH_RATE_B" );
INIT_ATTRIBUTE( SumkWhRateC,                                        "SUM_KWH_RATE_C" );
INIT_ATTRIBUTE( SumkWhRateD,                                        "SUM_KWH_RATE_D" );
INIT_ATTRIBUTE( SumkVAh,                                            "SUM_KVAH" );
INIT_ATTRIBUTE( SumkVArh,                                           "SUM_KVARH" );
INIT_ATTRIBUTE( SumkVArhRateA,                                      "SUM_KVARH_RATE_A" );
INIT_ATTRIBUTE( SumkVArhRateB,                                      "SUM_KVARH_RATE_B" );
INIT_ATTRIBUTE( SumkVArhRateC,                                      "SUM_KVARH_RATE_C" );
INIT_ATTRIBUTE( SumkVArhRateD,                                      "SUM_KVARH_RATE_D" );

INIT_ATTRIBUTE( UsageperInterval,                                   "USAGE_PER_INTERVAL" );
INIT_ATTRIBUTE( DeliveredkWhperInterval,                            "DELIVERED_KWH_PER_INTERVAL" );
INIT_ATTRIBUTE( ReceivedkWhperInterval,                             "RECEIVED_KWH_PER_INTERVAL" );
INIT_ATTRIBUTE( SumkWhperInterval,                                  "SUM_KWH_PER_INTERVAL" );
INIT_ATTRIBUTE( NetkWhperInterval,                                  "NET_KWH_PER_INTERVAL" );
INIT_ATTRIBUTE( SumkVAhperInterval,                                 "SUM_KVAH_PER_INTERVAL" );
INIT_ATTRIBUTE( DeliveredkVArhperInterval,                          "DELIVERED_KVARH_PER_INTERVAL" );
INIT_ATTRIBUTE( SumkVArhperInterval,                                "SUM_KVARH_PER_INTERVAL" );
INIT_ATTRIBUTE( WaterUsageperInterval,                              "WATER_USAGE_PER_INTERVAL" );
INIT_ATTRIBUTE( GasUsageperInterval,                                "GAS_USAGE_PER_INTERVAL" );
INIT_ATTRIBUTE( ForwardInductivekVArhperInterval,                   "FORWARD_INDUCTIVE_KVARH_PER_INTERVAL" );
INIT_ATTRIBUTE( ForwardCapacitivekVArhperInterval,                  "FORWARD_CAPACITIVE_KVARH_PER_INTERVAL" );
INIT_ATTRIBUTE( ReverseInductivekVArhperInterval,                   "REVERSE_INDUCTIVE_KVARH_PER_INTERVAL" );
INIT_ATTRIBUTE( ReverseCapacitivekVArhperInterval,                  "REVERSE_CAPACITIVE_KVARH_PER_INTERVAL" );

INIT_ATTRIBUTE( DeliveredkWLoadProfile,                             "DELIVERED_KW_LOAD_PROFILE" );
INIT_ATTRIBUTE( DeliveredkVArLoadProfile,                           "DELIVERED_KVAR_LOAD_PROFILE" );
INIT_ATTRIBUTE( ReceivedkWLoadProfile,                              "RECEIVED_KW_LOAD_PROFILE" );
INIT_ATTRIBUTE( SumkWLoadProfile,                                   "SUM_KW_LOAD_PROFILE" );
INIT_ATTRIBUTE( NetkWLoadProfile,                                   "NET_KW_LOAD_PROFILE" );
INIT_ATTRIBUTE( SumkVALoadProfile,                                  "SUM_KVA_LOAD_PROFILE" );
INIT_ATTRIBUTE( SumkVArLoadProfile,                                 "SUM_KVAR_LOAD_PROFILE" );
INIT_ATTRIBUTE( kVALoadProfile,                                     "KVA_LOAD_PROFILE" );

INIT_ATTRIBUTE( NetkVArh,                                           "NET_KVARH" );
INIT_ATTRIBUTE( NetDeliveredkVArh,                                  "NET_DELIVERED_KVARH" );
INIT_ATTRIBUTE( NetDeliveredkVArhRateA,                             "NET_DELIVERED_KVARH_RATE_A" );
INIT_ATTRIBUTE( NetDeliveredkVArhRateB,                             "NET_DELIVERED_KVARH_RATE_B" );
INIT_ATTRIBUTE( NetDeliveredkVArhRateC,                             "NET_DELIVERED_KVARH_RATE_C" );
INIT_ATTRIBUTE( NetDeliveredkVArhRateD,                             "NET_DELIVERED_KVARH_RATE_D" );

INIT_ATTRIBUTE( ReceivedkVArh,                                      "RECEIVED_KVARH" );
INIT_ATTRIBUTE( ReceivedkVArhRateA,                                 "RECEIVED_KVARH_RATE_A" );
INIT_ATTRIBUTE( ReceivedkVArhRateB,                                 "RECEIVED_KVARH_RATE_B" );
INIT_ATTRIBUTE( ReceivedkVArhRateC,                                 "RECEIVED_KVARH_RATE_C" );
INIT_ATTRIBUTE( ReceivedkVArhRateD,                                 "RECEIVED_KVARH_RATE_D" );
INIT_ATTRIBUTE( NetReceivedkVArh,                                   "NET_RECEIVED_KVARH" );
INIT_ATTRIBUTE( NetReceivedkVArhRateA,                              "NET_RECEIVED_KVARH_RATE_A" );
INIT_ATTRIBUTE( NetReceivedkVArhRateB,                              "NET_RECEIVED_KVARH_RATE_B" );
INIT_ATTRIBUTE( NetReceivedkVArhRateC,                              "NET_RECEIVED_KVARH_RATE_C" );
INIT_ATTRIBUTE( NetReceivedkVArhRateD,                              "NET_RECEIVED_KVARH_RATE_D" );

INIT_ATTRIBUTE( AlternateModeEntry,                                 "ALTERNATE_MODE_ENTRY" );
INIT_ATTRIBUTE( ANSISecurityFailed,                                 "ANSI_SECURITY_FAILED" );
INIT_ATTRIBUTE( BadUpgradeSecurityParameter,                        "BAD_UPGRADE_SECURITY_PARAM" );
INIT_ATTRIBUTE( BatteryEndOfLife,                                   "BATTERY_END_OF_LIFE" );
INIT_ATTRIBUTE( ConfigurationError,                                 "CONFIGURATION_ERROR" );
INIT_ATTRIBUTE( ClockError,                                         "CLOCK_ERROR" );
INIT_ATTRIBUTE( CRCFailureMemoryCorrupt,                            "CRC_FAILURE_MEMORY_CORRUPT" );
INIT_ATTRIBUTE( CrystalOscillatorError,                             "CRYSTAL_OSCILLATOR_ERROR" );
INIT_ATTRIBUTE( CurrentLoss,                                        "CURRENT_LOSS" );
INIT_ATTRIBUTE( CurrentWaveformDistortion,                          "CURRENT_WAVEFORM_DISTORTION" );
INIT_ATTRIBUTE( DemandOverload,                                     "DEMAND_OVERLOAD" );
INIT_ATTRIBUTE( DemandReadsAndReset,                                "DEMAND_READS_AND_RESET" );
INIT_ATTRIBUTE( DemandThresholdExceededWarning,                     "DEMAND_THRESHOLD_EXCEEDED_WARNING" );
INIT_ATTRIBUTE( DNP3AddressChanged,                                 "DNP3_ADDRESS_CHANGED" );
INIT_ATTRIBUTE( DisplayLockedByWarning,                             "DISPLAY_LOCKED_BY_WARNING" );
INIT_ATTRIBUTE( EepromAccessError,                                  "EEPROM_ACCESS_ERROR" );
INIT_ATTRIBUTE( EmptyPipe,                                          "EMPTY_PIPE" );
INIT_ATTRIBUTE( Encoder,                                            "ENCODER" );
INIT_ATTRIBUTE( EncryptionKeyTableCrcError,                         "ENCRYPTION_KEY_TABLE_CRC_ERROR" );
INIT_ATTRIBUTE( EndOfCalendarWarning,                               "END_OF_CALENDAR_WARNING" );
INIT_ATTRIBUTE( EnergyAccumulatedWhileInStandbyMode,                "ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE" );
INIT_ATTRIBUTE( ExceedingMaximumFlow,                               "EXCEEDING_MAXIMUM_FLOW" );
INIT_ATTRIBUTE( FailedUpgradeSignatureVerification,                 "FAILED_UPGRADE_SIGNATURE_VERIF" );
INIT_ATTRIBUTE( ImproperMeterEngineOperationWarning,                "IMPROPER_METER_ENGINE_OPERATION_WARNING" );
INIT_ATTRIBUTE( InactivePhaseCurrentDiagnosticError,                "INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR" );
INIT_ATTRIBUTE( InternalCommunicationError,                         "INTERNAL_COMMUNICATION_ERROR" );
INIT_ATTRIBUTE( InternalErrorFlag,                                  "INTERNAL_ERROR_FLAG" );
INIT_ATTRIBUTE( InvalidService,                                     "INVALID_SERVICE" );
INIT_ATTRIBUTE( LineFrequencyWarning,                               "LINE_FREQUENCY_WARNING" );
INIT_ATTRIBUTE( LoadSideVoltageIsMissing,                           "LOAD_SIDE_VOLTAGE_IS_MISSING" );
INIT_ATTRIBUTE( LossOfAllCurrent,                                   "LOSS_OF_ALL_CURRENT" );
INIT_ATTRIBUTE( LossOfPhaseACurrent,                                "LOSS_OF_PHASE_A_CURRENT" );
INIT_ATTRIBUTE( LossOfPhaseCCurrent,                                "LOSS_OF_PHASE_C_CURRENT" );
INIT_ATTRIBUTE( LowBatteryWarning,                                  "LOW_BATTERY_WARNING" );
INIT_ATTRIBUTE( LowLossPotential,                                   "LOW_LOSS_POTENTIAL" );
INIT_ATTRIBUTE( MassMemoryError,                                    "MASS_MEMORY_ERROR" );
INIT_ATTRIBUTE( MeasurementError,                                   "MEASUREMENT_ERROR" );
INIT_ATTRIBUTE( MeterFunctioningCorrectly,                          "METER_FUNCTIONING_CORRECTLY" );
INIT_ATTRIBUTE( MeterReconfigure,                                   "METER_RECONFIGURE" );
INIT_ATTRIBUTE( MetrologyCommunicationFailure,                      "METROLOGY_COMM_FAILURE" );
INIT_ATTRIBUTE( NonVolatileMemFailure,                              "NON_VOLATILE_MEM_FAILURE" );
INIT_ATTRIBUTE( OutstationDNP3SerCommLocked,                        "OUTSTATION_DNP3_SERCOMM_LOCKED" );
INIT_ATTRIBUTE( PasswordTableCrcError,                              "PASSWORD_TABLE_CRC_ERROR" );
INIT_ATTRIBUTE( PhaseAngleDisplacement,                             "PHASE_ANGLE_DISPLACEMENT" );
INIT_ATTRIBUTE( PhaseLoss,                                          "PHASE_LOSS" );
INIT_ATTRIBUTE( PolarityCrossPhaseAndEnergyFlowDiagnostic,          "POLARITY_CROSS_PHASE_ENERGY_FLOW_DIAGNOSTIC" );
INIT_ATTRIBUTE( PotentialIndicatorWarning,                          "POTENTIAL_INDICATOR_WARNING" );
INIT_ATTRIBUTE( PowerFailDataSaveError,                             "POWER_FAIL_DATA_SAVE_ERROR" );
INIT_ATTRIBUTE( PqmTestFailureWarning,                              "PQM_TEST_FAILURE_WARNING" );
INIT_ATTRIBUTE( RamError,                                           "RAM_ERROR" );
INIT_ATTRIBUTE( RegisterFullScaleExceeded,                          "REGISTER_FULL_SCALE_EXCEEDED" );
INIT_ATTRIBUTE( ReverseFlow,                                        "REVERSE_FLOW" );
INIT_ATTRIBUTE( ReversedAggregate,                                  "REVERSED_AGGREGATE" );
INIT_ATTRIBUTE( ReversedPhaseA,                                     "REVERSED_PHASE_A" );
INIT_ATTRIBUTE( ReversedPhaseC,                                     "REVERSED_PHASE_C" );
INIT_ATTRIBUTE( RfnBlinkCount,                                      "RFN_BLINK_COUNT" );
INIT_ATTRIBUTE( RfnBlinkRestoreCount,                               "RFN_BLINK_RESTORE_COUNT" );
INIT_ATTRIBUTE( RFNHighTemperatureAlarm,                            "RFN_TEMPERATURE_ALARM" );
INIT_ATTRIBUTE( RfnOutageCount,                                     "RFN_OUTAGE_COUNT" );
INIT_ATTRIBUTE( RfnOutageRestoreCount,                              "RFN_OUTAGE_RESTORE_COUNT" );
INIT_ATTRIBUTE( RomError,                                           "ROM_ERROR" );
INIT_ATTRIBUTE( SeasonChange,                                       "SEASON_CHANGE" );
INIT_ATTRIBUTE( SecurityConfigurationError,                         "SECURITY_CONFIGURATION_ERROR" );
INIT_ATTRIBUTE( SelfCheckError,                                     "SELF_CHECK_ERROR" );
INIT_ATTRIBUTE( SensorError,                                        "SENSOR_ERROR" );
INIT_ATTRIBUTE( ServiceCurrentTestFailureWarning,                   "SERVICE_CURRENT_TEST_FAILURE_WARNING" );
INIT_ATTRIBUTE( ServiceDisconnectSwitchError,                       "SERVICE_DISCONNECT_SWITCH_ERROR" );
INIT_ATTRIBUTE( ServiceDisconnectSwitchOpen,                        "SERVICE_DISCONNECT_SWITCH_OPEN" );
INIT_ATTRIBUTE( ServiceDisconnectSwitchSensorError,                 "SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR" );
INIT_ATTRIBUTE( SiteScanError,                                      "SITESCAN_ERROR" );
INIT_ATTRIBUTE( StorageMode,                                        "STORAGE_MODE" );
INIT_ATTRIBUTE( StuckSwitch,                                        "STUCK_SWITCH" );
INIT_ATTRIBUTE( SuspectedLeak,                                      "SUSPECTED_LEAK" );
INIT_ATTRIBUTE( TableCrcError,                                      "TABLE_CRC_ERROR" );
INIT_ATTRIBUTE( TemperatureOutOfRange,                              "TEMPERATURE_OUT_OF_RANGE" );
INIT_ATTRIBUTE( THDVorTDDIError,                                    "THD_V_OR_TDD_I_ERROR" );
INIT_ATTRIBUTE( ThirtyDaysNoUsage,                                  "THIRTY_DAYS_NO_USAGE" );
INIT_ATTRIBUTE( TimeAdjustment,                                     "TIME_ADJUSTMENT" );
INIT_ATTRIBUTE( TimeSyncFailed,                                     "TIME_SYNC_FAILED" );
INIT_ATTRIBUTE( TOUScheduleChange,                                  "TOU_SCHEDULE_CHANGE" );
INIT_ATTRIBUTE( TOUScheduleError,                                   "TOU_SCHEDULE_ERROR" );
INIT_ATTRIBUTE( Unconfigured,                                       "UNCONFIGURED" );
INIT_ATTRIBUTE( Unprogrammed,                                       "UNPROGRAMMED" );
INIT_ATTRIBUTE( UserProgrammableTemperatureThresholdExceeded,       "USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED" );
INIT_ATTRIBUTE( VibrationTiltTamperDetected,                        "VIBRATION_TILT_TAMPER_DETECTED" );
INIT_ATTRIBUTE( VoltageAlerts,                                      "VOLTAGE_ALERTS" );
INIT_ATTRIBUTE( VoltageLoss,                                        "VOLTAGE_LOSS" );
INIT_ATTRIBUTE( VoltagePhaseAOut,                                   "VOLTAGE_PHASE_A_OUT" );
INIT_ATTRIBUTE( VoltagePhaseBOut,                                   "VOLTAGE_PHASE_B_OUT" );
INIT_ATTRIBUTE( VoltagePhaseCOut,                                   "VOLTAGE_PHASE_C_OUT" );
INIT_ATTRIBUTE( VoltagePhaseError,                                  "VOLTAGE_PHASE_ERROR" );

INIT_ATTRIBUTE( NoEncoderFound,                                     "METER_READ_NO_ENCODER_FOUND" );
INIT_ATTRIBUTE( ParityError,                                        "METER_READ_PARITY_ERROR" );
INIT_ATTRIBUTE( NoEOFDetected,                                      "METER_READ_NO_EOF_DETECTED" );
INIT_ATTRIBUTE( UndeterminedProtocol,                               "METER_READ_PROTOCOL_CANNOT_BE_DETERMINED" );
INIT_ATTRIBUTE( FieldExceededMaximumDigits,                         "METER_READ_FIELD_EXCEEDED_MAXIMUM_DIGITS" );
INIT_ATTRIBUTE( SerialNumberReadError,                              "METER_READ_ERROR_READING_SERIAL_NUMBER" );
INIT_ATTRIBUTE( ChecksumError,                                      "METER_READ_CHECKSUM_ERROR" );
INIT_ATTRIBUTE( TamperCableCut,                                     "TAMPER_CABLE_CUT" );
INIT_ATTRIBUTE( DebugEvent,                                         "DEBUG_EVENT" );

INIT_ATTRIBUTE( RegisterRemoval,                                    "REGISTER_REMOVAL" );
INIT_ATTRIBUTE( MagnetTampering,                                    "MAGNET_TAMPERING" );

INIT_ATTRIBUTE( TamperNoUsageOver24Hours,                           "TAMPER_NO_USAGE_OVER_24_HOURS" );
INIT_ATTRIBUTE( TamperReverseWhDetected,                            "TAMPER_REVERSE_WH_DETECTED" );
INIT_ATTRIBUTE( TamperLargeIncreaseAfterOutage,                     "TAMPER_LARGE_INCREASE_AFTER_OUTAGE" );
INIT_ATTRIBUTE( TamperLargeDecreaseAfterOutage,                     "TAMPER_LARGE_DECREASE_AFTER_OUTAGE" );

INIT_ATTRIBUTE( SecurityAlarm,                                      "SECURITY_ALARM" );
INIT_ATTRIBUTE( PowerFailure,                                       "POWER_FAILURE" );
INIT_ATTRIBUTE( RadioFailure,                                       "RADIO_FAILURE" );
INIT_ATTRIBUTE( DoorOpen,                                           "DOOR_OPEN" );
INIT_ATTRIBUTE( NodeCountExceeded,                                  "NODE_COUNT_EXCEEDED" );

INIT_ATTRIBUTE( StreamingCapableDeviceCount,                        "STREAMING_CAPABLE_DEVICE_COUNT" );
INIT_ATTRIBUTE( StreamingActiveDeviceCount,                         "STREAMING_ACTIVE_DEVICE_COUNT" );
INIT_ATTRIBUTE( DataStreamingLoad,                                  "DATA_STREAMING_LOAD" );
INIT_ATTRIBUTE( ReadyNodes,                                         "READY_NODES" );

INIT_ATTRIBUTE( FirmwareVersion,                                    "FIRMWARE_VERSION" );
INIT_ATTRIBUTE( FirmwareVersionMajor,                               "FIRMWARE_VERSION_MAJOR" );
INIT_ATTRIBUTE( FirmwareVersionMinor,                               "FIRMWARE_VERSION_MINOR" );
INIT_ATTRIBUTE( IgnoredControlReason,                               "IGNORED_CONTROL_REASON" );
INIT_ATTRIBUTE( IpAddress,                                          "IP_ADDRESS" );
INIT_ATTRIBUTE( LastControlReason,                                  "LAST_CONTROL_REASON" );
INIT_ATTRIBUTE( NeutralCurrentSensor,                               "NEUTRAL_CURRENT_SENSOR" );
INIT_ATTRIBUTE( SerialNumber,                                       "SERIAL_NUMBER" );
INIT_ATTRIBUTE( UdpPort,                                            "UDP_PORT" );
INIT_ATTRIBUTE( ConnectedLoad,                                      "CONNECTED_LOAD" );
INIT_ATTRIBUTE( DiversifiedLoad,                                    "DIVERSIFIED_LOAD" );
INIT_ATTRIBUTE( MaxLoadReduction,                                   "MAX_LOAD_REDUCTION" );
INIT_ATTRIBUTE( AvailableLoadReduction,                             "AVAILABLE_LOAD_REDUCTION" );

INIT_ATTRIBUTE( PorterCpuUtilization,                               "PORTER_CPU_UTILIZATION" );
INIT_ATTRIBUTE( DispatchCpuUtilization,                             "DISPATCH_CPU_UTILIZATION" );
INIT_ATTRIBUTE( ScannerCpuUtilization,                              "SCANNER_CPU_UTILIZATION" );
INIT_ATTRIBUTE( CalcCpuUtilization,                                 "CALC_CPU_UTILIZATION" );
INIT_ATTRIBUTE( CapcontrolCpuUtilization,                           "CAPCONTROL_CPU_UTILIZATION" );
INIT_ATTRIBUTE( FdrCpuUtilization,                                  "FDR_CPU_UTILIZATION" );
INIT_ATTRIBUTE( MacsCpuUtilization,                                 "MACS_CPU_UTILIZATION" );

INIT_ATTRIBUTE( NotificationServerCpuUtilization,                   "NOTIFICATION_SERVER_CPU_UTILIZATION" );
INIT_ATTRIBUTE( ServiceManagerCpuUtilization,                       "SERVICE_MANAGER_CPU_UTILIZATION" );
INIT_ATTRIBUTE( WebServiceCpuUtilization,                           "WEB_SERVICE_CPU_UTILIZATION" );
INIT_ATTRIBUTE( MessageBrokerCpuUtilization,                        "MESSAGE_BROKER_CPU_UTILIZATION" );

INIT_ATTRIBUTE( PorterMemoryUtilization,                            "PORTER_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( DispatchMemoryUtilization,                          "DISPATCH_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( ScannerMemoryUtilization,                           "SCANNER_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( CalcMemoryUtilization,                              "CALC_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( CapcontrolMemoryUtilization,                        "CAPCONTROL_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( FdrMemoryUtilization,                               "FDR_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( MacsMemoryUtilization,                              "MACS_MEMORY_UTILIZATION" );

INIT_ATTRIBUTE( NotificationServerMemoryUtilization,                "NOTIFICATION_SERVER_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( ServiceManagerMemoryUtilization,                    "SERVICE_MANAGER_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( WebServiceMemoryUtilization,                        "WEB_SERVICE_MEMORY_UTILIZATION" );
INIT_ATTRIBUTE( MessageBrokerMemoryUtilization,                     "MESSAGE_BROKER_MEMORY_UTILIZATION" );

INIT_ATTRIBUTE( LoadManagementCpuUtilization,                       "LOAD_MANAGEMENT_CPU_UTILIZATION" );
INIT_ATTRIBUTE( LoadManagementMemoryUtilization,                    "LOAD_MANAGEMENT_MEMORY_UTILIZATION" );

INIT_ATTRIBUTE( ThermostatRelayState,                               "THERMOSTAT_RELAY_STATE" );

INIT_ATTRIBUTE( AnalogInputOne,                                     "ANALOG_INPUT_ONE" );
INIT_ATTRIBUTE( AutoBlockEnable,                                    "AUTO_BLOCK_ENABLE" );
INIT_ATTRIBUTE( AutoRemoteControl,                                  "AUTO_REMOTE_CONTROL" );
INIT_ATTRIBUTE( AutoVoltageControl,                                 "AUTO_VOLTAGE_CONTROL" );
INIT_ATTRIBUTE( BadRelay,                                           "BAD_RELAY" );
INIT_ATTRIBUTE( CloseOperationCount,                                "CLOSE_OPERATION_COUNT" );
INIT_ATTRIBUTE( ControlMode,                                        "CONTROL_MODE" );
INIT_ATTRIBUTE( ControlPoint,                                       "CONTROL_POINT" );
INIT_ATTRIBUTE( DailyMaxOperations,                                 "DAILY_MAX_OPERATION" );
INIT_ATTRIBUTE( DeltaVoltage,                                       "DELTA_VOLTAGE" );
INIT_ATTRIBUTE( DSTActive,                                          "DST_ACTIVE" );
INIT_ATTRIBUTE( EnableOvuvControl,                                  "ENABLE_OVUV_CONTROL" );
INIT_ATTRIBUTE( EnableVarControl,                                   "ENABLE_VAR_CONTROL" );
INIT_ATTRIBUTE( EnableTemperatureControl,                           "ENABLE_TEMPERATURE_CONTROL" );
INIT_ATTRIBUTE( EnableTimeControl,                                  "ENABLE_TIME_CONTROL" );
INIT_ATTRIBUTE( ForwardBandwidth,                                   "FORWARD_BANDWIDTH" );
INIT_ATTRIBUTE( ForwardSetPoint,                                    "FORWARD_SET_POINT" );
INIT_ATTRIBUTE( HeartbeatTimerConfig,                               "HEARTBEAT_TIMER_CONFIG" );
INIT_ATTRIBUTE( HighVoltage,                                        "HIGH_VOLTAGE" );
INIT_ATTRIBUTE( IgnoredIndicator,                                   "IGNORED_INDICATOR" );
INIT_ATTRIBUTE( KeepAlive,                                          "KEEP_ALIVE" );
INIT_ATTRIBUTE( LastControlReasonAnalog,                            "LAST_CONTROL_REASON_ANALOG" );
INIT_ATTRIBUTE( LastControlReasonDigital,                           "LAST_CONTROL_REASON_DIGITAL" );
INIT_ATTRIBUTE( LastControlReasonLocal,                             "LAST_CONTROL_REASON_LOCAL" );
INIT_ATTRIBUTE( LastControlReasonNeutralFault,                      "LAST_CONTROL_REASON_NEUTRAL_FAULT" );
INIT_ATTRIBUTE( LastControlReasonOvUv,                              "LAST_CONTROL_REASON_OVUV" );
INIT_ATTRIBUTE( LastControlReasonRemote,                            "LAST_CONTROL_REASON_REMOTE" );
INIT_ATTRIBUTE( LastControlReasonScheduled,                         "LAST_CONTROL_REASON_SCHEDULED" );
INIT_ATTRIBUTE( LastControlReasonTemperature,                       "LAST_CONTROL_REASON_TEMPERATURE" );
INIT_ATTRIBUTE( LowVoltage,                                         "LOW_VOLTAGE" );
INIT_ATTRIBUTE( NeutralCurrentAlarmThreshold,                       "NEUTRAL_CURRENT_ALARM_THRESHOLD" );
INIT_ATTRIBUTE( NeutralCurrentFault,                                "NEUTRAL_CURRENT_FAULT" );
INIT_ATTRIBUTE( NeutralLockout,                                     "NEUTRAL_LOCKOUT" );
INIT_ATTRIBUTE( OpenOperationCount,                                 "OPEN_OPERATION_COUNT" );
INIT_ATTRIBUTE( OperationFailedNeutralCurrent,                      "OPERATION_FAILED_NEUTRAL_CURRENT" );
INIT_ATTRIBUTE( OverUnderVoltageTrackTime,                          "OVER_UNDER_VOLTAGE_TRACKING_TIME" );
INIT_ATTRIBUTE( OverVoltageCount,                                   "OVER_VOLTAGE_COUNT" );
INIT_ATTRIBUTE( RadioSignalStrengthIndicator,                       "RADIO_SIGNAL_STRENGTH_INDICATOR" );
INIT_ATTRIBUTE( RecloseBlocked,                                     "RECLOSE_BLOCKED" );
INIT_ATTRIBUTE( ScadaOverrideClear,                                 "SCADA_OVERRIDE_CLEAR" );
INIT_ATTRIBUTE( ScadaOverrideControlPoint,                          "SCADA_OVERRIDE_CONTROL_POINT" );
INIT_ATTRIBUTE( ScadaOverrideCountdownTimer,                        "SCADA_OVERRIDE_COUNTDOWN_TIMER" );
INIT_ATTRIBUTE( ScadaOverrideEnable,                                "SCADA_OVERRIDE_ENABLE" );
INIT_ATTRIBUTE( ScadaOverrideHeartbeat,                             "SCADA_OVERRIDE_HEARTBEAT" );
INIT_ATTRIBUTE( ScadaOverrideMode,                                  "SCADA_OVERRIDE_MODE" );
INIT_ATTRIBUTE( SourceVoltage,                                      "SOURCE_VOLTAGE" );
INIT_ATTRIBUTE( TapDown,                                            "TAP_DOWN" );
INIT_ATTRIBUTE( TapPosition,                                        "TAP_POSITION" );
INIT_ATTRIBUTE( TapUp,                                              "TAP_UP" );
INIT_ATTRIBUTE( TemperatureAlarm,                                   "TEMPERATURE_ALARM" );
INIT_ATTRIBUTE( Terminate,                                          "TERMINATE" );
INIT_ATTRIBUTE( TimeTempControlSeasonOne,                           "TIME_TEMP_CONTROL_SEASON_ONE" );
INIT_ATTRIBUTE( TimeTempControlSeasonTwo,                           "TIME_TEMP_CONTROL_SEASON_TWO" );
INIT_ATTRIBUTE( TotalOperationCount,                                "TOTAL_OPERATION_COUNT" );
INIT_ATTRIBUTE( UnderVoltageCount,                                  "UNDER_VOLTAGE_COUNT" );
INIT_ATTRIBUTE( VarControl,                                         "VAR_CONTROL" );
INIT_ATTRIBUTE( VoltageControl,                                     "VOLTAGE_CONTROL" );
INIT_ATTRIBUTE( VoltageDeltaAbnormal,                               "VOLTAGE_DELTA_ABNORMAL" );
INIT_ATTRIBUTE( PortQueueCount,                                     "PORT_QUEUE_COUNT" );

INIT_ATTRIBUTE( ReverseBandwidth,                                   "REVERSE_BANDWIDTH" );
INIT_ATTRIBUTE( ReverseSetPoint,                                    "REVERSE_SET_POINT" );
INIT_ATTRIBUTE( ReverseFlowIndicator,                               "REVERSE_FLOW_INDICATOR" );

INIT_ATTRIBUTE( EventSuperseded,                                    "EVENT_SUPERSEDED" );

AttributeNotFound::AttributeNotFound(const std::string &name)
{
    desc = "Attribute not found: " + name;
}

const char* AttributeNotFound::what() const
{
    return desc.c_str();
}

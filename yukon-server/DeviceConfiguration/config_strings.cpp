#include "precompiled.h"

#include "config_data_mct.h"
#include "config_data_dnp.h"
#include "config_data_rfn.h"
#include "config_data_cbc.h"
#include "config_data_regulator.h"
#include "config_device.h"

using std::string;

using namespace Cti::Config;

const string MCTStrings::Bronze = "bronzeAddress";
const string MCTStrings::Lead = "leadAddress";
const string MCTStrings::Collection = "collectionAddress";
const string MCTStrings::ServiceProviderID = "serviceProviderId";

const string MCTStrings::touEnabled = "enableTou";

const string MCTStrings::MondaySchedule = "monday";
const string MCTStrings::TuesdaySchedule = "tuesday";
const string MCTStrings::WednesdaySchedule = "wednesday";
const string MCTStrings::ThursdaySchedule = "thursday";
const string MCTStrings::FridaySchedule = "friday";
const string MCTStrings::SaturdaySchedule = "saturday";
const string MCTStrings::SundaySchedule = "sunday";
const string MCTStrings::HolidaySchedule = "holiday";
const string MCTStrings::WeekdaysSchedule = "weekday";
const string MCTStrings::DefaultTOURate = "defaultRate";

const string MCTStrings::Schedule1Time0 = "schedule1time0";
const string MCTStrings::Schedule1Rate0 = "schedule1rate0";
const string MCTStrings::Schedule1Time1 = "schedule1time1";
const string MCTStrings::Schedule1Rate1 = "schedule1rate1";
const string MCTStrings::Schedule1Time2 = "schedule1time2";
const string MCTStrings::Schedule1Rate2 = "schedule1rate2";
const string MCTStrings::Schedule1Time3 = "schedule1time3";
const string MCTStrings::Schedule1Rate3 = "schedule1rate3";
const string MCTStrings::Schedule1Time4 = "schedule1time4";
const string MCTStrings::Schedule1Rate4 = "schedule1rate4";
const string MCTStrings::Schedule1Time5 = "schedule1time5";
const string MCTStrings::Schedule1Rate5 = "schedule1rate5";
const string MCTStrings::Schedule1Time6 = "schedule1time6";
const string MCTStrings::Schedule1Rate6 = "schedule1rate6";
const string MCTStrings::Schedule1Time7 = "schedule1time7";
const string MCTStrings::Schedule1Rate7 = "schedule1rate7";
const string MCTStrings::Schedule1Time8 = "schedule1time8";
const string MCTStrings::Schedule1Rate8 = "schedule1rate8";
const string MCTStrings::Schedule1Time9 = "schedule1time9";
const string MCTStrings::Schedule1Rate9 = "schedule1rate9";

const string MCTStrings::Schedule2Time0 = "schedule2time0";
const string MCTStrings::Schedule2Rate0 = "schedule2rate0";
const string MCTStrings::Schedule2Time1 = "schedule2time1";
const string MCTStrings::Schedule2Rate1 = "schedule2rate1";
const string MCTStrings::Schedule2Time2 = "schedule2time2";
const string MCTStrings::Schedule2Rate2 = "schedule2rate2";
const string MCTStrings::Schedule2Time3 = "schedule2time3";
const string MCTStrings::Schedule2Rate3 = "schedule2rate3";
const string MCTStrings::Schedule2Time4 = "schedule2time4";
const string MCTStrings::Schedule2Rate4 = "schedule2rate4";
const string MCTStrings::Schedule2Time5 = "schedule2time5";
const string MCTStrings::Schedule2Rate5 = "schedule2rate5";
const string MCTStrings::Schedule2Time6 = "schedule2time6";
const string MCTStrings::Schedule2Rate6 = "schedule2rate6";
const string MCTStrings::Schedule2Time7 = "schedule2time7";
const string MCTStrings::Schedule2Rate7 = "schedule2rate7";
const string MCTStrings::Schedule2Time8 = "schedule2time8";
const string MCTStrings::Schedule2Rate8 = "schedule2rate8";
const string MCTStrings::Schedule2Time9 = "schedule2time9";
const string MCTStrings::Schedule2Rate9 = "schedule2rate9";

const string MCTStrings::Schedule3Time0 = "schedule3time0";
const string MCTStrings::Schedule3Rate0 = "schedule3rate0";
const string MCTStrings::Schedule3Time1 = "schedule3time1";
const string MCTStrings::Schedule3Rate1 = "schedule3rate1";
const string MCTStrings::Schedule3Time2 = "schedule3time2";
const string MCTStrings::Schedule3Rate2 = "schedule3rate2";
const string MCTStrings::Schedule3Time3 = "schedule3time3";
const string MCTStrings::Schedule3Rate3 = "schedule3rate3";
const string MCTStrings::Schedule3Time4 = "schedule3time4";
const string MCTStrings::Schedule3Rate4 = "schedule3rate4";
const string MCTStrings::Schedule3Time5 = "schedule3time5";
const string MCTStrings::Schedule3Rate5 = "schedule3rate5";
const string MCTStrings::Schedule3Time6 = "schedule3time6";
const string MCTStrings::Schedule3Rate6 = "schedule3rate6";
const string MCTStrings::Schedule3Time7 = "schedule3time7";
const string MCTStrings::Schedule3Rate7 = "schedule3rate7";
const string MCTStrings::Schedule3Time8 = "schedule3time8";
const string MCTStrings::Schedule3Rate8 = "schedule3rate8";
const string MCTStrings::Schedule3Time9 = "schedule3time9";
const string MCTStrings::Schedule3Rate9 = "schedule3rate9";

const string MCTStrings::Schedule4Time0 = "schedule4time0";
const string MCTStrings::Schedule4Rate0 = "schedule4rate0";
const string MCTStrings::Schedule4Time1 = "schedule4time1";
const string MCTStrings::Schedule4Rate1 = "schedule4rate1";
const string MCTStrings::Schedule4Time2 = "schedule4time2";
const string MCTStrings::Schedule4Rate2 = "schedule4rate2";
const string MCTStrings::Schedule4Time3 = "schedule4time3";
const string MCTStrings::Schedule4Rate3 = "schedule4rate3";
const string MCTStrings::Schedule4Time4 = "schedule4time4";
const string MCTStrings::Schedule4Rate4 = "schedule4rate4";
const string MCTStrings::Schedule4Time5 = "schedule4time5";
const string MCTStrings::Schedule4Rate5 = "schedule4rate5";
const string MCTStrings::Schedule4Time6 = "schedule4time6";
const string MCTStrings::Schedule4Rate6 = "schedule4rate6";
const string MCTStrings::Schedule4Time7 = "schedule4time7";
const string MCTStrings::Schedule4Rate7 = "schedule4rate7";
const string MCTStrings::Schedule4Time8 = "schedule4time8";
const string MCTStrings::Schedule4Rate8 = "schedule4rate8";
const string MCTStrings::Schedule4Time9 = "schedule4time9";
const string MCTStrings::Schedule4Rate9 = "schedule4rate9";

const string MCTStrings::TimeZoneOffset = "timeZoneOffset";

const string MCTStrings::PeakKwResolution1 = "channel1PeakKWResolution";
const string MCTStrings::LastIntervalDemandResolution1 = "channel1LastIntervalDemandResolution";
const string MCTStrings::ProfileResolution1 = "channel1ProfileResolution";

const string MCTStrings::PeakKwResolution2 = "channel2PeakKWResolution";
const string MCTStrings::LastIntervalDemandResolution2 = "channel2LastIntervalDemandResolution";
const string MCTStrings::ProfileResolution2 = "channel2ProfileResolution";

const string MCTStrings::PeakKwResolution3 = "channel3PeakKWResolution";
const string MCTStrings::LastIntervalDemandResolution3 = "channel3LastIntervalDemandResolution";
const string MCTStrings::ProfileResolution3 = "channel3ProfileResolution";

const string MCTStrings::PeakKwResolution4 = "channel4PeakKWResolution";
const string MCTStrings::LastIntervalDemandResolution4 = "channel4LastIntervalDemandResolution";
const string MCTStrings::ProfileResolution4 = "channel4ProfileResolution";

const string MCTStrings::DemandInterval = "demandInterval";
const string MCTStrings::ProfileInterval = "profileInterval";

const string MCTStrings::TimeAdjustTolerance = "timeAdjustTolerance";
const string MCTStrings::EnableDst = "enableDst";
const string MCTStrings::ElectronicMeter = "electronicMeter";

const string MCTStrings::Channel1Type = "channel1Type";
const string MCTStrings::Channel2Type = "channel2Type";
const string MCTStrings::Channel3Type = "channel3Type";
const string MCTStrings::Channel4Type = "channel4Type";

const string MCTStrings::Channel1PhysicalChannel = "channel1PhysicalChannel";
const string MCTStrings::Channel2PhysicalChannel = "channel2PhysicalChannel";
const string MCTStrings::Channel3PhysicalChannel = "channel3PhysicalChannel";
const string MCTStrings::Channel4PhysicalChannel = "channel4PhysicalChannel";

const string MCTStrings::ChannelMultiplier1 = "channel1Multiplier";
const string MCTStrings::ChannelMultiplier2 = "channel2Multiplier";
const string MCTStrings::ChannelMultiplier3 = "channel3Multiplier";
const string MCTStrings::ChannelMultiplier4 = "channel4Multiplier";

const string MCTStrings::RelayATimer = "relayATimer";
const string MCTStrings::RelayBTimer = "relayBTimer";
const string MCTStrings::TableReadInterval = "tableReadInterval";
const string MCTStrings::MeterNumber = "meterNumber";
const string MCTStrings::TableType = "tableType";

const string MCTStrings::PhaseLossThreshold = "phaseLossThreshold";
const string MCTStrings::PhaseLossDuration  = "phaseLossDuration";

const string MCTStrings::displayItem01 = "displayItem1";
const string MCTStrings::displayItem02 = "displayItem2";
const string MCTStrings::displayItem03 = "displayItem3";
const string MCTStrings::displayItem04 = "displayItem4";
const string MCTStrings::displayItem05 = "displayItem5";
const string MCTStrings::displayItem06 = "displayItem6";
const string MCTStrings::displayItem07 = "displayItem7";
const string MCTStrings::displayItem08 = "displayItem8";
const string MCTStrings::displayItem09 = "displayItem9";
const string MCTStrings::displayItem10 = "displayItem10";
const string MCTStrings::displayItem11 = "displayItem11";
const string MCTStrings::displayItem12 = "displayItem12";
const string MCTStrings::displayItem13 = "displayItem13";
const string MCTStrings::displayItem14 = "displayItem14";
const string MCTStrings::displayItem15 = "displayItem15";
const string MCTStrings::displayItem16 = "displayItem16";
const string MCTStrings::displayItem17 = "displayItem17";
const string MCTStrings::displayItem18 = "displayItem18";
const string MCTStrings::displayItem19 = "displayItem19";
const string MCTStrings::displayItem20 = "displayItem20";
const string MCTStrings::displayItem21 = "displayItem21";
const string MCTStrings::displayItem22 = "displayItem22";
const string MCTStrings::displayItem23 = "displayItem23";
const string MCTStrings::displayItem24 = "displayItem24";
const string MCTStrings::displayItem25 = "displayItem25";
const string MCTStrings::displayItem26 = "displayItem26";

const string MCTStrings::LcdCycleTime = "lcdCycleTime";
const string MCTStrings::DisconnectDisplayDisabled = "disconnectDisplayDisabled";
const string MCTStrings::DisplayDigits = "displayDigits";

const string MCTStrings::DisconnectMode = "disconnectMode";
const string MCTStrings::DisconnectDemandThreshold = "disconnectDemandThreshold";
const string MCTStrings::DisconnectLoadLimitConnectDelay = "disconnectLoadLimitConnectDelay";
const string MCTStrings::DisconnectMinutes = "disconnectMinutes";
const string MCTStrings::ConnectMinutes = "connectMinutes";
const string MCTStrings::ReconnectParameter = "reconnectParam";

const string MCTStrings::DemandFreezeDay    = "demandFreezeDay";

/* DNP */
const string DNPStrings::omitTimeRequest = "omitTimeRequest";
const string DNPStrings::internalRetries = "internalRetries";
const string DNPStrings::enableUnsolicitedClass1 = "enableUnsolicitedMessagesClass1";
const string DNPStrings::enableUnsolicitedClass2 = "enableUnsolicitedMessagesClass2";
const string DNPStrings::enableUnsolicitedClass3 = "enableUnsolicitedMessagesClass3";
const string DNPStrings::enableNonUpdatedOnFailedScan = "enableNonUpdatedOnFailedScan";
const string DNPStrings::enableDnpTimesyncs = "enableDnpTimesyncs";
const string DNPStrings::timeOffset = "timeOffset";

const string DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix     = "attributeMappings";
const string DNPStrings::AttributeMappingConfiguration::AttributeMappings::Attribute = "attribute";
const string DNPStrings::AttributeMappingConfiguration::AttributeMappings::PointName = "pointName";

/* RFN */
const string RfnStrings::displayItem01 = "displayItem1";
const string RfnStrings::displayItem02 = "displayItem2";
const string RfnStrings::displayItem03 = "displayItem3";
const string RfnStrings::displayItem04 = "displayItem4";
const string RfnStrings::displayItem05 = "displayItem5";
const string RfnStrings::displayItem06 = "displayItem6";
const string RfnStrings::displayItem07 = "displayItem7";
const string RfnStrings::displayItem08 = "displayItem8";
const string RfnStrings::displayItem09 = "displayItem9";
const string RfnStrings::displayItem10 = "displayItem10";
const string RfnStrings::displayItem11 = "displayItem11";
const string RfnStrings::displayItem12 = "displayItem12";
const string RfnStrings::displayItem13 = "displayItem13";
const string RfnStrings::displayItem14 = "displayItem14";
const string RfnStrings::displayItem15 = "displayItem15";
const string RfnStrings::displayItem16 = "displayItem16";
const string RfnStrings::displayItem17 = "displayItem17";
const string RfnStrings::displayItem18 = "displayItem18";
const string RfnStrings::displayItem19 = "displayItem19";
const string RfnStrings::displayItem20 = "displayItem20";
const string RfnStrings::displayItem21 = "displayItem21";
const string RfnStrings::displayItem22 = "displayItem22";
const string RfnStrings::displayItem23 = "displayItem23";
const string RfnStrings::displayItem24 = "displayItem24";
const string RfnStrings::displayItem25 = "displayItem25";
const string RfnStrings::displayItem26 = "displayItem26";

const string RfnStrings::LcdCycleTime = "lcdCycleTime";
const string RfnStrings::DisconnectDisplayDisabled = "disconnectDisplayDisabled";
const string RfnStrings::DisplayDigits = "displayDigits";

const string RfnStrings::voltageAveragingInterval   = "voltageAveragingInterval";
const string RfnStrings::demandInterval        = "demandInterval";
const string RfnStrings::enableDataStreaming   = "enableDataStreaming";
const string RfnStrings::voltageDataStreamingIntervalMinutes = "voltageDataStreamingIntervalMinutes";

const string RfnStrings::demandFreezeDay       = "demandFreezeDay";

const string RfnStrings::touEnabled             = "enableTou";

// day table
const string RfnStrings::MondaySchedule        = "monday";
const string RfnStrings::TuesdaySchedule       = "tuesday";
const string RfnStrings::WednesdaySchedule     = "wednesday";
const string RfnStrings::ThursdaySchedule      = "thursday";
const string RfnStrings::FridaySchedule        = "friday";
const string RfnStrings::SaturdaySchedule      = "saturday";
const string RfnStrings::SundaySchedule        = "sunday";
const string RfnStrings::HolidaySchedule       = "holiday";

// default rate
const string RfnStrings::DefaultTouRate        = "defaultRate";

// schedule 1
const string RfnStrings::Schedule1Time0        = "schedule1time0";
const string RfnStrings::Schedule1Rate0        = "schedule1rate0";
const string RfnStrings::Schedule1Time1        = "schedule1time1";
const string RfnStrings::Schedule1Rate1        = "schedule1rate1";
const string RfnStrings::Schedule1Time2        = "schedule1time2";
const string RfnStrings::Schedule1Rate2        = "schedule1rate2";
const string RfnStrings::Schedule1Time3        = "schedule1time3";
const string RfnStrings::Schedule1Rate3        = "schedule1rate3";
const string RfnStrings::Schedule1Time4        = "schedule1time4";
const string RfnStrings::Schedule1Rate4        = "schedule1rate4";
const string RfnStrings::Schedule1Time5        = "schedule1time5";
const string RfnStrings::Schedule1Rate5        = "schedule1rate5";

// schedule 2
const string RfnStrings::Schedule2Time0        = "schedule2time0";
const string RfnStrings::Schedule2Rate0        = "schedule2rate0";
const string RfnStrings::Schedule2Time1        = "schedule2time1";
const string RfnStrings::Schedule2Rate1        = "schedule2rate1";
const string RfnStrings::Schedule2Time2        = "schedule2time2";
const string RfnStrings::Schedule2Rate2        = "schedule2rate2";
const string RfnStrings::Schedule2Time3        = "schedule2time3";
const string RfnStrings::Schedule2Rate3        = "schedule2rate3";
const string RfnStrings::Schedule2Time4        = "schedule2time4";
const string RfnStrings::Schedule2Rate4        = "schedule2rate4";
const string RfnStrings::Schedule2Time5        = "schedule2time5";
const string RfnStrings::Schedule2Rate5        = "schedule2rate5";

// schedule 3
const string RfnStrings::Schedule3Time0        = "schedule3time0";
const string RfnStrings::Schedule3Rate0        = "schedule3rate0";
const string RfnStrings::Schedule3Time1        = "schedule3time1";
const string RfnStrings::Schedule3Rate1        = "schedule3rate1";
const string RfnStrings::Schedule3Time2        = "schedule3time2";
const string RfnStrings::Schedule3Rate2        = "schedule3rate2";
const string RfnStrings::Schedule3Time3        = "schedule3time3";
const string RfnStrings::Schedule3Rate3        = "schedule3rate3";
const string RfnStrings::Schedule3Time4        = "schedule3time4";
const string RfnStrings::Schedule3Rate4        = "schedule3rate4";
const string RfnStrings::Schedule3Time5        = "schedule3time5";
const string RfnStrings::Schedule3Rate5        = "schedule3rate5";

// schedule 4
const string RfnStrings::Schedule4Time0        = "schedule4time0";
const string RfnStrings::Schedule4Rate0        = "schedule4rate0";
const string RfnStrings::Schedule4Time1        = "schedule4time1";
const string RfnStrings::Schedule4Rate1        = "schedule4rate1";
const string RfnStrings::Schedule4Time2        = "schedule4time2";
const string RfnStrings::Schedule4Rate2        = "schedule4rate2";
const string RfnStrings::Schedule4Time3        = "schedule4time3";
const string RfnStrings::Schedule4Rate3        = "schedule4rate3";
const string RfnStrings::Schedule4Time4        = "schedule4time4";
const string RfnStrings::Schedule4Rate4        = "schedule4rate4";
const string RfnStrings::Schedule4Time5        = "schedule4time5";
const string RfnStrings::Schedule4Rate5        = "schedule4rate5";

// OV/UV Configuration
const string RfnStrings::OvUvEnabled                = "ovuvEnabled";
const string RfnStrings::OvThreshold                = "ovThreshold";
const string RfnStrings::UvThreshold                = "uvThreshold";
const string RfnStrings::OvUvAlarmReportingInterval = "alarmReportingInterval";
const string RfnStrings::OvUvAlarmRepeatInterval    = "alarmRepeatInterval";
const string RfnStrings::OvUvRepeatCount            = "ovuvRepeats";

// Disconnect configuration
const string RfnStrings::DisconnectMode              = "disconnectMode";
const string RfnStrings::ReconnectParam              = "reconnectParam";
const string RfnStrings::DisconnectDemandInterval    = "disconnectDemandInterval";
const string RfnStrings::DisconnectDemandThreshold   = "disconnectDemandThreshold";
const string RfnStrings::LoadLimitConnectDelay       = "disconnectLoadLimitConnectDelay";
const string RfnStrings::MaxDisconnects              = "maxDisconnects";
const string RfnStrings::DisconnectMinutes           = "disconnectMinutes";
const string RfnStrings::ConnectMinutes              = "connectMinutes";

// Temperature Alarm configuration
const string RfnStrings::TemperatureAlarmEnabled            = "temperatureAlarmEnabled";
const string RfnStrings::TemperatureAlarmRepeatInterval     = "temperatureAlarmRepeatInterval";
const string RfnStrings::TemperatureAlarmRepeatCount        = "temperatureAlarmRepeatCount";
const string RfnStrings::TemperatureAlarmHighTempThreshold  = "temperatureAlarmHighTempThreshold";

// Channel configuration
const string RfnStrings::ChannelConfiguration::EnabledChannels_Prefix = "enabledChannels";
const string RfnStrings::ChannelConfiguration::EnabledChannels::Attribute = "attribute";
const string RfnStrings::ChannelConfiguration::EnabledChannels::Read    = "read";
const string RfnStrings::ChannelConfiguration::RecordingIntervalMinutes = "recordingIntervalMinutes";
const string RfnStrings::ChannelConfiguration::ReportingIntervalMinutes = "reportingIntervalMinutes";

// Water node configuration
const string RfnStrings::WaterNodeConfiguration::ReportingIntervalSeconds = "reportingIntervalSeconds";
const string RfnStrings::WaterNodeConfiguration::RecordingIntervalSeconds = "recordingIntervalSeconds";

// Metrology Library configuration
const string RfnStrings::MetrologyLibraryEnabled = "metrologyLibraryEnabled";

// Regulators
const string RegulatorStrings::voltageControlMode  = "voltageControlMode";
const string RegulatorStrings::voltageChangePerTap = "voltageChangePerTap";
const string RegulatorStrings::heartbeatPeriod     = "regulatorHeartbeatPeriod";
const string RegulatorStrings::heartbeatValue      = "regulatorHeartbeatValue";
const string RegulatorStrings::heartbeatMode       = "regulatorHeartbeatMode";
const string RegulatorStrings::installOrientation  = "installOrientation";
const string RegulatorStrings::minTapPosition      = "minTapPosition";
const string RegulatorStrings::maxTapPosition      = "maxTapPosition";

// CBC
const string CbcStrings::cbcHeartbeatPeriod     = "cbcHeartbeatPeriod";
const string CbcStrings::cbcHeartbeatValue      = "cbcHeartbeatValue";
const string CbcStrings::cbcHeartbeatMode       = "cbcHeartbeatMode";

using DIV = DisplayItemValues;

// Display Items
#define DIV_STRING(x) const string DIV::x = #x

DIV_STRING(SLOT_DISABLED);
DIV_STRING(NO_SEGMENTS);
DIV_STRING(ALL_SEGMENTS);

DIV_STRING(CURRENT_LOCAL_TIME);
DIV_STRING(CURRENT_LOCAL_DATE);
DIV_STRING(TOTAL_KWH);
DIV_STRING(NET_KWH);
DIV_STRING(DELIVERED_KWH);
DIV_STRING(RECEIVED_KWH);
DIV_STRING(LAST_INTERVAL_KW);
DIV_STRING(PEAK_KW);
DIV_STRING(PEAK_KW_DATE);
DIV_STRING(PEAK_KW_TIME);
DIV_STRING(LAST_INTERVAL_VOLTAGE);
DIV_STRING(PEAK_VOLTAGE);
DIV_STRING(PEAK_VOLTAGE_DATE);
DIV_STRING(PEAK_VOLTAGE_TIME);
DIV_STRING(MINIMUM_VOLTAGE);
DIV_STRING(MINIMUM_VOLTAGE_DATE);
DIV_STRING(MINIMUM_VOLTAGE_TIME);
DIV_STRING(TOU_RATE_A_KWH);
DIV_STRING(TOU_RATE_A_PEAK_KW);
DIV_STRING(TOU_RATE_A_DATE_OF_PEAK_KW);
DIV_STRING(TOU_RATE_A_TIME_OF_PEAK_KW);
DIV_STRING(TOU_RATE_B_KWH);
DIV_STRING(TOU_RATE_B_PEAK_KW);
DIV_STRING(TOU_RATE_B_DATE_OF_PEAK_KW);
DIV_STRING(TOU_RATE_B_TIME_OF_PEAK_KW);
DIV_STRING(TOU_RATE_C_KWH);
DIV_STRING(TOU_RATE_C_PEAK_KW);
DIV_STRING(TOU_RATE_C_DATE_OF_PEAK_KW);
DIV_STRING(TOU_RATE_C_TIME_OF_PEAK_KW);
DIV_STRING(TOU_RATE_D_KWH);
DIV_STRING(TOU_RATE_D_PEAK_KW);
DIV_STRING(TOU_RATE_D_DATE_OF_PEAK_KW);
DIV_STRING(TOU_RATE_D_TIME_OF_PEAK_KW);

IM_EX_CONFIG const std::map<std::string, uint8_t> Cti::Config::displayItemMap {
    { DIV::SLOT_DISABLED,              slotDisabled         },
    { DIV::NO_SEGMENTS,                noSegments           },
    { DIV::ALL_SEGMENTS,               allSegments          },
    { DIV::CURRENT_LOCAL_TIME,         currentLocalTime     },
    { DIV::CURRENT_LOCAL_DATE,         currentLocalDate     },
    { DIV::TOTAL_KWH,                  totalKwh             },
    { DIV::NET_KWH,                    netKwh               },
    { DIV::DELIVERED_KWH,              deliveredKwh         },
    { DIV::RECEIVED_KWH,               receivedKwh          },
    { DIV::LAST_INTERVAL_KW,           lastIntervalKw       },
    { DIV::PEAK_KW,                    peakKw               },
    { DIV::PEAK_KW_DATE,               peakKwDate           },
    { DIV::PEAK_KW_TIME,               peakKwTime           },
    { DIV::LAST_INTERVAL_VOLTAGE,      lastIntervalVoltage  },
    { DIV::PEAK_VOLTAGE,               peakVoltage          },
    { DIV::PEAK_VOLTAGE_DATE,          peakVoltageDate      },
    { DIV::PEAK_VOLTAGE_TIME,          peakVoltageTime      },
    { DIV::MINIMUM_VOLTAGE,            minimumVoltage       },
    { DIV::MINIMUM_VOLTAGE_DATE,       minimumVoltageDate   },
    { DIV::MINIMUM_VOLTAGE_TIME,       minimumVoltageTime   },
    { DIV::TOU_RATE_A_KWH,             touRateAKwh          },
    { DIV::TOU_RATE_A_PEAK_KW,         touRateAPeakKw       },
    { DIV::TOU_RATE_A_DATE_OF_PEAK_KW, touRateADateOfPeakKw },
    { DIV::TOU_RATE_A_TIME_OF_PEAK_KW, touRateATimeOfPeakKw },
    { DIV::TOU_RATE_B_KWH,             touRateBKwh          },
    { DIV::TOU_RATE_B_PEAK_KW,         touRateBPeakKw       },
    { DIV::TOU_RATE_B_DATE_OF_PEAK_KW, touRateBDateOfPeakKw },
    { DIV::TOU_RATE_B_TIME_OF_PEAK_KW, touRateBTimeOfPeakKw },
    { DIV::TOU_RATE_C_KWH,             touRateCKwh          },
    { DIV::TOU_RATE_C_PEAK_KW,         touRateCPeakKw       },
    { DIV::TOU_RATE_C_DATE_OF_PEAK_KW, touRateCDateOfPeakKw },
    { DIV::TOU_RATE_C_TIME_OF_PEAK_KW, touRateCTimeOfPeakKw },
    { DIV::TOU_RATE_D_KWH,             touRateDKwh          },
    { DIV::TOU_RATE_D_PEAK_KW,         touRateDPeakKw       },
    { DIV::TOU_RATE_D_DATE_OF_PEAK_KW, touRateDDateOfPeakKw },
    { DIV::TOU_RATE_D_TIME_OF_PEAK_KW, touRateDTimeOfPeakKw }
};

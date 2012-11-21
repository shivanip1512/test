#include "precompiled.h"

#include "config_data_cbc.h"
#include "config_data_mct.h"
#include "config_data_dnp.h"

using std::string;

using namespace Cti::Config;

const string Cti::Config::MCTStrings::Bronze = "bronze address";
const string MCTStrings::Lead = "lead address";
const string MCTStrings::Collection = "collection address";
const string MCTStrings::ServiceProviderID = "service provider id";

const string MCTStrings::MondaySchedule = "monday";
const string MCTStrings::TuesdaySchedule = "tuesday";
const string MCTStrings::WednesdaySchedule = "wednesday";
const string MCTStrings::ThursdaySchedule = "thursday";
const string MCTStrings::FridaySchedule = "friday";
const string MCTStrings::SaturdaySchedule = "saturday";
const string MCTStrings::SundaySchedule = "sunday";
const string MCTStrings::HolidaySchedule = "holiday";
const string MCTStrings::DefaultTOURate = "default rate";

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
const string MCTStrings::Schedule1Time10 = "schedule1time10";
const string MCTStrings::Schedule1Rate10 = "schedule1rate10";
const string MCTStrings::Schedule1Time0 = "schedule1time0";
const string MCTStrings::Schedule1Rate0 = "schedule1rate0";

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
const string MCTStrings::Schedule2Time10 = "schedule2time10";
const string MCTStrings::Schedule2Rate10 = "schedule2rate10";
const string MCTStrings::Schedule2Time0 = "schedule2time0";
const string MCTStrings::Schedule2Rate0 = "schedule2rate0";

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
const string MCTStrings::Schedule3Time10 = "schedule3time10";
const string MCTStrings::Schedule3Rate10 = "schedule3rate10";
const string MCTStrings::Schedule3Time0 = "schedule3time0";
const string MCTStrings::Schedule3Rate0 = "schedule3rate0";

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
const string MCTStrings::Schedule4Time10 = "schedule4time10";
const string MCTStrings::Schedule4Rate10 = "schedule4rate10";
const string MCTStrings::Schedule4Time0 = "schedule4time0";
const string MCTStrings::Schedule4Rate0 = "schedule4rate0";

const string MCTStrings::DstBegin = "dst begin";
const string MCTStrings::DstEnd = "dst end";
const string MCTStrings::TimeZoneOffset = "time zone offset";

const string MCTStrings::UnderVoltageThreshold = "under vthreshold";
const string MCTStrings::OverVoltageThreshold = "over vthreshold";

const string MCTStrings::PeakKwResolution1 = "channel 1 peak kw resolution";
const string MCTStrings::LastIntervalDemandResolution1 = "channel 1 last interval demand resolution";
const string MCTStrings::LoadProfileResolution1 = "channel 1 load profile resolution";

const string MCTStrings::PeakKwResolution2 = "channel 2 peak kw resolution";
const string MCTStrings::LastIntervalDemandResolution2 = "channel 2 last interval demand resolution";
const string MCTStrings::LoadProfileResolution2 = "channel 2 load profile resolution";

const string MCTStrings::PeakKwResolution3 = "channel 3 peak kw resolution";
const string MCTStrings::LastIntervalDemandResolution3 = "channel 3 last interval demand resolution";
const string MCTStrings::LoadProfileResolution3 = "channel 3 load profile resolution";

const string MCTStrings::PeakKwResolution4 = "channel 4 peak kw resolution";
const string MCTStrings::LastIntervalDemandResolution4 = "channel 4 last interval demand resolution";
const string MCTStrings::LoadProfileResolution4 = "channel 4 load profile resolution";

const string MCTStrings::DemandInterval = "demand interval";
const string MCTStrings::LoadProfileInterval = "load profile interval 1";
const string MCTStrings::VoltageLPInterval = "voltage lp interval";
const string MCTStrings::VoltageDemandInterval = "demand v interval";
const string MCTStrings::LoadProfileInterval2 = "load profile interval 2";

const string MCTStrings::TimeAdjustTolerance = "time adjust tolerance";
const string MCTStrings::AlarmMaskEvent1 = "alarm mask event 1";
const string MCTStrings::AlarmMaskEvent2 = "alarm mask event 2";
const string MCTStrings::AlarmMaskMeter = "alarm mask meter";

const string MCTStrings::Configuration = "configuration";
const string MCTStrings::Options = "options";
const string MCTStrings::OutageCycles = "outage cycles";

const string MCTStrings::DemandThreshold = "demand threshold";
const string MCTStrings::ConnectDelay = "connect delay";
const string MCTStrings::CyclingDisconnectMinutes = "cycling disconnect minutes";
const string MCTStrings::CyclingConnectMinutes = "cycling connect minutes";

const string MCTStrings::HolidayDate1 = "holiday date 1";
const string MCTStrings::HolidayDate2 = "holiday date 2";
const string MCTStrings::HolidayDate3 = "holiday date 3";

const string MCTStrings::Channel1Length = "channel 1 length";
const string MCTStrings::Channel2Length = "channel 2 length";
const string MCTStrings::Channel3Length = "channel 3 length";
const string MCTStrings::Channel4Length = "channel 4 length";
const string MCTStrings::ChannelConfig1 = "channel config 1";
const string MCTStrings::ChannelConfig2 = "channel config 2";
const string MCTStrings::ChannelConfig3 = "channel config 3";
const string MCTStrings::ChannelConfig4 = "channel config 4";
const string MCTStrings::ChannelMultiplier1 = "channel 1 multiplier";
const string MCTStrings::ChannelMultiplier2 = "channel 2 multiplier";
const string MCTStrings::ChannelMultiplier3 = "channel 3 multiplier";
const string MCTStrings::ChannelMultiplier4 = "channel 4 multiplier";

const string MCTStrings::RelayATimer = "relay a timer";
const string MCTStrings::RelayBTimer = "relay b timer";
const string MCTStrings::TableReadInterval = "table read interval";
const string MCTStrings::MeterNumber = "meter number";
const string MCTStrings::TableType = "table type";
const string MCTStrings::DemandMetersToScan = "demand meters to scan";
const string MCTStrings::DisplayParameters = "display parameters";
const string MCTStrings::TransformerRatio = "transformer ratio";

const string MCTStrings::DNPCollection1BinaryA = "collection 1 binary a";
const string MCTStrings::DNPCollection1BinaryB = "collection 1 binary b";
const string MCTStrings::DNPCollection2BinaryA = "collection 2 binary a";
const string MCTStrings::DNPCollection2BinaryB = "collection 2 binary b";
const string MCTStrings::DNPCollection1Analog = "collection 1 analog";
const string MCTStrings::DNPCollection2Analog = "collection 2 analog";
const string MCTStrings::DNPCollection1Accumulator = "collection 1 accumulator";
const string MCTStrings::DNPCollection2Accumulator = "collection 2 accumulator";
const string MCTStrings::DNPAnalog1 = "analog 1";
const string MCTStrings::DNPAnalog2 = "analog 2";
const string MCTStrings::DNPAnalog3 = "analog 3";
const string MCTStrings::DNPAnalog4 = "analog 4";
const string MCTStrings::DNPAnalog5 = "analog 5";
const string MCTStrings::DNPAnalog6 = "analog 6";
const string MCTStrings::DNPAnalog7 = "analog 7";
const string MCTStrings::DNPAnalog8 = "analog 8";
const string MCTStrings::DNPAnalog9 = "analog 9";
const string MCTStrings::DNPAnalog10 = "analog 10";
const string MCTStrings::DNPAccumulator1 = "accumulator 1";
const string MCTStrings::DNPAccumulator2 = "accumulator 2";
const string MCTStrings::DNPAccumulator3 = "accumulator 3";
const string MCTStrings::DNPAccumulator4 = "accumulator 4";
const string MCTStrings::DNPAccumulator5 = "accumulator 5";
const string MCTStrings::DNPAccumulator6 = "accumulator 6";
const string MCTStrings::DNPAccumulator7 = "accumulator 7";
const string MCTStrings::DNPAccumulator8 = "accumulator 8";
const string MCTStrings::DNPBinaryByte1A = "binary byte 1a";
const string MCTStrings::DNPBinaryByte1B = "binary byte 1b";

const string MCTStrings::PhaseLossThreshold = "phase loss threshold";
const string MCTStrings::PhaseLossDuration  = "phase loss duration";

const string MCTStrings::EnableDST          = "enable dst";

//CBC

const string CBCStrings::ActiveSettings = "active setting";
const string CBCStrings::UVClosePoint = "under voltage close point";
const string CBCStrings::OVTripPoint = "over voltage trip point";
const string CBCStrings::OVUVControlTriggerTime = "over under voltage control trig time";
const string CBCStrings::AdaptiveVoltageHysteresis = "adaptive voltage hysteresis";
const string CBCStrings::AdaptiveVoltageFlag = "adaptive voltage flag";
const string CBCStrings::EmergencyUVPoint = "emergency under voltage point";
const string CBCStrings::EmergencyOVPoint = "emergency over voltage point";
const string CBCStrings::EmergencyVoltageTime = "emergency voltage time";

const string CBCStrings::CommsLostUVClosePoint = "under voltage close point";
const string CBCStrings::CommsLostOVTripPoint = "over voltage trip point";
const string CBCStrings::CommsLostTime = "comms lost time";
const string CBCStrings::CommsLostAction = "comms lost action";

const string CBCStrings::FaultCurrentSetPoint = "fault current set point";
const string CBCStrings::StateChangeSetPoint = "state change set point";
const string CBCStrings::NeutralCurrentRetryCount = "neutral current retry";

const string CBCStrings::FaultDetectionActive = "fault detection active";
const string CBCStrings::AI1AverageTime = "ai1 average time";
const string CBCStrings::AI2AverageTime = "ai2 average time";
const string CBCStrings::AI3AverageTime = "ai3 average time";
const string CBCStrings::AI1PeakSamples = "ai1 peak samples";
const string CBCStrings::AI2PeakSamples = "ai2 peak samples";
const string CBCStrings::AI3PeakSamples = "ai3 peak samples";
const string CBCStrings::AI1RatioThreshold = "ai1 ratio threshold";
const string CBCStrings::AI2RatioThreshold = "ai2 ratio threshold";
const string CBCStrings::AI3RatioThreshold = "ai3 ratio threshold";
const string CBCStrings::BatteryOnTime = "battery on time";

const string CBCStrings::Season1Start = "season start 1";
const string CBCStrings::WeekdayTimedControlClose1 = "weekday timed control close 1";
const string CBCStrings::WeekendTimedControlClose1 = "weekend timed control close 1";
const string CBCStrings::WeekdayTimedControlTrip1 = "weekday timed control trip 1";
const string CBCStrings::WeekendTimedControlTrip1 = "weekend timed control trip 1";
const string CBCStrings::OffTimeState1 = "off time state 1";
const string CBCStrings::TempMinThreshold1 = "temp min threshold 1";
const string CBCStrings::TempMinThresholdAction1 = "temp min threshold action 1";
const string CBCStrings::TempMinHysterisis1 = "temp min hysterisis 1";
const string CBCStrings::TempMinThresholdTrigTime1 = "temp min threshold trig time 1";
const string CBCStrings::TempMaxThreshold1 = "temp max threshold 1";
const string CBCStrings::TempMaxThresholdAction1 = "temp max threshold action 1";
const string CBCStrings::TempMaxHysterisis1 = "temp max hysterisis 1";
const string CBCStrings::TempMaxThresholdTrigTime1 = "temp max threshold trig time 1";

const string CBCStrings::Season2Start = "season start 2";
const string CBCStrings::WeekdayTimedControlClose2 = "weekday timed control close 2";
const string CBCStrings::WeekendTimedControlClose2 = "weekend timed control close 2";
const string CBCStrings::WeekdayTimedControlTrip2 = "weekday timed control trip 2";
const string CBCStrings::WeekendTimedControlTrip2 = "weekend timed control trip 2";
const string CBCStrings::OffTimeState2 = "off time state 2";
const string CBCStrings::TempMinThreshold2 = "temp min threshold 2";
const string CBCStrings::TempMinThresholdAction2 = "temp min threshold action 2";
const string CBCStrings::TempMinHysterisis2 = "temp min hysterisis 2";
const string CBCStrings::TempMinThresholdTrigTime2 = "temp min threshold trig time 2";
const string CBCStrings::TempMaxThreshold2 = "temp max threshold 2";
const string CBCStrings::TempMaxThresholdAction2 = "temp max threshold action 2";
const string CBCStrings::TempMaxHysterisis2 = "temp max hysterisis 2";
const string CBCStrings::TempMaxThresholdTrigTime2 = "temp max threshold trig time 2";

const string CBCStrings::ContactClosureTime = "contact closure time";
const string CBCStrings::ManualControlDelayTrip = "manual control delay trip";
const string CBCStrings::ManualControlDelayClose = "manual control delay close";
const string CBCStrings::RecloseDelayTime = "reclose delay time";

const string CBCStrings::DataLogFlags = "data log flags";
const string CBCStrings::LogTimeInterval = "log time interval";

const string CBCStrings::Geo = "geo address";
const string CBCStrings::Substation = "substation address";
const string CBCStrings::Feeder = "feeder address";
const string CBCStrings::Zip = "zip address";
const string CBCStrings::UserDefined = "user defined address";
const string CBCStrings::Program = "program";
const string CBCStrings::Splinter = "splinter";
const string CBCStrings::RequiredAddressLevel = "required address level";

const string CBCStrings::LineVoltageDeadBand = "line voltage dead band";
const string CBCStrings::DeltaVoltageDeadBand = "delta voltage dead band";
const string CBCStrings::AnalogDeadBand = "analog value dead band";

const string CBCStrings::RetryDelay = "retry delay";
const string CBCStrings::PollTimeout = "poll timeout";

/* DNP */
const string DNPStrings::omitTimeRequest = "Omit Time Request";
const string DNPStrings::internalRetries = "Internal Retries";
const string DNPStrings::enableUnsolicited = "Enable Unsolicited Messages";
const string DNPStrings::enableDnpTimesyncs = "Enable DNP Timesyncs";
const string DNPStrings::useLocalTime = "Local Time";

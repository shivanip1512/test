#include "precompiled.h"

#include "config_data_mct.h"
#include "config_data_dnp.h"
#include "config_data_rfn.h"

using std::string;

using namespace Cti::Config;

const string MCTStrings::Bronze = "bronzeAddress";
const string MCTStrings::Lead = "leadAddress";
const string MCTStrings::Collection = "collectionAddress";
const string MCTStrings::ServiceProviderID = "serviceProviderId";

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
const string MCTStrings::LoadProfileResolution1 = "channel1LoadProfileResolution";

const string MCTStrings::PeakKwResolution2 = "channel2PeakKWResolution";
const string MCTStrings::LastIntervalDemandResolution2 = "channel2LastIntervalDemandResolution";
const string MCTStrings::LoadProfileResolution2 = "channel2LoadProfileResolution";

const string MCTStrings::PeakKwResolution3 = "channel3PeakKWResolution";
const string MCTStrings::LastIntervalDemandResolution3 = "channel3LastIntervalDemandResolution";
const string MCTStrings::LoadProfileResolution3 = "channel3LoadProfileResolution";

const string MCTStrings::PeakKwResolution4 = "channel4PeakKWResolution";
const string MCTStrings::LastIntervalDemandResolution4 = "channel4LastIntervalDemandResolution";
const string MCTStrings::LoadProfileResolution4 = "channel4LoadProfileResolution";

const string MCTStrings::DemandInterval = "demandInterval";
const string MCTStrings::LoadProfileInterval = "loadProfileInterval1";

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

const string MCTStrings::LcdCycleTime = "lcdCycleTime";
const string MCTStrings::DisconnectDisplayDisabled = "disconnectDisplayDisabled";
const string MCTStrings::DisplayDigits = "displayDigits";

const string MCTStrings::DisconnectDemandThreshold = "disconnectDemandThreshold";
const string MCTStrings::DisconnectLoadLimitConnectDelay = "disconnectLoadLimitConnectDelay";
const string MCTStrings::DisconnectMinutes = "disconnectMinutes";
const string MCTStrings::ConnectMinutes = "connectMinutes";
const string MCTStrings::ReconnectButton = "reconnectButton";

/* DNP */
const string DNPStrings::omitTimeRequest = "omitTimeRequest";
const string DNPStrings::internalRetries = "internalRetries";
const string DNPStrings::enableUnsolicited = "enableUnsolicitedMessages";
const string DNPStrings::enableDnpTimesyncs = "enableDnpTimesyncs";
const string DNPStrings::useLocalTime = "localTime";

/* RFN */
const string RfnStrings::displayMetric01 = "displayMetric1";
const string RfnStrings::displayMetric02 = "displayMetric2";
const string RfnStrings::displayMetric03 = "displayMetric3";
const string RfnStrings::displayMetric04 = "displayMetric4";
const string RfnStrings::displayMetric05 = "displayMetric5";
const string RfnStrings::displayMetric06 = "displayMetric6";
const string RfnStrings::displayMetric07 = "displayMetric7";
const string RfnStrings::displayMetric08 = "displayMetric8";
const string RfnStrings::displayMetric09 = "displayMetric9";
const string RfnStrings::displayMetric10 = "displayMetric10";
const string RfnStrings::displayMetric11 = "displayMetric11";
const string RfnStrings::displayMetric12 = "displayMetric12";
const string RfnStrings::displayMetric13 = "displayMetric13";
const string RfnStrings::displayMetric14 = "displayMetric14";
const string RfnStrings::displayMetric15 = "displayMetric15";
const string RfnStrings::displayMetric16 = "displayMetric16";
const string RfnStrings::displayMetric17 = "displayMetric17";
const string RfnStrings::displayMetric18 = "displayMetric18";
const string RfnStrings::displayMetric19 = "displayMetric19";
const string RfnStrings::displayMetric20 = "displayMetric20";
const string RfnStrings::displayMetric21 = "displayMetric21";
const string RfnStrings::displayMetric22 = "displayMetric22";
const string RfnStrings::displayMetric23 = "displayMetric23";
const string RfnStrings::displayMetric24 = "displayMetric24";
const string RfnStrings::displayMetric25 = "displayMetric25";
const string RfnStrings::displayMetric26 = "displayMetric26";

const string RfnStrings::demandInterval         = "demandInterval";
const string RfnStrings::loadProfileInterval    = "loadProfileInterval";

const string RfnStrings::demandFreezeDay    = "demandFreezeDay";

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
const string RfnStrings::Schedule1MidnightRate = "schedule1MidnightRate";
const string RfnStrings::Schedule1Time1        = "schedule1Time1";
const string RfnStrings::Schedule1Rate1        = "schedule1Rate1";
const string RfnStrings::Schedule1Time2        = "schedule1Time2";
const string RfnStrings::Schedule1Rate2        = "schedule1Rate2";
const string RfnStrings::Schedule1Time3        = "schedule1Time3";
const string RfnStrings::Schedule1Rate3        = "schedule1Rate3";
const string RfnStrings::Schedule1Time4        = "schedule1Time4";
const string RfnStrings::Schedule1Rate4        = "schedule1Rate4";
const string RfnStrings::Schedule1Time5        = "schedule1Time5";
const string RfnStrings::Schedule1Rate5        = "schedule1Rate5";

// schedule 2
const string RfnStrings::Schedule2MidnightRate = "schedule2MidnightRate";
const string RfnStrings::Schedule2Time1        = "schedule2Time1";
const string RfnStrings::Schedule2Rate1        = "schedule2Rate1";
const string RfnStrings::Schedule2Time2        = "schedule2Time2";
const string RfnStrings::Schedule2Rate2        = "schedule2Rate2";
const string RfnStrings::Schedule2Time3        = "schedule2Time3";
const string RfnStrings::Schedule2Rate3        = "schedule2Rate3";
const string RfnStrings::Schedule2Time4        = "schedule2Time4";
const string RfnStrings::Schedule2Rate4        = "schedule2Rate4";
const string RfnStrings::Schedule2Time5        = "schedule2Time5";
const string RfnStrings::Schedule2Rate5        = "schedule2Rate5";

// schedule 3
const string RfnStrings::Schedule3MidnightRate = "schedule3MidnightRate";
const string RfnStrings::Schedule3Time1        = "schedule3Time1";
const string RfnStrings::Schedule3Rate1        = "schedule3Rate1";
const string RfnStrings::Schedule3Time2        = "schedule3Time2";
const string RfnStrings::Schedule3Rate2        = "schedule3Rate2";
const string RfnStrings::Schedule3Time3        = "schedule3Time3";
const string RfnStrings::Schedule3Rate3        = "schedule3Rate3";
const string RfnStrings::Schedule3Time4        = "schedule3Time4";
const string RfnStrings::Schedule3Rate4        = "schedule3Rate4";
const string RfnStrings::Schedule3Time5        = "schedule3Time5";
const string RfnStrings::Schedule3Rate5        = "schedule3Rate5";

// schedule 4
const string RfnStrings::Schedule4MidnightRate = "schedule4MidnightRate";
const string RfnStrings::Schedule4Time1        = "schedule4Time1";
const string RfnStrings::Schedule4Rate1        = "schedule4Rate1";
const string RfnStrings::Schedule4Time2        = "schedule4Time2";
const string RfnStrings::Schedule4Rate2        = "schedule4Rate2";
const string RfnStrings::Schedule4Time3        = "schedule4Time3";
const string RfnStrings::Schedule4Rate3        = "schedule4Rate3";
const string RfnStrings::Schedule4Time4        = "schedule4Time4";
const string RfnStrings::Schedule4Rate4        = "schedule4Rate4";
const string RfnStrings::Schedule4Time5        = "schedule4Time5";
const string RfnStrings::Schedule4Rate5        = "schedule4Rate5";
















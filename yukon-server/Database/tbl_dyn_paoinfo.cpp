#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_paoinfo.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"
#include "std_helper.h"

#include <sstream>

#include <boost/assign.hpp>
#include <boost/optional.hpp>
#include <boost/lexical_cast.hpp>

using std::endl;

namespace {

typedef CtiTableDynamicPaoInfo Dpi;

//  !!!  Any changes to these strings will require a DB update - this is what the DB keys on  !!!
typedef boost::bimap<CtiTableDynamicPaoInfo::PaoInfoKeys, std::string> PaoInfoKeyNames;

const PaoInfoKeyNames KeyNames = boost::assign::list_of<PaoInfoKeyNames::relation>
        (Dpi::Key_MCT_SSpec,                  "mct sspec")
        (Dpi::Key_MCT_SSpecRevision,          "mct sspec revision")
        (Dpi::Key_MCT_LoadProfileConfig,      "mct load profile config")
        (Dpi::Key_MCT_LoadProfileInterval,    "mct load profile interval")
        (Dpi::Key_MCT_LoadProfileInterval2,   "mct load profile interval 2")
        (Dpi::Key_MCT_IEDLoadProfileInterval, "mct ied load profile rate")

        (Dpi::Key_FreezeCounter,              "freeze counter")
        (Dpi::Key_FreezeExpected,             "expected freeze")
        (Dpi::Key_VerificationSequence,       "verification sequence")
        (Dpi::Key_RFN_E2eRequestId,            "rfn e2e request id")

        (Dpi::Key_MCT_TimeAdjustTolerance,    "mct time adjust tolerance")
        (Dpi::Key_MCT_DSTStartTime,           "mct dst start time")
        (Dpi::Key_MCT_DSTEndTime,             "mct dst end time")
        (Dpi::Key_MCT_TimeZoneOffset,         "mct time zone offset")
        (Dpi::Key_MCT_OverVoltageThreshold,   "mct over voltage threshold")
        (Dpi::Key_MCT_UnderVoltageThreshold,  "mct under voltage threshold")
        (Dpi::Key_MCT_DemandInterval,         "mct demand interval")
        (Dpi::Key_MCT_VoltageLPInterval,      "mct voltage profile interval")
        (Dpi::Key_MCT_VoltageDemandInterval,  "mct voltage demand interval")
        (Dpi::Key_MCT_ScheduledFreezeDay,     "mct scheduled freeze day")
        (Dpi::Key_MCT_ScheduledFreezeConfigTimestamp, "mct scheduled freeze config timestamp")
        (Dpi::Key_MCT_TouEnabled,             "mct tou enabled")
        (Dpi::Key_MCT_DayTable,               "mct day table")
        (Dpi::Key_MCT_DaySchedule1,           "mct day schedule 1")
        (Dpi::Key_MCT_DaySchedule2,           "mct day schedule 2")
        (Dpi::Key_MCT_DaySchedule3,           "mct day schedule 3")
        (Dpi::Key_MCT_DaySchedule4,           "mct day schedule 4")
        (Dpi::Key_MCT_DefaultTOURate,         "mct default tou rate")
        (Dpi::Key_MCT_AddressBronze,          "mct bronze address")
        (Dpi::Key_MCT_AddressLead,            "mct lead address")
        (Dpi::Key_MCT_AddressCollection,      "mct collection address")
        (Dpi::Key_MCT_AddressServiceProviderID, "mct service provider id")
        (Dpi::Key_MCT_Configuration,          "mct configuration")
        (Dpi::Key_MCT_Options,                "mct options")
        (Dpi::Key_MCT_EventFlagsMask1,        "mct event flags mask 1")
        (Dpi::Key_MCT_EventFlagsMask2,        "mct event flags mask 2")
        (Dpi::Key_MCT_MeterAlarmMask,         "mct meter alarm mask")
        (Dpi::Key_MCT_OutageCycles,           "mct outage cycles")
        (Dpi::Key_MCT_DisconnectMode,         "mct disconnect mode")
        (Dpi::Key_MCT_DemandThreshold,        "mct demand limit")
        (Dpi::Key_MCT_ConnectDelay,           "mct connect delay")
        (Dpi::Key_MCT_DisconnectMinutes,      "mct disconnect minutes")
        (Dpi::Key_MCT_ConnectMinutes,         "mct connect minutes")
        (Dpi::Key_MCT_Holiday1,               "mct holiday 1")
        (Dpi::Key_MCT_Holiday2,               "mct holiday 2")
        (Dpi::Key_MCT_Holiday3,               "mct holiday 3")
        (Dpi::Key_MCT_LLPChannel1Len,         "mct llp channel 1 length")
        (Dpi::Key_MCT_LLPChannel2Len,         "mct llp channel 2 length")
        (Dpi::Key_MCT_LLPChannel3Len,         "mct llp channel 3 length")
        (Dpi::Key_MCT_LLPChannel4Len,         "mct llp channel 4 length")

        (Dpi::Key_MCT_LoadProfileChannelConfig1, "mct load profile channel 1 config")
        (Dpi::Key_MCT_LoadProfileChannelConfig2, "mct load profile channel 2 config")
        (Dpi::Key_MCT_LoadProfileChannelConfig3, "mct load profile channel 3 config")
        (Dpi::Key_MCT_LoadProfileChannelConfig4, "mct load profile channel 4 config")
        (Dpi::Key_MCT_LoadProfileMeterRatio1,    "mct load profile meter ratio 1")
        (Dpi::Key_MCT_LoadProfileMeterRatio2,    "mct load profile meter ratio 2")
        (Dpi::Key_MCT_LoadProfileMeterRatio3,    "mct load profile meter ratio 3")
        (Dpi::Key_MCT_LoadProfileMeterRatio4,    "mct load profile meter ratio 4")
        (Dpi::Key_MCT_LoadProfileKRatio1,        "mct load profile k ratio 1")
        (Dpi::Key_MCT_LoadProfileKRatio2,        "mct load profile k ratio 2")
        (Dpi::Key_MCT_LoadProfileKRatio3,        "mct load profile k ratio 3")
        (Dpi::Key_MCT_LoadProfileKRatio4,        "mct load profile k ratio 4")

        (Dpi::Key_MCT_RelayATimer,               "mct relay a timer")
        (Dpi::Key_MCT_RelayBTimer,               "mct relay b timer")

        (Dpi::Key_MCT_DisplayParameters,         "mct display parameters")
        (Dpi::Key_MCT_TransformerRatio,          "mct transformer ratio")

        (Dpi::Key_MCT_PrecannedTableReadInterval, "mct precanned table read interval")
        (Dpi::Key_MCT_PrecannedMeterNumber,       "mct precanned meter number")
        (Dpi::Key_MCT_PrecannedTableType,         "mct precanned table type")

        (Dpi::Key_MCT_LLPInterest_Time,         "mct llp interest time")
        (Dpi::Key_MCT_LLPInterest_Channel,      "mct llp interest channel")
        (Dpi::Key_MCT_LLPInterest_RequestBegin, "mct llp interest request begin")
        (Dpi::Key_MCT_LLPInterest_RequestEnd,   "mct llp interest request end")

        (Dpi::Key_MCT_LoadProfilePeakReportTimestamp, "mct load profile peak report timestamp")

        (Dpi::Key_MCT_DailyReadInterestChannel, "mct daily read interest channel")

        (Dpi::Key_MCT_DNP_AccumulatorCRC1, "mct dnp accumulator crc1")
        (Dpi::Key_MCT_DNP_AccumulatorCRC2, "mct dnp accumulator crc2")
        (Dpi::Key_MCT_DNP_AccumulatorCRC3, "mct dnp accumulator crc3")
        (Dpi::Key_MCT_DNP_AccumulatorCRC4, "mct dnp accumulator crc4")
        (Dpi::Key_MCT_DNP_AnalogCRC1,      "mct dnp analog crc1")
        (Dpi::Key_MCT_DNP_AnalogCRC2,      "mct dnp analog crc2")
        (Dpi::Key_MCT_DNP_AnalogCRC3,      "mct dnp analog crc3")
        (Dpi::Key_MCT_DNP_AnalogCRC4,      "mct dnp analog crc4")
        (Dpi::Key_MCT_DNP_AnalogCRC5,      "mct dnp analog crc5")
        (Dpi::Key_MCT_DNP_RealTime1CRC,    "mct dnp realtime1 crc")
        (Dpi::Key_MCT_DNP_RealTime2CRC,    "mct dnp realtime2 crc")
        (Dpi::Key_MCT_DNP_BinaryCRC,       "mct dnp binary crc")

        (Dpi::Key_MCT_LcdMetric01,         "mct lcd metric 01")
        (Dpi::Key_MCT_LcdMetric02,         "mct lcd metric 02")
        (Dpi::Key_MCT_LcdMetric03,         "mct lcd metric 03")
        (Dpi::Key_MCT_LcdMetric04,         "mct lcd metric 04")
        (Dpi::Key_MCT_LcdMetric05,         "mct lcd metric 05")
        (Dpi::Key_MCT_LcdMetric06,         "mct lcd metric 06")
        (Dpi::Key_MCT_LcdMetric07,         "mct lcd metric 07")
        (Dpi::Key_MCT_LcdMetric08,         "mct lcd metric 08")
        (Dpi::Key_MCT_LcdMetric09,         "mct lcd metric 09")
        (Dpi::Key_MCT_LcdMetric10,         "mct lcd metric 10")
        (Dpi::Key_MCT_LcdMetric11,         "mct lcd metric 11")
        (Dpi::Key_MCT_LcdMetric12,         "mct lcd metric 12")
        (Dpi::Key_MCT_LcdMetric13,         "mct lcd metric 13")
        (Dpi::Key_MCT_LcdMetric14,         "mct lcd metric 14")
        (Dpi::Key_MCT_LcdMetric15,         "mct lcd metric 15")
        (Dpi::Key_MCT_LcdMetric16,         "mct lcd metric 16")
        (Dpi::Key_MCT_LcdMetric17,         "mct lcd metric 17")
        (Dpi::Key_MCT_LcdMetric18,         "mct lcd metric 18")
        (Dpi::Key_MCT_LcdMetric19,         "mct lcd metric 19")
        (Dpi::Key_MCT_LcdMetric20,         "mct lcd metric 20")
        (Dpi::Key_MCT_LcdMetric21,         "mct lcd metric 21")
        (Dpi::Key_MCT_LcdMetric22,         "mct lcd metric 22")
        (Dpi::Key_MCT_LcdMetric23,         "mct lcd metric 23")
        (Dpi::Key_MCT_LcdMetric24,         "mct lcd metric 24")
        (Dpi::Key_MCT_LcdMetric25,         "mct lcd metric 25")
        (Dpi::Key_MCT_LcdMetric26,         "mct lcd metric 26")

        (Dpi::Key_MCT_WaterMeterReadInterval, "mct water meter read interval")

        (Dpi::Key_FrozenRateAPeakTimestamp,   "frozen rate a peak timestamp")
        (Dpi::Key_FrozenRateBPeakTimestamp,   "frozen rate b peak timestamp")
        (Dpi::Key_FrozenRateCPeakTimestamp,   "frozen rate c peak timestamp")
        (Dpi::Key_FrozenRateDPeakTimestamp,   "frozen rate d peak timestamp")
        (Dpi::Key_FrozenDemandPeakTimestamp,  "frozen demand peak timestamp")
        (Dpi::Key_FrozenDemand2PeakTimestamp, "frozen channel 2 demand peak timestamp")
        (Dpi::Key_FrozenDemand3PeakTimestamp, "frozen channel 3 demand peak timestamp")
        (Dpi::Key_DemandFreezeTimestamp,      "demand freeze timestamp")
        (Dpi::Key_VoltageFreezeTimestamp,     "voltage freeze timestamp")

        (Dpi::Key_UDP_IP,       "udp ip")
        (Dpi::Key_UDP_Port,     "udp port")
        (Dpi::Key_UDP_Sequence, "udp sequence")

        (Dpi::Key_LCR_SSpec,         "lcr sspec")
        (Dpi::Key_LCR_SSpecRevision, "lcr sspec revision")
        (Dpi::Key_LCR_SerialAddress, "lcr serial address")
        (Dpi::Key_LCR_Spid,          "lcr ssid")
        (Dpi::Key_LCR_GeoAddress,    "lcr geo address")
        (Dpi::Key_LCR_Substation,    "lcr substation")
        (Dpi::Key_LCR_Feeder,        "lcr feeder")
        (Dpi::Key_LCR_ZipCode,       "lcr zip code")
        (Dpi::Key_LCR_Uda,           "lcr uda")
        (Dpi::Key_LCR_ProgramAddressRelay1,  "lcr relay 1 program address")
        (Dpi::Key_LCR_ProgramAddressRelay2,  "lcr relay 2 program address")
        (Dpi::Key_LCR_ProgramAddressRelay3,  "lcr relay 3 program address")
        (Dpi::Key_LCR_ProgramAddressRelay4,  "lcr relay 4 program address")
        (Dpi::Key_LCR_SplinterAddressRelay1, "lcr relay 1 splinter address")
        (Dpi::Key_LCR_SplinterAddressRelay2, "lcr relay 2 splinter address")
        (Dpi::Key_LCR_SplinterAddressRelay3, "lcr relay 3 splinter address")
        (Dpi::Key_LCR_SplinterAddressRelay4, "lcr relay 4 splinter address")

        (Dpi::Key_RPT_SSpec,         "rpt sspec")
        (Dpi::Key_RPT_SSpecRevision, "rpt sspec revision")

        (Dpi::Key_MCT_Holiday4,    "mct holiday 4")
        (Dpi::Key_MCT_Holiday5,    "mct holiday 5")
        (Dpi::Key_MCT_Holiday6,    "mct holiday 6")
        (Dpi::Key_MCT_Holiday7,    "mct holiday 7")
        (Dpi::Key_MCT_Holiday8,    "mct holiday 8")
        (Dpi::Key_MCT_Holiday9,    "mct holiday 9")
        (Dpi::Key_MCT_Holiday10,   "mct holiday 10")
        (Dpi::Key_MCT_Holiday11,   "mct holiday 11")
        (Dpi::Key_MCT_Holiday12,   "mct holiday 12")
        (Dpi::Key_MCT_Holiday13,   "mct holiday 13")
        (Dpi::Key_MCT_Holiday14,   "mct holiday 14")
        (Dpi::Key_MCT_Holiday15,   "mct holiday 15")
        (Dpi::Key_MCT_Holiday16,   "mct holiday 16")
        (Dpi::Key_MCT_Holiday17,   "mct holiday 17")
        (Dpi::Key_MCT_Holiday18,   "mct holiday 18")
        (Dpi::Key_MCT_Holiday19,   "mct holiday 19")
        (Dpi::Key_MCT_Holiday20,   "mct holiday 20")
        (Dpi::Key_MCT_Holiday21,   "mct holiday 21")
        (Dpi::Key_MCT_Holiday22,   "mct holiday 22")
        (Dpi::Key_MCT_Holiday23,   "mct holiday 23")
        (Dpi::Key_MCT_Holiday24,   "mct holiday 24")
        (Dpi::Key_MCT_Holiday25,   "mct holiday 25")
        (Dpi::Key_MCT_Holiday26,   "mct holiday 26")
        (Dpi::Key_MCT_Holiday27,   "mct holiday 27")
        (Dpi::Key_MCT_Holiday28,   "mct holiday 28")

        (Dpi::Key_MCT_PhaseLossPercent, "mct phase loss percent")
        (Dpi::Key_MCT_PhaseLossSeconds, "mct phase loss seconds")

        (Dpi::Key_DisplayItem01, "display item 01")
        (Dpi::Key_DisplayItem02, "display item 02")
        (Dpi::Key_DisplayItem03, "display item 03")
        (Dpi::Key_DisplayItem04, "display item 04")
        (Dpi::Key_DisplayItem05, "display item 05")
        (Dpi::Key_DisplayItem06, "display item 06")
        (Dpi::Key_DisplayItem07, "display item 07")
        (Dpi::Key_DisplayItem08, "display item 08")
        (Dpi::Key_DisplayItem09, "display item 09")
        (Dpi::Key_DisplayItem10, "display item 10")
        (Dpi::Key_DisplayItem11, "display item 11")
        (Dpi::Key_DisplayItem12, "display item 12")
        (Dpi::Key_DisplayItem13, "display item 13")
        (Dpi::Key_DisplayItem14, "display item 14")
        (Dpi::Key_DisplayItem15, "display item 15")
        (Dpi::Key_DisplayItem16, "display item 16")
        (Dpi::Key_DisplayItem17, "display item 17")
        (Dpi::Key_DisplayItem18, "display item 18")
        (Dpi::Key_DisplayItem19, "display item 19")
        (Dpi::Key_DisplayItem20, "display item 20")
        (Dpi::Key_DisplayItem21, "display item 21")
        (Dpi::Key_DisplayItem22, "display item 22")
        (Dpi::Key_DisplayItem23, "display item 23")
        (Dpi::Key_DisplayItem24, "display item 24")
        (Dpi::Key_DisplayItem25, "display item 25")
        (Dpi::Key_DisplayItem26, "display item 26")

        (Dpi::Key_RFN_TouEnabled,         "rfn tou enabled")

        (Dpi::Key_RFN_MondaySchedule,     "rfn monday schedule")
        (Dpi::Key_RFN_TuesdaySchedule,    "rfn tuesday schedule")
        (Dpi::Key_RFN_WednesdaySchedule,  "rfn wednesday schedule")
        (Dpi::Key_RFN_ThursdaySchedule,   "rfn thursday schedule")
        (Dpi::Key_RFN_FridaySchedule,     "rfn friday schedule")
        (Dpi::Key_RFN_SaturdaySchedule,   "rfn saturday schedule")
        (Dpi::Key_RFN_SundaySchedule,     "rfn sunday schedule")
        (Dpi::Key_RFN_HolidaySchedule,    "rfn holiday schedule")

        (Dpi::Key_RFN_DefaultTOURate,     "rfn default tou rate")

        (Dpi::Key_RFN_Schedule1Rate0,     "rfn schedule 1 rate 0")
        (Dpi::Key_RFN_Schedule1Time1,     "rfn schedule 1 time 1")
        (Dpi::Key_RFN_Schedule1Rate1,     "rfn schedule 1 rate 1")
        (Dpi::Key_RFN_Schedule1Time2,     "rfn schedule 1 time 2")
        (Dpi::Key_RFN_Schedule1Rate2,     "rfn schedule 1 rate 2")
        (Dpi::Key_RFN_Schedule1Time3,     "rfn schedule 1 time 3")
        (Dpi::Key_RFN_Schedule1Rate3,     "rfn schedule 1 rate 3")
        (Dpi::Key_RFN_Schedule1Time4,     "rfn schedule 1 time 4")
        (Dpi::Key_RFN_Schedule1Rate4,     "rfn schedule 1 rate 4")
        (Dpi::Key_RFN_Schedule1Time5,     "rfn schedule 1 time 5")
        (Dpi::Key_RFN_Schedule1Rate5,     "rfn schedule 1 rate 5")

        (Dpi::Key_RFN_Schedule2Rate0,     "rfn schedule 2 rate 0")
        (Dpi::Key_RFN_Schedule2Time1,     "rfn schedule 2 time 1")
        (Dpi::Key_RFN_Schedule2Rate1,     "rfn schedule 2 rate 1")
        (Dpi::Key_RFN_Schedule2Time2,     "rfn schedule 2 time 2")
        (Dpi::Key_RFN_Schedule2Rate2,     "rfn schedule 2 rate 2")
        (Dpi::Key_RFN_Schedule2Time3,     "rfn schedule 2 time 3")
        (Dpi::Key_RFN_Schedule2Rate3,     "rfn schedule 2 rate 3")
        (Dpi::Key_RFN_Schedule2Time4,     "rfn schedule 2 time 4")
        (Dpi::Key_RFN_Schedule2Rate4,     "rfn schedule 2 rate 4")
        (Dpi::Key_RFN_Schedule2Time5,     "rfn schedule 2 time 5")
        (Dpi::Key_RFN_Schedule2Rate5,     "rfn schedule 2 rate 5")

        (Dpi::Key_RFN_Schedule3Rate0,     "rfn schedule 3 rate 0")
        (Dpi::Key_RFN_Schedule3Time1,     "rfn schedule 3 time 1")
        (Dpi::Key_RFN_Schedule3Rate1,     "rfn schedule 3 rate 1")
        (Dpi::Key_RFN_Schedule3Time2,     "rfn schedule 3 time 2")
        (Dpi::Key_RFN_Schedule3Rate2,     "rfn schedule 3 rate 2")
        (Dpi::Key_RFN_Schedule3Time3,     "rfn schedule 3 time 3")
        (Dpi::Key_RFN_Schedule3Rate3,     "rfn schedule 3 rate 3")
        (Dpi::Key_RFN_Schedule3Time4,     "rfn schedule 3 time 4")
        (Dpi::Key_RFN_Schedule3Rate4,     "rfn schedule 3 rate 4")
        (Dpi::Key_RFN_Schedule3Time5,     "rfn schedule 3 time 5")
        (Dpi::Key_RFN_Schedule3Rate5,     "rfn schedule 3 rate 5")

        (Dpi::Key_RFN_Schedule4Rate0,     "rfn schedule 4 rate 0")
        (Dpi::Key_RFN_Schedule4Time1,     "rfn schedule 4 time 1")
        (Dpi::Key_RFN_Schedule4Rate1,     "rfn schedule 4 rate 1")
        (Dpi::Key_RFN_Schedule4Time2,     "rfn schedule 4 time 2")
        (Dpi::Key_RFN_Schedule4Rate2,     "rfn schedule 4 rate 2")
        (Dpi::Key_RFN_Schedule4Time3,     "rfn schedule 4 time 3")
        (Dpi::Key_RFN_Schedule4Rate3,     "rfn schedule 4 rate 3")
        (Dpi::Key_RFN_Schedule4Time4,     "rfn schedule 4 time 4")
        (Dpi::Key_RFN_Schedule4Rate4,     "rfn schedule 4 rate 4")
        (Dpi::Key_RFN_Schedule4Time5,     "rfn schedule 4 time 5")
        (Dpi::Key_RFN_Schedule4Rate5,     "rfn schedule 4 rate 5")

        (Dpi::Key_RFN_Holiday1,           "rfn holiday 1")
        (Dpi::Key_RFN_Holiday2,           "rfn holiday 2")
        (Dpi::Key_RFN_Holiday3,           "rfn holiday 3")

        (Dpi::Key_RFN_VoltageAveragingInterval,   "rfn voltage averaging interval")
        (Dpi::Key_RFN_DemandInterval,             "rfn demand interval")
        (Dpi::Key_RFN_LoadProfileInterval,        "rfn load profile interval")
        (Dpi::Key_RFN_VoltageProfileEnabled,      "rfn voltage profile enabled")
        (Dpi::Key_RFN_VoltageProfileEnabledUntil, "rfn voltage profile enabled until")

        (Dpi::Key_RFN_DemandFreezeDay,    "rfn demand freeze day")

        (Dpi::Key_RFN_OvUvEnabled,                   "rfn ovuv enabled" )
        (Dpi::Key_RFN_OvThreshold,                   "rfn ovuv ov threshold" )
        (Dpi::Key_RFN_UvThreshold,                   "rfn ovuv uv threshold" )
        (Dpi::Key_RFN_OvUvAlarmReportingInterval,    "rfn ovuv alarm reporting interval" )
        (Dpi::Key_RFN_OvUvAlarmRepeatInterval,       "rfn ovuv alarm repeating interval" )
        (Dpi::Key_RFN_OvUvRepeatCount,               "rfn ovuv alarm repeat count" )

        (Dpi::Key_RFN_LcdCycleTime,                  "rfn lcd cycle time" )
        (Dpi::Key_RFN_LcdDisconnectDisplayDisabled,  "rfn lcd disconnect display disabled" )
        (Dpi::Key_RFN_LcdDisplayDigits,              "rfn lcd display digits" )

        (Dpi::Key_RFN_DisconnectMode,            "rfn disconnect mode")
        (Dpi::Key_RFN_ReconnectParam,            "rfn reconnect param")
        (Dpi::Key_RFN_DisconnectDemandInterval,  "rfn disconnect demand interval")
        (Dpi::Key_RFN_DemandThreshold,           "rfn demand threshold")
        (Dpi::Key_RFN_ConnectDelay,              "rfn connect delay")
        (Dpi::Key_RFN_MaxDisconnects,            "rfn max disconnects")
        (Dpi::Key_RFN_DisconnectMinutes,         "rfn disconnect minutes")
        (Dpi::Key_RFN_ConnectMinutes,            "rfn connect minutes")

        (Dpi::Key_RFN_TempAlarmUnsupported,         "rfn temp alarm unsupported" )
        (Dpi::Key_RFN_TempAlarmIsEnabled,           "rfn temp alarm enabled" )
        (Dpi::Key_RFN_TempAlarmRepeatInterval,      "rfn temp alarm repeat interval" )
        (Dpi::Key_RFN_TempAlarmRepeatCount,         "rfn temp alarm repeat count" )
        (Dpi::Key_RFN_TempAlarmHighTempThreshold,   "rfn temp alarm high temp threshold" )

        (Dpi::Key_RF_DA_DnpSlaveAddress, "rf da dnp slave address")

        (Dpi::Key_RFN_RecordingIntervalSeconds, "rfn recording interval seconds")
        (Dpi::Key_RFN_ReportingIntervalSeconds, "rfn reporting interval seconds")
            
        (Dpi::Key_RFN_MeterProgrammingProgress,         "rfn meter programming progress")
        ;

typedef CtiTableDynamicPaoInfoIndexed DpiIndexed;

//  !!!  Any changes to these strings will require a DB update - this is what the DB keys on  !!!
typedef boost::bimap<CtiTableDynamicPaoInfoIndexed::PaoInfoKeysIndexed, std::string> PaoInfoKeyNamesIndexed;

const PaoInfoKeyNamesIndexed KeyNamesIndexed = boost::assign::list_of<PaoInfoKeyNamesIndexed::relation>
        (DpiIndexed::Key_RFN_MidnightMetrics, "rfn midnight metrics")
        (DpiIndexed::Key_RFN_IntervalMetrics, "rfn interval metrics")
        ;

} // anonymous

/*-----------------------------------------------------------------------------
    CtiTableDynamicPaoInfoBase Class
-----------------------------------------------------------------------------*/

CtiTableDynamicPaoInfoBase::CtiTableDynamicPaoInfoBase()                                 : _fromDb(false), _pao_id(-1) {}
CtiTableDynamicPaoInfoBase::CtiTableDynamicPaoInfoBase(long paoid, int value)            : _fromDb(false), _pao_id(paoid), _value(CtiNumStr(value)) {}
CtiTableDynamicPaoInfoBase::CtiTableDynamicPaoInfoBase(long paoid, unsigned int value)   : _fromDb(false), _pao_id(paoid), _value(CtiNumStr(value)) {}
CtiTableDynamicPaoInfoBase::CtiTableDynamicPaoInfoBase(long paoid, long value)           : _fromDb(false), _pao_id(paoid), _value(CtiNumStr(value)) {}
CtiTableDynamicPaoInfoBase::CtiTableDynamicPaoInfoBase(long paoid, unsigned long value)  : _fromDb(false), _pao_id(paoid), _value(CtiNumStr(value)) {}
CtiTableDynamicPaoInfoBase::CtiTableDynamicPaoInfoBase(long paoid, double value)         : _fromDb(false), _pao_id(paoid), _value(CtiNumStr(value)) {}
CtiTableDynamicPaoInfoBase::CtiTableDynamicPaoInfoBase(long paoid, std::string value)    : _fromDb(false), _pao_id(paoid), _value(value) {}

bool CtiTableDynamicPaoInfoBase::Insert(Cti::Database::DatabaseConnection &conn, const std::string &owner)
{
    std::string keyString = getKeyString();

    if( getPaoID() < 0 || owner.empty() || keyString.empty() || _value.empty() )
    {
        CTILOG_ERROR(dout, "invalid attempt to insert into DynamicPaoInfo - paoid = "<< getPaoID() <<", owner = \""<< owner <<"\", keyString = \""<< keyString <<"\", and value = \"" << _value << "\"");

        return false;
    }

    static const std::string sql = "insert into DynamicPaoInfo values (?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter
        << getPaoID()
        << owner
        << keyString
        << _value
        << CtiTime();

    if( ! Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug( isDebugLudicrous() )))
    {
        return false;
    }

    setFromDb();

    return true; // No error occurred!
}

bool CtiTableDynamicPaoInfoBase::Update(Cti::Database::DatabaseConnection &conn, const std::string &owner)
{
    std::string keyString = getKeyString();

    if( getPaoID() < 0 || owner.empty() || keyString.empty() || _value.empty() )
    {
        CTILOG_ERROR(dout, "invalid attempt to update DynamicPaoInfo - paoid = "<< getPaoID() <<", owner = \""<< owner <<"\", keyString = \""<< keyString <<"\", and value = \"" << _value << "\"");

        return false;
    }

    static const std::string sql =
        "update DynamicPaoInfo"
        " set "
            "value = ?, "
            "updatetime = ?"
        " where "
            "paobjectid = ? and "
            "owner = ? and "
            "infokey = ?";

    Cti::Database::DatabaseWriter updater(conn, sql);

    updater
        << _value
        << CtiTime()
        << getPaoID()
        << owner
        << keyString;

    if( ! Cti::Database::executeCommand(updater, CALLSITE) )
    {
        return false;
    }

    if( ! updater.rowsAffected() )
    {
        return false;
    }

    return true; // No error occurred!
}

std::string CtiTableDynamicPaoInfoBase::getSQLCoreStatement()
{
    static const std::string sql =
            "SELECT DPI.owner, DPI.paobjectid, DPI.infokey, DPI.value "
            "FROM DynamicPaoInfo DPI ";

    return sql;
}

long CtiTableDynamicPaoInfoBase::getPaoID() const
{
    return _pao_id;
}

std::string CtiTableDynamicPaoInfoBase::getValue() const
{
    return _value;
}

bool CtiTableDynamicPaoInfoBase::isFromDb() const
{
    return _fromDb;
}

void CtiTableDynamicPaoInfoBase::setFromDb()
{
    _fromDb = true;
}

//  these may need to become individually named get functions, if the assignment idiom doesn't work out
void CtiTableDynamicPaoInfoBase::getValue(int &destination) const
{
    destination = atoi(_value.c_str());
}

void CtiTableDynamicPaoInfoBase::getValue(unsigned int &destination) const
{
    double tmp;
    getValue(tmp);

    destination = std::max<unsigned>(tmp, 0U);
}

void CtiTableDynamicPaoInfoBase::getValue(long &destination) const
{
    destination = atol(_value.c_str());
}

void CtiTableDynamicPaoInfoBase::getValue(unsigned long &destination) const
{
    double tmp;
    getValue(tmp);

    destination = (tmp >= 0) ? static_cast<unsigned long>(tmp) : 0UL;
}

void CtiTableDynamicPaoInfoBase::getValue(double &destination) const
{
    destination = atof(_value.c_str());
}

void CtiTableDynamicPaoInfoBase::getValue(std::string &destination) const
{
    destination = _value;
}

/*-----------------------------------------------------------------------------
    CtiTableDynamicPaoInfo Class
-----------------------------------------------------------------------------*/

CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, int value)            : CtiTableDynamicPaoInfoBase(paoid, value), _key(k) {}
CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, unsigned int value)   : CtiTableDynamicPaoInfoBase(paoid, value), _key(k) {}
CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, long value)           : CtiTableDynamicPaoInfoBase(paoid, value), _key(k) {}
CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, unsigned long value)  : CtiTableDynamicPaoInfoBase(paoid, value), _key(k) {}
CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, double value)         : CtiTableDynamicPaoInfoBase(paoid, value), _key(k) {}
CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, std::string value)    : CtiTableDynamicPaoInfoBase(paoid, value), _key(k) {}

CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(Cti::RowReader& rdr)
    :   _key(Key_Invalid)
{
    setFromDb();

    std::string tmp_keyString;

    rdr["paobjectid"] >> _pao_id;
    rdr["value"]      >> _value;
    rdr["infokey"]    >> tmp_keyString;

    boost::optional<PaoInfoKeys> resolvedKey = Cti::bimapFind<PaoInfoKeys>(KeyNames.right, tmp_keyString);

    if( ! resolvedKey )
    {
        std::string tmp_OwnerString;

        rdr["owner"] >> tmp_OwnerString;

        throw BadKeyException(_pao_id, tmp_keyString, tmp_OwnerString);
    }

    _key = *resolvedKey;
}

CtiTableDynamicPaoInfo::PaoInfoKeys CtiTableDynamicPaoInfo::getKey() const
{
    return _key;
}

std::string CtiTableDynamicPaoInfo::getKeyString() const
{
    return getKeyString(_key);
}

std::string CtiTableDynamicPaoInfo::getKeyString(const PaoInfoKeys key)
{
    if( const boost::optional<std::string> keyString = Cti::bimapFind<std::string>(KeyNames.left, key) )
    {
        return *keyString;
    }

    return std::string();
}

std::string CtiTableDynamicPaoInfo::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableDynamicPaoInfo";
    itemList.add("getPaoID()")     << getPaoID();
    itemList.add("getKeyString()") << getKeyString();
    itemList.add("getKey()")       << getKey();
    itemList.add("getValue()")     << getValue();

    return itemList.toString();
}

/*-----------------------------------------------------------------------------
    CtiTableDynamicPaoInfoIndexed Class
-----------------------------------------------------------------------------*/

CtiTableDynamicPaoInfoIndexed::CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned long numberOfindex)          : CtiTableDynamicPaoInfoBase(paoid, numberOfindex), _key(k) {}
CtiTableDynamicPaoInfoIndexed::CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, int value)            : CtiTableDynamicPaoInfoBase(paoid, value), _key(k), _index(index) {}
CtiTableDynamicPaoInfoIndexed::CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, unsigned int value)   : CtiTableDynamicPaoInfoBase(paoid, value), _key(k), _index(index) {}
CtiTableDynamicPaoInfoIndexed::CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, long value)           : CtiTableDynamicPaoInfoBase(paoid, value), _key(k), _index(index) {}
CtiTableDynamicPaoInfoIndexed::CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, unsigned long value)  : CtiTableDynamicPaoInfoBase(paoid, value), _key(k), _index(index) {}
CtiTableDynamicPaoInfoIndexed::CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, double value)         : CtiTableDynamicPaoInfoBase(paoid, value), _key(k), _index(index) {}
CtiTableDynamicPaoInfoIndexed::CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, std::string value)    : CtiTableDynamicPaoInfoBase(paoid, value), _key(k), _index(index) {}

CtiTableDynamicPaoInfoIndexed::CtiTableDynamicPaoInfoIndexed(Cti::RowReader& rdr)
    :   _key(Key_Invalid)
{
    setFromDb();

    std::string tmp_keyString;

    rdr["paobjectid"] >> _pao_id;
    rdr["value"]      >> _value;
    rdr["infokey"]    >> tmp_keyString;

    // try to resolve the indexed base key
    boost::optional<PaoInfoKeysIndexed> resolvedKey = Cti::bimapFind<PaoInfoKeysIndexed>(KeyNamesIndexed.right, tmp_keyString);

    if( ! resolvedKey )
    {
        // this could be an indexed value

        // look for the last space character expected to precede digits
        const unsigned pos = tmp_keyString.find_last_of(' ');

        // make sure the position, if found, is not the first nor the last character
        if( pos != std::string::npos && pos > 0 && pos+1 < tmp_keyString.length() )
        {
            try
            {
                const int index = boost::lexical_cast<int>( tmp_keyString.substr(pos+1) );

                if( index >= 0 )
                {
                    _index = index;

                    // only try to resolve the key if the index is valid
                    resolvedKey = Cti::bimapFind<PaoInfoKeysIndexed>(KeyNamesIndexed.right, tmp_keyString.substr(0, pos));
                }
            }
            catch( boost::bad_lexical_cast& )
            {
                // let it fall through: resolvedKey is expected to be boost::none
            }
        }

        if( ! resolvedKey )
        {
            std::string tmp_OwnerString;

            rdr["owner"] >> tmp_OwnerString;

            throw CtiTableDynamicPaoInfo::BadKeyException(_pao_id, tmp_keyString, tmp_OwnerString);
        }
    }

    _key = *resolvedKey;
}

CtiTableDynamicPaoInfoIndexed::PaoInfoKeysIndexed CtiTableDynamicPaoInfoIndexed::getKey() const
{
    return _key;
}

boost::optional<unsigned> CtiTableDynamicPaoInfoIndexed::getIndex() const
{
    return _index;
}

std::string CtiTableDynamicPaoInfoIndexed::getKeyString() const
{
    std::string keyString = getKeyString(_key);

    if( ! keyString.empty() && _index )
    {
        // insert space character before digits
        keyString += " " + boost::lexical_cast<std::string>(*_index);
    }

    return keyString;
}

std::string CtiTableDynamicPaoInfoIndexed::getKeyString(const PaoInfoKeysIndexed key)
{
    if( const boost::optional<std::string> keyString = Cti::bimapFind<std::string>(KeyNamesIndexed.left, key) )
    {
        return *keyString;
    }

    return std::string();
}

std::string CtiTableDynamicPaoInfoIndexed::toString() const
{
    const boost::optional<unsigned> index = getIndex();

    Cti::FormattedList itemList;

    itemList <<"CtiTableDynamicPaoInfoIndexed";
    itemList.add("getPaoID()")     << getPaoID();
    itemList.add("getKeyString()") << getKeyString();
    itemList.add("getKey()")       << getKey();
    itemList.add("getValue()")     << getValue();
    itemList.add("getIndex()")     << (index ? boost::lexical_cast<std::string>(*index) : std::string("none"));

    return itemList.toString();
}

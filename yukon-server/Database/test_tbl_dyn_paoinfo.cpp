#include <boost/test/auto_unit_test.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/multi_array.hpp>

#include "tbl_dyn_paoinfo.h"
#include "boostutil.h"

BOOST_AUTO_TEST_SUITE( test_tbl_dyn_paoinfo )

BOOST_AUTO_TEST_CASE(test_getKeyString)
{
    //
    // verify non-indexed keys
    //
    struct test_case
    {
        CtiTableDynamicPaoInfo::PaoInfoKeys key;
        std::string keyAsString;
    }
    const test_cases[] =
    {
        { CtiTableDynamicPaoInfo::Key_Invalid,                               "" },
        { CtiTableDynamicPaoInfo::Key_MCT_SSpec,                             "mct sspec" },
        { CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision,                     "mct sspec revision" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig,                 "mct load profile config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval,               "mct load profile interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2,              "mct load profile interval 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval,            "mct ied load profile rate" },
        { CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance,               "mct time adjust tolerance" },
        { CtiTableDynamicPaoInfo::Key_MCT_DSTStartTime,                      "mct dst start time" },
        { CtiTableDynamicPaoInfo::Key_MCT_DSTEndTime,                        "mct dst end time" },
        { CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset,                    "mct time zone offset" },
        { CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold,             "mct under voltage threshold" },
        { CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold,              "mct over voltage threshold" },
        { CtiTableDynamicPaoInfo::Key_MCT_DemandInterval,                    "mct demand interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_VoltageLPInterval,                 "mct voltage profile interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_VoltageDemandInterval,             "mct voltage demand interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay,                "mct scheduled freeze day" },
        { CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeConfigTimestamp,    "mct scheduled freeze config timestamp" },
        { CtiTableDynamicPaoInfo::Key_MCT_TouEnabled,                        "mct tou enabled" },
        { CtiTableDynamicPaoInfo::Key_MCT_DayTable,                          "mct day table" },
        { CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1,                      "mct day schedule 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2,                      "mct day schedule 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3,                      "mct day schedule 3" },
        { CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4,                      "mct day schedule 4" },
        { CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate,                    "mct default tou rate" },
        { CtiTableDynamicPaoInfo::Key_MCT_AddressBronze,                     "mct bronze address" },
        { CtiTableDynamicPaoInfo::Key_MCT_AddressCollection,                 "mct collection address" },
        { CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID,          "mct service provider id" },
        { CtiTableDynamicPaoInfo::Key_MCT_AddressLead,                       "mct lead address" },
        { CtiTableDynamicPaoInfo::Key_MCT_Configuration,                     "mct configuration" },
        { CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1,                   "mct event flags mask 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2,                   "mct event flags mask 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask,                    "mct meter alarm mask" },
        { CtiTableDynamicPaoInfo::Key_MCT_Options,                           "mct options" },
        { CtiTableDynamicPaoInfo::Key_MCT_OutageCycles,                      "mct outage cycles" },
        { CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode,                    "mct disconnect mode" },
        { CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold,                   "mct demand limit" },
        { CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay,                      "mct connect delay" },
        { CtiTableDynamicPaoInfo::Key_MCT_DisconnectMinutes,                 "mct disconnect minutes" },
        { CtiTableDynamicPaoInfo::Key_MCT_ConnectMinutes,                    "mct connect minutes" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len,                    "mct llp channel 1 length" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len,                    "mct llp channel 2 length" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len,                    "mct llp channel 3 length" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len,                    "mct llp channel 4 length" },
        { CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters,                 "mct display parameters" },
        { CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio,                  "mct transformer ratio" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday1,                          "mct holiday 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday2,                          "mct holiday 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday3,                          "mct holiday 3" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio1,                "mct load profile k ratio 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio2,                "mct load profile k ratio 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio3,                "mct load profile k ratio 3" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio4,                "mct load profile k ratio 4" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio1,            "mct load profile meter ratio 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio2,            "mct load profile meter ratio 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio3,            "mct load profile meter ratio 3" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio4,            "mct load profile meter ratio 4" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1,         "mct load profile channel 1 config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2,         "mct load profile channel 2 config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3,         "mct load profile channel 3 config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4,         "mct load profile channel 4 config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileResolution,             "" },
        { CtiTableDynamicPaoInfo::Key_MCT_RelayATimer,                       "mct relay a timer" },
        { CtiTableDynamicPaoInfo::Key_MCT_RelayBTimer,                       "mct relay b timer" },
        { CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableReadInterval,        "mct precanned table read interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_PrecannedMeterNumber,              "mct precanned meter number" },
        { CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType,                "mct precanned table type" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime1CRC,                  "mct dnp realtime1 crc" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime2CRC,                  "mct dnp realtime2 crc" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_BinaryCRC,                     "mct dnp binary crc" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC1,                    "mct dnp analog crc1" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC2,                    "mct dnp analog crc2" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC3,                    "mct dnp analog crc3" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC4,                    "mct dnp analog crc4" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC5,                    "mct dnp analog crc5" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC1,               "mct dnp accumulator crc1" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC2,               "mct dnp accumulator crc2" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC3,               "mct dnp accumulator crc3" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC4,               "mct dnp accumulator crc4" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01,                       "mct lcd metric 01" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02,                       "mct lcd metric 02" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03,                       "mct lcd metric 03" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04,                       "mct lcd metric 04" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05,                       "mct lcd metric 05" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06,                       "mct lcd metric 06" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07,                       "mct lcd metric 07" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08,                       "mct lcd metric 08" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09,                       "mct lcd metric 09" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10,                       "mct lcd metric 10" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11,                       "mct lcd metric 11" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12,                       "mct lcd metric 12" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13,                       "mct lcd metric 13" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14,                       "mct lcd metric 14" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15,                       "mct lcd metric 15" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16,                       "mct lcd metric 16" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17,                       "mct lcd metric 17" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18,                       "mct lcd metric 18" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19,                       "mct lcd metric 19" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20,                       "mct lcd metric 20" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21,                       "mct lcd metric 21" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22,                       "mct lcd metric 22" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23,                       "mct lcd metric 23" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24,                       "mct lcd metric 24" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25,                       "mct lcd metric 25" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26,                       "mct lcd metric 26" },
        { CtiTableDynamicPaoInfo::Key_MCT_WaterMeterReadInterval,            "mct water meter read interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Time,                  "mct llp interest time" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Channel,               "mct llp interest channel" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin,          "mct llp interest request begin" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd,            "mct llp interest request end" },
        { CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel,          "mct daily read interest channel" },
        { CtiTableDynamicPaoInfo::Key_FreezeCounter,                         "freeze counter" },
        { CtiTableDynamicPaoInfo::Key_FreezeExpected,                        "expected freeze" },
        { CtiTableDynamicPaoInfo::Key_VerificationSequence,                  "verification sequence" },
        { CtiTableDynamicPaoInfo::Key_FrozenRateAPeakTimestamp,              "frozen rate a peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenRateBPeakTimestamp,              "frozen rate b peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenRateCPeakTimestamp,              "frozen rate c peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenRateDPeakTimestamp,              "frozen rate d peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenDemandPeakTimestamp,             "frozen demand peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenDemand2PeakTimestamp,            "frozen channel 2 demand peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenDemand3PeakTimestamp,            "frozen channel 3 demand peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp,                 "demand freeze timestamp" },
        { CtiTableDynamicPaoInfo::Key_VoltageFreezeTimestamp,                "voltage freeze timestamp" },
        { CtiTableDynamicPaoInfo::Key_UDP_IP,                                "udp ip" },
        { CtiTableDynamicPaoInfo::Key_UDP_Port,                              "udp port" },
        { CtiTableDynamicPaoInfo::Key_UDP_Sequence,                          "udp sequence" },
        { CtiTableDynamicPaoInfo::Key_LCR_SSpec,                             "lcr sspec" },
        { CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision,                     "lcr sspec revision" },
        { CtiTableDynamicPaoInfo::Key_LCR_SerialAddress,                     "lcr serial address" },
        { CtiTableDynamicPaoInfo::Key_LCR_Spid,                              "lcr ssid" },
        { CtiTableDynamicPaoInfo::Key_LCR_GeoAddress,                        "lcr geo address" },
        { CtiTableDynamicPaoInfo::Key_LCR_Substation,                        "lcr substation" },
        { CtiTableDynamicPaoInfo::Key_LCR_Feeder,                            "lcr feeder" },
        { CtiTableDynamicPaoInfo::Key_LCR_ZipCode,                           "lcr zip code" },
        { CtiTableDynamicPaoInfo::Key_LCR_Uda,                               "lcr uda" },
        { CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay1,              "lcr relay 1 program address" },
        { CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay2,              "lcr relay 2 program address" },
        { CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay3,              "lcr relay 3 program address" },
        { CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay4,              "lcr relay 4 program address" },
        { CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay1,             "lcr relay 1 splinter address" },
        { CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay2,             "lcr relay 2 splinter address" },
        { CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay3,             "lcr relay 3 splinter address" },
        { CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay4,             "lcr relay 4 splinter address" },
        { CtiTableDynamicPaoInfo::Key_RPT_SSpec,                             "rpt sspec" },
        { CtiTableDynamicPaoInfo::Key_RPT_SSpecRevision,                     "rpt sspec revision" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday4,                          "mct holiday 4" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday5,                          "mct holiday 5" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday6,                          "mct holiday 6" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday7,                          "mct holiday 7" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday8,                          "mct holiday 8" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday9,                          "mct holiday 9" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday10,                         "mct holiday 10" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday11,                         "mct holiday 11" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday12,                         "mct holiday 12" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday13,                         "mct holiday 13" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday14,                         "mct holiday 14" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday15,                         "mct holiday 15" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday16,                         "mct holiday 16" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday17,                         "mct holiday 17" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday18,                         "mct holiday 18" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday19,                         "mct holiday 19" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday20,                         "mct holiday 20" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday21,                         "mct holiday 21" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday22,                         "mct holiday 22" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday23,                         "mct holiday 23" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday24,                         "mct holiday 24" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday25,                         "mct holiday 25" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday26,                         "mct holiday 26" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday27,                         "mct holiday 27" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday28,                         "mct holiday 28" },
        { CtiTableDynamicPaoInfo::Key_MCT_PhaseLossPercent,                  "mct phase loss percent" },
        { CtiTableDynamicPaoInfo::Key_MCT_PhaseLossSeconds,                  "mct phase loss seconds" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem01,                         "display item 01" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem02,                         "display item 02" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem03,                         "display item 03" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem04,                         "display item 04" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem05,                         "display item 05" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem06,                         "display item 06" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem07,                         "display item 07" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem08,                         "display item 08" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem09,                         "display item 09" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem10,                         "display item 10" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem11,                         "display item 11" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem12,                         "display item 12" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem13,                         "display item 13" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem14,                         "display item 14" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem15,                         "display item 15" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem16,                         "display item 16" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem17,                         "display item 17" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem18,                         "display item 18" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem19,                         "display item 19" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem20,                         "display item 20" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem21,                         "display item 21" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem22,                         "display item 22" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem23,                         "display item 23" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem24,                         "display item 24" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem25,                         "display item 25" },
        { CtiTableDynamicPaoInfo::Key_DisplayItem26,                         "display item 26" },
        { CtiTableDynamicPaoInfo::Key_RFN_TouEnabled,                        "rfn tou enabled" },
        { CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,                    "rfn monday schedule" },
        { CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,                   "rfn tuesday schedule" },
        { CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule,                 "rfn wednesday schedule" },
        { CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,                  "rfn thursday schedule" },
        { CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,                    "rfn friday schedule" },
        { CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,                  "rfn saturday schedule" },
        { CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,                    "rfn sunday schedule" },
        { CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,                   "rfn holiday schedule" },
        { CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate,                    "rfn default tou rate" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate0,                    "rfn schedule 1 rate 0" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1,                    "rfn schedule 1 time 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1,                    "rfn schedule 1 rate 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2,                    "rfn schedule 1 time 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2,                    "rfn schedule 1 rate 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3,                    "rfn schedule 1 time 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3,                    "rfn schedule 1 rate 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4,                    "rfn schedule 1 time 4" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4,                    "rfn schedule 1 rate 4" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5,                    "rfn schedule 1 time 5" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5,                    "rfn schedule 1 rate 5" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate0,                    "rfn schedule 2 rate 0" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1,                    "rfn schedule 2 time 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1,                    "rfn schedule 2 rate 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2,                    "rfn schedule 2 time 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2,                    "rfn schedule 2 rate 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3,                    "rfn schedule 2 time 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3,                    "rfn schedule 2 rate 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4,                    "rfn schedule 2 time 4" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4,                    "rfn schedule 2 rate 4" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5,                    "rfn schedule 2 time 5" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5,                    "rfn schedule 2 rate 5" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate0,                    "rfn schedule 3 rate 0" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1,                    "rfn schedule 3 time 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1,                    "rfn schedule 3 rate 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2,                    "rfn schedule 3 time 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2,                    "rfn schedule 3 rate 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3,                    "rfn schedule 3 time 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3,                    "rfn schedule 3 rate 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4,                    "rfn schedule 3 time 4" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4,                    "rfn schedule 3 rate 4" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5,                    "rfn schedule 3 time 5" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5,                    "rfn schedule 3 rate 5" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate0,                    "rfn schedule 4 rate 0" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1,                    "rfn schedule 4 time 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1,                    "rfn schedule 4 rate 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2,                    "rfn schedule 4 time 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2,                    "rfn schedule 4 rate 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3,                    "rfn schedule 4 time 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3,                    "rfn schedule 4 rate 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4,                    "rfn schedule 4 time 4" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4,                    "rfn schedule 4 rate 4" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5,                    "rfn schedule 4 time 5" },
        { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5,                    "rfn schedule 4 rate 5" },
        { CtiTableDynamicPaoInfo::Key_RFN_Holiday1,                          "rfn holiday 1" },
        { CtiTableDynamicPaoInfo::Key_RFN_Holiday2,                          "rfn holiday 2" },
        { CtiTableDynamicPaoInfo::Key_RFN_Holiday3,                          "rfn holiday 3" },
        { CtiTableDynamicPaoInfo::Key_RFN_DemandInterval,                    "rfn demand interval" },
        { CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval,               "rfn load profile interval" },
        { CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay,                   "rfn demand freeze day" },
        { CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled,                       "rfn ovuv enabled" },
        { CtiTableDynamicPaoInfo::Key_RFN_OvThreshold,                       "rfn ovuv ov threshold" },
        { CtiTableDynamicPaoInfo::Key_RFN_UvThreshold,                       "rfn ovuv uv threshold" },
        { CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval,        "rfn ovuv alarm reporting interval" },
        { CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval,           "rfn ovuv alarm repeating interval" },
        { CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount,                   "rfn ovuv alarm repeat count" },
        { CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime,                      "rfn lcd cycle time" },
        { CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled,      "rfn lcd disconnect display disabled" },
        { CtiTableDynamicPaoInfo::Key_RFN_LcdDisplayDigits,                  "rfn lcd display digits" },
        { CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode,                    "rfn disconnect mode" },
        { CtiTableDynamicPaoInfo::Key_RFN_ReconnectParam,                    "rfn reconnect param" },
        { CtiTableDynamicPaoInfo::Key_RFN_DisconnectDemandInterval,          "rfn disconnect demand interval" },
        { CtiTableDynamicPaoInfo::Key_RFN_DemandThreshold,                   "rfn demand threshold" },
        { CtiTableDynamicPaoInfo::Key_RFN_ConnectDelay,                      "rfn connect delay" },
        { CtiTableDynamicPaoInfo::Key_RFN_MaxDisconnects,                    "rfn max disconnects" },
        { CtiTableDynamicPaoInfo::Key_RFN_DisconnectMinutes,                 "rfn disconnect minutes" },
        { CtiTableDynamicPaoInfo::Key_RFN_ConnectMinutes,                    "rfn connect minutes" },
        { CtiTableDynamicPaoInfo::Key_RFN_TempAlarmUnsupported,              "rfn temp alarm unsupported" },
        { CtiTableDynamicPaoInfo::Key_RFN_TempAlarmIsEnabled,                "rfn temp alarm enabled" },
        { CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatInterval,           "rfn temp alarm repeat interval" },
        { CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatCount,              "rfn temp alarm repeat count" },
        { CtiTableDynamicPaoInfo::Key_RFN_TempAlarmHighTempThreshold,        "rfn temp alarm high temp threshold" },
        { CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds,          "rfn recording interval seconds" },
        { CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds,          "rfn reporting interval seconds" },
        { CtiTableDynamicPaoInfo::Key_RF_DA_DnpSlaveAddress,                 "rf da dnp slave address" },
        { CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress,          "rfn meter programming progress" },
    };

    {
        std::vector<std::string> expected, results;
        std::vector<bool> duplicateExpected, duplicateResults;
        std::set<std::string> tmp_set;

        for each(test_case tc in test_cases)
        {
            const std::string keyString = CtiTableDynamicPaoInfo::getKeyString(tc.key);

            expected.push_back(tc.keyAsString);
            results .push_back(keyString);

            duplicateExpected.push_back(false);

            if( ! keyString.empty() )
            {
                const bool isDuplicate = ! tmp_set.insert(keyString).second;
                duplicateResults.push_back(isDuplicate);
            }
            else
            {
                // keep the index of the collection in sync with the key array under test
                duplicateResults.push_back(false);
            }
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(), results.begin(), results.end());
        BOOST_CHECK_EQUAL_COLLECTIONS(duplicateExpected.begin(), duplicateExpected.end(), duplicateResults.begin(), duplicateResults.end());
    }

    //
    // test indexed keys
    //
    struct test_case_indexed
    {
        CtiTableDynamicPaoInfoIndexed::PaoInfoKeysIndexed key;
        std::string keyAsString;
    }
    const test_cases_indexed[] =
    {
       { CtiTableDynamicPaoInfoIndexed::Key_Invalid,             "" },
       { CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, "rfn midnight metrics" },
       { CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, "rfn interval metrics" },
    };

    {
        std::vector<std::string> expected, results;
        std::vector<bool> duplicateExpected, duplicateResults;
        std::set<std::string> tmp_set;

        for each(test_case_indexed tc_indexed in test_cases_indexed)
        {
            const std::string keyStringIndexed = CtiTableDynamicPaoInfoIndexed::getKeyString(tc_indexed.key);

            expected.push_back(tc_indexed.keyAsString);
            results .push_back(keyStringIndexed);

            duplicateExpected.push_back(false);

            if( ! keyStringIndexed.empty() )
            {
                const bool isDuplicate = ! tmp_set.insert(keyStringIndexed).second;
                duplicateResults.push_back(isDuplicate);
            }
            else
            {
                // keep the index of the collection in sync with the key array under test
                duplicateResults.push_back(false);
            }

        }

        BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(), results.begin(), results.end());
        BOOST_CHECK_EQUAL_COLLECTIONS(duplicateExpected.begin(), duplicateExpected.end(), duplicateResults.begin(), duplicateResults.end());
    }

    //
    // test that no key string matches fully or partially (prefix) any indexed keys string
    //
    {
        for each(test_case_indexed tc_indexed in test_cases_indexed)
        {
            const std::string keyStringIndexed = CtiTableDynamicPaoInfoIndexed::getKeyString(tc_indexed.key);

            for each(test_case tc in test_cases)
            {
                const std::string keyString = CtiTableDynamicPaoInfo::getKeyString(tc.key);

                if( ! keyStringIndexed.empty() && ! keyString.empty() )
                {
                    const bool matches = keyString.compare(0, std::min( keyString.length(), keyStringIndexed.length()), keyStringIndexed ) == 0;

                    BOOST_CHECK_MESSAGE( ! matches,
                            "keyStringIndexed (" << tc_indexed.key << "): \"" << keyStringIndexed << "\""
                            " == prefix of keyString: (" << tc.key << "): \"" << keyString << "\"" );
                }
            }
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()

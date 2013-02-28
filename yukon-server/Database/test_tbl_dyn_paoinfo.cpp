#include <boost/test/auto_unit_test.hpp>

#include "tbl_dyn_paoinfo.h"
#include "boostutil.h"

BOOST_AUTO_TEST_SUITE( test_tbl_dyn_paoinfo )


BOOST_AUTO_TEST_CASE(test_getowner)
{
    CtiTableDynamicPaoInfo info;

    info.setOwner(Application_CalcLogic);
    BOOST_CHECK_EQUAL(info.getOwnerString(), "");

    info.setOwner(Application_CapControl);
    BOOST_CHECK_EQUAL(info.getOwnerString(), "");

    info.setOwner(Application_Dispatch);
    BOOST_CHECK_EQUAL(info.getOwnerString(), "dispatch");

    info.setOwner(Application_Invalid);
    BOOST_CHECK_EQUAL(info.getOwnerString(), "");

    info.setOwner(Application_LoadManagement);
    BOOST_CHECK_EQUAL(info.getOwnerString(), "");

    info.setOwner(Application_Porter);
    BOOST_CHECK_EQUAL(info.getOwnerString(), "porter");

    info.setOwner(Application_Scanner);
    BOOST_CHECK_EQUAL(info.getOwnerString(), "scanner");
}


BOOST_AUTO_TEST_CASE(test_getKeyString)
{
    struct test_case
    {
        CtiTableDynamicPaoInfo::PaoInfoKeys key;
        std::string keyAsString;
    }
    const test_cases[] =
    {
        { CtiTableDynamicPaoInfo::Key_Invalid,                                      "" },
        { CtiTableDynamicPaoInfo::Key_MCT_SSpec,                                    "mct sspec" },
        { CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision,                            "mct sspec revision" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig,                        "mct load profile config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval,                      "mct load profile interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2,                     "mct load profile interval 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval,                   "mct ied load profile rate" },
        { CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance,                      "mct time adjust tolerance" },
        { CtiTableDynamicPaoInfo::Key_MCT_DSTStartTime,                             "mct dst start time" },
        { CtiTableDynamicPaoInfo::Key_MCT_DSTEndTime,                               "mct dst end time" },
        { CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset,                           "mct time zone offset" },
        { CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold,                    "mct under voltage threshold" },
        { CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold,                     "mct over voltage threshold" },
        { CtiTableDynamicPaoInfo::Key_MCT_DemandInterval,                           "mct demand interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_VoltageLPInterval,                        "mct voltage profile interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_VoltageDemandInterval,                    "mct voltage demand interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay,                       "mct scheduled freeze day" },
        { CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeConfigTimestamp,           "mct scheduled freeze config timestamp" },
        { CtiTableDynamicPaoInfo::Key_MCT_DayTable,                                 "mct day table" },
        { CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1,                             "mct day schedule 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2,                             "mct day schedule 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3,                             "mct day schedule 3" },
        { CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4,                             "mct day schedule 4" },
        { CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate,                           "mct default tou rate" },
        { CtiTableDynamicPaoInfo::Key_MCT_AddressBronze,                            "mct bronze address" },
        { CtiTableDynamicPaoInfo::Key_MCT_AddressCollection,                        "mct collection address" },
        { CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID,                 "mct service provider id" },
        { CtiTableDynamicPaoInfo::Key_MCT_AddressLead,                              "mct lead address" },
        { CtiTableDynamicPaoInfo::Key_MCT_Configuration,                            "mct configuration" },
        { CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1,                          "mct event flags mask 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2,                          "mct event flags mask 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask,                           "mct meter alarm mask" },
        { CtiTableDynamicPaoInfo::Key_MCT_Options,                                  "mct options" },
        { CtiTableDynamicPaoInfo::Key_MCT_OutageCycles,                             "mct outage cycles" },
        { CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold,                          "mct demand limit" },
        { CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay,                             "mct connect delay" },
        { CtiTableDynamicPaoInfo::Key_MCT_DisconnectMinutes,                        "mct disconnect minutes" },
        { CtiTableDynamicPaoInfo::Key_MCT_ConnectMinutes,                           "mct connect minutes" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len,                           "mct llp channel 1 length" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len,                           "mct llp channel 2 length" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len,                           "mct llp channel 3 length" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len,                           "mct llp channel 4 length" },
        { CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters,                        "mct display parameters" },
        { CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio,                         "mct transformer ratio" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday1,                                 "mct holiday 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday2,                                 "mct holiday 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday3,                                 "mct holiday 3" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio1,                       "mct load profile k ratio 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio2,                       "mct load profile k ratio 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio3,                       "mct load profile k ratio 3" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio4,                       "mct load profile k ratio 4" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio1,                   "mct load profile meter ratio 1" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio2,                   "mct load profile meter ratio 2" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio3,                   "mct load profile meter ratio 3" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio4,                   "mct load profile meter ratio 4" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1,                "mct load profile channel 1 config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2,                "mct load profile channel 2 config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3,                "mct load profile channel 3 config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4,                "mct load profile channel 4 config" },
        { CtiTableDynamicPaoInfo::Key_MCT_LoadProfileResolution,                    "" },
        { CtiTableDynamicPaoInfo::Key_MCT_RelayATimer,                              "mct relay a timer" },
        { CtiTableDynamicPaoInfo::Key_MCT_RelayBTimer,                              "mct relay b timer" },
        { CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableReadInterval,               "mct precanned table read interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_PrecannedMeterNumber,                     "mct precanned meter number" },
        { CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType,                       "mct precanned table type" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime1CRC,                         "mct dnp realtime1 crc" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime2CRC,                         "mct dnp realtime2 crc" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_BinaryCRC,                            "mct dnp binary crc" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC1,                           "mct dnp analog crc1" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC2,                           "mct dnp analog crc2" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC3,                           "mct dnp analog crc3" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC4,                           "mct dnp analog crc4" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC5,                           "mct dnp analog crc5" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC1,                      "mct dnp accumulator crc1" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC2,                      "mct dnp accumulator crc2" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC3,                      "mct dnp accumulator crc3" },
        { CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC4,                      "mct dnp accumulator crc4" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01,                              "mct lcd metric 01" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02,                              "mct lcd metric 02" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03,                              "mct lcd metric 03" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04,                              "mct lcd metric 04" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05,                              "mct lcd metric 05" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06,                              "mct lcd metric 06" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07,                              "mct lcd metric 07" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08,                              "mct lcd metric 08" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09,                              "mct lcd metric 09" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10,                              "mct lcd metric 10" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11,                              "mct lcd metric 11" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12,                              "mct lcd metric 12" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13,                              "mct lcd metric 13" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14,                              "mct lcd metric 14" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15,                              "mct lcd metric 15" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16,                              "mct lcd metric 16" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17,                              "mct lcd metric 17" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18,                              "mct lcd metric 18" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19,                              "mct lcd metric 19" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20,                              "mct lcd metric 20" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21,                              "mct lcd metric 21" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22,                              "mct lcd metric 22" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23,                              "mct lcd metric 23" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24,                              "mct lcd metric 24" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25,                              "mct lcd metric 25" },
        { CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26,                              "mct lcd metric 26" },
        { CtiTableDynamicPaoInfo::Key_MCT_WaterMeterReadInterval,                   "mct water meter read interval" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Time,                         "mct llp interest time" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Channel,                      "mct llp interest channel" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin,                 "mct llp interest request begin" },
        { CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd,                   "mct llp interest request end" },
        { CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel,                 "mct daily read interest channel" },
        { CtiTableDynamicPaoInfo::Key_FreezeCounter,                                "freeze counter" },
        { CtiTableDynamicPaoInfo::Key_FreezeExpected,                               "expected freeze" },
        { CtiTableDynamicPaoInfo::Key_VerificationSequence,                         "verification sequence" },
        { CtiTableDynamicPaoInfo::Key_FrozenRateAPeakTimestamp,                     "frozen rate a peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenRateBPeakTimestamp,                     "frozen rate b peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenRateCPeakTimestamp,                     "frozen rate c peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenRateDPeakTimestamp,                     "frozen rate d peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenDemandPeakTimestamp,                    "frozen demand peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenDemand2PeakTimestamp,                   "frozen channel 2 demand peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_FrozenDemand3PeakTimestamp,                   "frozen channel 3 demand peak timestamp" },
        { CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp,                        "demand freeze timestamp" },
        { CtiTableDynamicPaoInfo::Key_VoltageFreezeTimestamp,                       "voltage freeze timestamp" },
        { CtiTableDynamicPaoInfo::Key_UDP_IP,                                       "udp ip" },
        { CtiTableDynamicPaoInfo::Key_UDP_Port,                                     "udp port" },
        { CtiTableDynamicPaoInfo::Key_UDP_Sequence,                                 "udp sequence" },
        { CtiTableDynamicPaoInfo::Key_LCR_SSpec,                                    "lcr sspec" },
        { CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision,                            "lcr sspec revision" },
        { CtiTableDynamicPaoInfo::Key_LCR_SerialAddress,                            "lcr serial address" },
        { CtiTableDynamicPaoInfo::Key_LCR_Spid,                                     "lcr ssid" },
        { CtiTableDynamicPaoInfo::Key_LCR_GeoAddress,                               "lcr geo address" },
        { CtiTableDynamicPaoInfo::Key_LCR_Substation,                               "lcr substation" },
        { CtiTableDynamicPaoInfo::Key_LCR_Feeder,                                   "lcr feeder" },
        { CtiTableDynamicPaoInfo::Key_LCR_ZipCode,                                  "lcr zip code" },
        { CtiTableDynamicPaoInfo::Key_LCR_Uda,                                      "lcr uda" },
        { CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay1,                     "lcr relay 1 program address" },
        { CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay2,                     "lcr relay 2 program address" },
        { CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay3,                     "lcr relay 3 program address" },
        { CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay4,                     "lcr relay 4 program address" },
        { CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay1,                    "lcr relay 1 splinter address" },
        { CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay2,                    "lcr relay 2 splinter address" },
        { CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay3,                    "lcr relay 3 splinter address" },
        { CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay4,                    "lcr relay 4 splinter address" },
        { CtiTableDynamicPaoInfo::Key_RPT_SSpec,                                    "rpt sspec" },
        { CtiTableDynamicPaoInfo::Key_RPT_SSpecRevision,                            "rpt sspec revision" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday4,                                 "mct holiday 4" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday5,                                 "mct holiday 5" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday6,                                 "mct holiday 6" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday7,                                 "mct holiday 7" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday8,                                 "mct holiday 8" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday9,                                 "mct holiday 9" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday10,                                "mct holiday 10" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday11,                                "mct holiday 11" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday12,                                "mct holiday 12" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday13,                                "mct holiday 13" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday14,                                "mct holiday 14" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday15,                                "mct holiday 15" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday16,                                "mct holiday 16" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday17,                                "mct holiday 17" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday18,                                "mct holiday 18" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday19,                                "mct holiday 19" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday20,                                "mct holiday 20" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday21,                                "mct holiday 21" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday22,                                "mct holiday 22" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday23,                                "mct holiday 23" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday24,                                "mct holiday 24" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday25,                                "mct holiday 25" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday26,                                "mct holiday 26" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday27,                                "mct holiday 27" },
        { CtiTableDynamicPaoInfo::Key_MCT_Holiday28,                                "mct holiday 28" },
        { CtiTableDynamicPaoInfo::Key_MCT_PhaseLossPercent,                         "mct phase loss percent" },
        { CtiTableDynamicPaoInfo::Key_MCT_PhaseLossSeconds,                         "mct phase loss seconds" },
    };

    std::vector<std::string> expected, results;

    for each(test_case tc in test_cases)
    {
        expected.push_back(tc.keyAsString);

        CtiTableDynamicPaoInfo paoinfo(-1, tc.key);

        results.push_back(paoinfo.getKeyString());
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(), results.begin(), results.end());
}


BOOST_AUTO_TEST_SUITE_END()

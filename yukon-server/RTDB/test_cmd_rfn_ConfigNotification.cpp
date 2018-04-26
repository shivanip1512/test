#include <boost/test/unit_test.hpp>

#include "cmd_rfn_ConfigNotification.h"

#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnConfigNotificationCommand;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_ConfigNotification )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE(test_request)
{
    RfnConfigNotificationCommand cmd;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand(execute_time);

        const std::vector<unsigned> exp {
            0x1d };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        std::vector<unsigned char> response {
            0x1e, 
            0x00, 0x02, 
            //  TLV 5
            0x00, 0x05,  //  Interval recording
            0x00, 0x11,
            0x20, 0x1c, 0x00, 0x00,  //  7200
            0x80, 0x51, 0x01, 0x00,  //  86400
            0x04,  //  4 metrics
            0x01, 0x00,
            0x02, 0x00,
            0x03, 0x00,
            0x04, 0x00,
            //  TLV 6
            0x00, 0x06,  // Channel selection
            0x00, 0x09,
            0x04,
            0x05, 0x00, 
            0x06, 0x00, 
            0x07, 0x00, 
            0x08, 0x00 };

        const auto rcv = cmd.decodeCommand(execute_time, response);

        std::string exp = "Interval recording configuration:"
            "\nRecording interval : 7200 seconds"
            "\nReporting interval : 86400 seconds"
            "\nInterval metrics   : 1, 2, 3, 4"
            "\nChannel selection configuration:"
            "\nMetric IDs: 5, 6, 7, 8";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_handle_unsolicited)
{
    const std::vector<uint8_t> payload { 
        0x1e, 
        0x00, 0x01, 
        0x00, 0x01,
        0x00, 0x01,
        0x01
    };

    auto cmd = RfnCommand::handleUnsolicitedReport(execute_time, payload);

    BOOST_REQUIRE(cmd);

    auto configNotificationCmd = dynamic_cast<RfnConfigNotificationCommand *>(cmd.get());

    BOOST_REQUIRE(configNotificationCmd);

    BOOST_REQUIRE(configNotificationCmd->touEnabled);

    BOOST_CHECK_EQUAL(configNotificationCmd->touEnabled.value(), Cti::Devices::Commands::RfnTouConfigurationCommand::TouEnable);
}

BOOST_AUTO_TEST_CASE(test_no_tlvs)
{
    const std::vector<uint8_t> payload { 
        0x1e, 
        0x00, 0x00
    };

    RfnConfigNotificationCommand cmd;

    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK      (result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description, 
        "Device Configuration Request:"
        "\nEmpty");
}

BOOST_AUTO_TEST_CASE(test_unknown_tlv)
{
    const std::vector<uint8_t> payload { 
        0x1e, 
        0x00, 0x01,
        0x00, 0x71,
        0x00, 0x01,
        0x01
    };

    RfnConfigNotificationCommand cmd;

    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK      (result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description, 
        "Device Configuration Request:"
        "\nUnknown TLV type 113");
}

BOOST_AUTO_TEST_CASE(test_empty_tlv)
{
    const std::vector<uint8_t> payload { 
        0x1e, 
        0x00, 0x01,
        0x00, 0x01,
        0x00, 0x00
    };

    RfnConfigNotificationCommand cmd;

    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK      (result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description, 
        "Device Configuration Request:"
        "\nEmpty payload for TLV type 1");
}

BOOST_AUTO_TEST_CASE(test_one_tlv)
{
    const std::vector<uint8_t> payload { 
        0x1e, 
        0x00, 0x01, 
        0x00, 0x01,
        0x00, 0x01,
        0x01
    };

    RfnConfigNotificationCommand cmd;
    
    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK      (result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description, 
        "Device Configuration Request:"
        "\nTOU enabled");

    BOOST_REQUIRE(cmd.touEnabled);

    BOOST_CHECK_EQUAL(cmd.touEnabled.value(), Cti::Devices::Commands::RfnTouConfigurationCommand::TouEnable);
}

BOOST_AUTO_TEST_CASE(test_all_tlvs)
{
    const std::vector<uint8_t> payload { 
        0x1e, 
        0x00, 0x0c, 
        //  TLV 1
        0x00, 0x01,  //  TOU Enable/Disable
        0x00, 0x01,
            //  TOU enable
            0x01,
        //  TLV 2
        0x00, 0x02,  //  TOU Schedule
        0x00, 0x38,
            //  Day table
            0x88, 0x36, 0x05,  //  1, 2, 3, 4, 4, 3, 2, 1
            //  Schedule 1 switch times
            0x00, 0x03, 
            0x00, 0x01, 
            0x00, 0x04, 
            0x00, 0x01, 
            0x00, 0x05, 
            //  Schedule 2 switch times
            0x00, 0x09, 
            0x00, 0x02, 
            0x00, 0x06, 
            0x00, 0x05, 
            0x00, 0x03, 
            //  Schedule 3 switch times
            0x00, 0x05, 
            0x00, 0x08, 
            0x00, 0x09, 
            0x00, 0x07, 
            0x00, 0x09, 
            //  Schedule 4 switch times
            0x00, 0x03, 
            0x00, 0x02, 
            0x00, 0x03, 
            0x00, 0x08, 
            0x00, 0x04, 
            //  Schedule 1 rates
            0xc1, 0x14, 0x00,  //  A B C D A B
            //  Schedule 2 rates
            0x53, 0x30, 0x01,  //  C D A B C D
            //  Schedule 3 rates
            0x0a, 0xa6, 0x00,  //  B C D A B C
            //  Schedule 4 rates
            0x98, 0x82, 0x01,  //  D A B C D A
            //  default rate
            0x01,  //  B
        //  TLV 3
        0x00, 0x03,  //  TOU Holiday
        0x00, 0x0c,
            //  Holiday 1
            0x5A, 0xA9, 0x3B, 0x26, //  
            //  Holiday 2
            0x5B, 0x34, 0x53, 0x9D, //
            //  Holiday 3
            0x5A, 0x7B, 0x45, 0x42, 
        //  TLV 4
        0x00, 0x04,  //  Demand Freeze Day
        0x00, 0x01,
            0x20,  //  32
        //  TLV 5
        0x00, 0x05,  //  Interval recording
        0x00, 0x11,
            0x20, 0x1c, 0x00, 0x00,  //  7200
            0x80, 0x51, 0x01, 0x00,  //  86400
            0x04,  //  4 metrics
            0x01, 0x00,
            0x02, 0x00,
            0x03, 0x00,
            0x04, 0x00,
        //  TLV 6
        0x00, 0x06,  // Channel selection
        0x00, 0x09,
            0x04,
            0x05, 0x00, 
            0x06, 0x00, 
            0x07, 0x00, 
            0x08, 0x00, 
        //  TLV 7
        0x00, 0x07,  //  Disconnect
        0x00, 0x06,
            0x02,  //  Demand threshold
            0x01,  //  Reconnect method
            0x18,  //  Demand interval
            0x1f,  //  Demand threshold
            0x11,  //  Connect delay
            0x07,  //  Max disconnects
        //  TLV 8
        0x00, 0x08,  //  Voltage profile
        0x00, 0x02,
            0x07,  //  Voltage demand interval (15 second increments)
            0x0b,  //  Voltage load profile interval (minutes)
        //  TLV 9
        0x00, 0x09,  //  C2SX Display
        0x00, 0x0d,
            0x06,
            0x00, 0x04,
            0x01, 0x08,
            0x02, 0x0c,
            0xfd, 0x05,
            0xfe, 0x07,
            0xff, 0x00,
        //  TLV 10
        0x00, 0x0a,  //  Focus AL Display
        0x00, 0x0b,
            0x03,
            0x06,
            0x00, 0x3e, 0x55,
            0x02, 0x41, 0x3b,
            0x08, 0x47, 0x47,
        //  TLV 11
        0x00, 0x0b,  //  OV/UV configuration
        0x00, 0x12,
            0x7f,        //  Meter ID
            0x01, 0xff,  //  Event ID
            0x01,        //  OV/UV Enable/disable
            0x0e,        //  New alarm reporting interval
            0x03,        //  Alarm repeat interval
            0x02,        //  Set alarm repeat count
            0x05,        //  Clear alarm repeat count
            0x0d,        //  Severity
            0x0e, 0x0e, 0x0e, 0x0e,  //  Set threshold value
            0x0e,        //  Unit of measure
            0x0e, 0x0e,  //  UOM modifier 1
            0x0e, 0x0e,  //  UOM modifier 2
        //  TLV 12
        0x00, 0x0c,  //  Temperature configuration
        0x00, 0x07,
            0x01,  //  Enable/disable
            0x01, 0x17,  //  high temp threshold
            0x01, 0x03,  //  low temp threshold
            0x07,  //  repeat interval
            0x0b,  //  Max repeats
    };

    const std::string expected = 
        "Device Configuration Request:"
        "\nTOU enabled"
        "\nTOU schedule configuration:"
        "\nDay table               : "
        "\n1"
        "\n2"
        "\n3"
        "\n4"
        "\n4"
        "\n3"
        "\n2"
        "\n1"
        "\nSchedule 1 switch times : "
        "\n00:03, 00:01, 00:04, 00:01, 00:05"
        "\nSchedule 2 switch times : "
        "\n00:09, 00:02, 00:06, 00:05, 00:03"
        "\nSchedule 3 switch times : "
        "\n00:05, 00:08, 00:09, 00:07, 00:09"
        "\nSchedule 4 switch times : "
        "\n00:03, 00:02, 00:03, 00:08, 00:04"
        "\nSchedule 1 rates        : "
        "\nB, A, D, C, B, A"
        "\nSchedule 2 rates        : "
        "\nD, C, B, A, D, C"
        "\nSchedule 3 rates        : "
        "\nC, B, A, D, C, B"
        "\nSchedule 4 rates        : "
        "\nA, D, C, B, A, D"
        "\nDefault rate            : B"
        "\nTOU holiday configuration:"
        "\n Date 1 - 2018-Mar-14"
        "\n Date 2 - 2018-Jun-27"
        "\n Date 3 - 2018-Feb-07"
        "\nFreeze day: 32"
        "\nInterval recording configuration:"
        "\nRecording interval : 7200 seconds"
        "\nReporting interval : 86400 seconds"
        "\nInterval metrics   : 1, 2, 3, 4"
        "\nChannel selection configuration:"
        "\nMetric IDs: 5, 6, 7, 8"
        "\nDisconnect configuration:"
        "\nDisconnect mode  : demand threshold"
        "\nReconnect method : 1"
        "\nDemand interval  : 24 minutes"
        "\nDemand threshold : 3.1000000000000001kW"
        "\nConnect delay    : 17 minutes"
        "\nMax disconnects  : 7"
        "\nVoltage profile configuration:"
        "\nVoltage demand interval  : 105 seconds"
        "\nVoltage profile interval : 11 minutes"
        "\nC2SX display:"
        "\nSlot 0             : 4"
        "\nSlot 1             : 8"
        "\nSlot 2             : 12"
        "\nDisplay digits     : 5"
        "\nCycle time         : 7"
        "\nDisconnect display : 0"
        "\nFocus AL display:"
        "\nDisplay time : 6 seconds"
        "\nSlot 0       : > U"
        "\nSlot 2       : A *"
        "\nOV/UV configuration:"
        "\nMeter ID                     : 127"
        "\nEvent ID                     : 65281"
        "\nOV/UV alarming enabled       : true"
        "\nNew alarm reporting interval : 14 minutes"
        "\nAlarm repeat interval        : 3 minutes"
        "\nSet alarm repeat count       : 2"
        "\nClear alarm repeat count     : 5"
        "\nSeverity                     : 13"
        "\nSet threshold value          : 235802.12599999999"
        "\nUnit of measure              : 14"
        "\nUOM modifier 1               : 3598"
        "\nUOM modifier 2               : 3598"
        "\nTemperature alarm configuration:"
        "\nTemperature alarming enabled : true"
        "\nHigh temp threshold          : 5889"
        "\nLow temp threshold           : 769"
        "\nRepeat interval              : 7 minutes"
        "\nMax repeats                  : 11";

    RfnConfigNotificationCommand cmd;

    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK      (result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description, expected);

    BOOST_REQUIRE(cmd.c2sxDisplay);
    BOOST_CHECK_EQUAL(cmd.c2sxDisplay->disconnectDisplay.value(), false);
    BOOST_CHECK_EQUAL(cmd.c2sxDisplay->displayDigits.value(), 5);
    BOOST_CHECK_EQUAL(cmd.c2sxDisplay->lcdCycleTime.value(), 7);
    BOOST_CHECK_EQUAL(cmd.c2sxDisplay->displayItems[0], 4);
    BOOST_CHECK_EQUAL(cmd.c2sxDisplay->displayItems[1], 8);
    BOOST_CHECK_EQUAL(cmd.c2sxDisplay->displayItems[2], 12);

    BOOST_REQUIRE(cmd.channelSelections);
    const auto selectionsExpected = { 5, 6, 7, 8 };
    BOOST_CHECK_EQUAL_RANGES(cmd.channelSelections.value(), selectionsExpected);

    BOOST_REQUIRE(cmd.demandFreezeDay);
    BOOST_CHECK_EQUAL(cmd.demandFreezeDay.value(), 32);

    BOOST_REQUIRE(cmd.disconnect);
    BOOST_CHECK_EQUAL(cmd.disconnect->connectDelay, 17);
    BOOST_CHECK_EQUAL(cmd.disconnect->demandInterval, 24);
    BOOST_CHECK_EQUAL(cmd.disconnect->demandThreshold, 3.1);
    BOOST_CHECK_EQUAL(cmd.disconnect->disconnectMode, 2);
    BOOST_CHECK_EQUAL(cmd.disconnect->maxDisconnects, 7);
    BOOST_CHECK_EQUAL(cmd.disconnect->reconnect, 1);

    BOOST_REQUIRE(cmd.focusDisplay);
    BOOST_CHECK_EQUAL(cmd.focusDisplay->displayItemDuration, 6);
    const auto focusDisplayExpected = { 0, 2 };
    BOOST_CHECK_EQUAL_RANGES(cmd.focusDisplay->displayItems, focusDisplayExpected);

    BOOST_REQUIRE(cmd.intervalRecording);
    const auto intervalMetricsExpected = { 1, 2, 3, 4 };
    BOOST_CHECK_EQUAL_RANGES(cmd.intervalRecording->intervalMetrics, intervalMetricsExpected);
    BOOST_CHECK_EQUAL(cmd.intervalRecording->recordingInterval, 7200);
    BOOST_CHECK_EQUAL(cmd.intervalRecording->reportingInterval, 86400);

    BOOST_REQUIRE(cmd.ovuv);
    BOOST_CHECK_EQUAL(cmd.ovuv->ovThreshold.is_initialized(), false);
    BOOST_CHECK_EQUAL(cmd.ovuv->ovuvAlarmRepeatCount, 2);
    BOOST_CHECK_EQUAL(cmd.ovuv->ovuvAlarmRepeatInterval, 3);
    BOOST_CHECK_EQUAL(cmd.ovuv->ovuvAlarmReportingInterval, 14);
    BOOST_CHECK_EQUAL(cmd.ovuv->ovuvEnabled, true);
    BOOST_CHECK_EQUAL(cmd.ovuv->uvThreshold.value(), 235802.126);

    BOOST_REQUIRE(cmd.temperature);
    BOOST_CHECK_EQUAL(cmd.temperature->alarmEnabled, true);
    BOOST_CHECK_EQUAL(cmd.temperature->alarmHighTempThreshold, 5889);
    BOOST_CHECK_EQUAL(cmd.temperature->alarmRepeatCount, 11);
    BOOST_CHECK_EQUAL(cmd.temperature->alarmRepeatInterval, 7);

    BOOST_REQUIRE(cmd.touEnabled);
    BOOST_CHECK_EQUAL(cmd.touEnabled.value(), Cti::Devices::Commands::RfnTouConfigurationCommand::TouEnable);

    BOOST_REQUIRE(cmd.touHolidays);
    BOOST_CHECK_EQUAL(cmd.touHolidays->operator[](0), CtiDate(14, 3, 2018));
    BOOST_CHECK_EQUAL(cmd.touHolidays->operator[](1), CtiDate(27, 6, 2018));
    BOOST_CHECK_EQUAL(cmd.touHolidays->operator[](2), CtiDate( 7, 2, 2018));

    BOOST_REQUIRE(cmd.touSchedule);

    const auto dayTableExpected = { "SCHEDULE_1", "SCHEDULE_2", "SCHEDULE_3", "SCHEDULE_4", "SCHEDULE_4", "SCHEDULE_3", "SCHEDULE_2", "SCHEDULE_1" };
    BOOST_CHECK_EQUAL_RANGES(cmd.touSchedule->_dayTable, dayTableExpected);
    BOOST_CHECK_EQUAL(cmd.touSchedule->_defaultRate, "B");
    {
        BOOST_REQUIRE_EQUAL(cmd.touSchedule->_rates.size(), 4);
        const auto rates0 = { "B", "A", "D", "C", "B", "A" };
        const auto rates1 = { "D", "C", "B", "A", "D", "C" };
        const auto rates2 = { "C", "B", "A", "D", "C", "B" };
        const auto rates3 = { "A", "D", "C", "B", "A", "D" };
        auto itr = cmd.touSchedule->_rates.cbegin();
        BOOST_CHECK_EQUAL(itr->first, 0);
        BOOST_CHECK_EQUAL_RANGES(itr->second, rates0);

        ++itr;
        BOOST_CHECK_EQUAL(itr->first, 1);
        BOOST_CHECK_EQUAL_RANGES(itr->second, rates1);

        ++itr;
        BOOST_CHECK_EQUAL(itr->first, 2);
        BOOST_CHECK_EQUAL_RANGES(itr->second, rates2);

        ++itr;
        BOOST_CHECK_EQUAL(itr->first, 3);
        BOOST_CHECK_EQUAL_RANGES(itr->second, rates3);
    }
    {
        BOOST_REQUIRE_EQUAL(cmd.touSchedule->_times.size(), 4);
        const auto times0 = { "00:03", "00:01", "00:04", "00:01", "00:05" };
        const auto times1 = { "00:09", "00:02", "00:06", "00:05", "00:03" };
        const auto times2 = { "00:05", "00:08", "00:09", "00:07", "00:09" };
        const auto times3 = { "00:03", "00:02", "00:03", "00:08", "00:04" };
        auto itr = cmd.touSchedule->_times.cbegin();
        BOOST_CHECK_EQUAL(itr->first, 0);
        BOOST_CHECK_EQUAL_RANGES(itr->second, times0);

        ++itr;
        BOOST_CHECK_EQUAL(itr->first, 1);
        BOOST_CHECK_EQUAL_RANGES(itr->second, times1);

        ++itr;
        BOOST_CHECK_EQUAL(itr->first, 2);
        BOOST_CHECK_EQUAL_RANGES(itr->second, times2);

        ++itr;
        BOOST_CHECK_EQUAL(itr->first, 3);
        BOOST_CHECK_EQUAL_RANGES(itr->second, times3);
    }
}

BOOST_AUTO_TEST_SUITE_END()

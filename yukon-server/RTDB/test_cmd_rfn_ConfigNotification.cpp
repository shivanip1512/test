#include <boost/test/unit_test.hpp>

#include "cmd_rfn_ConfigNotification.h"

#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnConfigNotificationCommand;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_ConfigNotification )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

extern const std::vector<uint8_t> payload { 
    0x1e, 
    0x00, 0x10,  //  16 TLVs
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
        0x05, 0xa0,  //  24 hours
        0x00, 0x00, 
        0x00, 0x00, 
        0x00, 0x00, 
        0x00, 0x00, 
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
    0x00, 0x25,
        0x00, 0x00, 0x1c, 0x20,  //  7200
        0x00, 0x01, 0x51, 0x80,  //  86400
        0x07,  //  7 metrics
        0x00, 0x01, 0x00, 0x00,
        0x00, 0x09, 0x00, 0x08,
        0x00, 0x0a, 0x00, 0x10,
        0x00, 0x0b, 0x00, 0x20,
        0x00, 0x02, 0x00, 0x00,
        0x00, 0x03, 0x00, 0x00,
        0x00, 0x04, 0x00, 0x00,
    //  TLV 6
    0x00, 0x06,  // Channel selection
    0x00, 0x1d,
        0x07,
        0x00, 0x05, 0x00, 0x00,
        0x00, 0x09, 0x00, 0x08,
        0x00, 0x0a, 0x00, 0x10,
        0x00, 0x0b, 0x00, 0x20,
        0x00, 0x06, 0x00, 0x00,
        0x00, 0x07, 0x00, 0x00,
        0x00, 0x08, 0x00, 0x00,
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
        0xff, 0x00,  //  disconnect display = 0 (disabled)
    //  TLV 10
    0x00, 0x0a,  //  Focus AL Display
    0x00, 0x0b,
        0x03,
        0x06,
        0x00, 0x3e, 0x55,
        0x02, 0x41, 0x3b,
        0x08, 0x47, 0x47,
    //  TLV 11, for OV
    0x00, 0x0b,  //  OV/UV configuration
    0x00, 0x12,
        0x7f,        //  Meter ID
        0x07, 0xe7,  //  Event ID
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
    //  TLV 11 again, for UV this time
    0x00, 0x0b,  //  OV/UV configuration
    0x00, 0x12,
        0x7f,        //  Meter ID
        0x07, 0xe6,  //  Event ID
        0x01,        //  OV/UV Enable/disable
        0x0e,        //  New alarm reporting interval
        0x03,        //  Alarm repeat interval
        0x02,        //  Set alarm repeat count
        0x05,        //  Clear alarm repeat count
        0x0d,        //  Severity
        0x0d, 0x0d, 0x0d, 0x0d,  //  Set threshold value
        0x0e,        //  Unit of measure
        0x0e, 0x0e,  //  UOM modifier 1
        0x0e, 0x0e,  //  UOM modifier 2
    //  TLV 12
    0x00, 0x0c,  //  Temperature configuration
    0x00, 0x07,
        0x01,  //  Enable/disable
        0x17, 0x01,  //  high temp threshold
        0xff, 0xe4,  //  low temp threshold
        0x07,  //  repeat interval
        0x0b,  //  Max repeats
    //  TLV 13
    0x00, 0x0d,  //  Data Streaming configuration
    0x00, 0x15,
        0x03,  //  number of metrics
        0x01,  //  data streaming on/off
        0x00, 0x05,  //  metric ID 1
        0x01,          //  metric ID 1 enable/disable
        0x05,          //  metric ID 1 interval
        0x00,          //  metric ID 1 status
        0x00, 0x73,  //  metric ID 2
        0x00,          //  metric ID 2 enable/disable
        0x0f,          //  metric ID 2 interval
        0x01,          //  metric ID 2 status
        0x00, 0x50,  //  metric ID 3
        0x01,          //  metric ID 3 enable/disable
        0x1e,          //  metric ID 3 interval
        0x02,          //  metric ID 3 status
        0xde, 0xad, 0xbe, 0xef, //  DS metrics sequence number
    //  TLV 14
    0x00, 0x0e,  //  Demand Interval configuration
    0x00, 0x01,
        0x06,  //  6 minutes
    //  TLV 15
    0x00, 0x0f,  //  Voltage profile status
    0x00, 0x05,
        0x02,  //  temporary enable
        0x51, 0x23, 0x45, 0x67  //  enabled-until timestamp
    };

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
        std::vector<unsigned char> response{
            0x1e,
            0x00, 0x02,
            //  TLV 5
            0x00, 0x05,  //  Interval recording
            0x00, 0x25,
            0x00, 0x00, 0x1c, 0x20, //  7200
            0x00, 0x01, 0x51, 0x80, //  86400
            0x07,  //  7 metrics
            0x00, 0x01, 0x00, 0x00,
            0x00, 0x09, 0x00, 0x08,
            0x00, 0x0a, 0x00, 0x10,
            0x00, 0x0b, 0x00, 0x20,
            0x00, 0x02, 0x00, 0x00,
            0x00, 0x03, 0x00, 0x00,
            0x00, 0x04, 0x00, 0x00,
            //  TLV 6
            0x00, 0x06,  // Channel selection
            0x00, 0x1d,
            0x07,
            0x00, 0x05, 0x00, 0x00,
            0x00, 0x09, 0x00, 0x08,
            0x00, 0x0a, 0x00, 0x10,
            0x00, 0x0b, 0x00, 0x20,
            0x00, 0x06, 0x00, 0x00,
            0x00, 0x07, 0x00, 0x00,
            0x00, 0x08, 0x00, 0x00 };

        const auto rcv = cmd.decodeCommand(execute_time, response);

        std::string exp = "Interval recording configuration:"
            "\n    Recording interval : 7,200 seconds"
            "\n    Reporting interval : 86,400 seconds"
            "\n    Interval metrics   : 1, 2, 3, 4"
            "\n    Coincident metrics : 9, 10, 11"
            "\nChannel selection configuration:"
            "\n    Midnight metrics   : 5, 6, 7, 8"
            "\n    Coincident metrics : 9, 10, 11";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_handle_node_originated)
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
        "\nTOU enable configuration:"
        "\n    TOU enabled");

    BOOST_REQUIRE(cmd.touEnabled);

    BOOST_CHECK_EQUAL(cmd.touEnabled.value(), Cti::Devices::Commands::RfnTouConfigurationCommand::TouEnable);
}

BOOST_AUTO_TEST_CASE(test_channel_configuration)
{
    const std::vector<uint8_t> payload{
        0x1e,
        0x00, 0x02,
        //  TLV 5
        0x00, 0x05,  //  Interval recording
        0x00, 0x25,
        0x00, 0x00, 0x1c, 0x20, //  7200
        0x00, 0x01, 0x51, 0x80, //  86400
        0x07,  //  7 metrics
        0x00, 0x01, 0x00, 0x00,
        0x00, 0x09, 0x00, 0x08,
        0x00, 0x0a, 0x00, 0x10,
        0x00, 0x0b, 0x00, 0x20,
        0x00, 0x02, 0x00, 0x00,
        0x00, 0x03, 0x00, 0x00,
        0x00, 0x04, 0x00, 0x00,
        //  TLV 6
        0x00, 0x06,  // Channel selection
        0x00, 0x1d,
        0x07,  //  7 metrics
        0x00, 0x05, 0x00, 0x00,
        0x00, 0x09, 0x00, 0x08,
        0x00, 0x0a, 0x00, 0x10,
        0x00, 0x0b, 0x00, 0x20,
        0x00, 0x06, 0x00, 0x00,
        0x00, 0x07, 0x00, 0x00,
        0x00, 0x08, 0x00, 0x00 };

    RfnConfigNotificationCommand cmd;

    std::string exp = "Device Configuration Request:"
        "\nInterval recording configuration:"
        "\n    Recording interval : 7,200 seconds"
        "\n    Reporting interval : 86,400 seconds"
        "\n    Interval metrics   : 1, 2, 3, 4"
        "\n    Coincident metrics : 9, 10, 11"
        "\nChannel selection configuration:"
        "\n    Midnight metrics   : 5, 6, 7, 8"
        "\n    Coincident metrics : 9, 10, 11";

    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK(result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description, exp);

    const auto interval_expected = { 1, 2, 3, 4 };
    const auto midnight_expected = { 5, 6, 7, 8 };

    BOOST_CHECK_EQUAL_RANGES(*cmd.channelSelections, midnight_expected);
    BOOST_CHECK_EQUAL_RANGES(cmd.intervalRecording->intervalMetrics, interval_expected);
}

BOOST_AUTO_TEST_CASE(test_channel_configuration_no_coincidents)
{
    const std::vector<uint8_t> payload{
        0x1e,
        0x00, 0x02,
        //  TLV 5
        0x00, 0x05,  //  Interval recording
        0x00, 0x19,
        0x00, 0x00, 0x1c, 0x20, //  7200
        0x00, 0x01, 0x51, 0x80, //  86400
        0x04,  //  4 metrics
        0x00, 0x01, 0x00, 0x00,
        0x00, 0x02, 0x00, 0x00,
        0x00, 0x03, 0x00, 0x00,
        0x00, 0x04, 0x00, 0x00,
        //  TLV 6
        0x00, 0x06,  // Channel selection
        0x00, 0x11,
        0x04,
        0x00, 0x05, 0x00, 0x00,
        0x00, 0x06, 0x00, 0x00,
        0x00, 0x07, 0x00, 0x00,
        0x00, 0x08, 0x00, 0x00 };

    RfnConfigNotificationCommand cmd;

    std::string exp = "Device Configuration Request:"
        "\nInterval recording configuration:"
        "\n    Recording interval : 7,200 seconds"
        "\n    Reporting interval : 86,400 seconds"
        "\n    Interval metrics   : 1, 2, 3, 4"
        "\nChannel selection configuration:"
        "\n    Midnight metrics : 5, 6, 7, 8";

    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK(result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description, exp);

    const auto intervals_expected = { 1, 2, 3, 4 };
    const auto channels_expected  = { 5, 6, 7, 8 };

    BOOST_CHECK_EQUAL_RANGES(*cmd.channelSelections, channels_expected);
    BOOST_CHECK_EQUAL_RANGES(cmd.intervalRecording->intervalMetrics, intervals_expected);
}

BOOST_AUTO_TEST_CASE(test_temperature)
{
    const std::vector<uint8_t> payload{
        0x1e,
        0x00, 0x01,
        0x00, 0x0c,  //  Temperature configuration
        0x00, 0x07,
        0x01,  //  Enable/disable
        0xff, 0xee,  //  high temp threshold
        0xff, 0xe4,  //  low temp threshold
        0x77,  //  repeat interval
        0xbb,  //  Max repeats
    };

    RfnConfigNotificationCommand cmd;

    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK(result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description,
        "Device Configuration Request:"
        "\nTemperature alarm configuration:"
        "\n    Temperature alarming enabled : true"
        "\n    High temp threshold          : -18"
        "\n    Low temp threshold           : -28"
        "\n    Repeat interval              : 119 minutes"
        "\n    Max repeats                  : 187");

    BOOST_REQUIRE(cmd.temperature);

    BOOST_CHECK_EQUAL(cmd.temperature->alarmEnabled, true);
    BOOST_CHECK_EQUAL(cmd.temperature->alarmHighTempThreshold, -18);
    BOOST_CHECK_EQUAL(cmd.temperature->alarmRepeatCount, 187);
    BOOST_CHECK_EQUAL(cmd.temperature->alarmRepeatInterval, 119);
}

BOOST_AUTO_TEST_CASE(test_all_tlvs)
{
    Cti::Test::set_to_central_timezone();

    const std::string expected = 
        "Device Configuration Request:"
        "\nTOU enable configuration:"
        "\n    TOU enabled"
        "\nTOU schedule configuration:"
        "\n    Day table               : 1, 2, 3, 4, 4, 3, 2, 1"
        "\n    Schedule 1 switch times : 00:00, 00:03, 00:04, 00:08, 00:09, 00:14"
        "\n    Schedule 2 switch times : 00:00, 00:09, 00:11, 00:17, 00:22, 00:25"
        "\n    Schedule 3 switch times : 00:00, 00:05, 00:13, 00:22, 00:29, 00:38"
        "\n    Schedule 4 switch times : 00:00, 00:00, 00:00, 00:00, 00:00, 00:00"
        "\n    Schedule 1 rates        : B, A, D, C, B, A"
        "\n    Schedule 2 rates        : D, C, B, A, D, C"
        "\n    Schedule 3 rates        : C, B, A, D, C, B"
        "\n    Schedule 4 rates        : A, D, C, B, A, D"
        "\n    Default rate            : B"
        "\nTOU holiday configuration:"
        "\n     Date 1 - 2018-Mar-14"
        "\n     Date 2 - 2018-Jun-27"
        "\n     Date 3 - 2018-Feb-07"
        "\nDemand freeze configuration:"
        "\n    Demand freeze day: 32"
        "\nInterval recording configuration:"
        "\n    Recording interval : 7,200 seconds"
        "\n    Reporting interval : 86,400 seconds"
        "\n    Interval metrics   : 1, 2, 3, 4"
        "\n    Coincident metrics : 9, 10, 11"
        "\nChannel selection configuration:"
        "\n    Midnight metrics   : 5, 6, 7, 8"
        "\n    Coincident metrics : 9, 10, 11"
        "\nDisconnect configuration:"
        "\n    Disconnect mode  : demand threshold"
        "\n    Reconnect method : 1"
        "\n    Demand interval  : 24 minutes"
        "\n    Demand threshold : 3.1000000000000001kW"
        "\n    Connect delay    : 17 minutes"
        "\n    Max disconnects  : 7"
        "\nVoltage profile configuration:"
        "\n    Voltage demand interval  : 105 seconds"
        "\n    Voltage profile interval : 11 minutes"
        "\nC2SX display:"
        "\n    Slot 0             : 4"
        "\n    Slot 1             : 8"
        "\n    Slot 2             : 12"
        "\n    Display digits     : 5"
        "\n    Cycle time         : 7"
        "\n    Disconnect display : 0"
        "\nFocus AL display:"
        "\n    Display time : 6 seconds"
        "\n    Metric 0     : > U"
        "\n    Metric 2     : A *"
        "\n    Metric 8     : G G"
        "\nOV/UV configuration:"
        "\n    Meter ID                     : 127"
        "\n    Event ID                     : 2023"
        "\n    OV/UV alarming enabled       : true"
        "\n    New alarm reporting interval : 14 minutes"
        "\n    Alarm repeat interval        : 3 minutes"
        "\n    Set alarm repeat count       : 2"
        "\n    Clear alarm repeat count     : 5"
        "\n    Severity                     : 13"
        "\n    Set threshold value          : 235802.12599999999"
        "\n    Unit of measure              : 14"
        "\n    UOM modifier 1               : 3598"
        "\n    UOM modifier 2               : 3598"
        "\nOV/UV configuration:"
        "\n    Meter ID                     : 127"
        "\n    Event ID                     : 2022"
        "\n    OV/UV alarming enabled       : true"
        "\n    New alarm reporting interval : 14 minutes"
        "\n    Alarm repeat interval        : 3 minutes"
        "\n    Set alarm repeat count       : 2"
        "\n    Clear alarm repeat count     : 5"
        "\n    Severity                     : 13"
        "\n    Set threshold value          : 218959.117"
        "\n    Unit of measure              : 14"
        "\n    UOM modifier 1               : 3598"
        "\n    UOM modifier 2               : 3598"
        "\nTemperature alarm configuration:"
        "\n    Temperature alarming enabled : true"
        "\n    High temp threshold          : 5889"
        "\n    Low temp threshold           : -28"
        "\n    Repeat interval              : 7 minutes"
        "\n    Max repeats                  : 11"
        "\nData Streaming configuration:"
        "\n    Global enable : true"
        "\n    Metric count  : 3"
        "\n    Metric 5      : Enabled @ 5 minutes, status 0"
        "\n    Metric 115    : Disabled @ 15 minutes, status 1"
        "\n    Metric 80     : Enabled @ 30 minutes, status 2"
        "\n    Sequence      : 3735928559"
        "\nDemand interval configuration:"
        "\n    Demand Interval : 6 minutes"
        "\nVoltage profile status:"
        "\n    Mode          : Temporarily enabled"
        "\n    Temporary end : 02/19/2013 03:27:03";

    RfnConfigNotificationCommand cmd;

    const auto result = cmd.handleResponse(execute_time, payload);

    BOOST_REQUIRE_EQUAL(result.size(), 1);

    BOOST_CHECK_EQUAL(result[0].status, ClientErrors::None);
    BOOST_CHECK      (result[0].points.empty());
    BOOST_CHECK_EQUAL(result[0].description, expected);

    BOOST_REQUIRE(cmd.c2sxDisplay);
    BOOST_CHECK_EQUAL(cmd.c2sxDisplay->disconnectDisplayDisabled.value(), true);
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
    const auto focusDisplayExpected = { 0, 2, 8 };
    BOOST_CHECK_EQUAL_RANGES(cmd.focusDisplay->displayItems, focusDisplayExpected);

    BOOST_REQUIRE(cmd.intervalRecording);
    const auto intervalMetricsExpected = { 1, 2, 3, 4 };
    BOOST_CHECK_EQUAL_RANGES(cmd.intervalRecording->intervalMetrics, intervalMetricsExpected);
    BOOST_CHECK_EQUAL(cmd.intervalRecording->recordingInterval, 7200);
    BOOST_CHECK_EQUAL(cmd.intervalRecording->reportingInterval, 86400);

    BOOST_REQUIRE(cmd.ovuv);
    BOOST_CHECK_EQUAL(cmd.ovuv->ovThreshold.value(), 218959.117);
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

    BOOST_REQUIRE(cmd.voltageProfile);
    BOOST_CHECK_EQUAL(cmd.voltageProfile->voltageDemandInterval, 105);
    BOOST_CHECK_EQUAL(cmd.voltageProfile->voltageProfileInterval, 11);

    BOOST_REQUIRE(cmd.voltageProfileStatus);
    BOOST_CHECK_EQUAL(cmd.voltageProfileStatus->enabled, false);
    BOOST_REQUIRE(cmd.voltageProfileStatus->temporaryEnd);
    BOOST_CHECK_EQUAL(cmd.voltageProfileStatus->temporaryEnd.value(), CtiTime(CtiDate(19, 2, 2013), 3, 27, 3));

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
        const auto times0 = { "00:00", "00:03", "00:04", "00:08", "00:09", "00:14" };
        const auto times1 = { "00:00", "00:09", "00:11", "00:17", "00:22", "00:25" };
        const auto times2 = { "00:00", "00:05", "00:13", "00:22", "00:29", "00:38" };
        const auto times3 = { "00:00", "00:00", "00:00", "00:00", "00:00", "00:00" };
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

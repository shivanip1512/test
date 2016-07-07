#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "dev_rfnMeter.h"
#include "ctidate.h"
#include "cmd_rfn.h"
#include "config_device.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"
#include "deviceconfig_test_helpers.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_RfnMeterDevice : RfnMeterDevice
{
    using RfnMeterDevice::handleCommandResult;
    using CtiTblPAOLite::_type;
};

struct test_state_rfnMeter
{
    std::auto_ptr<CtiRequestMsg> request;
    RfnDevice::ReturnMsgList     returnMsgs;
    RfnDevice::RfnCommandList    rfnRequests;

    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfnMeter() :
        request( new CtiRequestMsg ),
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }

    void resetTestState()
    {
        request.reset( new CtiRequestMsg );
        returnMsgs.clear();
        rfnRequests.clear();
    }
};


namespace std {

    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const vector<unsigned char> &v);
    ostream& operator<<(ostream& out, const vector<bool> &v);
}


const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );
const CtiTime decode_time ( CtiDate( 27, 8, 2013 ) , 16 );


BOOST_FIXTURE_TEST_SUITE( test_dev_rfnMeter, test_state_rfnMeter )

BOOST_AUTO_TEST_CASE( putconfig_install_temperaturealarm_success_no_tlv )
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::TemperatureAlarmEnabled,           "true" );
    cfg.insertValue( RfnStrings::TemperatureAlarmRepeatInterval,    "17" );
    cfg.insertValue( RfnStrings::TemperatureAlarmRepeatCount,       "3" );
    cfg.insertValue( RfnStrings::TemperatureAlarmHighTempThreshold, "80" );

    {
        CtiCommandParser parse("putconfig install temperaturealarm");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            const std::vector< unsigned char > exp {
                    0x88, 0x00, 0x01,
                        0x01, 0x07, 0x01, 0x00, 0x1a, 0x00, 0x10, 0x11, 0x03 };

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            const std::vector< unsigned char > response {
                    0x89, 0x00, 0x00, 0x00 };

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp =
                    "Status: Success (0)";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string tempAlarmEnabled,
                    tempAlarmHighTempThreshold,
                    tempAlarmRepeatInterval,
                    tempAlarmRepeatCount;

        using DPI = CtiTableDynamicPaoInfo;

        BOOST_CHECK( dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmIsEnabled ) );
        BOOST_CHECK( dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmHighTempThreshold ) );
        BOOST_CHECK( dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatInterval ) );
        BOOST_CHECK( dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatCount ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmUnsupported ) );

        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmIsEnabled,         tempAlarmEnabled );
        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmHighTempThreshold, tempAlarmHighTempThreshold );
        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmRepeatInterval,    tempAlarmRepeatInterval );
        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmRepeatCount,       tempAlarmRepeatCount );

        BOOST_CHECK_EQUAL( tempAlarmEnabled,           "1" );
        BOOST_CHECK_EQUAL( tempAlarmHighTempThreshold, "26" );
        BOOST_CHECK_EQUAL( tempAlarmRepeatInterval,    "17" );
        BOOST_CHECK_EQUAL( tempAlarmRepeatCount,       "3" );
    }
}

BOOST_AUTO_TEST_CASE( putconfig_install_temperaturealarm_success_returnMismatch )
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::TemperatureAlarmEnabled,           "true" );
    cfg.insertValue( RfnStrings::TemperatureAlarmRepeatInterval,    "15" );
    cfg.insertValue( RfnStrings::TemperatureAlarmRepeatCount,       "7" );
    cfg.insertValue( RfnStrings::TemperatureAlarmHighTempThreshold, "60" );

    {
        CtiCommandParser parse("putconfig install temperaturealarm");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            const std::vector< unsigned char > exp {
                    0x88, 0x00, 0x01,
                        0x01, 0x07, 0x01, 0x00, 0x0f, 0x00, 0x05, 0x0f, 0x07 };

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            const std::vector< unsigned char > response {
                    0x89, 0x00, 0x00, 0x01, 0x01, 0x07, 0x01, 0x00, 0x2d, 0x00, 0x23, 0x05, 0x03 };

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp =
                    "Status: Success (0)"
                    "\nState: Alarm Enabled (1)"
                    "\nHigh Temperature Threshold: 45 degrees (0x002d)"
                    "\nLow Temperature Threshold: 35 degrees (0x0023)"
                    "\nAlarm Repeat Interval: 5 minutes"
                    "\nAlarm Repeat Count: 3 counts";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string tempAlarmEnabled,
                    tempAlarmHighTempThreshold,
                    tempAlarmRepeatInterval,
                    tempAlarmRepeatCount;

        using DPI = CtiTableDynamicPaoInfo;

        BOOST_CHECK( dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmIsEnabled ) );
        BOOST_CHECK( dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmHighTempThreshold ) );
        BOOST_CHECK( dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatInterval ) );
        BOOST_CHECK( dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatCount ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmUnsupported ) );

        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmIsEnabled,         tempAlarmEnabled );
        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmHighTempThreshold, tempAlarmHighTempThreshold );
        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmRepeatInterval,    tempAlarmRepeatInterval );
        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmRepeatCount,       tempAlarmRepeatCount );

        BOOST_CHECK_EQUAL( tempAlarmEnabled,           "1" );
        BOOST_CHECK_EQUAL( tempAlarmHighTempThreshold, "45" );
        BOOST_CHECK_EQUAL( tempAlarmRepeatInterval,    "5" );
        BOOST_CHECK_EQUAL( tempAlarmRepeatCount,       "3" );
    }
}

BOOST_AUTO_TEST_CASE( putconfig_install_temperaturealarm_failure )
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::TemperatureAlarmEnabled,           "true" );
    cfg.insertValue( RfnStrings::TemperatureAlarmRepeatInterval,    "15" );
    cfg.insertValue( RfnStrings::TemperatureAlarmRepeatCount,       "3" );
    cfg.insertValue( RfnStrings::TemperatureAlarmHighTempThreshold, "60" );

    {
        CtiCommandParser parse("putconfig install temperaturealarm");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            const std::vector< unsigned char > exp {
                    0x88, 0x00, 0x01,
                        0x01, 0x07, 0x01, 0x00, 0x0f, 0x00, 0x05, 0x0f, 0x03 };

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            const std::vector< unsigned char > response {
                    0x89, 0x00, 0x01, 0x00 };

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp =
                    "Status: Failure (1)";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        using DPI = CtiTableDynamicPaoInfo;

        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmIsEnabled ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmHighTempThreshold ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatInterval ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatCount ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmUnsupported ) );
    }
}

BOOST_AUTO_TEST_CASE( putconfig_install_temperaturealarm_unsupported )
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( RfnStrings::TemperatureAlarmEnabled,           "true" );
    cfg.insertValue( RfnStrings::TemperatureAlarmRepeatInterval,    "15" );
    cfg.insertValue( RfnStrings::TemperatureAlarmRepeatCount,       "3" );
    cfg.insertValue( RfnStrings::TemperatureAlarmHighTempThreshold, "60" );

    {
        CtiCommandParser parse("putconfig install temperaturealarm");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            const std::vector< unsigned char > exp {
                    0x88, 0x00, 0x01,
                        0x01, 0x07, 0x01, 0x00, 0x0f, 0x00, 0x05, 0x0f, 0x03 };

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            const std::vector< unsigned char > response {
                    0x89, 0x00, 0x02, 0x00 };

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp =
                    "Status: Unsupported (2)";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        using DPI = CtiTableDynamicPaoInfo;

        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmIsEnabled ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmHighTempThreshold ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatInterval ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatCount ) );
        BOOST_CHECK(   dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmUnsupported ) );

        std::string tempAlarmUnsupported;

        dut.getDynamicInfo( DPI::Key_RFN_TempAlarmUnsupported, tempAlarmUnsupported );

        BOOST_CHECK_EQUAL( tempAlarmUnsupported, "1" );
    }
}

void config_a_meter(Cti::Test::test_DeviceConfig &cfg)
{
    /* Configuration settings */
    const std::map<std::string, std::string> configItems = boost::assign::map_list_of
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix, "5" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DELIVERED_KWH" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".0."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".1."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "RECEIVED_KWH" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".1."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "MIDNIGHT" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".2."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "SUM_KWH" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".2."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "INTERVAL" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".3."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "NET_KWH" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".3."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "INTERVAL" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".4."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Attribute, "DEMAND" )
        ( RfnStrings::ChannelConfiguration::EnabledChannels_Prefix + ".4."
        + RfnStrings::ChannelConfiguration::EnabledChannels::Read, "INTERVAL" )
        ( RfnStrings::ChannelConfiguration::RecordingIntervalMinutes, "123" )
        ( RfnStrings::ChannelConfiguration::ReportingIntervalMinutes, "456" );

    cfg.addCategory(
        Cti::Config::Category::ConstructCategory(
        "rfnChannelConfiguration",
        configItems ) );
}

/**
Device is a subset of the configuration.

This test does a "putconfig install channelconfig verify" and checks that it properly notices that the 
device configuration is missing several midnight and interval channel settings.
*/
BOOST_AUTO_TEST_CASE( putconfig_install_channel_verify_missing )
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    /* Device settings */
    std::vector<unsigned long> paoMidnightMetrics = { 1, 2, 3 };
    std::vector<unsigned long> paoIntervalMetrics = { 3, 4 };

    dut.setID( 1234 );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, paoMidnightMetrics );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, paoIntervalMetrics );
    dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, 123 * 60 );
    dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, 456 * 60 );

    config_a_meter(cfg);

    /* And now we run the command. */
    {
        CtiCommandParser parse( "putconfig install channelconfig verify" );

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests ) );

        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        Cti::Test::msgsEqual( returnMsgs, ClientErrors::ConfigNotCurrent, {
            "Midnight channel config possibly not supported by meter.  Meter is missing NET_KWH, DEMAND",
            "Config: DELIVERED_KWH, RECEIVED_KWH, SUM_KWH, NET_KWH, DEMAND",
            "Meter: DELIVERED_KWH, RECEIVED_KWH, SUM_KWH",
            "Interval channel config possibly not supported by meter.  Meter is missing DEMAND",
            "Config: SUM_KWH, NET_KWH, DEMAND",
            "Meter: SUM_KWH, NET_KWH",
            "Config channelconfig is NOT current."
        } );
    }
}

/**
Configuration is a subset of the Device.

This test does a "putconfig install channelconfig verify" and checks that even though there
are extra meter channels, this is ok.
*/
BOOST_AUTO_TEST_CASE(test_dev_rfnMeter_putconfig_install_channel_verify_extra)
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

                                                         /* Device settings */
    std::vector<unsigned long> paoMidnightMetrics = { 1, 2, 3, 4, 5, 9 };
    std::vector<unsigned long> paoIntervalMetrics = { 1, 2, 3, 4, 5, 9 };

    dut.setID(1234);
    dut.setDynamicInfo(CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, paoMidnightMetrics);
    dut.setDynamicInfo(CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, paoIntervalMetrics);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, 123 * 60);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, 456 * 60);

    config_a_meter(cfg);

    /* And now we run the command. */
    {
        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));

        BOOST_REQUIRE_EQUAL(0, rfnRequests.size());

        Cti::Test::msgsEqual(returnMsgs, ClientErrors::ConfigNotCurrent, {
            "Midnight channel program mismatch.  Meter also contains PEAK_DEMAND_FROZEN",
            "Config: DELIVERED_KWH, RECEIVED_KWH, SUM_KWH, NET_KWH, DEMAND",
            "Meter: DELIVERED_KWH, RECEIVED_KWH, SUM_KWH, NET_KWH, DEMAND, PEAK_DEMAND_FROZEN",
            "Interval channel program mismatch.  Meter also contains DELIVERED_KWH, RECEIVED_KWH, PEAK_DEMAND_FROZEN",
            "Config: SUM_KWH, NET_KWH, DEMAND",
            "Meter: DELIVERED_KWH, RECEIVED_KWH, SUM_KWH, NET_KWH, DEMAND, PEAK_DEMAND_FROZEN",
            "Config channelconfig is NOT current.",
        });
    }
}

/**
This test does a "putconfig install channelconfig verify" and checks that it properly notices that the 
device configuration is missing several midnight and interval channel settings.
*/
BOOST_AUTO_TEST_CASE( putconfig_install_channel_verify_match )
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    /* Device settings */
    std::vector<unsigned long> paoMidnightMetrics = { 1, 2, 3, 4, 5 };
    std::vector<unsigned long> paoIntervalMetrics = { 3, 4, 5 };

    dut.setID( 1234 );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, paoMidnightMetrics );
    dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, paoIntervalMetrics );
    dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, 123 * 60 );
    dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, 456 * 60 );

    config_a_meter(cfg);

    /* And now we run the command. */
    {
        CtiCommandParser parse( "putconfig install channelconfig verify" );

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests ) );

        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        Cti::Test::msgsEqual( returnMsgs, ClientErrors::None, {
            "Config channelconfig is current."
        } );
    }
}

/**
This test does a "putconfig install channelconfig verify" and checks that it properly notices that the 
device configuration is missing several midnight and interval channel settings.
*/
BOOST_AUTO_TEST_CASE(test_dev_rfnMeter_putconfig_install_channel_verify_disjoint)
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    /* Device settings */
    std::vector<unsigned long> paoMidnightMetrics = { 9, 11, 12 };
    std::vector<unsigned long> paoIntervalMetrics = { 9, 11, 12 };

    dut.setID(1234);
    dut.setDynamicInfo(CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, paoMidnightMetrics);
    dut.setDynamicInfo(CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, paoIntervalMetrics);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, 123 * 60);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, 456 * 60);

    config_a_meter(cfg);

    /* And now we run the command. */
    {
        CtiCommandParser parse("putconfig install channelconfig verify");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));

        BOOST_REQUIRE_EQUAL(0, rfnRequests.size());

        Cti::Test::msgsEqual(returnMsgs, ClientErrors::ConfigNotCurrent, {
            "Midnight channel program mismatch.",
            "Config: DELIVERED_KWH, RECEIVED_KWH, SUM_KWH, NET_KWH, DEMAND",
            "Meter: PEAK_DEMAND_FROZEN, USAGE_FROZEN, RECEIVED_KWH_FROZEN",
            "Interval channel program mismatch.",
            "Config: SUM_KWH, NET_KWH, DEMAND",
            "Meter: PEAK_DEMAND_FROZEN, USAGE_FROZEN, RECEIVED_KWH_FROZEN",
            "Config channelconfig is NOT current."
        });
    }
}

BOOST_AUTO_TEST_CASE( putconfig_install_channel_verify_uninitialized )
{
    test_RfnMeterDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    /* Device settings */
    //std::vector<unsigned long> paoMidnightMetrics = {};
    //std::vector<unsigned long> paoIntervalMetrics = {};

    dut.setID( 1234 );
    ////dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, paoMidnightMetrics );
    ////dut.setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, paoIntervalMetrics );
    //dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, 123 * 60 );
    //dut.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, 456 * 60 );

    config_a_meter(cfg);

    /* And now we run the command. */
    {
        CtiCommandParser parse( "putconfig install channelconfig verify" );

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests ) );

        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        Cti::Test::msgsEqual( returnMsgs, ClientErrors::ConfigNotCurrent, {
            "Midnight channel program mismatch.  Meter data is Empty.",
            "Interval channel program mismatch.  Meter data is Empty.",
            "Config Channel Recording Interval (sec) did not match. Config: 7380, Meter: Uninitialized",
            "Config Channel Reporting Interval (sec) did not match. Config: 27360, Meter: Uninitialized",
            "Config channelconfig is NOT current."
        } );
    }
}

BOOST_AUTO_TEST_CASE( putconfig_behavior_rfndatastreaming_disabled_unassigned )
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_REQUIRE_EQUAL(1, rfnRequests.size());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "1 command queued for device");
        }
    }

    auto rfnRequest_itr = rfnRequests.begin();
    {
        const auto& command = *rfnRequest_itr++;
        {
            auto rcv = command->executeCommand(execute_time);

            const std::vector<unsigned char> exp {
                0x86,  //  command code
                0x00,  //  number of metrics
                0x00 };//  data streaming OFF

            BOOST_CHECK_EQUAL(rcv, exp);
        }

        {
            const std::vector<unsigned char> response{
                0x87,       //  command code
                0x03,  //  number of metrics
                0x00,  //  data streaming on/off
                0x00, 0x05,  //  metric ID 1
                0x01,        //  metric ID 1 enable/disable
                0x05,        //  metric ID 1 interval
                0x00, 0x73,  //  metric ID 2
                0x00,        //  metric ID 2 enable/disable
                0x0f,        //  metric ID 2 interval
                0x00, 0x53,  //  metric ID 3
                0x01,        //  metric ID 3 enable/disable
                0x1e,        //  metric ID 3 interval
                0xde, 0xad, 0xbe, 0xef };  //  DS metrics sequence number

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand(decode_time, response);

            const std::string exp =
                R"json({
"streamingEnabled" : false,
"configuredMetrics" : [
  {
    "attribute" : "DEMAND",
    "interval" : 5,
    "enabled" : true
  },
  {
    "attribute" : "VOLTAGE",
    "interval" : 15,
    "enabled" : false
  },
  {
    "attribute" : "POWER_FACTOR",
    "interval" : 30,
    "enabled" : true
  }],
"sequence" : 3735928559
})json";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }
    }
}

BOOST_AUTO_TEST_CASE( putconfig_behavior_rfndatastreaming_disabled_no_channels )
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    b.behaviorManagerHandle->behaviorValues.emplace("channels", "0");

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_CHECK(rfnRequests.empty());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 284);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Configuration data is invalid.");
        }
    }
}

BOOST_AUTO_TEST_CASE( putconfig_behavior_rfndatastreaming_two_channels_no_device_report )
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    b.behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" }};

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_REQUIRE_EQUAL(1, rfnRequests.size());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "1 command queued for device");
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand(execute_time);

            const std::vector<unsigned char> exp {
                0x86,  //  command code
                0x02,  //  number of metrics
                0x01,  //  data streaming ON
                0x00, 0x73,  //  metric ID 1
                0x01,        //  metric ID 1 enable/disable
                0x04,        //  metric ID 1 interval
                0x00, 0x05,  //  metric ID 2
                0x01,        //  metric ID 2 enable/disable
                0x07         //  metric ID 2 interval
            };

            BOOST_CHECK_EQUAL(rcv, exp);
        }
    }
}


BOOST_AUTO_TEST_CASE(putconfig_behavior_rfndatastreaming_two_channels_device_matches)
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    b.behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" }};

    b.behaviorManagerHandle->behaviorReport = std::map<std::string, std::string> {
        { "enabled", "true" },
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.0.enabled", "true" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" },
        { "channels.1.enabled", "true" }};

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_CHECK(rfnRequests.empty());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 220);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Unknown Error Code (220)");
        }
    }
}


BOOST_AUTO_TEST_CASE(putconfig_behavior_rfndatastreaming_two_channels_device_disabled)
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    b.behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" }};

    b.behaviorManagerHandle->behaviorReport = std::map<std::string, std::string> {
        { "enabled", "false" },
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.0.enabled", "true" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" },
        { "channels.1.enabled", "true" }};

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_REQUIRE_EQUAL(1, rfnRequests.size());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "1 command queued for device");
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand(execute_time);

            const std::vector<unsigned char> exp {
                0x86,  //  command code
                0x00,  //  number of metrics
                0x01   //  data streaming ON
            };

            BOOST_CHECK_EQUAL(rcv, exp);
        }
    }
}


BOOST_AUTO_TEST_CASE(putconfig_behavior_rfndatastreaming_two_channels_one_device_channel_disabled)
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    b.behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" }};

    b.behaviorManagerHandle->behaviorReport = std::map<std::string, std::string> {
        { "enabled", "true" },
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.0.enabled", "true" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" },
        { "channels.1.enabled", "false" }};

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_REQUIRE_EQUAL(1, rfnRequests.size());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "1 command queued for device");
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand(execute_time);

            const std::vector<unsigned char> exp {
                0x86,  //  command code
                0x01,  //  number of metrics
                0x01,  //  data streaming ON
                0x00, 0x05, 
                0x01, 
                0x07
            };

            BOOST_CHECK_EQUAL(rcv, exp);
        }
    }
}


BOOST_AUTO_TEST_CASE(putconfig_behavior_rfndatastreaming_two_channels_one_channel_interval_mismatch)
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    b.behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" }};

    b.behaviorManagerHandle->behaviorReport = std::map<std::string, std::string> {
        { "enabled", "true" },
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.0.enabled", "true" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "17" },
        { "channels.1.enabled", "true" }};

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_REQUIRE_EQUAL(1, rfnRequests.size());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "1 command queued for device");
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand(execute_time);

            const std::vector<unsigned char> exp {
                0x86,  //  command code 
                0x01,  //  number of metrics 
                0x01,  //  data streaming ON 
                0x00, 0x05, 
                0x01, 
                0x07
            };

            BOOST_CHECK_EQUAL(rcv, exp);
        }
    }
}


BOOST_AUTO_TEST_CASE(putconfig_behavior_rfndatastreaming_two_channels_device_opposite)
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    b.behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" }};

    b.behaviorManagerHandle->behaviorReport = std::map<std::string, std::string> {
        { "enabled", "true" },
        { "channels", "4" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "40" },
        { "channels.0.enabled", "false" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "17" },
        { "channels.1.enabled", "false" },
        { "channels.2.attribute", "KVAR" },
        { "channels.2.interval", "9" },
        { "channels.2.enabled", "true" },
        { "channels.3.attribute", "POWER_FACTOR" },
        { "channels.3.interval", "11" },
        { "channels.3.enabled", "true" }};

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_REQUIRE_EQUAL(1, rfnRequests.size());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 0);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "1 command queued for device");
        }
    }

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand(execute_time);

            const std::vector<unsigned char> exp {
                0x86,  //  command code
                0x04,  //  number of metrics
                0x01,  //  data streaming ON
                0x00, 0x05,  //  metric ID 1
                0x01,        //  metric ID 1 enable/disable
                0x07,        //  metric ID 1 interval
                0x00, 0x73,  //  metric ID 2
                0x01,        //  metric ID 2 enable/disable
                0x04,        //  metric ID 2 interval
                0x00, 0x20,  //  metric ID 3
                0x00,        //  metric ID 3 enable/disable
                0x1e,        //  metric ID 3 interval
                0x00, 0x53,  //  metric ID 4
                0x00,        //  metric ID 4 enable/disable
                0x1e         //  metric ID 4 interval
            };

            BOOST_CHECK_EQUAL(rcv, exp);
        }
    }
}


BOOST_AUTO_TEST_CASE(putconfig_behavior_rfndatastreaming_unsupported_attribute)
{
    Cti::Test::Override_BehaviorManager b;

    test_RfnMeterDevice dut;

    b.behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "1" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" }};

    b.behaviorManagerHandle->behaviorReport = std::map<std::string, std::string> {
        { "enabled", "true" },
        { "channels", "3" },
        { "channels.0.attribute", "DEMAND" },
        { "channels.0.interval", "17" },
        { "channels.0.enabled", "false" },
        { "channels.1.attribute", "KVAR" },
        { "channels.1.interval", "9" },
        { "channels.1.enabled", "true" },
        { "channels.2.attribute", "POWER_FACTOR" },
        { "channels.2.interval", "11" },
        { "channels.2.enabled", "true" }};

    {
        CtiCommandParser parse("putconfig behavior rfndatastreaming");

        BOOST_CHECK_EQUAL(ClientErrors::None, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests));
        BOOST_REQUIRE_EQUAL(1, returnMsgs.size());
        BOOST_CHECK(rfnRequests.empty());

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL(returnMsg.Status(), 284);
            BOOST_CHECK_EQUAL(returnMsg.ResultString(), "Configuration data is invalid.");
        }
    }
}


BOOST_AUTO_TEST_SUITE_END()

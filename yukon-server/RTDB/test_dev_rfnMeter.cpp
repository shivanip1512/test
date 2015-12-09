#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "dev_rfnMeter.h"
#include "ctidate.h"
#include "cmd_rfn.h"
#include "config_device.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"
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

BOOST_AUTO_TEST_CASE( test_dev_rfnMeter_putconfig_install_temperaturealarm_success_no_tlv )
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

BOOST_AUTO_TEST_CASE( test_dev_rfnMeter_putconfig_install_temperaturealarm_success_returnMismatch )
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

BOOST_AUTO_TEST_CASE( test_dev_rfnMeter_putconfig_install_temperaturealarm_failure )
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

BOOST_AUTO_TEST_CASE( test_dev_rfnMeter_putconfig_install_temperaturealarm_unsupported )
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

/**
This test does a "putconfig install channelconfig verify" and checks that it properly notices that the 
device configuration is missing several midnight and interval channel settings.
*/
BOOST_AUTO_TEST_CASE( test_dev_rfnMeter_putconfig_install_channel_verify )
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

    /* And now we run the command. */
    {
        CtiCommandParser parse( "putconfig install channelconfig verify" );

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests ) );

        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        Cti::Test::msgsEqual( returnMsgs, ClientErrors::ConfigNotCurrent, {
            "Config Midnight Channel Metrics did not match. Config: (1, 2, 3, 4, 5), Device: (1, 2, 3)",
            "The meter device is missing 2 midnight channels which suggests the meter may not be configured for them.",
            "Config Interval Channel Metrics did not match. Config: (3, 4, 5), Device: (3, 4)",
            "The meter device is missing 1 interval channels which suggests the meter may not be configured for them.",
            "Config channelconfig is NOT current."
        } );
    }

}

BOOST_AUTO_TEST_SUITE_END()

#include <boost/test/unit_test.hpp>

#include "dev_rfnMeter.h"
#include "ctidate.h"
#include "cmd_rfn.h"
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

        using DPI = CtiTableDynamicPaoInfo;

        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmIsEnabled ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmHighTempThreshold ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatInterval ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatCount ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmUnsupported ) );
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

        using DPI = CtiTableDynamicPaoInfo;

        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmIsEnabled ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmHighTempThreshold ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatInterval ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmRepeatCount ) );
        BOOST_CHECK( ! dut.hasDynamicInfo( DPI::Key_RFN_TempAlarmUnsupported ) );
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
BOOST_AUTO_TEST_SUITE_END()

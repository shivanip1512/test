#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rfn_LgyrFocus_al.h"
#include "cmd_rfn.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"

#include "boost_test_helpers.h"


struct test_state_rfn_lgyr_focus_al
{
    CtiRequestMsg request;
    Cti::Devices::RfnDevice::ReturnMsgList  returnMsgs;
    Cti::Devices::RfnDevice::RfnCommandList rfnRequests;
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfn_lgyr_focus_al() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }
};


namespace std {

    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const vector<unsigned char> &v);
}

const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );
const CtiTime decode_time ( CtiDate( 27, 8, 2013 ) , 16 );

BOOST_FIXTURE_TEST_SUITE( test_dev_rfn_lgyr_focus_al, test_state_rfn_lgyr_focus_al )

BOOST_AUTO_TEST_CASE( test_putconfig_display )
{
    Cti::Devices::RfnLgyrFocusAlDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( Cti::Config::RfnStrings::LcdCycleTime,  "8" );
    cfg.insertValue( Cti::Config::RfnStrings::DisplayDigits, "5" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem01, "DELIVERED_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem02, "REVERSE_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem03, "TOTAL_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem04, "NET_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem05, "DIAGNOSTIC_FLAGS" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem06, "ALL_SEGMENTS" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem07, "FIRMWARE_VERSION" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem08, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem09, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem10, "SLOT_DISABLED" );

    {
        CtiCommandParser parse("putconfig install display");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(&request, parse, returnMsgs, rfnRequests) );
    }

    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
    {
        Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;
        {
            Cti::Devices::Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x72)(0x00)  //  LCD configuration
                    (0x07)  //  7 metrics
                    (0x08)  //  LCD cycle time
                    (0x01)(0x30)(0x31)
                    (0x05)(0x30)(0x32)
                    (0x09)(0x30)(0x33)
                    (0x0d)(0x30)(0x34)
                    (0x10)(0x30)(0x35)
                    (0x11)(0x30)(0x36)
                    (0x12)(0x30)(0x37);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x73)
                    (0x00);

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp =
                    "Status: Success (0)"
                    "\nDisplay items sent:"
                    "\nLCD cycle time : 8 seconds"
                    "\nDisplay metric 1 : Delivered kWh (5 digit)"
                    "\nDisplay metric 2 : Reverse kWh (5 digit)"
                    "\nDisplay metric 3 : Total kWh (5 digit)"
                    "\nDisplay metric 4 : Net kWh (5 digit)"
                    "\nDisplay metric 5 : Diagnostic flags"
                    "\nDisplay metric 6 : All Segments"
                    "\nDisplay metric 7 : Firmware version";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string metric_name;
        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem01, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "DELIVERED_KWH_5X1");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem02, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "REVERSE_KWH_5X1");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem03, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "TOTAL_KWH_5X1");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem04, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "NET_KWH_5X1");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem05, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "DIAGNOSTIC_FLAGS");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem06, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "ALL_SEGMENTS");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem07, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "FIRMWARE_VERSION");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem08, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "SLOT_DISABLED");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem09, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "SLOT_DISABLED");

        dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem10, metric_name);
        BOOST_CHECK_EQUAL(metric_name, "SLOT_DISABLED");

        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime), 8);
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_display_out_of_order_slot_disabled )
{
    Cti::Devices::RfnLgyrFocusAlDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( Cti::Config::RfnStrings::LcdCycleTime,  "8" );
    cfg.insertValue( Cti::Config::RfnStrings::DisplayDigits, "5" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem01, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem02, "DELIVERED_KWH" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem03, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem04, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem05, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem06, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem07, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem08, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem09, "SLOT_DISABLED" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem10, "SLOT_DISABLED" );

    {
        CtiCommandParser parse("putconfig install display");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(&request, parse, returnMsgs, rfnRequests) );
    }

    BOOST_REQUIRE_EQUAL( 2, returnMsgs.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       284 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Device \"\" - Invalid value (DELIVERED_KWH) seen after SLOT_DISABLED for config key \"displayItem2\"" );
    }

    BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
}

BOOST_AUTO_TEST_SUITE_END()


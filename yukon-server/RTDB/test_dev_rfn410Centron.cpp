#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rfn410centron.h"
#include "cmd_rfn.h"
#include "config_data_rfn.h"
#include "rtdb_test_helpers.h"

#include "boost_test_helpers.h"


struct test_Rfn410CentronDevice : Cti::Devices::Rfn410CentronDevice
{

};

struct test_state_rfn410centron
{
    CtiRequestMsg request;
    Cti::Devices::RfnDevice::ReturnMsgList  returnMsgs;
    Cti::Devices::RfnDevice::RfnCommandList rfnRequests;
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    test_state_rfn410centron() :
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

BOOST_FIXTURE_TEST_SUITE( test_dev_rfn410centron, test_state_rfn410centron )

BOOST_AUTO_TEST_CASE( test_dev_rfn410Centron_putconfig_display )
{
    test_Rfn410CentronDevice dut;

    Cti::Test::test_DeviceConfig &cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    cfg.insertValue( Cti::Config::RfnStrings::LcdCycleTime,  "0" );
    cfg.insertValue( Cti::Config::RfnStrings::DisconnectDisplayDisabled, "true" );  //  make sure it's ignored, even if available
    cfg.insertValue( Cti::Config::RfnStrings::DisplayDigits, "5" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem01, "16" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem02, "17" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem03, "18" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem04, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem05, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem06, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem07, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem08, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem09, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem10, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem11, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem12, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem13, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem14, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem15, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem16, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem17, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem18, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem19, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem20, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem21, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem22, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem23, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem24, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem25, "0" );
    cfg.insertValue( Cti::Config::RfnStrings::displayItem26, "0" );

    {
        CtiCommandParser parse("putconfig install display");

        BOOST_CHECK_EQUAL( ClientErrors::None, dut.ExecuteRequest(&request, parse, returnMsgs, rfnRequests) );
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

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x70)  //  LCD configuration
                    (0x00)  //  write
                    (0x1d)  //  29 metrics (26 + 3 for display digits, cycle delay, and disconnect)
                    (0x00)(0x10)(0x01)(0x11)(0x02)(0x12)(0x03)(0x00)
                    (0x04)(0x00)(0x05)(0x00)(0x06)(0x00)(0x07)(0x00)
                    (0x08)(0x00)(0x09)(0x00)(0x0a)(0x00)(0x0b)(0x00)
                    (0x0c)(0x00)(0x0d)(0x00)(0x0e)(0x00)(0x0f)(0x00)
                    (0x10)(0x00)(0x11)(0x00)(0x12)(0x00)(0x13)(0x00)
                    (0x14)(0x00)(0x15)(0x00)(0x16)(0x00)(0x17)(0x00)
                    (0x18)(0x00)(0x19)(0x00)
                    (0xfd)(0x05)(0xfe)(0x00);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x71)(0x00)(0x00);

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp =
                    "Display metrics successfully set"
                    "\nDisplay metric 1: Date of Peak Voltage"
                    "\nDisplay metric 2: Time of Peak Voltage"
                    "\nDisplay metric 3: Min Voltage (V)"
                    "\nDisplay metric 4: Metric Slot Disabled"
                    "\nDisplay metric 5: Metric Slot Disabled"
                    "\nDisplay metric 6: Metric Slot Disabled"
                    "\nDisplay metric 7: Metric Slot Disabled"
                    "\nDisplay metric 8: Metric Slot Disabled"
                    "\nDisplay metric 9: Metric Slot Disabled"
                    "\nDisplay metric 10: Metric Slot Disabled"
                    "\nDisplay metric 11: Metric Slot Disabled"
                    "\nDisplay metric 12: Metric Slot Disabled"
                    "\nDisplay metric 13: Metric Slot Disabled"
                    "\nDisplay metric 14: Metric Slot Disabled"
                    "\nDisplay metric 15: Metric Slot Disabled"
                    "\nDisplay metric 16: Metric Slot Disabled"
                    "\nDisplay metric 17: Metric Slot Disabled"
                    "\nDisplay metric 18: Metric Slot Disabled"
                    "\nDisplay metric 19: Metric Slot Disabled"
                    "\nDisplay metric 20: Metric Slot Disabled"
                    "\nDisplay metric 21: Metric Slot Disabled"
                    "\nDisplay metric 22: Metric Slot Disabled"
                    "\nDisplay metric 23: Metric Slot Disabled"
                    "\nDisplay metric 24: Metric Slot Disabled"
                    "\nDisplay metric 25: Metric Slot Disabled"
                    "\nDisplay metric 26: Metric Slot Disabled"
                    "\nLCD cycle time: (default)"
                    "\nDisplay digits: 5x1";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem01), 0x10);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem02), 0x11);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem03), 0x12);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem04), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem05), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem06), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem07), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem08), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem09), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem10), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem11), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem12), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem13), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem14), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem15), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem16), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem17), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem18), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem19), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem20), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem21), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem22), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem23), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem24), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem25), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_DisplayItem26), 0x00);

        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime), 0x00);
        BOOST_CHECK_EQUAL(dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisplayDigits), 0x05);
        BOOST_CHECK( ! dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled));
    }
}

BOOST_AUTO_TEST_SUITE_END()


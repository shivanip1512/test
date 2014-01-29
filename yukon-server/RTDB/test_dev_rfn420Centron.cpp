#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rfn420centron.h"
#include "cmd_rfn.h"
#include "config_data_rfn.h"
#include "mgr_config.h"

#include "boost_test_helpers.h"


struct test_Rfn420CentronDevice : Cti::Devices::Rfn420CentronDevice
{

};

struct test_state_rfn420centron
{
    CtiRequestMsg request;
    Cti::Devices::RfnDevice::ReturnMsgList  returnMsgs;
    Cti::Devices::RfnDevice::RfnCommandList rfnRequests;
};

struct test_DeviceConfig : public Cti::Config::DeviceConfig
{
    test_DeviceConfig() : DeviceConfig(-1, string()) {}

    using DeviceConfig::insertValue;
};

class test_ConfigManager : public CtiConfigManager
{
    Cti::Config::DeviceConfigSPtr   _config;

public:

    test_ConfigManager( Cti::Config::DeviceConfigSPtr config )
        :   _config( config )
    {
        // empty
    }

    virtual Cti::Config::DeviceConfigSPtr fetchConfig( const long deviceID, const DeviceTypes deviceType )
    {
        return _config;
    }
};


namespace std {

    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const vector<unsigned char> &v);
}

const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );

BOOST_FIXTURE_TEST_SUITE( test_dev_rfn420centron, test_state_rfn420centron )

BOOST_AUTO_TEST_CASE( test_dev_rfn420Centron_putconfig_display )
{
    test_Rfn420CentronDevice dut;

    test_DeviceConfig cfg;

    cfg.insertValue( Cti::Config::RfnStrings::LcdCycleTime,  "0" );
    cfg.insertValue( Cti::Config::RfnStrings::DisconnectDisplayDisabled, "true" );
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

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

    {
        CtiCommandParser parse("putconfig install display");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(&request, parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }

        Cti::Devices::RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            Cti::Devices::Commands::RfnCommandSPtr command = *rfnRequest_itr++;

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
                    (0xfd)(0x00)(0xfe)(0x00)(0xff)(0x00);

            BOOST_CHECK_EQUAL( rcv, exp );
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()


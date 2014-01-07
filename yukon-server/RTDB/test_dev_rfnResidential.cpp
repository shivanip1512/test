#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "dev_rfnResidential.h"
#include "cmd_rfn.h"
#include "config_data_rfn.h"
#include "mgr_config.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices;
using namespace Cti::Config;

struct test_RfnResidentialDevice : RfnResidentialDevice
{
    using RfnResidentialDevice::handleCommandResult;
    using CtiTblPAOLite::_type;
};

struct test_state_rfnResidential
{
    std::auto_ptr<CtiRequestMsg> request;
    RfnDevice::ReturnMsgList     returnMsgs;
    RfnDevice::RfnCommandList    rfnRequests;

    test_state_rfnResidential() : request( new CtiRequestMsg )
    {
    }

    void resetTestState()
    {
        request.reset( new CtiRequestMsg );
        returnMsgs.clear();
        rfnRequests.clear();
    }
};


struct test_DeviceConfig : public DeviceConfig
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

    virtual Cti::Config::DeviceConfigSPtr   fetchConfig( const long deviceID, const DeviceTypes deviceType )
    {
        return _config;
    }
};

namespace std {

    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const vector<unsigned char> &v);
}


const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );
const CtiTime decode_time ( CtiDate( 27, 8, 2013 ) , 16 );


BOOST_FIXTURE_TEST_SUITE( test_dev_rfnResidential, test_state_rfnResidential )

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_tou_schedule )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig tou 13242313 "
                           "schedule 1 a/00:00 b/00:01 c/10:06 d/12:22 a/23:33 b/23:44 "
                           "schedule 2 d/00:00 a/01:23 b/03:12 c/04:01 d/05:23 a/16:28 "
                           "schedule 3 c/00:00 d/01:02 a/02:03 b/04:05 c/05:06 d/06:07 "
                           "schedule 4 b/00:00 c/00:01 d/08:59 a/12:12 b/23:01 c/23:55 "
                           "default b");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x04)
                (0x0A)
                (0x01)(0x03) // day table
                (0x50)(0x16)(0x41)
                (0x02)(0x0A) // schedule 1 times
                (0x00)(0x01)(0x02)(0x5d)(0x00)(0x88)(0x02)(0x9f)(0x00)(0x0b)
                (0x03)(0x0A) // schedule 2 times
                (0x00)(0x53)(0x00)(0x6d)(0x00)(0x31)(0x00)(0x52)(0x02)(0x99)
                (0x04)(0x0A) // schedule 3 times
                (0x00)(0x3e)(0x00)(0x3d)(0x00)(0x7a)(0x00)(0x3d)(0x00)(0x3d)
                (0x05)(0x0A) // schedule 4 times
                (0x00)(0x01)(0x02)(0x1a)(0x00)(0xc1)(0x02)(0x89)(0x00)(0x36)
                (0x06)(0x03) // schedule 1 rates
                (0x88)(0x86)(0x00)
                (0x07)(0x03) // schedule 2 rates
                (0x43)(0x34)(0x00)
                (0x08)(0x03) // schedule 3 rates
                (0x1A)(0xA2)(0x01)
                (0x09)(0x03) // schedule 4 rates
                (0xD1)(0x10)(0x01)
                (0x0A)(0x01) // default TOU rate
                (0x01);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_tou_schedule_badparam )
{
    test_RfnResidentialDevice dut;

    // test wrong switch time order
    {
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:03 c/01:02 d/01:04 a/01:05 " // schedule 2 switch time 2 > switch time 3
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid switch time for Schedule 2 - (01:02, expected > 01:03)";

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test first time is not midnight
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:01 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 " // schedule 3 midnight time 00:01
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid midnight time for Schedule 3 - (00:01, expected 00:00)";

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test switch time out of range
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:60 a/04:03 b/04:04 c/04:05 " // schedule 4 switch time 2 set to 03:60
                               "default b");

        std::string exp = "Invalid switch time for Schedule 4 - (03:60)";

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test invalid rate
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 e/00:04 b/00:05 " // schedule 1 rate 4 set to 'e'
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid rate for Schedule 1 - (e)";

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test invalid change number
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 b/01:06 " // schedule 2 changes == 7
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid number of switch time for Schedule 2 - (7, expected 6)";

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }

    // test unexpected schedule
    {
        resetTestState();
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "schedule 5 a/00:00 b/04:01 c/04:02 d/04:03 a/04:04 b/04:05 " // unexpected schedule 5
                               "default b");

        std::string exp = "Invalid schedule - (Schedule 5)";

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), exp );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_tou_enable )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig tou enable");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_CHECK_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x01)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_tou_disable )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig tou disable");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x02)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_getconfig_tou_schedule )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("getconfig tou");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x05)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_tou_install )
{
    test_RfnResidentialDevice dut;

    test_DeviceConfig cfg;

    // Schedule 1
    cfg.insertValue( RfnStrings::Schedule1Time1, "00:01" );
    cfg.insertValue( RfnStrings::Schedule1Time2, "10:06" );
    cfg.insertValue( RfnStrings::Schedule1Time3, "12:22" );
    cfg.insertValue( RfnStrings::Schedule1Time4, "23:33" );
    cfg.insertValue( RfnStrings::Schedule1Time5, "23:44" );

    cfg.insertValue( RfnStrings::Schedule1Rate0, "A" );
    cfg.insertValue( RfnStrings::Schedule1Rate1, "B" );
    cfg.insertValue( RfnStrings::Schedule1Rate2, "C" );
    cfg.insertValue( RfnStrings::Schedule1Rate3, "D" );
    cfg.insertValue( RfnStrings::Schedule1Rate4, "A" );
    cfg.insertValue( RfnStrings::Schedule1Rate5, "B" );

    // Schedule 2
    cfg.insertValue( RfnStrings::Schedule2Time1, "01:23" );
    cfg.insertValue( RfnStrings::Schedule2Time2, "03:12" );
    cfg.insertValue( RfnStrings::Schedule2Time3, "04:01" );
    cfg.insertValue( RfnStrings::Schedule2Time4, "05:23" );
    cfg.insertValue( RfnStrings::Schedule2Time5, "16:28" );

    cfg.insertValue( RfnStrings::Schedule2Rate0, "D" );
    cfg.insertValue( RfnStrings::Schedule2Rate1, "A" );
    cfg.insertValue( RfnStrings::Schedule2Rate2, "B" );
    cfg.insertValue( RfnStrings::Schedule2Rate3, "C" );
    cfg.insertValue( RfnStrings::Schedule2Rate4, "D" );
    cfg.insertValue( RfnStrings::Schedule2Rate5, "A" );

    // Schedule 3
    cfg.insertValue( RfnStrings::Schedule3Time1, "01:02" );
    cfg.insertValue( RfnStrings::Schedule3Time2, "02:03" );
    cfg.insertValue( RfnStrings::Schedule3Time3, "04:05" );
    cfg.insertValue( RfnStrings::Schedule3Time4, "05:06" );
    cfg.insertValue( RfnStrings::Schedule3Time5, "06:07" );

    cfg.insertValue( RfnStrings::Schedule3Rate0, "C" );
    cfg.insertValue( RfnStrings::Schedule3Rate1, "D" );
    cfg.insertValue( RfnStrings::Schedule3Rate2, "A" );
    cfg.insertValue( RfnStrings::Schedule3Rate3, "B" );
    cfg.insertValue( RfnStrings::Schedule3Rate4, "C" );
    cfg.insertValue( RfnStrings::Schedule3Rate5, "D" );

    // Schedule 4
    cfg.insertValue( RfnStrings::Schedule4Time1, "00:01" );
    cfg.insertValue( RfnStrings::Schedule4Time2, "08:59" );
    cfg.insertValue( RfnStrings::Schedule4Time3, "12:12" );
    cfg.insertValue( RfnStrings::Schedule4Time4, "23:01" );
    cfg.insertValue( RfnStrings::Schedule4Time5, "23:55" );

    cfg.insertValue( RfnStrings::Schedule4Rate0, "B" );
    cfg.insertValue( RfnStrings::Schedule4Rate1, "C" );
    cfg.insertValue( RfnStrings::Schedule4Rate2, "D" );
    cfg.insertValue( RfnStrings::Schedule4Rate3, "A" );
    cfg.insertValue( RfnStrings::Schedule4Rate4, "B" );
    cfg.insertValue( RfnStrings::Schedule4Rate5, "C" );

    // day table
    cfg.insertValue( RfnStrings::SundaySchedule,    "Schedule 1" );
    cfg.insertValue( RfnStrings::MondaySchedule,    "Schedule 1" );
    cfg.insertValue( RfnStrings::TuesdaySchedule,   "Schedule 3" );
    cfg.insertValue( RfnStrings::WednesdaySchedule, "Schedule 2" );
    cfg.insertValue( RfnStrings::ThursdaySchedule,  "Schedule 4" );
    cfg.insertValue( RfnStrings::FridaySchedule,    "Schedule 2" );
    cfg.insertValue( RfnStrings::SaturdaySchedule,  "Schedule 3" );
    cfg.insertValue( RfnStrings::HolidaySchedule,   "Schedule 3" );

    // default rate
    cfg.insertValue( RfnStrings::DefaultTouRate, "B" );

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    dut.setConfigManager(&cfgMgr);  // attach config manager to the deice so it can find the config

    {
        CtiCommandParser parse("putconfig install tou");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }

        {
            Commands::RfnCommandSPtr command = rfnRequests.front();

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x60)(0x04)
                    (0x0A)
                    (0x01)(0x03) // day table
                    (0x80)(0xB2)(0x48)
                    (0x02)(0x0A) // schedule 1 times
                    (0x00)(0x01)(0x02)(0x5d)(0x00)(0x88)(0x02)(0x9f)(0x00)(0x0b)
                    (0x03)(0x0A) // schedule 2 times
                    (0x00)(0x53)(0x00)(0x6d)(0x00)(0x31)(0x00)(0x52)(0x02)(0x99)
                    (0x04)(0x0A) // schedule 3 times
                    (0x00)(0x3e)(0x00)(0x3d)(0x00)(0x7a)(0x00)(0x3d)(0x00)(0x3d)
                    (0x05)(0x0A) // schedule 4 times
                    (0x00)(0x01)(0x02)(0x1a)(0x00)(0xc1)(0x02)(0x89)(0x00)(0x36)
                    (0x06)(0x03) // schedule 1 rates
                    (0x88)(0x86)(0x00)
                    (0x07)(0x03) // schedule 2 rates
                    (0x43)(0x34)(0x00)
                    (0x08)(0x03) // schedule 3 rates
                    (0x1A)(0xA2)(0x01)
                    (0x09)(0x03) // schedule 4 rates
                    (0xD1)(0x10)(0x01)
                    (0x0A)(0x01) // default TOU rate
                    (0x01);

            BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                           exp.begin() , exp.end() );
        }
    }

    {
        resetTestState();
        CtiCommandParser parse("putconfig install tou verify");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size()  );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       ConfigNotCurrent );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config tou is NOT current." );
    }

    {
        resetTestState();
        CtiCommandParser parse("getconfig install tou");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }

        {
            Commands::RfnCommandSPtr command = rfnRequests.front();

            Commands::RfnCommand::RfnRequestPayload request_rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> request_exp = boost::assign::list_of
                    (0x60)(0x05)(0x00);

            BOOST_CHECK_EQUAL_COLLECTIONS( request_rcv.begin() , request_rcv.end() ,
                                           request_exp.begin() , request_exp.end() );

            std::vector<unsigned char> response = boost::assign::list_of
                    (0x61)(0x00)(0x00)(0x00)(0x00)
                    (0x0A)
                    (0x01)(0x03) // day table
                    (0x80)(0xB2)(0x48)
                    (0x02)(0x0A) // schedule 1 switch times
                    (0x00)(0x01)(0x02)(0x5d)(0x00)(0x88)(0x02)(0x9f)(0x00)(0x0b)
                    (0x03)(0x0A) // schedule 2 switch times
                    (0x00)(0x53)(0x00)(0x6d)(0x00)(0x31)(0x00)(0x52)(0x02)(0x99)
                    (0x04)(0x0A) // schedule 3 switch times
                    (0x00)(0x3e)(0x00)(0x3d)(0x00)(0x7a)(0x00)(0x3d)(0x00)(0x3d)
                    (0x05)(0x0A) // schedule 4 switch time
                    (0x00)(0x01)(0x02)(0x1a)(0x00)(0xc1)(0x02)(0x89)(0x00)(0x36)
                    (0x06)(0x03) // schedule 1 rates
                    (0x88)(0x86)(0x00)
                    (0x07)(0x03) // schedule 2 rates
                    (0x43)(0x34)(0x00)
                    (0x08)(0x03) // schedule 3 rates
                    (0x1A)(0xA2)(0x01)
                    (0x09)(0x03) // schedule 4 rates
                    (0xD1)(0x10)(0x01)
                    (0x0A)(0x01) // default TOU rate
                    (0x01);

            command->decodeCommand( decode_time, response );

            dut.extractCommandResult( *command );
        }
    }

    //
    // NOTE: At this point the configuration is expected to be valid
    //

    {
        resetTestState();
        CtiCommandParser parse("putconfig install tou");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size()  );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       NoError );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config tou is current." );
    }

    {
        resetTestState();
        CtiCommandParser parse("putconfig install tou verify");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size()  );

        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       NoError );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "Config tou is current." );
    }

    {
        resetTestState();
        CtiCommandParser parse("putconfig install tou force");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }

        {
            Commands::RfnCommandSPtr command = rfnRequests.front();

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x60)(0x04)
                    (0x0A)
                    (0x01)(0x03) // day table
                    (0x80)(0xB2)(0x48)
                    (0x02)(0x0A) // schedule 1 times
                    (0x00)(0x01)(0x02)(0x5d)(0x00)(0x88)(0x02)(0x9f)(0x00)(0x0b)
                    (0x03)(0x0A) // schedule 2 times
                    (0x00)(0x53)(0x00)(0x6d)(0x00)(0x31)(0x00)(0x52)(0x02)(0x99)
                    (0x04)(0x0A) // schedule 3 times
                    (0x00)(0x3e)(0x00)(0x3d)(0x00)(0x7a)(0x00)(0x3d)(0x00)(0x3d)
                    (0x05)(0x0A) // schedule 4 times
                    (0x00)(0x01)(0x02)(0x1a)(0x00)(0xc1)(0x02)(0x89)(0x00)(0x36)
                    (0x06)(0x03) // schedule 1 rates
                    (0x88)(0x86)(0x00)
                    (0x07)(0x03) // schedule 2 rates
                    (0x43)(0x34)(0x00)
                    (0x08)(0x03) // schedule 3 rates
                    (0x1A)(0xA2)(0x01)
                    (0x09)(0x03) // schedule 4 rates
                    (0xD1)(0x10)(0x01)
                    (0x0A)(0x01) // default TOU rate
                    (0x01);

            BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                           exp.begin() , exp.end() );
        }
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_tou_holiday )
{
    Cti::Test::set_to_central_timezone();

    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon holiday 02/01/2025 06/14/2036 12/30/2050");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x06)
                (0x01)
                (0x0C)(0x0C)
                (0x67)(0x9d)(0xb8)(0x60)
                (0x7c)(0xfe)(0x2c)(0xd0)
                (0x98)(0x59)(0x5a)(0xe0);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_tou_holiday_active )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon holiday active");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        const std::vector< unsigned char > exp = boost::assign::list_of
                (0x60)(0x0C)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_tou_holiday_cancel )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon holiday cancel");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        const std::vector< unsigned char > exp = boost::assign::list_of
                (0x60)(0x0D)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_getconfig_tou_holiday )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("getconfig emetcon holiday");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x07)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_voltage_profile )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon voltage profile demandinterval 17 lpinterval 34");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                ( 0x68 )( 0x00 )( 0x01 )( 0x01 )( 0x00 )( 0x02 )( 0x44 )( 0x22 );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_voltage_profile_enable )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon voltage profile enable");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                ( 0x68 )( 0x03 )( 0x00 );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_voltage_profile_disable )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("putconfig emetcon voltage profile disable");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                ( 0x68 )( 0x02 )( 0x00 );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_getconfig_voltage_profile )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("getconfig voltage profile");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                ( 0x68 )( 0x01 )( 0x00 );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_getvalue_voltage_profile_state )
{
    test_RfnResidentialDevice dut;

    CtiCommandParser parse("getconfig voltage profile state");

    BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr command = rfnRequests.front();

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        std::vector<unsigned char> exp = boost::assign::list_of
                ( 0x68 )( 0x04 )( 0x00 );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_immediate_demand_freeze )
{
    test_RfnResidentialDevice    dev;

    CtiCommandParser    parse("putstatus freeze");

    BOOST_CHECK_EQUAL( NoError, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr    command = rfnRequests.front();

        // execute message and check request bytes

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x01 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_tou_critical_peak_cancel )
{
    test_RfnResidentialDevice    dev;

    CtiCommandParser    parse("putstatus tou critical peak cancel");

    BOOST_CHECK_EQUAL( NoError, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr    command = rfnRequests.front();

        // execute message and check request bytes

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x60 )( 0x09 )( 0x00 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_tou_critical_peak_today )
{
    test_RfnResidentialDevice    dev;

    CtiCommandParser    parse("putstatus tou critical peak rate b until 23:00");

    BOOST_CHECK_EQUAL( NoError, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr    command = rfnRequests.front();

        // execute message and check request bytes

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x60 )( 0x08 )( 0x01 )( 0x0b )( 0x05 )( 0x01 )( 0x52 )( 0x1d )( 0x75 )( 0xc0 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}


BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_tou_critical_peak_tomorrow )
{
    test_RfnResidentialDevice    dev;

    CtiCommandParser    parse("putstatus tou critical peak rate b until 8:00");

    BOOST_CHECK_EQUAL( NoError, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
    BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    {
        const CtiReturnMsg &returnMsg = returnMsgs.front();

        BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
        BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
    }

    {
        Commands::RfnCommandSPtr    command = rfnRequests.front();

        // execute message and check request bytes

        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x60 )( 0x08 )( 0x01 )( 0x0b )( 0x05 )( 0x01 )( 0x52 )( 0x1d )( 0xf4 )( 0x50 );

        Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_getconfig_install_ovuv_meterids )
{
    test_RfnResidentialDevice    dev;

    const std::vector<DeviceTypes> rfnTypes = boost::assign::list_of
        //(TYPE_RFN410FL)  not an rfnResidential device
        (TYPE_RFN410FX)
        (TYPE_RFN410FD)
        (TYPE_RFN420FL)
        (TYPE_RFN420FX)
        (TYPE_RFN420FD)
        (TYPE_RFN420FRX)
        (TYPE_RFN420FRD)
        (TYPE_RFN410CL)
        (TYPE_RFN420CL)
        (TYPE_RFN420CD);

    CtiCommandParser parse("getconfig install ovuv");

    const std::vector<std::vector<unsigned char>> expected = boost::assign::list_of
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x02)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x02)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x03)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x06)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x06)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x04)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x04)(0x07)(0xe7))
        (boost::assign::list_of(0x34)(0x04)(0x07)(0xe6))
        (boost::assign::list_of(0x34)(0x04)(0x07)(0xe7));

    std::vector<std::vector<unsigned char>> results;

    for each( DeviceTypes type in rfnTypes )
    {
        dev._type = type;

        BOOST_CHECK_EQUAL( NoError, dev.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );

        for each( Cti::Devices::Commands::RfnCommandSPtr cmd in rfnRequests )
        {
            results.push_back(cmd->executeCommand(execute_time));
        }

        rfnRequests.clear();
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
            expected.begin(), expected.end(),
            results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_install_ovuv )
{
    test_RfnResidentialDevice dut;

    test_DeviceConfig cfg;

    cfg.insertValue( RfnStrings::OvUvEnabled,                "true" );
    cfg.insertValue( RfnStrings::OvUvAlarmReportingInterval, "5" );
    cfg.insertValue( RfnStrings::OvUvAlarmRepeatInterval,    "60" );
    cfg.insertValue( RfnStrings::OvUvRepeatCount,            "2" );
    cfg.insertValue( RfnStrings::OvThreshold,                "123.456" );
    cfg.insertValue( RfnStrings::UvThreshold,                 "78.901" );

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

    dut._type = TYPE_RFN410FX;

    {
        CtiCommandParser parse("putconfig install ovuv");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 6, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "6 commands queued for device" );
        }

        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x24)(0x01);

            BOOST_CHECK_EQUAL( rcv, exp );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x26)(0x05);

            BOOST_CHECK_EQUAL( rcv, exp );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x27)(0x3c);

            BOOST_CHECK_EQUAL( rcv, exp );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x28)(0x02);

            BOOST_CHECK_EQUAL( rcv, exp );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x25)(0x03)(0x07)(0xe6)(0x00)(0x01)(0xe2)(0x40)(0x10)(0x80)(0x00)(0x01)(0xc0);

            BOOST_CHECK_EQUAL( rcv, exp );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x25)(0x03)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0);

            BOOST_CHECK_EQUAL( rcv, exp );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_install_ovuv_meter_ids )
{
    test_RfnResidentialDevice dut;

    test_DeviceConfig cfg;

    cfg.insertValue( RfnStrings::OvUvEnabled,                "true" );
    cfg.insertValue( RfnStrings::OvUvAlarmReportingInterval, "5" );
    cfg.insertValue( RfnStrings::OvUvAlarmRepeatInterval,    "60" );
    cfg.insertValue( RfnStrings::OvUvRepeatCount,            "2" );
    cfg.insertValue( RfnStrings::OvThreshold,                "123.456" );
    cfg.insertValue( RfnStrings::UvThreshold,                 "78.901" );

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

    //  set the dynamic pao info to match
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled,                 1);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval,  5);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval,    60);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount,             2);
    dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_OvThreshold,           123.456);
    //dut.setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_UvThreshold,          78.901);  //  leave this out so it's the only one sent

    {
        const std::vector<DeviceTypes> rfnTypes = boost::assign::list_of
            //(TYPE_RFN410FL)  not an rfnResidential device
            (TYPE_RFN410FX)
            (TYPE_RFN410FD)
            (TYPE_RFN420FL)
            (TYPE_RFN420FX)
            (TYPE_RFN420FD)
            (TYPE_RFN420FRX)
            (TYPE_RFN420FRD)
            (TYPE_RFN410CL)
            (TYPE_RFN420CL)
            (TYPE_RFN420CD);

        const std::vector<std::vector<unsigned char>> expected = boost::assign::list_of
            (boost::assign::list_of(0x25)(0x03)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x03)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x02)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x03)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x03)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x03)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x03)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x06)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x04)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0))
            (boost::assign::list_of(0x25)(0x04)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0));

        CtiCommandParser parse("putconfig install ovuv");

        std::vector<std::vector<unsigned char>> results;

        for each( DeviceTypes type in rfnTypes )
        {
            dut._type = type;

            BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
            BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

            {
                const CtiReturnMsg &returnMsg = returnMsgs.front();

                BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
            }

            results.push_back(rfnRequests.front()->executeCommand( execute_time ));

            returnMsgs.clear();
            rfnRequests.clear();
        }

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results.begin(),  results.end());
    }
}


BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_install_freezeday )
{
    test_RfnResidentialDevice dut;

    test_DeviceConfig cfg;

    cfg.insertValue( RfnStrings::demandFreezeDay, "7" );

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

    dut._type = TYPE_RFN410FX;

    BOOST_CHECK( ! dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay) );

    {
        CtiCommandParser parse("putconfig install freezeday");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "1 command queued for device" );
        }

        RfnDevice::RfnCommandList::iterator rfnRequest_itr = rfnRequests.begin();
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x55)(0x02)(0x07);

            BOOST_CHECK_EQUAL( rcv, exp );

            std::vector<unsigned char> response = boost::assign::list_of
                        (0x56)(0x00)(0x00)(0x00)(0x00);

            command->decodeCommand( CtiTime::now(), response );

            dut.extractCommandResult( *command );

            BOOST_CHECK( dut.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay) );

            BOOST_CHECK_EQUAL( 7, dut.getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay) );
        }
    }
}


BOOST_AUTO_TEST_SUITE_END()


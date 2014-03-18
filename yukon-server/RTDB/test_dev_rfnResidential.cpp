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
    using DeviceConfig::findValue;
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
    typedef map<CtiDeviceBase::PaoInfoKeys, std::string> TouScheduleCompareKeysMap;

    const TouScheduleCompareKeysMap touScheduleCompareKeys = boost::assign::map_list_of
    // day table
    ( CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,    RfnStrings::MondaySchedule    )
    ( CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,   RfnStrings::TuesdaySchedule   )
    ( CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule, RfnStrings::WednesdaySchedule )
    ( CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,  RfnStrings::ThursdaySchedule  )
    ( CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,    RfnStrings::FridaySchedule    )
    ( CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,  RfnStrings::SaturdaySchedule  )
    ( CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,    RfnStrings::SundaySchedule    )
    ( CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,   RfnStrings::HolidaySchedule   )
    // default rate
    ( CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate,    RfnStrings::DefaultTouRate    )
    // schedule 1
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate0,    RfnStrings::Schedule1Rate0    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1,    RfnStrings::Schedule1Time1    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1,    RfnStrings::Schedule1Rate1    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2,    RfnStrings::Schedule1Time2    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2,    RfnStrings::Schedule1Rate2    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3,    RfnStrings::Schedule1Time3    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3,    RfnStrings::Schedule1Rate3    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4,    RfnStrings::Schedule1Time4    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4,    RfnStrings::Schedule1Rate4    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5,    RfnStrings::Schedule1Time5    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5,    RfnStrings::Schedule1Rate5    )
    // schedule 2
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate0,    RfnStrings::Schedule2Rate0    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1,    RfnStrings::Schedule2Time1    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1,    RfnStrings::Schedule2Rate1    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2,    RfnStrings::Schedule2Time2    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2,    RfnStrings::Schedule2Rate2    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3,    RfnStrings::Schedule2Time3    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3,    RfnStrings::Schedule2Rate3    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4,    RfnStrings::Schedule2Time4    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4,    RfnStrings::Schedule2Rate4    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5,    RfnStrings::Schedule2Time5    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5,    RfnStrings::Schedule2Rate5    )
    // schedule 3
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate0,    RfnStrings::Schedule3Rate0    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1,    RfnStrings::Schedule3Time1    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1,    RfnStrings::Schedule3Rate1    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2,    RfnStrings::Schedule3Time2    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2,    RfnStrings::Schedule3Rate2    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3,    RfnStrings::Schedule3Time3    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3,    RfnStrings::Schedule3Rate3    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4,    RfnStrings::Schedule3Time4    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4,    RfnStrings::Schedule3Rate4    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5,    RfnStrings::Schedule3Time5    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5,    RfnStrings::Schedule3Rate5    )
    // schedule 4
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate0,    RfnStrings::Schedule4Rate0    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1,    RfnStrings::Schedule4Time1    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1,    RfnStrings::Schedule4Rate1    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2,    RfnStrings::Schedule4Time2    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2,    RfnStrings::Schedule4Rate2    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3,    RfnStrings::Schedule4Time3    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3,    RfnStrings::Schedule4Rate3    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4,    RfnStrings::Schedule4Time4    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4,    RfnStrings::Schedule4Rate4    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5,    RfnStrings::Schedule4Time5    )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5,    RfnStrings::Schedule4Rate5    );

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

    // set TOU enabled
    cfg.insertValue( RfnStrings::touEnabled, "true" );

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    {
        //
        // Test Put Config Install
        //

        test_RfnResidentialDevice dut;

        dut.setConfigManager(&cfgMgr);  // attach config manager to the deice so it can find the config

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
            CtiCommandParser parse("putconfig install tou");

            BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
            BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
            BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );

            {
                const CtiReturnMsg &returnMsg = returnMsgs.front();

                BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "2 commands queued for device" );
            }

            {
                {
                    Commands::RfnCommandSPtr command = rfnRequests[0];

                    Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

                    std::vector<unsigned char> exp = boost::assign::list_of
                            (0x60)(0x01)
                            (0x00);

                    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                                   exp.begin() , exp.end() );

                    dut.extractCommandResult( *command );

                    boost::optional<bool>   dynamicInfo,
                                            configValue;

                    BOOST_CHECK( (configValue = cfg.findValue<bool>( RfnStrings::touEnabled ))
                                 && (dynamicInfo = dut.findDynamicInfo<bool>( CtiTableDynamicPaoInfo::Key_RFN_TouEnabled ))
                                 && configValue == dynamicInfo );
                }
                {
                    Commands::RfnCommandSPtr command = rfnRequests[1];

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

                    dut.extractCommandResult( *command );

                    const std::vector<bool> dynamicInfo_exp( touScheduleCompareKeys.size(), true );
                          std::vector<bool> dynamicInfo_rcv;

                    for each( const TouScheduleCompareKeysMap::value_type & p in touScheduleCompareKeys )
                    {
                        std::string dynamicInfo;
                        dynamicInfo_rcv.push_back( dut.getDynamicInfo( p.first, dynamicInfo ) &&
                                dynamicInfo == cfg.findValue<std::string>( p.second ) );
                    }

                    BOOST_CHECK_EQUAL_COLLECTIONS( dynamicInfo_rcv.begin(), dynamicInfo_rcv.end(),
                                                   dynamicInfo_exp.begin(), dynamicInfo_exp.end() );
                }
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
            BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );

            {
                const CtiReturnMsg &returnMsg = returnMsgs.front();

                BOOST_CHECK_EQUAL( returnMsg.Status(),       0 );
                BOOST_CHECK_EQUAL( returnMsg.ResultString(), "2 commands queued for device" );
            }

            {
                {
                    Commands::RfnCommandSPtr command = rfnRequests[0];

                    Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

                    std::vector<unsigned char> exp = boost::assign::list_of
                            (0x60)(0x01)
                            (0x00);

                    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                                   exp.begin() , exp.end() );
                }
                {
                    Commands::RfnCommandSPtr command = rfnRequests[1];

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
    }

    {
        //
        // Test Get Config Install
        //

        test_RfnResidentialDevice dut;

        dut.setConfigManager(&cfgMgr);  // attach config manager to the deice so it can find the config

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
                        (0x61)(0x00)(0x00)(0x00)
                        (0x01)       // TOU enabled
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

                std::string dynamicInfo;
                boost::optional<std::string> configValue;

                for each( const TouScheduleCompareKeysMap::value_type & p in touScheduleCompareKeys )
                {
                    BOOST_CHECK( (configValue = cfg.findValue<std::string>( p.second ))
                            && dut.getDynamicInfo( p.first, dynamicInfo )
                            && *configValue == dynamicInfo );
                }
            }
        }

        //
        // NOTE: At this point the configuration is expected to be valid
        //

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

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_disconnect_on_demand )
{
    test_RfnResidentialDevice dut;

    dut.setType(TYPE_RFN420CD); // Make it a disconnect type.

    test_DeviceConfig cfg;

    cfg.insertValue( RfnStrings::DisconnectMode, "ON_DEMAND" );
    cfg.insertValue( RfnStrings::ReconnectParam, "ARM" );

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

    {
        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
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
                    (0x82) // remote disconnect
                    (0x00) // set configuration
                    (0x01) // 1 tlv
                    (0x01) // on-demand
                    (0x01) // length 1
                    (0x00);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x83)(0x00)(0x00)(0x01)(0x00);

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp = "Status: Success (0)";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string disconnectMode, reconnectParam;

        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode, disconnectMode );
        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReconnectParam, reconnectParam );

        BOOST_CHECK_EQUAL( disconnectMode, "ON_DEMAND" );
        BOOST_CHECK_EQUAL( reconnectParam, "ARM" );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_disconnect_demand_threshold )
{
    test_RfnResidentialDevice dut;

    dut.setType(TYPE_RFN420CD); // Make it a disconnect type.

    test_DeviceConfig cfg;

    cfg.insertValue( RfnStrings::DisconnectMode, "DEMAND_THRESHOLD" );
    cfg.insertValue( RfnStrings::ReconnectParam, "IMMEDIATE" );
    cfg.insertValue( RfnStrings::DisconnectDemandInterval, "5" );
    cfg.insertValue( RfnStrings::DemandThreshold, "10.2" );
    cfg.insertValue( RfnStrings::ConnectDelay, "15" );
    cfg.insertValue( RfnStrings::MaxDisconnects, "10" );

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

    {
        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
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
                    (0x82) // remote disconnect
                    (0x00) // set configuration
                    (0x01) // 1 tlv
                    (0x02) // demand-threshold
                    (0x05) // length 5
                    (0x01)(0x05)(0x66)(0x0f)(0x0a);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x83)(0x00)(0x00)(0x02)(0x00);

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp = "Status: Success (0)";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string disconnectMode, reconnectParam;
        double threshold;

        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode, disconnectMode );
        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReconnectParam, reconnectParam );
        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandThreshold, threshold );

        BOOST_CHECK_EQUAL( disconnectMode, "DEMAND_THRESHOLD" );
        BOOST_CHECK_EQUAL( reconnectParam, "IMMEDIATE" );
        BOOST_CHECK_CLOSE( threshold, 10.2, 1e-5);
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectDemandInterval ), 5 );
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ConnectDelay ), 15 );
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_MaxDisconnects ), 10 );
    }
}

BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_disconnect_cycling )
{
    test_RfnResidentialDevice dut;

    dut.setType(TYPE_RFN420CD); // Make it a disconnect type.

    test_DeviceConfig cfg;

    cfg.insertValue( RfnStrings::DisconnectMode, "CYCLING" );
    cfg.insertValue( RfnStrings::DisconnectMinutes, "10" );
    cfg.insertValue( RfnStrings::ConnectMinutes, "20" );

    test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

    dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

    {
        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
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
                    (0x82) // remote disconnect
                    (0x00) // set configuration
                    (0x01) // 1 tlv
                    (0x03) // on-demand
                    (0x05) // length 5
                    (0x01)(0x00)(0x0a)(0x00)(0x14);

            BOOST_CHECK_EQUAL( rcv, exp );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    (0x83)(0x00)(0x00)(0x03)(0x00);

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp = "Status: Success (0)";

            BOOST_CHECK_EQUAL(rcv.description, exp);
        }

        dut.extractCommandResult( *command );

        std::string disconnectMode;

        dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode, disconnectMode );

        BOOST_CHECK_EQUAL( disconnectMode, "CYCLING" );
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMinutes ), 10 );
        BOOST_CHECK_EQUAL( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ConnectMinutes ), 20 );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_install_disconnect_invalid_config )
{
    test_RfnResidentialDevice dut;

    dut.setType(TYPE_RFN420CD); // Make it a disconnect type.

    {
        ///// Missing config data /////

        test_DeviceConfig cfg;

        test_ConfigManager cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       NoConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "ERROR: Invalid config data. Config name:disconnect" );
        }

    }

    {
        resetTestState();

        ///// Invalid config data /////

        test_DeviceConfig cfg;

        // add remote disconnect config
        cfg.insertValue( RfnStrings::DisconnectMode, "NOT_A_DISCONNECT_MODE" ); // invalid disconnect mode

        test_ConfigManager cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install disconnect");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ErrorInvalidConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "ERROR: NoMethod or invalid config. Config name:disconnect" );
        }
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

        dut.extractCommandResult( *command );

        double   test_DemandInterval = 0;
        unsigned test_LoadProfileInterval = 0;

        BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandInterval,      test_DemandInterval ));
        BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval, test_LoadProfileInterval ));

        BOOST_CHECK_EQUAL( test_DemandInterval,      17 );
        BOOST_CHECK_EQUAL( test_LoadProfileInterval, 34 );

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

        {
            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    ( 0x68 )( 0x01 )( 0x00 );

            BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                           exp.begin() , exp.end() );
        }

        {
            std::vector<unsigned char> response = boost::assign::list_of
                    ( 0x69 )( 0x01 )( 0x00 )( 0x01 )( 0x01 )( 0x00 )( 0x02 )( 0x44 )( 0x22 );

            const Cti::Devices::Commands::RfnCommandResult rcv = command->decodeCommand( decode_time, response );

            const std::string exp = "Status: Success (0)\n"
                                    "Voltage Demand interval: 17.0 minutes\n"
                                    "Load Profile Demand interval: 34 minutes";

            BOOST_CHECK_EQUAL( rcv.description, exp );
        }

        {
            dut.extractCommandResult( *command );

            double   test_DemandInterval = 0;
            unsigned test_LoadProfileInterval = 0;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandInterval,      test_DemandInterval ));
            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval, test_LoadProfileInterval ));

            BOOST_CHECK_EQUAL( test_DemandInterval,      17 );
            BOOST_CHECK_EQUAL( test_LoadProfileInterval, 34 );
        }
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

            dut.extractCommandResult( *command );

            long test_ovuvEnabled;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled, test_ovuvEnabled ));
            BOOST_CHECK_EQUAL( static_cast<bool>(test_ovuvEnabled), true );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x26)(0x05);

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            unsigned test_ovuvAlarmReportingInterval;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval, test_ovuvAlarmReportingInterval ));
            BOOST_CHECK_EQUAL( test_ovuvAlarmReportingInterval, 5 );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x27)(0x3c);

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            unsigned test_ovuvAlarmRepeatInterval;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval, test_ovuvAlarmRepeatInterval ));
            BOOST_CHECK_EQUAL( test_ovuvAlarmRepeatInterval, 60 );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x28)(0x02);

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            unsigned test_ovuvAlarmRepeatCount;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount, test_ovuvAlarmRepeatCount ));
            BOOST_CHECK_EQUAL( test_ovuvAlarmRepeatCount, 2 );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x25)(0x03)(0x07)(0xe6)(0x00)(0x01)(0xe2)(0x40)(0x10)(0x80)(0x00)(0x01)(0xc0);

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            double test_ovThreshold;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvThreshold, test_ovThreshold ));
            BOOST_CHECK_EQUAL( test_ovThreshold, 123.456 );
        }
        {
            Commands::RfnCommandSPtr command = *rfnRequest_itr++;

            Commands::RfnCommand::RfnRequestPayload rcv = command->executeCommand( execute_time );

            std::vector<unsigned char> exp = boost::assign::list_of
                    (0x25)(0x03)(0x07)(0xe7)(0x00)(0x01)(0x34)(0x35)(0x10)(0x80)(0x00)(0x01)(0xc0);

            BOOST_CHECK_EQUAL( rcv, exp );

            dut.extractCommandResult( *command );

            double test_uvThreshold;

            BOOST_CHECK( dut.getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_UvThreshold,  test_uvThreshold ));
            BOOST_CHECK_EQUAL( test_uvThreshold, 78.901 );
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


BOOST_AUTO_TEST_CASE( test_dev_rfnResidential_putconfig_install_ovuv_invalid_config )
{
    test_RfnResidentialDevice dut;
    dut._type = TYPE_RFN410FX;

    {
        ///// Missing config data /////

        test_DeviceConfig cfg;

        test_ConfigManager cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install ovuv");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 1, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 0, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       NoConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "ERROR: Invalid config data. Config name:ovuv" );
        }

    }

    {
        resetTestState();

        ///// Invalid config data /////

        test_DeviceConfig cfg;

        test_ConfigManager cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        cfg.insertValue( RfnStrings::OvUvEnabled,                "true" );
        cfg.insertValue( RfnStrings::OvUvAlarmReportingInterval, "5" );
        cfg.insertValue( RfnStrings::OvUvAlarmRepeatInterval,    "-60" ); // insert invalid negative value
        cfg.insertValue( RfnStrings::OvUvRepeatCount,            "2" );
        cfg.insertValue( RfnStrings::OvThreshold,                "123.456" );
        cfg.insertValue( RfnStrings::UvThreshold,                "78.901" );

        CtiCommandParser parse("putconfig install ovuv");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest(request.get(), parse, returnMsgs, rfnRequests) );
        BOOST_REQUIRE_EQUAL( 2, returnMsgs.size() );
        BOOST_REQUIRE_EQUAL( 2, rfnRequests.size() );

        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();

            BOOST_CHECK_EQUAL( returnMsg.Status(),       ErrorInvalidConfigData );
            BOOST_CHECK_EQUAL( returnMsg.ResultString(), "ERROR: NoMethod or invalid config. Config name:ovuv" );
        }
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

BOOST_AUTO_TEST_CASE( test_putconfig_install_all )
{
    test_RfnResidentialDevice dut;
    dut._type = TYPE_RFN410FX;

    test_DeviceConfig cfg;

    {
        ////// empty configuration (no valid configuration) //////

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  4 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 0 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true)(true)(false); // 4 error messages, NOTE: last expectMore expected to be false

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add demand freeze day config
    cfg.insertValue( RfnStrings::demandFreezeDay, "7" );

    {
        ////// 1 valid configuration //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  4 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 1 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true)(true)(true); // 3 error messages + 1 message to notify that 1 config has been sent to the device

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add OVUV config
    cfg.insertValue( RfnStrings::OvUvEnabled,                "true" );
    cfg.insertValue( RfnStrings::OvUvAlarmReportingInterval, "5" );
    cfg.insertValue( RfnStrings::OvUvAlarmRepeatInterval,    "60" );
    cfg.insertValue( RfnStrings::OvUvRepeatCount,            "2" );
    cfg.insertValue( RfnStrings::OvThreshold,                "123.456" );
    cfg.insertValue( RfnStrings::UvThreshold,                "78.901" );

    {
        ////// 2 valid configurations //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  3 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 7 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true)(true); // 2 error messages + 1 message to notify that 2 config has been sent to the device

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add TOU config

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

    // set TOU enabled
    cfg.insertValue( RfnStrings::touEnabled, "true" );

    {
        ////// 3 valid configurations //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  2 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 9 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true); // 1 error messages + 1 message to notify that 3 config has been sent to the device

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add voltage averaging config
    cfg.insertValue( RfnStrings::demandInterval, "1" );
    cfg.insertValue( RfnStrings::profileInterval,"2" );

    {
        ////// 4 valid configurations //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  1 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 10 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true); // 1 message to notify that all config has been sent to the device

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }
}

BOOST_AUTO_TEST_CASE( test_putconfig_install_all_disconnect_meter )
{
    test_RfnResidentialDevice dut;
    dut._type = TYPE_RFN420CD;

    test_DeviceConfig cfg;

    {
        ////// empty configuration (no valid configuration) //////

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  5 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 0 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true)(true)(true)(false); // 4 error messages, NOTE: last expectMore expected to be false

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add remote disconnect config
    cfg.insertValue( RfnStrings::DisconnectMode, "CYCLING" );
    cfg.insertValue( RfnStrings::ConnectMinutes, "100" );
    cfg.insertValue( RfnStrings::DisconnectMinutes, "60" );
        
    {
        ////// 1 valid configuration //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  5 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 1 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true)(true)(true)(true);

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add demand freeze day config
    cfg.insertValue( RfnStrings::demandFreezeDay, "7" );

    {
        ////// 2 valid configurations //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  4 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 2 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true)(true)(true); // 3 error messages + 1 message to notify that 1 config has been sent to the device

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add OVUV config
    cfg.insertValue( RfnStrings::OvUvEnabled,                "true" );
    cfg.insertValue( RfnStrings::OvUvAlarmReportingInterval, "5" );
    cfg.insertValue( RfnStrings::OvUvAlarmRepeatInterval,    "60" );
    cfg.insertValue( RfnStrings::OvUvRepeatCount,            "2" );
    cfg.insertValue( RfnStrings::OvThreshold,                "123.456" );
    cfg.insertValue( RfnStrings::UvThreshold,                "78.901" );

    {
        ////// 3 valid configurations //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  3 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 8 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true)(true); // 2 error messages + 1 message to notify that 2 config has been sent to the device

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add TOU config

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

    // set TOU enabled
    cfg.insertValue( RfnStrings::touEnabled, "true" );

    {
        ////// 4 valid configurations //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  2 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 10 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true)(true); // 1 error messages + 1 message to notify that 3 config has been sent to the device

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }

    // add voltage averaging config
    cfg.insertValue( RfnStrings::demandInterval, "1" );
    cfg.insertValue( RfnStrings::profileInterval,"2" );

    {
        ////// 5 valid configurations //////

        resetTestState();

        test_ConfigManager  cfgMgr(Cti::Config::DeviceConfigSPtr(&cfg, null_deleter())); //  null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        dut.setConfigManager(&cfgMgr);  // attach config manager to the device so it can find the config

        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( NoError, dut.ExecuteRequest( request.get(), parse, returnMsgs, rfnRequests) );

        BOOST_CHECK_EQUAL( returnMsgs.size(),  1 );
        BOOST_CHECK_EQUAL( rfnRequests.size(), 11 );

        std::vector<bool> expectMoreRcv;
        while( ! returnMsgs.empty() )
        {
            const CtiReturnMsg &returnMsg = returnMsgs.front();
            expectMoreRcv.push_back( returnMsg.ExpectMore() );
            returnMsgs.pop_front();
        }

        const std::vector<bool> expectMoreExp = boost::assign::list_of
                (true); // 1 message to notify that all config has been sent to the device

        BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                       expectMoreExp.begin() , expectMoreExp.end() );
    }
}


BOOST_AUTO_TEST_SUITE_END()


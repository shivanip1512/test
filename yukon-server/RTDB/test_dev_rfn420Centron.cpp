#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rfn420centron.h"
#include "cmd_rfn.h"

#include "boost_test_helpers.h"


using namespace Cti::Devices;

struct test_Rfn420CentronDevice : Rfn420CentronDevice
{

};

struct test_state_rfn420centron
{
    CtiRequestMsg               request;
    RfnDevice::CtiMessageList   retList;
    RfnDevice::RfnCommandList   rfnRequests;

    ~test_state_rfn420centron()
    {
        delete_container(retList);
    }
};

const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );


BOOST_FIXTURE_TEST_SUITE( test_dev_rfn420centron, test_state_rfn420centron )

BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_immediate_demand_freeze )
{
    test_Rfn420CentronDevice    centron;

    CtiCommandParser    parse("putstatus freeze");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr    command = rfnRequests.front();

    // execute message and check request bytes

    const std::vector< unsigned char > exp = boost::assign::list_of
        ( 0x55 )( 0x01 );

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                   exp.begin() , exp.end() );
}


BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_tou_critical_peak_cancel )
{
    test_Rfn420CentronDevice    centron;

    CtiCommandParser    parse("putstatus tou critical peak cancel");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr    command = rfnRequests.front();

    // execute message and check request bytes

    const std::vector< unsigned char > exp = boost::assign::list_of
        ( 0x60 )( 0x09 )( 0x00 );

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                   exp.begin() , exp.end() );
}


BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_tou_critical_peak_today )
{
    test_Rfn420CentronDevice    centron;

    CtiCommandParser    parse("putstatus tou critical peak rate b until 23:00");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr    command = rfnRequests.front();

    // execute message and check request bytes

    const std::vector< unsigned char > exp = boost::assign::list_of
        ( 0x60 )( 0x08 )( 0x01 )( 0x0b )( 0x05 )( 0x01 )( 0x52 )( 0x1d )( 0x75 )( 0xc0 );

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                   exp.begin() , exp.end() );
}


BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_tou_critical_peak_tomorrow )
{
    test_Rfn420CentronDevice    centron;

    CtiCommandParser    parse("putstatus tou critical peak rate b until 8:00");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );

    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr    command = rfnRequests.front();

    // execute message and check request bytes

    const std::vector< unsigned char > exp = boost::assign::list_of
        ( 0x60 )( 0x08 )( 0x01 )( 0x0b )( 0x05 )( 0x01 )( 0x52 )( 0x1d )( 0xf4 )( 0x50 );

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                   exp.begin() , exp.end() );
}


BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_putconfig_tou_schedule )
{
    test_Rfn420CentronDevice centron;

    CtiCommandParser parse("putconfig tou 13242313 "
                           "schedule 1 a/00:00 b/00:01 c/10:06 d/12:22 a/23:33 b/23:44 "
                           "schedule 2 d/00:00 a/01:23 b/03:12 c/04:01 d/05:23 a/16:28 "
                           "schedule 3 c/00:00 d/01:02 a/02:03 b/04:05 c/05:06 d/06:07 "
                           "schedule 4 b/00:00 c/00:01 d/08:59 a/12:12 b/23:01 c/23:55 "
                           "default b");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );

    BOOST_CHECK_EQUAL( 0, retList.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr command = rfnRequests.front();

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

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

BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_putconfig_tou_schedule_badparam )
{
    test_Rfn420CentronDevice centron;

    {
        // test wrong switch time order
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:03 c/01:02 d/01:04 a/01:05 " // schedule 2 switch time 2 > switch time 3
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid switch time for schedule 2 - (01:02, expected > 01:03)";

        retList.clear();
        BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, retList.size() );

        CtiReturnMsg *retMsg = static_cast<CtiReturnMsg *>( retList.front() );

        BOOST_CHECK_EQUAL( retMsg->Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), exp );
    }

    {
        // test first time is not midnight
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:01 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 " // schedule 3 midnight time 00:01
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid midnight time for schedule 3 - (00:01, expected 00:00)";

        retList.clear();
        BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, retList.size() );

        CtiReturnMsg *retMsg = static_cast<CtiReturnMsg *>( retList.front() );

        BOOST_CHECK_EQUAL( retMsg->Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), exp );
    }

    {
        // test switch time out of range
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:60 a/04:03 b/04:04 c/04:05 " // schedule 4 switch time 2 set to 03:60
                               "default b");

        std::string exp = "Invalid switch time for schedule 4 - (03:60)";

        retList.clear();
        BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, retList.size() );

        CtiReturnMsg *retMsg = static_cast<CtiReturnMsg *>( retList.front() );

        BOOST_CHECK_EQUAL( retMsg->Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), exp );
    }

    {
        // test invalid rate
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 e/00:04 b/00:05 " // schedule 1 rate 4 set to 'e'
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid rate for schedule 1 - (e)";

        retList.clear();
        BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, retList.size() );

        CtiReturnMsg *retMsg = static_cast<CtiReturnMsg *>( retList.front() );

        BOOST_CHECK_EQUAL( retMsg->Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), exp );
    }

    {
        // test invalid change number
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 b/01:06 " // schedule 2 changes == 7
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "default b");

        std::string exp = "Invalid number of switch time for schedule 2 - (7, expected 6)";

        retList.clear();
        BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, retList.size() );

        CtiReturnMsg *retMsg = static_cast<CtiReturnMsg *>( retList.front() );

        BOOST_CHECK_EQUAL( retMsg->Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), exp );
    }

    {
        // test unexpected schedule
        CtiCommandParser parse("putconfig tou 12341234 "
                               "schedule 1 a/00:00 b/00:01 c/00:02 d/00:03 a/00:04 b/00:05 "
                               "schedule 2 d/00:00 a/01:01 b/01:02 c/01:03 d/01:04 a/01:05 "
                               "schedule 3 c/00:00 d/02:02 a/02:02 b/02:03 c/02:04 d/02:05 "
                               "schedule 4 b/00:00 c/03:01 d/03:02 a/03:03 b/03:04 c/03:05 "
                               "schedule 5 a/00:00 b/04:01 c/04:02 d/04:03 a/04:04 b/04:05 " // unexpected schedule 5
                               "default b");

        std::string exp = "Invalid schedule - (schedule 5)";

        retList.clear();
        BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );

        BOOST_CHECK_EQUAL( 0, rfnRequests.size() );
        BOOST_REQUIRE_EQUAL( 1, retList.size() );

        CtiReturnMsg *retMsg = static_cast<CtiReturnMsg *>( retList.front() );

        BOOST_CHECK_EQUAL( retMsg->Status(),       BADPARAM );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), exp );
    }
}


BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_getconfig_tou_schedule )
{
    test_Rfn420CentronDevice centron;

    CtiCommandParser parse("getconfig tou");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr command = rfnRequests.front();

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

    std::vector<unsigned char> exp = boost::assign::list_of
          (0x60)(0x05)(0x00);

    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                   exp.begin() , exp.end() );
}

BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_putconfig_tou_holiday )
{
    Cti::Test::set_to_central_timezone();

    test_Rfn420CentronDevice centron;

    CtiCommandParser parse("putconfig emetcon holiday 02/01/2025 06/14/2036 12/30/2050");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr command = rfnRequests.front();

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

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

BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_putconfig_tou_holiday_active )
{
    test_Rfn420CentronDevice centron;

    CtiCommandParser parse("putconfig emetcon holiday active");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr command = rfnRequests.front();

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

    const std::vector< unsigned char > exp = boost::assign::list_of
            (0x60)(0x0C)
            (0x00);

    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                   exp.begin() , exp.end() );
}

BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_putconfig_tou_holiday_cancel )
{
    test_Rfn420CentronDevice centron;

    CtiCommandParser parse("putconfig emetcon holiday cancel");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr command = rfnRequests.front();

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

    const std::vector< unsigned char > exp = boost::assign::list_of
            (0x60)(0x0D)
            (0x00);

    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                   exp.begin() , exp.end() );

}

BOOST_AUTO_TEST_CASE( test_dev_rfn420centron_getconfig_tou_holiday )
{
    Cti::Test::set_to_central_timezone();

    test_Rfn420CentronDevice centron;

    CtiCommandParser parse("getconfig emetcon holiday");

    BOOST_CHECK_EQUAL( NoError, centron.ExecuteRequest(&request, parse, retList, rfnRequests) );
    BOOST_CHECK_EQUAL( 0, retList.size() );
    BOOST_REQUIRE_EQUAL( 1, rfnRequests.size() );

    Commands::RfnCommandSPtr command = rfnRequests.front();

    Commands::RfnCommand::RfnRequest rcv = command->executeCommand( execute_time );

    std::vector<unsigned char> exp = boost::assign::list_of
            (0x60)(0x07)
            (0x00);

    BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                   exp.begin() , exp.end() );
}

BOOST_AUTO_TEST_SUITE_END()


#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "dev_rfn420centron.h"
#include "cmd_rfn.h"


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


BOOST_AUTO_TEST_SUITE_END()


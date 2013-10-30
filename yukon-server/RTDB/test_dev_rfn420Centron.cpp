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
    RfnDevice::ReturnMsgList    returnMsgs;
    RfnDevice::RfnCommandList   rfnRequests;
};

const CtiTime execute_time( CtiDate( 27, 8, 2013 ) , 15 );


BOOST_FIXTURE_TEST_SUITE( test_dev_rfn420centron, test_state_rfn420centron )

//...

BOOST_AUTO_TEST_SUITE_END()


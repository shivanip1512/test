#include <boost/test/unit_test.hpp>

#include "cmd_rfn_MeterDisconnect.h"

#include "ctidate.h"

#include "std_helper.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices::Commands;

BOOST_AUTO_TEST_SUITE(test_cmd_rfn_MeterDisconnect)

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE(test_mismatched_response_command)
{
}

BOOST_AUTO_TEST_CASE(test_mismatched_echoed_action)
{
}

BOOST_AUTO_TEST_CASE(test_unknown_status)
{
}

BOOST_AUTO_TEST_CASE(test_protocol_fail)
{
}

BOOST_AUTO_TEST_CASE(test_meter_fail)
{
}

BOOST_AUTO_TEST_CASE(test_unknown_tlv)
{
}

BOOST_AUTO_TEST_CASE(test_arm)
{
}

BOOST_AUTO_TEST_CASE(test_query)
{
}

BOOST_AUTO_TEST_CASE(test_resume)
{
}

BOOST_AUTO_TEST_CASE(test_terminate)
{
}

BOOST_AUTO_TEST_SUITE_END()
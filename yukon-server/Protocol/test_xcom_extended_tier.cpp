/*
    Test Expresscom Extended Tier
*/

#define BOOST_TEST_MAIN      "Test Expresscom Extended Tier Command"
#define BOOST_AUTO_TEST_MAIN "Test Expresscom Extended Tier"

#include "yukon.h"
#include "expresscom.h"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>

using boost::unit_test_framework::test_suite;


BOOST_AUTO_TEST_CASE(test_xcom_extended_tier)
{
    CtiCommandParser        parse( "putconfig xcom extended tier 2 timeout 14400 serial 1234" );
    CtiProtocolExpresscom   xcom;
    CtiOutMessage           out;

    BYTE expected_result[] = { 0, 0, 0, 0, 0,       // addressing not assigned: defaults to zero
                               0x17,                // message type: extended tier
                               0x10,                // flags: send a timeout value
                               0x02,                // tier number
                               0x38, 0x40 };        // timeout value: 14400 == 0x3840

    BOOST_CHECK_EQUAL( xcom.parseRequest(parse, out), NORMAL );
    BOOST_CHECK_EQUAL( xcom.entries(), 1 );
    BOOST_CHECK_EQUAL( xcom.messageSize(1), sizeof (expected_result) / sizeof (expected_result[0]) );

    for (int i = 0; i < xcom.messageSize(1); ++i)
    {
        BOOST_CHECK_EQUAL( xcom.getByte(i, 1), expected_result[i] );
    }
}


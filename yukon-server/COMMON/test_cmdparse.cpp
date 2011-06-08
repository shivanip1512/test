/*
 * file test_cmdparse.cpp
 *
 * Author: Jian Liu
 * Date: 07/18/2005 11:23:53
 *
 *
 * test cmdparse.cpp
 *
 * use test_cmdparse_input.h as the input
 * should expect output in test_cmdparse_output.h
 *
 */

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test CommandParse"
#include <boost/test/unit_test.hpp>

#include "boostutil.h"

#include <string>

#include "test_cmdparse_input.h"
#include "test_cmdparse_output.h"
#include "cmdparse.h"
#include "ctistring.h"

using std::string;

using boost::unit_test_framework::test_suite;

BOOST_AUTO_TEST_CASE(testString)
{
    const size_t test_size = sizeof(inputString) / sizeof(inputString[0]);

    for( int i = 0; i < test_size; i++ )
    {
        CtiCommandParser parse(inputString[i]);

        BOOST_CHECK_EQUAL(parse.asString(), parse_asString[i]);
    }
}

BOOST_AUTO_TEST_CASE(testDeviceGroupQuotes)
{
    const string lead_trail_apos = "getvalue kwh update timeout 1800 select group '/Meters/Collection/'Test Group''";
    const string mid_apos        = "getvalue kwh update timeout 1800 select group '/Meters/Collection/Intern's Group'";

    CtiCommandParser leadTrailParser(lead_trail_apos);
    CtiCommandParser midParser      (mid_apos);

    static const CtiString lead_trail_outcome = "/Meters/Collection/'Test Group'";
    static const CtiString mid_outcome        = "/Meters/Collection/Intern's Group";

    BOOST_CHECK_EQUAL(leadTrailParser.getsValue("group"), lead_trail_outcome);
    BOOST_CHECK_EQUAL(midParser.getsValue("group"), mid_outcome);
}

BOOST_AUTO_TEST_CASE(testShedTimes)
{
    std::string inStrings[] = {
      "control shed 5m relay 3",
      "control shed 5m relay 2",
      "control shed 5m relay 1",
      "control shed 60m",
      "control shed 30m",
      "control shed 15m",
      "control shed 7.m",
      "control shed 5m",
      "control sa305 shed 30m update",
      "control sa205 shed 30m update",
    };

    double shedSecondTimes[] = {
        300,
        300,
        300,
        3600,
        1800,
        900,
        420,
        300,
        1800,
        1800,
    };

    const size_t test_size = sizeof(inStrings) / sizeof(inStrings[0]);

    for( int i = 0; i < test_size; i++ )
    {
        CtiCommandParser parser(inStrings[i]);

        BOOST_CHECK_EQUAL(parser.getdValue("shed"), shedSecondTimes[i]);
    }
}



#include "cmdparse.h"

#define BOOST_TEST_MAIN
#include <boost/test/unit_test.hpp>

#include "boostutil.h"
#include "test_cmdparse_input.h"
#include "test_cmdparse_output.h"

using boost::unit_test_framework::test_suite;

BOOST_AUTO_TEST_CASE(testString)
{
    std::vector<std::string> parsedStrings;

    for each( const std::string input in inputStrings )
    {
        parsedStrings.push_back(CtiCommandParser(input).asString());
    }

    const size_t expected_size = sizeof(parse_asString) / sizeof(parse_asString[0]);

    BOOST_CHECK_EQUAL_COLLECTIONS(parsedStrings.begin(), parsedStrings.end(), parse_asString, parse_asString + expected_size);
}

BOOST_AUTO_TEST_CASE(testDeviceGroupQuotes)
{
    const std::string lead_trail_apos = "getvalue kwh update timeout 1800 select group '/Meters/Collection/'Test Group''";
    const std::string mid_apos        = "getvalue kwh update timeout 1800 select group '/Meters/Collection/Intern's Group'";

    CtiCommandParser leadTrailParser(lead_trail_apos);
    CtiCommandParser midParser      (mid_apos);

    const std::string lead_trail_outcome = "/Meters/Collection/'Test Group'";
    const std::string mid_outcome        = "/Meters/Collection/Intern's Group";

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

    std::vector<double> shedTimes;

    for each( std::string input in inStrings )
    {
        shedTimes.push_back(CtiCommandParser(input).getdValue("shed"));
    }

    const size_t expected_size = sizeof(shedSecondTimes) / sizeof(shedSecondTimes[0]);

    BOOST_CHECK_EQUAL_COLLECTIONS(shedTimes.begin(), shedTimes.end(), shedSecondTimes, shedSecondTimes + expected_size);
}



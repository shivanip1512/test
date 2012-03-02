#include <boost/test/unit_test.hpp>

#include "cmdparse.h"

#include "test_cmdparse_input.h"
#include "test_cmdparse_output.h"

BOOST_AUTO_TEST_SUITE( test_cmdparse )

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

BOOST_AUTO_TEST_CASE(test_getconfig_water_meter_read_interval)
{
    std::string inputCommands[] = {
      "getconfig water meter read interval",
      "getconfig read water meter interval"
    };

    bool results[] = {
        true,
        false
    };

    std::vector<bool>   testResults;

    for each ( const std::string & command in inputCommands )
    {
        testResults.push_back(CtiCommandParser(command).isKeyValid("water_meter_read_interval"));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(testResults.begin(), testResults.end(),
                                  results, results + (sizeof(results) / sizeof(*results)));
}

BOOST_AUTO_TEST_CASE(test_putconfig_water_meter_read_interval)
{
    std::string inputCommands[] = {
        "putconfig water meter read interval",
        "putconfig water meter read interval ",
        "putconfig water meter read interval12 hours",
        "putconfig water meter read interval 12 hrs",
        "putconfig water meter read interval 15hours",
        "putconfig water meter read interval 90 min",
        "putconfig water meter read interval 75m",
        "putconfig water meter read interval 15q",
        "putconfig water meter read interval 15 timeslices"
    };

    bool validCoreResults[] = {
        true,
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        true
    };

    std::vector<bool>   testResults;

    for each ( const std::string & command in inputCommands )
    {
        testResults.push_back(CtiCommandParser(command).isKeyValid("water_meter_read_interval"));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(testResults.begin(), testResults.end(),
                                  validCoreResults, validCoreResults + (sizeof(validCoreResults) / sizeof(*validCoreResults)));

    bool validDurationResults[] = {
        false,
        false,
        false,
        true,
        true,
        true,
        true,
        false,
        false
    };

    testResults.clear();

    for each ( const std::string & command in inputCommands )
    {
        testResults.push_back(CtiCommandParser(command).isKeyValid("read_interval_duration_seconds"));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(testResults.begin(), testResults.end(),
                                  validDurationResults,
                                  validDurationResults + (sizeof(validDurationResults) / sizeof(*validDurationResults)));

    int validDurationValueResults[] = {
        INT_MIN,
        INT_MIN,
        INT_MIN,
        43200,
        54000,
        5400,
        4500,
        INT_MIN,
        INT_MIN
    };

    std::vector<int>    durations;

    for each ( const std::string & command in inputCommands )
    {
        durations.push_back(CtiCommandParser(command).getiValue("read_interval_duration_seconds"));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(durations.begin(), durations.end(),
                                  validDurationValueResults,
                                  validDurationValueResults + (sizeof(validDurationValueResults) / sizeof(*validDurationValueResults)));
}

BOOST_AUTO_TEST_CASE(test_getconfig_load_profile_allocation)
{
    std::string inputCommands[] = {
      "getconfig load profile allocation",
      "getconfig load allocation profile"
    };

    bool results[] = {
        true,
        false
    };

    std::vector<bool>   testResults;

    for each ( const std::string & command in inputCommands )
    {
        testResults.push_back(CtiCommandParser(command).isKeyValid("load_profile_allocation"));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(testResults.begin(), testResults.end(),
                                  results, results + (sizeof(results) / sizeof(*results)));
}

BOOST_AUTO_TEST_CASE(test_putconfig_load_profile_allocation)
{
    std::string inputCommands[] = {
        "putconfig emetcon load profile allocation 1:75 2:75 3:0 4:18"
    };

    CtiCommandParser    parser(inputCommands[0]);

    BOOST_CHECK_EQUAL( true, parser.isKeyValid("load_profile_allocation") );

    BOOST_CHECK_EQUAL( true, parser.isKeyValid("load_profile_allocation_channel_1") );
    BOOST_CHECK_EQUAL( true, parser.isKeyValid("load_profile_allocation_channel_2") );
    BOOST_CHECK_EQUAL( true, parser.isKeyValid("load_profile_allocation_channel_3") );
    BOOST_CHECK_EQUAL( true, parser.isKeyValid("load_profile_allocation_channel_4") );

    BOOST_CHECK_EQUAL( 75, parser.getiValue("load_profile_allocation_channel_1") );
    BOOST_CHECK_EQUAL( 75, parser.getiValue("load_profile_allocation_channel_2") );
    BOOST_CHECK_EQUAL( 0,  parser.getiValue("load_profile_allocation_channel_3") );
    BOOST_CHECK_EQUAL( 18, parser.getiValue("load_profile_allocation_channel_4") );
}

BOOST_AUTO_TEST_CASE(test_putconfig_channel_2_netmetering_water_meter_none)
{
    std::string inputCommands[] = {
        "putconfig channel 2 netmetering",
        "putconfig channel 2 ui1203 water meter",
        "putconfig channel 2 ui1204 water meter",
        "putconfig channel 2 none",
        "putconfig channel 2 explode"
    };

    bool parseResults[] = {
        true,
        true,
        true,
        true,
        false
    };

    std::vector<bool>   testResults;

    for each ( const std::string & command in inputCommands )
    {
        testResults.push_back(CtiCommandParser(command).isKeyValid("channel_2_configuration"));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(testResults.begin(), testResults.end(),
                                  parseResults,
                                  parseResults + (sizeof(parseResults) / sizeof(*parseResults)));

    testResults.clear();

    for each ( const std::string & command in inputCommands )
    {
        testResults.push_back(CtiCommandParser(command).isKeyValid("channel_2_configuration_setting"));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(testResults.begin(), testResults.end(),
                                  parseResults,
                                  parseResults + (sizeof(parseResults) / sizeof(*parseResults)));

    std::string expectedResults[] = {
        "netmetering",
        "ui1203",
        "ui1204",
        "none",
        ""
    };

    std::vector<std::string>    settingResults;

    for each ( const std::string & command in inputCommands )
    {
        settingResults.push_back(CtiCommandParser(command).getsValue("channel_2_configuration_setting"));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(settingResults.begin(), settingResults.end(),
                                  expectedResults,
                                  expectedResults + (sizeof(expectedResults) / sizeof(*expectedResults)));
}

BOOST_AUTO_TEST_SUITE_END()

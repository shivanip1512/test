#include <boost/test/unit_test.hpp>

#include "date_utility.h"

using Cti::parseDateString;
using Cti::parseDurationString;
using Cti::parseTimeString;
using Cti::TimeParts;

BOOST_AUTO_TEST_SUITE( test_date_utility )

BOOST_AUTO_TEST_CASE(test_parse_date_string)
{
    {
        const auto date = parseDateString("10/25/10");

        BOOST_CHECK_EQUAL(25,   date.dayOfMonth());
        BOOST_CHECK_EQUAL(10,   date.month());
        BOOST_CHECK_EQUAL(2010, date.year());
    }

    {
        const auto date = parseDateString("10/25/2010");

        BOOST_CHECK_EQUAL(25,   date.dayOfMonth());
        BOOST_CHECK_EQUAL(10,   date.month());
        BOOST_CHECK_EQUAL(2010, date.year());
    }

    {
        const auto date = parseDateString("110/25/2010");

        BOOST_CHECK_EQUAL(1,    date.dayOfMonth());
        BOOST_CHECK_EQUAL(1,    date.month());
        BOOST_CHECK_EQUAL(1970, date.year());
    }

    {
        const auto date = parseDateString("10/15");

        BOOST_CHECK(date.is_neg_infinity());
    }
}

BOOST_AUTO_TEST_CASE(test_parse_time_string)
{
    auto parseTime = [](std::string timeStr) {
        BOOST_TEST_INFO(timeStr);

        const auto parsedTime = parseTimeString(timeStr);

        BOOST_REQUIRE(parsedTime);

        return parsedTime;
    };

    //  Valid time - hh:mm:ss
    {
        const auto parsedTime = parseTime("12:34:56");

        BOOST_CHECK_EQUAL(12, parsedTime->hour);
        BOOST_CHECK_EQUAL(34, parsedTime->minute);
        BOOST_CHECK_EQUAL(56, parsedTime->second);
    }
    {
        const auto parsedTime = parseTime("00:00:00");

        BOOST_CHECK_EQUAL( 0, parsedTime->hour);
        BOOST_CHECK_EQUAL( 0, parsedTime->minute);
        BOOST_CHECK_EQUAL( 0, parsedTime->second);
    }
    {
        const auto parsedTime = parseTime("23:59:59");

        BOOST_CHECK_EQUAL( 23, parsedTime->hour);
        BOOST_CHECK_EQUAL( 59, parsedTime->minute);
        BOOST_CHECK_EQUAL( 59, parsedTime->second);
    }

    //  Valid time - hh:mm
    {
        const auto parsedTime = parseTime("12:34");

        BOOST_CHECK_EQUAL(12, parsedTime->hour);
        BOOST_CHECK_EQUAL(34, parsedTime->minute);
        BOOST_CHECK_EQUAL( 0, parsedTime->second);
    }
    {
        const auto parsedTime = parseTime("00:00");

        BOOST_CHECK_EQUAL( 0, parsedTime->hour);
        BOOST_CHECK_EQUAL( 0, parsedTime->minute);
        BOOST_CHECK_EQUAL( 0, parsedTime->second);
    }
    {
        const auto parsedTime = parseTime("23:59");

        BOOST_CHECK_EQUAL(23, parsedTime->hour);
        BOOST_CHECK_EQUAL(59, parsedTime->minute);
        BOOST_CHECK_EQUAL( 0, parsedTime->second);
    }

    //  Invalid time - empty string
    BOOST_CHECK( ! parseTimeString(""));

    //  Invalid time - hh:
    BOOST_CHECK( ! parseTimeString("12:"));

    //  Invalid time - hh:qq
    BOOST_CHECK( ! parseTimeString("12:qq"));

    //  Overflow
    BOOST_CHECK( ! parseTimeString("24:34:56"));
    BOOST_CHECK( ! parseTimeString("12:60:56"));
    BOOST_CHECK( ! parseTimeString("12:34:60"));

    //  Negatives
    BOOST_CHECK( ! parseTimeString("-12:34:56"));
    BOOST_CHECK( ! parseTimeString("12:-34:56"));
    BOOST_CHECK( ! parseTimeString("12:34:-56"));
}

BOOST_AUTO_TEST_CASE(test_parse_duration)
{
    BOOST_CHECK( ! parseDurationString(""));
    BOOST_CHECK( ! parseDurationString(".123"));
    BOOST_CHECK( ! parseDurationString("z"));
    BOOST_CHECK( ! parseDurationString("3.14159z"));
    
    auto getCount = [](std::string s) {
        const auto duration = parseDurationString(s);

        BOOST_TEST_INFO("with duration string " << s);

        BOOST_REQUIRE(duration);

        return duration->count();
    };

    BOOST_CHECK_EQUAL(getCount("0h"), 0);
    BOOST_CHECK_EQUAL(getCount("0m"), 0);
    BOOST_CHECK_EQUAL(getCount("0s"), 0);

    BOOST_CHECK_EQUAL(getCount("1h"), 3600);
    BOOST_CHECK_EQUAL(getCount("1m"),   60);
    BOOST_CHECK_EQUAL(getCount("1s"),    1);

    BOOST_CHECK_EQUAL(getCount("314159h"), 1130972400);
    BOOST_CHECK_EQUAL(getCount("314159m"), 18849540);
    BOOST_CHECK_EQUAL(getCount("314159s"), 314159);

    BOOST_CHECK_EQUAL(getCount("3.14159h"), 11309);
    BOOST_CHECK_EQUAL(getCount("3.14159m"), 188);
    BOOST_CHECK_EQUAL(getCount("3.14159s"), 3);
}

BOOST_AUTO_TEST_SUITE_END()

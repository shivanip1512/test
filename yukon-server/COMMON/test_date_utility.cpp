#include <boost/test/unit_test.hpp>

#include "date_utility.h"

using Cti::parseDateString;
using Cti::parseTimeString;
using Cti::TimeParts;

BOOST_AUTO_TEST_SUITE( test_date_utility )

BOOST_AUTO_TEST_CASE(test_parse_date_string)
{
    {
        std::string dateStr = "10/25/10";
        CtiDate date;

        date = parseDateString(dateStr);

        BOOST_CHECK_EQUAL(25,   date.dayOfMonth());
        BOOST_CHECK_EQUAL(10,   date.month());
        BOOST_CHECK_EQUAL(2010, date.year());
    }

    {
        std::string dateStr = "10/25/2010";
        CtiDate date;

        date = parseDateString(dateStr);

        BOOST_CHECK_EQUAL(25,   date.dayOfMonth());
        BOOST_CHECK_EQUAL(10,   date.month());
        BOOST_CHECK_EQUAL(2010, date.year());
    }

    {
        std::string dateStr = "110/25/2010";
        CtiDate date;

        date = parseDateString(dateStr);

        BOOST_CHECK_EQUAL(1,    date.dayOfMonth());
        BOOST_CHECK_EQUAL(1,    date.month());
        BOOST_CHECK_EQUAL(1970, date.year());
    }

    {
        std::string dateStr = "10/15";
        CtiDate date;

        date = parseDateString(dateStr);

        BOOST_CHECK(date.is_neg_infinity());
    }
}

BOOST_AUTO_TEST_CASE(test_parse_time_string)
{
    //  Valid time - hh:mm:ss
    {
        std::string timeStr = "12:34:56";

        const boost::optional<TimeParts> parsedTime =
                parseTimeString(timeStr);

        BOOST_REQUIRE(parsedTime);
        BOOST_CHECK_EQUAL(12, parsedTime->hour);
        BOOST_CHECK_EQUAL(34, parsedTime->minute);
        BOOST_CHECK_EQUAL(56, parsedTime->second);
    }
    {
        std::string timeStr = "00:00:00";

        const boost::optional<TimeParts> parsedTime =
                parseTimeString(timeStr);

        BOOST_REQUIRE(parsedTime);
        BOOST_CHECK_EQUAL( 0, parsedTime->hour);
        BOOST_CHECK_EQUAL( 0, parsedTime->minute);
        BOOST_CHECK_EQUAL( 0, parsedTime->second);
    }
    {
        std::string timeStr = "23:59:59";

        const boost::optional<TimeParts> parsedTime =
                parseTimeString(timeStr);

        BOOST_REQUIRE(parsedTime);
        BOOST_CHECK_EQUAL( 0, parsedTime->hour);
        BOOST_CHECK_EQUAL( 0, parsedTime->minute);
        BOOST_CHECK_EQUAL( 0, parsedTime->second);
    }

    //  Valid time - hh:mm
    {
        std::string timeStr = "12:34";

        const boost::optional<TimeParts> parsedTime =
                parseTimeString(timeStr);

        BOOST_REQUIRE(parsedTime);
        BOOST_CHECK_EQUAL(12, parsedTime->hour);
        BOOST_CHECK_EQUAL(34, parsedTime->minute);
        BOOST_CHECK_EQUAL( 0, parsedTime->second);
    }
    {
        std::string timeStr = "00:00";

        const boost::optional<TimeParts> parsedTime =
                parseTimeString(timeStr);

        BOOST_REQUIRE(parsedTime);
        BOOST_CHECK_EQUAL( 0, parsedTime->hour);
        BOOST_CHECK_EQUAL( 0, parsedTime->minute);
        BOOST_CHECK_EQUAL( 0, parsedTime->second);
    }
    {
        std::string timeStr = "23:59";

        const boost::optional<TimeParts> parsedTime =
                parseTimeString(timeStr);

        BOOST_REQUIRE(parsedTime);
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

BOOST_AUTO_TEST_SUITE_END()

#define BOOST_AUTO_TEST_MAIN "Test date_utility.h"

#include "date_utility.h"

#include <boost/test/unit_test.hpp>
using boost::unit_test_framework::test_suite;

BOOST_AUTO_TEST_CASE(test_parse_date_value)
{
    {
        std::string dateStr = "10/25/10";
        CtiDate date;

        date = parseDateValue(dateStr);

        BOOST_CHECK_EQUAL(25,   date.dayOfMonth());
        BOOST_CHECK_EQUAL(10,   date.month());
        BOOST_CHECK_EQUAL(2010, date.year());
    }

    {
        std::string dateStr = "10/25/2010";
        CtiDate date;

        date = parseDateValue(dateStr);

        BOOST_CHECK_EQUAL(25,   date.dayOfMonth());
        BOOST_CHECK_EQUAL(10,   date.month());
        BOOST_CHECK_EQUAL(2010, date.year());
    }

    {
        std::string dateStr = "110/25/2010";
        CtiDate date;

        date = parseDateValue(dateStr);

        BOOST_CHECK_EQUAL(1,    date.dayOfMonth());
        BOOST_CHECK_EQUAL(1,    date.month());
        BOOST_CHECK_EQUAL(1970, date.year());
    }

    {
        std::string dateStr = "10/15";
        CtiDate date;

        date = parseDateValue(dateStr);

        BOOST_CHECK(date.is_neg_infinity());
    }
}

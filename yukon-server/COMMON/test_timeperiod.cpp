#define BOOST_AUTO_TEST_MAIN "Test TimePeriod"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

#include "timeperiod.h"
#include "ctitime.h"
#include "ctidate.h"
using boost::unit_test_framework::test_suite;

using Cti::TimePeriod;

BOOST_AUTO_TEST_CASE(test_timeperiod_conversion)
{
    CtiDate::CtiDate(1, 1, 1992);
    CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    TimePeriod largePeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0),
                           CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2010), 0, 0, 0));
    TimePeriod smallPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0),
                           CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0));
    TimePeriod smallIntersectingPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0),
                                       CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 1));

    // Check that conversions work
    BOOST_CHECK_EQUAL(largePeriod.begin(),CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0));
    BOOST_CHECK_EQUAL(largePeriod.end(),CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2010), 0, 0, 0));

    TimePeriod infinity(CtiTime::CtiTime(CtiTime::neg_infin),CtiTime::CtiTime(CtiTime::pos_infin));
    BOOST_CHECK_EQUAL(infinity.begin(), CtiTime::CtiTime(CtiTime::neg_infin));
    BOOST_CHECK_EQUAL(infinity.end(), CtiTime::CtiTime(CtiTime::pos_infin));
}

BOOST_AUTO_TEST_CASE(test_timeperiod_intersects)
{
    CtiDate::CtiDate(1, 1, 1992);
    CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    TimePeriod largePeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0),
                           CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2010), 0, 0, 0));
    TimePeriod smallPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0),
                           CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0));
    TimePeriod smallIntersectingPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0),
                                       CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 1));
    TimePeriod largeIntersectingPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0),
                                       CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2000), 0, 0, 1));

    // Check if intersect works
    BOOST_CHECK(!largePeriod.intersects(smallPeriod));
    TimePeriod aPeriod = largePeriod.intersection(smallPeriod);
    BOOST_CHECK(aPeriod.is_null());
    BOOST_CHECK(largePeriod.intersects(smallIntersectingPeriod));
    BOOST_CHECK(smallIntersectingPeriod.intersects(largePeriod));

    aPeriod = largePeriod.intersection(smallIntersectingPeriod);
    BOOST_CHECK_EQUAL(aPeriod.begin(), CtiTime::CtiTime(CtiDate::CtiDate(1,1,1992),0,0,0));
    BOOST_CHECK_EQUAL(aPeriod.end(), CtiTime::CtiTime(CtiDate::CtiDate(1,1,1992),0,0,1));

    aPeriod = largePeriod.intersection(largeIntersectingPeriod);
    BOOST_CHECK_EQUAL(aPeriod.begin(), CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0));
    BOOST_CHECK_EQUAL(aPeriod.end(), CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2000), 0, 0, 1));
}

BOOST_AUTO_TEST_CASE(test_timeperiod_merge)
{
    CtiDate::CtiDate(1, 1, 1992);
    CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    TimePeriod largePeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0),
                           CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2010), 0, 0, 0));
    TimePeriod smallPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0),
                           CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0));
    TimePeriod smallIntersectingPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0),
                                       CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 1));
    TimePeriod largeIntersectingPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0),
                                       CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2020), 0, 0, 1));

    // Check if merge works
    // assert(!largePeriod.intersects(smallPeriod));
    TimePeriod aPeriod = largePeriod.merge(smallPeriod);
    BOOST_CHECK(aPeriod.is_null());

    aPeriod = largePeriod.merge(smallIntersectingPeriod);
    BOOST_CHECK_EQUAL(aPeriod.begin(), CtiTime::CtiTime(CtiDate::CtiDate(1,1,1991),0,0,0));
    BOOST_CHECK_EQUAL(aPeriod.end(), CtiTime::CtiTime(CtiDate::CtiDate(1,1,2010),0,0,0));

    aPeriod = largePeriod.merge(largeIntersectingPeriod);
    BOOST_CHECK_EQUAL(aPeriod.begin(), CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0));
    BOOST_CHECK_EQUAL(aPeriod.end(), CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 2020), 0, 0, 1));
}

BOOST_AUTO_TEST_CASE(test_timeperiod_invalid)
{
    TimePeriod invalidPeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0),
                             CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0));
    BOOST_CHECK(invalidPeriod.is_null());

    invalidPeriod = TimePeriod::TimePeriod(CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 1),
                                           CtiTime::CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0));
    BOOST_CHECK(invalidPeriod.is_null());

    TimePeriod validPeriod(CtiTime::CtiTime(CtiTime::neg_infin), CtiTime(CtiTime::pos_infin));
    BOOST_CHECK(!validPeriod.is_null());

    // This is not a hard set rule, but a test that addDays is ok when invalid.
    BOOST_CHECK_EQUAL(invalidPeriod.begin(), invalidPeriod.addDays(1).begin());
}

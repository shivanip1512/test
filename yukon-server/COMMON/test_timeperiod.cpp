#include <boost/test/unit_test.hpp>

#include "timeperiod.h"
#include "ctitime.h"
#include "ctidate.h"

using Cti::TimePeriod;

BOOST_AUTO_TEST_SUITE( test_utility )

BOOST_AUTO_TEST_CASE(test_timeperiod_conversion)
{
    const CtiTime LargePeriodBegin    = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    const CtiTime LargePeriodEnd      = CtiTime(CtiDate::CtiDate(1, 1, 2010), 0, 0, 0);
    const CtiTime PositiveInfinity    = CtiTime(CtiTime::pos_infin);
    const CtiTime NegativeInfinity    = CtiTime(CtiTime::neg_infin);

    TimePeriod largePeriod(LargePeriodBegin, LargePeriodEnd);

    // Check that conversions work
    BOOST_CHECK_EQUAL(largePeriod.begin(), LargePeriodBegin);
    BOOST_CHECK_EQUAL(largePeriod.end(), LargePeriodEnd);

    TimePeriod infinity(NegativeInfinity, PositiveInfinity);
    BOOST_CHECK_EQUAL(infinity.begin(), NegativeInfinity);
    BOOST_CHECK_EQUAL(infinity.end(), PositiveInfinity);
}

BOOST_AUTO_TEST_CASE(test_timeperiod_intersects)
{
    const CtiTime LargePeriodBegin    = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    const CtiTime LargePeriodEnd      = CtiTime(CtiDate::CtiDate(1, 1, 2010), 0, 0, 0);
    const CtiTime SmallPeriodBegin    = CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0);
    const CtiTime SmallPeriodEnd      = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    const CtiTime SmallIntPeriodBegin = CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0);
    const CtiTime SmallIntPeriodEnd   = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 1);
    const CtiTime LargeIntPeriodBegin = CtiTime(CtiDate::CtiDate(1, 1, 1993), 0, 0, 0);
    const CtiTime LargeIntPeriodEnd   = CtiTime(CtiDate::CtiDate(1, 1, 2000), 0, 0, 1);
    const CtiTime PositiveInfinity    = CtiTime(CtiTime::pos_infin);
    const CtiTime NegativeInfinity    = CtiTime(CtiTime::neg_infin);

    TimePeriod largePeriod(LargePeriodBegin, LargePeriodEnd);
    TimePeriod smallPeriod(SmallPeriodBegin, SmallPeriodEnd);
    TimePeriod smallIntersectingPeriod(SmallIntPeriodBegin, SmallIntPeriodEnd);
    TimePeriod largeIntersectingPeriod(LargeIntPeriodBegin, LargeIntPeriodEnd);

    // Check if intersect works
    BOOST_CHECK(!largePeriod.intersects(smallPeriod));
    TimePeriod aPeriod = largePeriod.intersection(smallPeriod);
    BOOST_CHECK(aPeriod.is_null());
    BOOST_CHECK(largePeriod.intersects(smallIntersectingPeriod));
    BOOST_CHECK(smallIntersectingPeriod.intersects(largePeriod));

    aPeriod = largePeriod.intersection(smallIntersectingPeriod);
    BOOST_CHECK_EQUAL(aPeriod.begin(), LargePeriodBegin);
    BOOST_CHECK_EQUAL(aPeriod.end(), SmallIntPeriodEnd);

    aPeriod = largePeriod.intersection(largeIntersectingPeriod);
    BOOST_CHECK_EQUAL(aPeriod.begin(), LargeIntPeriodBegin);
    BOOST_CHECK_EQUAL(aPeriod.end(), LargeIntPeriodEnd);
}

BOOST_AUTO_TEST_CASE(test_timeperiod_merge)
{
    const CtiTime LargePeriodBegin    = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    const CtiTime LargePeriodEnd      = CtiTime(CtiDate::CtiDate(1, 1, 2010), 0, 0, 0);
    const CtiTime SmallPeriodBegin    = CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0);
    const CtiTime SmallPeriodEnd      = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    const CtiTime SmallIntPeriodBegin = CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0);
    const CtiTime SmallIntPeriodEnd   = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 1);
    const CtiTime LargeIntPeriodBegin = CtiTime(CtiDate::CtiDate(1, 1, 1991), 0, 0, 0);
    const CtiTime LargeIntPeriodEnd   = CtiTime(CtiDate::CtiDate(1, 1, 2020), 0, 0, 1);

    TimePeriod largePeriod(LargePeriodBegin, LargePeriodEnd);
    TimePeriod smallPeriod(SmallPeriodBegin, SmallPeriodEnd);
    TimePeriod smallIntersectingPeriod(SmallIntPeriodBegin, SmallIntPeriodEnd);
    TimePeriod largeIntersectingPeriod(LargeIntPeriodBegin, LargeIntPeriodEnd);

    // Check if merge works
    // assert(!largePeriod.intersects(smallPeriod));
    TimePeriod aPeriod = largePeriod.merge(smallPeriod);
    BOOST_CHECK(aPeriod.is_null());

    aPeriod = largePeriod.merge(smallIntersectingPeriod);
    BOOST_CHECK_EQUAL(aPeriod.begin(), SmallIntPeriodBegin);
    BOOST_CHECK_EQUAL(aPeriod.end(), LargePeriodEnd);

    aPeriod = largePeriod.merge(largeIntersectingPeriod);
    BOOST_CHECK_EQUAL(aPeriod.begin(), LargeIntPeriodBegin);
    BOOST_CHECK_EQUAL(aPeriod.end(), LargeIntPeriodEnd);
}

BOOST_AUTO_TEST_CASE(test_timeperiod_invalid)
{
    const CtiTime StartOfYear      = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 0);
    const CtiTime AfterStartOfYear = CtiTime(CtiDate::CtiDate(1, 1, 1992), 0, 0, 1);
    const CtiTime PositiveInfinity = CtiTime(CtiTime::pos_infin);
    const CtiTime NegativeInfinity = CtiTime(CtiTime::neg_infin);

    TimePeriod invalidPeriod(StartOfYear, StartOfYear);
    BOOST_CHECK(invalidPeriod.is_null());

    invalidPeriod = TimePeriod::TimePeriod(AfterStartOfYear, StartOfYear);
    BOOST_CHECK(invalidPeriod.is_null());

    TimePeriod validPeriod(NegativeInfinity, PositiveInfinity);
    BOOST_CHECK(!validPeriod.is_null());

    // This is not a hard set rule, but a test that addDays is ok when invalid.
    BOOST_CHECK_EQUAL(invalidPeriod.begin(), invalidPeriod.addDays(1).begin());
}

BOOST_AUTO_TEST_SUITE_END()

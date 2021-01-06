#include <boost/test/unit_test.hpp>

#include "PaoStatisticsRecord.h"
#include "ctidate.h"

BOOST_AUTO_TEST_SUITE( test_PaoStatisticsRecord )

using Cti::Porter::Statistics::PaoStatisticsRecord;

struct test_PaoStatisticsRecord : PaoStatisticsRecord
{
    static PaoStatisticsRecord makeHourlyPaoStatisticsRecord(int pao_id, CtiTime record_time)
    {
        return PaoStatisticsRecord(pao_id, PaoStatisticsRecord::Hourly, record_time);
    }
    static PaoStatisticsRecord makeDailyPaoStatisticsRecord(int pao_id, CtiTime record_time)
    {
        return PaoStatisticsRecord(pao_id, PaoStatisticsRecord::Daily, record_time);
    }
    static PaoStatisticsRecord makeMonthlyPaoStatisticsRecord(int pao_id, CtiTime record_time)
    {
        return PaoStatisticsRecord(pao_id, PaoStatisticsRecord::Monthly, record_time);
    }
    static PaoStatisticsRecord makeLifetimePaoStatisticsRecord(int pao_id, CtiTime record_time)
    {
        return PaoStatisticsRecord(pao_id, PaoStatisticsRecord::Lifetime, record_time);
    }
};

BOOST_AUTO_TEST_CASE(test_isStale)
{
    {
        const PaoStatisticsRecord hourly =
            test_PaoStatisticsRecord::makeHourlyPaoStatisticsRecord(1, CtiTime(CtiDate(1, 2, 2013), 1, 2, 3));

        BOOST_CHECK( ! hourly.isStale(CtiTime(CtiDate( 1,  1, 2013),  0,  0,  0)));
        BOOST_CHECK( ! hourly.isStale(CtiTime(CtiDate( 1,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( ! hourly.isStale(CtiTime(CtiDate( 1,  2, 2013),  1,  0,  0)));
        BOOST_CHECK( ! hourly.isStale(CtiTime(CtiDate( 1,  2, 2013),  1,  2,  3)));
        BOOST_CHECK( ! hourly.isStale(CtiTime(CtiDate( 1,  2, 2013),  1, 59, 59)));
        BOOST_CHECK( hourly.isStale(CtiTime(CtiDate( 1,  2, 2013),  2,  0,  0)));
        BOOST_CHECK( hourly.isStale(CtiTime(CtiDate( 2,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( hourly.isStale(CtiTime(CtiDate(28,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( hourly.isStale(CtiTime(CtiDate( 1,  3, 2013),  0,  0,  0)));
    }
    {
        const PaoStatisticsRecord daily =
            test_PaoStatisticsRecord::makeDailyPaoStatisticsRecord(1, CtiTime(CtiDate(1, 2, 2013), 1, 2, 3));

        BOOST_CHECK( ! daily.isStale(CtiTime(CtiDate( 1,  1, 2013),  0,  0,  0)));
        BOOST_CHECK( ! daily.isStale(CtiTime(CtiDate( 1,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( ! daily.isStale(CtiTime(CtiDate( 1,  2, 2013),  1,  0,  0)));
        BOOST_CHECK( ! daily.isStale(CtiTime(CtiDate( 1,  2, 2013),  1,  2,  3)));
        BOOST_CHECK( ! daily.isStale(CtiTime(CtiDate( 1,  2, 2013),  1, 59, 59)));
        BOOST_CHECK( ! daily.isStale(CtiTime(CtiDate( 1,  2, 2013),  2,  0,  0)));
        BOOST_CHECK( daily.isStale(CtiTime(CtiDate( 2,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( daily.isStale(CtiTime(CtiDate(28,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( daily.isStale(CtiTime(CtiDate( 1,  3, 2013),  0,  0,  0)));
    }
    {
        const PaoStatisticsRecord monthly =
            test_PaoStatisticsRecord::makeMonthlyPaoStatisticsRecord(1, CtiTime(CtiDate(1, 2, 2013), 1, 2, 3));

        BOOST_CHECK( ! monthly.isStale(CtiTime(CtiDate( 1,  1, 2013),  0,  0,  0)));
        BOOST_CHECK( ! monthly.isStale(CtiTime(CtiDate( 1,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( ! monthly.isStale(CtiTime(CtiDate( 1,  2, 2013),  1,  0,  0)));
        BOOST_CHECK( ! monthly.isStale(CtiTime(CtiDate( 1,  2, 2013),  1,  2,  3)));
        BOOST_CHECK( ! monthly.isStale(CtiTime(CtiDate( 1,  2, 2013),  1, 59, 59)));
        BOOST_CHECK( ! monthly.isStale(CtiTime(CtiDate( 1,  2, 2013),  2,  0,  0)));
        BOOST_CHECK( ! monthly.isStale(CtiTime(CtiDate( 2,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( ! monthly.isStale(CtiTime(CtiDate(28,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( monthly.isStale(CtiTime(CtiDate( 1,  3, 2013),  0,  0,  0)));
    }
    {
        const PaoStatisticsRecord lifetime =
            test_PaoStatisticsRecord::makeLifetimePaoStatisticsRecord(1, CtiTime(CtiDate(1, 2, 2013), 1, 2, 3));

        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate( 1,  1, 2013),  0,  0,  0)));
        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate( 1,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate( 1,  2, 2013),  1,  0,  0)));
        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate( 1,  2, 2013),  1,  2,  3)));
        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate( 1,  2, 2013),  1, 59, 59)));
        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate( 1,  2, 2013),  2,  0,  0)));
        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate( 2,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate(28,  2, 2013),  0,  0,  0)));
        BOOST_CHECK( ! lifetime.isStale(CtiTime(CtiDate( 1,  3, 2013),  0,  0,  0)));
    }
}

BOOST_AUTO_TEST_SUITE_END()

#include <boost/test/unit_test.hpp>

#include "Mct410.h"
#include "ctidate.h"

#include <boost/assign/list_of.hpp>
#include <boost/tuple/tuple.hpp>
#include <boost/tuple/tuple_comparison.hpp>

#include "boost_test_helpers.h"

#include <cmath>

using namespace Cti::Simulator;

BOOST_AUTO_TEST_SUITE( test_mct410_sim )

struct testMct410Sim : Mct410Sim
{
    using Mct410Sim::getConsumptionMultiplier;
    using Mct410Sim::makeValueConsumption;
    using Mct410Sim::getHectoWattHours;
    using Mct410Sim::fillLoadProfile;
    using Mct410Sim::fillLongLoadProfile;
    using Mct410Sim::getTablePointer;
    using Mct410Sim::checkForNewPeakDemand;
    using Mct410Sim::peak_demand_t;
    using Mct410Sim::DawnOfTime;

    struct test_peak_demand_t : peak_demand_t
    {
        test_peak_demand_t(peak_demand_t t) :
            peak_demand_t(t)
        {}
    };
};

struct ConsumptionValue
{
    double value;

    ConsumptionValue(double v) : value(v) {};

    bool operator==(const ConsumptionValue &other) const
    {
        double epsilon = std::max(value, other.value) * 1e-6;
        return abs(value - other.value) <= epsilon;
    }

    bool operator!=(const ConsumptionValue &other) const
    {
        return !(*this == other);
    }
};

std::ostream& operator<<(std::ostream& out, const ConsumptionValue &v)
{
    out << v.value;
    return out;
}

bool operator!=(const testMct410Sim::peak_demand_t &lhs, const testMct410Sim::peak_demand_t &rhs)
{
    return boost::tie(lhs.peakDemand, lhs.peakTimestamp) != boost::tie(rhs.peakDemand, rhs.peakTimestamp);
}

std::ostream& operator<<(std::ostream& out, const testMct410Sim::peak_demand_t &v)
{
    out << v.peakDemand << " " << v.peakTimestamp;
    return out;
}

BOOST_AUTO_TEST_CASE( mct_consumption_multiplier )
{
    unsigned address = 0;
    for (; address < 400; address++)
    {
        BOOST_CHECK_EQUAL(testMct410Sim::getConsumptionMultiplier(address), 1.0);
    }
    for (; address < 600; address++)
    {
        BOOST_CHECK_EQUAL(testMct410Sim::getConsumptionMultiplier(address), 2.0);
    }
    for (; address < 800; address++)
    {
        BOOST_CHECK_EQUAL(testMct410Sim::getConsumptionMultiplier(address), 3.0);
    }
    for (; address < 950; address++)
    {
        BOOST_CHECK_EQUAL(testMct410Sim::getConsumptionMultiplier(address), 5.0);
    }
    for (; address < 995; address++)
    {
        BOOST_CHECK_EQUAL(testMct410Sim::getConsumptionMultiplier(address), 10.0);
    }
    for (; address < 1000; address++)
    {
        BOOST_CHECK_EQUAL(testMct410Sim::getConsumptionMultiplier(address), 20.0);
    }
}

BOOST_AUTO_TEST_CASE( test_make_value_consumption )
{
    Cti::Test::set_to_central_timezone();

    struct ConsumptionTestCase
    {
        unsigned address;
        CtiTime  startTime;
        unsigned duration;
        double   output;
    }
    const consumption_test_cases[] =
    {
        {   0, CtiTime(CtiDate(4, 4, 2012), 8, 0, 0), 3600,     6111679.02 },
        {   0, CtiTime(CtiDate(4, 4, 2012), 8, 0, 0),    0,           0.00 },
        {   0, CtiTime(CtiDate(8, 1, 2011), 2, 0, 0), 3600,     2913167.66 },
        {   0, CtiTime(CtiDate(8, 1, 2011), 2, 0, 0),    0,           0.00 },
        {   1, CtiTime(CtiDate(4, 4, 2012), 8, 0, 0), 3600,     6111679.02 },
        {   1, CtiTime(CtiDate(4, 4, 2012), 8, 0, 0),  900,     1543737.42 },
        {   1, CtiTime(CtiDate(8, 1, 2011), 2, 0, 0), 3600,     2913167.66 },
        {   1, CtiTime(CtiDate(8, 1, 2011), 2, 0, 0),  900,      692697.48 },
        { 489, CtiTime(CtiDate(4, 4, 2012), 8, 0, 0), 3600,    12223358.05 },
        { 489, CtiTime(CtiDate(4, 4, 2012), 8, 0, 0),    0,           0.00 },
        { 489, CtiTime(CtiDate(8, 1, 2011), 2, 0, 0), 3600,     5826335.32 },
        { 489, CtiTime(CtiDate(8, 1, 2011), 2, 0, 0),    0,           0.00 }
    };

    std::vector<ConsumptionValue> result, expected;
    for each(const ConsumptionTestCase &testCase in consumption_test_cases)
    {
        expected.push_back(testCase.output);
        result.push_back(testMct410Sim::makeValueConsumption(testCase.address, testCase.startTime, testCase.duration));
        BOOST_CHECK_CLOSE(testCase.output, testMct410Sim::makeValueConsumption(testCase.address, testCase.startTime, testCase.duration), 1e-1);
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(result.begin(), result.end(), expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE( test_get_hectowatt_hours )
{
    Cti::Test::set_to_central_timezone();

    struct HectoWattTestCase
    {
        unsigned address;
        CtiTime time;
        unsigned value;
    }
    const hectoWatt_test_cases[] =
    {
        {       0, CtiTime(CtiDate( 4, 4, 2012), 8, 0, 0),  753685 },
        {       0, CtiTime(CtiDate( 8, 1, 2011), 2, 0, 0),  631940 },
        {     489, CtiTime(CtiDate( 4, 4, 2012), 8, 0, 0), 1579254 },
        {     489, CtiTime(CtiDate( 8, 1, 2011), 2, 0, 0), 1335763 },
        { 2049998, CtiTime(CtiDate(28, 5, 2015), 2, 0, 0), 3240896 }
    };

    std::vector<unsigned> result, expected;
    for each(const HectoWattTestCase &testCase in hectoWatt_test_cases)
    {
        expected.push_back(testCase.value);
        result.push_back(testMct410Sim::getHectoWattHours(testCase.address, testCase.time));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(result.begin(), result.end(), expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE( test_get_table_pointer )
{
    struct TablePointerTestCase
    {
        CtiTime  time;
        unsigned intervalSeconds;
        unsigned output;
    }
    const table_pointer_test_cases[] =
    {
        { CtiTime(CtiDate(4, 4, 2012), 8, 0, 0), 3600, 62 },
        { CtiTime(CtiDate(4, 4, 2012), 8, 0, 0),  900, 53 },
        { CtiTime(CtiDate(8, 1, 2012), 2, 0, 0), 3600, 81 },
        { CtiTime(CtiDate(8, 1, 2012), 2, 0, 0),  900, 33 }
    };

    std::vector<unsigned> result, expected;
    for each(const TablePointerTestCase &testCase in table_pointer_test_cases)
    {
        expected.push_back(testCase.output);
        result.push_back(testMct410Sim::getTablePointer(testCase.time, testCase.intervalSeconds));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(result.begin(), result.end(), expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE( test_fill_load_profile )
{
    Cti::Test::set_to_central_timezone();

    const unsigned address(0);
    const CtiDate startDate(4, 4, 2012);
    const CtiTime blockStart = CtiTime(startDate, 8, 0, 0); // 08:00 4/4/2012

    bytes resultBytes;
    byte_appender result_oitr = byte_appender(resultBytes);

    const bytes expected = boost::assign::list_of
        (0x26)(0xc3)  // 07:00-08:00 dynamic demand
        (0x26)(0xc2)  // 06:00-07:00 dynamic demand
        (0x26)(0xa0)  // 05:00-06:00 dynamic demand
        (0x26)(0x5f)  // 04:00-05:00 dynamic demand
        (0x26)(0x02)  // 03:00-04:00 dynamic demand
        (0x25)(0x91); // 02:00-03:00 dynamic demand

    testMct410Sim::fillLoadProfile(address, blockStart, 3600, result_oitr);

    BOOST_CHECK_EQUAL_COLLECTIONS(resultBytes.begin(), resultBytes.end(), expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE( test_fill_long_load_profile )
{
    Cti::Test::set_to_central_timezone();

    const unsigned address(0);
    const unsigned interval(3600);
    const CtiDate startDate(4, 4, 2012);

    {
        const CtiTime blockStart = CtiTime(startDate, 8, 0, 0); // 08:00 4/4/2012

        bytes resultBytes;
        byte_appender result_oitr = byte_appender(resultBytes);

        const bytes expected = boost::assign::list_of
            (0x25)(0x91)  // 02:00-03:00 dynamic demand
            (0x26)(0x02)  // 03:00-04:00 dynamic demand
            (0x26)(0x5f)  // 04:00-05:00 dynamic demand
            (0x26)(0xa0)  // 05:00-06:00 dynamic demand
            (0x26)(0xc2)  // 06:00-07:00 dynamic demand
            (0x26)(0xc3); // 07:00-08:00 dynamic demand

        testMct410Sim::fillLongLoadProfile(address, blockStart, interval, result_oitr);

        BOOST_CHECK_EQUAL_COLLECTIONS(resultBytes.begin(), resultBytes.end(), expected.begin(), expected.end());
    }

    // Test overlap
    {
        const CtiTime blockStart = CtiTime(startDate, 12, 0, 0); // 12:00 4/4/2012

        bytes resultBytes;
        byte_appender result_oitr = byte_appender(resultBytes);

        const bytes expected = boost::assign::list_of
            (0x26)(0xc2)  // 06:00-07:00 dynamic demand
            (0x26)(0xc3)  // 07:00-08:00 dynamic demand
            (0x26)(0xa2)  // 08:00-09:00 dynamic demand
            (0x26)(0x61)  // 09:00-10:00 dynamic demand
            (0x26)(0x06)  // 10:00-11:00 dynamic demand
            (0x25)(0x95); // 11:00-12:00 dynamic demand

        testMct410Sim::fillLongLoadProfile(address, blockStart, interval, result_oitr);

        BOOST_CHECK_EQUAL_COLLECTIONS(resultBytes.begin(), resultBytes.end(), expected.begin(), expected.end());
    }
}

BOOST_AUTO_TEST_CASE( test_peak_demand )
{
    Cti::Test::set_to_central_timezone();

    CtiTime lastFreeze(CtiDate(6, 5, 2012), 10, 0, 0);
    CtiTime nowTime(CtiDate(28, 5, 2012), 2, 0, 0);
    struct PeakDemandTestCase
    {
        unsigned address;
        unsigned demandInterval; // seconds
        ctitime_t lastFreezeTimestamp;
        CtiTime c_time;
        testMct410Sim::peak_demand_t result;
    }
    const peak_demand_test_cases[] =
    {
        {   0,  300, lastFreeze.seconds(), nowTime, { 0x3000 | 2000, 1338120300 } },
        {   0,  900, lastFreeze.seconds(), nowTime, { 0x2000 |  600, 1338120900 } },
        {   0, 3600, lastFreeze.seconds(), nowTime, { 0x2000 | 2300, 1338123600 } },
        {  42,  300, lastFreeze.seconds(), nowTime, { 0x3000 | 2000, 1338120300 } },
        {  42,  900, lastFreeze.seconds(), nowTime, { 0x2000 |  600, 1338120900 } },
        {  42, 3600, lastFreeze.seconds(), nowTime, { 0x2000 | 2300, 1338123600 } },
        { 999,  300, lastFreeze.seconds(), nowTime, { 0x2000 | 3800, 1338120300 } },
        { 999,  900, lastFreeze.seconds(), nowTime, { 0x1000 | 1130, 1338120900 } },
        { 999, 3600, lastFreeze.seconds(), nowTime, {           454, 1338123600 } },
    };

    std::vector<testMct410Sim::test_peak_demand_t> result, expected;
    for each(const PeakDemandTestCase &testCase in peak_demand_test_cases)
    {
        expected.push_back(testCase.result);
        result.push_back(
            testMct410Sim::checkForNewPeakDemand(
                testCase.address,
                testCase.demandInterval,
                testCase.lastFreezeTimestamp,
                testCase.c_time));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(result.begin(), result.end(), expected.begin(), expected.end());
}

BOOST_AUTO_TEST_SUITE_END()

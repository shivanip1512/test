#include <boost/test/unit_test.hpp>

#include "Mct410.h"
#include "ctidate.h"

#include <boost/assign/list_of.hpp>

#include <cmath>

using namespace Cti::Simulator;

BOOST_AUTO_TEST_SUITE( test_mct410_sim )

struct testMct410Sim : Mct410Sim
{
    using Mct410Sim::getConsumptionMultiplier;
    using Mct410Sim::makeValueConsumption;
    using Mct410Sim::getHectoWattHours;
    using Mct410Sim::fillLoadProfile;
    using Mct410Sim::getTablePointer;
    using Mct410Sim::DawnOfTime;
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
    struct ConsumptionTestCase
    {
        unsigned address;
        CtiTime  startTime;
        unsigned duration;
        double   output;
    };

    const ConsumptionTestCase consumption_test_cases[] =
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
    struct HectoWattTestCase
    {
        unsigned address;
        CtiTime time;
        unsigned value;
    };

    const HectoWattTestCase hectoWatt_test_cases[] =
    {
        {   0, CtiTime(CtiDate(4, 4, 2012), 8, 0, 0),  753685 },
        {   0, CtiTime(CtiDate(8, 1, 2011), 2, 0, 0),  631940 },
        { 489, CtiTime(CtiDate(4, 4, 2012), 8, 0, 0), 1579254 },
        { 489, CtiTime(CtiDate(8, 1, 2011), 2, 0, 0), 1335763 }
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
    };

    const TablePointerTestCase table_pointer_test_cases[] =
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
    const unsigned address(0);
    const CtiDate startDate(4, 4, 2012);
    const CtiTime blockStart = CtiTime(startDate, 8, 0, 0); // 08:00 4/4/2012

    bytes resultBytes;
    byte_appender result_oitr = byte_appender(resultBytes);

    const bytes expected = boost::assign::list_of
        (0x26)(0xc3)(0x26)(0xc2)(0x26)(0xa0)(0x26)(0x5f)(0x26)(0x02)(0x25)(0x91);

    testMct410Sim::fillLoadProfile(address, blockStart, 3600, result_oitr);

    BOOST_CHECK_EQUAL_COLLECTIONS(resultBytes.begin(), resultBytes.end(), expected.begin(), expected.end());
}

BOOST_AUTO_TEST_SUITE_END()

#define BOOST_AUTO_TEST_MAIN "Test mct410 device"

#include "precompiled.h"
#include "boostutil.h"
#include "types.h"
#include "Mct410.h"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using namespace Cti::Simulator;

struct testMct410Sim : Mct410Sim
{
    using Mct410Sim::getConsumptionMultiplier;
};

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

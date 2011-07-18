#define BOOST_AUTO_TEST_MAIN "Test BchBehavior"

#include "precompiled.h"
#include "boostutil.h"
#include "BehaviorCollection.h"
#include "BchBehavior.h"
#include "types.h"
#include "logger.h"
#include "SimulatorLogger.h"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using namespace Cti::Simulator;

BOOST_AUTO_TEST_CASE(test_bch_behavior)
{
    SimulatorLogger logger(dout);
    BehaviorCollection<PlcBehavior> behaviorCollection;
    std::auto_ptr<PlcBehavior> d(new BchBehavior());
    d->setChance(1000);

    behaviorCollection.push_back(d);

    bytes message, reference;

    // Pushing data onto each vector. The BchBehavior should modify the
    // second-to-last nibble of the message.
    message.push_back(0x04);
    message.push_back(0x08);
    message.push_back(0x0F);
    reference.push_back(0x04);
    reference.push_back(0x08);
    reference.push_back(0x1F); // This is where the change should occur!

    behaviorCollection.processMessage(message, logger);

    // Check to see that the BchBehavior correctly
    // processed the message and put the information back
    // in the intended order to match the reference vector.
    BOOST_CHECK_EQUAL_COLLECTIONS(message.begin(), message.end(), reference.begin(), reference.end());
}

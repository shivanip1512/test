#include <boost/test/unit_test.hpp>

#include "BehaviorCollection.h"
#include "NackBehavior.h"
#include "SimulatorLogger.h"

using namespace Cti::Simulator;

BOOST_AUTO_TEST_SUITE( test_nack_behavior )

BOOST_AUTO_TEST_CASE(test_nack_behavior)
{
    SimulatorLogger logger(dout);
    BehaviorCollection<PlcBehavior> behaviorCollection;

    behaviorCollection.push_back(
            std::make_unique<NackBehavior>(1000));

    bytes message, reference;

    // Pushing data onto each vector. The BchBehavior should modify the
    // second-to-last nibble of the message.
    message.push_back(0x04);
    message.push_back(0x08);
    message.push_back(0x0F);

    behaviorCollection.processMessage(message, logger);

    // Check to see that the BchBehavior correctly
    // processed the message and put the information back
    // in the intended order to match the reference vector.
    BOOST_CHECK(message.empty());
}

BOOST_AUTO_TEST_SUITE_END()

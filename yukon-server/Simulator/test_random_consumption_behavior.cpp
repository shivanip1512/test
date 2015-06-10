#include <boost/test/unit_test.hpp>

#include "RandomConsumptionBehavior.h"
#include "BehaviorCollection.h"

using namespace Cti::Simulator;

BOOST_AUTO_TEST_SUITE( test_random_consumption_behavior )

BOOST_AUTO_TEST_CASE(test_random_consumption_behavior)
{
    SimulatorLogger logger(dout);
    BehaviorCollection<MctBehavior> behaviorCollection;
    auto d = std::make_unique<RandomConsumptionBehavior>();
    d->setChance(1000);

    behaviorCollection.push_back(std::move(d));

    {
        bytes message;

        /**
         * Pushing data onto each vector. For messages returning from
         * function read 0x90, the RandomConsumptionBehavior should
         * modify the first three bytes of the message to be a randomly
         * generated value.
         *
         * The only thing we can guarantee about the message returning
         * from this read is that the rest of the message will be left
         * untouched. We can't even guarantee with 100% assurance that
         * the random kWh value that returns won't be the same as the
         * one that went in (unlikely, but entirely possible.)
         */
        message.push_back(0x04);
        message.push_back(0x08);
        message.push_back(0x0F);
        message.push_back(0x10);
        message.push_back(0x17);
        message.push_back(0x2A);
        message.push_back(0x31);
        message.push_back(0x62);

        MctMessageContext contextBegin = { message, 0x90, true };
        MctMessageContext contextEnd   = { message, 0x90, true };

        behaviorCollection.processMessage(contextBegin, logger);

        // Check to see that the RandomConsumptionBehavior correctly
        // processed the message and put the information back
        // in the intended order to match contextEnd.
        BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin()+3, contextBegin.data.end(),
                                      contextEnd.data.begin()+3, contextEnd.data.end());
        BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
        BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);
    }
}

BOOST_AUTO_TEST_SUITE_END()

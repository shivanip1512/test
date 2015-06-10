#include <boost/test/unit_test.hpp>

#include "FrozenPeakTimestampBehavior.h"
#include "BehaviorCollection.h"

using namespace Cti::Simulator;

BOOST_AUTO_TEST_SUITE( test_frozen_peak_timestamp_behavior )

BOOST_AUTO_TEST_CASE(test_frozen_peak_timestamp_behavior)
{
    SimulatorLogger logger(dout);
    BehaviorCollection<MctBehavior> behaviorCollection;
    auto d = std::make_unique<FrozenPeakTimestampBehavior>();
    d->setChance(1000);

    behaviorCollection.push_back(std::move(d));

    {
        bytes message, reference;

        /**
         * Pushing data onto each vector. For messages returning from
         * function read 0x94, the FrozenPeakTimestampBehavior should
         * add 86400 seconds to the timestamp in bytes 2-5.
         */
        message.push_back(0x04);
        message.push_back(0x08);
        message.push_back(0x0F);
        message.push_back(0x10);
        message.push_back(0x17);
        message.push_back(0x2A);

        reference.push_back(0x04);
        reference.push_back(0x08);
        reference.push_back(0x0F);
        reference.push_back(0x11); // Changes
        reference.push_back(0x68); // occur
        reference.push_back(0xAA); // here.

        MctMessageContext contextBegin = { message, 0x94, true };
        MctMessageContext contextEnd   = { reference, 0x94, true };

        behaviorCollection.processMessage(contextBegin, logger);

        // Check to see that the FrozenPeakTimestampBehavior correctly
        // processed the message and put the information back
        // in the intended order to match contextEnd.
        BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin(), contextBegin.data.end(),
                                      contextEnd.data.begin(), contextEnd.data.end());
        BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
        BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);
    }

    {
        bytes message, reference;

        /**
         * Pushing data onto each vector. For messages returning from
         * anything except function read 0x94, the
         * FrozenPeakTimestampBehavior should leave the message
         * untouched.
         */
        message.push_back(0x04);
        message.push_back(0x08);
        message.push_back(0x0F);
        message.push_back(0x10);
        message.push_back(0x17);
        message.push_back(0x2A);

        reference.push_back(0x04);
        reference.push_back(0x08);
        reference.push_back(0x0F);
        reference.push_back(0x10);
        reference.push_back(0x17);
        reference.push_back(0x2A);

        {
            MctMessageContext contextBegin = { message, 0x94, false };   // Data read 0x94, nothing should happen.
            MctMessageContext contextEnd   = { reference, 0x94, false };

            behaviorCollection.processMessage(contextBegin, logger);

            // Check to see that the FrozenReadParityBehavior correctly
            // processed the message and put the information back
            // in the intended order to match contextEnd.
            BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin(), contextBegin.data.end(),
                                          contextEnd.data.begin(), contextEnd.data.end());
            BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
            BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);
        }

        {
            MctMessageContext contextBegin = { message, 0x93, true };   // Function read 0x93, nothing should happen.
            MctMessageContext contextEnd   = { reference, 0x93, true };

            behaviorCollection.processMessage(contextBegin, logger);

            // Check to see that the FrozenReadParityBehavior correctly
            // processed the message and put the information back
            // in the intended order to match contextEnd.
            BOOST_CHECK_EQUAL_COLLECTIONS(contextBegin.data.begin(), contextBegin.data.end(),
                                          contextEnd.data.begin(), contextEnd.data.end());
            BOOST_CHECK_EQUAL(contextBegin.function_read, contextEnd.function_read);
            BOOST_CHECK_EQUAL(contextBegin.function, contextEnd.function);
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()

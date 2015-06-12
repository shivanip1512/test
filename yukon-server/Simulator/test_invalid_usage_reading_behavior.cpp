#include "InvalidUsageReadingBehavior.h"
#include "BehaviorCollection.h"

#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

using namespace Cti::Simulator;

BOOST_AUTO_TEST_SUITE( test_invalid_usage_reading_behavior )

BOOST_AUTO_TEST_CASE( test_invalid_usage_reading )
{
    SimulatorLogger logger(dout);
    BehaviorCollection<MctBehavior> behaviorCollection;

    behaviorCollection.push_back(
            std::make_unique<InvalidUsageReadingBehavior>(100));

    {
        const bytes message = boost::assign::list_of
                (0x04)
                (0x08)
                (0x0F)
                (0x10)
                (0x17)
                (0x2A)
                (0x31)
                (0x62);

        const MctMessageContext original = { message, 0x90, true };
        MctMessageContext modified = original;

        /**
         * For messages returning from function read 0x90, the
         * InvalidUsageReadingBehavior should modify the first three
         * bytes of the message to be 0xFFFFFC.
         */
        behaviorCollection.processMessage(modified, logger);

        //  Just the first three bytes are modified
        BOOST_CHECK_EQUAL(modified.data[0], 0xff);
        BOOST_CHECK_EQUAL(modified.data[1], 0xff);
        BOOST_CHECK_EQUAL(modified.data[2], 0xfc);

        //  the rest of the data is untouched
        BOOST_CHECK_EQUAL_COLLECTIONS(modified.data.begin()+3, modified.data.end(),
                                      original.data.begin()+3, original.data.end());

        BOOST_CHECK_EQUAL(modified.function_read,
                          original.function_read);
        BOOST_CHECK_EQUAL(modified.function,
                          original.function);
    }
}

BOOST_AUTO_TEST_SUITE_END()

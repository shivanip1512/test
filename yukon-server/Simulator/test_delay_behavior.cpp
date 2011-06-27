#define BOOST_AUTO_TEST_MAIN "Test delayBehavior"

#include "yukon.h"
#include "boostutil.h"
#include "BehaviorCollection.h"
#include "DelayBehavior.h"
#include "types.h"
#include "SimulatorLogger.h"

#include <time.h>
#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using namespace Cti::Simulator;

BOOST_AUTO_TEST_CASE(test_delay_behavior)
{
    srand(time(NULL));
    // Apparently the first random needs to be dumped or else
    // the random number will be some linear function as 
    // opposed to being truly (or rather pseudo-) random.
    rand();

    SimulatorLogger logger(dout);

    BehaviorCollection<CommsBehavior> behaviorCollection;
    std::auto_ptr<CommsBehavior> d(new DelayBehavior());
    d->setChance(1000);

    behaviorCollection.push_back(d);

    bytes message, reference;

    // Pushing data onto each vector. The delayBehavior should set the
    // message vector to be empty and hold onto the data that was sent
    // into the behavior, while reference retains its data.
    message.push_back(0x04);
    message.push_back(0x08);
    message.push_back(0x0F);
    reference.push_back(0x04);
    reference.push_back(0x08);
    reference.push_back(0x0F);

    // Ensure that the message size is indeed 3 after the 3 bytes were pushed on. 
    BOOST_CHECK_EQUAL(message.size(), 3);

    behaviorCollection.processMessage(message, logger);

    // The delayBehavior should have emptied the message vector and held onto its contents.
    // After the message has been delayed, this vector should be of size 0.
    BOOST_CHECK_EQUAL(message.size(), 0);

    // Push more data onto each of the vectors.
    message.push_back(0x10);
    message.push_back(0x17);
    message.push_back(0x2A);
    reference.push_back(0x10);
    reference.push_back(0x17);
    reference.push_back(0x2A);

    behaviorCollection.processMessage(message, logger);

    // After message has been processed the second time it should return with the 
    // data that was initially delayed as well as all its current data.
    // Since 3 bytes were added the first time and another 3 have been added
    // this most recent time, the size should be 6.
    BOOST_CHECK_EQUAL(message.size(), 6);

    // Both message and reference should be of size 6 after the last process.
    BOOST_CHECK_EQUAL(message.size(), reference.size());

    BOOST_CHECK_EQUAL_COLLECTIONS(message.begin(), message.end(), reference.begin(), reference.end());
}
